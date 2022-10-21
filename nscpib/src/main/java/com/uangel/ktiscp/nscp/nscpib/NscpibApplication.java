package com.uangel.ktiscp.nscp.nscpib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.uangel.ktiscp.nscp.nscpib", 
		"com.uangel.ktiscp.nscp.common.asn1",
		"com.uangel.ktiscp.nscp.common.sock"
})
public class NscpibApplication {

	public static void main(String[] args) {
		SpringApplication.run(NscpibApplication.class, args);
	}

}
