Ext.define('erp.view.common.main.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpHeader'
			}, {
				xtype: 'erpBottom',
				hidden: true
			}, {
				xtype: 'erpTabPanel'
			},{
			   xtype:'erpTreeTabPanel'	
			}]
		});
		me.callParent(arguments); 
	}
});