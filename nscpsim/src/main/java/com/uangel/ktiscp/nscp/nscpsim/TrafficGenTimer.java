package com.uangel.ktiscp.nscp.nscpsim;

import java.util.Timer;
import java.util.TimerTask;

import lombok.extern.slf4j.Slf4j;

public class TrafficGenTimer extends Timer {
	int cps;
	long duration;
	TrafficGenTimerTask task = new TrafficGenTimerTask();
	public TrafficGenTimer(String name, int cps, long duration) {
		super(name);
		this.cps = cps;
		this.duration = duration;
	}
	
	public void startTimer() {
		task.cps = this.cps;
		task.duration = this.duration;
		this.scheduleAtFixedRate(task, 1000, 1000);
	}
	
	public void incCps() {
		cps++;
		task.cps = cps;
	}
	
	public void decCps() {
		cps--;
		task.cps = cps;
	}
	
	public int getCps() {
		return cps;
	}
}

@Slf4j
class TrafficGenTimerTask extends TimerTask {
	int cps;
	long duration;
	int runCount = 0;
	NscpSimContext nscpSimContext = null;
	SimScenarioTemplate scenarioTemplate = null;
	NscpClientSim nscpClientSim = null;
	
	TrafficGenTimerTask() {
		nscpSimContext = NscpsimApplication.getBean(NscpSimContext.class);
		scenarioTemplate = NscpsimApplication.getBean(SimScenarioTemplate.class);
		nscpClientSim = NscpsimApplication.getBean(NscpClientSim.class);
	}
	public void run() {
		try {
			runCount++;
			if ( runCount >= duration ) {
				this.cancel();
			}
			
			if ( nscpClientSim.quitFlag == true ) {
				this.cancel();
				return;
			}
			
			for ( int i = 0; i < cps; i++ ) {
				SimScenario scen = scenarioTemplate.getNewScenario();
				scen.mdn = nscpSimContext.nextMdn();
				while ( scen.getNextActionName().equals("send") ) {
					scen.runNextAction(null);
				}
			}
		} catch (Exception e) {
			log.error("Exception in TrafficGenTimerTask", e);
		}
	}
}
