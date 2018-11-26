package com.cosmo.litenet.anno;

import android.support.annotation.Nullable;
import android.util.Log;
import com.cosmo.litenet.net.BaseObserve;
import okhttp3.Headers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Observer;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cosmo.Chen
 */

//T
final class ServiceMethod<T>{
    String TAG = "ServiceMethod";

    boolean mIsPostMethod;// mHttpMethod;
    boolean mHasBody;
     Set<String> mRelativeUrlParamNames;
     String mBaseUrl;
     String mRelativeUrl;
     Headers mHeaders;

     Type[] mParameterTypes;
     Annotation[][] parameterAnnotationsArray;

    LiteNet mUtils;
    Method mMethod;

    static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
    static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

    HashMap<String,String> mParams = new HashMap<>();
    HashMap<String,String> mHeader = new HashMap<>();
    Observer mObserver;

    Type mObserverableType;//用于Gson解析使用

    public ServiceMethod(LiteNet utils,Method method) {
        mUtils = utils;
        mMethod = method;
        if(method != null){
            Annotation[] methodAnnotations = method.getAnnotations();
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }
            parameterAnnotationsArray = method.getParameterAnnotations();
            mParameterTypes = method.getGenericParameterTypes();
            Type returnType = method.getGenericReturnType();
            mObserverableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        }
    }

    BaseObserve<T> request(String baseUrl, @Nullable Object[] args){
        parseParams(args);
        BaseObserve baseObserveHash = new BaseObserve<T>(mObserver,mObserverableType,mHeader,mParams,baseUrl+mRelativeUrl,true);
        return baseObserveHash;
    }

    void parseParams(@Nullable Object[] args){
        if(parameterAnnotationsArray == null)
            return;

        mParams.clear();
        mHeader.clear();
        int parameterCount = parameterAnnotationsArray.length;
        for (int p = 0; p < parameterCount; p++) {
            Type type = mParameterTypes[p];
            if(args[p] instanceof Observer){
                mObserver = (Observer) args[p];
            }
            Annotation[] parameterAnnotations = parameterAnnotationsArray[p];

            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof Field) {
                    Field field = (Field) annotation;
                    mParams.put(field.value(), (String) args[p]);
                    Log.e(TAG, "value:" + field.value() + " toString:" + field.toString() + " v1:" + annotation.annotationType().getSimpleName());
                    Log.e(TAG, "getCanonicalName:" + annotation.annotationType().getCanonicalName());
                    Class<? extends Annotation> type1 = annotation.annotationType();
                    Log.e(TAG, "type1:" + type1);
                } else if (annotation instanceof Header) {
                    Header header = (Header) annotation;
                    mHeader.put(header.value(), (String) args[p]);
                    Log.e(TAG, "value:" + header.value());
                }
//                else {
//                    if(args[p] instanceof Observer){
//                        mObserver = (Observer) args[p];
//                    }
//                }
                Log.e("", "parameterType:" + type + " annotation:" + annotation);
            }
        }
    }


    private void parseMethodAnnotation(Annotation annotation) {
        if (annotation instanceof GET) {
            parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
        } else if (annotation instanceof POST) {
            parseHttpMethodAndPath("POST", ((POST) annotation).value(), true);
        }
    }

    private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
        mHasBody = hasBody;
        mIsPostMethod = "POST".equals(httpMethod);

        if (value == null || value.isEmpty()) {
            return;
        }

        // Get the relative URL path and existing query string, if present.
        int question = value.indexOf('?');
        if (question != -1 && question < value.length() - 1) {
            // Ensure the query string does not have any named parameters.
            String queryParams = value.substring(question + 1);
            Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
            if (queryParamMatcher.find()) {
                Log.e(TAG,"URL query string \"%s\" must not have replace block. "
                        + "For dynamic query parameters use @Query."+queryParams);
            }
        }

        mRelativeUrl = value;
        mRelativeUrlParamNames = parsePathParameters(value);
    }

     Set<String> parsePathParameters(String path) {
        Matcher m = PARAM_URL_REGEX.matcher(path);
        Set<String> patterns = new LinkedHashSet<>();
        while (m.find()) {
            patterns.add(m.group(1));
        }
        return patterns;
    }

    static Type getParameterUpperBound(int index, ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (index < 0 || index >= types.length) {
            throw new IllegalArgumentException(
                    "Index " + index + " not in range [0," + types.length + ") for " + type);
        }
        Type paramType = types[index];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }
}
