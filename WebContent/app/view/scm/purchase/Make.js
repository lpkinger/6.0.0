Ext.define('erp.view.scm.purchase.Make',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/purchase/saveMake.action',
				deleteUrl: 'scm/purchase/deleteMake.action',
				updateUrl: 'scm/purchase/updateMake.action',
				auditUrl: 'scm/purchase/auditMake.action',
				printUrl: 'scm/purchase/printMake.action',
				resAuditUrl: 'scm/purchase/resAuditMake.action',
				submitUrl: 'scm/purchase/submitMake.action',
				resSubmitUrl: 'scm/purchase/resSubmitMake.action',
				bannedUrl: 'scm/purchase/bannedMake.action',
				resBannedUrl: 'scm/purchase/resBannedMake.action',
				getIdUrl: 'common/getId.action?seq=MAKE_SEQ',
				keyField: 'ma_id',
				codeField: 'ma_code',
				statusField: 'ma_checkstatuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'mm_detno',
				necessaryField: 'mm_prodcode',
				keyField: 'mm_id',
				mainField: 'mm_maid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});