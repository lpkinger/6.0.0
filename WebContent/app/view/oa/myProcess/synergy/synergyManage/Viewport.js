Ext.define('erp.view.oa.myProcess.synergy.synergyManage.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpSynergyManageFormPanel',  
	    	  anchor: '100% 30%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpSynergyManageGridPanel',  
	    	  anchor: '100% 70%'
	    }]
		});
		me.callParent(arguments); 
	}
});