package org.pdfserv;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	
	public static File DOCUMENT_FOLDER = new File(System.getProperty("user.dir") + File.separator + "documents");

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println(String.format("Document folder: %s", DOCUMENT_FOLDER));
    }

}
