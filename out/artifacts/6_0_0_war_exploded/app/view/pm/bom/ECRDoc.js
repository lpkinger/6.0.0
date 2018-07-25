Ext.define('erp.view.pm.bom.ECRDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'pm/bom/saveECRDOC.action',
				deleteUrl: 'pm/bom/deleteECRDOC.action',
				updateUrl: 'pm/bom/updateECRDOC.action',
				auditUrl: 'pm/bom/auditECRDOC.action',
				resAuditUrl: 'pm/bom/resAuditECRDOC.action',
				submitUrl: 'pm/bom/submitECRDOC.action',
				resSubmitUrl: 'pm/bom/resSubmitECRDOC.action',
				getIdUrl: 'common/getId.action?seq=ECR_SEQ',
				keyField: 'ecr_id',
				codeField: 'ecr_code',
				statusField: 'ecr_checkstatuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'ecd_detno',
				keyField: 'ecd_id',
				mainField: 'ecd_ecrid',
				necessaryField: 'ecd_doc'
			}]
		}); 
		me.callParent(arguments); 
	} 
});