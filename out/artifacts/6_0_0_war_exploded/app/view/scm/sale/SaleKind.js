Ext.define('erp.view.scm.sale.SaleKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/sale/saveSaleKind.action',
				deleteUrl: 'scm/sale/deleteSaleKind.action',
				updateUrl: 'scm/sale/updateSaleKind.action',
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				bannedUrl: 'common/bannedCommon.action?caller='+caller,
				resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=SALEKIND_SEQ',
				keyField: 'sk_id',
			    codeField: 'sk_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});