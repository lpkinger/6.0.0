Ext.define('erp.view.scm.qc.QuaProject',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/qc/saveQuaProject.action',
				deleteUrl: 'scm/qc/deleteQuaProject.action',
				updateUrl: 'scm/qc/updateQuaProject.action',
				auditUrl: 'scm/qc/auditQuaProject.action',
				resAuditUrl: 'scm/qc/resAuditQuaProject.action',
				submitUrl: 'scm/qc/submitQuaProject.action',
				resSubmitUrl: 'scm/qc/resSubmitQuaProject.action',
				getIdUrl: 'common/getId.action?seq=QUA_PROJECT_SEQ',
				codeField: 'pr_code',
				keyField: 'pr_id',
				statusField: 'pr_status'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'pd_detno',
				necessaryField: 'pd_itemcode',
				keyField: 'pd_id',
				mainField: 'pd_prid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});