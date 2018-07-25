Ext.define('erp.view.crm.chance.CustomerImpDistrApply',{ 
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
					saveUrl: 'crm/chance/saveCustomerImpDistrApply.action',
					deleteUrl: 'crm/chance/deleteCustomerImpDistrApply.action',
					updateUrl: 'crm/chance/updateCustomerImpDistrApply.action',
					getIdUrl: 'common/getId.action?seq=CustomerImpDistrApply_SEQ',
					auditUrl: 'crm/Chance/auditCustomerImpDistrApply.action',
					resAuditUrl: 'crm/Chance/resAuditCustomerImpDistrApply.action',
					submitUrl: 'crm/Chance/submitCustomerImpDistrApply.action',
					resSubmitUrl: 'crm/Chance/resSubmitCustomerImpDistrApply.action',
					keyField: 'ca_id',
					codeField: 'ca_code',
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