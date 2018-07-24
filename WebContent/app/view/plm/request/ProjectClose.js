Ext.define('erp.view.plm.request.ProjectClose',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', //fit
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				saveUrl: 'plm/request/saveProjectClose.action',
				deleteUrl: 'plm/request/deleteProjectClose.action',
				updateUrl: 'plm/request/updateProjectClose.action',
				auditUrl: 'plm/request/auditProjectClose.action',
				resAuditUrl: 'plm/request/resAuditProjectClose.action',
				submitUrl: 'plm/request/submitProjectClose.action',
				resSubmitUrl: 'plm/request/resSubmitProjectClose.action',
				getIdUrl: 'common/getId.action?seq=ProjectClose_SEQ',
				keyField: 'pc_id',
				codeField: 'pp_code',
				statusField: 'pc_status',
				statuscodeField: 'pc_statuscode'
			}]
		}); 
		this.callParent(arguments); 
	}
});