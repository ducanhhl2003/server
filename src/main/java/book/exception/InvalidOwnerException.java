package book.exception;

import lombok.Getter;

@Getter
public class InvalidOwnerException extends RuntimeException {
	private Integer id;

	public InvalidOwnerException(String message) {
		super(message);
	}
}
