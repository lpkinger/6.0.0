Ext.define('erp.view.crm.chance.CustomerDistr',{ 
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
					saveUrl: 'crm/chance/saveCustomerDistr.action',
					deleteUrl: 'crm/chance/deleteCustomerDistr.action',
					updateUrl: 'crm/chance/updateCustomerDistr.action',
					getIdUrl: 'common/getId.action?seq=CustomerDistr_SEQ',
					keyField: 'cu_id',
					codeField: 'cu_code',
//					statusField: 'as_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'cd_detno',
					necessaryField: 'cd_sellercode',
					keyField: 'cd_id',
					mainField: 'cd_cuid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});