
## Index (인덱스)

### Index란?
**Index**는 책의 목차처럼, 테이블의 모든 행을 다 읽지 않아도 빠르게 원하는 데이터를 찾게 해주는 자료구조다.
테이블에 100만 개 행이 있을 때, Index 없으면 최악의 경우 100만 개를 다 읽어야 한다. 
Index가 있으면 대부분 10-20개 정도만 읽고 찾을 수 있다.

### MySQL의 B+ Tree 구조
MySQL의 Index는 **B+ Tree**로 구현되어 있다.
- **Root Node**: 데이터 범위를 나눔
- **Internal Node**: 다시 범위를 나눔
- **Leaf Node**: 실제 데이터(또는 포인터)가 있음
- **중요**: Leaf Node들이 서로 연결되어 있어서 범위 검색(age > 30)도 순차적으로 빠르게 처리 가능

### Index의 종류

#### 1. Clustering Index (클러스터 인덱스)
- **PRIMARY KEY** 기반
- 테이블당 **1개만** 존재 가능
- Leaf Node에 **실제 테이블의 모든 데이터**가 포함됨
- 이 Index로 찾으면 바로 모든 컬럼을 얻을 수 있음 → **가장 빠름**

#### 2. Secondary Index (세컨더리 인덱스)
- 일반 컬럼(name, age, status 등)을 기반으로 생성
- 여러 개 만들 수 있음
- Leaf Node에는 **Index 컬럼 + PK 포인터만** 포함됨
- Secondary Index로 찾은 후 → PK로 Clustering Index 다시 접근 필요 (**2번 접근**)
- Clustering Index보다 느림

#### 3. Covering Index (커버링 인덱스) ⭐
**SELECT하려는 모든 컬럼이 Index에 포함되어 있으면 테이블에 접근하지 않아도 된다.**

예시:
```
CREATE INDEX idx_lookup ON users(user_id, name, email);
SELECT user_id, name, email FROM users WHERE user_id = 1;
```
→ Index Leaf Node에 필요한 모든 데이터가 있으므로 테이블 접근 불필요 → **가장 빠름**

### 복합 Index (Composite Index)

여러 컬럼을 함께 Index하는 경우:
```
CREATE INDEX idx ON users(age, status, name);
```

**LEFT-MOST PREFIX 원칙**:
- ✅ `WHERE age > 30` → Index 탐
- ✅ `WHERE age > 30 AND status = 'active'` → Index 탐
- ❌ `WHERE status = 'active'` → Index 못 탐 (age를 건너뜀)
- ❌ `WHERE name LIKE 'Kim%'` → Index 못 탐 (age, status를 건너뜀)

**왜?** Index는 앞 컬럼부터 순서대로 정렬되어 있기 때문. age 없이는 뒤를 찾을 수 없음.

### Selectivity (선택도) & Cardinality

**Selectivity** = (서로 다른 값의 개수) / (전체 행의 개수)

높을수록 Index 효과가 좋음:
- 성별(M/F): 2 / 1,000,000 = 0.0002 → **매우 낮음**, Index 별로
- user_id: 1,000,000 / 1,000,000 = 1.0 → **높음**, Index 좋음
- age: 100 / 1,000,000 = 0.0001 → **낮음**, Index 별로

**규칙**: Selectivity > 0.1 (10% 이상)이면 Index 가치가 있음

---

## SQL Injection (보안)

### SQL Injection이란?
**SQL Injection**은 악의적 사용자가 입력 필드에 SQL 코드를 삽입해서 쿼리를 조작하는 공격이다.

### 공격 원리
쿼리와 사용자 입력이 섞여 있을 때 발생:

```
입력: "1' OR '1'='1"
결과: SELECT * FROM users WHERE user_id = '1' OR '1'='1'
→ 모든 사용자 정보 반환 (보안 위험!)
```


### 방어: PrepareStatement
**쿼리와 데이터를 완전히 분리한다.**

