package org.docserv;

public class DocumentItem {
	
	private String name;
	
	private String path;
	
	private boolean isFolder;
	
	public DocumentItem() {}

	public DocumentItem(String name, String path, boolean isFolder) {
		this.name = name;
		this.path = path;
		this.isFolder = isFolder;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
