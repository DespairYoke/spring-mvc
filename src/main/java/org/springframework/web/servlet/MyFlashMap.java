package org.springframework.web.servlet;

import java.util.HashMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public final class MyFlashMap extends HashMap<String, Object> implements Comparable<MyFlashMap>{
    @Override
    public int compareTo(MyFlashMap myFlashMap) {
        return 0;
    }
}
