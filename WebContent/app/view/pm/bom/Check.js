Ext.define('erp.view.pm.bom.Check',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ECRViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 51%',
					saveUrl: 'pm/bom/saveCheck.action',
					deleteUrl: 'pm/bom/deleteCheck.action',
					updateUrl: 'pm/bom/updateCheck.action',
					printUrl: 'pm/bom/printsingleBOM.action',
					auditUrl: 'pm/bom/auditCheck.action',
					resAuditUrl: 'pm/bom/resAuditCheck.action',
					submitUrl: 'pm/bom/submitCheck.action',
					resSubmitUrl: 'pm/bom/resSubmitCheck.action',
					endUrl:'pm/bom/endCheck.action',
					resEndUrl:'pm/bom/resEndCheck.action',
					getIdUrl: 'common/getId.action?seq=ECR_SEQ',
					keyField: 'ecr_id',
					codeField: 'ecr_code',
					statusField: 'ecr_checkstatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 49%', 
					detno: 'ecrd_detno',
					keyField: 'ecrd_id',
					mainField: 'ecrd_ecrid',
					necessaryField: 'ecrd_type'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});