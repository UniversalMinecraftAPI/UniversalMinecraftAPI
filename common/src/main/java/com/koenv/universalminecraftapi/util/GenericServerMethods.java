package com.koenv.universalminecraftapi.util;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.http.rest.RestOperation;
import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.methods.APIMethod;
import com.koenv.universalminecraftapi.methods.APINamespace;
import com.koenv.universalminecraftapi.permissions.RequiresPermission;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

import java.io.File;

@APINamespace("server")
public class GenericServerMethods {
    @APIMethod
    @RestResource("server/memory")
    public static PerformanceHolder getMemory() {
        return new PerformanceHolder(Runtime.getRuntime().maxMemory(), Runtime.getRuntime().freeMemory());
    }

    @APIMethod
    @RestResource("server/disk")
    public static PerformanceHolder getDisk() {
        return new PerformanceHolder((new File(".")).getTotalSpace(), (new File(".")).getFreeSpace());
    }

    @APIMethod(operatesOn = PerformanceHolder.class)
    @RestOperation(PerformanceHolder.class)
    @RequiresPermission("server.performance.total")
    public static double getTotal(PerformanceHolder self) {
        return self.total / 1024.0 / 1024.0;
    }

    @APIMethod(operatesOn = PerformanceHolder.class)
    @RestOperation(PerformanceHolder.class)
    @RequiresPermission("server.performance.used")
    public static double getUsed(PerformanceHolder self) {
        return self.used / 1024.0 / 1024.0;
    }

    @APIMethod(operatesOn = PerformanceHolder.class)
    @RestOperation(PerformanceHolder.class)
    @RequiresPermission("server.performance.free")
    public static double getFree(PerformanceHolder self) {
        return self.free / 1024.0 / 1024.0;
    }

    public static class PerformanceHolder implements JsonSerializable {
        private double total;
        private double used;
        private double free;

        PerformanceHolder(double total, double used, double free) {
            this.total = total;
            this.used = used;
            this.free = free;
        }

        PerformanceHolder(double total, double free) {
            this(total, total - free, free);
        }

        @Override
        public void toJson(JSONWriter writer, SerializerManager serializerManager) {
            writer.object()
                    .key("total").value(total / 1024.0 / 1024.0)
                    .key("used").value(used / 1024.0 / 1024.0)
                    .key("free").value(free / 1024.0 / 1024.0)
                    .endObject();
        }
    }
}
