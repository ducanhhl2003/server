package book.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "categoryStatus")
	private Boolean categoryStatus;

	@ManyToMany(mappedBy = "categories")
	private Set<BookEntity> books;

	@JsonIgnore // Ngăn vòng lặp khi serialize
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
	private Set<ProductEntity> products;

}
