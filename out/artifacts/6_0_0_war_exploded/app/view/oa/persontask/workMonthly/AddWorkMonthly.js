Ext.define('erp.view.oa.persontask.workMonthly.AddWorkMonthly',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'oa/persontask/saveWorkMonthly.action',
				deleteUrl: 'oa/persontask/deleteWorkMonthly.action',
				updateUrl: 'oa/persontask/updateWorkMonthly.action',
				submitUrl: 'oa/persontask/submitWorkMonthly.action',
				resSubmitUrl:'oa/persontask/resSubmitWorkMonthly.action',
				auditUrl: 'oa/persontask/auditWorkMonthly.action',
				resAuditUrl:'oa/persontask/resAuditWorkMonthly.action',
				getIdUrl: 'common/getId.action?seq=WORKMONTHLY_SEQ',
				keyField: 'wm_id',
				statusField:'wm_statuscode'
			}]
		});
		me.callParent(arguments); 
	}
});