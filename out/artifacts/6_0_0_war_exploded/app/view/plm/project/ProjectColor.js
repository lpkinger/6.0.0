Ext.define('erp.view.plm.project.ProjectColor',{ 
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
					saveUrl: 'plm/project/saveProjectColor.action',
					deleteUrl: 'plm/project/deleteProjectColor.action',
					updateUrl: 'plm/project/updateProjectColor.action',
					auditUrl: 'plm/project/auditProjectColor.action',
					resAuditUrl: 'plm/project/resAuditProjectColor.action',
					submitUrl: 'plm/project/submitProjectColor.action',
					resSubmitUrl: 'plm/project/resSubmitProjectColor.action',
					getIdUrl: 'common/getId.action?seq=PROJECTCOLOR_SEQ',
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