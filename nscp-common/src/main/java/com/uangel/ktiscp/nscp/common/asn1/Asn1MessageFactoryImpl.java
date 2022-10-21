package com.uangel.ktiscp.nscp.common.asn1;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
public class Asn1MessageFactoryImpl implements Asn1MessageFactory {

	MappingInfo recvMsgMappings[] = {
			new MappingInfo(0x06, 0x01, true, "CS-Roaming-Noti-Invoke"),
			new MappingInfo(0x06, 0x02, true, "PS-Roaming-Noti-Invoke"),
			new MappingInfo(0x06, 0x03, true, "Cancellation-Noti-Invoke"),
			new MappingInfo(0x06, 0x04, true, "EPS-Roaming-Noti"),
//			new MappingInfo(0x06, 0x04, "Roaming-Information-Query-Return"),
	};
	
	Map<String,String> recvMsgNameMap;  // Key = serviceId:opCode, Value = OperationName
	Map<String,Integer> nameToServiceId;
	Map<String,Integer> nameToOpcode;
	
	@PostConstruct
	public void init() {
		recvMsgNameMap = new HashMap<String,String>();
		nameToServiceId = new HashMap<String, Integer>();
		nameToOpcode = new HashMap<String,Integer>();
		for (MappingInfo info:recvMsgMappings) {
			recvMsgNameMap.put(info.serviceId + ":" + info.opCode, info.operationName);
			nameToServiceId.put(info.operationName, info.serviceId);
			nameToOpcode.put(info.operationName, info.opCode);
		}
	}

	@Override
	public Asn1Message newMessage(String name) {
		return new Asn1MessageImpl(name);
	}

	@Override
	public Asn1Message newRecvMessage(int serviceId, int opCode) {
		String name = this.getOperationName(serviceId, opCode);
		
		if (name == null ) {
			throw new IllegalArgumentException(
					String.format("Invalid serviceId and opCode. serviceId=%s, opCode=%s", serviceId, opCode));
		}
		
		return new Asn1MessageImpl(name);
	}

	// serviceId + opcode => operationName에 대한 매핑 정보를 정의하기 위해 선언한 클래스
	@AllArgsConstructor
	class MappingInfo {
		int serviceId;
		int opCode;
		boolean recvFlag; // NSCP 기준으로 수신 메시지 여부
		String operationName;
	}

	@Override
	public Integer getOpcodeByName(String name) {
		
		Integer result = nameToOpcode.get(name);
		if ( result == null ) {
			throw new RuntimeException("Not found operationName. name=" + name);
		}
		return result;
	}

	@Override
	public Integer getServiceIdByName(String name) {
		Integer result = nameToServiceId.get(name);
		if ( result == null ) {
			throw new RuntimeException("Not found operationName. name=" + name);
		}
		return result;
	}

	@Override
	public String getOperationName(int serviceId, int opCode) {
		return recvMsgNameMap.get(serviceId+":"+opCode);
	}
}
