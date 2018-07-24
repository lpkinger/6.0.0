Ext.define('erp.view.fs.loaded.LoadedPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				readOnly: parent.readOnly==1,
				saveUrl: 'fs/loaded/saveLoadedPlan.action',
				updateUrl: 'fs/loaded/updateLoadedPlan.action',
				//deleteUrl: 'fs/loaded/deleteLoadedPlan.action',
				submitUrl: 'fs/loaded/submitLoadedPlan.action',
				resSubmitUrl: 'fs/loaded/resSubmitLoadedPlan.action',
				auditUrl: 'fs/loaded/auditLoadedPlan.action',
				resAuditUrl: 'fs/loaded/resAuditLoadedPlan.action',
				getIdUrl: 'common/getId.action?seq=FSLOADEDPLANTABLE_SEQ',
				keyField: 'pt_id',
				statusField: 'pt_status',
				statuscodeField: 'pt_statuscode'
			}]
		}); 
		this.callParent(arguments); 
	}
});