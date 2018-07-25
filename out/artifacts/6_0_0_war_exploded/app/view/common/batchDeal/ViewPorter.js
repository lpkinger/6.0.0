Ext.define('erp.view.common.batchDeal.ViewPorter',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "erpBatchDealerFormPanel",  
	    	anchor: '100% 25%',
	    },{
			region: 'center',         
			xtype: "erpBatchDealerGridPanel",  
	    	anchor: '100% 75%',
	    	selectObject:new Object()
	    }]
		});
		me.callParent(arguments); 
	}
});