1. 쿼리는 미리 정의: `"SELECT * FROM users WHERE user_id = ?"`
2. DB가 `?` 위치를 "데이터가 올 자리"로 인식
3. 실제 데이터는 나중에 바인딩
4. DB는 이 데이터를 절대 쿼리 코드로 해석하지 않고 **순수 데이터로만** 취급

따라서 `"1' OR '1'='1"`이 들어가도 단순 문자열로만 처리됨 → **공격 불가능**

---

## Statement vs PrepareStatement

### Statement 
매번 새로운 SQL 문자열을 만들어서 실행한다.

```
100번 쿼리 실행:
1번째: "SELECT * FROM users WHERE user_id = '1'" 생성 → Parsing → Compilation → Execution
2번째: "SELECT * FROM users WHERE user_id = '2'" 생성 → Parsing → Compilation → Execution
3번째: "SELECT * FROM users WHERE user_id = '3'" 생성 → Parsing → Compilation → Execution
...
```

**결과**: Parsing 100회, Compilation 100회, Execution 100회 (매우 비효율)

### PrepareStatement (좋은 방식) 
쿼리 틀을 한 번 만들고 **파라미터만 바꿔가며** 반복 실행한다.

```
1번 준비:
"SELECT * FROM users WHERE user_id = ?" → Parsing (1회) → Compilation (1회)
→ 결과를 메모리에 저장 (캐싱)

반복 실행:
setInt(1, 1) → Execution
setInt(1, 2) → Execution
setInt(1, 3) → Execution
...
```

**결과**: Parsing 1회, Compilation 1회, Execution 100회 (효율적)

### 실행 단계별 설명

**Parsing**: SQL 문법이 맞는지, 테이블/컬럼이 존재하는지 확인
**Compilation**: 실행 계획을 세우기 (어떤 Index를 쓸지, 어떤 순서로 읽을지)
**Execution**: 실제 DB 접근

PrepareStatement는 Parsing과 Compilation을 1번만 하므로 훨씬 빠르다.

### 저장 위치
PreparedStatement 객체는 **JVM Heap**에 저장된다. 연결이 닫히면 GC 대상이 된다.

---

## 효과적인 쿼리 저장

### Named Query (사전 정의)

쿼리를 애플리케이션 시작할 때 미리 선언해두고 이름으로 꺼내서 사용한다.

**동작**:
```
애플리케이션 시작
→ @NamedQuery 스캔
→ Parsing (1회만!)
→ Compilation (1회만!)
→ 메모리에 저장 (캐싱)

런타임:
"User.findById" 이름으로 꺼냄
→ 파라미터만 바꿈
→ Execution (반복)
```

**언제 쓰나?**
- 자주 쓰이는 쿼리 (같은 조건으로 여러 번)
- 예: getUserById, getProductByCode 같은 기본 조회

**단점**:
- 동적 조건이 많으면 복잡해짐
- 조건이 바뀔 때마다 새로운 Named Query 필요

### Dynamic Query (런타임 생성)

조건에 따라 런타임에 쿼리를 만들어서 실행한다.

```
런타임:
name이 있으면 → "SELECT u FROM User u WHERE u.name LIKE :name" 추가
age가 있으면 → "AND u.age > :age" 추가
...

결과 쿼리를 Parsing → Compilation → Execution
```

**언제 쓰나?**
- 검색 필터처럼 조건이 자주 바뀌는 경우
- 예: 이름, 나이, 상태 등 여러 조건으로 검색

**단점**:
- 매번 Parsing, Compilation (느림)
- 런타임에 오류 발견 (조기 발견 안 됨)

### QueryDsl (타입 안전한 동적 쿼리) 

Dynamic Query처럼 유연하지만 **타입 안전**하다.

**장점**:
- 필드명 오타나면 **컴파일 오류** 발생 (런타임 오류 방지)
- 가독성이 좋음
- 쿼리 재사용 가능

**단점**:
- 여전히 런타임에 쿼리 생성 (Dynamic Query와 같음)
- 조건에 따라 다른 쿼리가 생성되므로 Parsing은 반복됨

### 정리: 언제 뭘 쓰나?

