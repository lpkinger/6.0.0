Ext.define('erp.view.plm.budget.ProjectPie',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items:[{
			xtype:'ProjectChart',
		    anchor:'100% 100%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});