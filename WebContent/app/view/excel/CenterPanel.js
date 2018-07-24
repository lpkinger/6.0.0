
Ext.define('erp.view.excel.CenterPanel', {
	alias:'widget.excelCenterPanel',
	extend : 'Ext.Panel',	
	region: 'center',
	id:'center',
	SHEET_API_HD:null,
//	isTpl:true,
	layout : 'fit',
	requires:[
        'Ext.layout.container.Fit',
        'Ext.layout.container.Border',
        'Ext.layout.container.Form',
        'EnterpriseSheet.Config',
        'EnterpriseSheet.api.SheetAPI'      
    ],
    constructor : function(config){
        config = config || {};
        SHEET_API = Ext.create('EnterpriseSheet.api.SheetAPI');
        SHEET_API_HD = SHEET_API.createSheetApp({
        	scrollerAlwaysVisible: SCONFIG.SCROLLER_ALWAYS_VISIBLE
        });
        this.SHEET_API_HD = SHEET_API_HD;
        config.items = [SHEET_API_HD.appCt];
        this.callParent([config]);
    }
});
