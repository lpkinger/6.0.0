Ext.define('erp.view.oa.mail.Delete',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpDeleteGroupGrid',
					anchor: '100% 100%'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});