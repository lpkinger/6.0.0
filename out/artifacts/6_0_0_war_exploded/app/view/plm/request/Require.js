Ext.define('erp.view.plm.request.Require',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'plm/request/saveRequire.action',
				updateUrl: 'plm/request/updateRequire.action',
				deleteUrl: 'plm/request/deleteRequire.action',
				auditUrl:'plm/request/auditRequire.action',
				turnProjectUrl:'plm/request/turnProject.action',
				turnPrepProjectUrl:'plm/request/turnPrepProject.action',
				resAuditUrl: 'plm/request/resAuditRequire.action',
				submitUrl: 'plm/request/submitRequire.action',
				resSubmitUrl: 'plm/request/resSubmitRequire.action',
				getIdUrl: 'common/getId.action?seq=Require_SEQ',
				keyField: 'pr_id',
				statusField: 'pr_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});