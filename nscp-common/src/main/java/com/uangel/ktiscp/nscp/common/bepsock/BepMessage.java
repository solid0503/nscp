package com.uangel.ktiscp.nscp.common.bepsock;

import com.uangel.ktiscp.nscp.common.json.JsonType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BepMessage {
	public static final int HEADER_SIZE = 113;
	
	String subSystemId;   // 20	  연동 프로세스 구분자
	String messageType;   // 10	  메시지 유형(ALIVE: client의 alive check 요청, COMMAND: 일반 메시지)
	String requestType;   // 8    REQUEST: 요청 메시지, RESPONSE: 응답 메시지
	String command;       // 20   메시지 command 설정, 서비스별 정의하여 사용
	String transactionId; // 20   트랜잭셕 ID, 메시지를 생성하는 프로세스에서 unique한 값을 할당하여 사용
	String serviceId;     // 10   서비스 구분자
	String routingKey;    // 20   메시지의 목적지를 판단할 routing key, 메시지를 생성하는 프로세스가 라우팅할때 사용할 key 값을 설정하여 전송
	String bodyLength;    // 5    Body의 length

	JsonType json;
	
	public String getTraceString() {
		StringBuilder sb = new StringBuilder(1024);
		
		sb.append("\n\t\t").append("subSystemId   : ").append(subSystemId);
		sb.append("\n\t\t").append("messageType   : ").append(messageType);
		sb.append("\n\t\t").append("requestType   : ").append(requestType);
		sb.append("\n\t\t").append("command       : ").append(command);
		sb.append("\n\t\t").append("transactionId : ").append(transactionId);
		sb.append("\n\t\t").append("serviceId     : ").append(serviceId);
		sb.append("\n\t\t").append("routingKey    : ").append(routingKey); 
		if ( json != null ) {
			sb.append("\n\t\t").append("Json : ").append(json.toString());
		}
		return sb.toString();
	}
	
	public BepMessage getResponse() {
		BepMessage res = new BepMessage();
		res.subSystemId = this.subSystemId;
		res.messageType = this.messageType;
		res.requestType = this.requestType;
		res.command = this.command;
		res.transactionId = this.transactionId;
		res.serviceId = this.serviceId;
		res.routingKey = this.routingKey;
		return res;
	}
}
