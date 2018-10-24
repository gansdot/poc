package com.poc.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Configuration
public class ForceConfig {

	
	static final String USERNAME = "ganesan.m@tcs.com.ssr";
	static final String PASSWORD = "Passw0rd1rS3gZkcvfEWG0FrlbI7cSXDyV";

	
	@Bean(name="config")
	public ConnectorConfig config() {
		ConnectorConfig sfc = new ConnectorConfig();
		sfc.setUsername(USERNAME);
		sfc.setPassword(PASSWORD);
		return sfc;
	}

	
	@Bean 
	public EnterpriseConnection enterpriseConnection() throws ConnectionException {
		return Connector.newConnection(config());
	}
	
}
