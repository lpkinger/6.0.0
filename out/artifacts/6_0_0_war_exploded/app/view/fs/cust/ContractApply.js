Ext.define('erp.view.fs.cust.ContractApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/cust/saveContractApply.action',
					updateUrl: 'fs/cust/updateContractApply.action',
					deleteUrl: 'fs/cust/deleteContractApply.action',
					submitUrl: 'fs/cust/submitContractApply.action',
					resSubmitUrl: 'fs/cust/resSubmitContractApply.action',
					auditUrl: 'fs/cust/auditContractApply.action',
					resAuditUrl: 'fs/cust/resAuditContractApply.action',
					getIdUrl: 'common/getId.action?seq=ContractApply_SEQ',
					keyField: 'ca_id',
					codeField: 'ca_code',
					statusField: 'ca_status',
					statuscodeField: 'ca_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});