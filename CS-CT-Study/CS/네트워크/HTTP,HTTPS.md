# HTTP/HTTPS 네트워크 학습 자료

## 1️⃣ HTTP vs HTTPS

### HTTP (HyperText Transfer Protocol)

웹에서 클라이언트-서버 간 데이터 통신 프로토콜

- **Stateless**: 각 요청 독립적
- **Connectionless**: 응답 후 연결 종료
- 80포트, 평문 전송 → 보안 취약

### HTTPS (HTTP Secure)

HTTP + 보안 계층(TLS/SSL)

- 데이터 암호화 (기밀성)
- 서버 신원 확인 (인증)
- 데이터 무결성 (변조 방지)
- 443포트

### TLS 핸드셰이크

```
1. Client Hello (암호화 방식, 키 공유)
2. Server Hello (인증서, 키 공유)
3. 암호화 통신 시작

핵심: 비대칭키로 대칭키 교환 → 대칭키로 데이터 암호화
이유: 보안(비대칭) + 속도(대칭) 둘 다 확보

```

### HTTP 버전

```
HTTP/1.1: Keep-Alive, HOL Blocking
HTTP/2: 멀티플렉싱, 헤더 압축
HTTP/3: UDP 기반, 0-RTT

```

---

## 2️⃣ HTTP 헤더

### 정의

```
HTTP 헤더
HTTP 메시지의 메타데이터를 담는 키-값 쌍
역할: 캐싱, 인증, 압축, CORS 등 제어

```

### 4가지 분류

```
1. 일반: Cache-Control, Connection, Date
2. 요청: Host, Authorization, Cookie, Origin
3. 응답: Set-Cookie, ETag, Access-Control-*
4. 엔티티: Content-Type, Content-Length

```

### Cache-Control 디렉티브

```
max-age=<초>
리소스가 신선한 기간, 이 시간 동안 서버 안 감

no-cache
캐시 저장은 하되, 사용 전 항상 서버 검증
→ 304 Not Modified로 빠르게 응답

no-store
아예 캐싱 금지, 매번 서버에서 다운로드
용도: 민감한 정보 (은행, 결제)

public / private
public: 모든 캐시 가능 (CDN 포함)
private: 브라우저만 캐시

immutable
절대 안 바뀜, 새로고침해도 검증 안 함
조건: 파일명에 해시 필수 (app.a3f5b2c.js)

```

### 검증 메커니즘

```
ETag (Entity Tag)
리소스의 특정 버전 식별자 (해시값)
If-None-Match로 검증 → 304 or 200

Last-Modified
마지막 수정 시간
If-Modified-Since로 검증 → 304 or 200

비교:
ETag: 콘텐츠 기반, 정확, 느림
Last-Modified: 시간 기반, 빠름, 1초 단위

실무: 둘 다 사용 (ETag 우선)

```

### 실전 캐싱 전략

```
HTML:
Cache-Control: no-cache

빌드 JS/CSS:
Cache-Control: public, max-age=31536000, immutable
파일명: app.a3f5b2c.js (해시)

이미지:
Cache-Control: public, max-age=86400

API:
Cache-Control: private, max-age=60

```

---

## 3️⃣ HTTP/HTTPS 동작 과정

```
1. DNS 조회
   브라우저→OS→로컬→루트→TLD→Authoritative → IP

2. TCP 3-way Handshake
   SYN → SYN-ACK → ACK

3. TLS Handshake (HTTPS만)
   인증서 검증, 키 교환

4. HTTP 요청/응답
   GET /index.html → 200 OK

5. 브라우저 렌더링

6. TCP 종료 (4-way Handshake)

```

---

## 4️⃣ CORS

### 정의

```
CORS (Cross-Origin Resource Sharing)
교차 출처 리소스 공유

배경: Same-Origin Policy
브라우저는 같은 출처만 리소스 접근 허용
Origin = 프로토콜 + 도메인 + 포트

역할:
서버가 특정 출처를 명시적으로 허용하는 메커니즘
Access-Control-Allow-Origin 헤더로 제어

```

### Simple vs Preflight

```
Simple Request (바로 전송)
- 메서드: GET, POST, HEAD
- Content-Type: form-urlencoded, multipart, text/plain
- 커스텀 헤더 없음

Preflight (OPTIONS 먼저)
- 메서드: PUT, DELETE, PATCH
- Content-Type: application/json
- 커스텀 헤더: Authorization 등

동작: OPTIONS로 사전 확인 → 실제 요청

```

