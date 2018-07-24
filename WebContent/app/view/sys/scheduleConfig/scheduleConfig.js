Ext.define('erp.view.sys.scheduleConfig.scheduleConfig',{ 
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
					anchor: '100% 100%',
					autoScroll: true,
					saveUrl: 'sys/scheduleConfig/save.action',
					deleteUrl: 'sys/scheduleConfig/delete.action',
					updateUrl: 'sys/scheduleConfig/update.action',
					submitUrl: '',
					resSubmitUrl: '',
					getIdUrl: 'common/getId.action?seq=SYS_SCHEDULETASK_SEQ',
					keyField: 'id_', 
					codeField: 'code_',
					statusField: 'statuscode_'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});