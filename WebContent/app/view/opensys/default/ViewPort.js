Ext.define('erp.view.opensys.default.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	border: false,
	bodyBorder:false,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'headerpanel'
			}, {
				xtype: 'footer'
			}, {
				xtype: 'centerTabPanel'
			},{
			    xtype:'navigationpanel'
			}]
		});
		me.callParent(arguments); 
	}
});