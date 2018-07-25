Ext.define('erp.view.oa.task.TaskTemplate',{ 
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
					saveUrl: 'oa/task/saveTaskTemplate.action',
					deleteUrl: 'oa/task/deleteTaskTemplate.action',
					updateUrl: 'oa/task/updateTaskTemplate.action',
					bannedUrl: 'oa/task/bannedTaskTemplate.action',
					resBannedUrl: 'oa/task/resBannedTaskTemplate.action',
					getIdUrl: 'common/getId.action?seq=TASKTEMPLATE_SEQ',
					keyField: 'tt_id',
					codeField:'TT_CODE',
					statusField: 'TT_STATUS'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});