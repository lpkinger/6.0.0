Ext.define('erp.view.crm.chance.VendorDistrApply',{ 
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
					saveUrl: 'crm/chance/saveVendorDistrApply.action',
					deleteUrl: 'crm/chance/deleteVendorDistrApply.action',
					updateUrl: 'crm/chance/updateVendorDistrApply.action',
					getIdUrl: 'common/getId.action?seq=VendorDistrApply_SEQ',
					auditUrl: 'crm/Chance/auditVendorDistrApply.action',
					resAuditUrl: 'crm/Chance/resAuditVendorDistrApply.action',
					submitUrl: 'crm/Chance/submitVendorDistrApply.action',
					resSubmitUrl: 'crm/Chance/resSubmitVendorDistrApply.action',
					keyField: 'va_id',
					codeField: 'va_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'vad_detno',
					necessaryField: 'vad_personcode',
					keyField: 'vad_id',
					mainField: 'vad_vaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});