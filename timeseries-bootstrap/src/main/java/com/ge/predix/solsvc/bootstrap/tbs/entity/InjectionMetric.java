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

import java.util.ArrayList;
import java.util.List;

/**
 * This has the messageID
 * @author 212421693
 */
public class InjectionMetric {
	private Long messageId ;
	private List<InjectionBody> body = new ArrayList<InjectionBody>();
	
	/**
	 * @param messageId -
	 */
	public InjectionMetric(Long messageId) {
		this.messageId=messageId;
	}
	/**
	 * @return the messageId
	 */
	public Long getMessageId() {
		return this.messageId;
	}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
	/**
	 * @return the body
	 */
	public List<InjectionBody> getBody() {
		return this.body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(List<InjectionBody> body) {
		this.body = body;
	}
	
	

}
