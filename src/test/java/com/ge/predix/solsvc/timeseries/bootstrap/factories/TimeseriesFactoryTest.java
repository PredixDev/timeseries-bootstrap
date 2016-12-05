/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.timeseries.bootstrap.factories;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * 
 * @author 212438846
 */

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest(
{
        "server.port=0"
})
@ComponentScan("com.ge.predix.solsvc.restclient")
@ActiveProfiles("local")
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml"
})
@PropertySource("classpath:timeseries-config-test.properties")
public class TimeseriesFactoryTest
{

    private static Logger log = LoggerFactory.getLogger(TimeseriesFactoryTest.class);

    @Autowired
    private JsonMapper    jsonMapper;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * -
     */
    @Test
    public void testPolymorphicUnmarshal()
    {
        DatapointsIngestion dp = new DatapointsIngestion();
        String dpString = this.jsonMapper.toJson(dp);

        log.debug(dpString);

        DatapointsIngestion dp2 = this.jsonMapper.fromJson(dpString, DatapointsIngestion.class);
        Assert.notNull(dp2);

        ArrayList<DatapointsIngestion> list = new ArrayList<DatapointsIngestion>();
        list.add(dp);
        dpString = this.jsonMapper.toJson(list);
        log.debug(dpString);
        
        List<DatapointsIngestion> dp3 = this.jsonMapper.fromJsonArray(dpString, DatapointsIngestion.class);

        Assert.notNull(dp3);
    }

    
    /**
     * -
     */
    @SuppressWarnings("nls")
    @Test
    public void testUnmarshalWithStandardUnmarshaler()
    {
        DatapointsIngestion dp = new DatapointsIngestion();
        String dpString;
        dpString = "{\"messageId\":null,\"body\":[]}";

        DatapointsIngestion dp2 = this.jsonMapper.fromJson(dpString, DatapointsIngestion.class);
        Assert.notNull(dp2);

        
    }

}
