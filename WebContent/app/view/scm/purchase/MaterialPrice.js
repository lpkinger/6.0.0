Ext.define('erp.view.scm.purchase.MaterialPrice',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/purchase/saveMaterialPrice.action',
				deleteUrl: 'scm/purchase/deleteMaterialPrice.action',
				updateUrl: 'scm/purchase/updateMaterialPrice.action',
				auditUrl: 'scm/purchase/auditMaterialPrice.action',
				resAuditUrl: 'scm/purchase/resAuditMaterialPrice.action',
				submitUrl: 'scm/purchase/submitMaterialPrice.action',
				resSubmitUrl: 'scm/purchase/resSubmitMaterialPrice.action',
				bannedUrl: 'scm/purchase/bannedMaterialPrice.action',
				resBannedUrl: 'scm/purchase/resBannedMaterialPrice.action',
				getIdUrl: 'common/getId.action?seq=MaterialPRICE_SEQ',
				codeField: 'pp_code',
				keyField: 'pp_id',
				statusField: 'pp_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'ppd_detno',
				necessaryField: 'ppd_prodcode',
				allowExtraButtons: true,
				keyField: 'ppd_id',
				mainField: 'ppd_ppid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});