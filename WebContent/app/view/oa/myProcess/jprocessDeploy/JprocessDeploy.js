Ext.define('erp.view.oa.myProcess.jprocessDeploy.JprocessDeploy',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpJCTreeGrid',
					anchor: '100% 100%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});