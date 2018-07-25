Ext.define('erp.view.common.RelativeSearch',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'tabpanel',
				layout: 'fit',
				defaults: {
					anchor: '100% 100%',
					xtype: 'container',
					layout: 'anchor',
					style: 'background: #f1f1f1;'
				}
			}]
		}); 
		me.callParent(arguments); 
	} 
});