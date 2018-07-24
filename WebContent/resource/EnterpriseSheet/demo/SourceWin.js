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
Ext.define('enterpriseSheet.demo.SourceWin', {
	
	/* Begin Definitions */
		
	extend : 'Ext.Window',
	
	/* End Definitions */
	width : 800,    	
	
	height : 550,  
        
	layout : 'fit',
	
	title: 'Source code',
	
	initComponent : function(){

		this.items = [{
		    xtype : "component",
		    autoEl:{
			    tag:'iframe',
			    src : 'js/EnterpriseSheet/demo/resources/html/' + this.srcHtml			
		    }	    
		}];
				
		this.callParent();		
	}
});