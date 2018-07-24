Ext.define('erp.view.scm.purchase.VerifyApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/purchase/saveVerifyApply.action?caller=' +caller,
				deleteUrl: 'scm/purchase/deleteVerifyApply.action?caller=' +caller,
				updateUrl: 'scm/purchase/updateVerifyApply.action?caller=' +caller,
				auditUrl: 'scm/purchase/auditVerifyApply.action?caller=' +caller,
				resAuditUrl: 'scm/purchase/resAuditVerifyApply.action?caller=' +caller,
				submitUrl: 'scm/purchase/submitVerifyApply.action?caller=' +caller,
				printUrl: 'scm/purchase/printVerifyApply.action?caller=' +caller,
				printBarUrl: 'scm/purchase/printBar.action?caller=' +caller,
				resSubmitUrl: 'scm/purchase/resSubmitVerifyApply.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=VERIFYAPPLY_SEQ',
				keyField: 'va_id',
				codeField: 'va_code',
				statusField: 'va_status',
				statuscodeField: 'va_statuscode'
			},{
				xtype: 'erpGridPanel2',
				allowExtraButtons: true,
				//deleteBeforeImport : true ,
				anchor: '100% 70%',
				binds: [{
					refFields:['vad_andid'],
					fields:['vad_pucode','vad_pudetno']
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});