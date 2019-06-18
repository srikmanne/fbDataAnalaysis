package com.irev.controller;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInit extends AbstractAnnotationConfigDispatcherServletInitializer {
    
    // MultipartConfigElement Parameters:
    // Directory location where files will be stored.
    private final String LOCATION = "/tmp";
    // Maximum size of uploaded files (1 GB).
    private long MAX_FILE_SIZE = 1073741824;
    // Maximum multipart/form-data request size (1 GB).
    private long MAX_REQUEST_SIZE = 1073741824;
    // Size when files are written to disk (1 MB).
    private int FILE_SIZE_THRESHOLD = 1048576;

    @Override
    protected Class<?>[] getRootConfigClasses() { 
        return new Class[] { AppConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }
    
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
    
//    @Override
//    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
//        registration.setMultipartConfig(getMultipartConfigElement());
//    }
    
//    private MultipartConfigElement getMultipartConfigElement() {
//        MultipartConfigElement multipartConfigElement =
//            new MultipartConfigElement( LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
//        return multipartConfigElement;
//    }
    
    @Override
    public void onStartup(ServletContext container) throws ServletException {
        super.onStartup(container);
        // Add utf8 filter.
        FilterRegistration.Dynamic encodingFilter =
            container.addFilter("encoding-filter", new CharacterEncodingFilter());
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");
    }
}
