package com.uangel.ktiscp.nscp.nscpsim;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimScenarioTemplate extends SimScenario {
	@Autowired
	NscpSimCounter counter;
	
	@SuppressWarnings("unchecked")
	public SimScenario getNewScenario() {
		SimScenario scen = new SimScenario();
		scen.actionList = (LinkedList<SimAction>)this.actionList.clone();
		counter.incTryCall();
		return scen;
	}
}
