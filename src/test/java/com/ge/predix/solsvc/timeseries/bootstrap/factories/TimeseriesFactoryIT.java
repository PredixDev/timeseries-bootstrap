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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.timeseries.aggregations.AggregationsList;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.ingestionresponse.AcknowledgementMessage;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.timeseries.tags.TagsList;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

/**
 * 
 * 
 * @author 212438846
 */

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest({ "server.port=0" })
@ComponentScan("com.ge.predix.solsvc.restclient")
@ActiveProfiles("local")
@ContextConfiguration(locations = {
		"classpath*:META-INF/spring/ext-util-scan-context.xml",
		"classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
		"classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml" })
@PropertySource("classpath:timeseries-config-test.properties")
public class TimeseriesFactoryIT{

    private static Logger log = LoggerFactory.getLogger(TimeseriesFactoryIT.class);

    /**
     * tsConfig - 
     */
    @Autowired
    @Qualifier("defaultTimeseriesConfig")
    ITimeseriesConfig tsConfig;
    
    

    
	/**
	 * 
	 */
	@Autowired
	protected TimeseriesFactory timeseriesFactory;


	/**
	 * -
	 */
	@Test
	public void runAllTest() {
		this.timeseriesFactory.overrideConfig(this.tsConfig);
		
		List<Header> headers = this.timeseriesFactory.getTimeseriesHeaders();
		
		WebSocketAdapter mListener = new WebSocketAdapter() {
			private JsonMapper jMapper = new JsonMapper();
			private Logger logger = LoggerFactory
					.getLogger(TimeseriesFactory.class);

			@Override
			public void onTextMessage(WebSocket wsocket, String message) {
				AcknowledgementMessage am = this.jMapper.fromJson(message,
						AcknowledgementMessage.class);
				this.logger.debug("Msg "+am.getMessageId()+" RECEIVED at "+new Timestamp(System.currentTimeMillis())); //$NON-NLS-1$ //$NON-NLS-2$

			}		
		};
		this.timeseriesFactory.createConnectionToTimeseriesWebsocket(mListener);
		createMetrics();
		try {
			Thread.sleep(1000); // / due to delay in Injection pipeline and
								// query
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		getTags(headers);
		getAggregations(headers);
		queryForDatapoints(headers);
		queryForDatapointsWithMilliSecsAsStartTime(headers);
		queryForDatapointsAndOrder(headers);
		queryForLatestDatapoints(headers);
	}
	
	
	/**
	 * 
	 */
	@Test
	public void runIDataInjectionTest() {
		this.timeseriesFactory.overrideConfig(this.tsConfig);

		List<Header> headers =  this.timeseriesFactory.getTimeseriesHeaders();
		WebSocketAdapter nullListener = null;
        this.timeseriesFactory.createConnectionToTimeseriesWebsocket(nullListener );
		createMetrics();
		try {
			Thread.sleep(1000); // / due to delay in Injection pipeline and
								// query
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
//		getTags(headers);
//		getAggregations(headers);
		queryForDatapoints(headers);
//		queryForDatapointsWithMilliSecsAsStartTime(headers);
//		queryForDatapointsAndOrder(headers);
//		queryForLatestDatapoints(headers);
	}


	@SuppressWarnings({ "unchecked" })
	private void createMetrics() {
		for (int i = 0; i < 10; i++) {
			DatapointsIngestion dpIngestion = new DatapointsIngestion();
			dpIngestion.setMessageId(String.valueOf(System.currentTimeMillis()));

			Body body = new Body();
			body.setName("RMD_metric2"); //$NON-NLS-1$
			List<Object> datapoint1 = new ArrayList<Object>();
			datapoint1.add(System.currentTimeMillis());
			datapoint1.add(10);
			datapoint1.add(3); // quality

			List<Object> datapoint2 = new ArrayList<Object>();
			datapoint2.add(System.currentTimeMillis());
			datapoint2.add(10);
			datapoint2.add(1); // quality

			List<Object> datapoints = new ArrayList<Object>();
			datapoints.add(datapoint1);
			datapoints.add(datapoint2);

			body.setDatapoints(datapoints);

			com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
			map.put("host", "server1"); //$NON-NLS-1$ //$NON-NLS-2$
			map.put("customer", "Acme"); //$NON-NLS-1$ //$NON-NLS-2$

			body.setAttributes(map);

			List<Body> bodies = new ArrayList<Body>();
			bodies.add(body);

			dpIngestion.setBody(bodies);
			this.timeseriesFactory.postDataToTimeseriesWebsocket(dpIngestion);
			log.debug("Msg "+dpIngestion.getMessageId()+" POSTED at "+new Timestamp(System.currentTimeMillis())); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}


	private void getTags(List<Header> headers) {
		TagsList tags = this.timeseriesFactory.listTags(headers);
		for (String tag : tags.getResults()) {
			if ("RMD_metric2".equals(tag)) { //$NON-NLS-1$
				return;
			}
		}
		fail("Expected tag (RMD_metric2) not found"); //$NON-NLS-1$
	}

	private void getAggregations(List<Header> headers) {
		AggregationsList aggregations = this.timeseriesFactory.listAggregations(headers);
		for (@SuppressWarnings("rawtypes") Map aggregation : aggregations.getResults()) {
			if ("max".equals(aggregation.get("name"))) { //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}
		fail("Expected aggregation (max) not found"); //$NON-NLS-1$
	}

	@SuppressWarnings({ "unchecked" })
	private void queryForDatapoints(List<Header> headers) {
		com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery datapoints = new com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery();
		datapoints.setStart("1y-ago"); //$NON-NLS-1$

		com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
		tag.setName("RMD_metric2"); //$NON-NLS-1$
		
		List<String> list = new ArrayList<String>();
		list.add("server1"); //$NON-NLS-1$
		com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
		map.put("host", list); //$NON-NLS-1$

		com.ge.predix.entity.timeseries.datapoints.queryrequest.Filters filters = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Filters();
		filters.setAttributes(map);

		com.ge.predix.entity.timeseries.datapoints.queryrequest.Measurements measurements = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Measurements();
		measurements.setCondition("le"); //$NON-NLS-1$
		List<Integer> values = new ArrayList<Integer>();
		values.add(36);
		measurements.setValues(values);
		filters.setMeasurements(measurements);
		com.ge.predix.entity.timeseries.datapoints.queryrequest.Qualities qualities = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Qualities();
		List<String> qualValues = new ArrayList<String>();
		qualValues.add("3"); //$NON-NLS-1$
		qualities.setValues(qualValues);
		filters.setQualities(qualities);

		tag.setFilters(filters);

		com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation agg = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation();
		agg.setInterval("2d"); //$NON-NLS-1$
		agg.setType("avg"); //$NON-NLS-1$
		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation> aggs = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation>();
		aggs.add(agg);
		tag.setAggregations(aggs);

		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag>();
		tags.add(tag);
		datapoints.setTags(tags);
		DatapointsResponse response = this.timeseriesFactory.queryForDatapoints(datapoints, headers);
		List<String> value = (List<String>) response.getTags().get(0).getResults().get(0).getAttributes()
				.get("customer"); //$NON-NLS-1$
		assertEquals("Acme", value.get(0)); //$NON-NLS-1$
		value = (List<String>) response.getTags().get(0).getResults().get(0).getAttributes().get("host"); //$NON-NLS-1$
		assertEquals("server1", value.get(0)); //$NON-NLS-1$
	}

	@SuppressWarnings({ "unchecked" })
	private void queryForDatapointsWithMilliSecsAsStartTime(List<Header> headers) {
		com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery datapoints = new com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery();
		datapoints.setStart(1427463525000d);

		com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
		tag.setName("RMD_metric2"); //$NON-NLS-1$

		com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation agg = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation();
		agg.setInterval("2d"); //$NON-NLS-1$
		agg.setType("avg"); //$NON-NLS-1$
		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation> aggs = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation>();
		aggs.add(agg);
		tag.setAggregations(aggs);

		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag>();
		tags.add(tag);
		datapoints.setTags(tags);
		DatapointsResponse response = this.timeseriesFactory.queryForDatapoints(datapoints, headers);
		log.debug(response.toString());
		List<String> value = (List<String>) response.getTags().get(0).getResults().get(0).getAttributes()
				.get("customer"); //$NON-NLS-1$
		assertEquals("Acme", value.get(0)); //$NON-NLS-1$
		value = (List<String>) response.getTags().get(0).getResults().get(0).getAttributes().get("host"); //$NON-NLS-1$
		assertEquals("server1", value.get(0)); //$NON-NLS-1$
	}

	@SuppressWarnings({ "unchecked" })
	private void queryForDatapointsAndOrder(List<Header> headers) {
		com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery datapoints = new com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery();
		datapoints.setStart("1y-ago"); //$NON-NLS-1$

		com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
		tag.setName("RMD_metric2"); //$NON-NLS-1$
		tag.setOrder("desc"); //$NON-NLS-1$

		com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation agg = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation();
		agg.setInterval("2d"); //$NON-NLS-1$
		agg.setType("avg"); //$NON-NLS-1$
		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation> aggs = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation>();
		aggs.add(agg);
		tag.setAggregations(aggs);

		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag>();
		tags.add(tag);
		datapoints.setTags(tags);
		com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse response = this.timeseriesFactory
				.queryForDatapoints(datapoints, headers);
		List<String> value = (List<String>) response.getTags().get(0).getResults().get(0).getAttributes()
				.get("customer"); //$NON-NLS-1$
		assertEquals("Acme", value.get(0)); //$NON-NLS-1$
		value = (List<String>) response.getTags().get(0).getResults().get(0).getAttributes().get("host"); //$NON-NLS-1$
		assertEquals("server1", value.get(0)); //$NON-NLS-1$
	}

	@SuppressWarnings({ "unchecked" })
	private void queryForLatestDatapoints(List<Header> headers) {
		com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery datapoints = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery();
		com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag();
		tag.setName("RMD_metric2"); //$NON-NLS-1$

		List<String> list = new ArrayList<String>();
		list.add("server1"); //$NON-NLS-1$
		com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
		map.put("host", list); //$NON-NLS-1$

		com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Filters filters = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Filters();
		filters.setAttributes(map);

		com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Measurements measurements = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Measurements();
		measurements.setCondition("le"); //$NON-NLS-1$
		List<Integer> values = new ArrayList<Integer>();
		values.add(36);
		measurements.setValues(values);
		filters.setMeasurements(measurements);
		com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Qualities qualities = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Qualities();
		List<String> qualValues = new ArrayList<String>();
		qualValues.add("3"); //$NON-NLS-1$
		qualities.setValues(qualValues);
		filters.setQualities(qualities);

		tag.setFilters(filters);
		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag> tagList = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag>();
		tagList.add(tag);
		datapoints.setTags(tagList);
		com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse response = this.timeseriesFactory
				.queryForLatestDatapoint(datapoints, headers);
		assertNotNull(response);
	}

}
