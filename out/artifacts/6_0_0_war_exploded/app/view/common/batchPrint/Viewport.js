Ext.define('erp.view.common.batchPrint.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "erpBatchPrintFormPanel",  
//			printUrl:'',
	    	anchor: '100% 30%'
	    },{
			region: 'south',         
			xtype: "erpBatchPrintGridPanel",  
	    	anchor: '100% 70%'
	    }]
		});
		me.callParent(arguments); 
	}
});