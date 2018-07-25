Ext.define('erp.view.crm.aftersalemgr.Complaint',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'crm/aftersalemgr/saveComplaint.action',
					deleteUrl: 'crm/aftersalemgr/deleteComplaint.action',
					updateUrl: 'crm/aftersalemgr/updateComplaint.action',		
					getIdUrl: 'common/getId.action?seq=Complaint_SEQ',
					submitUrl: 'crm/aftersalemgr/submitComplaint.action',
					resSubmitUrl: 'crm/aftersalemgr/resSubmitComplaint.action',
					auditUrl: 'crm/aftersalemgr/auditComplaint.action',
					resAuditUrl: 'crm/aftersalemgr/resAuditComplaint.action',
					keyField: 'co_id',
					codeField: 'co_code',
					statusField: 'co_status',
					statuscodeField: 'co_statuscode',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});