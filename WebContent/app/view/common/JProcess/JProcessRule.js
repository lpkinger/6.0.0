Ext.define('erp.view.common.JProcess.JProcessRule',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'common/saveJprocessRule.action',
				deleteUrl: 'common/deleteJprocessRule.action',
				updateUrl: 'common/updateJprocessRule.action',			
				getIdUrl: 'common/getId.action?seq=JPROCESSRULE_SEQ',
				keyField: 'ru_id',
				statusField: 'ru_status'
			}]
		}); 
		me.callParent(arguments); 
	} 
});