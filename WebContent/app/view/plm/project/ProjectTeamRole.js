Ext.define('erp.view.plm.project.ProjectTeamRole',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, {
			items: [{
			    layout: 'anchor', 
			    items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'plm/project/saveProjectTeamRole.action',
					deleteUrl: 'plm/project/deleteProjectTeamRole.action',
					updateUrl: '/plm/project/updateProjectTeamRole.action',
					getIdUrl: 'common/getId.action?seq=TEAMROLE_SEQ',
					//submitUrl:'test/submitTest.action',
					//resSubmitUrl: 'test/reSubmitTest.action',
					//auditUrl: 'test/auditTest.action',
					//resAuditUrl: 'test/resAuditTest.action',
					keyField: 'tr_id',
					statusField: 'tr_status',
					statuscodeField: 'tr_statuscode'
			    }]
			}]
		}); 
		me.callParent(arguments); 
	} 
});