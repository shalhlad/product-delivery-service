package com.shalhlad.productdeliveryservice.entity.order;

import com.shalhlad.productdeliveryservice.entity.user.Employee;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_handlers_table")
public class OrderHandlers {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
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
