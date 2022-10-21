package com.uangel.ktiscp.nscp.nscpsim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.transaction.Transaction;
import com.uangel.ktiscp.nscp.common.transaction.TransactionManager;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SimTransactionManager extends TransactionManager {
	@Autowired
	NscpSimCounter counter;

	@Value("${tcp.client.trTimeout:3000}")
	long timeout;
	
	@PostConstruct
	public void init() {
		super.init("SimTransactionManager");
		super.setTrTimeout(timeout);
	}
	
	@Override
	public void handleTimeout(Transaction tr) {
		counter.incTimeoutCall();
	}
}
