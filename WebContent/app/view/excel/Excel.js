Ext.define('erp.view.excel.Excel', {
    extend: 'Ext.Viewport',
	id:'view',
//	isTpl:true,
	SHEET_API_HD:null,
    requires:[
        'Ext.layout.container.Fit',
        'Ext.layout.container.Border',
        'Ext.layout.container.Form',
        'EnterpriseSheet.Config',
        'EnterpriseSheet.api.SheetAPI',
        'erp.view.excel.CenterPanel',
        'erp.view.excel.WestPanel'
    ],
  	layout:'border',
    initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'excelWestPanel',
				region: 'west',
				width: 300
			},{
				region: 'center',
				xtype: 'excelCenterPanel',
				width: 800
			}]
		}); 
		me.callParent(arguments); 
	}

/*    constructor : function(config){
        config = config || {};
        SHEET_API = Ext.create('EnterpriseSheet.api.SheetAPI');
        SHEET_API_HD = SHEET_API.createSheetApp({
        	scrollerAlwaysVisible: SCONFIG.SCROLLER_ALWAYS_VISIBLE
        });
        this.SHEET_API_HD = SHEET_API_HD;
        // =============================Start you defined event listener ==============================
//        CUSTOMER_DEFINED_CELL_EDITOR_FN(SHEET_API_HD.sheet);        
    	// ============================End your defined event listener ==================================
        config.items = [SHEET_API_HD.appCt];
        console.log(SHEET_API_HD);
        this.callParent([config]);
    }*/

});
