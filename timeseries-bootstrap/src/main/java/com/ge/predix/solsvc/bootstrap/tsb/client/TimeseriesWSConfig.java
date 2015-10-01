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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author 212421693
 */
@Component
public class TimeseriesWSConfig extends TimeSeriesBaseConfig
        implements EnvironmentAware
{

    private static Logger      log                    = LoggerFactory.getLogger(TimeseriesWSConfig.class);

    /**
     * vcap variable name
     */
    public static final String TIME_SERIES_VCAPS_NAME = "predix_timeseries_name";                         //$NON-NLS-1$

    @Value("${predix.timeseries.ingestUri}")
    private String             injectionUri;

    @Value("${predix.timeseries.ingestionUsername}")
    private String             userName;

    @Value("${predix.timeseries.ingestionPassword}")
    private String             password;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
     * springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(Environment env)
    {
        String vcapPropertyName = null;
        String tsName = env.getProperty(TIME_SERIES_VCAPS_NAME); // this is set on the manifest of the application
        if ( StringUtils.isEmpty(tsName) )
        {
            tsName = "predixTimeseries"; //$NON-NLS-1$
        }

        vcapPropertyName = "vcap.services." + tsName + ".credentials.ingest.uri"; //$NON-NLS-1$//$NON-NLS-2$
        if ( StringUtils.isNotBlank(env.getProperty(vcapPropertyName)) )
        {
            this.injectionUri = env.getProperty(vcapPropertyName);
        }
        
        
        vcapPropertyName = null;
        vcapPropertyName = "vcap.services." + tsName + ".credentials.ingest.zone-http-header-name"; //$NON-NLS-1$//$NON-NLS-2$

        if ( StringUtils.isNotBlank(env.getProperty(vcapPropertyName)) )
        {
            this.setPredixZoneIdHeaderName(env.getProperty(vcapPropertyName));
        }
        
        vcapPropertyName = null;
        vcapPropertyName = "vcap.services." + tsName + ".credentials.ingest.zone-http-header-value"; //$NON-NLS-1$//$NON-NLS-2$

        if ( StringUtils.isNotBlank(env.getProperty(vcapPropertyName)) )
        {
            this.setZoneId(env.getProperty(vcapPropertyName));
        }
        log.info("Setting from Env-----" + toString()); //$NON-NLS-1$

    }

    /**
     * @return the injectionUri
     */
    public String getInjectionUri()
    {
        return this.injectionUri;
    }

    /**
     * @param injectionUri
     *            the injectionUri to set
     */
    public void setInjectionUri(String injectionUri)
    {
        this.injectionUri = injectionUri;
    }

    /**
     * @return the userName
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    

    /**
     * @param currentTime
     *            -
     * @return -
     * @throws NoSuchAlgorithmException
     *             -
     * @throws UnsupportedEncodingException
     *             -
     */
    public String getSHAToken(long currentTime)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String text = this.userName + this.password + currentTime;
        MessageDigest digest = MessageDigest.getInstance("SHA-256"); //$NON-NLS-1$
        byte[] secretHash = digest.digest(text.getBytes("UTF-8")); //$NON-NLS-1$
        String signature = String.format("%x", new BigInteger(1, secretHash)); //$NON-NLS-1$
        log.info("Signature Generated for ws:" + signature); //$NON-NLS-1$
        return signature;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Injection URL =" + this.injectionUri + ","); //$NON-NLS-1$//$NON-NLS-2$
        stringBuffer.append("userName = " + this.userName + ", "); //$NON-NLS-1$//$NON-NLS-2$
        stringBuffer.append("password = " + this.password + " "); //$NON-NLS-1$//$NON-NLS-2$
        return stringBuffer.toString();
    }


}
