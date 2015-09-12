package org.docserv;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/services")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(DocumentResource.class);
        register(DirectoryResource.class);
    }

}
