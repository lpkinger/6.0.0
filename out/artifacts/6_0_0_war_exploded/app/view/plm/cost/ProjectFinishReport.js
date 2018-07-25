Ext.define('erp.view.plm.cost.ProjectFinishReport',{ 
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
					saveUrl: 'plm/cost/saveProjectFinishReport.action',
					deleteUrl:'plm/cost/deleteProjectFinishReport.action',
					updateUrl:'plm/cost/updateProjectFinishReport.action',
					submitUrl:'plm/cost/submitProjectFinishReport.action',
					resSubmitUrl:'plm/cost/resSubmitProjectFinishReport.action',
					auditUrl:'plm/cost/auditProjectFinishReport.action',
					resAuditUrl:'plm/cost/resAuditProjectFinishReport.action',
					getIdUrl:'common/getId.action?seq=PROJECTFINISHREPORT_SEQ',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});