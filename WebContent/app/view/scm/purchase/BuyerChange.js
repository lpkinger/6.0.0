Ext.define('erp.view.scm.purchase.BuyerChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/saveBuyerChange.action',
				deleteUrl: 'scm/purchase/deleteBuyerChange.action',
				updateUrl: 'scm/purchase/updateBuyerChange.action',
				auditUrl: 'scm/purchase/auditBuyerChange.action',
				printUrl: 'scm/purchase/printBuyerChange.action',
				resAuditUrl: 'scm/purchase/resAuditBuyerChange.action',
				submitUrl: 'scm/purchase/submitBuyerChange.action',
				resSubmitUrl: 'scm/purchase/resSubmitBuyerChange.action',
				getIdUrl: 'common/getId.action?seq=BuyerChange_SEQ',
				codeField: 'bc_code',
				keyField: 'bc_id',
				statusField: 'bc_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});