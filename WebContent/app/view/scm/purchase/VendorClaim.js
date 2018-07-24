Ext.define('erp.view.scm.purchase.VendorClaim',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/purchase/saveVendorClaim.action',
				updateUrl: 'scm/purchase/updateVendorClaim.action',
				deleteUrl: 'scm/purchase/deleteVendorClaim.action',
				auditUrl: 'scm/purchase/auditVendorClaim.action',
				resAuditUrl: 'scm/purchase/resAuditVendorClaim.action',
				submitUrl: 'scm/purchase/submitVendorClaim.action',
				resSubmitUrl: 'scm/purchase/resSubmitVendorClaim.action',
				turnAPBillUrl: 'scm/purchase/turnAPBillVendorClaim.action',
				getIdUrl: 'common/getId.action?seq=VENDORCLAIM_SEQ',
				keyField: 'vc_id',
				codeField: 'vc_code',
				statusField: 'vc_statuscode'
	    	},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%',
				detno: 'vcd_deton',
				keyField: 'vcd_id',
				mainField: 'vcd_vcid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});