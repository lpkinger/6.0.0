Ext.define('erp.view.scm.sale.customerBatchDeal.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',	
	id:'customerbatchdeal',
	//title:'客户批量获取UU号',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items : [{
				xtype : 'tabpanel',	
				//id:'content-panel',
				bodyBorder : false,
				cls:'top_tabbar',
				items : [{
					title : '未开通',
					anchor : '100% 100%',
					xtype : 'erpnoPassedUU',
					//id:'erpnoPassedUU1',
					showRowNum : false,
					
				},
				{
					title : '已开通',
					anchor : '100% 100%',
					xtype : 'erpPassedUU',
					//id:'erpPassedUU2',
					showRowNum : false,	
						
				}					
				],
			}]
	});
		me.callParent(arguments); 
 
}
});