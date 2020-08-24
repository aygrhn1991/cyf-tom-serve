package com.cyf.tom.suit.util;

import javax.servlet.http.HttpServletRequest;

public class UtilHttp {
    public static String getBaseUrl(HttpServletRequest request) {
        //request.getServerName()----->www.abc.com
        //request.getContextPath()----->/xyz
        return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
    }
}
