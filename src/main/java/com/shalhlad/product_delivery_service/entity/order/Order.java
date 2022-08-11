package com.shalhlad.product_delivery_service.entity.order;

import com.shalhlad.product_delivery_service.entity.department.Department;
import com.shalhlad.product_delivery_service.entity.product.Product;
import com.shalhlad.product_delivery_service.entity.user.User;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
  private Department department;

  @Column(nullable = false, length = 50)
  private String deliveryAddress;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "order_handlers_id", referencedColumnName = "id", nullable = false)
  private OrderHandlers orderHandlers;

  @ElementCollection
  @JoinTable(
      name = "ordered_products_table",
      joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
  )
  @MapKeyJoinColumn(name = "product_id", referencedColumnName = "id")
  private Map<Product, OrderedProductDetails> orderedProducts;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Stage stage;

  @ElementCollection
  @JoinTable(
      name = "order_stage_history",
      joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
  )
  @MapKeyEnumerated(EnumType.STRING)
  @MapKeyColumn(name = "stage")
  @Column(name = "date", nullable = false)
  private Map<Stage, Date> stageHistory;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Order order = (Order) o;
    return id != null && Objects.equals(id, order.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
