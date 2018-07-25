Ext.define('erp.view.crm.chance.CustomerDistrApply',{ 
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
					anchor: '100% 30%',
					saveUrl: 'crm/chance/saveCustomerDistrApply.action',
					deleteUrl: 'crm/chance/deleteCustomerDistrApply.action',
					updateUrl: 'crm/chance/updateCustomerDistrApply.action',
					getIdUrl: 'common/getId.action?seq=CustomerDistrApply_SEQ',
					auditUrl: 'crm/Chance/auditCustomerDistrApply.action',
					resAuditUrl: 'crm/Chance/resAuditCustomerDistrApply.action',
					submitUrl: 'crm/Chance/submitCustomerDistrApply.action',
					resSubmitUrl: 'crm/Chance/resSubmitCustomerDistrApply.action',
					keyField: 'ca_id',
					codeField: 'ca_code',
//					statusField: 'as_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'cad_detno',
					necessaryField: 'cad_sellercode',
					keyField: 'cad_id',
					mainField: 'cad_caid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});