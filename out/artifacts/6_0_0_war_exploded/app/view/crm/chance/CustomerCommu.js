Ext.define('erp.view.crm.chance.CustomerCommu',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'crm/Chance/saveCustomerCommu.action',
					deleteUrl: 'crm/Chance/deleteCustomerCommu.action',
					updateUrl: 'crm/Chance/updateCustomerCommu.action',
					getIdUrl: 'common/getId.action?seq=CUSTOMERCOMMU_SEQ',
					keyField: 'cc_id',
					codeField: 'cc_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});