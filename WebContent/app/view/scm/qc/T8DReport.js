Ext.define('erp.view.scm.qc.T8DReport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/qc/saveT8DReport.action',
				deleteUrl: 'scm/qc/deleteT8DReport.action',
				updateUrl: 'scm/qc/updateT8DReport.action',
				auditUrl: 'scm/qc/auditT8DReport.action',
				resAuditUrl: 'scm/qc/resAuditT8DReport.action',
				submitUrl: 'scm/qc/submitT8DReport.action',
				resSubmitUrl: 'scm/qc/resSubmitT8DReport.action',
				checkUrl: 'scm/qc/checkT8DReport.action',
				resCheckUrl: 'scm/qc/resCheckT8DReport.action',
				getIdUrl: 'common/getId.action?seq=T8DREPORT_SEQ',
				codeField: 're_code',
				keyField: 're_id',
				statusField: 're_status',
				statuscodeField: 're_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});