package com.zwd.web.context;


import com.zwd.web.WebApplicationInitializer;
import com.zwd.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-17
 **/
public class MyWebApplicationInitalizer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {

        System.out.println("zzzz");
        ServletRegistration.Dynamic spring = servletContext.addServlet("testServlet", new DispatcherServlet());

        spring.addMapping("/*");

        spring.setLoadOnStartup(1);
    }
}
