Ext.define('erp.view.common.subs.subscribeView',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',	
	id:'viewportsubs',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items : [{
				xtype : 'tabpanel',							
				bodyBorder : false,
				cls:'top_tabbar',
				items : [{
					title : '未订阅',
					anchor : '100% 100%',
					xtype : 'erpnosubscribeGridPanel',																			
					showRowNum : false,
					iconCls:null
					
				},
				{
					title : '已订阅',
					anchor : '100% 100%',
					xtype : 'erpsubscribedGridPanel',					
					showRowNum : false,	
					iconCls:null	
				}					
				],
			}]
	});
		me.callParent(arguments); 
 
}
});