Ext.define('erp.view.scm.purchase.VendorResBannedApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/saveVendorBannedApply.action',
				deleteUrl: 'scm/purchase/deleteVendorBannedApply.action',
				updateUrl: 'scm/purchase/updateVendorBannedApply.action',
				auditUrl: 'scm/purchase/auditVendorBannedApply.action',
				resAuditUrl: 'scm/purchase/resAuditVendorBannedApply.action',
				submitUrl: 'scm/purchase/submitVendorBannedApply.action',
				resSubmitUrl: 'scm/purchase/resSubmitVendorBannedApply.action',
				getIdUrl: 'common/getId.action?seq=VENDORBANNEDAPPLY_SEQ',
				keyField: 'vba_id',
				codeField: 'vba_code',
				statusField: 'vba_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});