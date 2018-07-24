Ext.define('erp.view.scm.product.SafeReserve',{ 
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
			    	layout: 'border',
			    	items: [{
			    		region: 'center',
			    		xtype: 'SafeReserve'
			    	}]
			    }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});