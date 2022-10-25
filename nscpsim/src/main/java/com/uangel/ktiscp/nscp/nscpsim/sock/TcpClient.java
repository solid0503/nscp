package com.uangel.ktiscp.nscp.nscpsim.sock;

import com.uangel.ktiscp.nscp.common.sock.NscpMessage;

public interface TcpClient {
	void connect();
	void send(NscpMessage msg);
	boolean isConnected();
}
