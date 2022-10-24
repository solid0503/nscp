package com.uangel.ktiscp.nscp.nscpsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import com.uangel.ualib.syslib.uaLog;
import com.uangel.ualib.syslib.uaLogLevel;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(scanBasePackages = {
		"com.uangel.ktiscp.nscp.nscpsim", 
		"com.uangel.ktiscp.nscp.common.asn1",
		"com.uangel.ktiscp.nscp.common.sock"
})
@Slf4j
public class NscpsimApplication {

	static ConfigurableApplicationContext ctx = null;
	
	public static void main(String[] args) {
		if ( args.length < 1 ) {
			System.out.println("Usage : nscpsim <file>");
			return;
		}
		
		uaLog.setLogLevel(0, uaLogLevel.L_CRT);
		
		ctx = SpringApplication.run(NscpsimApplication.class, args);
	}

	public static void printActiveProfiles(ApplicationContext ctx) {
		for (final String profileName : ctx.getEnvironment().getActiveProfiles()) {
			log.info("currently active profile={}", profileName);
		}
	}

	public static Object getBean(String beanName) {
		return ctx.getBean(beanName);
	}

	public static <T> T getBean(Class<T> classType) {
		return ctx.getBean(classType);
	}
	
	public static void waitForCtx() {
		while ( ctx == null ) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}
