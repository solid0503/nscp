package com.uangel.ktiscp.nscp.nscpib.sock;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.transaction.Transaction;
import com.uangel.ktiscp.nscp.common.transaction.TransactionManager;

/**
 * 메시지 트랜젝션 관리자
 * 기본적으로 BEP로 메시지를 던지고, 응답 처리 시 사용
 * 그리고 Ping/Pong 메시지 처리 할때도 사용
 */
@Component
public class NscpibTrManager extends TransactionManager {
	
	@Value("${tcp.server.tr-timeout:3000}")
	private long trTimeout;
	
	
	@PostConstruct
	public void init() {
		super.init("NscpibTrManager");
		this.setTrTimeout(trTimeout);
	}

	// BEP로 요청 후 timeout에 대한 것은 여기서 처리한다. Ping/Pong은 동작이 다르므로 해당 트랜젝션 생성하는 곳에서 정의한다.
	@Override
	public void handleTimeout(Transaction tr) {
		
	}
}

