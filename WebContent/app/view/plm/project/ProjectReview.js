Ext.define('erp.view.plm.project.ProjectReview', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	autoScroll : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'ReviewPanel',
				anchor : '100% 63%',
				updateUrl : 'plm/review/updateProjectReview.action',
				deleteUrl : 'plm/review/deleteProjectReview.action',
				submitUrl : 'plm/review/submitProjectReview.action',
				resSubmitUrl : 'plm/review/resSubmitProjectReview.action',
				auditUrl : 'plm/review/auditProjectReview.action',
				resAuditUrl : 'plm/review/resAuditProjectReview.action',
				planTaskUrl : 'plm/review/planMainTask.action'
			}, {
				xtype : 'tabpanel',
				anchor : '100% 35%',
				items:[{
				xtype : 'erpGridPanel2',
				title : '项目里程碑',
				id : 'projectKeyDeviceGrid',
				//caller : 'ProjectKeyDevice',
				keyField : 'pp_id',
				mainField : 'pp_prid'
			}, {
				xtype : 'projectcostbudget',
				caller : 'ProjectCostBudget',
				title : '费用预算',
				keyField : 'pcb_id',
				mainField : 'pcb_prid',
				detno:'pcb_detno'
			}]
			} ]
		});
		me.callParent(arguments);
	}
});