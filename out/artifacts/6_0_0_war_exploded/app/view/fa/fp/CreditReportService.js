Ext.define('erp.view.fa.fp.CreditReportService',{ 
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
					saveUrl: 'fa/fp/saveCreditReportService.action',
					deleteUrl: 'fa/fp/deleteCreditReportService.action',
					updateUrl: 'fa/fp/updateCreditReportService.action',
					auditUrl: 'fa/fp/auditCreditReportService.action',
					resAuditUrl: 'fa/fp/resAuditCreditReportService.action',
					submitUrl: 'fa/fp/submitCreditReportService.action',
					resSubmitUrl: 'fa/fp/resSubmitCreditReportService.action',
					getIdUrl: 'common/getId.action?seq=CreditReportService_SEQ',
					keyField: 'crs_id',
					codeField: 'crs_code',
					statusField: 'crs_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});