package com.uangel.ktiscp.nscp.nscpsim;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.uangel.ktiscp.nscp.common.sock.MessageType;
import com.uangel.ktiscp.nscp.common.sock.NscpMessage;
import com.uangel.ktiscp.nscp.common.transaction.Transaction;
import com.uangel.ktiscp.nscp.nscpsim.sock.TcpClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimScenario {
	LinkedList<SimAction> actionList = new LinkedList<SimAction>();
	public String mdn;
	private NscpMessage lastRes = null;
	private static Timer waitTimer = new Timer("Scen-Wait-Timer");
	boolean bSucc = false;
	Object syncObj = new Object();
	
	public void addAction(SimAction action) {
		actionList.addLast(action);
	}
	
	public void runNextAction(NscpMessage res) {
		if ( res != null ) {
			lastRes = res;
		}
		if ( actionList.isEmpty() ) {
			return;
		}
		SimAction action = actionList.pop();
		if ( res != null && action.actionName.equals("recv")==false ) {
			log.error("Not expected scenario action. expected:recv, real:{}", action.actionName);
			return;
		}
		
		NscpSimContext context = NscpsimApplication.getBean(NscpSimContext.class);
		NscpSimTrManager trManager = NscpsimApplication.getBean(NscpSimTrManager.class);
		TcpClient client = NscpsimApplication.getBean(TcpClient.class);
		NscpSimCounter counter = NscpsimApplication.getBean(NscpSimCounter.class);
		
		if ( action.actionName.equals("send") ) {
			NscpMessage msg = null;
			
			if ( action.opName.equalsIgnoreCase("ReleaseRequest") ) {
				msg = context.newRelReqMessage();
			} else {
				msg = context.newMessage(action.opName);
				for ( int i = 0; i < action.paramNameList.size(); i++) {
					if ( action.paramValueList.get(i).equals("$MDN") ) {
						msg.setParameter(action.paramNameList.get(i), mdn);
						msg.setRoutingInfoFromMdn(mdn);
					} else if ( action.paramValueList.get(i).startsWith("@") ) {
						String resParameter = action.paramValueList.get(i).substring(1).replaceAll(" ", "_").toUpperCase();
						msg.setParameter(action.paramNameList.get(i), lastRes.getParameter(resParameter));
					} else {
						msg.setParameter(action.paramNameList.get(i), action.paramValueList.get(i));
					}
				}
			}
			
			Transaction tr = new Transaction();
			tr.putData("scen", this);
			
			trManager.addTransaction(msg.getTransactionId(), tr);
			counter.incMsgSend();
			client.send(msg);
			
		} else if ( action.actionName.equals("recv") ) {
			/*
			if ( !action.opName.equals(res.getOpName(res.getOpCode())) ) {
				log.error("Not expected message. expectedOp:{}, receivedOp:{}", action.opName, res.getOpName(res.getOpCode()));
				DifepSimCounter.getInstance().incFailCall();
				actionList.clear();
				return;
			}
			*/
			/*
			if ( res.getOperationType().equals(TlvMessage.OPERATION_TYPE_ERROR) ) {
				DifepSimCounter.getInstance().incFailCall();
				actionList.clear();
				return;
			}
			*/
			
			if ( isError(res) ) {
				counter.incFailCall();
				actionList.clear();
				return;
			}
		} else if ( action.actionName.equals("wait") ) {
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					try {
						if ( actionList.isEmpty() ) {
							synchronized (syncObj) {
								if ( bSucc == false ) {
									bSucc = true;
									log.debug("scenario succ.");
									counter.incSuccCall();
								}
							}
						}
						runNextAction(null);
					}
					catch(Exception err) {
						log.error("Exception in WaitTimerTask.", err);
					}
					
				}
				
			};
			SimScenario.waitTimer.schedule(task, action.waitTime);
		}
		if ( actionList.isEmpty() ) {
			synchronized (syncObj) {
				if ( bSucc == false ) {
					bSucc = true;
					log.debug("scenario succ.");
					counter.incSuccCall();
				}
			}
		}
	}
	
	public boolean isError(NscpMessage message) {
		
		switch(MessageType.fromInt(message.getMessageType()) ){
		case ERROR:
		case REJECT:
		case ABORT:
			return true;
		default :
			return false;
		}
	}
	
	public String getNextActionName() {
		if ( actionList.isEmpty() ) {
			return "";
		}
		return actionList.get(0).actionName;
	}
}