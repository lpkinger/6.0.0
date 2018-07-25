Ext.define('erp.view.crm.customermgr.development.Schedule',{ 
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
					saveUrl: 'crm/customermgr/saveSchedule.action',
					deleteUrl: 'crm/customermgr/deleteSchedule.action',
					updateUrl: 'crm/customermgr/updateSchedule.action',
					getIdUrl: 'common/getId.action?seq=Schedule_SEQ',
					keyField: 'sc_id',
					codeField: 'sc_code',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});