| 상황 | 추천 | 이유 |
|------|------|------|
| 자주 쓰는 쿼리 (같은 조건) | Named Query | Parsing 1회, 캐싱 활용 |
| 동적 조건 많은 쿼리 | QueryDsl | 유연성 + 타입 안전 |
| 매우 복잡한 쿼리 | Dynamic Query + QueryDsl | 최대 유연성 |

---

## MySQL 옵티마이저

### 옵티마이저란?
**Optimizer**는 쿼리를 여러 가지 방법으로 실행할 수 있는데, 그 중 가장 빠른 방법을 자동으로 선택해주는 DB 엔진이다.

### 옵티마이저의 실행 과정

#### Step 1: Parsing
SQL 문자열을 읽어서 문법이 맞는지, 테이블/컬럼이 존재하는지 확인한다. Parse Tree라는 구조로 변환된다.

#### Step 2: Preprocessing (전처리)
불필요한 조건을 제거한다. 예를 들어 `WHERE 1=1 AND age > 30`은 `WHERE age > 30`으로 단순화된다.

#### Step 3: Optimization (최적화) ← 여기가 핵심!
**여러 개의 실행 계획(Execution Plan)을 생성하고 비용을 계산해서 가장 싼 계획을 선택한다.**

예시:
```
쿼리: SELECT * FROM users, orders 
      WHERE users.user_id = orders.user_id 
      AND users.age > 30

가능한 실행 계획들:
1번: users (age > 30 필터) → orders 조인
     예상 Cost: 50 (가장 싼 것!) ← 선택됨
     
2번: orders → users 조인 (age > 30 필터)
     예상 Cost: 200

3번: users (Full Scan) → orders 조인
     예상 Cost: 1000
```

**Optimizer가 1번을 선택한다.**

#### Step 4: Compilation (컴파일)
선택된 실행 계획을 실제로 실행 가능한 형태로 컴파일한다.

#### Step 5: Execution (실행)
실제 DB 접근이 일어난다.

### 기본 원리: Cost-Based Optimizer

#### Cost 계산 방식
각 실행 계획의 예상 비용을 계산한다:

```
Cost = (디스크 I/O 횟수) * 1.0 + (CPU 작업) * 0.2
```

**왜 이 비율인가?**
- 디스크 I/O가 CPU 연산보다 훨씬 느리다
- 디스크 읽기는 밀리초 단위, CPU 연산은 나노초 단위
- 따라서 디스크 읽기를 최소화하는 계획을 선택한다

#### 비용 계산 예시

```
실행 계획 1: Index Scan
- 디스크 읽기: 10 페이지
- CPU 작업: 100
- Cost = 10 * 1.0 + 100 * 0.2 = 30 (선택됨!)

실행 계획 2: Full Table Scan
- 디스크 읽기: 1000 페이지
- CPU 작업: 10000
- Cost = 1000 * 1.0 + 10000 * 0.2 = 3000
```

### 옵티마이저가 사용하는 정보: 통계

Optimizer가 좋은 결정을 하려면 **통계 정보**가 필요하다:

```
ANALYZE TABLE users;
```

이 명령어로 수집되는 정보:
- **테이블의 전체 행 수**: 100만 개인지, 1000개인지?
- **각 컬럼의 Cardinality**: 서로 다른 값이 몇 개인가?
- **데이터 분포**: 값들이 균등하게 분포되어 있나?
- **NULL 개수**: NULL 값이 얼마나 있나?

**중요**: 통계 정보가 부정확하면 Optimizer가 잘못된 선택을 할 수 있다.

### JOIN 순서 결정

Optimizer는 **가장 효율적인 JOIN 순서**를 결정한다.

```
쿼리: SELECT * FROM users u, orders o, products p
      WHERE u.user_id = o.user_id 
      AND o.product_id = p.product_id
      AND u.age > 30
```

**Optimizer의 생각**:
1. users에서 age > 30인 행 찾기 (10만 행)
2. 각 user마다 orders 조인 (평균 5개) → 50만 행
3. 각 order마다 products 조인 (1:1) → 50만 행
4. **총 I/O: 10만 + 50만 + 50만 = 110만**

