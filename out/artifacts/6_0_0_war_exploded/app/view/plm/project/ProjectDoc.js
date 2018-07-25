Ext.define('erp.view.plm.project.ProjectDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'erpProjectFileTree'
			}]
		}); 
		me.callParent(arguments); 
	} 
});