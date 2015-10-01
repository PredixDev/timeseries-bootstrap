package com.ge.predix.solsvc.bootstrap.tsb.client;

import org.springframework.beans.factory.annotation.Value;

public class TimeSeriesBaseConfig {

	//@Value("${predix.timeseries.predix.zone.headername}:'Predix-Zone-Id'")
    private String 				predixZoneIdHeaderName = "Predix-Zone-Id";
    
	@Value("${predix.timeseries.zoneid}")
	protected String 				zoneId;

	public String getPredixZoneIdHeaderName() {
		return predixZoneIdHeaderName;
	}

	public void setPredixZoneIdHeaderName(String predixZoneIdHeaderName) {
		this.predixZoneIdHeaderName = predixZoneIdHeaderName;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String predixZoneId) {
		this.zoneId = predixZoneId;
	}
    
    
	    
}
