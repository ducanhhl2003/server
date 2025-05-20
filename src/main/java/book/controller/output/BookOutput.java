package book.controller.output;

import java.util.ArrayList;
import java.util.List;

import book.dto.BookDTO;

public class BookOutput {
	private int page;
	private int totalPage;
	private List<BookDTO> listResult = new ArrayList<>();

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<BookDTO> getListResult() {
		return listResult;
	}

	public void setListResult(List<BookDTO> listResult) {
		this.listResult = listResult;
	}

}
