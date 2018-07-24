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
Ext.define('EnterpriseSheetApp.view.Viewport', {
    extend: 'Ext.container.Viewport',
	id:'view',
	isTpl:true,
	SHEET_API_HD:null,
    requires:[
        'Ext.layout.container.Fit',
        'Ext.layout.container.Border',
        'Ext.layout.container.Form',
        'EnterpriseSheet.Config',
        'EnterpriseSheet.api.SheetAPI'      
    ],

    layout: {
        type: 'fit'
    },

    constructor : function(config){
        config = config || {};
        
        
        SHEET_API = Ext.create('EnterpriseSheet.api.SheetAPI');
        SHEET_API_HD = SHEET_API.createSheetApp({
        	scrollerAlwaysVisible: SCONFIG.SCROLLER_ALWAYS_VISIBLE
        });
        
        this.SHEET_API_HD = SHEET_API_HD;
        
        
        /**
         * This part just add your defined function ...can be removed if need 
         * You can add your defined event, and your defined custom code
         */
        // =============================Start you defined event listener ==============================
        CUSTOMER_DEFINED_CELL_EDITOR_FN(SHEET_API_HD.sheet);        
    	// ============================End your defined event listener ==================================
        
        config.items = [SHEET_API_HD.appCt];
		
        console.log(SHEET_API_HD);
        
        
        this.callParent([config]);

        /*
         * load the file
         */
     	
        
        if(!SCOM.isEmptyValue(config.fileId)){
        	
            SHEET_API.loadFile(SHEET_API_HD, {
            fileId:config.fileId,
            isTpl:true
            
            }, function(data){
            	//console.log(SHEET_API.getDefinedNameContent(SHEET_API_HD, 'longway'))
            }, this);
            
            
        }
    }

});
