Ext.define('erp.view.crm.marketmgr.marketresearch.MarketResearch',{ 
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
					saveUrl: 'crm/customermgr/saveMarketResearch.action',
					deleteUrl: 'crm/customermgr/deleteMarketResearch.action',
					updateUrl: 'crm/customermgr/updateMarketResearch.action',
					getIdUrl: 'common/getId.action?seq=MarketResearch_SEQ',
					auditUrl: 'crm/customermgr/auditMarketResearch.action',
					resAuditUrl: 'crm/customermgr/resAuditMarketResearch.action',
					submitUrl: 'crm/customermgr/submitMarketResearch.action',
					resSubmitUrl: 'crm/customermgr/resSubmitMarketResearch.action',
					keyField: 'mr_id',
					codeField: 'mr_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});