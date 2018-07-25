Ext.define('erp.view.fs.credit.CreditTargets',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/credit/saveCreditTargets.action',
					updateUrl: 'fs/credit/updateCreditTargets.action',
					deleteUrl: 'fs/credit/deleteCreditTargets.action',
					submitUrl: 'fs/credit/submitCreditTargets.action',
					resSubmitUrl: 'fs/credit/resSubmitCreditTargets.action',
					auditUrl: 'fs/credit/auditCreditTargets.action',
					resAuditUrl: 'fs/credit/resAuditCreditTargets.action',
					getIdUrl: 'common/getId.action?seq=CREDITTARGETS_SEQ',
					keyField: 'ct_id',
					codeField: 'ct_code',
					statusField: 'ct_status',
					statuscodeField: 'ct_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});