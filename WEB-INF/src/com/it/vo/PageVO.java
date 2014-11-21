package com.it.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageVO {

	private int page;
	private int totalPages;
	private int total;
	private int rowsPerPage;
	private List<Object> currentPage = new ArrayList<Object>();

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public PageVO(int page, int rows, List<Object> alls) {
		super();
		if (page > 0 && rows > 0 && alls != null && !alls.isEmpty()) {
			this.page = page;
			this.rowsPerPage = rows;
			this.total = alls.size();
			this.totalPages = (int) Math.ceil((double) total / rows);

			if (page < totalPages) {
				currentPage = alls.subList(rows * (page - 1), rows * (page - 1)
						+ rows);
			} else if (page == totalPages) {
				currentPage = alls.subList(rows * (page - 1), total);
			}
		}

	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Object> getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(List<Object> currentPage) {
		this.currentPage = currentPage;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		PageVO vo = new PageVO(2, 3, Arrays.asList(new Object[] { "1", "2",
				"3", "4", "5", "6", "7" }));
		System.out.println(vo.getCurrentPage());

	}

}
