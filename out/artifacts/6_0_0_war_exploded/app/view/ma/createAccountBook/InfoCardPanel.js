Ext.define('erp.view.ma.createAccountBook.InfoCardPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.infocard',
	layout: 'card', 
	hideBorders: true,
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, { 
		});
		me.callParent(arguments); 
	},
	defaults: {
	},
	style: {
		buttonAlign: 'center'
	},
	items: [{
		xtype: 'companyinfo'
	},{
		xtype: 'accountbookinfo'
	},{
		xtype: 'activityaccountbook'
	}]
});