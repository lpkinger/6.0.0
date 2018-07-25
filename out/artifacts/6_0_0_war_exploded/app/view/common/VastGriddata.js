Ext.define('erp.view.common.VastGriddata',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpGridPanel4',
					anchor: '100% 95%',
					saveUrl:'hr/emplmana/vastGriddata.action'
				},{
					xtype: 'erpToolbar3',
					anchor: '100% 5%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});