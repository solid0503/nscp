package com.uangel.ktiscp.nscp.common.asn1;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.uangel.asn1.Asn1Object;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes=Asn1CodecImpl.class)
@ActiveProfiles("test")
@Slf4j
public class Asn1CodecImplTest {

	@Autowired
	Asn1CodecImpl asn1CodecImpl;
	
	Asn1MessageFactory asn1MessageFactory;
	
	@Test
	@DisplayName("EncodeDecodeTest")
	public void encodeDecodeTest() throws Exception {
		log.debug("encodeDecodeTest");
		
		
		Asn1Message msg = new Asn1MessageImpl("CS-Roaming-Noti-Invoke");
		msg.setValue("imsi", "450081090009000");
		msg.setValue("msisdn", "01090009000");
		msg.setValue("mcc-mnc", "44010");
		msg.setValue("first-ul", "1");
		msg.setValue("msc-Number", "0");
		msg.setValue("vlr-Number", "1");
		msg.setValue("hlr-Number", "2");
		msg.setValue("mvno-op-code", "KT");
		msg.setValue("mvno-subs-type", "0");
		
		
		ByteBuffer buf = asn1CodecImpl.encode(msg);
		log.info("buf={}", buf);
		
		byte[] bytes = new byte[buf.limit()];
		buf.get(bytes);
		log.info("bytes={}", bytes);
		
		ByteBuffer buf2 = ByteBuffer.wrap(bytes);
		Asn1Message msg2 = new Asn1MessageImpl("CS-Roaming-Noti-Invoke");
		asn1CodecImpl.decode(msg2, buf2);
		log.info("msg2={}", msg2);
		
	}
}
