package org.pdfserv;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

@Component
@Path("/document")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentResource {
	
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@GET
	public List<String> documentList() {
    	return Arrays.asList(
    			Application.DOCUMENT_FOLDER.list( 
    					(File dir, String name) -> { return name.endsWith("pdf") || name.endsWith("PDF"); }
    			)
    		);
    }

	private File openFile(String documentName) {
		return new File(Application.DOCUMENT_FOLDER, documentName);
	}
	
	private int readPageCount(File documentFile) throws IOException {
		try (PDDocument pdfDocument = PDDocument.load(documentFile)) {
			return pdfDocument.getNumberOfPages();
		}
	}
	
	private Date getLastModified(File documentFile) {
		return new Date(documentFile.lastModified());
	}
	
	private EntityTag createETag(File documentFile) {
		return new EntityTag(documentFile.lastModified() + "");
	}
	
	@GET
	@Path("/{documentName}")
	public Response getDocument(@PathParam("documentName") String documentName) throws IOException {
		File documentFile = openFile(documentName);
		if (!documentFile.exists()) {
			throw new NotFoundException();
		}
		Document document = new Document();
		document.setName(documentName);
		document.setLastModified(DATE_FORMAT.format(getLastModified(documentFile)));
		document.setPageCount(readPageCount(documentFile));
		return Response.ok(document).tag(createETag(documentFile)).build();
	}
}
