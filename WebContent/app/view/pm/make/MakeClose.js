Ext.define('erp.view.pm.make.MakeClose',{ 
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
					title: '制造单逐张结案作业',
					autoShow: true,
					closable: false,
					maximizable : true,
			    	width: '65%',
			    	height: '65%',
			    	layout: 'border',
					items: [{
						xtype: 'MakeClose',
						region: 'center'					
					}]
			}] 
			}]
		}); 
		me.callParent(arguments); 
	} 
});