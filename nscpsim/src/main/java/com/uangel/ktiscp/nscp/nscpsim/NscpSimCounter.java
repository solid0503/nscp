package com.uangel.ktiscp.nscp.nscpsim;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NscpSimCounter {
	
	@Autowired
	NscpSimContext context;
	
	Long tryCall = 0L;
	Long succCall = 0L;
	Long failCall = 0L;
	Long refusedCall = 0L;
	Long timeoutCall = 0L;
	Long msgSend = 0L;
	Long msgRecv = 0L;
	Long msgSendPerSec = 0L;
	Long msgRecvPerSec = 0L;
	Long succCallPerSec = 0L;
	Long failCallPerSec = 0L;
	
	long responseTime[];
	long responseTimeCounter[];
	double totalResTime = 0;
	
	private NscpSimCounter() {
		
	}
	
	public void incResponseTimeCounter(long diffTime) {
		if ( diffTime > 500 ) {
			log.error("diffTime={}", diffTime);
		} else {
			log.debug("diffTime={}", diffTime);
		}
		int index;
		for ( index = 0; index < responseTimeCounter.length; index++ ) {
			if ( diffTime >= responseTime[index] && diffTime < responseTime[index+1] ) {
				break;
			}
		}
		synchronized(responseTimeCounter) {
			responseTimeCounter[index]++;
			totalResTime += diffTime;
		}
	}
	
	public void startResetTimer() {
		String[] strResponseTimes = context.getResponseTimes().split(",");
		responseTime = new long[strResponseTimes.length+2];
		responseTime[0] = 0;
		int i;
		for ( i = 1; i <= strResponseTimes.length; i++ ) {
			responseTime[i] = Long.parseLong(strResponseTimes[i-1].trim());
		}
		responseTime[i] = Integer.MAX_VALUE;
		responseTimeCounter = new long[strResponseTimes.length+1];
		CounterResetTimerTask task = new CounterResetTimerTask();
		Timer timer = new Timer("Counter-Reset-Timer");
		timer.scheduleAtFixedRate(task, 1000, 1000);
	}
	
	public void incTryCall() {
		synchronized (this) {
			tryCall++;
		}
	}
	public void incSuccCall() {
		synchronized (this) {
			succCall++;
			this.succCallPerSec++;
		}
	}
	public void incFailCall() {
		synchronized (this) {
			failCall++;
			this.failCallPerSec++;
		}
	}
	public void incRefusedCall() {
		synchronized (this) {
			refusedCall++;
		}
	}
	public void incTimeoutCall() {
		synchronized (this) {
			timeoutCall++;
		}
	}
	
	public void incMsgRecv() {
		synchronized (this) {
			this.msgRecvPerSec++;
			this.msgRecv++;
		}
	}
	
	public void resetSec() {
		synchronized (this) {
			msgRecvPerSec = 0L;
			this.msgSendPerSec = 0L;
			this.succCallPerSec = 0L;
			this.failCallPerSec = 0L;
		}
	}
	
	public void incMsgSend() {
		synchronized(this) {
			this.msgSendPerSec++;
			this.msgSend++;
		}
	}

	public long getTryCall() {
		return tryCall;
	}

	public long getSuccCall() {
		return succCall;
	}

	public long getFailCall() {
		return failCall;
	}

	public long getRefusedCall() {
		return refusedCall;
	}

	public long getTimeoutCall() {
		return timeoutCall;
	}
	
	public long getMsgSendPerSec() {
		return msgSendPerSec;
	}
	
	public long getMsgRecvPerSec() {
		return msgRecvPerSec;
	}
	
	public long getMsgSend() {
		return this.msgSend;
	}
	
	public long getMsgRecv() {
		return this.msgRecv;
	}
	
	public double getTotalResTime() {
		return totalResTime;
	}
	
	public long getSuccCallPerSec() {
		return this.succCallPerSec;
	}
	public long getFailCallPerSec() {
		return this.failCallPerSec;
	}
}

class CounterResetTimerTask extends TimerTask {
	public void run() {
//		DifepSimCounter.getInstance().resetMsgRecv();
//		DifepSimCounter.getInstance().resetMsgSend();
	}
}
