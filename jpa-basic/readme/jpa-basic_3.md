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
    ```java
    // 엔티티를 생성한 상태(비영속)
    Member member = new Member();
    member.setId("member1");
    member.setUsername("회원1");

    // 엔티티를 영속
    em.persist(member);

    ---- 
    Member member = new Member();
    member.setId("member1");
    member.setUsername("회원1");

    // 1차 캐시에 저장됨
    em.persist(member);
    

    // 1차 캐시에서 조회
    Member findMember = em.find(Member.class, "member1");

    // 데이터베이스에서 조회
    Member findMember2 = em.find(Memeber.class, "member2");
    ```
- 동일성(identity) 보장
    ```java
    // 영속 엔티티의 동일성 보장
    Member a = em.find(Member.class, "member1");
    Member b = em.find(Member.class, "member1");

    System.out.println(a == b) // 동일성 비교 true
    ```
    - 1차 캐시로 `REPEATABLE READ` 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 **어플리케이션 차원**에서 제공
    
- 트랜잭션을 지원하는 쓰기지연(transactional write-behind)
    ```java
    EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    //엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.
    transaction.begin(); // [트랜잭션] 시작

    em.persist(memberA);
    em.persist(memberB);
    //여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
    //커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.
    transaction.commit(); // [트랜잭션] 커밋
    ```
- 변경 감지(Dirty Checking) 
    ```java
    EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    transaction.begin(); // [트랜잭션] 시작

    // 영속 엔티티 조회
    Member memberA = em.find(Member.class, "memberA");

    // 영속 엔티티 데이터 수정
    memberA.setUsername("hi");
    memberA.setAge(10);

    //em.update(member) 이런 코드가 있어야 하지 않을까?
    transaction.commit(); // [트랜잭션] 커밋
    ```
- 엔티티 삭제
    ```java
    //삭제 대상 엔티티 조회
    Member memberA = em.find(Member.class, “memberA");
    em.remove(memberA); //엔티티 삭제
    ```
- 지연 로딩(Lazy Loading)

## 플러시
- 영속성 컨텍스트의 변경내용을 데이터베이스에 반영

### 플러시 발생
    - 변경감지
    - 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
    - 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송(등록, 수정, 삭제쿼리)

### 영속성 컨텍스트를 플러시하는 방법
    - em.flush() - 직접호출 
    - 트랜잭션 커밋 - 플러시 자동 호출
    - JPQL 쿼리 실행 - 플러시 자동 호출

```java
public class JpaMain {

    public static void main(String[] args) {
        
        ...

        Member member = new Member(200L, "A");
        entityManager.persist(member);

        entityManager.flush();
        System.out.println("-----");

        tx.commit();
    }
}
```
```sql
Hibernate: 
    /* insert hellojpa.Member */ 
    insert into
            Member
            (name, id) 
        values
            (?, ?)
-----
```
- 쿼리를 미리 반영하고 싶으면 커밋 전에 강제로 flush()를 호출할 수 있다.
- flush()를 해도 1차 캐시는 유지된다.
    - 1차 캐시와는 상관없이 쓰기 지연 SQL 저장소에 쌓인 쿼리나 변경 감지한 내용이 DB에 반영되는 것이다.


### JPQL 쿼리 실행시 플러시가 자동으로 호출되는 이유
```java
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);

//중간에 JPQL 실행
query = em.createQuery("select m from Member m", Member.class);
List<Member> members= query.getResultList();
```

### 플러시 모드 옵션

`em.setFlushMode(FlushModeType.COMMIT)`
- **FlushModeType.AUTO**
    - 커밋이나 쿼리를 실행할 떄, 플러시 (기본값)
- **FlushModeType.COMMIT**
    - 커밋할 때만 플러시가 실행
    - 쿼리를 할 때는 실행되지 않음
        - 중간에 실행하는 JPQL 쿼리가 앞과는 전혀 다른 데이터를 써서 굳이 당장 플러시할 필요가 없을 때 사용

### 플러시의 특징
- 플러시는 영속성 컨텍스트를 비우지 않음
- 영속성 컨텍스트의 변경 내용을 데이터베이스에 동기화하는 작업
- 트랜잭션이라는 개념이 있기 때문에 동작 가능한 매커니즘
- **트랜잭션**이라는 작업의 단위가 중요 &rarr; 커밋 직전에만 동기화 하면 됨(즉, 변경내역을 DB에 날려주면 된다.)


## 준영속 상태

- 영속
    - 영속성 컨텍스트에서 관리되는 상태
    - insert뿐만 아니라 조회 시점에서 1차 캐시에 없어서 DB에서 가져와 1차 캐시에 올려두는 상태도 포함
- 준영속
    - 영속 상태의 Entity가 영속성 컨텍스트에서 분리되는 상태
    - `detach()`를 실행하면 트랜잭션을 커밋해도 영향을 받지 않음
    - 영속성 컨텍스트가 더 이상 관리하지 않는 상태
    - 즉, **영속성 컨텍스트가 제공하는 기능을 사용할 수 없는 상태**