Ext.define('erp.view.crm.marketmgr.projectanalyse.MarketAnalyse',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'crm/marketmgr/saveMarketAnalyse.action',
					deleteUrl: 'crm/marketmgr/deleteMarketAnalyse.action',
					updateUrl: 'crm/marketmgr/updateMarketAnalyse.action',
					getIdUrl: 'common/getId.action?seq=MarketAnalyse_SEQ',
					auditUrl: 'crm/marketmgr/auditMarketAnalyse.action',
					resAuditUrl: 'crm/marketmgr/resAuditMarketAnalyse.action',
					submitUrl: 'crm/marketmgr/submitMarketAnalyse.action',
					resSubmitUrl: 'crm/marketmgr/resSubmitMarketAnalyse.action',
					keyField: 'ma_id',
					codeField: 'ma_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});