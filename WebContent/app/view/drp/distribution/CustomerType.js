Ext.define('erp.view.drp.distribution.CustomerType',{ 
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
					saveUrl: 'drp/distribution/saveCustomerType.action',
					deleteUrl: 'drp/distribution/deleteCustomerType.action',
					updateUrl: 'drp/distribution/updateCustomerType.action',
					auditUrl: 'drp/distribution/auditCustomerType.action',
					resAuditUrl: 'drp/distribution/resAuditCustomerType.action',
					submitUrl: 'drp/distribution/submitCustomerType.action',
					resSubmitUrl: 'drp/distribution/resSubmitCustomerType.action',
					bannedUrl: 'drp/distribution/bannedCustomerType.action',
					resBannedUrl: 'drp/distribution/resBannedCustomerType.action',
					getIdUrl: 'common/getId.action?seq=CUSTOMER_SEQ',
					keyField: 'cu_id',
					codeField: 'cu_code',
					statusField:'cu_auditstatuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});