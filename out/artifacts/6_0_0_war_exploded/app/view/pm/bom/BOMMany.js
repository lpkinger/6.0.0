Ext.define('erp.view.pm.bom.BOMMany',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					region: 'north',
					xtype: 'erpQueryFormPanel',
					anchor: '100% 30%'
				},{
					xtype: 'bomTreeGrid',
					anchor: '100% 70%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});