package com.uangel.ktiscp.nscp.nscpsim;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.transaction.Transaction;
import com.uangel.ktiscp.nscp.nscpsim.sock.TcpClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NscpClientSim extends Thread {
	@Autowired
	private ApplicationArguments applicationArguments;
	
	@Autowired
	NscpSimContext context;
	
	@Autowired
	TcpClient client;
	
	@Autowired
	NscpSimCounter counter;
	
	@Autowired
	TrafficGenTimerManager trafficGenTimerManager;
	
	@Autowired
	NscpSimScreen screen;
	
	boolean quitFlag = false;

	public NscpClientSim() {
		super("NscpClientSim-Thread");
	}
	
	@PostConstruct
	public void init() {
		start();
	}
	
	public void run() {
		NscpsimApplication.waitForCtx();
		
		log.info("NscpClientSim started!");
		
		String[] args = applicationArguments.getSourceArgs();
		
		String xmlFile = args[0];
		
		context.loadConfig(xmlFile);
		
		log.info("waiting for connection...");
		while ( ! client.isConnected() ) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		log.info("connected!!");
		
		counter.startResetTimer();
		trafficGenTimerManager.start(context.getCps(), context.getDuration());
	
		
		
		if ( context.isPrefMode() ) {
			screen.startScreenTimer();
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while ( true ) {
			try {
				String line = br.readLine();
				if ( line.equals("") ) {
					screen.setDiaplyFlag(false);
					System.out.println("To increase cps, input +n");
					System.out.println("To decrease cps, input -n");
					System.out.println("To stop and exit, input quit");
					System.out.print("input :");
					line = br.readLine();
					System.out.println("");
					if ( line.startsWith("+") ) {
						String cps = line.substring(1);
						trafficGenTimerManager.incCps(Integer.parseInt(cps));
					} else if ( line.startsWith("-") ) {
						String cps = line.substring(1);
						trafficGenTimerManager.decCps(Integer.parseInt(cps));
					} else if ( line.equalsIgnoreCase("quit")) {
						quitFlag = true;
					}
				}
			} catch (Exception e) {
				
			} finally {
				screen.setDiaplyFlag(true);
			}
		}
	}
}
