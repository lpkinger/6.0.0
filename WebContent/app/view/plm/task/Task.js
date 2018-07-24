Ext.define('erp.view.plm.task.Task',{ 
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
					saveUrl: 'plm/task/saveTask.action',
					deleteUrl: 'plm/task/deleteTask.action',
					updateUrl: 'plm/task/updateTask.action',
					submitUrl: 'plm/task/submitTask.action',
					resSubmitUrl:'plm/task/resSubmitTask.action',
					auditUrl: 'plm/task/auditTask.action',
					getIdUrl: 'common/getId.action?seq=PROJECTTASK_SEQ',
					keyField: 'id',
					codeField:'taskcode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});