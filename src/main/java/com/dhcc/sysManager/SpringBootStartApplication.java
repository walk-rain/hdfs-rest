package com.dhcc.sysManager;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class SpringBootStartApplication extends SpringBootServletInitializer {
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(new Class[] { SysManagerApplication.class });
    }
}