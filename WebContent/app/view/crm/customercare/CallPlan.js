Ext.define('erp.view.crm.customercare.CallPlan',{ 
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
					saveUrl: 'crm/customercare/saveCallPlan.action',
					deleteUrl: 'crm/customercare/deleteCallPlan.action',
					updateUrl: 'crm/customercare/updateCallPlan.action',
					getIdUrl: 'common/getId.action?seq=callPlan_SEQ',
					keyField: 'cp_id',
					codeField: 'cp_code',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});