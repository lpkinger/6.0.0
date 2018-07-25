Ext.define('erp.view.crm.chance.Scheduler',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'crm/Chance/saveScheduler.action',
					deleteUrl: 'crm/Chance/deleteScheduler.action',
					updateUrl: 'crm/Chance/updateScheduler.action',
					getIdUrl: 'common/getId.action?seq=SCHEDULER_SEQ',
					keyField: 'sc_id',
					codeField: 'sc_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});