package book.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "booktransactions")
public class BookTransactionEntity extends BaseEntity {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "borrow_date")
	private LocalDate borrow_date;

	@Column(name = "return_date")
	private LocalDate return_date;

	@ManyToOne
	@JoinColumn(name = "userId", referencedColumnName = "id")
	private UserEntity user;
	@ManyToOne
	@JoinColumn(name = "bookId", referencedColumnName = "id")
	private BookEntity book;

}
