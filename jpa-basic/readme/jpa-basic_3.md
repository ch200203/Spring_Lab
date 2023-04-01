# JPA-기본3

- 요청이 올떄 마다 EntityManagerFactory를 통해 EntityManager를 생성한다.
- EntityManager는 내부적으로 DB Connection을 통해 DB를 사용한다.


## 영속성 컨텍스트

- Entity를 영구 저장하는 환경
- 영속성 컨텍스트는 놀시적인 개념이라 눈에 보이지 않는다.
- EntityManager를 통해 영속성 컨텍스트에 접근한다.
`EntityManager.persist(entity);`
- Entity를 DB에 저장하는 코드
    - 영속성 컨텍스트를 통해 Entity를 영속화 한다는 뜻
    - 즉, Entity를 영속성 컨텍스트에 저장하는 것

## 엔티티의 생명주기

- 비영속(new/transient)
    - 영속성 컨텍스트와 전혀 관계없는 **새로운** 상태
- 셩속(managed)
    - 영속성 컨텍스트에 **관리**되는 상태
- 준영속(detached)
    - 영속성 컨텍스트에 저장되었다가 **분리**된 상태
- 삭제(removed)
    - **삭제**된 상태

### 1. 비영속
```java
// 객체를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");
```

### 2. 영속
```java
// 객체를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");

EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

// 객체를 저장한 상태(영속)
em.persist(member);
```

### 3. 준영속, 삭제

```java
// 회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태
em.detach(member);
```

```java
//객체를 삭제한 상태(삭제)
em.remove(member);
```

## 영속성 컨텍스트의 이점

- 1차 캐시
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기지연(transactional write-behind)
- 변경 감지(Dirty Checking) 
- 지연 로딩(Lazy Loading)