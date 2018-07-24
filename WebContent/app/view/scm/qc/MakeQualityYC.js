Ext.define('erp.view.scm.qc.MakeQualityYC',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				deleteUrl: 'scm/qc/deleteMakeQualityYC.action',
				updateUrl: 'scm/qc/updateMakeQualityYC.action',
				auditUrl: 'scm/qc/auditMakeQualityYC.action',
				resAuditUrl: 'scm/qc/resAuditMakeQualityYC.action',
				saveUrl: 'scm/qc/saveMakeQualityYC.action',
				submitUrl: 'scm/qc/submitMakeQualityYC.action',
				resSubmitUrl: 'scm/qc/resSubmitMakeQualityYC.action',
				getIdUrl: 'common/getId.action?seq=MakeQualityYC_SEQ',
				codeField: 'mq_code',
				keyField: 'mq_id',
				statusField: 'mq_status',
				statuscodeField: 'mq_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});