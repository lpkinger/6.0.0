Ext.define('erp.view.ma.logic.LogicSet',{ 
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
					anchor: '100% 21%',
					updateUrl: 'ma/logic/updateLogicSet.action'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 79%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	}
});