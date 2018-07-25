Ext.define('erp.view.oa.mail.receive',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGroupGrid',
					anchor: '100% 100%'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});