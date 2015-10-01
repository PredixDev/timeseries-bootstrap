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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kairosdb.client.builder.DataPoint;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author 212421693
 */
public class InjectionBody {
	
		private String name;
		
		@SerializedName("datapoints")
		private List<DataPoint> dataPoints = new ArrayList<DataPoint>();

        private Map<String, String> attributes = new HashMap<String, String>();
        

        /**
		 * @param name -
		 */
		public InjectionBody(String name)
		{
			this.name = checkNotNullOrEmpty(name);
		}

		

		/**
		 * Adds a tag to the data point.
		 *
		 * @param name  tag identifier
		 * @param value tag value
		 * @return the InjectionBody the tag was added to
		 */
		public InjectionBody addAttributes(String name, String value)
		{
			checkNotNullOrEmpty(name);
			checkNotNullOrEmpty(value);
			this.attributes.put(name, value);

			return this;
		}

		/**
		 * Adds tags to the data point.
		 * @param tags map of tags
		 * @return the InjectionBody the tags were added to
		 */
		public InjectionBody addAttributes(Map<String, String> tags)
		{
			checkNotNull(tags);
			tags.putAll(tags);

			return this;
		}

		/**
		 * Adds the data point to the InjectionBody.
		 *
		 * @param timestamp when the measurement occurred
		 * @param value     the measurement value
		 * @return the InjectionBody
		 */
		public InjectionBody addDataPoint(long timestamp, long value)
		{
			this.dataPoints.add(new DataPoint(timestamp, value));
			return this;
		}

		/**
		 * Adds the data point to the InjectionBody with a timestamp of now.
		 *
		 * @param value the measurement value
		 * @return the InjectionBody
		 */
		public InjectionBody addDataPoint(long value)
		{
			return addDataPoint(System.currentTimeMillis(), value);
		}

		/**
		 * @param timestamp -
		 * @param value -
		 * @return -
		 */
		public InjectionBody addDataPoint(long timestamp, Object value)
		{
			this.dataPoints.add(new DataPoint(timestamp, value));
			return this;
		}

		/**
		 * Adds the data point to the InjectionBody.
		 *
		 * @param timestamp when the measurement occurred
		 * @param value     the measurement value
		 * @return the InjectionBody
		 */
		public InjectionBody addDataPoint(long timestamp, double value)
		{
			this.dataPoints.add(new DataPoint(timestamp, value));
			return this;
		}

		/**
		 * Adds the data point to the InjectionBody with a timestamp of now.
		 *
		 * @param value the measurement value
		 * @return the InjectionBody
		 */
		public InjectionBody addDataPoint(double value)
		{
			return addDataPoint(System.currentTimeMillis(), value);
		}

		/**
		 * @return -
		 */
		public List<DataPoint> getDataPoints()
		{
			return Collections.unmodifiableList(this.dataPoints);
		}

		/**
		 * Returns the InjectionBody name.
		 *
		 * @return InjectionBody name
		 */
		public String getName()
		{
			return this.name;
		}
		/**
		 * @return the attributes
		 */
		public Map<String, String> getAttributes() {
			return this.attributes;
		}

		/**
		 * @param attributes the attributes to set
		 */
		public void setAttributes(Map<String, String> attributes) {
			this.attributes = attributes;
		}

		


}
