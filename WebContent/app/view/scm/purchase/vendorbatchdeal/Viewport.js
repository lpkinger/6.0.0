Ext.define('erp.view.scm.purchase.vendorbatchdeal.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',	
	id:'supplierbatchdeal',
	//title:'供应商批量获取UU号',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items : [{
				xtype : 'tabpanel',	
				//id:'content-panel',
				cls:'top_tabbar',
				bodyBorder : false,
				items : [{
					title : '未开通',
					anchor : '100% 100%',
					xtype : 'erpSuppliernoPassedUU',
					//id:'erpnoPassedUU1',
					showRowNum : false
					
				},
				{
					title : '已开通',
					anchor : '100% 100%',
					xtype : 'erpSupplierPassedUU',
					//id:'erpPassedUU2',
					showRowNum : false
						
				}					
				],
			}]
	});
		me.callParent(arguments); 
 
}
});