Ext.define('erp.view.plm.budget.ProjectBudget',{ 
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
					anchor: '100% 55%',
					saveUrl: 'plm/budget/saveProjectBudget.action',
					deleteUrl:'plm/budget/deleteProjectBudget.action',
					updateUrl:'plm/budget/updateProjectBudget.action',
					submitUrl:'plm/budget/submitProjectBudget.action',
					resSubmitUrl:'plm/budget/resSubmitProjectBudget.action',
					auditUrl:'plm/budget/auditProjectBudget.action',
					resAuditUrl:'plm/budget/resAuditProjectBudget.action',
					getIdUrl:'common/getId.action?seq=PROJECTBUDGET_SEQ',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 45%',
				  }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});