Ext.define('erp.view.sysmng.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	border: false,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items : [
			{
				xtype:'mainnavigation',
				region:'north',
				border:false,
				height:50,
			},
			{   region:'center',
			    layout:'border',
			    bodyBorder:false,
			    items:[
				   {	region:'center',
						xtype:'mainnavpanel'
					}]
			}
			]
		});
		me.callParent(arguments); 
	}
});