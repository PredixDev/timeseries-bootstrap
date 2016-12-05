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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.timeseries.aggregations.AggregationsList;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.ingestionresponse.AcknowledgementMessage;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.timeseries.tags.TagsList;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.timeseries.bootstrap.api.TimeSeriesAPIV1;
import com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
/**
 * The main entry point for using the Time Series Bootstrap. Each method
 * represents a major Time Series API.
 * 
 * @author 212438846
 *
 */
@Component
@Scope("prototype")
public class TimeseriesFactoryImpl
        implements TimeseriesFactory
{
   
	private static Logger        log = LoggerFactory.getLogger(TimeseriesFactoryImpl.class);

    @Autowired
    private RestClient           restClient;

    @Autowired
    private WebSocketClient      wsClient;

    @Autowired
    private JsonMapper           jsonMapper;
      

    @Autowired
    @Qualifier("defaultTimeseriesConfig")   
    private ITimeseriesConfig timeseriesConfig;
    
    @Override
    public void overrideConfig(ITimeseriesConfig tsConfig){
        this.timeseriesConfig = tsConfig;
    	this.restClient.overrideRestConfig(tsConfig);
        this.wsClient.overrideWebSocketConfig(tsConfig);
    }
       
    
	@PostConstruct
	private void init() {
	    //
	}

    /**
     * @param messageListener
     *            - method accepts custom message listener
     * @since Predix Time Series API v1.0 Method to create connection to TS
     *        Websocket to the configured TS Server List<Header> headers
     */
    @SuppressWarnings("nls")
    @Override
    public void createConnectionToTimeseriesWebsocket(WebSocketAdapter messageListener)
    {
        try
        {
            WebSocketAdapter listener = messageListener;
            if ( listener == null )
            {
                listener = registerDefaultMessageListener();
            }
            List<Header> nullHeaders = null; 

            this.wsClient.init(this.restClient, nullHeaders, listener);
        }
        catch (Exception e)
        {
            log.error("Connection to websocket failed. " + e); 
            throw new RuntimeException("Connection to websocket failed. ", e); 
        }

    }

    /**
     * @since Predix Time Series API v1.0 Method to create connection to TS
     *        Websocket to the configured TS Server List<Header> headers
     */
    @SuppressWarnings("nls")
    @Override
    public void createConnectionToTimeseriesWebsocket()
    {
        try
        {
            WebSocketAdapter listener = null;
            createConnectionToTimeseriesWebsocket(listener);
        }
        catch (Exception e)
        {
            log.error("Connection to websocket failed. " + e); 
            throw new RuntimeException("Connection to websocket failed. ", e); 
        }

    }
    
    private WebSocketAdapter registerDefaultMessageListener()
    {
        WebSocketAdapter mListener = new WebSocketAdapter()
        {
            private JsonMapper jMapper = new JsonMapper();
            private Logger     logger  = LoggerFactory.getLogger(TimeseriesFactory.class);

            @Override
            public void onTextMessage(WebSocket wsocket, String message)
            {
                handleErrorMessage(message);
            }

            @Override
            public void onBinaryMessage(WebSocket wsocket, byte[] binary)
            {
                String message = new String(binary, StandardCharsets.UTF_8);
                handleErrorMessage(message);
            }

            /**
             * @param message
             */
            @SuppressWarnings("nls")
            private void handleErrorMessage(String message)
            {
                AcknowledgementMessage am = this.jMapper.fromJson(message, AcknowledgementMessage.class);
                if ( am.getStatusCode() > 299 && am.getStatusCode() < 400 )
                {
                    this.logger.info("STATUS CODE...." + am.getStatusCode() + "--ID:" + am.getMessageId());  //$NON-NLS-2$
                }
                else if ( am.getStatusCode() > 399 )
                {
                    this.logger.error("ERROR STATUS CODE...." + am.getStatusCode() + "--ID:" + am.getMessageId());  //$NON-NLS-2$
                }
                else
                {
                    this.logger.debug("SUCCESS...." + am.getStatusCode() + "--ID:" + am.getMessageId());  //$NON-NLS-2$

                }
            }
        };
        return mListener;
    }

    /**
     * @since Predix Time Series API v1.0 Method to post data through Websocket
     *        to the configured TS Server
     * @param datapointsIngestion
     *            -
     * @see DatapointsIngestion
     * 
     */
    @SuppressWarnings("nls")
    @Override
    public void postDataToTimeseriesWebsocket(DatapointsIngestion datapointsIngestion)
    {
        String request = this.jsonMapper.toJson(datapointsIngestion);
        log.debug(request);

        try
        {
            this.wsClient.postTextWSData(request);
        }
        catch (IOException | WebSocketException e)
        {
            throw new RuntimeException("Failed to post data to websocket. " + e); 
        }
    }

    /**
     * @since Predix Time Series API v1.0
     * @param uri
     *            -
     * @see TimeSeriesAPIV1
     * @param datapoints
     *            -
     * @see DatapointsQuery
     * @param headers
     *            {@href https://github.com/PredixDev/predix-rmd-ref-app}
     * @return @see DatapointsResponse
     */

    @SuppressWarnings("nls")
    @Override
    public DatapointsResponse queryForDatapoints(DatapointsQuery datapoints, List<Header> headers)
    {
        DatapointsResponse response = null;

        if ( datapoints == null )
        {
            log.debug("datapoints request obj is null"); 
            return response;
        }
        CloseableHttpResponse httpResponse = null;
        try
        {
            String request = this.jsonMapper.toJson(datapoints);
            log.debug(request);
            httpResponse = this.restClient.post(this.timeseriesConfig.getQueryUrl(),
                    this.jsonMapper.toJson(datapoints), headers,
                    this.timeseriesConfig.getDefaultConnectionTimeout(),
                    this.timeseriesConfig.getDefaultSocketTimeout());
            handleIfErrorResponse(httpResponse, headers);
            String responseEntity = processHttpResponseEntity(httpResponse.getEntity());
            if ( responseEntity == null ) return null;
            response = this.jsonMapper.fromJson(responseEntity, DatapointsResponse.class);
            return response;
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    "Error occured calling=" + this.timeseriesConfig.getQueryUrl() 
                            + " for query=" + datapoints + " with headers=" + headers,  //$NON-NLS-2$
                    e);
        }
        finally
        {
            if ( httpResponse != null ) try
            {
                httpResponse.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * @since Predix Time Series API v1.0
     * @param uri
     *            -
     * @see TimeSeriesAPIV1
     * @param datapoints
     *            -
     * @see DatapointsLatestQuery
     * @param headers
     *            {@href https://github.com/PredixDev/predix-rmd-ref-app}
     * @return @see DatapointsResponse
     */

    @SuppressWarnings("nls")
    @Override
    public DatapointsResponse queryForLatestDatapoint(DatapointsLatestQuery latestDatapoints,
            List<Header> headers)
    {
        DatapointsResponse response = null;

        if ( latestDatapoints == null )
        {
            log.debug("datapoints obj is null"); 
            return response;
        }
        CloseableHttpResponse httpResponse = null;
        String latestDatapointsUrl = this.timeseriesConfig.getQueryUrl().replace(TimeSeriesAPIV1.datapointsURI, TimeSeriesAPIV1.latestdatapointsURI);

        try
        {
            String request = this.jsonMapper.toJson(latestDatapoints);
            log.debug(request);
            httpResponse = this.restClient.post(latestDatapointsUrl,
                    this.jsonMapper.toJson(latestDatapoints), headers,
                    this.timeseriesConfig.getDefaultConnectionTimeout(),
                    this.timeseriesConfig.getDefaultSocketTimeout());
            handleIfErrorResponse(httpResponse, headers);
            String responseEntity = processHttpResponseEntity(httpResponse.getEntity());
            log.debug("Response from TS service = " + responseEntity); 
            if ( responseEntity == null ) return null;
            response = this.jsonMapper.fromJson(responseEntity, DatapointsResponse.class);
            return response;
        }
        catch (IOException e)
        {
            throw new RuntimeException("error occurred calling queryUrl=" 
                    + this.timeseriesConfig.getQueryUrl() + " for query=" + latestDatapoints 
                    + " with headers=" + headers, e); 
        }
        finally
        {
            if ( httpResponse != null ) try
            {
                httpResponse.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * @since Predix Time Series API v1.0
     * @param queryUrl
     *            -
     * @see TimeSeriesAPIV1
     * @param headers
     *            {@href https://github.com/PredixDev/predix-rmd-ref-app}
     * @return @see TagsResponse
     */
    @SuppressWarnings("nls")
    @Override
    public TagsList listTags(List<Header> headers)
    {
        TagsList responseTagList = null;
        CloseableHttpResponse httpResponse = null;
        String tagsUrl = this.timeseriesConfig.getQueryUrl().replace(TimeSeriesAPIV1.datapointsURI, TimeSeriesAPIV1.tagsURI);
        try
        {
            log.trace("listTags url=" + tagsUrl); 

            httpResponse = this.restClient.get(tagsUrl , headers,
                    this.timeseriesConfig.getDefaultConnectionTimeout(),
                    this.timeseriesConfig.getDefaultSocketTimeout());
            handleIfErrorResponse(httpResponse, headers);
            String responseEntity = processHttpResponseEntity(httpResponse.getEntity());
            log.debug("Response from TS service = " + responseEntity); 
            if ( responseEntity == null ) return null;
            responseTagList = this.jsonMapper.fromJson(responseEntity, TagsList.class);
            return responseTagList;
        }
        catch (IOException e)
        {
            throw new RuntimeException("error occurred calling tagsUrl=" 
                    + tagsUrl  
                    + " with headers=" + headers, e); 
        }
        finally
        {
            if ( httpResponse != null ) try
            {
                httpResponse.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

    }

    @SuppressWarnings("nls")
    @Override
    public AggregationsList listAggregations(List<Header> headers)
    {
        AggregationsList responseAggregationsList = null;
        CloseableHttpResponse httpResponse = null;
        String aggregationsUrl = this.timeseriesConfig.getQueryUrl().replace(TimeSeriesAPIV1.datapointsURI, TimeSeriesAPIV1.aggregationsURI);
        try
        {
            httpResponse = this.restClient.get(aggregationsUrl, headers,
                    this.timeseriesConfig.getDefaultConnectionTimeout(),
                    this.timeseriesConfig.getDefaultSocketTimeout());
            handleIfErrorResponse(httpResponse, headers);
            String responseEntity = processHttpResponseEntity(httpResponse.getEntity());
            log.debug("Response from TS service = " + responseEntity); 
            if ( responseEntity == null ) return null;
            responseAggregationsList = this.jsonMapper.fromJson(responseEntity, AggregationsList.class);
            return responseAggregationsList;
        }
        catch (IOException e)
        {
            throw new RuntimeException("error occurred calling aggregationsUrl=" 
                    + aggregationsUrl  
                    + " with headers=" + headers, e); 
        }
        finally
        {
            if ( httpResponse != null ) try
            {
                httpResponse.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("nls")
    private String processHttpResponseEntity(org.apache.http.HttpEntity entity)
            throws IOException
    {
        if ( entity == null ) return null;
        if ( entity instanceof GzipDecompressingEntity )
        {
            return IOUtils.toString(((GzipDecompressingEntity) entity).getContent(), "UTF-8"); 
        }
        return EntityUtils.toString(entity);
    }

    @SuppressWarnings("nls")
	private static void handleIfErrorResponse(CloseableHttpResponse httpResponse, List<Header> headers)
    {
        try {
			if ( httpResponse.getStatusLine() == null )
			{
			    log.info("No Status response was received. Locale:" 
			            + httpResponse.getLocale());
			    throw new RuntimeException("No Status response was received. Locale:" 
			            + httpResponse.getLocale());
			}
			if ( httpResponse.getStatusLine().getStatusCode() >= 300 )
			{
			    String body = (httpResponse == null) ? "Response body was empty" 
			            : EntityUtils.toString(httpResponse.getEntity());
				log.info("Query was unsuccessful. Status Code:" 
                        + httpResponse.getStatusLine().getStatusCode() + " body=" + body + " headers=" + headers);
			    throw new RuntimeException("Query was unsuccessful. Status Code:" 
			            + httpResponse.getStatusLine().getStatusCode() + " Response Body=" + body + " headers=" + headers);
			}
		}  catch (IOException e) {
			throw new RuntimeException("Unable to get response", e);
		}
    }
    
	@SuppressWarnings("nls")
    @Override
	public List<Header> getTimeseriesHeaders() {
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		this.restClient.addZoneToHeaders(headers, this.timeseriesConfig.getZoneId());
		headers.add(new BasicHeader("Origin", "http://localhost"));  //$NON-NLS-2$
		return headers;
	}


}
