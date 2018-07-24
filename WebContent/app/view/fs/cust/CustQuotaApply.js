Ext.define('erp.view.fs.cust.CustQuotaApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fs/cust/saveCustQuotaApply.action',
				updateUrl: 'fs/cust/updateCustQuotaApply.action',
				deleteUrl: 'fs/cust/deleteCustQuotaApply.action',
				submitUrl: 'fs/cust/submitCustQuotaApply.action',
				resSubmitUrl: 'fs/cust/resSubmitCustQuotaApply.action',
				auditUrl: 'fs/cust/auditCustQuotaApply.action',
				resAuditUrl: 'fs/cust/resAuditCustQuotaApply.action',
				getIdUrl: 'common/getId.action?seq=CUSTQUOTAAPPLY_SEQ',
				keyField: 'ca_id',
				codeField: 'ca_code',
				statusField: 'ca_status',
				statuscodeField: 'ca_statuscode'
			}]
		}); 
		this.callParent(arguments); 
	}
});