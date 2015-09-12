package org.docserv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.NotFoundException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentFactory {
	
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@Autowired
	private Set<DocumentProvider> documentProviders;
	
	private final MimeTypes mimeTypes;
	
	public DocumentFactory() {
		try {
			this.mimeTypes = MimeTypesFactory.create(MimeTypes.class.getResourceAsStream("tika-mimetypes.xml"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private DocumentProvider findProvider(InputStream input) throws IOException {
		String mimeType = mimeTypes.detect(input, new Metadata()).toString();
		for (DocumentProvider documentProvider : documentProviders) {
			if (documentProvider.supports(mimeType)) {
				return documentProvider;
			}
		}
		return null;
	}
	
	public boolean isSupported(File file) {
		try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			return findProvider(input) != null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private File openDocumentFile(String documentName) {
		File documentFile = new File(Application.DOCUMENT_FOLDER, documentName);
		if (!documentFile.exists()) {
			throw new NotFoundException();
		}
		return documentFile;
	}
	
	private Date getLastModified(File documentFile) {
		return new Date(documentFile.lastModified());
	}
	
	public Document create(DocumentRequest request) {
		File file = openDocumentFile(request.getName());
		Document document = null;
		try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			input.mark(mimeTypes.getMinLength());
			DocumentProvider documentProvider = findProvider(input);
			if (documentProvider != null) {
				document = documentProvider.create(request, input);
			} else {
				document = new Document();
			}
		} catch (IOException e) {
			throw new NotFoundException();
		}
		document.setName(request.getName());
		document.setLastModified(DATE_FORMAT.format(getLastModified(file)));
		return document;
	}
}
