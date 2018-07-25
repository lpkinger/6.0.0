Ext.define('erp.view.plm.project.ProjectScheme',{ 
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
					saveUrl: 'plm/project/saveProjectEvaluation.action?caller='+caller,
					deleteUrl: 'plm/project/deleteProjectEvaluation.action?caller='+caller,
					updateUrl: 'plm/project/updateProjectEvaluation.action?caller='+caller,
					auditUrl: 'plm/project/auditProjectEvaluation.action?caller='+caller,
					resAuditUrl: 'plm/project/resAuditProjectEvaluation.action?caller='+caller,
					submitUrl: 'plm/project/submitProjectEvaluation.action?caller='+caller,
					resSubmitUrl: 'plm/project/resSubmitProjectEvaluation.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=ProjectEvaluation_SEQ',
					keyField: 'pe_id',
					statuscodeField: 'pe_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});