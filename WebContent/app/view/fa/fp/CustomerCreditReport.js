Ext.define('erp.view.fa.fp.CustomerCreditReport',{ 
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
					saveUrl: 'fa/fp/saveCustomerCreditReport.action',
					deleteUrl: 'fa/fp/deleteCustomerCreditReport.action',
					updateUrl: 'fa/fp/updateCustomerCreditReport.action',
					auditUrl: 'fa/fp/auditCustomerCreditReport.action',
					resAuditUrl: 'fa/fp/resAuditCustomerCreditReport.action',
					submitUrl: 'fa/fp/submitCustomerCreditReport.action',
					resSubmitUrl: 'fa/fp/resSubmitCustomerCreditReport.action',
					getIdUrl: 'common/getId.action?seq=CustomerCreditReport_SEQ',
					keyField: 'ccr_id',
					statusField: 'ccr_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});