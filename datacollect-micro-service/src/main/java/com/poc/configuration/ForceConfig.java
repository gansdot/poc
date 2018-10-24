package com.poc.configuration;


import org.apache.catalina.connector.Connector;
import org.springframework.context.annotation.Configuration;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Configuration
public class ForceConfig {

	
	static final String USERNAME = "ganesan.m@tcs.com.ssr";
	static final String PASSWORD = "Passw0rd1rS3gZkcvfEWG0FrlbI7cSXDyV";

	
	public ConnectorConfig config() {
		ConnectorConfig sfc = new ConnectorConfig();
		sfc.setUsername(USERNAME);
		sfc.setPassword(PASSWORD);
		return sfc;
	}

	
	public EnterpriseConnection enterpriseConnection() throws ConnectionException {
		return Connector.newConnection(config());
	}
	
}
