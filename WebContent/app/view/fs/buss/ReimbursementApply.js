Ext.define('erp.view.fs.buss.ReimbursementApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/buss/saveReimbursementApply.action',
					updateUrl: 'fs/buss/updateReimbursementApply.action',
					deleteUrl: 'fs/buss/deleteReimbursementApply.action',
					submitUrl: 'fs/buss/submitReimbursementApply.action',
					resSubmitUrl: 'fs/buss/resSubmitReimbursementApply.action',
					auditUrl: 'fs/buss/auditReimbursementApply.action',
					resAuditUrl: 'fs/buss/resAuditReimbursementApply.action',
					getIdUrl: 'common/getId.action?seq=ReimbursementApply_SEQ',
					keyField: 'ra_id',
					codeField: 'ra_code',
					statusField: 'ra_status',
					statuscodeField: 'ra_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});