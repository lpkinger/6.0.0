Ext.define('erp.view.ma.DocumentPowerSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'groupdocumentpower',
					anchor: '100% 100%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});