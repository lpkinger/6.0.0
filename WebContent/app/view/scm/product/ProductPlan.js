Ext.define('erp.view.scm.product.ProductPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/saveProductPlan.action',
				updateUrl: 'scm/product/updateProductPlan.action',
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id', 
				codeField: 'pr_code',
				statusField: 'pr_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});