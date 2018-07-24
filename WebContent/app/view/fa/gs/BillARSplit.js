Ext.define('erp.view.fa.gs.BillARSplit',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',
				updateUrl: 'fa/gs/saveBillARSplit.action',
				keyField: 'bar_id',
				codeField: 'bar_code'
			},{
				xtype: 'erpGridPanel2',
				region: 'center',
				necessaryField: 'brd_amount',
				keyField: 'brd_id',
				mainField: 'brd_barid',
				bbar: {xtype: 'erpToolbar', id:'toolbar',enableUp: false, enableDown: false},
				allowExtraButtons: true
			}]
		}); 
		me.callParent(arguments); 
	} 
});