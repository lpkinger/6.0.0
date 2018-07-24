Ext.define('erp.view.ma.createAccountBook.CreateAccountBook',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true,
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, { 
			items: [{
				xtype: 'navigationbar',
				region: 'north'
			},{
				xtype : 'infocard',
				region: 'center',
				anchor : '100% 100%'
			}]
		});
		me.callParent(arguments); 
	}
});