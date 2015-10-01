/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.bootstrap.tbs.websocket.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;

import org.apache.http.Header;

import com.ge.predix.solsvc.bootstrap.tsb.client.TimeseriesWSConfig;

/**
 * 
 * @author 212421693
 */

public class AuthorizationConfigurator extends ClientEndpointConfig.Configurator
        implements ClientEndpointConfig
{


    @SuppressWarnings("unused")
	private TimeseriesWSConfig tsInjectionWSConfig;
    
    private String             payload;

    private long               currentTime;

    private List<Header> authHeaders;

    /**
     * @return the currentTime
     */
    public long getCurrentTime()
    {
        return this.currentTime;
    }

    /**
     * @param currentTime the currentTime to set
     */
    public void setCurrentTime(long currentTime)
    {
        this.currentTime = currentTime;
    }

    
    /**
     * @param headers -
     * @param tsInjectionWSConfig -
     * @param payload -
     * @param currentTime -
     */
    public AuthorizationConfigurator(List<Header> headers, TimeseriesWSConfig tsInjectionWSConfig, String payload, long currentTime)
    {
        this.authHeaders = headers;
        this.tsInjectionWSConfig = tsInjectionWSConfig;
        this.payload = payload;
        this.currentTime = currentTime;
    }

    /*
     * (non-Javadoc)
     * @see javax.websocket.ClientEndpointConfig.Configurator#beforeRequest(java.util.Map)
     */
    @Override
    public void beforeRequest(Map<String, List<String>> headers)
    {
        super.beforeRequest(headers);
        
        for ( Header header : this.authHeaders ) { 
            List<String> attribList = new ArrayList<String>();
            attribList.add(header.getValue());
            headers.put(header.getName(),attribList);
        }

//        List<String> attribList = new ArrayList<String>();
//        attribList.add(this.tsInjectionWSConfig.getUserName());
//        headers.put("X-Predix-Username", attribList); //$NON-NLS-1$

//        try
//        {
//            attribList = new ArrayList<String>();
//            attribList.add(this.tsInjectionWSConfig.getSHAToken(this.getCurrentTime()));
//            headers.put("X-Predix-Secret", attribList); //$NON-NLS-1$
//        }
//        catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
//        {
//            throw new RuntimeException(e);
//        }
//        attribList = new ArrayList<String>();
//        attribList.add(this.authorization);
//        headers.put("Authorization", attribList); //$NON-NLS-1$
//
//        attribList = new ArrayList<String>();
//        attribList.add(this.zoneId);
//        headers.put("Predix-Zone-Id", attribList); //$NON-NLS-1$
//
//        attribList = new ArrayList<String>();
//        attribList.add(new Long(this.currentTime).toString());
//        headers.put("X-Predix-Date", attribList); //$NON-NLS-1$

      List<String> attribList = new ArrayList<String>();
        attribList = new ArrayList<String>();
        attribList.add("http://localhost"); //$NON-NLS-1$
        headers.put("Origin", attribList); //$NON-NLS-1$

    }

    /*
     * (non-Javadoc)
     * @see javax.websocket.ClientEndpointConfig.Configurator#afterResponse(javax.websocket.HandshakeResponse)
     */
    @Override
    public void afterResponse(HandshakeResponse handshakeResponse)
    {
        // TODO Auto-generated method stub
        super.afterResponse(handshakeResponse);
    }

    /*
     * (non-Javadoc)
     * @see javax.websocket.EndpointConfig#getDecoders()
     */
    @Override
    public List<Class<? extends Decoder>> getDecoders()
    {
        return new ArrayList<Class<? extends Decoder>>();
    }

    /*
     * (non-Javadoc)
     * @see javax.websocket.EndpointConfig#getEncoders()
     */
    @Override
    public List<Class<? extends Encoder>> getEncoders()
    {
        return new ArrayList<Class<? extends Encoder>>();
    }

    /*
     * (non-Javadoc)
     * @see javax.websocket.EndpointConfig#getUserProperties()
     */
    @SuppressWarnings("nls")
    @Override
    public Map<String, Object> getUserProperties()
    {
        Map<String, Object> userProperties = new HashMap<String, Object>();
        userProperties.put("payload", this.payload);
        return userProperties;
    }

    /*
     * (non-Javadoc)
     * @see javax.websocket.ClientEndpointConfig#getConfigurator()
     */
    @Override
    public Configurator getConfigurator()
    {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see javax.websocket.ClientEndpointConfig#getExtensions()
     */
    @Override
    public List<Extension> getExtensions()
    {
        return new ArrayList<Extension>();

    }

    /*
     * (non-Javadoc)
     * @see javax.websocket.ClientEndpointConfig#getPreferredSubprotocols()
     */
    @Override
    public List<String> getPreferredSubprotocols()
    {
        return new ArrayList<String>();
    }

}
