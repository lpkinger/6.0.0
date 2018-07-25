Ext.define('erp.view.fa.fix.CurrencysMonth',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'currencysMonthGrid',
				anchor: '100% 100%',
				keyField: 'ac_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});