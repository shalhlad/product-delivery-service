package com.shalhlad.productdeliveryservice.entity.user;

import com.shalhlad.productdeliveryservice.entity.department.Department;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "employees")
public class Employee extends User {

  @ManyToOne
  @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
  private Department department;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Employee employee = (Employee) o;
    return getId() != null && Objects.equals(getId(), employee.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
