package org.pdfserv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import javax.ws.rs.core.StreamingOutput;

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
	
	private StreamingOutput createPage(File documentFile, int pageNumber, int pageResolution) {
		return (OutputStream output) -> {
			try (PDDocument pdfDocument = PDDocument.load(documentFile)) {
				int pageNumberInternal = Math.min(pageNumber, pdfDocument.getNumberOfPages() - 1);
				PDFRenderer renderer = new PDFRenderer(pdfDocument);
				BufferedImage pageImage = renderer.renderImageWithDPI(pageNumberInternal, pageResolution, ImageType.RGB);
				ImageIO.write(pageImage, "PNG", output);
			} finally {
				output.close();
			}
		};
	}
	
	@GET
	@Path("/{documentName}/{pageNumber}")
	@Produces("image/png")
	public Response getPage(
			@PathParam("documentName") String documentName, 
			@PathParam("pageNumber") int pageNumber,
			@QueryParam("pageResolution") Integer pageResolution,
			@Context Request request) throws IOException {
		File documentFile = openDocumentFile(documentName);
		EntityTag eTag = createETag(documentFile);
		if (isUnchanged(request, eTag)) {
			return Response.notModified(eTag).build();
		} else {
			int pageResolutionInternal = (pageResolution == null) ? 72 : pageResolution;
			return Response.ok(createPage(documentFile, pageNumber, pageResolutionInternal)).tag(eTag).build();
		}
	}
}
