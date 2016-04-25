package com.koenv.universalminecraftapi.http;

import com.koenv.universalminecraftapi.http.rest.RestQueryParamsMap;
import spark.QueryParamsMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebServerQueryParamsMap implements RestQueryParamsMap {
    private QueryParamsMap sparkQueryParams;
    private String[] values;

    public WebServerQueryParamsMap(QueryParamsMap sparkQueryParams) {
        this.sparkQueryParams = sparkQueryParams;
        if (sparkQueryParams.hasValue()) {
            this.values = sparkQueryParams.values(); // cache because it clones
        } else {
            this.values = new String[]{};
        }
    }

    @Override
    public RestQueryParamsMap get(String key) {
        return new WebServerQueryParamsMap(sparkQueryParams.get(key));
    }

    @Override
    public boolean hasValue() {
        return values.length == 1;
    }

    @Override
    public String value() {
        return values[0];
    }

    @Override
    public boolean hasValues() {
        return values.length > 1;
    }

    @Override
    public List<String> values() {
        return Arrays.asList(values);
    }

    @Override
    public boolean hasChildren() {
        return sparkQueryParams.hasKeys();
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(sparkQueryParams.toMap().keySet());
    }
}
