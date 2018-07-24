Ext.define('erp.view.plm.bug.Bug',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					keyField: 'bu_id',
					statusField:'bu_status',
					codeField:'bu_code'
				},
				
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});