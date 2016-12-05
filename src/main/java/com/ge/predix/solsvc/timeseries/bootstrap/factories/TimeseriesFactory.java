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

import java.util.List;

import org.apache.http.Header;

import com.ge.predix.entity.timeseries.aggregations.AggregationsList;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.timeseries.tags.TagsList;
import com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig;
import com.neovisionaries.ws.client.WebSocketAdapter;

/**
 * The main entry point for using the Time Series Bootstrap. Each method represents a major Time Series API.
 * 
 * @author 212438846
 *
 */
public interface TimeseriesFactory
{
	/**
	 * @return - 
	 */
	public List<Header> getTimeseriesHeaders();
	
	/**
	 * @param messageListener - method accepts custom message listener
	 * @since Predix Time Series API v1.0 Method to create connection to TS
	 *        Websocket to the configured TS Server List<Header> headers	  
	 */
	public void createConnectionToTimeseriesWebsocket(WebSocketAdapter messageListener);

	   /**
     * @param messageListener - method accepts custom message listener
     * @since Predix Time Series API v1.0 Method to create connection to TS
     *        Websocket to the configured TS Server List<Header> headers      
     */
    public void createConnectionToTimeseriesWebsocket();

    
    /**
	 * @since Predix Time Series API v1.0 Method to post data through Websocket
	 *        to the configured TS Server
	 * @param datapointsIngestion -
	 *            @see DatapointsIngestion
	 * 
	 */	
	public void postDataToTimeseriesWebsocket(DatapointsIngestion datapointsIngestion);

    /**
     * @since Predix Time Series API v1.0
     * @param uri @see TimeSeriesAPIV1
     * @param datapointsQuery @see DatapointsQuery
     * @param headers {@href https://github.com/PredixDev/predix-rest-client}
     * @return @see DatapointsResponse
     */
		
    /**
     * @param uri  -
     * @since Predix Time Series API v1.0
     * @param queryUrl @see TimeSeriesAPIV1
     * @param DatapointsQuery @see DatapointsQuery
     * @param headers {@href https://github.com/PredixDev/predix-rmd-ref-app}
     * @return @see DatapointsResponse
     */
    public DatapointsResponse queryForDatapoints(DatapointsQuery DatapointsQuery, List<Header> headers);

    /**
     * @since Predix Time Series API v1.0
     * @param queryUrl @see TimeSeriesAPIV1
     * @param latestDatapoints @see DatapointsLatestQuery
     * @param headers {@href https://github.com/PredixDev/predix-rmd-ref-app}
     * @return @see DatapointsResponse
     */

    public DatapointsResponse queryForLatestDatapoint(DatapointsLatestQuery latestDatapoints,
            List<Header> headers);
        
    /**
     * @since Predix Time Series API v1.0
     * @param queryUrl @see TimeSeriesAPIV1
       * @param headers {@href https://github.com/PredixDev/predix-rmd-ref-app}
     * @return @see TagsList
     */
    public TagsList listTags(List<Header> headers);
    
    /**
     * @since Predix Time Series API v1.0
     * @param queryUrl @see TimeSeriesAPIV1
       * @param headers {@href https://github.com/PredixDev/predix-rmd-ref-app}
     * @return @see AggregationsList
     */
    public AggregationsList listAggregations(List<Header> headers);

    /**
     * @param restConfig -
     * @param config -
     * @param tsConfig -
     */
    public void overrideConfig(ITimeseriesConfig tsConfig);

}
