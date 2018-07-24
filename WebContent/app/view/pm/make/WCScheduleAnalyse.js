Ext.define('erp.view.pm.make.WCScheduleAnalyse',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					region: 'north',
					xtype: 'erpQueryFormPanel',
					anchor: '100% 30%'
				},{
					xtype: 'WCPlanTreeGrid',
					anchor: '100% 70%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});