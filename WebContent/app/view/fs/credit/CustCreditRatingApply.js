Ext.define('erp.view.fs.credit.CustCreditRatingApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/credit/saveCustCreditRatingApply.action',
					updateUrl: 'fs/credit/updateCustCreditRatingApply.action',
					deleteUrl: 'fs/credit/deleteCustCreditRatingApply.action',
					submitUrl: 'fs/credit/submitCustCreditRatingApply.action',
					resSubmitUrl: 'fs/credit/resSubmitCustCreditRatingApply.action',
					auditUrl: 'fs/credit/auditCustCreditRatingApply.action',
					resAuditUrl: 'fs/credit/resAuditCustCreditRatingApply.action',
					getIdUrl: 'common/getId.action?seq=CUSTCREDITRATINGAPPLY_SEQ',
					keyField: 'cra_id',
					codeField: 'cra_code',
					statusField: 'cra_status',
					statuscodeField: 'cra_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});