Ext.define('erp.view.sysmng.upgrade.sql.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	border: false,
	bodyBorder:false,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items : [{
				xtype:'upgradSqlForm'
			}]
		});
		me.callParent(arguments); 
	}
});