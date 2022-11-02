package com.uangel.ktiscp.nscp.common.bepsock;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.transaction.Transaction;
import com.uangel.ktiscp.nscp.common.transaction.TransactionManager;

@Component
public class CallbackTrManager extends TransactionManager {
	
	@PostConstruct
	public void init() {
		super.init("BepClientCallbackTrManager");
	}
	
	@Override
	public void handleTimeout(Transaction tr) {
		RecvTimeoutCallback cb = (RecvTimeoutCallback)tr.getData("timeoutCallback");
		cb.timeout();
	}
}
