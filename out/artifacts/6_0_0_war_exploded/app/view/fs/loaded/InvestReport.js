Ext.define('erp.view.fs.loaded.InvestReport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fs/loaded/saveInvestReport.action',
				updateUrl: 'fs/loaded/updateInvestReport.action',
				deleteUrl: 'fs/loaded/deleteInvestReport.action',
				submitUrl: 'fs/loaded/submitInvestReport.action',
				resSubmitUrl: 'fs/loaded/resSubmitInvestReport.action',
				auditUrl: 'fs/loaded/auditInvestReport.action',
				resAuditUrl: 'fs/loaded/resAuditInvestReport.action',
				getIdUrl: 'common/getId.action?seq=INVESTREPORT_SEQ',
				keyField: 'li_id',
				codeField: 'li_code',
				statusField: 'li_status',
				statuscodeField: 'li_statuscode'
			}]
		}); 
		this.callParent(arguments); 
	}
});