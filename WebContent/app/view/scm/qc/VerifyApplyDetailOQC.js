Ext.define('erp.view.scm.qc.VerifyApplyDetailOQC',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/qc/saveVerifyApplyDetailOQC.action',
				deleteUrl: 'scm/qc/deleteVerifyApplyDetailOQC.action',
				updateUrl: 'scm/qc/updateVerifyApplyDetailOQC.action',
				auditUrl: 'scm/qc/auditVerifyApplyDetailOQC.action',
				resAuditUrl: 'scm/qc/resAuditVerifyApplyDetailOQC.action',
				submitUrl: 'scm/qc/submitVerifyApplyDetailOQC.action',
				resSubmitUrl: 'scm/qc/resSubmitVerifyApplyDetailOQC.action',
				getIdUrl: 'common/getId.action?seq=QUA_VERIFYAPPLYDETAIL_SEQ',
				codeField: 've_code',
				keyField: 've_id',
				statusField: 've_status',
				statuscodeField: 've_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});