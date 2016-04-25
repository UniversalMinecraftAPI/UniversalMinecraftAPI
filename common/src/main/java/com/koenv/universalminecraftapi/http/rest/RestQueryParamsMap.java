package com.koenv.universalminecraftapi.http.rest;

import java.util.List;

public interface RestQueryParamsMap {
    RestQueryParamsMap get(String key);

    boolean hasValue();

    String value();

    boolean hasValues();

    List<String> values();

    boolean hasChildren();

    List<String> getKeys();
}
