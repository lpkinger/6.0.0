Ext.define('erp.view.oa.mail.send',{ 
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
					xtype: 'erpMailFormPanel'
				}, {
					xtype: 'erpMailTreePanel'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});