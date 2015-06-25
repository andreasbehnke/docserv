package org.pdfserv;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

@Component
@Path("/document")
public class DocumentResource {

	@GET
    public String helloWorld() {
    	return "Hello World!";
    }

}
