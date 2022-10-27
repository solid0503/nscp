package com.uangel.ktiscp.nscp.common.transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionManagerTest {
	
	static MyTransactionManager myTransactionManager = new MyTransactionManager();
	static {
		myTransactionManager.init("MyTransactionManager's Timer");
	}
	
	@Test
	public void transactionTest() throws InterruptedException {
		
		Transaction tr = new Transaction();
		tr.putData("key", "value");
		
		String trKey = "tr-key:001";
		myTransactionManager.addTransaction(trKey, tr);
		
		Transaction gettedTr = myTransactionManager.getTransaction(trKey);
		Assertions.assertEquals("value", gettedTr.getData("key"));
		
		Transaction removedTr = myTransactionManager.removeTransaction(trKey);
		Assertions.assertEquals("value", removedTr.getData("key"));
	
		myTransactionManager.setTrTimeout(10);
		myTransactionManager.addTransaction(trKey, tr);
		
		synchronized (tr) {
			tr.wait(1000); // timeout에 의해 wait가 해제되는지 확인하여 판단
		}
		
	}
}

class MyTransactionManager extends TransactionManager {

	@Override
	public void handleTimeout(Transaction tr) {
		synchronized (tr) {
			tr.notifyAll();
		}
	}
	
}