package org.docserv;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/document")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentResource {
	
	@Autowired
	private DocumentFactory documentFactory;

	@GET
	public List<String> documentList() {
    	return Arrays.asList(
			Application.DOCUMENT_FOLDER.list( 
				(File dir, String name) -> {
					File file = new File(dir, name);
					return file.isFile() && documentFactory.isSupported(file); 
				}
			)
		);
    }

	private File openDocumentFile(String documentName) {
		File documentFile = new File(Application.DOCUMENT_FOLDER, documentName);
		if (!documentFile.exists()) {
			throw new NotFoundException();
		}
		return documentFile;
	}
	
	private EntityTag createETag(File documentFile) {
		return new EntityTag(documentFile.lastModified() + "");
	}
	
	private boolean isUnchanged(Request request, EntityTag eTag) {
		return request.evaluatePreconditions(eTag) != null;
	}
	
	@GET
	@Path("/{documentName}")
	public Response getDocument(@PathParam("documentName") String documentName, @Context Request request) throws IOException {
		File documentFile = openDocumentFile(documentName);
		EntityTag eTag = createETag(documentFile);
		if (isUnchanged(request, eTag)) {
			return Response.notModified(eTag).build();
		} else {
			DocumentRequest documentRequest = new DocumentRequest(documentName);
			return Response.ok(documentFactory.create(documentRequest)).tag(eTag).build();
		}
	}
	
	@GET
	@Path("/{documentName}/{pageIndex}")
	public Response getPage(
			@PathParam("documentName") String documentName, 
			@PathParam("pageIndex") int pageIndex,
			@QueryParam("pageResolution") Integer pageResolution,
			@Context Request request) throws IOException {
		File documentFile = openDocumentFile(documentName);
		EntityTag eTag = createETag(documentFile);
		if (isUnchanged(request, eTag)) {
			return Response.notModified(eTag).build();
		} else {
			int pageResolutionInternal = (pageResolution == null) ? 72 : pageResolution;
			DocumentRequest documentRequest = new DocumentRequest(documentName, pageIndex, pageResolutionInternal);
			return Response.ok(documentFactory.create(documentRequest)).tag(eTag).build();
		}
	}
}