### Credentials

```
정의: 쿠키 포함 요청

프론트: credentials: 'include'
백엔드:
- Access-Control-Allow-Origin: https://myapp.com (특정!)
- Access-Control-Allow-Credentials: true

중요: credentials + * 불가
이유: 모든 사이트가 쿠키로 요청 가능 (CSRF 위험)

```

### SameSite vs CORS

```
SameSite: 쿠키 전송 제어 (CSRF 방어)
CORS: 리소스 접근 제어
→ 완전히 다른 개념

```

---

## 5️⃣ GET vs POST

### 비교

```
GET: 조회, 멱등 O, 캐싱 O, Body X
POST: 생성, 멱등 X, Body O
PUT: 전체 교체, 멱등 O
PATCH: 부분 수정
DELETE: 삭제, 멱등 O

```

### 멱등성

```
정의: 같은 요청을 여러 번 해도 결과 동일

멱등: GET, PUT, DELETE
비멱등: POST

```

---

## 6️⃣ 쿠키와 세션

### 정의

```
배경: HTTP는 Stateless
각 요청은 독립적, 이전 요청 기억 못 함
→ 상태 유지 메커니즘 필요

쿠키
클라이언트(브라우저)에 저장되는 데이터
- Set-Cookie로 전송 → 브라우저 저장
- 이후 자동으로 Cookie 헤더에 포함
- Key=Value 형태

세션
서버에 저장되는 사용자 정보
- 세션 ID만 쿠키로 전달
- 실제 데이터는 서버에만

```

### 비교

```
           쿠키         세션
저장       브라우저     서버
보안       낮음         높음
용량       4KB         무제한
수평 확장  쉬움         어려움

```

### 쿠키 보안 속성

```
HttpOnly
JS 접근 차단 → XSS 방어

Secure
HTTPS에서만 전송 → 중간자 공격 방어

SameSite
다른 사이트에서 쿠키 전송 제어 → CSRF 방어
- Strict: 같은 사이트만
- Lax: GET 링크 허용 (기본)
- None: 모든 요청 (Secure 필수)

```

### JWT

```
정의: JSON 기반 자가 수용적 토큰
구조: Header.Payload.Signature

장점: Stateless, 수평 확장 쉬움
단점: 즉시 무효화 어려움

실무: Access Token(JWT,15분) + Refresh Token(세션,2주)

```

---

## 7️⃣ DNS

```
조회 순서:
브라우저→OS→로컬→루트→TLD→Authoritative

TTL: 캐시 유효 시간

레코드:
A: IPv4
AAAA: IPv6
CNAME: 별칭
MX: 메일

```

---

## 8️⃣ REST와 RESTful

### 정의

```
REST
네트워크 아키텍처 원칙
자원을 URI로 표현, HTTP 메서드로 행위 정의

핵심:
1. 자원: URI (/users/1)
2. 행위: HTTP 메서드 (GET, POST)
3. 표현: JSON, XML

6가지 원칙:
Client-Server, Stateless, Cacheable,
Uniform Interface, Layered System, Code on Demand

RESTful
REST 원칙을 잘 지키는 API

```

### 설계 원칙

```
1. 명사 사용 (동사 금지)
   ✓ GET /users
   ✗ GET /getUsers

2. 복수형
   ✓ /users, /posts

3. 계층 구조
   ✓ /users/1/posts/5/comments

4. HTTP 메서드로 행위
   GET: 조회
   POST: 생성
   PUT: 전체 수정
   PATCH: 부분 수정
   DELETE: 삭제

```

### 상태 코드

```
2xx: 성공 (200, 201, 204)
3xx: 리다이렉션 (301, 304)
4xx: 클라이언트 오류 (400, 401, 403, 404)
5xx: 서버 오류 (500, 503)

```

---

## 🎯 최종 체크리스트

- [ ] HTTP: Stateless, 80포트, 평문
- [ ] HTTPS: TLS, 443포트, 암호화
- [ ] TLS: 비대칭키→대칭키
- [ ] Cache-Control: max-age, no-cache, no-store, immutable
- [ ] no-cache = 검증 후 사용
- [ ] no-store = 저장 금지
- [ ] ETag = 해시, Last-Modified = 시간
- [ ] CORS = 다른 출처 리소스
