Ext.define('erp.view.fa.fp.CustomerBaseFP',{ 
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
					anchor: '100% 80%',
					saveUrl: 'fa/fp/saveCustomerBaseFP.action',
					deleteUrl: 'fa/fp/deleteCustomerBaseFP.action',
					updateUrl: 'fa/fp/updateCustomerBaseFP.action',
					auditUrl: 'fa/fp/auditCustomerBaseFP.action',
					resAuditUrl: 'fa/fp/resAuditCustomerBaseFP.action',
					submitUrl: 'fa/fp/submitCustomerBaseFP.action',
					resSubmitUrl: 'fa/fp/resSubmitCustomerBaseFP.action',
					bannedUrl: 'fa/fp/bannedCustomerBaseFP.action',
					resBannedUrl: 'fa/fp/resBannedCustomerBaseFP.action',
					HandleHangCustomerBaseUrl: 'fa/fp/submitHandleHangCustomerBaseFP.action',
					getIdUrl: 'common/getId.action?seq=CUSTOMER_SEQ',
					keyField: 'cu_id',
					codeField: 'cu_code',
					statusField: 'cu_auditstatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 20%', 
					detno: 'shi_detno',
					keyField: 'shi_id',
					mainField: 'shi_cuid'				
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});