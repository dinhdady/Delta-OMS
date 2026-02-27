package com.project.management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String name;
    private String phone;
    private String email;
    private String taxCode;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private CustomerType customerType;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<CustomerAddress> addresses;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    private LocalDateTime createdAt = LocalDateTime.now();
}
