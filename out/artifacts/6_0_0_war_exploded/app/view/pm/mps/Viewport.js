Ext.define('erp.view.common.batchDeal.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "erpBatchDealFormPanel",  
	    	anchor: '100% 25%',
	    },{
			region: 'south',         
			xtype: "erpBatchDealGridPanel",  
	    	anchor: '100% 75%'
	    }]
		});
		me.callParent(arguments); 
	}
});