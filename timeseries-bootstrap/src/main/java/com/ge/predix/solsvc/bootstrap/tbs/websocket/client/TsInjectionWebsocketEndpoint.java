/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.bootstrap.tbs.websocket.client;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 212421693
 */
public class TsInjectionWebsocketEndpoint extends Endpoint {

	private static Logger log = LoggerFactory
			.getLogger(TsInjectionWebsocketEndpoint.class);

	private Session userSession = null;

	/**
	 * @return the userSession
	 */
	public Session getUserSession() {
		return this.userSession;
	}

	/**
	 * @param userSession
	 *            the userSession to set
	 */
	public void setUserSession(Session userSession) {
		this.userSession = userSession;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Endpoint#onOpen(javax.websocket.Session,
	 * javax.websocket.EndpointConfig)
	 */
	@Override
	public void onOpen(final Session session, EndpointConfig config) {
		this.setUserSession(session);

		/*log.info("Connected ... " + session.getId()); //$NON-NLS-1$
		//session.getAsyncRemote().sendText(
			//	(String) config.getUserProperties().get("payload")); //$NON-NLS-1$
		try {
			session.getBasicRemote().sendText(
					(String) config.getUserProperties().get("payload"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //$NON-NLS-1$
		log.info("Send ... " + (String) config.getUserProperties().get("payload")); //$NON-NLS-1$ //$NON-NLS-2$
*/		
	}
	/**
     * Defines the behavior of the client message handler when the session
     * is closed.
     * 
     * @param session The web socket session
     * @param closeReason Provides information on the session close including
     *            close reason phrase, code, etc...
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason)
    {
        log.info("Client: Session " + session.getId() + " closed because of " + closeReason.getReasonPhrase()); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
