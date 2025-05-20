package book.controller.output;

import java.util.ArrayList;
import java.util.List;

import book.dto.PostDTO;

public class PostOutput {
	private int page;
	private int totalPage;
	private List<PostDTO> listResult = new ArrayList<>();

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

	public List<PostDTO> getListResult() {
		return listResult;
	}

	public void setListResult(List<PostDTO> listResult) {
		this.listResult = listResult;
	}

}
