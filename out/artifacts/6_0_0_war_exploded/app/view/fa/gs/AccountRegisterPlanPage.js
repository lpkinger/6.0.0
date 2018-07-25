Ext.define('erp.view.fa.gs.AccountRegisterPlanPage',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',
				width: '20%',
			},{
				region: 'west',
				width: '20%',
				xtype: 'accountregistertree',
			},{
				region: 'center',
				xtype:'erpDatalistGridPanel',
				noSpecialQuery:true,
				caller: caller
			}]
		}); 
		me.callParent(arguments); 
	}
});