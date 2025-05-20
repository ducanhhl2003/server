package book.controller.output;

import java.util.ArrayList;
import java.util.List;

import book.dto.OrderDTO;

public class OrderOutput {
	private int page;
	private int totalPage;
	private List<OrderDTO> listResult = new ArrayList<>();

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

	public List<OrderDTO> getListResult() {
		return listResult;
	}

	public void setListResult(List<OrderDTO> listResult) {
		this.listResult = listResult;
	}

}
