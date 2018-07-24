Ext.define('erp.view.oa.info.PagingReceive',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpPagingGridr',
					anchor: '100% 100%'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});