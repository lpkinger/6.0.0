Ext.define('erp.view.scm.reserve.setBarcode.Viewport',{ 
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
					xtype: "erpSetBarcodeGridPanel",  
			    	anchor: '100% 100%',
			    	keyField:'bi_piid',
			    	mainField:'bi_piid',
			     }]  
		});
		me.callParent(arguments); 
	}
});