/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.bootstrap.tsb.client;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author 212421693
 */
@Component
public class TimeseriesRestConfig  extends TimeSeriesBaseConfig implements EnvironmentAware{
	
	
	public static final String TIME_SERIES_VCAPS_NAME = "predix_timeseries_name"; //$NON-NLS-1$
	 
	@Value("${predix.timeseries.queryUri}")
	 private String  queryUri;
	
	private String  currentValueUri;
	    
    /**
     * 
     */
    private String hostUri;
    
    
    @Value("${predix.timeseries.override.oauthClientId}")
    private String oauthClientId;
   
    @Value("${predix.timeseries.override.oauthRestHost}")
    private String oauthRestHost;
    
    @Value("${predix.timeseries.override.oauthOverride}")
    private Boolean oauthOverride;
    /**
	 * @return the currentValueUri
	 */
	public String getCurrentValueUri() {
		if(StringUtils.isNotBlank(this.currentValueUri)) return this.currentValueUri;
		if(!StringUtils.isEmpty(this.queryUri)) this.hostUri = this.currentValueUri = this.queryUri + "/current";  //$NON-NLS-1$
		return this.currentValueUri;
	}

	/**
	 * @param currentValueUri the currentValueUri to set
	 */
	public void setCurrentValueUri(String currentValueUri) {
		this.currentValueUri = currentValueUri;
	}


    
    
	/* (non-Javadoc)
	 * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
	 */
	@Override
	public void setEnvironment(Environment env) {
		String vcapPropertyName = null;
    	String tsName = env.getProperty(TIME_SERIES_VCAPS_NAME); // this is set on the manifest of the application
    	
    	vcapPropertyName = null;
    	if(StringUtils.isEmpty(tsName)) {
    		vcapPropertyName = "vcap.services.predixTimeseries.credentials.query.uri"; // this is set from vcaps of the application //$NON-NLS-1$
    	}else { 
    		vcapPropertyName = "vcap.services."+tsName+".credentials.query.uri";  //$NON-NLS-1$//$NON-NLS-2$
    	}
    	if(!StringUtils.isEmpty(env.getProperty(vcapPropertyName))){
    		this.queryUri = env.getProperty(vcapPropertyName);
    	}
    	if(this.queryUri !=null ){ 
    		this.hostUri = this.queryUri.replaceAll("/datapoints/query", "");  //$NON-NLS-1$//$NON-NLS-2$
    		this.currentValueUri = this.queryUri + "/current";  //$NON-NLS-1$
    	}
    	if(StringUtils.isEmpty(tsName)) {
    		vcapPropertyName = "vcap.services.predixTimeseries.credentials.query.zone-http-header-value"; // this is set from vcaps of the application //$NON-NLS-1$
    	}else { 
    		vcapPropertyName = "vcap.services."+tsName+".credentials.query.zone-http-header-value";  //$NON-NLS-1$//$NON-NLS-2$
    	}
    	if(!StringUtils.isEmpty(env.getProperty(vcapPropertyName))){
    		this.zoneId=env.getProperty(vcapPropertyName); 
    	}
    	if(StringUtils.isEmpty(tsName)) {
    		vcapPropertyName = "vcap.services.predixTimeseries.credentials.query.zone-http-header-name"; // this is set from vcaps of the application //$NON-NLS-1$
    	}else { 
    		vcapPropertyName = "vcap.services."+tsName+".credentials.query.zone-http-header-name";  //$NON-NLS-1$//$NON-NLS-2$
    	}
    	if(!StringUtils.isEmpty(env.getProperty(vcapPropertyName))){
    		setPredixZoneIdHeaderName(env.getProperty(vcapPropertyName)); 
    	}
		
	}

	/**
	 * @return the hostUri
	 */
	public String getHostUri() {
		if(StringUtils.isNotBlank(this.hostUri)) return this.hostUri;
		if(!StringUtils.isEmpty(this.queryUri)) this.hostUri = this.queryUri.replaceAll("/datapoints/query", "");  //$NON-NLS-1$//$NON-NLS-2$
		return this.hostUri;
	}

	/**
	 * @param hostUri the hostUri to set
	 */
	public void setHostUri(String hostUri) {
		this.hostUri = hostUri;
	}

	/**
	 * @return the queryUri
	 */
	public String getQueryUri() {
		return this.queryUri;
	}

	/**
	 * @param queryUri the queryUri to set
	 */
	public void setQueryUri(String queryUri) {
		this.queryUri = queryUri;
	}

    
    /**
     * @return -
     */
    public String getOauthClientId()
    {
        return this.oauthClientId;
    }

    /**
     * @return -
     */
    public String getOauthRestHost()
    {
        return this.oauthRestHost;
    }

    /**
     * @return -
     */
    public boolean getOauthTimeseriesOverride()
    {
        return this.oauthOverride;
    }

		

}
