Ext.define('erp.view.pm.make.WCPlanResult',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "erpQueryFormPanel", 
			_noc:1,
	    	anchor: '100% 20%',
	    },{
			region: 'south',         
			xtype: 'erpDatalistGridPanel',  
			noSpecialQuery:true,
			_noc:1,
	    	anchor: '100% 80%'
	    }]
		});
		me.callParent(arguments); 
	}
});