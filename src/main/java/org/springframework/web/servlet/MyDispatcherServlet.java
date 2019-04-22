package org.springframework.web.servlet;



import org.springframework.context.ApplicationContext;


public class MyDispatcherServlet extends MyFrameworkServlet{

    protected void onRefresh(ApplicationContext context) {

        System.out.println("dispatherServlet onfresh");
//        initStrategies(context);
    }
}
