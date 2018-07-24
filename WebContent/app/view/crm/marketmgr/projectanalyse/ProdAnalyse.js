Ext.define('erp.view.crm.marketmgr.projectanalyse.ProdAnalyse',{ 
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
					saveUrl: 'crm/marketmgr/saveProdAnalyse.action',
					deleteUrl: 'crm/marketmgr/deleteProdAnalyse.action',
					updateUrl: 'crm/marketmgr/updateProdAnalyse.action',
					getIdUrl: 'common/getId.action?seq=ProdAnalyse_SEQ',
					auditUrl: 'crm/marketmgr/auditProdAnalyse.action',
					resAuditUrl: 'crm/marketmgr/resAuditProdAnalyse.action',
					submitUrl: 'crm/marketmgr/submitProdAnalyse.action',
					resSubmitUrl: 'crm/marketmgr/resSubmitProdAnalyse.action',
					keyField: 'pa_id',
					codeField: 'pa_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});