package com.uangel.ktiscp.nscp.common.asn1;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes= {Asn1CodecImpl.class, Asn1MessageFactoryImpl.class})
@ActiveProfiles("test")
@Slf4j
public class Asn1CodecImplTest {

	@Autowired
	Asn1CodecImpl asn1CodecImpl;
	
	@Autowired
	Asn1MessageFactory asn1MessageFactory;
	
	@Test
	@DisplayName("EncodeDecodeTest")
	public void encodeDecodeTest() throws Exception {
		log.debug("encodeDecodeTest");
		
		
		Asn1Message msg =asn1MessageFactory.newMessage("CS-Roaming-Noti-Invoke");
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
		Asn1Message msg2 = asn1MessageFactory.newMessage("CS-Roaming-Noti-Invoke");
		asn1CodecImpl.decode(msg2, buf2);
		log.info("msg2={}", msg2);
		
		Assertions.assertEquals("450081090009000", msg2.getStringValue("imsi"));
		Assertions.assertEquals("01090009000", msg2.getStringValue("msisdn"));
		Assertions.assertEquals("44010", msg2.getStringValue("mcc-mnc"));
		
		String traceString =  msg2.getTraceString();
		Assertions.assertTrue(traceString.contains("CS-Roaming-Noti-Invoke"));
	}
	
	@Test
	public void messageFactoryTest() {
		Assertions.assertEquals(1, asn1MessageFactory.getOpcodeByName("CS-Roaming-Noti-Invoke"));
		Assertions.assertEquals(6, asn1MessageFactory.getServiceIdByName("CS-Roaming-Noti-Invoke"));
		Assertions.assertEquals("CS-Roaming-Noti-Invoke", asn1MessageFactory.getOperationName(6,1));
	}
}
