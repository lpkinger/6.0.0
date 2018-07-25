Ext.define('erp.view.scm.qc.VerifyApplyDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				deleteUrl: 'scm/qc/deleteVerifyApplyDetail.action?caller=' +caller,
				updateUrl: 'scm/qc/updateVerifyApplyDetail.action?caller=' +caller,
				auditUrl: 'scm/qc/auditVerifyApplyDetail.action?caller=' +caller,
				printUrl: 'scm/qc/printVerifyApplyDetail.action?caller=' +caller,
				resAuditUrl: 'scm/qc/resAuditVerifyApplyDetail.action?caller=' +caller,
				submitUrl: 'scm/qc/submitVerifyApplyDetail.action?caller=' +caller,
				resSubmitUrl: 'scm/qc/resSubmitVerifyApplyDetail.action?caller=' +caller,
				checkUrl: 'scm/qc/checkVerifyApplyDetail.action?caller=' +caller,
				resCheckUrl: 'scm/qc/resCheckVerifyApplyDetail.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=QUAVERIFYAPPLYDETAIL_SEQ',
				codeField: 've_code',
				keyField: 've_id',
				statusField: 've_status',
				statuscodeField: 've_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'ved_detno',
				keyField: 'ved_id',
				mainField: 'ved_veid',
				allowExtraButtons : true
			}]
		}); 
		me.callParent(arguments); 
	} 
});