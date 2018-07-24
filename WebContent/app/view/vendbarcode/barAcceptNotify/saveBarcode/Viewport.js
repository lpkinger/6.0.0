Ext.define('erp.view.vendbarcode.barAcceptNotify.saveBarcode.Viewport',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
				items: [/*{
					    xtype: 'erpSaveBarcodeFormPanel',
					    region: 'north', 
						anchor: '100% 18%',
					    mainField:'bi_piid',
					    necessaryField:'bi_status'

			    },*/{
					xtype: "erpVendBarcodeGridPanel",  
					anchor: '100% 100%'
			     }]  
		});
		me.callParent(arguments); 
	}
});