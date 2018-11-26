package com.cosmo.litenet.net;

import java.util.Observable;

public class ObserveData extends Observable {
	public static final int STATE_NET_TIMEOUT = -1; // 连接超时
	public static final int STATE_NET_DISCONN = -2; // 网络未连接
	public static final int STATE_OK = 1; // 获取数据成功
	public static final int STATE_REFRESH = 2; // 更新
	public static final int STATE_NET_ERR = 3; // 网络未知错误

	private int state = 0;
	private int what;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getWhat() {
		return what;
	}

	public void setWhat(int what) {
		this.what = what;
	}

}
