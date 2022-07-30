package com.shalhlad.product_delivery_service.entity.product;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Entity(name = "products")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 50)
  private String name;

  @Column(precision = 5, scale = 2, nullable = false)
  private BigDecimal price;

  @ManyToOne
  @JoinColumn(name = "category_id", referencedColumnName = "id")
  private Category category;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Product product = (Product) o;
    return id != null && Objects.equals(id, product.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
