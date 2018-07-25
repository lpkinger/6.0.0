Ext.define('erp.view.fs.cust.CustomerQuota',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/cust/saveCustomerQuota.action?caller=' +caller,
					updateUrl: 'fs/cust/updateCustomerQuota.action?caller=' +caller,
					deleteUrl: 'fs/cust/deleteCustomerQuota.action?caller=' +caller,
					submitUrl: 'fs/cust/submitCustomerQuota.action?caller=' +caller,
					resSubmitUrl: 'fs/cust/resSubmitCustomerQuota.action?caller=' +caller,
					auditUrl: 'fs/cust/auditCustomerQuota.action?caller=' +caller,
					resAuditUrl: 'fs/cust/resAuditCustomerQuota.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=CustomerQuota_SEQ',
					keyField: 'cq_id',
					codeField: 'cq_code',
					statusField: 'cq_status',
					statuscodeField: 'cq_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});