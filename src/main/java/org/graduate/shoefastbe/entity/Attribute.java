package org.graduate.shoefastbe.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_attribute")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private Long productId;
    private Long size;
    private Long stock;
    private Long cache;
    private LocalDate createDate;
    private LocalDate modifyDate;
    @Version
    private Long version;
}