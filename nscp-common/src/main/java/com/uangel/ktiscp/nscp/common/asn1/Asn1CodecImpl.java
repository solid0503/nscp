package com.uangel.ktiscp.nscp.common.asn1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.uangel.asn1.Asn1Object;
import com.uangel.asn1.Asn1World;
import com.uangel.asn1.BerDecoder;
import com.uangel.asn1.BerEncoder;
import com.uangel.asn1.parser.Asn1Parser;
import com.uangel.asn1.parser.Asn1ParserFactory;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Asn1CodecImpl implements Asn1Codec {

	private Asn1World asn1World;
	
	@Value("${asn1.paths}")
    List<String> asn1FilePaths;
	
	@Value("${asn1.max-size:2048}")
	int maxSize;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@PostConstruct
	public void init() throws Exception {
		try {
			
			asn1World = new Asn1World();
			for ( String asn1FilePath:asn1FilePaths) {
				log.info("asn1.paths : {}", asn1FilePath);
				
				InputStream inputStream = resourceLoader.getResource(asn1FilePath).getInputStream();
				InputStreamReader streamReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(streamReader);
				
				Asn1Parser asn1Parser = Asn1ParserFactory.getAsn1Parser(reader);
				
//				Asn1Parser asn1Parser = Asn1ParserFactory.getAsn1Parser(
//						new BufferedReader(new FileReader(asn1FilePath)));
				asn1Parser.parse(asn1World);
			}
			
			log.info("asn1.max-size : {}", maxSize );
		} catch (Exception e) {
			log.error("Asn1 init failure.", e);
			throw e;
		}
		log.info("Asn1CodecImpl init succ.");
	}
	
	@Override
	public ByteBuffer encode(Asn1Message msg) {
		
		Asn1Object asn1Obj = asn1World.newObject(msg.getName());
		if ( asn1Obj == null ) {
			throw new RuntimeException("Not found asn1 parameter info for " + msg.getName());
		}
		
		msg.encode(asn1Obj);
		ByteBuffer buf = ByteBuffer.allocate(maxSize);
		BerEncoder.encode(asn1World, asn1Obj, buf);
		buf.flip();
		return buf;
	}

	@Override
	public void decode(Asn1Message msg, ByteBuffer buf) {
		Asn1Object asn1Obj = BerDecoder.decode(asn1World, msg.getName(), buf);
		msg.decode(asn1Obj);
	}
}
