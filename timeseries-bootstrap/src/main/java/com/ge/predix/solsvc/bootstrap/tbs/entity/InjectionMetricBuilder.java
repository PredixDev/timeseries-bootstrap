/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.bootstrap.tbs.entity;

import java.io.IOException;

import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.builder.aggregator.CustomAggregator;
import org.kairosdb.client.builder.grouper.CustomGrouper;
import org.kairosdb.client.serializer.CustomAggregatorSerializer;
import org.kairosdb.client.serializer.CustomGrouperSerializer;
import org.kairosdb.client.serializer.DataPointSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author 212421693
 */
public class InjectionMetricBuilder {

	private InjectionMetric injectionRequest ;
	private transient Gson mapper;

	private InjectionMetricBuilder()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(CustomAggregator.class, new CustomAggregatorSerializer());
		builder.registerTypeAdapter(CustomGrouper.class, new CustomGrouperSerializer());
		builder.registerTypeAdapter(DataPoint.class, new DataPointSerializer());
		this.mapper = builder.create();
	}

	/**
	 * Returns a new metric builder.
	 *
	 * @return metric builder
	 */
	public static InjectionMetricBuilder getInstance()
	{
		return new InjectionMetricBuilder();
	}

	/**
	 * Adds a metric to the builder.
	 * @param metrics metric name
	 *
	 * 
	 */
	public void addMetrics(InjectionMetric metrics)
	{
		this.injectionRequest = metrics;
		
	}

		

	/**
	 * Returns the JSON string built by the builder. This is the JSON that can be used by the client add metrics.
	 *
	 * @return JSON
	 * @throws IOException if metrics cannot be converted to JSON
	 */
	public String build() throws IOException
	{
		return this.mapper.toJson(this.injectionRequest);
	}

	/**
	 * @return the injectionRequest
	 */
	public InjectionMetric getInjectionRequest() {
		return this.injectionRequest;
	}

	/**
	 * @param injectionRequest the injectionRequest to set
	 */
	public void setInjectionRequest(InjectionMetric injectionRequest) {
		this.injectionRequest = injectionRequest;
	}
	

}
