package com.uangel.ktiscp.nscp.common.transaction;

import java.util.HashMap;

public class Transaction {
	private String transactionId;
	private long requestTime;
	private TransactionTimerTask timerTask;
	private HashMap<String,Object> datas = new HashMap<String,Object>();
	
	public Transaction() {
		requestTime = System.currentTimeMillis();
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void putData(String key, Object value) {
		datas.put(key, value);
	}
	
	public Object getData(String key) {
		return datas.get(key);
	}
	
	public long getRequestTime() {
		return requestTime;
	}
	
	public void setTimerTask(TransactionTimerTask timerTask) {
		this.timerTask = timerTask;
	}
	
	public long getDiffTime() {
		return System.currentTimeMillis() - this.requestTime;
	}
	
	public void cancelTimer() {
		if ( this.timerTask != null ) {
			this.timerTask.cancel();
			this.timerTask = null;
		}
	}
}
