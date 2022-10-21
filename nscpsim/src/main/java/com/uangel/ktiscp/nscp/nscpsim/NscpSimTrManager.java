package com.uangel.ktiscp.nscp.nscpsim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.transaction.Transaction;
import com.uangel.ktiscp.nscp.common.transaction.TransactionManager;

@Component
public class NscpSimTrManager extends TransactionManager {
	@Autowired
	NscpSimCounter counter;
	
	@PostConstruct
	public void init() {
		super.init("NscpSimTrManager");
	}
	
	public void handleTimeout(Transaction tr) {
		counter.incTimeoutCall();
	}
}
