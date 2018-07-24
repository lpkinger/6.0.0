Ext.define('erp.view.vendbarcode.main.main',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'vendbarcodeHeader'
			}, {
				xtype: 'erpBottom'
			}, {
				xtype: 'vendErpTabPanel'
			},{
			   xtype:'vendbarcodeTreePanel'	
			}]
		});
		me.callParent(arguments); 
	}
});