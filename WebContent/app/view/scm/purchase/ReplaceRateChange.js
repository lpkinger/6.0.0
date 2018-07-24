Ext.define('erp.view.scm.purchase.ReplaceRateChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/purchase/saveReplaceRateChange.action',
				deleteUrl: 'scm/purchase/deleteReplaceRateChange.action',
				updateUrl: 'scm/purchase/updateReplaceRateChange.action',
				auditUrl: 'scm/purchase/auditReplaceRateChange.action',
				resAuditUrl: 'scm/purchase/resAuditReplaceRateChange.action',
				submitUrl: 'scm/purchase/submitReplaceRateChange.action',
				resSubmitUrl: 'scm/purchase/resSubmitReplaceRateChange.action',
				bannedUrl: 'scm/purchase/bannedReplaceRateChange.action',
				resBannedUrl: 'scm/purchase/resBannedReplaceRateChange.action',
				getIdUrl: 'common/getId.action?seq=ReplaceRateChange_SEQ',
				codeField: 'rc_code',
				keyField: 'rc_id',
				statusField: 'rc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'rd_detno',
				necessaryField: 'rd_prodcode',
				allowExtraButtons: true,
				keyField: 'rd_id',
				mainField: 'rd_rcid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});