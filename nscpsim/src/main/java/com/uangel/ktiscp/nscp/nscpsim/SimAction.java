package com.uangel.ktiscp.nscp.nscpsim;

import java.util.ArrayList;


public class SimAction {
	public String actionName;
	public String opName;
	public ArrayList<String> paramNameList = new ArrayList<String>();
	public ArrayList<String> paramValueList = new ArrayList<String>();
	public long waitTime=0;
	public void addParameter(String name, String value) {
		paramNameList.add(name);
		paramValueList.add(value);
	}
}
