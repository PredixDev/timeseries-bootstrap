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
import static com.google.common.base.Preconditions.checkState;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kairosdb.client.builder.RelativeTime;
import org.kairosdb.client.builder.TimeUnit;
import org.kairosdb.client.builder.TimeValidator;
import org.kairosdb.client.builder.aggregator.CustomAggregator;
import org.kairosdb.client.serializer.CustomAggregatorSerializer;
import org.kairosdb.client.serializer.ListMultiMapSerializer;
import org.kairosdb.client.serializer.OrderSerializer;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author 212421693
 */
public class TimeseriesQueryBuilder {
	
	@SerializedName("start_absolute")
	private Long startAbsolute;

	@SerializedName("end_absolute")
	private Long endAbsolute;

	@SerializedName("start_relative")
	private RelativeTime startRelative;

	@SerializedName("end_relative")
	private RelativeTime endRelative;

	private int cacheTime;
	private List<TimeseriesQueryTag> tags = new ArrayList<TimeseriesQueryTag>();
	private transient Gson mapper;

	private TimeseriesQueryBuilder()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(CustomAggregator.class, new CustomAggregatorSerializer());
		builder.registerTypeAdapter(ListMultimap.class, new ListMultiMapSerializer());
		builder.registerTypeAdapter(TimeseriesQueryTag.Order.class, new OrderSerializer());

		mapper = builder.create();
	}

	/**
	 * The beginning time of the time range.
	 *
	 * @param start start time
	 * @return the builder
	 */
	public TimeseriesQueryBuilder setStart(Date start)
	{
		checkNotNull(start);
		checkArgument(startRelative == null, "Both relative and absolute start times cannot be set.");

		this.startAbsolute = start.getTime();
		checkArgument(startAbsolute <= System.currentTimeMillis(), "Start time cannot be in the future.");
		return this;
	}

	/**
	 * The beginning time of the time range relative to now. For example, return all data points that starting 2 days
	 * ago.
	 *
	 * @param duration relative time value
	 * @param unit     unit of time
	 * @return the builder
	 */
	public TimeseriesQueryBuilder setStart(int duration, TimeUnit unit)
	{
		checkArgument(duration > 0);
		checkNotNull(unit);
		checkArgument(startAbsolute == null, "Both relative and absolute start times cannot be set.");

		startRelative = new RelativeTime(duration, unit);
		checkArgument(startRelative.getTimeRelativeTo(System.currentTimeMillis()) <= System.currentTimeMillis(), "Start time cannot be in the future.");
		return this;
	}

	/**
	 * The ending value of the time range. Must be later in time than the start time. An end time is not required
	 * and default to now.
	 *
	 * @param end end time
	 * @return the builder
	 */
	public TimeseriesQueryBuilder setEnd(Date end)
	{
		checkArgument(endRelative == null, "Both relative and absolute end times cannot be set.");
		this.endAbsolute = end.getTime();
		return this;
	}

	/**
	 * The ending time of the time range relative to now.
	 *
	 * @param duration relative time value
	 * @param unit     unit of time
	 * @return the builder
	 */
	public TimeseriesQueryBuilder setEnd(int duration, TimeUnit unit)
	{
		checkNotNull(unit);
		checkArgument(duration > 0);
		checkArgument(endAbsolute == null, "Both relative and absolute end times cannot be set.");
		endRelative = new RelativeTime(duration, unit);
		return this;
	}

	/**
	 * How long to cache this exact query. The default is to never cache.
	 *
	 * @param cacheTime cache time in milliseconds
	 * @return the builder
	 */
	public TimeseriesQueryBuilder setCacheTime(int cacheTime)
	{
		checkArgument(cacheTime > 0, "Cache time must be greater than 0.");
		this.cacheTime = cacheTime;
		return this;
	}

	/**
	 * Returns a new query builder.
	 *
	 * @return new query builder
	 */
	public static TimeseriesQueryBuilder getInstance()
	{
		return new TimeseriesQueryBuilder();
	}

	/**
	 * The metric to query for.
	 *
	 * @param name metric name
	 * @return the builder
	 */
	public TimeseriesQueryTag addTags(String name)
	{
		checkNotNullOrEmpty(name, "Name cannot be null or empty.");

		TimeseriesQueryTag metric = new TimeseriesQueryTag(name);
		tags.add(metric);
		return metric;
	}

	/**
	 * Returns the absolute range start time.
	 *
	 * @return absolute range start time
	 */
	public Date getStartAbsolute()
	{
		return new Date(startAbsolute);
	}

	/**
	 * Returns the absolute range end time.
	 *
	 * @return absolute range end time
	 */
	public Date getEndAbsolute()
	{
		return new Date(endAbsolute);
	}

	/**
	 * Returns the relative range start time.
	 *
	 * @return relative range start time
	 */
	public RelativeTime getStartRelative()
	{
		return startRelative;
	}

	/**
	 * Returns the relative range end time.
	 *
	 * @return relative range end time
	 */
	public RelativeTime getEndRelative()
	{
		return endRelative;
	}

	/**
	 * Returns the cache time.
	 *
	 * @return cache time
	 */
	public int getCacheTime()
	{
		return cacheTime;
	}

	/**
	 * Returns the list metrics to query for.
	 *
	 * @return metrics
	 */
	public List<TimeseriesQueryTag> getTags()
	{
		return tags;
	}

	/**
	 * Returns the JSON string built by the builder. This is the JSON that can be used by the client to query KairosDB
	 *
	 * @return JSON
	 * @throws IOException if the query is invalid and cannot be converted to JSON
	 */
	public String build() throws IOException
	{
		validateTimes();

		return mapper.toJson(this);
	}

	private void validateTimes()
	{
		checkState(startAbsolute != null || startRelative != null, "Start time must be specified");

		if (endAbsolute != null)
		{
			if (startAbsolute != null)
				TimeValidator.validateEndTimeLaterThanStartTime(startAbsolute, endAbsolute);
			else
				TimeValidator.validateEndTimeLaterThanStartTime(startRelative, endAbsolute);
			}
		else if (endRelative != null)
		{
			if (startAbsolute != null)
				TimeValidator.validateEndTimeLaterThanStartTime(startAbsolute, endRelative);
			else
				TimeValidator.validateEndTimeLaterThanStartTime(startRelative, endRelative);
		}
	}

}
