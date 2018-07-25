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
Ext.onReady(function() {

    Ext.QuickTips.init();
    
    /**
     * Define those 2 methods as global variable
     */
    SHEET_API = Ext.create('EnterpriseSheet.api.SheetAPI', {
        openFileByOnlyLoadDataFlag: true
    });
    
    SHEET_API_HD = SHEET_API.createSheetApp({
	   withoutTitlebar: false,
       withoutSheetbar: false,
       withoutToolbar: false,
       withoutContentbar: false,
       withoutSidebar: false //,
       // disableCalEditorStyle: true,
       //showValueInCalEditor: true
	});
    
    // this is tab panel include main and details 
    var centralPanel = Ext.create('enterpriseSheet.demo.CenterTabPanel', {
        width: 800,
        height: 600
    });
    
    // define west panel with tree structure
    var westPanel = Ext.create('enterpriseSheet.demo.WestPanel', {
    	centralPanel : centralPanel
    });
    	
    Ext.create('Ext.Viewport', {    	
        layout: 'border',
        items: [westPanel,  centralPanel],
        listeners: {
	      afterlayout: function(v, layout, eOpts) {
		      westPanel.selectNode();	
	      }
	    }
    });
    
    var sheet = SHEET_API_HD.getSheet();
    sheet.on('resethistory', function(){        	        
    	// console.log("history is reset ...");
    });  
    
    sheet.getStore().on('aftercellchange', function(){
    	// console.log(arguments)
    })
});