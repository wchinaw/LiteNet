package com.cosmo.litenet.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by cg on 2016/11/9.
 */

public class GsonUtil {
    private static GsonUtil mInstance = null;
    private Gson mGson = null;

    public GsonUtil(){
        mGson = new GsonBuilder().serializeNulls().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
    }

    public static GsonUtil getInstance(){
        if(mInstance == null)
            mInstance = new GsonUtil();
        return mInstance;
    };

    public <T> T fromJson(String gsonString, Type type) {
        T t = mGson.fromJson(gsonString, type);
        return t;
    }

    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public <T> T fromJson(String gsonString, Class<T> cls) {
        T t = mGson.fromJson(gsonString, cls);
        return t;
//        if (mGson != null) {
//            try {
//                t = mGson.fromJson(gsonString, cls);
//            }
//            catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//        return t;
    }



    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }


    public <T> T fromJsonX(String gsonString, Class<T> cls) throws Exception {
        T t = null;
        if (mGson != null) {
            t = mGson.fromJson(gsonString, cls);
        }
        return t;
    }

}
