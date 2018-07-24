Ext.define('erp.view.fs.credit.ResultsReport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 20%',
					updateUrl: 'fs/credit/updateFsReport.action',
					auditUrl: 'fs/credit/auditFsReport.action',
					resAuditUrl: 'fs/credit/resAuditFsReport.action',
					submitUrl: 'fs/credit/submitFsReport.action',
					resSubmitUrl: 'fs/credit/resSubmitFsReport.action',
					getIdUrl: 'common/getId.action?seq=FsReport_SEQ',
					keyField: 're_id',
					statusField: 're_status',
					statuscodeField: 're_statuscode'
				},{				
					xtype: 'erpGridPanel2',
					anchor : '100% 80%',
					detno: 'rr_detno',
					keyField: 'rr_id',
					mainField: 'rr_reid'
				}]
		}); 
		
		this.callParent(arguments); 
	}
});