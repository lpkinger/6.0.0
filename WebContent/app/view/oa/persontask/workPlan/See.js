Ext.define('erp.view.oa.persontask.workPlan.See',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'anchor', 
				items: [{
					xtype: 'workplanfield',
					anchor: '100% 100%',
					title: '本月计划',
//					fieldLabel: '月计划',
					keyField: 'wp_id',
					collapsed: false,
					value: value
					
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});