**vs 다른 순서 (orders 먼저)**:
1. orders 전체 (100만 행)
2. users와 조인
3. products와 조인
4. **총 I/O: 훨씬 많음**

→ Optimizer가 첫 번째 순서를 선택한다.

### Index 선택

같은 테이블에 여러 Index가 있을 때, Optimizer는 **어느 Index를 사용할지 결정**한다.

```
테이블: users (100만 행)
Index들:
- idx_age (Cardinality: 100)
- idx_status (Cardinality: 5)
- idx_email (Cardinality: 1000000)

쿼리: SELECT * FROM users WHERE age > 30

Optimizer의 판단:
- idx_age 사용 → Cardinality 100 (1%)
- idx_status 사용 → Cardinality 5 (0.0005%, 너무 낮음)
- Full Scan → Cardinality 1000000 (100%)

결정: idx_age 사용 (가장 효율적)
```

### Index가 여러 개일 때의 선택

```
쿼리: SELECT * FROM users WHERE age > 30 AND status = 'active'

가능한 선택지:
1. idx_age 사용
   - age > 30로 필터링 (30만 행)
   - 그 후 status = 'active'로 추가 필터링
   
2. idx_status 사용
   - status = 'active'로 필터링 (50만 행)
   - 그 후 age > 30으로 추가 필터링
   
3. 복합 Index (age, status) 있으면
   - 한 번에 둘 다 필터링 (최고!)

Optimizer는 각 방식의 예상 행 수를 계산해서 가장 적은 행을 반환하는 Index를 선택한다.
```

### Optimizer가 잘못 선택하는 경우

Optimizer가 항상 맞는 것은 아니다:

**원인 1: 통계 정보가 오래되었을 때**
```
ANALYZE TABLE users;  ← 정기적으로 실행해야 함
```

**원인 2: 데이터 분포가 특이할 때**
```
age 값들이 대부분 30 이상인데, 
Optimizer는 균등 분포를 가정해서 잘못 계산할 수 있다.
```

**원인 3: Optimizer의 한계**
복잡한 쿼리에서는 모든 경우를 다 계산할 수 없어서 휴리스틱(경험칙)을 사용한다.

### Index Hint로 강제하기

Optimizer의 선택이 마음에 안 들면 **강제**할 수 있다:

```
USE INDEX (idx_age)
→ idx_age를 사용하도록 강제

FORCE INDEX (idx_age)
→ idx_age를 반드시 사용하도록 강제

IGNORE INDEX (idx_status)
→ idx_status를 사용하지 않도록 강제
```

**주의**: Index Hint는 최후의 수단. Optimizer가 보통 더 똑똑하다.

---

## EXPLAIN (느린 쿼리 분석의 필수 도구) 

### EXPLAIN이란?
쿼리를 실제로 실행하지 않고, **Optimizer가 어떻게 실행할지 보여주는** 명령어다.

```
EXPLAIN SELECT * FROM users WHERE age > 30;
```

### 봐야 할 것 TOP 3

#### 1️⃣ type (성능 지표, 가장 중요!) ⭐⭐⭐

**느린 순서** (왼쪽이 느림):

| type | 설명 | 성능 |
|------|------|------|
| **ALL** | 테이블 전체 스캔 | 가장 느림 ❌ |
| **index** | Index 전체 스캔 | 느림 ❌ |
| **range** | 범위 검색 (>, <, BETWEEN) | 좋음 ✅ |
| **ref** | Index로 정확히 매칭 | 좋음 ✅ |
| **eq_ref** | JOIN에서 정확히 1행 | 매우 좋음 ✅✅ |
| **const** | 상수로 정확히 1행 (PK) | 가장 빠름 ✅✅✅ |

**예시**:
```
WHERE age > 30
- age Index 없으면: type = ALL (100만 개 행 다 스캔)
- age Index 있으면: type = range (33만 개만 스캔)
```

#### 2️⃣ key (Index 사용 여부)

