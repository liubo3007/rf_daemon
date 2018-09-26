package com.kc.walle.station.engine.daemon.common;

public class Result<T> {
	private boolean success = true;

    private T data;

    private String errorCode;

    private String errorMsg;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setError(String errorCode,String errorMsg) {
		this.success = false;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
}
