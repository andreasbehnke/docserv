package org.docserv;

public class DocumentRequest {
	
	private String name;
	
	private Integer pageIndex;
	
	private Integer pageResolution;
	
	public DocumentRequest() {}
	
	public DocumentRequest(String name) {
		this.name = name;
	}
	
	public DocumentRequest(String name, Integer pageIndex, Integer pageResolution) {
		this.name = name;
		this.pageIndex = pageIndex;
		this.pageResolution = pageResolution;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageResolution() {
		return pageResolution;
	}

	public void setPageResolution(Integer pageResolution) {
		this.pageResolution = pageResolution;
	}
}
