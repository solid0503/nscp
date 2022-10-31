package com.uangel.ktiscp.nscp.common.asn1;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.uangel.asn1.Asn1Object;
import com.uangel.ktiscp.nscp.common.json.JsonType;
import com.uangel.utms.uTMS_Util.ByteUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asn1MessageImpl implements Asn1Message {
	String name;
	Map<String,Object> map = new HashMap<String, Object>();   // 값을 맵에 저장해둔다.
	ArrayList<String> orderedNames = new ArrayList<String>(); // 출력할때 순서를 위해 별도 관리
	
	Asn1MessageImpl(String name) {
		this.name = name;
	}
	
	@Override
	public void setValue(String name, Object value) {
		log.debug("setValue({},{})", name, value);
		orderedNames.add(name);
		map.put(name, value);
	}
	
	@Override
	public Object getValue(String name) {
		return map.get(name);
	}
	
	@Override
	public String getStringValue(String name) {
		
		Object value = map.get(name);
		if ( value == null ) {
			return null;
		}
		
		if ( value instanceof byte[] ) {
			if ( ByteUtil.isAsciiString((byte[])value) ) {
				try {
					return new String((byte[])value, "utf-8");
				} catch (UnsupportedEncodingException e) {
					return "0x"+ByteUtil.toHexString((byte[])value);
				}
			} 
			return "0x"+ByteUtil.toHexString((byte[])value);
		} else if ( value instanceof String ) {
			return (String)value;
		} else {
			return value.toString();
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void encode(Asn1Object asn1Object) {
		map.forEach((key,value)->{
			asn1Object.setValue(key, value);
		});
	}
	
	@Override
	public void decode(Asn1Object asn1Object) {
		for ( Asn1Object obj:asn1Object.getArray() ) {
			this.setValue(obj.getName(), obj.getValue());
		}
	}
	
	@Override
	public String toString() {
		return String.format("[Name=%s, MapData=%s]", name, map);
	}
	
	@Override
	public String getTraceString() {
		StringBuilder sb = new StringBuilder(1024);
		sb.append("\n\t\t\t").append("OperationName       : ").append(name);
		
		for (String paraName : orderedNames) {
			sb.append("\n\t\t\t").append(String.format("%-20s: [%s]", paraName, this.getStringValue(paraName)));
		}
		
		return sb.toString();
	}
	
	public void writeToJsonType(JsonType jsonType) {
		jsonType.setValue("OperationName", name);
		for (String paraName : orderedNames) {
			jsonType.setValue(paraName, this.getStringValue(paraName));
		}
	}
}
