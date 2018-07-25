Ext.define('erp.view.scm.reserve.ProductWhMoney',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				keyField: 'pw_id'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});