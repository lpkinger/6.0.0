Ext.define('erp.view.fs.cust.CustPersonInfo',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/cust/saveCustPersonInfo.action',
					updateUrl: 'fs/cust/updateCustPersonInfo.action',
					deleteUrl: 'fs/cust/deleteCustPersonInfo.action',
					submitUrl: 'fs/cust/submitCustPersonInfo.action',
					resSubmitUrl: 'fs/cust/resSubmitCustPersonInfo.action',
					auditUrl: 'fs/cust/auditCustPersonInfo.action',
					resAuditUrl: 'fs/cust/resAuditCustPersonInfo.action',
					getIdUrl: 'common/getId.action?seq=CUSTPERSONINFO_SEQ',
					keyField: 'cp_id',
					statusField: 'cp_status',
					statuscodeField: 'cp_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});