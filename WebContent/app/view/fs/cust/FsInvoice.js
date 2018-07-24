Ext.define('erp.view.fs.cust.FsInvoice',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/cust/saveFsInvoice.action',
					updateUrl: 'fs/cust/updateFsInvoice.action',
					deleteUrl: 'fs/cust/deleteFsInvoice.action',
					submitUrl: 'fs/cust/submitFsInvoice.action',
					resSubmitUrl: 'fs/cust/resSubmitFsInvoice.action',
					auditUrl: 'fs/cust/auditFsInvoice.action',
					resAuditUrl: 'fs/cust/resAuditFsInvoice.action',
					getIdUrl: 'common/getId.action?seq=FsInvoice_SEQ',
					keyField: 'in_id',
					codeField: 'in_code',
					statusField: 'in_status',
					statuscodeField: 'in_statuscode'
				}]
			}); 
		this.callParent(arguments); 
	}
});