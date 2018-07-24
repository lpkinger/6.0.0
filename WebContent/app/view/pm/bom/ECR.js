Ext.define('erp.view.pm.bom.ECR',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'pm/bom/saveECR.action',
				deleteUrl: 'pm/bom/deleteECR.action',
				updateUrl: 'pm/bom/updateECR.action',
				auditUrl: 'pm/bom/auditECR.action',
				resAuditUrl: 'pm/bom/resAuditECR.action',
				printUrl: 'pm/bom/printsingleBOM.action',
				submitUrl: 'pm/bom/submitECR.action',
				resSubmitUrl: 'pm/bom/resSubmitECR.action',
				getIdUrl: 'common/getId.action?seq=ECR_SEQ',
				keyField: 'ecr_id',
				codeField: 'ecr_code',
				statusField: 'ecr_checkstatuscode'
			}/*,{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'ecrd_detno',
				keyField: 'ecrd_id',
				mainField: 'ecrd_ecrid',
				necessaryField: 'ecrd_mothercode'
			}*/]
		}); 
		me.callParent(arguments); 
	} 
});