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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author 212421693
 */
public class TimeseriesQuery {
	
	@SerializedName("sample_size")
	@Expose
	private Integer sampleSize;
	@Expose
	private List<TimeseriesResult> results = new ArrayList<TimeseriesResult>();

	/**
	* 
	* @return
	* The sampleSize
	*/
	public Integer getSampleSize() {
	return sampleSize;
	}

	/**
	* 
	* @param sampleSize
	* The sample_size
	*/
	public void setSampleSize(Integer sampleSize) {
	this.sampleSize = sampleSize;
	}

	/**
	* 
	* @return
	* The results
	*/
	public List<TimeseriesResult> getResults() {
	return results;
	}

	/**
	* 
	* @param results
	* The results
	*/
	public void setResults(List<TimeseriesResult> results) {
	this.results = results;
	}

}
