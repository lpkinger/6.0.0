Ext.define('erp.view.scm.purchase.MakeExp',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/purchase/saveMakeExp.action',
				deleteUrl: 'scm/purchase/deleteMakeExp.action',
				updateUrl: 'scm/purchase/updateMakeExp.action',
				auditUrl: 'scm/purchase/auditMakeExp.action',
				printUrl: 'scm/purchase/printMakeExp.action',
				resAuditUrl: 'scm/purchase/resAuditMakeExp.action',
				submitUrl: 'scm/purchase/submitMakeExp.action',
				resSubmitUrl: 'scm/purchase/resSubmitMakeExp.action',
				bannedUrl: 'scm/purchase/bannedMakeExp.action',
				resBannedUrl: 'scm/purchase/resBannedMakeExp.action',
				getIdUrl: 'common/getId.action?seq=MAKEEXP_SEQ',
				keyField: 'me_id',
				codeField: 'me_code',
				statusField: 'me_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'ma_detno',
				//necessaryField: 'mm_prodcode',
				keyField: 'ma_id',
				mainField: 'ma_expcode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});