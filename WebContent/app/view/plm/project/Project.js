Ext.define('erp.view.plm.project.Project',{ 
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
					saveUrl: 'plm/project/saveProject.action',
					deleteUrl: 'plm/project/deleteProject.action',
					updateUrl: 'plm/project/updateProject.action',
					auditUrl: 'plm/project/auditProject.action',
					resAuditUrl: 'plm/project/resAuditProject.action',
					submitUrl: 'plm/project/submitProject.action',
					resSubmitUrl: 'plm/project/resSubmitProject.action',
					turnReviewItemUrl:'plm/project/TurnProjectreview.action',
					getIdUrl: 'common/getId.action?seq=APPLYPROJECT_SEQ',
					keyField: 'prj_id',
					statusField:'prj_status',
					codeField:'prj_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});