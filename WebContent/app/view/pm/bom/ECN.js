Ext.define('erp.view.pm.bom.ECN',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'pm/bom/saveECN.action',
				deleteUrl: 'pm/bom/deleteECN.action',
				updateUrl: 'pm/bom/updateECN.action',
				auditUrl: 'pm/bom/auditECN.action',
				resAuditUrl: 'pm/bom/resAuditECN.action',
				submitUrl: 'pm/bom/submitECN.action',
				printUrl: 'pm/bom/printECN.action',
				resSubmitUrl: 'pm/bom/resSubmitECN.action',
				getIdUrl: 'common/getId.action?seq=ECN_SEQ',
				keyField: 'ecn_id',
				codeField: 'ecn_code',
				statusField: 'ecn_checkstatuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'ed_detno',
				keyField: 'ed_id',
				mainField: 'ed_ecnid',
				necessaryField: 'ed_type'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});