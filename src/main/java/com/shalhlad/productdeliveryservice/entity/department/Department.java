package com.shalhlad.productdeliveryservice.entity.department;

import com.shalhlad.productdeliveryservice.entity.product.Product;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "departments")
public class Department {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "address", unique = true, nullable = false, length = 50)
  private String address;

  @ElementCollection
  @JoinTable(
      name = "product_warehouse",
      joinColumns = @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
  )
  @MapKeyJoinColumn(name = "product_id", referencedColumnName = "id")
  @Column(name = "quantity", nullable = false)
  private Map<Product, Integer> productWarehouse;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Department that = (Department) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
