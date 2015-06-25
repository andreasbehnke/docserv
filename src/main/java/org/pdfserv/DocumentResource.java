package org.pdfserv;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

@Component
@Path("/document")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentResource {

	@GET
	public List<String> documentList() {
    	return Arrays.asList(
    			Application.DOCUMENT_FOLDER.list( 
    					(File dir, String name) -> { return name.endsWith("pdf") || name.endsWith("PDF"); }
    			)
    		);
    }

}
