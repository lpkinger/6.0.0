Ext.define('erp.view.fa.gs.arQuery.ShowArDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
		    	xtype: 'ardetailgrid',  
		    	anchor: '100% 100%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});