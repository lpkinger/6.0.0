Ext.define('erp.view.scm.purchase.VendorChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/purchase/saveVendorChange.action?caller=' + caller,
				deleteUrl: 'scm/purchase/deleteVendorChange.action?caller=' + caller,
				updateUrl: 'scm/purchase/updateVendorChange.action?caller=' + caller,
				auditUrl: 'scm/purchase/auditVendorChange.action?caller=' + caller,
				printUrl: 'scm/purchase/printVendorChange.action?caller=' + caller,
				resAuditUrl: 'scm/purchase/resAuditVendorChange.action?caller=' + caller,
				submitUrl: 'scm/purchase/submitVendorChange.action?caller=' + caller,
				resSubmitUrl: 'scm/purchase/resSubmitVendorChange.action?caller=' + caller,
				getIdUrl: 'common/getId.action?seq=VendorChange_SEQ',
				keyField: 'vc_id',
				codeField: 'vc_code',
				statusField: 'vc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'vcd_detno',
				necessaryField: 'vcd_vendcode',
				keyField: 'vcd_id',
				mainField: 'vcd_vcid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});