package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public interface MyHandlerMapping {

    @Nullable
    MyHandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
