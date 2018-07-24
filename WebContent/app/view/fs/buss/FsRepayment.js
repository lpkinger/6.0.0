Ext.define('erp.view.fs.buss.FsRepayment',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/buss/saveFsRepayment.action',
					deleteUrl: 'fs/buss/deleteFsRepayment.action',
					updateUrl: 'fs/buss/updateFsRepayment.action',
					auditUrl: 'fs/buss/auditFsRepayment.action',
					resAuditUrl: 'fs/buss/resAuditFsRepayment.action',
					submitUrl: 'fs/buss/submitFsRepayment.action',
					resSubmitUrl: 'fs/buss/resSubmitFsRepayment.action',
					getIdUrl: 'common/getId.action?seq=FSREPAYMENT_SEQ',
					codeField: 're_code',
					keyField: 're_id',
					statusField: 're_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});