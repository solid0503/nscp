package com.uangel.ktiscp.nscp.common.bepsock;

public interface BepClient {
	void connect();
	void send(BepMessage msg);
	void sendWithCallback(BepMessage msg, RecvCallback callback, RecvTimeoutCallback timeoutCallback);
}
