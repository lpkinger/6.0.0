Ext.define('erp.view.scm.purchase.VerifyApplyChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 28%',
				saveUrl: 'scm/purchase/saveVerifyApplyChange.action',
				deleteUrl: 'scm/purchase/deleteVerifyApplyChange.action',
				updateUrl: 'scm/purchase/updateVerifyApplyChange.action',
				auditUrl: 'scm/purchase/auditVerifyApplyChange.action',
				submitUrl: 'scm/purchase/submitVerifyApplyChange.action',
				resSubmitUrl: 'scm/purchase/resSubmitVerifyApplyChange.action',
				getIdUrl: 'common/getId.action?seq=VERIFYAPPLYCHANGE_SEQ',
				keyField: 'vc_id',
				codeField: 'vc_code',
				statusField: 'vc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 72%', 
				detno: 'vcd_detno',
				necessaryField: 'vcd_vacode',
				keyField: 'vcd_id',
				mainField: 'vcd_vcid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});