package book.controller.output;

import java.util.ArrayList;
import java.util.List;

import book.dto.PermissionDTO;

public class PermissionOutput {
	private int page;
	private int totalPage;
	private List<PermissionDTO> listResult = new ArrayList<>();

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

	public List<PermissionDTO> getListResult() {
		return listResult;
	}

	public void setListResult(List<PermissionDTO> listResult) {
		this.listResult = listResult;
	}

}
