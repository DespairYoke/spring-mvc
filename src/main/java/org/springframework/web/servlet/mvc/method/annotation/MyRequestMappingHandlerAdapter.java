//package org.springframework.web.servlet.mvc.method.annotation;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.BeanFactoryAware;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.config.ConfigurableBeanFactory;
//import org.springframework.lang.Nullable;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.MyModelAndView;
//import org.springframework.web.servlet.mvc.method.MyAbstractHandlerMethodAdapter;
//import org.springframework.web.util.WebUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
///**
// * TODO...
// *
// * @author zwd
// * @since 2019-04-28
// **/
//public class MyRequestMappingHandlerAdapter extends MyAbstractHandlerMethodAdapter
//        implements BeanFactoryAware, InitializingBean {
//
//    @Nullable
//    private ConfigurableBeanFactory beanFactory;
//
//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) {
//        if (beanFactory instanceof ConfigurableBeanFactory) {
//            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
//        }
//    }
//    @Override
//    public void afterPropertiesSet() throws Exception {
//
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//
//    @Override
//    public boolean supports(Object handler) {
//        return false;
//    }
//
//    @Override
//    protected MyModelAndView handleInternal(HttpServletRequest request,
//                                            HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
//
//        MyModelAndView mav;
//        checkRequest(request);
//
//        // Execute invokeHandlerMethod in synchronized block if required.
//        if (this.synchronizeOnSession) {
//            HttpSession session = request.getSession(false);
//            if (session != null) {
//                Object mutex = WebUtils.getSessionMutex(session);
//                synchronized (mutex) {
//                    mav = invokeHandlerMethod(request, response, handlerMethod);
//                }
//            }
//            else {
//                // No HttpSession available -> no mutex necessary
//                mav = invokeHandlerMethod(request, response, handlerMethod);
//            }
//        }
//        else {
//            // No synchronization on session demanded at all...
//            mav = invokeHandlerMethod(request, response, handlerMethod);
//        }
//
//        if (!response.containsHeader(HEADER_CACHE_CONTROL)) {
//            if (getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
//                applyCacheSeconds(response, this.cacheSecondsForSessionAttributeHandlers);
//            }
//            else {
//                prepareResponse(response);
//            }
//        }
//
//        return mav;
//    }
//}
