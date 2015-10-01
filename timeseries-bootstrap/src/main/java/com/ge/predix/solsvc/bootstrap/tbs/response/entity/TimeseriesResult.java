/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.bootstrap.tbs.response.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kairosdb.client.response.GroupResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author 212421693
 */
public class TimeseriesResult {

    private String name;

    @SerializedName("group_by")
    private List<GroupResult> groupResults;

    @Expose
	private List<List<Double>> values =new ArrayList<List<Double>>();

    @SerializedName("attributes")
    private Map<String, List<String>> attributes;
    

	/**
	 * @param name2 - 
	 * @param attributes2 - 
	 * @param values -
	 * @param dataPoints2 - 
	 * @param groupResults2 -
	 */
	public TimeseriesResult(String name2,
			Map<String, List<String>> attributes2, List<List<Double>> values,
			List<GroupResult> groupResults2) {
		this.name = name2;
		this.attributes =attributes2;
		this.values = values;
		
	}

	/**
	 * @return the attributes
	 */
	public Map<String, List<String>> getAttributes() {
		return this.attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, List<String>> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the groupResults
	 */
	public List<GroupResult> getGroupResults() {
		return this.groupResults;
	}

	/**
	 * @param groupResults the groupResults to set
	 */
	public void setGroupResults(List<GroupResult> groupResults) {
		this.groupResults = groupResults;
	}

	/**
	 * @return the values
	 */
	public List<List<Double>> getValues() {
		return this.values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<List<Double>> values) {
		this.values = values;
	}

}
