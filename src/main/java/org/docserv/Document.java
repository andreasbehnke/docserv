package org.docserv;


public class Document {

	private String name;
	
	private int pageCount;
	
	private String lastModified;
	
	private String pageImageSrc;
	
	private int pageIndex;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getPageImageSrc() {
		return pageImageSrc;
	}

	public void setPageImageSrc(String pageImageSrc) {
		this.pageImageSrc = pageImageSrc;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
}
