Ext.define('erp.view.scm.reserve.profitBarcode.Viewport',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
				items: [{
					xtype: "erpProfitBarcodeGridPanel",  
			    	anchor: '100% 100%',
			    	keyField:'bdd_bsid',
			    	mainField:'bdd_id',
			     }]  
		});
		me.callParent(arguments); 
	}
});