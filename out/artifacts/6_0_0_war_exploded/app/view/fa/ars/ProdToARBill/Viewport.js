Ext.define('erp.view.fa.ars.ProdToARBill.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "erpProdToARBillFormPanel",  
	    	anchor: '100% 25%',
	    },{
			region: 'south',         
			xtype: "erpProdToARBillGridPanel",  
	    	anchor: '100% 75%'
	    }]
		});
		me.callParent(arguments); 
	}
});