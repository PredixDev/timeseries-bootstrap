/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.timeseries.bootstrap.client;

import java.sql.Timestamp;
import java.util.List;

import org.apache.http.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.timeseries.datapoints.ingestionresponse.AcknowledgementMessage;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.config.TimeseriesConfigFactory;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

/**
 * 
 * 
 * @author 212438846
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("local")
@ContextConfiguration(locations = { 
		"classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml" })
@PropertySource("classpath:timeseries-config-test.properties")
public class TimeseriesClientRuntimeIT {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(TimeseriesClientIT.class);
	
	/**
	 * 
	 */
	@Autowired
	protected TimeseriesClient timeseriesClient;

	//we say 'runtimeConfig' but to keep our test cases tidy we read from the properties
	@Value("${predix.oauth.issuerId.url:#{null}}")
	private String oauthIssuerId;
	@Value("${predix.oauth.clientId:#{null}}")
	private String oauthClientId;
    @Value("${predix.timeseries.queryUrl}")
    private String             queryUrl;
    @Value("${predix.timeseries.websocket.uri}")
    private String             wsUri;
    @Value("${predix.timeseries.zoneid:#{null}}")
    private String             zoneId;


	/**
	 * -
	 */
	@Test
	public void runAllTest() {
		boolean encodeClientId = true;
		ITimeseriesConfig testConfig = TimeseriesConfigFactory.zoneEndpointClientCredentials(this.queryUrl, this.wsUri, this.zoneId, this.oauthIssuerId, this.oauthClientId, encodeClientId);
		this.timeseriesClient.overrideConfig(testConfig);

		//this.timeseriesClient.overrideConfig(this.tsConfig);
		List<Header> headers = this.timeseriesClient.getTimeseriesHeaders();

		WebSocketAdapter mListener = new WebSocketAdapter() {
			private JsonMapper jMapper = new JsonMapper();
			private Logger logger = LoggerFactory.getLogger(TimeseriesClient.class);

			@Override
			public void onTextMessage(WebSocket wsocket, String message) {
				AcknowledgementMessage am = this.jMapper.fromJson(message, AcknowledgementMessage.class);
				this.logger.debug(
						"Msg " + am.getMessageId() + " RECEIVED at " + new Timestamp(System.currentTimeMillis())); //$NON-NLS-1$ //$NON-NLS-2$

			}
		};
		this.timeseriesClient.createTimeseriesWebsocketConnectionPool(mListener);
		TimeseriesClientIT.createMetrics(this.timeseriesClient);
		try {
			Thread.sleep(1000); // / due to delay in Injection pipeline and
								// query
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		TimeseriesClientIT.getTags(this.timeseriesClient, headers);
		TimeseriesClientIT.queryForDatapoints(this.timeseriesClient,headers);

	}

	/**
	 * 
	 */
	@Test
	public void runIDataInjectionTest() {
		boolean encodeClientId = true;
		ITimeseriesConfig testConfig = TimeseriesConfigFactory.zoneEndpointClientCredentials(this.queryUrl, this.wsUri, this.zoneId, this.oauthIssuerId, this.oauthClientId, encodeClientId);
		this.timeseriesClient.overrideConfig(testConfig);

		List<Header> headers = this.timeseriesClient.getTimeseriesHeaders();
		WebSocketAdapter nullListener = null;
		this.timeseriesClient.createTimeseriesWebsocketConnectionPool(nullListener);
		TimeseriesClientIT.createMetrics(this.timeseriesClient);
		try {
			Thread.sleep(1000); // / due to delay in Injection pipeline and
								// query
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		// getTags(headers);
		// getAggregations(headers);
		TimeseriesClientIT.queryForDatapoints(this.timeseriesClient, headers);
		// queryForDatapointsWithMilliSecsAsStartTime(headers);
		// queryForDatapointsAndOrder(headers);
		// queryForLatestDatapoints(headers);
	}

}
