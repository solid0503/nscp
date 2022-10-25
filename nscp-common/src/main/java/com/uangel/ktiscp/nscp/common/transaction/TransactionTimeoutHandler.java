package com.uangel.ktiscp.nscp.common.transaction;

/**
 * 개별 트랜젝션에 대한 timeout을 정의하기 위한 인터페이스. 
 * 
 */
public interface TransactionTimeoutHandler {
	void handleTimeout(Transaction tr);
}
