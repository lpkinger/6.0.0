Ext.define('erp.view.oa.persontask.workWeekly.AddWorkWeekly',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'oa/persontask/saveWorkWeekly.action',
				deleteUrl: 'oa/persontask/deleteWorkWeekly.action',
				updateUrl: 'oa/persontask/updateWorkWeekly.action',
				submitUrl: 'oa/persontask/submitWorkWeekly.action',
				resSubmitUrl:'oa/persontask/resSubmitWorkWeekly.action',
				auditUrl: 'oa/persontask/auditWorkWeekly.action',
				resAuditUrl:'oa/persontask/resAuditWorkWeekly.action',
				getIdUrl: 'common/getId.action?seq=WORKWEEKLY_SEQ',
				keyField: 'ww_id',
				statusField:'ww_statuscode'
			}]
		});
		me.callParent(arguments); 
	}
});