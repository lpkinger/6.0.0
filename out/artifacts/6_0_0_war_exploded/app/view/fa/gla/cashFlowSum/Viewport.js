Ext.define('erp.view.fa.gla.cashFlowSum.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: "erpCashFlowGrid",  
		    	anchor: '100% 100%'
		    }]
		});
		me.callParent(arguments); 
	}
});