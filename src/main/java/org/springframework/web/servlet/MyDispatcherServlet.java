package org.springframework.web.servlet;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.mvc.method.annotation.MyRequestMappingHandlerMapping;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;


public class MyDispatcherServlet extends MyFrameworkServlet{


    private Log logger =  LogFactory.getLog(getClass());


    private boolean cleanupAfterInclude = true;

    @Nullable
    private MyLocaleResolver localeResolver;

    @Nullable
    private MyThemeResolver themeResolver;

    @Nullable
    private MyFlashMapManager flashMapManager;

    @Nullable
    private List<MyHandlerMapping> handlerMappings;

    private static final Properties defaultStrategies;

    @Nullable
    private List<MyHandlerAdapter> handlerAdapters;

    public static final String LOCALE_RESOLVER_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".LOCALE_RESOLVER";


    public static final String THEME_RESOLVER_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".THEME_RESOLVER";

    public static final String THEME_SOURCE_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".THEME_SOURCE";

    private static final String DEFAULT_STRATEGIES_PREFIX = "org.springframework.web.servlet";

    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".CONTEXT";

    private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";

    public static final String HANDLER_ADAPTER_BEAN_NAME = "myhandlerAdapter";

    private boolean detectAllHandlerAdapters = true;

    static {
        // Load default strategy implementations from properties file.
        // This is currently strictly internal and not meant to be customized
        // by application developers.
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, MyDispatcherServlet.class);
            defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not load '" + DEFAULT_STRATEGIES_PATH + "': " + ex.getMessage());
        }
    }

    protected void onRefresh(ApplicationContext context) {

        System.out.println("=======dispatherServlet onfresh");
        initStrategies(context);
    }


    public void initStrategies(ApplicationContext context) {
        initHandlerMappings(context);
        initHandlerAdapters(context);
    }

    private void initHandlerMappings(ApplicationContext context) {

        this.handlerMappings = null;
        if (this.handlerMappings == null) {
            this.handlerMappings = getDefaultStrategies(context, MyHandlerMapping.class);
            if (logger.isDebugEnabled()) {
                logger.debug("No HandlerMappings found in servlet '" + getServletName() + "': using default");
            }
        }
    }
    private void initHandlerAdapters(ApplicationContext context) {
        this.handlerAdapters = null;

        if (this.detectAllHandlerAdapters) {
            // Find all HandlerAdapters in the ApplicationContext, including ancestor contexts.
            Map<String, MyHandlerAdapter> matchingBeans =
                    BeanFactoryUtils.beansOfTypeIncludingAncestors(context, MyHandlerAdapter.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerAdapters = new ArrayList<>(matchingBeans.values());
                // We keep HandlerAdapters in sorted order.
                AnnotationAwareOrderComparator.sort(this.handlerAdapters);
            }
        }
        else {
            try {
                MyHandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, MyHandlerAdapter.class);
                this.handlerAdapters = Collections.singletonList(ha);
            }
            catch (NoSuchBeanDefinitionException ex) {
                // Ignore, we'll add a default HandlerAdapter later.
            }
        }

        // Ensure we have at least some HandlerAdapters, by registering
        // default HandlerAdapters if no other adapters are found.
        if (this.handlerAdapters == null) {
            this.handlerAdapters = getDefaultStrategies(context, MyHandlerAdapter.class);
            if (logger.isDebugEnabled()) {
                logger.debug("No HandlerAdapters found in servlet '" + getServletName() + "': using default");
            }
        }
    }

    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {

        String key = strategyInterface.getName();

        String value = defaultStrategies.getProperty(key);

        if (value != null) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            List<T> strategies = new ArrayList<>(classNames.length);
            for (String className : classNames) {

                try {
                    Class<?> clazz = ClassUtils.forName(className, MyDispatcherServlet.class.getClassLoader());
                    Object strategy = createDefaultStrategy(context, clazz);
                    strategies.add((T) strategy);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return strategies;
        }else {
            return new LinkedList<>();
        }



    }

    protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
        return context.getAutowireCapableBeanFactory().createBean(clazz);
    }

    //    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
