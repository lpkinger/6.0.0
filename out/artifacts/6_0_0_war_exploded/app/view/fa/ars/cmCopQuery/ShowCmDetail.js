Ext.define('erp.view.fa.ars.cmCopQuery.ShowCmDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
		    	xtype: 'cmcopdetailgrid',  
		    	anchor: '100% 100%'
		    }] 
		}); 
		me.callParent(arguments); 
	} 
});