Ext.define('erp.view.oa.mail.mailDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [{
					xtype: 'erpMailDetailFormPanel'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});