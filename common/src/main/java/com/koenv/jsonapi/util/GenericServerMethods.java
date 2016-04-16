package com.koenv.jsonapi.util;

import com.koenv.jsonapi.http.model.JsonSerializable;
import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.APINamespace;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;

import java.io.File;

@APINamespace("server")
public class GenericServerMethods {
    @APIMethod
    public static PerformanceHolder getMemory() {
        return new PerformanceHolder(Runtime.getRuntime().maxMemory(), Runtime.getRuntime().freeMemory());
    }

    @APIMethod
    public static PerformanceHolder getDisk() {
        return new PerformanceHolder((new File(".")).getTotalSpace(), (new File(".")).getFreeSpace());
    }

    @APIMethod(operatesOn = PerformanceHolder.class)
    public static double getTotal(PerformanceHolder self) {
        return self.total / 1024.0 / 1024.0;
    }

    @APIMethod(operatesOn = PerformanceHolder.class)
    public static double getUsed(PerformanceHolder self) {
        return self.used / 1024.0 / 1024.0;
    }

    @APIMethod(operatesOn = PerformanceHolder.class)
    public static double getFree(PerformanceHolder self) {
        return self.free / 1024.0 / 1024.0;
    }

    public static class PerformanceHolder implements JsonSerializable {
        private double total;
        private double used;
        private double free;

        public PerformanceHolder(double total, double used, double free) {
            this.total = total;
            this.used = used;
            this.free = free;
        }

        public PerformanceHolder(double total, double free) {
            this(total, total - free, free);
        }

        @Override
        public JSONObject toJson(SerializerManager serializerManager) {
            JSONObject json = new JSONObject();
            json.put("total", total / 1024.0 / 1024.0);
            json.put("used", used / 1024.0 / 1024.0);
            json.put("free", free / 1024.0 / 1024.0);
            return json;
        }
    }
}
