Ext.define('erp.view.fa.gs.DepositKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'depositKindViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					necessaryField: 'dk_code',
					keyField: 'dk_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});