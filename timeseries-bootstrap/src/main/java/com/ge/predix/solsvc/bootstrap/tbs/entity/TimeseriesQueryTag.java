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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.kairosdb.client.builder.Aggregator;
import org.kairosdb.client.builder.Grouper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author 212421693
 */
public class TimeseriesQueryTag {
	public enum Order
	{
		ASCENDING("asc"),
		DESCENDING("desc");

		private String text;

		Order(String text)
		{
			this.text = text;
		}

		@Override
		public String toString()
		{
			return this.text;
		}
	}

	@SuppressWarnings("unused")
	private final String name;

	private final ListMultimap<String, String> attributes = ArrayListMultimap.create();

	@SerializedName("group_by")
	private final List<Grouper> groupers = new ArrayList<Grouper>();

	private final List<Aggregator> aggregators = new ArrayList<Aggregator>();

	@SuppressWarnings("unused")
	private Integer limit;

	@SuppressWarnings("unused")
	private Order order;

	/**
	 * @param name -
	 */
	TimeseriesQueryTag(String name)
	{
		this.name = checkNotNullOrEmpty(name);
	}

	/**
	 * Add a map of attributes.
	 *
	 * @param attributes attributes to add
	 * @return the metric
	 */
	public TimeseriesQueryTag addMultiValuedattributes(Map<String, List<String>> attributes)
	{
		checkNotNull(attributes);

		for (String key : attributes.keySet())
		{
			this.attributes.putAll(key, attributes.get(key));
		}

		return this;
	}

	/**
	 * Add a map of attributes. This narrows the query to only show data points associated with the attributes' values.
	 *
	 * @param attributes attributes to add
	 * @return the metric
	 */
	public TimeseriesQueryTag addattributes(Map<String, String> attributes)
	{
		checkNotNull(attributes);

		for (String key : attributes.keySet())
		{
			this.attributes.put(key, attributes.get(key));
		}

		return this;
	}

	/**
	 * Adds a Attributes with multiple values. This narrows the query to only show data points associated with the attributes values.
	 *
	 * @param name   tag name
	 * @param values tag values
	 * @return the metric
	 */
	public TimeseriesQueryTag addAttribute(String name, String... values)
	{
		checkNotNullOrEmpty(name);
		checkArgument(values.length > 0);

		for (String value : values)
		{
			checkNotNullOrEmpty(value);
		}

		attributes.putAll(name, Arrays.asList(values));

		return (this);
	}

	/**
	 * Adds an aggregator to the metric.
	 *
	 * @param aggregator aggregator to add
	 * @return the metric
	 */
	public TimeseriesQueryTag addAggregator(Aggregator aggregator)
	{
		checkNotNull(aggregator);
		aggregators.add(aggregator);
		return this;
	}

	/**
	 * Add a grouper to the metric.
	 *
	 * @param grouper grouper to add
	 * @return the metric
	 */
	public TimeseriesQueryTag addGrouper(Grouper grouper)
	{
		checkNotNull(grouper);

		groupers.add(grouper);
		return this;
	}

	/**
	 * Limits the number of data point returned from the query. The limit is done before aggregators are executed.
	 * @param limit maximum number of data points to return
	 */
	public void setLimit(int limit)
	{
		checkArgument(limit > 0, "limit must be greater than 0");
		this.limit = limit;
	}

	/**
	 * Orders the data points. The server default is ascending.
	 * @param order how data points are sorted
	 */
	public void setOrder(Order order)
	{
		checkNotNull(order);
		this.order = order;
	}

}
