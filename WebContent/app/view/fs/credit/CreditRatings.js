Ext.define('erp.view.fs.credit.CreditRatings',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/credit/saveCreditRatings.action',
					updateUrl: 'fs/credit/updateCreditRatings.action',
					deleteUrl: 'fs/credit/deleteCreditRatings.action',
					submitUrl: 'fs/credit/submitCreditRatings.action',
					resSubmitUrl: 'fs/credit/resSubmitCreditRatings.action',
					auditUrl: 'fs/credit/auditCreditRatings.action',
					resAuditUrl: 'fs/credit/resAuditCreditRatings.action',
					getIdUrl: 'common/getId.action?seq=CREDITRATINGS_SEQ',
					keyField: 'cr_id',
					codeField: 'cr_code',
					statusField: 'cr_status',
					statuscodeField: 'cr_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});