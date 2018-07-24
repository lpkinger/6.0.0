Ext.define('erp.view.plm.change.ProjectTaskChange',{ 
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
					saveUrl: 'plm/change/saveProjectTaskChange.action',
					deleteUrl: 'plm/change/deleteProjectTaskChange.action',
					updateUrl: 'plm/change/updateProjectTaskChange.action',
					auditUrl: 'plm/change/auditProjectTaskChange.action',
					resAuditUrl: 'plm/change/resAuditProjectTaskChange.action',
					submitUrl: 'plm/change/submitProjectTaskChange.action',
					resSubmitUrl: 'plm/change/resSubmitProjectTaskChange.action',
					getIdUrl: 'common/getId.action?seq=PROJECTTASKCHANGE_SEQ'
					}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});