- `NULL`: Index를 안 씀 (나쁨)
- `idx_age`: Index를 씀 (좋음)

#### 3️⃣ rows (스캔한 행 수)

- 작을수록 좋음
- 100만 개 중에서 33만 개만 스캔하는 게 100만 개 다 스캔하는 것보다 좋음

### Extra (추가 정보)

| Extra | 의미 |
|-------|------|
| **Using index** | Covering Index 사용 (테이블 접근 안 함) → 최고! |
| **Using filesort** | 디스크에서 정렬 → 느림! |
| **Using temporary** | 임시 테이블 생성 → 느림! |

---

## JOIN 전략

### Nested Loop Join (기본)
작은 테이블의 각 행마다 큰 테이블을 검색한다.

```
users 테이블 100개 행:
- user_id = 1 → orders에서 검색 (5개)
- user_id = 2 → orders에서 검색 (3개)
...

총 I/O = 100 + (100 * 5) = 600회 (느림!)
```

### Hash Join (MySQL 8.0+) 

작은 테이블을 메모리의 해시 테이블로 만들고, 큰 테이블을 스캔하며 O(1) 시간에 매칭한다.

```
Step 1: orders를 메모리 해시 테이블로 로드 (1회)
Step 2: users를 스캔하며 해시 테이블과 매칭 (1회)

총 I/O = orders + users 읽기 = 1100회 (상수시간)
```

**Nested Loop Join보다 훨씬 빠르다!**

---

## 최적화 주의사항

### ❌ 함수 사용하면 Index 못 탐

```
WHERE YEAR(created_at) = 2024  ❌ Index 못 탐
WHERE UPPER(name) = 'KIM'       ❌ Index 못 탐

WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01'  ✅ Index 탐
WHERE name = 'KIM'                                               ✅ Index 탐
```

### ❌ OR 조건 조심

```
WHERE age > 30 OR status = 'inactive'  ❌ Full Scan 가능

WHERE age > 30
UNION
WHERE status = 'inactive'  ✅ 각각 Index 탐 가능
```

### ❌ LIKE '%...' 시작하면 Index 못 탐

```
WHERE name LIKE '%Kim%'   ❌ Index 못 탐
WHERE name LIKE '%Kim'    ❌ Index 못 탐

WHERE name LIKE 'Kim%'    ✅ Index 탐 (앞부터 시작)
```

---

## 실전 체크리스트 (느린 쿼리 마주쳤을 때)

1️⃣ **EXPLAIN 실행**
   ```
   EXPLAIN SELECT ... \G
   ```

2️⃣ **type 확인**
   - `type = ALL` or `index` → ❌ Index 만들자
   - `type = range` or `ref` → ✅ 괜찮음

3️⃣ **key 확인**
   - `key = NULL` → ❌ Index 사용 안 함
   - `key = idx_name` → ✅ Index 사용 중

4️⃣ **rows 확인**
   - 너무 많으면 → ❌ 쿼리 조건 다시 생각해봐
   - 적으면 → ✅ 좋음

5️⃣ **Extra 확인**
   - `Using filesort`, `Using temporary` → ❌ 쿼리 재설계
   - `Using index` → ✅ Covering Index (최고!)

---

## 핵심 요점 정리

| 주제 | 핵심 |
|------|------|
| **Index** | 테이블 전체 스캔 대신 빠르게 찾음 (Covering Index 최고) |
| **SQL Injection** | **PrepareStatement** 무조건 쓰기 |
| **Statement vs PrepareStatement** | PrepareStatement가 Parsing 1회 (빠름) |
| **Named Query** | 자주 쓰는 쿼리는 Parsing 1회 캐싱 |
| **Dynamic Query** | 매번 Parsing (느리지만 유연) |
| **QueryDsl** | Dynamic Query + 타입 안전 (추천) |
| **Optimizer** | Cost-Based로 가장 싼 실행 계획 선택 |
| **EXPLAIN** | **type, key, rows** 확인 (type 가장 중요!) |
| **Hash Join** | 메모리에 작은 테이블 로드 (빠름) |

---

