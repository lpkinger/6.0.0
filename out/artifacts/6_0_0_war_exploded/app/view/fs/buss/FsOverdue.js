Ext.define('erp.view.fs.buss.FsOverdue',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/buss/saveFsOverdue.action',
					updateUrl: 'fs/buss/updateFsOverdue.action',
					deleteUrl: 'fs/buss/deleteFsOverdue.action',
					submitUrl: 'fs/buss/submitFsOverdue.action',
					resSubmitUrl: 'fs/buss/resSubmitFsOverdue.action',
					auditUrl: 'fs/buss/auditFsOverdue.action',
					resAuditUrl: 'fs/buss/resAuditFsOverdue.action',
					getIdUrl: 'common/getId.action?seq=FsOverdue_SEQ',
					keyField: 'od_id',
					codeField: 'od_code'
				}]
			}); 
		this.callParent(arguments); 
	}
});