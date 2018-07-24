Ext.define('erp.view.scm.product.Product', {
	extend : 'Ext.Viewport',
	layout : 'border',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				region : 'center',
				autoScroll : true,
				saveUrl : 'scm/product/saveProduct.action',
				deleteUrl : 'scm/product/deleteProduct.action',
				updateUrl : 'scm/product/updateProduct.action',
				auditUrl : 'scm/product/auditProduct.action',
				resAuditUrl : 'scm/product/resAuditProduct.action',
				submitUrl : 'scm/product/submitProduct.action',
				resSubmitUrl : 'scm/product/resSubmitProduct.action',
				bannedUrl : 'scm/product/bannedProduct.action',
				resBannedUrl : 'scm/product/resBannedProduct.action',
				getIdUrl : 'common/getId.action?seq=PRODUCT_SEQ',
				keyField : 'pr_id',
				codeField : 'pr_code',
				statusField : 'pr_statuscode'
			} ]
		});
		me.callParent(arguments);
	}
});