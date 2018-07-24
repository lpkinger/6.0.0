Ext.define('erp.view.scm.product.VendProductLoss',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/saveVendProductLoss.action',
				deleteUrl: 'scm/product/deleteVendProductLoss.action',
				updateUrl: 'scm/product/updateVendProductLoss.action',
				getIdUrl: 'common/getId.action?seq=VendProdLoss_SEQ',
				keyField: 'vpl_id',
				codeField: 'vpl_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});