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

/**
 * 
 * @author 212421693
 */
public class InjectionResponse 	{
	/**
	 * 
	 */
	Long messageId;
	/**
	 * 
	 */
	Long statusCode;
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
	 * @return the statusCode
	 */
	public Long getStatusCode() {
		return this.statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(Long statusCode) {
		this.statusCode = statusCode;
	}
	
	
	

}
