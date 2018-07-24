Ext.define('erp.view.scm.qc.MRB',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/qc/saveMRB.action',
				deleteUrl: 'scm/qc/deleteMRB.action',
				updateUrl: 'scm/qc/updateMRB.action',
				auditUrl: 'scm/qc/auditMRB.action',
				resAuditUrl: 'scm/qc/resAuditMRB.action',
				submitUrl: 'scm/qc/submitMRB.action',
				resSubmitUrl: 'scm/qc/resSubmitMRB.action',
				checkUrl: 'scm/qc/checkMRB.action',
				resCheckUrl: 'scm/qc/resCheckMRB.action',
				getIdUrl: 'common/getId.action?seq=QUA_MRB_SEQ',
				codeField: 'mr_code',
				keyField: 'mr_id',
				statusField: 'mr_status',
				statuscodeField: 'mr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 28%', 
				detno: 'md_detno',
				keyField: 'md_id',
				mainField: 'md_mrid'
			},{
				xtype: 'mrbdetail',
				anchor: '100% 22%',
				caller:'MRBDetail',
				detno: 'mrd_detno',
				keyField: 'mrd_id',
				mainField: 'mrd_mrid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});