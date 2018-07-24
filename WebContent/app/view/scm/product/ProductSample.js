Ext.define('erp.view.scm.product.ProductSample',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/product/saveProductSample.action',
				deleteUrl: 'scm/product/deleteProductSample.action',
				updateUrl: 'scm/product/updateProductSample.action',		
				getIdUrl: 'common/getId.action?seq=ProductSample_SEQ',
				auditUrl: 'scm/product/auditProductSample.action',
				nullifyUrl: 'scm/product/nullifyProductSample.action',
				resAuditUrl: 'scm/product/resAuditProductSample.action',
				submitUrl: 'scm/product/submitProductSample.action',
				resSubmitUrl: 'scm/product/resSubmitProductSample.action',
				keyField: 'ps_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				necessaryField: 'pd_vendcode',
				keyField: 'pd_id',
				detno: 'pd_detno',
				mainField: 'pd_psid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});