package com.uangel.ktiscp.nscp.nscpsim;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TrafficGenTimerManager {
	ArrayList<TrafficGenTimer> timerList = new ArrayList<TrafficGenTimer>();
	public static final int TIMER_COUNT = 10;
	public int cpsDist = 0;
	public int cps = 0;
	public long duration;
	
	public TrafficGenTimerManager() {
		
	}
	
	public void start(int cps, long duration) {
		try {
			
			this.cps = cps;
			this.duration = duration;
			for ( int i = 0; i < TIMER_COUNT; i++ ) {
				timerList.add(new TrafficGenTimer("TrafficGenTimer-"+i, 0, duration));
			}
			for ( cpsDist = 0; cpsDist < cps; cpsDist++ ) {
				timerList.get(cpsDist%TIMER_COUNT).incCps();
			}
			
			for ( int i =0; i < TIMER_COUNT; i++) {
				this.timerList.get(i).startTimer();
				Thread.sleep(1000/TIMER_COUNT-2);
			}
		} catch (Exception e) {
			log.error("Exception in start()", e);
		}
	}

	public int getCps() {
		return cps;
	}

	public long getDuration() {
		return duration;
	}
	
	public void incCps(int n) {
		for ( int i = 0; i < n; i++ ) {
			this.incCps();
		}
	}
	
	public void decCps(int n) {
		for ( int i = 0; i < n; i++ ) {
			this.decCps();
		}
	}
	
	public void incCps() {
		TrafficGenTimer minTimer = getMinCpsTimer();
		minTimer.incCps();
		this.cps++;
	}
	
	public void decCps() {
		TrafficGenTimer maxTimer = getMaxCpsTimer();
		maxTimer.decCps();
		this.cps--;
	}
	
	private TrafficGenTimer getMaxCpsTimer() {
		int max = 0;
		int maxIdx = 0;
		for ( int i = 0; i < this.timerList.size(); i++ ) {
			if ( max < timerList.get(i).getCps() ) {
				maxIdx = i;
				max = timerList.get(i).getCps();
			}
		}
		return this.timerList.get(maxIdx);
	}
	
	private TrafficGenTimer getMinCpsTimer() {
		int min = Integer.MAX_VALUE;
		int minIdx = 0;
		for ( int i = 0; i < this.timerList.size(); i++ ) {
			if ( min > timerList.get(i).getCps() ) {
				minIdx = i;
				min = timerList.get(i).getCps();
			}
		}
		return this.timerList.get(minIdx);
	}
}
