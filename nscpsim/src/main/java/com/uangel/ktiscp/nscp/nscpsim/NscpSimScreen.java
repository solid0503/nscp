package com.uangel.ktiscp.nscp.nscpsim;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.util.TimeUtil;
import com.uangel.ualib.stringFormatTable.StringTableFormatter;
import com.uangel.utms.uTMS_Util.StringUtil;


@Component
public class NscpSimScreen {
	@Autowired
	NscpSimCounter counter;
	
	@Autowired
	TrafficGenTimerManager trafficGenTimerManager;
	
	String startTime = null;
	boolean displayFlag = true;
	long start = 0;
	
	private NscpSimScreen() {
		
	}
	
	public void setDiaplyFlag(boolean flag) {
		this.displayFlag = flag;
	}
	
	public void startScreenTimer() {
		
		NscpClientSim clientSim = NscpsimApplication.getBean(NscpClientSim.class);
		
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if ( displayFlag == false ) {
					return;
				}
				String nowTime = TimeUtil.dateToString(new java.util.Date(System.currentTimeMillis()), "yyyy/MM/dd HH:mm:ss");
				StringTableFormatter table = new StringTableFormatter("Field", "Value", "Field", "Value");
				
				table.addCell("Start-Time").addCell(startTime);
				table.addCell("Current-Time").addCell(nowTime);
				table.addCell("Msg send(/s)").addCell(counter.getMsgSendPerSec());
				table.addCell("Msg recv(/s)").addCell(counter.getMsgRecvPerSec());
				
				long succCallPerSec = counter.getSuccCallPerSec();
				long failCallPerSec = counter.getFailCallPerSec();
				
				counter.resetSec();
				table.addCell("Call rate(/s)").addCell(trafficGenTimerManager.getCps());
				table.addCell("duration(/s)").addCell(trafficGenTimerManager.getDuration());
				table.addCell("Msg send count").addCell(counter.getMsgSend());
				table.addCell("Msg recv count").addCell(counter.getMsgRecv());
				table.addCell("Try Call").addCell(counter.getTryCall());
				table.addCell("Succ Call").addCell(counter.getSuccCall());
				table.addCell("Fail Call").addCell(counter.getFailCall());
				table.addCell("Timeout Call").addCell(counter.getTimeoutCall());
				
				String strSuccRate = "N/A";
				if ( succCallPerSec + failCallPerSec > 0 ) {
					Double succRate = ((double)succCallPerSec) / (succCallPerSec + failCallPerSec) * 100;
					strSuccRate = StringUtil.sprintf("%.2f", succRate);
				}
				
				table.addCell("Succ rate(/s)").addCell(strSuccRate+"%");
				table.addCell("").addCell("");
				
				StringTableFormatter resTimeTable = new StringTableFormatter("Response-Time", "count");
				for ( int i = 0; i < counter.responseTimeCounter.length; i++ ) {
					String time = null;
					if ( counter.responseTime[i+1] == Integer.MAX_VALUE ) {
						time = StringUtil.sprintf("%4dms~", counter.responseTime[i]);
					} else { 
						time = StringUtil.sprintf("%4dms~%4dms", counter.responseTime[i], counter.responseTime[i+1]);
					}
					resTimeTable.addCell(time).addCell(counter.responseTimeCounter[i]);
				}
				resTimeTable.addCell("AVERAGE Time");
				if ( counter.getTotalResTime() == 0 ) {
					resTimeTable.addCell(0);
				} else {
					resTimeTable.addCell(StringUtil.sprintf("%.2f", counter.getTotalResTime()/counter.getMsgRecv()));
				}
				
				table.drawBorderLine();
				resTimeTable.setLeftSpace(4);
				resTimeTable.drawColumnBorderLine();
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println(table.toString());
				System.out.println(resTimeTable.toString());
				System.out.println("Press enter to command.");
				
				long now = System.currentTimeMillis();
				if ( now +1000 > start+(trafficGenTimerManager.getDuration()*1000)) {
					if ( counter.getTryCall() == counter.getSuccCall() + counter.getFailCall() + counter.getTimeoutCall() ) {
						System.exit(0);
					}
				}
				
				if ( clientSim.quitFlag == true ) {
					if ( counter.getTryCall() == counter.getSuccCall() + counter.getFailCall() + counter.getTimeoutCall() ) {
						System.exit(0);
					}
				}
				
				
			}
			
		};
		Timer timer = new Timer("Counter-Reset-Timer");
		timer.scheduleAtFixedRate(task, 1000, 1000);
		start = System.currentTimeMillis();
		startTime = TimeUtil.dateToString(new java.util.Date(start), "yyyy/MM/dd HH:mm:ss");
		
	}
}

