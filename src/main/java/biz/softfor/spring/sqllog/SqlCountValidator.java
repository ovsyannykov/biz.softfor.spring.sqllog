package biz.softfor.spring.sqllog;

import jakarta.persistence.EntityManager;
import lombok.Builder;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Builder(builderClassName = "Builder")
public class SqlCountValidator {

  private int insert;
  private int select;
  private int update;
  private int delete;
  private final EntityManager entityManager;
  @Builder.Default private boolean flash = true;

  public SqlCountValidator(
    int insert
  , int select
  , int update
  , int delete
  , EntityManager em
  , boolean flash
  ) {
    this.insert = insert;
    this.select = select;
    this.update = update;
    this.delete = delete;
    entityManager = em;
    this.flash = flash;
    em.flush();
    em.clear();
    QueryCountHolder.clear();
  }

  public SqlCountValidator(EntityManager em) {
    this(0, 0, 0, 0, em, true);
  }

  public SqlCountValidator insert(int v) {
    insert = v;
    return this;
  }

  public SqlCountValidator select(int v) {
    select = v;
    return this;
  }

  public SqlCountValidator update(int v) {
    update = v;
    return this;
  }

  public SqlCountValidator delete(int v) {
    delete += v;
    return this;
  }

  public void assertTotal() {
    int total = insert + select + update + delete;
    if(flash) {
      entityManager.flush();
    }
    QueryCount c = QueryCountHolder.getGrandTotal();
    assertAll(
      "SQL query count validate"
    , () -> assertThat(c.getInsert()).as("INSERT").isEqualTo(insert)
    , () -> assertThat(c.getSelect()).as("SELECT").isEqualTo(select)
    , () -> assertThat(c.getUpdate()).as("UPDATE").isEqualTo(update)
    , () -> assertThat(c.getDelete()).as("DELETE").isEqualTo(delete)
    , () -> assertThat(c.getTotal()).as("TOTAL").isEqualTo(total)
    );
  }

}
