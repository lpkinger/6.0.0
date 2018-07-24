Ext.define('erp.view.scm.product.ProductAssess',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/saveProductAssess.action',
				deleteUrl: 'scm/product/deleteProductAssess.action',
				updateUrl: 'scm/product/updateProductAssess.action',
				getIdUrl: 'common/getId.action?seq=ProductAssess_SEQ',
				auditUrl: 'scm/product/auditProductAssess.action',
				resAuditUrl: 'scm/product/resAuditProductAssess.action',
				submitUrl: 'scm/product/submitProductAssess.action',
				resSubmitUrl: 'scm/product/resSubmitProductAssess.action',
				keyField: 'pa_id',
				codeField: 'pa_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});