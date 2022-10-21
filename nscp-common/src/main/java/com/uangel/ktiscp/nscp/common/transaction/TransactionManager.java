package com.uangel.ktiscp.nscp.common.transaction;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import lombok.extern.slf4j.Slf4j;

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
		trTimer.schedule(task, trTimeout);
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
				trManager.handleTimeout(removedTr);
			}
		}
		catch(Exception err) {
			log.error("Error in TransactionTimerTask", err);
		}
	}
}
