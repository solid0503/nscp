package com.uangel.ktiscp.nscp.common.bepsock;

import java.nio.charset.Charset;

import com.uangel.ktiscp.nscp.common.json.JsonType;

import lombok.Getter;
import lombok.Setter;

@Getter
public class BepMessage {
	public static final int HEADER_SIZE = 113;
	
	public static final int LENGTH_OF_SUB_SYSTEM_ID  = 20;
	public static final int LENGTH_OF_MESSAGE_TYPE   = 10;
	public static final int LENGTH_OF_REQUEST_TYPE   = 8;
	public static final int LENGTH_OF_COMMAND        = 20;
	public static final int LENGTH_OF_TRANSACTION_ID = 20;
	public static final int LENGTH_OF_SERVICE_ID     = 10;
	public static final int LENGTH_OF_ROUTING_KEY    = 20;
	public static final int LENGTH_OF_BODY_LENGTH    = 5;
	public static final Charset CHARSET = Charset.forName("utf-8");
	
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

	public void setSubSystemId(String subSystemId) {
		if ( subSystemId != null && subSystemId.length() > LENGTH_OF_SUB_SYSTEM_ID ) {
			throw new IllegalArgumentException(
					String.format("SUB_SYSTEM_ID length over. max_length=%d, input_length=%d", 
							LENGTH_OF_SUB_SYSTEM_ID, subSystemId.length()));
		}
		this.subSystemId = subSystemId;
	}

	public void setMessageType(String messageType) {
		if ( messageType != null && messageType.length() > LENGTH_OF_MESSAGE_TYPE ) {
			throw new IllegalArgumentException(
					String.format("MESSAGE_TYPE length over. max_length=%d, input_length=%d", 
							LENGTH_OF_MESSAGE_TYPE, messageType.length()));
		}
		this.messageType = messageType;
	}

	public void setRequestType(String requestType) {
		if ( requestType != null && requestType.length() > LENGTH_OF_REQUEST_TYPE ) {
			throw new IllegalArgumentException(
					String.format("REQUEST_TYPE length over. max_length=%d, input_length=%d", 
							LENGTH_OF_REQUEST_TYPE, requestType.length()));
		}
		this.requestType = requestType;
	}

	public void setCommand(String command) {
		if ( command != null && command.length() > LENGTH_OF_COMMAND ) {
			throw new IllegalArgumentException(
					String.format("COMMAND length over. max_length=%d, input_length=%d", 
							LENGTH_OF_COMMAND, command.length()));
		}
		this.command = command;
	}

	public void setTransactionId(String transactionId) {
		if ( transactionId != null && transactionId.length() > LENGTH_OF_TRANSACTION_ID ) {
			throw new IllegalArgumentException(
					String.format("TRANSACTION_ID length over. max_length=%d, input_length=%d", 
							LENGTH_OF_TRANSACTION_ID, transactionId.length()));
		}
		this.transactionId = transactionId;
	}

	public void setServiceId(String serviceId) {
		if ( serviceId != null && serviceId.length() > LENGTH_OF_SERVICE_ID ) {
			throw new IllegalArgumentException(
					String.format("SERVICE_ID length over. max_length=%d, input_length=%d", 
							LENGTH_OF_SERVICE_ID, serviceId.length()));
		}
		this.serviceId = serviceId;
	}

	public void setRoutingKey(String routingKey) {
		if ( routingKey != null && routingKey.length() > LENGTH_OF_ROUTING_KEY ) {
			throw new IllegalArgumentException(
					String.format("ROUTING_KEY length over. max_length=%d, input_length=%d", 
							LENGTH_OF_ROUTING_KEY, routingKey.length()));
		}
		this.routingKey = routingKey;
	}

	public void setBodyLength(String bodyLength) {
		if ( bodyLength != null && bodyLength.length() > LENGTH_OF_BODY_LENGTH ) {
			throw new IllegalArgumentException(
					String.format("BODY_LENGTH length over. max_length=%d, input_length=%d", 
							LENGTH_OF_BODY_LENGTH, bodyLength.length()));
		}
		this.bodyLength = bodyLength;
	}

	public void setJson(JsonType json) {
		this.json = json;
	}
}
