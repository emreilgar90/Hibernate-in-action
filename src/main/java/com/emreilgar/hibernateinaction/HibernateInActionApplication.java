package com.emreilgar.hibernateinaction;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Formula;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


@Repository
interface CustomerRepository extends JpaRepository<Customer, Long> {
}

@Repository
interface ProductRepository extends JpaRepository<Product, Long> {
}

@SpringBootApplication
@Slf4j
public class HibernateInActionApplication {
	public static void main(String[] args) {
		SpringApplication.run(HibernateInActionApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository, ProductRepository productRepository) {
		return args -> {
			Customer customer = new Customer();
			customer.setName("Emre");
			customer.setCustomerType(CustomerType.ELITE);

			Product p = new Product();
			p.setTitle("iphone");
			p.setPrice(BigDecimal.valueOf(45000.12));
			Product p1 = new Product();
			p1.setTitle("Samsung");
			p1.setPrice(BigDecimal.valueOf(28000.12));
			customer.setProductList(List.of(p, p1));
			customerRepository.save(customer);
			log.info(String.valueOf(customer));
		};
	}
}
enum CustomerType {
	ELITE, SUPER, NORMAL
}
@Embeddable
class Metadata {
	private Timestamp createdAt;
	private Timestamp updateAt;
}

@Data
@Entity
@Table(name = "Customers")
class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Enumerated(value = EnumType.STRING)
	private CustomerType customerType;
	private Metadata metadata;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Product> productList;
}

@Data
@Getter
@Setter
@Entity
@Table(name = "Products")
class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private BigDecimal price;
	private BigDecimal tax; // Bu alanı veritabanında saklamak için setter/getter kullanabilirsiniz.

	@ManyToOne
	private Customer customer;

	@Formula("price * 0.18") // Formül sadece bir hesaplamadır, veritabanına kaydedilmez.
	private BigDecimal calculatedTax;
}


