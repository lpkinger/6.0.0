Ext.define('erp.view.crm.customercare.Repair',{ 
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
					saveUrl: 'crm/customercare/saveRepair.action',
					deleteUrl: 'crm/customercare/deleteRepair.action',
					updateUrl: 'crm/customercare/updateRepair.action',
					getIdUrl: 'common/getId.action?seq=Repair_SEQ',
					keyField: 're_id',
					codeField: 're_code',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});