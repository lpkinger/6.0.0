Ext.define('erp.view.scm.purchase.Vendor',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/saveVendor.action',
				deleteUrl: 'scm/purchase/deleteVendor.action',
				updateUrl: 'scm/purchase/updateVendor.action',
				auditUrl: 'scm/purchase/auditVendor.action',
				resAuditUrl: 'scm/purchase/resAuditVendor.action',
				submitUrl: 'scm/purchase/submitVendor.action',
				resSubmitUrl: 'scm/purchase/resSubmitVendor.action',
				bannedUrl: 'scm/purchase/bannedVendor.action',
				resBannedUrl: 'scm/purchase/resBannedVendor.action',
				getIdUrl: 'common/getId.action?seq=VENDOR_SEQ',
				keyField: 've_id',
				codeField: 've_code',
				statusField: 've_auditstatuscode'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});