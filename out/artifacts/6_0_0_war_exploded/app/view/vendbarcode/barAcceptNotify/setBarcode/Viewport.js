Ext.define('erp.view.vendbarcode.barAcceptNotify.setBarcode.Viewport',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
				items: [/*{
					    xtype: 'erpSetBarcodeFormPanel',
						anchor: '100% 8%',
						keyField:'bi_piid'
			    },*/{
					xtype: "erpVendSetBarcodeGridPanel",  
			    	anchor: '100% 100%',
			    	keyField:'ban_anid',
			    	mainField:'ban_anid',
			     }]  
		});
		me.callParent(arguments); 
	}
});