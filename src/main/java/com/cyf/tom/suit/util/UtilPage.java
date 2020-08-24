package com.cyf.tom.suit.util;


import com.cyf.tom.suit.request.Search;

public class UtilPage {

    public static String getPage(int page, int limit) {
        return String.format("%s,%s", (page - 1) * limit, limit);
    }

    public static String getPage(Search model) {
        return String.format("%s,%s", (model.page - 1) * model.limit, model.limit);
    }
}
