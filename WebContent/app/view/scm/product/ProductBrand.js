Ext.define('erp.view.scm.product.ProductBrand',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/saveProductBrand.action',
				updateUrl: 'scm/product/updateProductBrand.action',
				deleteUrl: 'scm/product/deleteProductBrand.action',
				submitUrl: 'scm/product/submitProductBrand.action',
				resSubmitUrl:'scm/product/resSubmitProductBrand.action',
				auditUrl:'scm/product/auditProductBrand.action',
				resAuditUrl:'scm/product/resAuditProductBrand.action',
				bannedUrl: 'scm/product/bannedProductBrand.action',
				resBannedUrl: 'scm/product/resBannedProductBrand.action',
				getIdUrl: 'common/getId.action?seq=PRODUCTBRAND_SEQ',
				keyField: 'pb_id', 
				statusField: 'pb_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});