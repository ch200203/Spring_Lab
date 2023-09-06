# JPA-기본2


## JPA의 구동 방식

![jpa_1](https://3513843782-files.gitbook.io/~/files/v0/b/gitbook-legacy-files/o/assets%2F-LxjHkZu4T9MzJ5fEMNe%2Fsync%2F082bea60e7a1caf438568c0bba92d5078a719b7e.png?generation=1615621418112064&alt=media)

> JPA의 Persistence 클래스가 META-INF/persistence.xml 설정 파일을 읽어서 EntityManagerFactory라는 클래스를 생성한다. 여기서 필요할 때마다 EntityManager를 만든다.

## 객체와 테이블의 매핑

```java
@Entity
public class Member {

    @Id
    private Long id;
    private String name;

    // getter, setter
}
```
- @Entity: JPA가 관리할 객체
- @Id: 데이터베이스 PK와 매핑

### 회원 저장 예시
```java
public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code

        try {
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA");

            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
```

> 단, 실무에서는 이렇게 활용할 일은 없다. 대부분의 기능을 Spring이 대신 해주기 때문


### 객체의 수정
```java
public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        try {
            // 수정할 대상을 가져온다.
            Member findMember = entityManager.find(Member.class, 1L);

            System.out.println("id: " + findMember.getId());
            System.out.println("name: " + findMember.getName());

            findMember.setName("helloJPA");

/*
            수정한 객체를 따로 저장(save)하지 않아도 된다. 
            데이터를 JPA를 통해 가져오면 변경 여부를 트랜잭션 커밋 시점에
            다 체크해서 바뀐 내용에 대해 업데이트 쿼리를 만들어 날린다.
*/
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            entityManager.close();
        }

        entityManagerFactory.close();
    }
}
```

**주의**
- `EntityManagerFactory`는 하나만 생성하서 어플리케이션 전체에서 공유 되어야 한다.
- `EntityManager`는 쓰레드 간에 공유해서는 안된다. (사용 후 반드시 종료)
- **`JPA의 모든 데이터 변경은 트랜잭션 안에서 실행되어야 한다.`**


## JPQL 

- 데이터 조회 시 가장 간단한 방법
    - `EntityManager.find()`
    - 객체 그래프 탐색 (a.getB().getC())
- 특정 조건을 만족하는 데이터를 검색하고 싶다면? &rarr; JPQL을 이용
```java
List<Member> result = em.createQuery("select m from Member", Member.class)
    .result();
```
- JPA는 테이블이 아닌 Entity 객체를 기반으로 개발
- find(검색) 또한 테이블이 아닌 Entity 객체를 대상으로 함
- 필요한 데이터만 DB에서 불러오려면 검색조건이 포함된 SQL이 필요하다.
    - 하지만 쿼리를 쓰면 SQL에 종속되게 된다. &rarr; JPA를 사용하는 이유가 없음.
- 이 때, **객체를 대상으로 검색할 수 있게하는 기술이 JPQL이다.**



### JPQL의 특징
- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리언어 제공
- SQL과 문법적으로 유사, `SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN` 지원
- 테이블이 아닌 객체를 대상으로 검색하는 **객체 지향 쿼리**
- SQL을 추상화하여 특정 데이터베이스에 의존하지 않음
- JPQL = 객체지향 SQL

**SQL과의 차이점**
- JPQL = **Entity 객체를 대상**으로 쿼리를 날림
- SQL = **Table을 대상**으로 쿼리를 날림