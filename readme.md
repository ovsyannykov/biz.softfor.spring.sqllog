<p>
[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor.spring.sqllog)](license.md)
[![Java CI with Maven](https://github.com/ovsyannykov/biz.softfor.spring.sqllog/actions/workflows/maven.yml/badge.svg)](https://github.com/ovsyannykov/biz.softfor.spring.sqllog/actions/workflows/maven.yml)
[![Maven Central Version](https://img.shields.io/maven-central/v/biz.softfor/biz.softfor.spring.sqllog)](https://mvnrepository.com/artifact/biz.softfor/biz.softfor.spring.sqllog)
</p>

<h1 align="center">biz.softfor.jpa.sqllog</h1>
<p align="right">
  <a href="readme.ua.md">UA</a>
  <a href="readme.ru.md">RU</a>
</p>

![Demo](doc/images/readme.png)

— This is a Spring Boot starter for logging SQL requests for testing and
debugging.

It’s very easy to get the text of an SQL query with parameters in the logs.
Add the dependency to the __POM__ file of your Spring Boot application:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.sqllog</artifactId>
  <version>3.4.4</version>
</dependency>
```

Often in scenarios using ORM or other third-party libraries we would like to
control the composition and number of requests to the database. Use the
__SqlCountValidator__ class for this:
```java
SqlCountValidator validator = new SqlCountValidator(em);
... business logic ...
validator.read(2).insert(4).update(1).delete(1).assertTotal();
```

It is also possible to __preset__ the parameters for assert:
```java
SqlCountValidator validator
= SqlCountValidator.builder().insert(4).entityManager(em).build();
... business logic ...
validator.assertTotal();
```

These examples are taken from the [autotests](src/test/java/biz/softfor/spring/sqllog/)
package.

During development, I used Vlad Mihalcea's article
[The best way to log SQL statements with JDBC, JPA or Hibernate](https://vladmihalcea.com/the-best-way-to-log-jdbc-statements/).
I compiled the code as a Spring Boot starter, removed duplicate logging of batch
queries, and added a query counter.

## License

This project is licensed under the MIT License - see the [license.md](license.md) file for details.
