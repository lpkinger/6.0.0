Ext.define('erp.view.scm.purchase.ProductVendorRate',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
			    updateUrl: 'scm/purchase/updateProductVendorRate.action'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%'	,
				keyField: 'pv_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});