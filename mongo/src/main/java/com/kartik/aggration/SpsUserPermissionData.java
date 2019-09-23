/**
 * Copyright (c) 2017 Yodlee Inc. All Rights Reserved.
 * This software is the confidential and proprietary information of
 * Yodlee, Inc. Use is subject to license terms.
 *
 */
package com.kartik.aggration;

/**
 * Collection that stores permission data @ user level for both site & sum info.
 * 
 * Utilized by
 * 		- {@link AdminServiceController#readPermissionData(String)}
 * 		- {@link OperationServiceController#operationPermissible(String)}
 * 
 * Updated by
 * 		- {@link AdminServiceController#updatePermissionData(String)} 
 * 		- {@link UserLevelProcessor#processForSite(long, long, long, String, long)}
 * 		- {@link UserLevelProcessor#processForSumInfo(long, long, long, String, long)}
 * 
 * Deleted by
 * 		- {@link AdminServiceController#deletePermissionData(String)}
 * 		- {@link PermissionDataScheduler} periodically for expired data
 *  
 * @author DVedanta
 *
 */
public class SpsUserPermissionData {
	
	public static String collectionName = "SPS_USER_PERMISSION_DATA";
	
	public static String operationName = "operationName";
	
	public static String cobrandId = "cobrandId";
	
	public static String userId = "userId";
	
	public static String siteId = "siteId";
	
	public static String sumInfoId = "sumInfoId";
	
	public static String allowed = "allowed";
	
	public static String action = "action";
	
	public static String expiry = "expiry";
	
	public static String entrySource = "entrySource";
	
}
