Ext.define('erp.view.fs.cust.SaleReport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	bodyStyle : 'background:#ffffff',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'border',
				items: [{
					region:'west',
					xtype:'salereportnavition'
				},{
					region:'center',
					xtype: 'navpanel'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});