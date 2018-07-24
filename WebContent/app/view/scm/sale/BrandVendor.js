Ext.define('erp.view.scm.sale.BrandVendor',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/sale/saveBrandVendor.action',
				updateUrl: 'scm/sale/updateBrandVendor.action',
				deleteUrl: 'scm/sale/deleteBrandVendor.action',
				submitUrl: 'scm/sale/submitBrandVendor.action',
				resSubmitUrl:'scm/sale/resSubmitBrandVendor.action',
				auditUrl:'scm/sale/auditBrandVendor.action',
				resAuditUrl:'scm/sale/resAuditBrandVendor.action',
				bannedUrl: 'scm/sale/bannedBrandVendor.action',
				resBannedUrl: 'scm/sale/resBannedBrandVendor.action',
				getIdUrl: 'common/getId.action?seq=PRODUCTBRAND_SEQ',
				keyField: 'bv_id', 
				statusField: 'bv_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});