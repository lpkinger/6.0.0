Ext.define('erp.view.scm.qc.QuaAQL',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/qc/saveAql.action?caller=' +caller,
				deleteUrl: 'scm/qc/deleteAql.action?caller=' +caller,
				updateUrl: 'scm/qc/updateAql.action?caller=' +caller,
				auditUrl: 'scm/qc/auditAql.action?caller=' +caller,
				resAuditUrl: 'scm/qc/resAuditAql.action?caller=' +caller,
				submitUrl: 'scm/qc/submitAql.action?caller=' +caller,
				resSubmitUrl: 'scm/qc/resSubmitAql.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=AQL_SEQ',
				codeField: 'al_code',
				keyField: 'al_id',
				statusField: 'al_status'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'ad_detno',
				necessaryField: 'ad_quotecode',
				keyField: 'ad_id',
				mainField: 'ad_alid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});