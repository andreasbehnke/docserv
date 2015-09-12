package org.docserv;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentProvider {

	boolean supports(String mimeType);
	
	Document create(DocumentRequest request, InputStream input) throws IOException;
}
