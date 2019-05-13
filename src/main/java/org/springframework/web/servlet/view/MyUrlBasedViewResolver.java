package org.springframework.web.servlet.view;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.MyView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public class MyUrlBasedViewResolver extends MyAbstractCachingViewResolver{


    private String prefix = "";

    private String suffix = "";

    @Nullable
    private Class<?> viewClass;

    @Nullable
    private String contentType;

    @Nullable
    private String requestContextAttribute;

    @Nullable
    private Boolean exposePathVariables;

    @Nullable
    private String[] exposedContextBeanNames;

    private final Map<String, Object> staticAttributes = new HashMap<>();

    public void setPrefix(@Nullable String prefix) {
        this.prefix = (prefix != null ? prefix : "");
    }


    protected String getPrefix() {
        return this.prefix;
    }


    public void setSuffix(@Nullable String suffix) {
        this.suffix = (suffix != null ? suffix : "");
    }

    protected String getSuffix() {
        return this.suffix;
    }


    @Nullable
    protected String getContentType() {
        return this.contentType;
    }

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }

    @Nullable
    protected String getRequestContextAttribute() {
        return this.requestContextAttribute;
    }


    public Map<String, Object> getAttributesMap() {
        return this.staticAttributes;
    }

    @Nullable
    private Boolean exposeContextBeansAsAttributes;


    public void setExposePathVariables(@Nullable Boolean exposePathVariables) {
        this.exposePathVariables = exposePathVariables;
    }

    @Nullable
    protected Boolean getExposePathVariables() {
        return this.exposePathVariables;
    }

    @Nullable
    protected Boolean getExposeContextBeansAsAttributes() {
        return this.exposeContextBeansAsAttributes;
    }

    public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
        this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
    }


    public void setExposedContextBeanNames(@Nullable String... exposedContextBeanNames) {
        this.exposedContextBeanNames = exposedContextBeanNames;
    }

    @Nullable
    protected String[] getExposedContextBeanNames() {
        return this.exposedContextBeanNames;
    }

    @Override
    protected MyView loadView(String viewName, Locale locale) throws Exception {
        MyAbstractUrlBasedView view = buildView(viewName);
        MyView result = applyLifecycleMethods(viewName, view);
        return (view.checkResource(locale) ? result : null);
    }

    public void setViewClass(@Nullable Class<?> viewClass) {
        if (viewClass != null && !requiredViewClass().isAssignableFrom(viewClass)) {
            throw new IllegalArgumentException("Given view class [" + viewClass.getName() +
                    "] is not of type [" + requiredViewClass().getName() + "]");
        }
        this.viewClass = viewClass;
    }
    protected Class<?> requiredViewClass() {
        return MyAbstractUrlBasedView.class;
    }
    @Override
    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName;
    }

    protected MyAbstractUrlBasedView buildView(String viewName) throws Exception {
        Class<?> viewClass = getViewClass();
        Assert.state(viewClass != null, "No view class");

        MyAbstractUrlBasedView view = (MyAbstractUrlBasedView) BeanUtils.instantiateClass(viewClass);
        view.setUrl(getPrefix() + viewName + getSuffix());

        String contentType = getContentType();
        if (contentType != null) {
            view.setContentType(contentType);
        }

        view.setRequestContextAttribute(getRequestContextAttribute());
        view.setAttributesMap(getAttributesMap());

        Boolean exposePathVariables = getExposePathVariables();
        if (exposePathVariables != null) {
            view.setExposePathVariables(exposePathVariables);
        }
        Boolean exposeContextBeansAsAttributes = getExposeContextBeansAsAttributes();
        if (exposeContextBeansAsAttributes != null) {
            view.setExposeContextBeansAsAttributes(exposeContextBeansAsAttributes);
        }
        String[] exposedContextBeanNames = getExposedContextBeanNames();
        if (exposedContextBeanNames != null) {
            view.setExposedContextBeanNames(exposedContextBeanNames);
        }

        return view;
    }

    @Nullable
    protected Class<?> getViewClass() {
        return this.viewClass;
    }

    protected MyView applyLifecycleMethods(String viewName, MyAbstractUrlBasedView view) {
        ApplicationContext context = getApplicationContext();
        if (context != null) {
            Object initialized = context.getAutowireCapableBeanFactory().initializeBean(view, viewName);
            if (initialized instanceof MyView) {
                return (MyView) initialized;
            }
        }
        return view;
    }
}
