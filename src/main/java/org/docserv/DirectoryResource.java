package org.docserv;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/directory")
@Produces(MediaType.APPLICATION_JSON)
public class DirectoryResource {
	
	@Autowired
	private DocumentFactory documentFactory;
	
	private List<DocumentItem> generateDocumentList(String path) {

		File directory = Application.DOCUMENT_FOLDER;
		List<DocumentItem> documentItems = new ArrayList<>();
		URI root = Application.DOCUMENT_FOLDER.toURI();
		if (path != null && !path.trim().isEmpty()) {
			directory = new File(directory, path);
		}
		if (directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				boolean addFile = file.isDirectory() || documentFactory.isSupported(file); 
				if (addFile) {
					String itemName = file.getName();
					String itemPath = root.relativize(file.toURI()).toString();
					documentItems.add(new DocumentItem(itemName, itemPath, file.isDirectory()));
				}
			}
		} else {
			throw new WebApplicationException(404);
		}
		return documentItems;
	}
	
	@GET
	public List<DocumentItem> getDirectory() throws IOException {
		return generateDocumentList(null);
	}
	
	@GET
	@Path("/{path:.*}")
	public List<DocumentItem> getDirectory(@PathParam("path") String path) throws IOException {
		return generateDocumentList(path);
	}
}
