Ext.define('erp.view.fa.ars.CustomerCreditSet',{ 
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
//					saveUrl: 'scm/sale/saveCustomerBase.action',
//					deleteUrl: 'scm/sale/deleteCustomerBase.action',
					updateUrl: 'scm/sale/updateCustomerCreditSet.action',
//					auditUrl: 'scm/sale/auditCustomerBase.action',
//					resAuditUrl: 'scm/sale/resAuditCustomerBase.action',
//					submitUrl: 'scm/sale/submitCustomerBase.action',
//					resSubmitUrl: 'scm/sale/resSubmitCustomerBase.action',
//					bannedUrl: 'scm/sale/bannedCustomerBase.action',
//					resBannedUrl: 'scm/sale/resBannedCustomerBase.action',
					getIdUrl: 'common/getId.action?seq=CUSTOMER_SEQ',
					keyField: 'cu_id',
					codeField: 'cu_code',
					statusField: 'cu_auditstatuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});