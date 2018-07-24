Ext.define('erp.view.scm.qc.Sample',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/qc/saveSample.action',
				deleteUrl: 'scm/qc/deleteSample.action',
				updateUrl: 'scm/qc/updateSample.action',
				auditUrl: 'scm/qc/auditSample.action',
				resAuditUrl: 'scm/qc/resAuditSample.action',
				submitUrl: 'scm/qc/submitSample.action',
				resSubmitUrl: 'scm/qc/resSubmitSample.action',
				getIdUrl: 'common/getId.action?seq=SAMPLE_SEQ',
				codeField: 'sa_code',
				keyField: 'sa_id',
				statusField: 'sa_status'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'sd_detno',
				necessaryField: 'sd_batchnumber',
				keyField: 'sd_id',
				mainField: 'sd_said'
			}]
		}); 
		me.callParent(arguments); 
	} 
});