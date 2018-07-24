Ext.define('erp.view.plm.cost.ProjectCost',{ 
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
					anchor: '100% 80%',
					saveUrl: 'plm/cost/saveProjectCost.action',
					deleteUrl: 'plm/cost/deleteProjectCost.action',
					updateUrl: 'plm/cost/updateProjectCost.action',
					auditUrl: 'plm/cost/auditProjectCost.action',
					resAuditUrl: 'plm/cost/resAuditProjectCost.action',
					submitUrl: 'plm/cost/submitProjectCost.action',
					resSubmitUrl: 'plm/cost/resSubmitProjectCost.action',
					getIdUrl: 'common/getId.action?seq=PROJECTCOST_SEQ',
					keyField: 'pc_id',
					statusField:'pc_status',
					codeField:'pc_code'
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});