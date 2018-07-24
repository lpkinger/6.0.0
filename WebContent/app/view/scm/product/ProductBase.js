Ext.define('erp.view.scm.product.ProductBase',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/saveProductBase.action',
				deleteUrl: 'scm/product/deleteProductBase.action',
				updateUrl: 'scm/product/updateProductBase.action',
				auditUrl: 'scm/product/auditProductBase.action',
				resAuditUrl: 'scm/product/resAuditProductBase.action',
				submitUrl: 'scm/product/submitProductBase.action',
				resSubmitUrl: 'scm/product/resSubmitProductBase.action',
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id', 
				codeField: 'pr_code',
				statusField: 'pr_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});