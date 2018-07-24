Ext.define('erp.view.common.VisitERP.CurNavigationTree',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'CNTreePanel',
				anchor: '100% 100%'
			}]
		});
		me.callParent(arguments); 
	}
});