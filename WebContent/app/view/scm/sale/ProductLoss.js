Ext.define('erp.view.scm.sale.ProductLoss',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 70%',
				saveUrl: 'scm/sale/saveProductLoss.action',
				deleteUrl: 'scm/sale/deleteProductLoss.action',
				updateUrl: 'scm/sale/updateProductLoss.action',
				getIdUrl: 'common/getId.action?seq=ProductLoss_SEQ',
				keyField: 'pl_id',
				codeField: 'pl_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});