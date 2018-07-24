Ext.define('erp.view.fs.buss.FsInterest',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					updateUrl: 'fs/buss/updateFsInterest.action',
					deleteUrl: 'fs/buss/deleteFsInterest.action',
					submitUrl: 'fs/buss/submitFsInterest.action',
					resSubmitUrl: 'fs/buss/resSubmitFsInterest.action',
					auditUrl: 'fs/buss/auditFsInterest.action',
					resAuditUrl: 'fs/buss/resAuditFsInterest.action',
					getIdUrl: 'common/getId.action?seq=FsInterest_SEQ',
					keyField: 'in_id',
					codeField: 'in_code'
				}]
			}); 
		this.callParent(arguments); 
	}
});