package com.cosmo.litenet.net;


import com.zhy.http.okhttp.callback.Callback;
import okhttp3.Response;

/**create by Cg 20170225*/
public abstract class BaseCallback<T> extends Callback<T> {

    public String decode(String source) throws Exception {
        //FIXME 添加解密模块
        return source;
    }

    public abstract T parsNetworkData(String source, int id) throws Exception;
//    public abstract T onResponseData(String source,int id) throws Exception;

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String string = "";
        try {
            string = decode(response.body().string());
        }
        catch (OutOfMemoryError e){
            e.printStackTrace();
        }
//        if(COM.DEBUG){
//            LogUtil.d(JsonUtil.formatJson(JsonUtil.decodeUnicode(string)));
//            LogUtils.log2file("parseNetworkResponse",string);
////            Log.e("parseNetworkResponse",string);
//        }
        return parsNetworkData(string,id);
    }

}