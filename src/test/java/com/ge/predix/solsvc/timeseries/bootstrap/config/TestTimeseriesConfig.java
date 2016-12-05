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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Properties needed to make rest calls to the Time Series instance
 * 
 * @author 212421693
 */
@Component("testTimeseriesConfig")
public class TestTimeseriesConfig extends DefaultTimeseriesConfig
        implements EnvironmentAware, ITimeseriesConfig
{
    /**
     * @param oauthIssuerId the oauthIssuerId to set
     */
    @Override
    @Value("${predix.timeseries2.oauth.issuerId.url}")
    public void setOauthIssuerId(String oauthIssuerId)
    {
        super.setOauthIssuerId(oauthIssuerId);
    }

    /**
     * @param oauthClientId the oauthClientId to set
     */
    @Override
    @Value("${predix.timeseries2.oauth.clientId:#{null}}")
    public void setOauthClientId(String oauthClientId)
    {
        super.setOauthClientId(oauthClientId);
    }

    /**
     * @param queryUrl the queryUrl to set
     */
    @Override
    @Value("${predix.test.timeseries.queryUrl}")
    public void setQueryUrl(String queryUrl)
    {
        super.setQueryUrl(queryUrl);
    }

    @Override
    /**
     * @param wsUri the wsUri to set
     */
    @Value("${predix.test.timeseries.websocket.uri}")
    public void setWsUri(String wsUri)
    {
        super.setWsUri(wsUri);
    }


    /**
     * @param zoneId -
     */
    @Override
    @Value("${predix.test.timeseries.zoneid}")
    public void setZoneId(String zoneId)
    {
        super.setZoneId(zoneId);
    }

 
    /**
     * The name of the VCAP property holding the name of the bound time series endpoint
     */
    private static final String TIME_SERIES2_VCAPS_NAME = "predix_timeseries2_name";   //$NON-NLS-1$
    

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
     * springframework.core.env.Environment)
     */
    @SuppressWarnings("nls")
    @Override
    public void setEnvironment(Environment env)
    {
        String vcapPropertyName = null;
        String tsName = env.getProperty(TIME_SERIES2_VCAPS_NAME); // this is set
                                                                 // on the
                                                                 // manifest
                                                                 // of the
                                                                 // application

        vcapPropertyName = null;
        vcapPropertyName = "vcap.services." + tsName + ".credentials.query.uri";
        if ( !StringUtils.isEmpty(env.getProperty(vcapPropertyName)) )
        {
            this.setQueryUrl(env.getProperty(vcapPropertyName));

        }

        vcapPropertyName = "vcap.services." + tsName + ".credentials.query.zone-http-header-value";
        if ( !StringUtils.isEmpty(env.getProperty(vcapPropertyName)) )
        {
            this.setZoneId(env.getProperty(vcapPropertyName));
        }
        
        // set ingest.uri
        if (StringUtils.isNotBlank(env.getProperty("vcap.services." + tsName + ".credentials.ingest.uri"))) //$NON-NLS-1$ //$NON-NLS-2$
        {
            this.setWsUri(env.getProperty("vcap.services." + tsName + ".credentials.ingest.uri")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

}
