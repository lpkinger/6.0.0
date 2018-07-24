Ext.define('erp.view.scm.sale.batchDeal.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',         
				xtype: "erpMyBatchDealFormPanel",  
		    	anchor: '100% 25%',
		    },{
				region: 'center',         
				xtype: "erpMyBatchDealGridPanel",  
		    	anchor: '100% 75%',
		    }]
			});
		me.callParent(arguments); 
	}
});