package book.exception;

import lombok.Getter;

@Getter
public class ImportExcelException extends RuntimeException {
	private final byte[] errorFile;

	public ImportExcelException(String message, byte[] errorFile) {
		super(message);
		this.errorFile = errorFile;
	}
}
