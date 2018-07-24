Ext.define('erp.view.pm.bom.ECNDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'pm/bom/saveECNDOC.action',
				deleteUrl: 'pm/bom/deleteECNDOC.action',
				updateUrl: 'pm/bom/updateECNDOC.action',
				auditUrl: 'pm/bom/auditECNDOC.action',
				resAuditUrl: 'pm/bom/resAuditECNDOC.action',
				submitUrl: 'pm/bom/submitECNDOC.action',
				resSubmitUrl: 'pm/bom/resSubmitECNDOC.action',
				getIdUrl: 'common/getId.action?seq=ECN_SEQ',
				keyField: 'ecn_id',
				codeField: 'ecn_code',
				statusField: 'ecn_checkstatuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'ecd_detno',
				keyField: 'ecd_id',
				mainField: 'ecd_ecnid',
				necessaryField: 'ecd_doc'
			}]
		}); 
		me.callParent(arguments); 
	} 
});