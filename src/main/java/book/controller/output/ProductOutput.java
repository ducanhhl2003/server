package book.controller.output;

import java.util.ArrayList;
import java.util.List;

import book.dto.ProductDTO;

public class ProductOutput {
	private int page;
	private int totalPage;
	private int categoryId;
	private List<ProductDTO> listResult = new ArrayList<>();

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

	public List<ProductDTO> getListResult() {
		return listResult;
	}

	public void setListResult(List<ProductDTO> listResult) {
		this.listResult = listResult;
	}

}
