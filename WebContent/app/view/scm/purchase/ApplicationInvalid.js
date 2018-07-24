Ext.define('erp.view.scm.purchase.ApplicationInvalid',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				keyField: 'ap_id',
				codeField: 'ap_code',
				statusField: 'ap_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});