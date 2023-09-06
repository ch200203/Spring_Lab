# JPA-기본1

지금 시대는 객체를 -> 관계형 DB에 보관
## JPA란?
- Java Persistence API
- 자바 진영의 **ORM** 기술 표준

## ORM?
- Object-Relational Mapping(객체 관계 매핑)
- 객체는 객체대로 설계
- 관계형 데이터베이스는 관계형 데이터베이스대로 설계
- ORM 프레임워크가 중간에서 매핑
- 대중적인 언어들은 ORM이 존재하고 있음.

JPA는 어플리케이션과 JDBC 사이에서 동작하고 있음

## JPA는 표준 명세
- JPA는 인터페이스의 모음
- JPA 2.1 표준 명세를 구현한 3가지 구현체
- **하이버네이트**(거의 사용), Eclipse Link, DataNucleus

## JPA를 왜 사용해야하는지?
- SQL 중심적인 개발에서 객체 중심으로 개발
- 생산성
- 유지보수
- 패러다임의 불일치 해결
- 성능
- 데이터 접근 추상화와 벤더 독립성
- 표준

### 생산성

- 저장 : jpa.persist(member)
- 조회 : Member member = jpa.find(memberId)
- 수정 : memeber.setName("변경할 이름")
- 삭제 : jpa.remove(member)

### 유지보수 
- 기존 : 필드 변경시 모든 SQL 수정
- JPA : 필드만 추가하면 됨, SQL은 JPA가 대신 해줌

### 패러다임의 불일치 해결

개발자가 할일 : jpa.persist(객체)
JPA 가 할일 : insert into A, insert into B...

### JPA와 연관관계, 객체 그래프 탐색

- 연관관계 저장
- 신뢰할 수 있는 엔티티(JPA가 관리하는 객체를 엔티티라고 부름), 계층

### JPA와 비교하기

```java
String memberId = "100";
Member member1 = jpa.find(Member.class, memberId);
Member member2 = jpa.find(Member.class, memberId);

member1 == member2 // true
```

동일한 트랜잭션에서 조회한 엔티티는 같음을 보장한다.

## *JPA 성능 최적화 기능*

1. 1차 캐시와 동일성(indentity) 보장
2. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
3. 지연 로딩(Lazy Loading)

### 1차 캐시와 동일성 보장

1. 같은 트랜잭션 안에서는 같은 엔티티를 반환 - 약간의 조회 성능 향상
2. DB Isolation Level이 Read Commit이여도 어플리케이션에서 Repeatable Read를 보장

```java
String memberId = "100";
Member member1 = jpa.find(Member.class, memberId); // SQL 
Member member2 = jpa.find(Member.class, memberId); // 캐시

print(member1 == member2) // true
```

&rarr; 즉, SQL은 한번만 실행된다.

### 트랜잭션을 지원하는 쓰기지연 - Insert

1. 트랜잭션을 커밋할 때까지 insert sql 을 모음
2. JDBC BATCH SQL 기능을 사용해서 SQL 전송

```java
transction.begin(); // [트랜잭션] 시작

em.persist(memberA)
em.persist(memberB)
em.persist(memberC)
// 여기까지 insert sql을 데이터베이스에 보내지 않는다.

// 커밋하는 순간 insert sql을 모아서 보낸다.
transactional.commit(); // [트랜잭션] 커밋
```

### 트랜잭션을 지원하느 쓰기지연 - update

1. update, delete로 인한 로우(row)락 시간 최소화
2. 트랜잭션 커밋 시 update, delete sql 실행하고 바로 커밋

```java
transction.begin(); // [트랜잭션] 시작

changeMember(memberA)
deleteMember(memberB)
비즈니스 로직 수행(); // 비즈니스 로직 수행동안 DB row lock이 걸리지 않는다.

// 커밋하는 순간 데이터베이스에 update, delete sql을 보낸다.
transactional.commit(); // [트랜잭션] 커밋
```

### 지연로딩과 즉시로딩
- 지연로딩 : 객체가 실제 사용될 떄 로딩
- 즉시로딩 : JOIN SQL로 한번에 연관된 객체까지 미리 조회한다.

```java
// 지연 로딩
Member member = memberDAO.find(memberId); // SQL : SELECT * FROM MEMBER;
Team team = member.getTeam();
String teamName = team.getName();       // SQL : SELECT * FROM TEAM;
```

```java
// 즉시로딩
Member member = memberDAO.find(memberId); // SQL : SELECT M.*, T.* FROM MEMBER M JOIN TEAM T...
Team team = member.getTeam();
String teamName = team.getName();      
```