Ext.define('erp.view.plm.task.ProjectMainTask',{ 
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
					anchor: '100% 35%',
					saveUrl: 'plm/main/saveProjectMainTask.action',
					deleteUrl: 'plm/main/deleteProjectMainTask.action',
					updateUrl: 'plm/main/updateProjectMainTask.action',
					submitUrl: 'plm/main/submitProjectMainTask.action',
					resSubmitUrl:'plm/main/resSubmitProjectMainTask.action',
					auditUrl: 'plm/main/auditProjectMainTask.action',
					resAuditUrl:'plm/main/resAuditProjectMainTask.action',
					TurnTaskUrl:'plm/main/TurnTask.action',
					LoadTaskNodeUrl:'plm/main/LoadTaskNode.action',
					ImportExcel:'plm/main/ImportExcel.action',
					resEndUrl:'plm/main/resEnd.action',
					endUrl:'plm/main/end.action',
					getIdUrl: 'common/getId.action?seq=PROJECTMAINTASK_SEQ',
					keyField: 'pt_id',
				},{
	/*			    title:'任务分配',*/
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					mainField: 'ptid',
					keyField:'id',
					allowExtraButtons:true
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});