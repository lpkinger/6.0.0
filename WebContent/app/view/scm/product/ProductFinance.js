Ext.define('erp.view.scm.product.ProductFinance',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/saveProductFinance.action',
				updateUrl: 'scm/product/updateProductFinance.action',
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id', 
				codeField: 'pr_code',
				statusField: 'pr_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});