package biz.softfor.spring.sqllog;

import biz.softfor.spring.sqllog.testassets.User;
import biz.softfor.spring.sqllog.testassets.UserGroup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.ws.Holder;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest(showSql = false)
@EnableAutoConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { ConfigSqlLog.class })
@ExtendWith(OutputCaptureExtension.class)
public class SqllogTest {

  @PersistenceContext
  protected EntityManager em;

  @Autowired
  protected PlatformTransactionManager tm;

  @Test
  void read() {
    Holder<Set<User>> hUgs = new Holder<>();
    new TransactionTemplate(tm).executeWithoutResult(status -> {
      hUgs.value = em.find(UserGroup.class, 1).getUsers();
    });
    assertThat(hUgs.value).as("Group 1 users").hasSize(2);
    List<Long> ids
    = hUgs.value.stream().map(User::getId).collect(Collectors.toList());
    assertThat(ids).as("Group 1 users ids").containsExactlyInAnyOrder(1L, 2L);
  }

  @Test
  void readTest(CapturedOutput output) {
    SqlCountValidator validator = new SqlCountValidator(em);
    read();
    validator.select(2).assertTotal();
    assertThat(output).as("Output log").contains(
      "select ug1_0.id,ug1_0.name from userGroups ug1_0 where ug1_0.id=1"
    , "select u1_0.groupId,u1_1.id,u1_1.email,u1_1.password,u1_1.personId,u1_1.username from users_groups u1_0 join users u1_1 on u1_1.id=u1_0.userId where u1_0.groupId=1"
    );
  }

  @Test
  void createTest(CapturedOutput output) {
    SqlCountValidator validator
    = SqlCountValidator.builder().insert(4).entityManager(em).build();
    new TransactionTemplate(tm).executeWithoutResult(status -> {
      UserGroup g = new UserGroup();
      g.setName("g");
      em.persist(g);
      User u1 = new User();
      u1.setEmail("u1@a.co");
      u1.setUsername("u1");
      u1.setPassword("p@ssw0rD");
      u1.addGroup(g);
      em.persist(u1);
      User u2 = new User();
      u2.setEmail("u2@a.co");
      u2.setUsername("u2");
      u2.setPassword("p@ssw0rD");
      u2.addGroup(g);
      em.persist(u2);
    });
    validator.assertTotal();
    assertThat(output).as("Output log").containsIgnoringWhitespaces(
      "insert into userGroups(name,id) values ('g',default)"
    , "insert into users(email,password,personId,username,id) values('u1@a.co','p@ssw0rD',NULL,'u1',default)"
    , "insert into users(email,password,personId,username,id) values('u2@a.co','p@ssw0rD',NULL,'u2',default)"
    , "BatchSize:2, Query:[\"insert into users_groups(groupId,userId) values(?, ?)\"], Params:[(2,3),(2,4)]"
    );
  }

}
