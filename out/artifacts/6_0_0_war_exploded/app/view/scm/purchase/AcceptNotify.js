Ext.define('erp.view.scm.purchase.AcceptNotify',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/purchase/saveAcceptNotify.action',
				deleteUrl: 'scm/purchase/deleteAcceptNotify.action',
				updateUrl: 'scm/purchase/updateAcceptNotify.action',
				auditUrl: 'scm/purchase/auditAcceptNotify.action',
				printUrl: 'scm/purchase/printAcceptNotify.action',
				resAuditUrl: 'scm/purchase/resAuditAcceptNotify.action',
				submitUrl: 'scm/purchase/submitAcceptNotify.action',
				resSubmitUrl: 'scm/purchase/resSubmitAcceptNotify.action',
				getIdUrl: 'common/getId.action?seq=ACCEPTNOTIFY_SEQ',
				codeField: 'an_code',
				keyField: 'an_id',
				statusField: 'an_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'and_detno',
				necessaryField: 'and_prodcode',
				keyField: 'and_id',
				mainField: 'and_anid',
				allowExtraButtons:true
			}]
		}); 
		me.callParent(arguments); 
	} 
});