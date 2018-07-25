Ext.define('erp.view.fs.cust.FinancingApply',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	autoScroll : true,
	initComponent : function(){
		var me = this;
		Ext.apply(me, {
			items: [{
				xtype: 'erpFinancingApplyFormPanel'
			}]
		}); 
		me.callParent(arguments); 
	}
});
