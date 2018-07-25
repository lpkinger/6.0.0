Ext.define('erp.view.scm.reserve.ReserveRestart',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: "window",
					autoShow: true,
					closable: false,
					maximizable : true,
			    	width: '65%',
			    	height: '65%',
			    	layout: 'anchor',
			    	items: [{
			    		anchor: '100% 100%',
			    		xtype: 'ReserveRestart'
			    	}]
			    }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});