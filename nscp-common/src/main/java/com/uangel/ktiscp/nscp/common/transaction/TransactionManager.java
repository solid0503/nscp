package com.uangel.ktiscp.nscp.common.transaction;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import lombok.extern.slf4j.Slf4j;

/**
 * 트랜젝션 관리 클래스로 사용하는 곳에서 상속하여 사용. 
 * handleTimeout을 구현하여 이 클래스에 의해 관리되는 모든 트랜젝션 timeout 처리를 구현한다. 
 * 단, 개별 트랜젝션에 대한 timeoutHandler가 설정되면 handleTimeout은 호출되지 않는다. 
 */
@Slf4j
public abstract class TransactionManager {
	private HashMap<String, Transaction> transactions = new HashMap<String, Transaction>();
	private long trTimeout = 3000;
	private Timer trTimer = null;
	
	public void init(String timerThreadName) {
		trTimer = new Timer(timerThreadName);
	}
	
	public void setTrTimeout(long trTimeout) {
		this.trTimeout = trTimeout;
	}
	
	public long getTrTimeout() {
		return this.trTimeout;
	}
	
	public void addTransaction(String key, Transaction tr) {
		log.debug("addTransaction. key={}", key);
		tr.setTransactionId(key);
		synchronized (this) {
			transactions.put(key, tr);
		}
		TransactionTimerTask task = new TransactionTimerTask(tr, this);
		if ( tr.timeoutTime > 0 ) {
			trTimer.schedule(task, tr.timeoutTime);
		} else {
			trTimer.schedule(task, trTimeout);
		}
		tr.setTimerTask(task);
	}
	
	public Transaction removeTransaction(String key) {
		log.debug("removeTransaction. key={}", key);
		Transaction tr = null;
		synchronized (this) {
			tr = transactions.remove(key);
		}
		if ( tr != null ) {
			tr.cancelTimer();
		}
		return tr;
	}
	
	public Transaction getTransaction(String key) {
		return transactions.get(key);
	}
	
	public abstract void handleTimeout(Transaction tr);
}

@Slf4j
class TransactionTimerTask extends TimerTask {
	private Transaction tr;
	private TransactionManager trManager;
	
	public TransactionTimerTask(Transaction tr, TransactionManager trManager) {
		this.tr = tr;
		this.trManager = trManager;
	}
	
	public void run() {
		try {
			tr.setTimerTask(null);
			Transaction removedTr = trManager.removeTransaction(tr.getTransactionId());
			if ( removedTr != null ) {
				log.error("TransactionTimeout!! key={}", removedTr.getTransactionId());
				if ( removedTr.timeoutHandler != null ) {
					removedTr.timeoutHandler.handleTimeout(removedTr);
				} else {
					trManager.handleTimeout(removedTr);
				}
			}
		}
		catch(Exception err) {
			log.error("Error in TransactionTimerTask", err);
		}
	}
}
