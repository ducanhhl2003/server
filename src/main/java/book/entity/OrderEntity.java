package book.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import book.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name")
	private Integer user_id;

	@Column(name = "fullname")
	private String fullName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone_number")
	private String phoneNumber;
	@Column(name = "address")
	private String address;
	@Column(name = "price")
	private Float price;

	@Column(name = "note")
	private String note;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "order_date", updatable = false)
	private Date orderDate;

	@Column(name = "status")
	@Enumerated(EnumType.STRING) // Lưu trạng thái dưới dạng String trong DB
	private OrderStatus status;

	@Column(name = "total_money")
	private Float totalMoney;

	@Column(name = "shipping_method")
	private String shippingMethod;
	@Column(name = "shipping_address")
	private String shippingAdress;
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "shipping_date", updatable = false)
	private Date shippingDate;
	@Column(name = "tracking_number")
	private String trackingNumber;
	@Column(name = "payment_method")
	private String paymentMethod;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "productcategories", joinColumns = @JoinColumn(name = "productId"), inverseJoinColumns = @JoinColumn(name = "categoryId"))
	private Set<CategoryEntity> categories;

	@JsonBackReference
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderDetailEntity> orderDetails;

}
