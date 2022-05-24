# Spring Boot JPA with TDD

## 요구사항

### **SpringDataJPA 를 설정한다.**

- datasource : h2 or mysql

### **엔티티를 구성한다**

- 회원(User)
    - id (PK) (auto increment)
    - name
    - age
    - hobby
    - **created_at**
    - **updated_at**
- 게시글(Post)
    - id (PK) (auto increment)
    - title
    - content
    - **created_at**
    - **updated_at**
- 회원과 게시글에 대한 연관관계를 설정한다.
    - 회원과 게시글은 1:N 관계이다.
- 게시글 Repository를 구현한다. (PostRepository)

### **API를 구현한다.**

- 게시글 조회
    - 페이징 조회 (GET "/posts")
    - 단건 조회 (GET "/posts/{id}")
- 게시글 작성 (POST "/posts")
- 게시글 수정 (PUT "/posts/{id}")

## Build

- Java 18
- Gradle
- Spring Boot Starter Web
- Spring data JPA

## UML

![게시판 RestApi](https://user-images.githubusercontent.com/28651727/169930663-84dcb88a-6524-48cd-9953-0dce96c5b3b3.jpg)

## 단위 테스트

[단위 테스트](https://htmlpreview.github.io/?https://github.com/waterfogsw/springboot-board-jpa/blob/main/docs/Test-Result.html)
