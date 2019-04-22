package org.springframework.web.servlet.view;

import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public class MyUrlBasedViewResolver {


    private String prefix = "";

    private String suffix = "";

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

}
