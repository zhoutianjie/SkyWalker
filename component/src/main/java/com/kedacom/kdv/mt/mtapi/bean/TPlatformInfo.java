package com.kedacom.kdv.mt.mtapi.bean;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kedacom.kdv.mt.mtapi.emun.EmResourceType;

import java.lang.reflect.Type;

/**
 * Created by zhoutianjie on 2018/12/21.
 */

public class TPlatformInfo extends TMtApi{
    public String achVersion;
    public long dwIP;
    public EmResourceType emResourceType;

    public static GsonBuilder createDeserializerGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EmResourceType.class, new JsonDeserializer<EmResourceType>() {

            @Override
            public EmResourceType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                try {
                    if (json.getAsInt() < EmResourceType.values().length) {
                        return EmResourceType.values()[json.getAsInt()];
                    }
                } catch (Exception e) {
                }

                return null;
            }
        });

        return gsonBuilder;
    }

    public static GsonBuilder createSerializationGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EmResourceType.class, new JsonSerializer<EmResourceType>() {

            @Override
            public JsonElement serialize(EmResourceType aliasType, Type arg1, JsonSerializationContext arg2) {
                return new JsonPrimitive(aliasType.ordinal());
            }
        });

        return gsonBuilder;
    }

    /**
     * @see com.kedacom.kdv.mt.mtapi.bean.TMtApi#toJson()
     */
    @Override
    public String toJson() {
        return createSerializationGsonBuilder().create().toJson(this);
    }

    /**
     * @see com.kedacom.kdv.mt.mtapi.bean.TMtApi#fromJson(java.lang.String)
     */
    @Override
    public TPlatformInfo fromJson(String gson) {
        return createDeserializerGsonBuilder().create().fromJson(gson, TPlatformInfo.class);
    }
}
