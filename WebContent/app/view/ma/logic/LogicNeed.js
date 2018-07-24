Ext.define('erp.view.ma.logic.LogicNeed',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel', 
				saveUrl: 'ma/logic/saveLogicNeed.action',
				updateUrl: 'ma/logic/updateLogicNeed.action',
				getIdUrl: 'common/getId.action?seq=LOGICNEED_SEQ',
				keyField: 'ln_id'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});