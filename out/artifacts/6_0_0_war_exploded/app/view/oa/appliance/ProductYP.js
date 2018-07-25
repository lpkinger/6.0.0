Ext.define('erp.view.oa.appliance.ProductYP',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'scm/product/saveProduct.action',
					deleteUrl: 'scm/product/deleteProduct.action',
					updateUrl: 'scm/product/updateProduct.action',
					auditUrl: 'scm/product/auditProduct.action',
					resAuditUrl: 'scm/product/resAuditProduct.action',
					submitUrl: 'scm/product/submitProduct.action',
					resSubmitUrl: 'scm/product/resSubmitProduct.action',
					bannedUrl: 'scm/product/bannedProduct.action',
					resBannedUrl: 'scm/product/resBannedProduct.action',
					getIdUrl: 'common/getId.action?seq=Product_SEQ',
					keyField: 'pr_id',
					codeField: 'pr_code',
					statusField: 'pr_statuscode'	
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});