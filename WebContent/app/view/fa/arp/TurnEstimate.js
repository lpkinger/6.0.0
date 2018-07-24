Ext.define('erp.view.fa.arp.TurnEstimate',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this;
		Ext.apply(me, { 
			items: [{
				xtype: 'erpDatalistGridPanel',
				noSpecialQuery:true,
				anchor: '100% 100%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});