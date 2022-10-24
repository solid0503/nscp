package com.uangel.ktiscp.nscp.common.transaction;

public interface TransactionTimeoutHandler {
	void handleTimeout(Transaction tr);
}
