package org.example.orderservice.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "order_id",referencedColumnName = "id")
    private Order order;


    @Column(nullable = false)
    private int productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;
}
