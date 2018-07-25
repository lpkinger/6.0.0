Ext.define('erp.view.plm.project.ProjectPlan',{ 
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
					anchor: '100% 50%',
					saveUrl: 'plm/projectplan/saveProjectPlan.action',
					deleteUrl: 'plm/projectplan/deleteProjectPlan.action',
					updateUrl: 'plm/projectplan/updateProjectPlan.action',
					submitUrl:'plm/projectplan/submitProjectPlan.action',
					resSubmitUrl:'plm/projectplan/resSubmitProjectPlan.action',
					auditUrl:'plm/projectplan/auditProjectPlan.action',
					resAuditUrl:'plm/projectplan/resAuditProjectPlan.action',
					turnReviewItemUrl:'plm/projectplan/TurnProjectreview.action',
					getIdUrl: 'common/getId.action?seq=PROJECTPLAN_SEQ',
					keyField: 'prjplan_id',
                    codeField:'prjplan_code',
				 },{
					 xtype:'tabpanel',
					 anchor:'100% 50%',
					 layout:'fit',
					 items:[{
					  title:'项目预算'	,
					  xtype:'erpGridPanel2',
					  caller:'ProjectBudget',
					  id:'grid',
					  mainField:'pd_prjid',
					  necessaryField:'pd_subjectname'
					 },{
					  title:'资源计划',
					  xtype:'erpGridPanel5',
					  caller:'Team',
					  mainField:'tm_prjid',
					  necessaryField:'tm_employeecode',
					  id:'team',
					  setReadOnly: function(bool){
							this.readOnly = bool;
						},
					 }]
				 }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});