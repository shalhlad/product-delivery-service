package com.shalhlad.product_delivery_service.entity.order;

import com.shalhlad.product_delivery_service.entity.user.Employee;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "order_handlers_table")
public class OrderHandlers {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "current_handler_id", referencedColumnName = "id")
  private Employee currentHandler;

  @OneToOne
  @JoinColumn(name = "collector_id", referencedColumnName = "id")
  private Employee collector;

  @OneToOne
  @JoinColumn(name = "courier_id", referencedColumnName = "id")
  private Employee courier;

}
