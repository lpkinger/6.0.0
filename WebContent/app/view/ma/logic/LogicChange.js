Ext.define('erp.view.ma.logic.LogicChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'ma/logic/saveLogicChange.action',
				updateUrl: 'ma/logic/updateLogicChange.action',
				getIdUrl: 'common/getId.action?seq=LOGICCHANGE_SEQ',
				keyField: 'lc_id'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});