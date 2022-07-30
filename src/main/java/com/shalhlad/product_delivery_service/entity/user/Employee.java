package com.shalhlad.product_delivery_service.entity.user;

import com.shalhlad.product_delivery_service.entity.department.Department;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Employee extends User {

  @ManyToOne
  @JoinColumn(name = "department_id", referencedColumnName = "id")
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
