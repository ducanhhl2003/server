package book.exception;

import lombok.Getter;

@Getter
public class DataNotFoundException extends RuntimeException {
	public Integer code;
	public String key;

	public DataNotFoundException(String message, Integer code) {
		super(message);
		this.code = code;
	}

	public DataNotFoundException(String message) {
		super(message);
	}

	public DataNotFoundException(String message, String key) {
		super(message);
		this.key = key;
	}
}