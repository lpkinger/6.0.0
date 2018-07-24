Ext.define('erp.view.plm.request.RequestChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'plm/request/saveRequestChange.action',
				updateUrl: 'plm/request/updateRequestChange.action',
				deleteUrl: 'plm/request/deleteRequestChange.action',
				auditUrl:'plm/request/auditRequestChange.action',
				resAuditUrl: 'plm/request/resAuditRequestChange.action',
				submitUrl: 'plm/request/submitRequestChange.action',
				resSubmitUrl: 'plm/request/resSubmitRequestChange.action',
				getIdUrl: 'common/getId.action?seq=RequestChange_SEQ',
				keyField: 'prc_id',
				statusField: 'prc_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});