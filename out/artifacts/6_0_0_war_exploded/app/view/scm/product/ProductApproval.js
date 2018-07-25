Ext.define('erp.view.scm.product.ProductApproval',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	autoScroll : true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor : '99% 98%',
				saveUrl: 'scm/product/saveProductApproval.action',
				deleteUrl: 'scm/product/deleteProductApproval.action',
				updateUrl: 'scm/product/updateProductApproval.action',
				auditUrl: 'scm/product/auditProductApproval.action',
				resAuditUrl: 'scm/product/resAuditProductApproval.action',
				submitUrl: 'scm/product/submitProductApproval.action',
				resSubmitUrl: 'scm/product/resSubmitProductApproval.action',
				getIdUrl: 'common/getId.action?seq=ProductApproval_SEQ',
				keyField: 'pa_id', 
				codeField: 'pa_code',
				statusField: 'pa_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	}
});