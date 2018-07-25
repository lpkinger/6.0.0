Ext.define('erp.view.scm.purchase.SupplierAssess',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/saveSupplierAssess.action',
				deleteUrl: 'scm/purchase/deleteSupplierAssess.action',
				updateUrl: 'scm/purchase/updateSupplierAssess.action',
				getIdUrl: 'common/getId.action?seq=ProductAssess_SEQ',
				auditUrl: 'scm/purchase/auditSupplierAssess.action',
				resAuditUrl: 'scm/purchase/resAuditSupplierAssess.action',
				submitUrl: 'scm/purchase/submitSupplierAssess.action',
				resSubmitUrl: 'scm/purchase/resSubmitSupplierAssess.action',
				keyField: 'sa_id',
				codeField: 'sa_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});