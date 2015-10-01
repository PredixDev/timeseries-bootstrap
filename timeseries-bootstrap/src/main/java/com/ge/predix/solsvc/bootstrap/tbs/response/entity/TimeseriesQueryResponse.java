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

/**
 * 
 * @author 212421693
 */
public class TimeseriesQueryResponse {
	
    @Expose
	private List<TimeseriesQuery> queries = new ArrayList<TimeseriesQuery>();

	/**
	 * 
	 */
	public TimeseriesQueryResponse()
	{
	}

	/**
	 * @return -
	 */
	public List<TimeseriesQuery> getQueries()
	{
		return this.queries;
	}

}
