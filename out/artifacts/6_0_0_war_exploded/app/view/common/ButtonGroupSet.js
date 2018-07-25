Ext.define('erp.view.common.ButtonGroupSet',{
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'ButtonGroupSetPanel',
				region: 'center'
			}]
		});
		me.callParent(arguments); 
	}
});