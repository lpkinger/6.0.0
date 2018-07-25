Ext.define('erp.view.scm.sale.TenderSubmission',{ 
	extend: 'Ext.Viewport', 
	layout:'anchor',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpTenderSubmissionFormPanel',
				anchor:'100% 50%',
				saveUrl: 'scm/sale/saveSaleTender.action',
				auditUrl: 'scm/sale/auditSaleTender.action',
				resAuditUrl: 'scm/sale/resAuditSaleTender.action',
				submitUrl: 'scm/sale/submitSaleTender.action',
				resSubmitUrl: 'scm/sale/resSubmitSaleTender.action',
				keyField:'id',
				statusField:"st_status",
				statuscodeField:"st_statuscode"
			},{
				xtype:'erpTenderSubmissionGridPanel',
				anchor:'100% 50%'
			}]
		}); 
		me.callParent(arguments); 
	}
});