//        String key = strategyInterface.getName();
//        String value = defaultStrategies.getProperty(key);
//        if (value != null) {
//            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
//            List<T> strategies = new ArrayList<>(classNames.length);
//            for (String className : classNames) {
//                try {
//                    Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
//                    Object strategy = createDefaultStrategy(context, clazz);
//                    strategies.add((T) strategy);
//                }
//                catch (ClassNotFoundException ex) {
//                    throw new BeanInitializationException(
//                            "Could not find DispatcherServlet's default strategy class [" + className +
//                                    "] for interface [" + key + "]", ex);
//                }
//                catch (LinkageError err) {
//                    throw new BeanInitializationException(
//                            "Unresolvable class definition for DispatcherServlet's default strategy class [" +
//                                    className + "] for interface [" + key + "]", err);
//                }
//            }
//            return strategies;
//        }
//        else {
//            return new LinkedList<>();
//        }
//    }
    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

//            if (logger.isDebugEnabled()) {
//                String resumed = WebAsyncUtils.getAsyncManager(request).hasConcurrentResult() ? " resumed" : "";
//                logger.debug("DispatcherServlet with name '" + getServletName() + "'" + resumed +
//                        " processing " + request.getMethod() + " request for [" + getRequestUri(request) + "]");
//            }

            // Keep a snapshot of the request attributes in case of an include,
            // to be able to restore the original attributes after the include.
//            Map<String, Object> attributesSnapshot = null;
//            if (WebUtils.isIncludeRequest(request)) {
//                attributesSnapshot = new HashMap<>();
//                Enumeration<?> attrNames = request.getAttributeNames();
//                while (attrNames.hasMoreElements()) {
//                    String attrName = (String) attrNames.nextElement();
//                    if (this.cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
//                        attributesSnapshot.put(attrName, request.getAttribute(attrName));
//                    }
//                }
//            }
//
//            // Make framework objects available to handlers and view objects.
//            request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
//            request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
//            request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
//            request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());
//
//            if (this.flashMapManager != null) {
//                MyFlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
//                if (inputFlashMap != null) {
//                    request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
//                }
//                request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
//                request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);
//            }

//            try {
//                doDispatch(request, response);
//            }
//            finally {
//                if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
//                    // Restore the original attribute snapshot, in case of an include.
//                    if (attributesSnapshot != null) {
//                        restoreAttributesAfterInclude(request, attributesSnapshot);
//                    }
//                }
//            }
            doDispatch(request, response);
    }

    @Nullable
    public final ThemeSource getThemeSource() {
        return (getWebApplicationContext() instanceof ThemeSource ? (ThemeSource) getWebApplicationContext() : null);
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MyHandlerExecutionChain mappedHandler = null;

        HttpServletRequest processedRequest = request;
        mappedHandler = getHandler(processedRequest);
//        MyHandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
//
//        mv = ha.handle(processedRequest, response, mappedHandler.getHandler());


    }
    protected MyHandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (MyHandlerAdapter ha : this.handlerAdapters) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Testing handler adapter [" + ha + "]");
                }
                if (ha.supports(handler)) {
                    return ha;
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler +
                "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }
    @Nullable
    protected MyHandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (MyHandlerMapping hm : this.handlerMappings) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
                }
                MyHandlerExecutionChain handler = hm.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

//    @Nullable
//    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
//        if (this.handlerMappings != null) {
//            for (HandlerMapping hm : this.handlerMappings) {
//                if (logger.isTraceEnabled()) {
//                    logger.trace(
//                            "Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
//                }
//                HandlerExecutionChain handler = hm.getHandler(request);
//                if (handler != null) {
//                    return handler;
//                }
//            }
//        }
//        return null;
//    }
}
