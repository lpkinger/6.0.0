Ext.define('erp.view.oa.storage.ReturnProperty',{ 
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
					anchor: '100% 50%',
					keyField: 'pa_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'pd_code',
					keyField: 'pd_id',
					detno: 'pd_detno',
					mainField: 'pd_paid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});