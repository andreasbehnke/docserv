package org.docserv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

@Service
public class PdfDocumentProvider implements DocumentProvider {

	@Override
	public boolean supports(String mimeType) {
		return "application/pdf".equalsIgnoreCase(mimeType);
	}
	
	private Document loadPage(Document document, PDDocument pdfDocument, Integer pageIndex, Integer pageResolution) throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			int pageNumberInternal = Math.min(pageIndex, pdfDocument.getNumberOfPages() - 1);
			PDFRenderer renderer = new PDFRenderer(pdfDocument);
			BufferedImage pageImage = renderer.renderImageWithDPI(pageNumberInternal, pageResolution, ImageType.RGB);
			ImageIO.write(pageImage, "PNG", output);
			document.setPageIndex(pageNumberInternal);
			document.setPageImageSrc(Base64.getEncoder().encodeToString(output.toByteArray()));
		}
		return document;
	}

	@Override
	public Document create(DocumentRequest request, InputStream input) throws IOException {
		Document document = new Document();
		try (PDDocument pdfDocument = PDDocument.load(input)) {
			document.setPageCount(pdfDocument.getNumberOfPages());
			if (request.getPageIndex() != null) {
				document = loadPage(document, pdfDocument, request.getPageIndex(), request.getPageResolution());
			}
		}
		return document;
	}
}
