Ext.define('erp.view.plm.task.TaskPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	autoScroll : true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor : '100% 20%',
				saveUrl: '/plm/task/saveTaskPlan.action',
				deleteUrl: '/plm/task/deleteTaskPlan.action',
				updateUrl: '/plm/task/updateTaskPlan.action',
				auditUrl: '/plm/task/auditTaskPlan.action',
				resAuditUrl: '/plm/task/resAuditTaskPlan.action',
				submitUrl: '/plm/task/submitTaskPlan.action',
				resSubmitUrl: '/plm/task/resSubmitTaskPlan.action',
				getIdUrl: 'common/getId.action?seq=TaskPlan_SEQ',
				keyField: 'wp_id', 
				codeField: 'wp_code',
				statusField: 'wp_statuscode'
			},{  
				 title:'上周工作总结',
				 id:'summary',
				 caller:'TaskSummary',
				 anchor : '100% 30%',
				 xtype:'erpGridPanel4',				 
				 condition:week==1?"wpd_week=0 and wpd_emcode='"+wpcode+"'  and ( to_char(wpd_plandate,'yyyymm')='"+(year-1)+"12' or  to_char(wpd_plandate,'yyyymm')='"+year+"01')":'wpd_week=' +(week-1) +" and wpd_emcode='"+wpcode+"'  and  to_char(wpd_plandate,'yyyy')='"+year+"'",
				 tbar:null,
				 _noc:1,
			},{
			 xtype:'tabpanel',
			 anchor : '100% 48%',
			 items:[{
				 title:'本周工作计划',
				 id:'plan',
				 caller:'TaskPlan',
				 margin: '10 0 0 0',
				 region:'south',
				 anchor : '100% 48%',
				 xtype:'erpGridPanel2',				
				 condition:'wpd_week=' +week +" and wpd_emcode='"+wpcode+"' and  to_char(wpd_plandate,'yyyy')='"+year+"'",
				 keyField:'wpd_id',
				 _noc:1
			 },{
				 xtype:'textareafield',
				 title:'需上级解决问题',
				 fieldStyle:'background:#FFFAFA;color:#515151;',
				 id:'request',
				 height:'48%'
			 }]
			}]
		}); 
		me.callParent(arguments); 
	}
});