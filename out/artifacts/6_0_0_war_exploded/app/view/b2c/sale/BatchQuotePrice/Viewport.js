Ext.define('erp.view.b2c.sale.BatchQuotePrice.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	id:'port',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',      
			xtype: "erpBatchDealFormPanel" ,
	    	anchor: '100% 25%'
	    }	
	    ,{
			region: 'south',         
			xtype: "erpBatchDealGridPanel",  
	    	anchor: '100% 75%'
	    }
		
	    ]
		});
		me.callParent(arguments); 
	}
});