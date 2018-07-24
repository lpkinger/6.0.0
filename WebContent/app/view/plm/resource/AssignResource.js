Ext.define('erp.view.plm.resource.AssignResource',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'resourcePanel',
					anchor: '100% 56%',
				},
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});