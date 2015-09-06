package org.docserv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
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

	private File openDocumentFile(String documentName) {
		File documentFile = new File(Application.DOCUMENT_FOLDER, documentName);
		if (!documentFile.exists()) {
			throw new NotFoundException();
		}
		return documentFile;
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
	
	private boolean isUnchanged(Request request, EntityTag eTag) {
		return request.evaluatePreconditions(eTag) != null;
	}
	
	private Document createDocument(String documentName, File documentFile) throws IOException {
		Document document = new Document();
		document.setName(documentName);
		document.setLastModified(DATE_FORMAT.format(getLastModified(documentFile)));
		document.setPageCount(readPageCount(documentFile));
		return document;
	}
	
	private Document createDocument(String documentName, File documentFile, int pageIndex, int pageResolution) throws IOException {
		Document document = createDocument(documentName, documentFile);
		document.setPageIndex(pageIndex);
		try (PDDocument pdfDocument = PDDocument.load(documentFile); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			int pageNumberInternal = Math.min(pageIndex, pdfDocument.getNumberOfPages() - 1);
			PDFRenderer renderer = new PDFRenderer(pdfDocument);
			BufferedImage pageImage = renderer.renderImageWithDPI(pageNumberInternal, pageResolution, ImageType.RGB);
			ImageIO.write(pageImage, "PNG", output);
			document.setPageImageSrc(Base64.getEncoder().encodeToString(output.toByteArray()));
		}
		return document;
	}
	
	@GET
	@Path("/{documentName}")
	public Response getDocument(@PathParam("documentName") String documentName, @Context Request request) throws IOException {
		File documentFile = openDocumentFile(documentName);
		EntityTag eTag = createETag(documentFile);
		if (isUnchanged(request, eTag)) {
			return Response.notModified(eTag).build();
		} else {
			return Response.ok(createDocument(documentName, documentFile)).tag(eTag).build();
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
			return Response.ok(createDocument(documentName, documentFile, pageIndex, pageResolutionInternal)).tag(eTag).build();
		}
	}
}
