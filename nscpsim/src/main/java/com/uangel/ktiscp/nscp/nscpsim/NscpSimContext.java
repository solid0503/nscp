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
	
	private long minSeq;
	private long maxSeq;
	private Long nowSeq;
	private long minSessionId;
	private long maxSessionId;
	private Long nowSessionId;
	private long startMdn;
	private long endMdn;
	private Long nowMdn;
	private long duration;
	private String responseTimes;
	private boolean bPrefMode = true;
	private int sysQueryRes = 1;
	private String callStatus = "1";
	private int connectionCount = 1;
	private Object syncObj = new Object();
	
	public long nextSeq() {
		long seq;
		synchronized(syncObj) {
			seq = nowSeq.longValue();
			nowSeq++;
			if ( nowSeq > this.maxSeq ) {
				nowSeq = this.minSeq;
			}
		}
		return seq;
		
	}
	
	public String nextSessionId() {
		long sessionId;
		synchronized(nowSessionId) {
			sessionId = nowSessionId.longValue();
			nowSessionId++;
			if ( nowSessionId > this.maxSessionId ) {
				nowSessionId = this.minSessionId;
			}
		}
		return StringUtil.sprintf("%d", sessionId);
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
			minSeq = Long.parseLong(reader.read("nscpsim/config/min_seq", true));
			maxSeq = Long.parseLong(reader.read("nscpsim/config/max_seq", true));
			minSessionId = Long.parseLong(reader.read("nscpsim/config/min_session_id", true));
			maxSessionId = Long.parseLong(reader.read("nscpsim/config/max_session_id", true));
			startMdn = Long.parseLong(reader.read("nscpsim/config/start_mdn", true));
			endMdn = Long.parseLong(reader.read("nscpsim/config/end_mdn", true));
			responseTimes = reader.read("nscpsim/config/response_times", true);
			String strSysQueryRes = reader.read("nscpsim/config/sys_query_res", true);
			if ( strSysQueryRes != null && strSysQueryRes.length() > 0 ) {
				this.sysQueryRes = Integer.parseInt(strSysQueryRes);
			}
			
			this.callStatus = reader.read("nscpsim/config/csr_call_status", true);
			if ( callStatus == null || callStatus.length() == 0) {
				callStatus = "1";
			}
			
			String strConnectionCount = reader.read("nscpsim/config/connection_count", true);
			if ( strConnectionCount != null &&  strConnectionCount.length() != 0 ) {
				this.connectionCount = Integer.parseInt(strConnectionCount);
				log.error("connectionCount={}", connectionCount);
			}
			
			nowSeq = minSeq;
			nowMdn = startMdn;
			nowSessionId = minSessionId;
			
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

	public void setMinSeq(long minSeq) {
		this.minSeq = minSeq;
	}

	public long getMaxSeq() {
		return maxSeq;
	}

	public void setMaxSeq(long maxSeq) {
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
		
		message.setAsn1Message(asn1MessageFactory.newMessage(operationName));
		
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
	
	public String getCallStatus() {
		return this.callStatus;
	}
	
	public int getConnectionCount() {
		return this.connectionCount;
	}
}
