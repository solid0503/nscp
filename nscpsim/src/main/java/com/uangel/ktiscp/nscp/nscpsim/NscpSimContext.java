package com.uangel.ktiscp.nscp.nscpsim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.uangel.ktiscp.nscp.common.asn1.Asn1MessageFactory;
import com.uangel.ktiscp.nscp.common.sock.MessageId;
import com.uangel.ktiscp.nscp.common.sock.MessageType;
import com.uangel.ktiscp.nscp.common.sock.NscpMessage;
import com.uangel.ktiscp.nscp.common.sock.NscpMessageFactory;
import com.uangel.ktiscp.nscp.common.sock.ServiceId;
import com.uangel.utms.uTMS_Util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NscpSimContext {
	@Autowired
	NscpMessageFactory nscpMessageFactory;
	
	@Autowired
	Asn1MessageFactory asn1MessageFactory;
	
	private String mode;
	private int cps;
	
	private int messageVersion = 1;
	
	private int minSeq;
	private int maxSeq;
	private Integer nowSeq;
	private long startMdn;
	private long endMdn;
	private Long nowMdn;
	private long duration;
	private String responseTimes;
	private boolean bPrefMode = false;
	private int sysQueryRes = 1;
	private int connRequest = 1;
	private int connectionCount = 1;
	private Object syncObj = new Object();
	
	
	public int nextSeq() {
		int seq;
		synchronized(syncObj) {
			seq = nowSeq.intValue();
			nowSeq++;
			if ( nowSeq > this.maxSeq ) {
				nowSeq = this.minSeq;
			}
		}
		return seq;
		
	}
	
	public String nextMdn() {
		long mdn;
		synchronized(nowMdn) {
			mdn = nowMdn.longValue();
			nowMdn++;
			if ( nowMdn > this.endMdn ) {
				nowMdn = this.startMdn;
			}
		}
		return StringUtil.sprintf("0%d", mdn);
	}
	
	public void loadConfig(String xmlFile) {
		try {
			XPathReader reader = new XPathReader(xmlFile, false);
			mode = reader.read("nscpsim/config/mode", true);
			cps = Integer.parseInt(reader.read("nscpsim/config/cps", true));
			duration = Long.parseLong(reader.read("nscpsim/config/duration", true));
			minSeq = Integer.parseInt(reader.read("nscpsim/config/min_seq", true));
			maxSeq = Integer.parseInt(reader.read("nscpsim/config/max_seq", true));
			startMdn = Long.parseLong(reader.read("nscpsim/config/start_mdn", true));
			endMdn = Long.parseLong(reader.read("nscpsim/config/end_mdn", true));
			responseTimes = reader.read("nscpsim/config/response_times", true);
			String strSysQueryRes = reader.read("nscpsim/config/sys_query_res", true);
			if ( strSysQueryRes != null && strSysQueryRes.length() > 0 ) {
				this.sysQueryRes = Integer.parseInt(strSysQueryRes);
			}
			
			String strConnReq = reader.read("nscpsim/config/conn_req", true);
			if ( strConnReq != null && strConnReq.length() > 0 ) {
				this.connRequest = Integer.parseInt(strConnReq);
			}
			
			String strConnectionCount = reader.read("nscpsim/config/connection_count", true);
			if ( strConnectionCount != null &&  strConnectionCount.length() != 0 ) {
				this.connectionCount = Integer.parseInt(strConnectionCount);
				log.error("connectionCount={}", connectionCount);
			}
			
			nowSeq = minSeq;
			nowMdn = startMdn;
			
			if ( cps <= 1 ) {
				bPrefMode = false;
			}
			
			SimScenarioTemplate scenarioTemplate = NscpsimApplication.getBean(SimScenarioTemplate.class);
			
			NodeList nodeList = reader.readNodeList("nscpsim/traffic").item(0).getChildNodes();
			for ( int i = 0; i < nodeList.getLength(); i++ ) {
	        	Node node = nodeList.item(i);
	        	if ( node.getNodeName().equals("#text") ) continue;
	        	if ( node.getNodeName().equals("#comment") ) continue;
	        	log.debug("XML parsing. name={}", node.getNodeName());
	        	SimAction action = new SimAction();
	        	action.actionName = node.getNodeName();
	        	if ( action.actionName.equals("send") || action.actionName.equals("recv") ) {
		        	NodeList childNodeList = node.getChildNodes();
		        	for ( int j = 0; j < childNodeList.getLength(); j++ ) {
			        	Node node2 = childNodeList.item(j);
			        	if ( node2.getNodeName().equals("#text") ) continue;
			        	if ( node2.getNodeName().equals("#comment") ) continue;
			        	log.debug("XML parsing.   name={}, data={}", node2.getNodeName(), node2.getTextContent().trim());
			        	if ( node2.getNodeName().equals("operation") ) {
			        		action.opName = node2.getTextContent().trim();
			        	} else {
			        		action.addParameter(node2.getNodeName().trim(), node2.getTextContent().trim().replaceAll("_", " "));
			        	}
		        	}
	        	} else if ( action.actionName.equals("wait") ) {
	        		action.waitTime = Long.parseLong(node.getTextContent().trim());
	        	} else {
	        		continue;
	        	}
	        	scenarioTemplate.addAction(action);
	        }
			
		} catch (Exception e) {
			log.error("config load failure.", e);
			System.exit(1);
		}
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getCps() {
		return cps;
	}

	public void setCps(int cps) {
		this.cps = cps;
	}

	public long getMinSeq() {
		return minSeq;
	}

	public void setMinSeq(int minSeq) {
		this.minSeq = minSeq;
	}

	public int getMaxSeq() {
		return maxSeq;
	}

	public void setMaxSeq(int maxSeq) {
		this.maxSeq = maxSeq;
	}

	public long getStartMdn() {
		return startMdn;
	}

	public void setStartMdn(long startMdn) {
		this.startMdn = startMdn;
	}

	public long getEndMdn() {
		return endMdn;
	}

	public void setEndMdn(long endMdn) {
		this.endMdn = endMdn;
	}
	public NscpMessage newMessage(String operationName) {
		
		NscpMessage message = nscpMessageFactory.createMessage();
		message.setMessageVersion(messageVersion);
		message.setLinkedId((short)0);
		message.setMessageId((short)MessageId.SERVICE_REQUEST.getValue());
		message.setServiceId((short)ServiceId.ROAMING_LOCATION_INFO.getValue());
		message.setMessageType(MessageType.TERMINATION.getValue());
		
		Integer serviceId = asn1MessageFactory.getServiceIdByName(operationName);
		message.setServiceId(serviceId);
		Integer opcode = asn1MessageFactory.getOpcodeByName(operationName);
		message.setOperationCode(opcode);
		message.setOTID(this.nextSeq());
		
		message.setAsn1Message(asn1MessageFactory.newMessage(operationName));
		
		return message;
	}
	
	public NscpMessage newConnReqMessage() {
		NscpMessage message = nscpMessageFactory.createMessage();
		message.setMessageVersion(messageVersion);
		message.setLinkedId((short)0);
		message.setMessageId((short)MessageId.CONNECTION_REQUEST.getValue());
		message.setMessageType(MessageType.TERMINATION.getValue());
		message.setOTID(1);
		return message;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public String getResponseTimes() {
		return responseTimes;
	}
	
	public boolean isPrefMode() {
		return bPrefMode;
	}
	
	public int getSysQueryRes() {
		return sysQueryRes;
	}
	
	public int getConnectionCount() {
		return this.connectionCount;
	}
	
	public int getConnRequest() {
		return connRequest;
	}
}
