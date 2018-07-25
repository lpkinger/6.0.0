Ext.define('erp.view.plm.task.MilePostFollow',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'plm/task/saveMilePostFollow.action',
				updateUrl: 'plm/task/updateMilePostFollow.action',
				deleteUrl: 'plm/task/deleteMilePostFollow.action',
				auditUrl:'plm/task/auditMilePostFollow.action',
				resAuditUrl: 'plm/task/resAuditMilePostFollow.action',
				submitUrl: 'plm/task/submitMilePostFollow.action',
				resSubmitUrl: 'plm/task/resSubmitMilePostFollow.action',
				getIdUrl: 'common/getId.action?seq=MilePostFollow_SEQ',
				keyField: 'mpf_id',
				statusField: 'mpf_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});