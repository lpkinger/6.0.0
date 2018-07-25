/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) FeyaSoft Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need to have a valid license key to access this file.
 */
Ext.define('EnterpriseSheet.override.Language', {	
	extend: 'EnterpriseSheet.lang.Language',
	
	// custom language define, it have high priority than language folder ...
	// Such as: 
	// 'file' : 'File'
	
}, function() {
	SLANG = window.SLANG || Ext.create('EnterpriseSheet.override.Language');
});