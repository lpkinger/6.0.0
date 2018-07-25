Ext.define('erp.view.plm.cost.ProjectConfirm',{ 
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
					saveUrl: 'plm/cost/saveProjectConfirm.action',
					deleteUrl:'plm/cost/deleteProjectConfirm.action',
					updateUrl:'plm/cost/updateProjectConfirm.action',
					submitUrl:'plm/cost/submitProjectConfirm.action',
					resSubmitUrl:'plm/cost/resSubmitProjectConfirm.action',
					auditUrl:'plm/cost/auditProjectConfirm.action',
					resAuditUrl:'plm/cost/resAuditProjectConfirm.action',
					getIdUrl:'common/getId.action?seq=PROJECTCONFIRM_SEQ',
					keyField: 'pc_id',
					statusField:'pc_status',
					codeField:'pc_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});