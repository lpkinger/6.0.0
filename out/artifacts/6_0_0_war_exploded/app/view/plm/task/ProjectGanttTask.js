Ext.define('erp.view.plm.task.ProjectGanttTask',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 	
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'prodForm',
				region:'north',
				updateUrl: 'plm/main/updateProjectGanttTask.action',
				deleteUrl: 'plm/main/deleteProjectGanttTask.action',
				submitUrl: 'plm/main/submitProjectGanttTask.action',
				resSubmitUrl:'plm/main/resSubmitProjectGanttTask.action',
				auditUrl: 'plm/main/auditProjectGanttTask.action',
				resAuditUrl:'plm/main/resAuditProjectGanttTask.action',
			},{
				xtype:'ganttpanel',
				region:'center',
				ganttConf:false,
				showScheduler:false
			}] 
		}); 
		me.callParent(arguments); 
	} 
});