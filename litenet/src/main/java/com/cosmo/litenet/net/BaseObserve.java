package com.cosmo.litenet.net;

import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Observer;

public class BaseObserve<T> extends ObserveData{
	protected String TAG = this.getClass().getName();
	public HashMap<String,String> mParams;
	public HashMap<String,String> mHeader;
	public T data;
	public String mExceptionMsg;
	public String mUrl;
	Type mGsonType;

	boolean mIsPost;

	public BaseObserve(Observer observer, Type gsonType, HashMap<String,String> header, HashMap<String,String> params, String url, boolean isPost){
		if(observer != null)
			addObserver(observer);
		mParams = params;
		mHeader = header;
		mIsPost = isPost;
		mUrl = url;
		mGsonType = gsonType;
		request();
	}

	protected void request(){

		BaseCallback callback = new BaseCallback<T>(){

			@Override
			public void onError(Call call, Exception e, int id) {
				setState(STATE_NET_ERR);
				setChanged();
				notifyObservers();
//				Toast.makeText(App.Companion.getContext(),mExceptionMsg,Toast.LENGTH_LONG).show();
			}

			@Override
			public void onResponse(T response, int id) {
				data = response;
				if (data != null) {
					setState(STATE_OK);
				} else {
//					Toast.makeText(App.Companion.getContext(),mExceptionMsg,Toast.LENGTH_LONG).show();
					setState(STATE_NET_TIMEOUT);
				}
				setChanged();
				notifyObservers();
			}

			@Override
			public T parsNetworkData(String source, int id) throws Exception{
				T data = null;
				try {
					data = GsonUtil.getInstance().fromJson(source, mGsonType);
				}catch (Exception e){
					mExceptionMsg = e.getMessage();
				}
				return data;
			}
		};


		try {
			if(mIsPost){
				OkHttpUtils.post()
						.url(mUrl )
						.params(mParams)
						.headers(mHeader)
						.tag(TAG)
						.build()//
						.connTimeOut(20000)
						.readTimeOut(20000)
						.writeTimeOut(20000)
						.execute(callback);
			}
			else{
				OkHttpUtils.get()
						.url(mUrl )
						.params(mParams)
						.headers(mHeader)
						.tag(TAG)
						.build()//
						.connTimeOut(20000)
						.readTimeOut(20000)
						.writeTimeOut(20000)
						.execute(callback);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void clearAll(){
		OkHttpUtils.getInstance().cancelTag(TAG);
	}

}
