/**
 * Copyright (c) 2017 Yodlee Inc. All Rights Reserved.
 * This software is the confidential and proprietary information of
 * Yodlee, Inc. Use is subject to license terms.
 *
 */
package com.kartik.aggration;

/**
 * Collection that stores the data related to event emitted by DB-Filer as part of ADD & EDIT flow.
 * This collection stores records on hourly basis.
 * 
 * Utilized & Populated by 
 * 		- {@link UserLevelProcessor}
 * 
 * Purged and Moved to {@link SpsUserStatsHist} by
 * 		- {@link UserStatsScheduler} 
 * 		- {@link AdminServiceController#updatePermissionData(String)}
 * 		- {@link AdminServiceController#deletePermissionData(String)}
 * 
 * @author DVedanta
 *
 */
public class SpsUserStats {
	public static String collectionName = "SPS_USER_STATS";
	
	public static String operationName = "operationName";
	
	public static String cobrandId = "cobrandId";
	
	public static String userId = "userId";
	
	public static String siteId = "siteId";
	
	public static String sumInfoId = "sumInfoId";
	
	public static String timestamp = "timestamp";
	
	public static String total = "total";
	
	public static String failure = "failure";
	
	public static String success = "success"; 

}
