Ext.define('erp.view.scm.sale.SaleForecastKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/sale/saveSaleForecastKind.action',
				deleteUrl: 'scm/sale/deleteSaleForecastKind.action',
				updateUrl: 'scm/sale/updateSaleForecastKind.action',
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				bannedUrl: 'common/bannedCommon.action?caller='+caller,
				resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=SALEFORECASTKIND_SEQ',
				keyField: 'sk_id',
			    codeField: 'sk_code'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});