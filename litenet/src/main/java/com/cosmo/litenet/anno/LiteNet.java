package com.cosmo.litenet.anno;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * Cosmo.Chen
 */
public final class LiteNet {
    static String TAG = "LiteNet";

    static LiteNet INSTANCE;
    String mBaseUrl;

    Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();

    public static LiteNet getInstance(){
        if(INSTANCE == null)
            INSTANCE = new LiteNet();
        return INSTANCE;
    }



    static <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        // Prevent API interfaces from extending other interfaces. This not only avoids a bug in
        // Android (http://b.android.com/58753) but it forces composition of API declarations which is
        // the recommended pattern.
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }

    public  <T> T create(@NonNull final String baseUrl, final Class<T> service) {
        mBaseUrl = baseUrl;
        validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
                new InvocationHandler() {
//                    private final Platform platform = Platform.get();

                    @Override public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                            throws Throwable {

                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }

                        ServiceMethod<Object> serviceMethod =
                                (ServiceMethod<Object>) loadServiceMethod(method);
                        return serviceMethod.request(mBaseUrl,args);

                    }
                });
    }

    static Class<?> getRawType(Type type) {

        if (type instanceof Class<?>) {
            // Type is a normal class.
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) throw new IllegalArgumentException();
            return (Class<?>) rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }

        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
    }





    ServiceMethod<?> loadServiceMethod(Method method) {
        ServiceMethod<?> result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod(this,method);
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }


//    public static void test(){
//        NetAnno anno = LiteNet.getInstance().create(NetAnno.class);
//    }
}
