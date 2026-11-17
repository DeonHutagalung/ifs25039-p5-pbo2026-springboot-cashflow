package org.delcom.ifs25039springbootcashflow.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

class AppConfigTest {

    @TempDir
    Path tmp;

    @Test
    void mapsUploadsHandler() {
        AppConfig config = new AppConfig(tmp.toString());
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(new StaticWebApplicationContext(),
                new MockServletContext());
        config.addResourceHandlers(registry);
        assertTrue(registry.hasMappingForPattern("/uploads/**"));
    }
}
