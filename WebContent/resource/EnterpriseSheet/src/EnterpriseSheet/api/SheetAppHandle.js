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
Ext.define('EnterpriseSheet.api.SheetAppHandle', {
	
	constructor : function(config){
		Ext.apply(this, config);
		
		this.callParent(arguments);
	},
	
	/*
	 * this function will return the reference of some useful component in your created sheet app such as sheet, store, toolbar and so on
	 */
	getRefer : function(name){
		return this[name];
	},
	
	getSheet : function(){
		return this.getRefer('sheet');
	},
	
	getStore : function(){
		return this.getRefer('store');
	},
	
	getAppContainer : function(){
		return this.getRefer('appCt');
	},
	
	getToolbar : function(){
		return this.getRefer('toolbar')
	},
	
	getContentbar : function(){
		return this.getRefer('contentbar')
	},
	
	getTitlebar : function(){
		return this.getRefer('titlebar')
	},
	
	getSidebar : function(){
		return this.getRefer('sidebar')
	}
});