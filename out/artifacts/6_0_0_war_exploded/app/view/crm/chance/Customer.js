Ext.define('erp.view.crm.chance.Customer',{ 
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
					anchor: '100% 70%',
					saveUrl: 'crm/chance/saveCustomer.action',
					deleteUrl: 'crm/chance/deleteCustomer.action',
					updateUrl: 'crm/chance/updateCustomer.action',
					auditUrl: 'crm/chance/auditCustomer.action',
					resAuditUrl: 'crm/chance/resAuditCustomer.action',
					submitUrl: 'crm/chance/submitCustomer.action',
					resSubmitUrl:  'crm/chance/resSubmitCustomer.action',
					getIdUrl: 'common/getId.action?seq=PRECUSTOMER_SEQ',
					keyField: 'cu_id',
					codeField: 'cu_code',
					statusField: 'cu_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'ct_detno',
					necessaryField: 'ct_name',
					keyField: 'ct_id',
					mainField: 'ct_cuid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});