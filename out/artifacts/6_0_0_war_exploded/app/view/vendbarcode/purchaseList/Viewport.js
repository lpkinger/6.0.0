Ext.define('erp.view.vendbarcode.purchaseList.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  xtype:'erpPurchaseListGrid',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});