Ext.define('erp.view.ma.datalimit.DataLimit',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true,
	layout:'border',
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype:'limitform',
				region:'north'
			},{
				xtype:'limitdetailgrid',
				region:'center'
			}]
		}); 
		me.callParent(arguments); 
	}
});