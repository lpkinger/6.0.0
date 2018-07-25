Ext.define('erp.view.opensys.complaint.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel2',
				autoScroll: true,
				anchor:'100% 100%',
				saveUrl: 'crm/aftersalemgr/saveComplaint.action',
				deleteUrl: 'crm/aftersalemgr/deleteComplaint.action',
				updateUrl: 'crm/aftersalemgr/updateComplaint.action',
				auditUrl: 'crm/aftersalemgr/auditComplaint.action',
				resAuditUrl: 'crm/aftersalemgr/resAuditComplaint.action',
				submitUrl: 'crm/aftersalemgr/submitComplaint.action',
				resSubmitUrl: 'crm/aftersalemgr/resSubmitComplaint.action',
				getIdUrl: 'common/getId.action?seq=Complaint_SEQ',
				keyField: 'co_id'
			}]
		});
		me.callParent(arguments); 
	}
});