Ext.define('erp.view.scm.qc.ComplaintRecords',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				deleteUrl: 'scm/qc/deleteComplaintRecords.action',
				updateUrl: 'scm/qc/updateComplaintRecords.action',
				auditUrl: 'scm/qc/auditComplaintRecords.action',
				resAuditUrl: 'scm/qc/resAuditComplaintRecords.action',
				saveUrl: 'scm/qc/saveComplaintRecords.action',
				submitUrl: 'scm/qc/submitComplaintRecords.action',
				resSubmitUrl: 'scm/qc/resSubmitComplaintRecords.action',
				endUrl: 'scm/qc/endComplaintRecords.action',
				resEndUrl: 'scm/qc/resEndComplaintRecords.action',
				printUrl: 'scm/qc/printComplaintRecords.action',
				getIdUrl: 'common/getId.action?seq=COMPLAINTRECORDS_SEQ',
				codeField: 'cr_code',
				keyField: 'cr_id',
				statusField: 'cr_status',
				statuscodeField: 'cr_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});