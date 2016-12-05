/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.timeseries.bootstrap.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.websocket.config.DefaultWebSocketConfigForTimeseries;

/**
 * Properties needed to make rest calls to the Time Series instance
 * 
 * To set up a 2nd Time series Client and Config. 
 * 1. extend this class
 * 2. create setters for the items you want to override
 * 3. add @Value annotations with the new property names
 * 4. ensure the 2nd config class does not call super.setEnvironment() (meaning does not call the setEnvironment() in this class)
 * 5. set the properties via the environment (VCAP) variables of the 2nd time series
 * 6. you may bind to a time series not in the same space by creating a CUPS entry and binding to that
 * 
 * @author 212421693
 */
@Component("defaultTimeseriesConfig")
@Scope("prototype")
public class DefaultTimeseriesConfig extends DefaultWebSocketConfigForTimeseries
        implements EnvironmentAware, ITimeseriesConfig
{
    private static Logger log = LoggerFactory.getLogger(DefaultTimeseriesConfig.class);

    @Value("${predix.timeseries.queryUrl}")
    private String             queryUrl;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
     * springframework.core.env.Environment)
     */
    @SuppressWarnings("nls")
    @Override
    public void setEnvironment(Environment env)
    {
        super.setEnvironment(env);
        String vcapPropertyName = null;
        String tsName = env.getProperty(TIME_SERIES_VCAPS_NAME); // this is set
                                                                 // on the
                                                                 // manifest
                                                                 // of the
                                                                 // application

        vcapPropertyName = null;
        vcapPropertyName = "vcap.services." + tsName + ".credentials.query.uri";
        if ( !StringUtils.isEmpty(env.getProperty(vcapPropertyName)) )
        {
            this.queryUrl = env.getProperty(vcapPropertyName);

        }
        log.info("DefaultTimeseriesConfig=" + this.toString());

    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig#getQueryUrl()
     */
    @Override
    public String getQueryUrl()
    {
        return this.queryUrl;
    }

    /**
     * you may override the setter with an @value annotated property

     * @param queryUrl -
     */
    public void setQueryUrl(String queryUrl)
    {
        this.queryUrl = queryUrl;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "DefaultTimeseriesConfig [queryUrl=" + this.queryUrl + "] " + super.toString();
    }

    
}
