Ext.define('erp.view.ma.PowerSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'grouppower',
					anchor: '100% 100%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});