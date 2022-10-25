package com.uangel.ktiscp.nscp.common.transaction;

import java.util.HashMap;

/**
 * 트랜잭션 클래스
 *
 */
public class Transaction {
	private String transactionId;
	private long requestTime;
	private TransactionTimerTask timerTask;
	private HashMap<String,Object> datas = new HashMap<String,Object>(); // 데이터를 맵에 저장하여 관리
	TransactionTimeoutHandler timeoutHandler; // timeroutHandler를 설정하면 해당 트랜젝션에 대해 특별하게 timeout처리를 할수 있다.
	long timeoutTime = 0; // timeout시간을 별도로 설정하면 해당 건만 별도로 처리한다.
	
	public Transaction() {
		requestTime = System.currentTimeMillis();
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTimeoutHandler(TransactionTimeoutHandler timeoutHandler) {
		this.timeoutHandler = timeoutHandler;
	}
	
	public void setTimeoutTime(long timeoutTime) {
		this.timeoutTime = timeoutTime;
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
