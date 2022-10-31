package com.uangel.ktiscp.nscp.common.sock;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.uangel.ktiscp.nscp.common.asn1.Asn1Codec;
import com.uangel.ktiscp.nscp.common.asn1.Asn1CodecImpl;
import com.uangel.ktiscp.nscp.common.asn1.Asn1MessageFactory;
import com.uangel.ktiscp.nscp.common.asn1.Asn1MessageFactoryImpl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes= {NscpMessageFactoryImpl.class, Asn1CodecImpl.class, Asn1MessageFactoryImpl.class})
@ActiveProfiles("test")
@Slf4j
public class NscpMessageEncoderDecoderTest {
	
	@Autowired
	NscpMessageFactory nscpMessageFactory;
	
	@Autowired
	Asn1MessageFactory asn1MessageFactory;
	
	@Autowired
	Asn1Codec asc1Codec;
	
	
	@Test
	public void messageTest() throws Exception {
		String operationName = "CS-Roaming-Noti-Invoke";
		NscpMessage message = nscpMessageFactory.createMessage();
		message.setMessageVersion(0);
		message.setLinkedId(0);
		message.setMessageId(MessageId.SERVICE_REQUEST.getValue());
		message.setServiceId(ServiceId.ROAMING_LOCATION_INFO.getValue());
		message.setMessageType(MessageType.TERMINATION.getValue());
		
		Integer serviceId = asn1MessageFactory.getServiceIdByName(operationName);
		message.setServiceId(serviceId);
		Integer opcode = asn1MessageFactory.getOpcodeByName(operationName);
		message.setOperationCode(opcode);
		message.setOTID(1001);
		
		message.setAsn1Message(asn1MessageFactory.newMessage(operationName));
		message.setValue("imsi", "450081012341234");
		message.setValue("msisdn", "01090009000");
		message.setValue("mcc-mnc", "45008");
		message.setValue("first-ul", "1");
		message.setValue("msc-Number", "1234");
		message.setValue("vlr-Number", "5678");
		message.setValue("hlr-Number", "9012");
		message.setValue("mvno-op-code", "KT");
		message.setValue("mvno-subs-type", "0");
		message.setRoutingInfoFromMdn("01090009000");
		
		log.info("nscpMessage={}", message.getTraceString());
		String origTraceString = message.getTraceString();
		
		NscpMsgEncoder encoder = new NscpMsgEncoder(asc1Codec);
		
		ByteBuf byteBuf = Unpooled.wrappedBuffer(new byte[4096]);
		byteBuf.resetWriterIndex();
		
		encoder.encode(null, message, byteBuf);
		
		log.info("buf={}", byteBuf);
		
		NscpMsgDecoder decoder = new NscpMsgDecoder(asn1MessageFactory, asc1Codec);
		List<Object> out = new ArrayList<Object>();
		decoder.decode(null, byteBuf, out);
		NscpMessage decodedMessage = (NscpMessage)out.get(0);
		
		log.info("decodedMessage={}", decodedMessage.getTraceString());
		String decodedTraceString = decodedMessage.getTraceString();
		Assertions.assertEquals(origTraceString, decodedTraceString);
		Assertions.assertEquals(message.getMessageVersion(), decodedMessage.getMessageVersion());
		Assertions.assertEquals(message.getLinkedId(), decodedMessage.getLinkedId());
		Assertions.assertEquals(message.getMessageId(), decodedMessage.getMessageId());
		Assertions.assertEquals(message.getMessageType(), decodedMessage.getMessageType());
		Assertions.assertEquals(message.getServiceId(), decodedMessage.getServiceId());
		Assertions.assertEquals(message.getOperationCode(), decodedMessage.getOperationCode());
		Assertions.assertEquals(message.getOTID(), decodedMessage.getOTID());
		Assertions.assertEquals(message.getDTID(), decodedMessage.getDTID());
		Assertions.assertEquals(message.getTimeStamp(), decodedMessage.getTimeStamp());
		
		Assertions.assertEquals(message.getStringValue("imsi"), decodedMessage.getStringValue("imsi"));
		Assertions.assertEquals(message.getStringValue("msisdn"), decodedMessage.getStringValue("msisdn"));
		Assertions.assertEquals(message.getStringValue("mcc-mnc"), decodedMessage.getStringValue("mcc-mnc"));
		Assertions.assertEquals(message.getStringValue("first-ul"), decodedMessage.getStringValue("first-ul"));
		Assertions.assertEquals(message.getStringValue("msc-Number"), decodedMessage.getStringValue("msc-Number"));
		Assertions.assertEquals(message.getStringValue("vlr-Number"), decodedMessage.getStringValue("vlr-Number"));
		Assertions.assertEquals(message.getStringValue("hlr-Number"), decodedMessage.getStringValue("hlr-Number"));
		Assertions.assertEquals(message.getStringValue("mvno-op-code"), decodedMessage.getStringValue("mvno-op-code"));
		Assertions.assertEquals(message.getStringValue("mvno-subs-type"), decodedMessage.getStringValue("mvno-subs-type"));
		
		String jsonString = decodedMessage.toJson();
		log.info("jsonStr={}", jsonString);
		Assertions.assertTrue(jsonString.contains("\"mcc-mnc\": \"45008\""));
		Assertions.assertTrue(jsonString.contains("\"mvno-subs-type\": \"0\""));
		Assertions.assertTrue(jsonString.contains("\"msc-Number\": \"1234\""));
		Assertions.assertTrue(jsonString.contains("\"hlr-Number\": \"9012\""));
		Assertions.assertTrue(jsonString.contains("\"imsi\": \"450081012341234\""));
		Assertions.assertTrue(jsonString.contains("\"msisdn\": \"01090009000\""));
		Assertions.assertTrue(jsonString.contains("\"first-ul\": \"1\""));
		Assertions.assertTrue(jsonString.contains("\"mvno-op-code\": \"KT\""));
		Assertions.assertTrue(jsonString.contains("\"vlr-Number\": \"5678\""));
		
		NscpMessage res = decodedMessage.getResponse(MessageType.NONE);
		Assertions.assertEquals(decodedMessage.getMessageVersion(), res.getMessageVersion());
		Assertions.assertEquals(decodedMessage.getLinkedId(), res.getLinkedId());
		Assertions.assertEquals(MessageType.NONE.getValue(), res.getMessageType());
		Assertions.assertEquals(decodedMessage.getServiceId(), res.getServiceId());
		Assertions.assertEquals(decodedMessage.getOperationCode(), res.getOperationCode());
		Assertions.assertEquals(decodedMessage.getOTID(), res.getDTID());
		Assertions.assertEquals(decodedMessage.getDTID(), res.getOTID());
		Assertions.assertEquals(decodedMessage.getTimeStamp(), res.getTimeStamp());
		
		Assertions.assertTrue(decodedMessage.isRequest());
		Assertions.assertFalse(res.isRequest());
		
		log.info("transactionId={}", decodedMessage.getTransactionId());
		
		
	}
	
	@Test
	public void createPingMessageTest() throws Exception {
		NscpMessage ping = nscpMessageFactory.createPingMessage();
		log.info("ping={}", ping);
		Assertions.assertEquals(1, ping.getMessageVersion());
		Assertions.assertEquals(1, ping.getMessageId());
		Assertions.assertEquals(3, ping.getMessageType());
		
	}
}
