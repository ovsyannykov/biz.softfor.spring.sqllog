[![GitHub License](https://img.shields.io/github/license/ovsyannykov/biz.softfor.spring.sqllog)](license.md)
[![Java CI with Maven](https://github.com/ovsyannykov/biz.softfor.spring.sqllog/actions/workflows/maven.yml/badge.svg)](https://github.com/ovsyannykov/biz.softfor.spring.sqllog/actions/workflows/maven.yml)
[![Maven Central Version](https://img.shields.io/maven-central/v/biz.softfor/biz.softfor.spring.sqllog)](https://mvnrepository.com/artifact/biz.softfor/biz.softfor.spring.sqllog)

<h1 align="center">biz.softfor.jpa.sqllog</h1>
<p align="right">
  <a href="readme.md">EN</a>
  <a href="readme.ru.md">RU</a>
</p>

![Demo](doc/images/readme.png)

— це Spring Boot стартер для логування SQL запитів під час тестування та
налагодження.

Отримати в логах текст запиту SQL з параметрами дуже просто.
Додайте залежність у __POM__-файл Вашої Spring Boot програми:
```xml
<dependency>
  <groupId>biz.softfor</groupId>
  <artifactId>biz.softfor.spring.sqllog</artifactId>
  <version>3.4.4</version>
</dependency>
```

Часто у сценаріях використання ORM чи інших сторонніх бібліотек ми б хотіли
контролювати склад та кількість запитів до БД. Використовуйте для цього клас
__SqlCountValidator__:
```java
SqlCountValidator validator = new SqlCountValidator(em);
... business logic ...
validator.read(2).insert(4).update(1).delete(1).assertTotal();
```

Також можливе __попереднє__ встановлення параметрів для assert:
```java
SqlCountValidator validator
= SqlCountValidator.builder().insert(4).entityManager(em).build();
... business logic ...
validator.assertTotal();
```

Ці приклади взято з [автотестів](src/test/java/biz/softfor/spring/sqllog/) пакету.

Під час розробки я використав статтю Vlad Mihalcea
[The best way to log SQL statements with JDBC, JPA or Hibernate](https://vladmihalcea.com/the-best-way-to-log-jdbc-statements/).
Я оформив код у вигляді Spring Boot starter, прибрав дублювання логування
batch-запитів та додав лічильник запитів.

## Ліцензія

Цей проект має ліцензію MIT - подробиці дивіться у файлі [license.md](license.md).
