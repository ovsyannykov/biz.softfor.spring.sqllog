package biz.softfor.spring.sqllog.testassets;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = User.TABLE)
@Getter
@Setter
@ToString
@JsonFilter("User")
public class User extends SetStoredEntity<Long> implements Serializable {

  public final static String TABLE = "users";
  public final static String TITLE = "user";

  @Column
  @NotBlank
  @Size(min = 2, max = 63)
  private String username;

  @Column
  @Email
  @NotBlank
  @Size(max = 63)
  private String email;

  @Column
  @NotBlank
  private String password;

  @Column
  private Long personId;

  @ManyToMany(mappedBy = UserGroup.USERS, fetch = FetchType.LAZY)
  @JsonIgnoreProperties(value = { UserGroup.USERS }, allowSetters = true)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<UserGroup> groups;
  public final static String GROUPS = "groups";

  public void addGroup(UserGroup v) {
    if(v != null) {
      Set<User> items = v.getUsers();
      if(items == null) {
        items = new HashSet<>();
        v.setUsers(items);
      }
      items.add(this);
      if(groups == null) {
        groups = new HashSet<>();
      }
      groups.add(v);
    }
  }

  public void removeGroup(UserGroup v) {
    if(v != null) {
      Set<User> items = v.getUsers();
      if(items != null) {
        items.remove(this);
      }
      if(groups != null) {
        groups.remove(v);
      }
    }
  }

  @SuppressWarnings("empty-statement")
  public void removeGroups() {
    if(groups != null) {
      for(Iterator<UserGroup> i = groups.iterator(); i.hasNext();) {
        Set<User> items = i.next().getUsers();
        if(items != null) {
          items.remove(this);
        }
        i.remove();
      }
    }
  }

  public void setGroups(Set<UserGroup> items) {
    if(groups != items) {
      removeGroups();
      if(items != null) {
        for(UserGroup v : items) {
          v.addUser(this);
        }
      }
      groups = items;
    }
  }

  private final static long serialVersionUID = 0L;

}
