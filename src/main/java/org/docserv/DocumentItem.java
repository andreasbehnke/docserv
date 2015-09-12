package org.docserv;

public class DocumentItem {
	
	private String path;
	
	private boolean isFolder;
	
	public DocumentItem() {}

	public DocumentItem(String path, boolean isFolder) {
		super();
		this.path = path;
		this.isFolder = isFolder;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
}
