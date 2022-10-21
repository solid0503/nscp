package com.uangel.ktiscp.nscp.common.asn1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.uangel.asn1.Asn1Object;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asn1MessageImpl implements Asn1Message {
	String name;
	Map<String,Object> map = new HashMap<String, Object>();
	ArrayList<String> orderedNames = new ArrayList<String>();
	
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
			if ( obj.getValue() instanceof byte[] ) {
				this.setValue(obj.getName(), new String((byte[])obj.getValue()));
			} else {
				this.setValue(obj.getName(), obj.getValue());
			}
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
			sb.append("\n\t\t\t").append(String.format("%-20s: [%s]", paraName, this.getValue(paraName)));
		}
		
		return sb.toString();
	}
}
