Ext.define('erp.view.oa.myProcess.synergy.SeeSynergy',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [{
					xtype: 'erpSeeSynergyFormPanel'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});