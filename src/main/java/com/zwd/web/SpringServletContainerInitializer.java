package com.zwd.web;

import com.zwd.web.servlet.DispatcherServlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-18
 **/
//@HandlesTypes(WebApplicationInitializer.class)
public class SpringServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

        System.out.println("zzzz");
        ServletRegistration.Dynamic spring = ctx.addServlet("testServlet", new DispatcherServlet());

        spring.addMapping("/*");

        spring.setLoadOnStartup(1);

    }
}
