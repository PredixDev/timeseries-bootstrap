/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.bootstrap.tsb.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.kairosdb.client.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.bootstrap.tbs.entity.InjectionMetricBuilder;
import com.ge.predix.solsvc.bootstrap.tbs.entity.InjectionResponse;
import com.ge.predix.solsvc.bootstrap.tbs.websocket.client.AuthorizationConfigurator;
import com.ge.predix.solsvc.bootstrap.tbs.websocket.client.TsInjectionWebsocketEndpoint;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.google.gson.Gson;

/**
 * 
 * @author 212421693
 */
@Component
public class TimeseriesWebsocketClient
{

    private static Logger        log     = LoggerFactory.getLogger(TsInjectionWebsocketEndpoint.class);

    @Autowired
    private RestClient           restClient;
    @Autowired
    private TimeseriesRestConfig tsRestConfig;
    @Autowired
    private TimeseriesWSConfig   websocketConfig;

    WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    private static Map<String,Session> sessions =  new HashMap<String,Session>();

    /**
     * 
     * @param tsRestConfig -
     * @param websocketConfig
     *            -
     */
    public TimeseriesWebsocketClient()
    {
        
    }

    /**
     * 
     * @param builder
     *            -
     * @return -
     * @throws URISyntaxException
     *             -
     * @throws IOException
     *             -
     */
    public Response pushMetrics(InjectionMetricBuilder builder)
            throws URISyntaxException, IOException
    {
        checkNotNull(builder);
        Response response = postWSData(builder.build());
        return response;
    }

    /**
     * 
     * @param json
     *            -
     * @return -
     * @throws URISyntaxException
     *             -
     * @throws IOException -
     */
    protected Response postWSData(String json)
            throws URISyntaxException, IOException
    {
        final Response statusResponse = new Response();
        Session session = sessions.get("messages");
        log.debug("session : "+session ); //$NON-NLS-1$
        if (session == null || !session.isOpen()) 
        {
            log.debug("is session not open" ); //$NON-NLS-1$
        }
        else
        {
            log.debug("Reusing connection" +session.getId()); //$NON-NLS-1$
        }

        if (session == null || !session.isOpen())
        {	
            try
            {
                long currentTime = (System.currentTimeMillis() / 1000) + 60;
                List<Header> headers = this.restClient.getSecureTokenForClientId();
                headers.add(new BasicHeader(this.websocketConfig.getPredixZoneIdHeaderName(), this.websocketConfig.getZoneId()));
                session = this.container.connectToServer(new TsInjectionWebsocketEndpoint(),
                        new AuthorizationConfigurator(headers, this.getWebsocketConfig(), json, currentTime), new URI(
                                this.getWebsocketConfig().getInjectionUri()));
                // process return response after send messaging
                session.setMaxIdleTimeout(0);
                
                sessions.put("messages", session);
                session.addMessageHandler(new MessageHandler.Whole<String>()
                {
                    @SuppressWarnings("synthetic-access")
                    @Override
                    public void onMessage(String response)
                    {
                        log.debug("On Message connecting to websocket ... " + response); //$NON-NLS-1$
                        try {
							InjectionResponse injectionResponse = new Gson().fromJson(response, InjectionResponse.class);
							statusResponse.setStatusCode(injectionResponse.getStatusCode().intValue());
						} catch (Exception e) {
							log.error("Error when parsing the response: ",e);
						}
                    }
                });
            }
            catch (DeploymentException | IOException e)
            {
                log.error("Errror connecting to websocket ... " + session, e); //$NON-NLS-1$
                throw new RuntimeException(e);
            }
        }
        session.getBasicRemote().sendText(json); //$NON-NLS-1$
        sessions.put("messages", session);
        return statusResponse;

    }

    /**
     * @return the websocketConfig
     */
    public TimeseriesWSConfig getWebsocketConfig()
    {
        return this.websocketConfig;
    }

    /**
     * @param websocketConfig
     *            the websocketConfig to set
     */
    public void setWebsocketConfig(TimeseriesWSConfig websocketConfig)
    {
        this.websocketConfig = websocketConfig;
    }

}
