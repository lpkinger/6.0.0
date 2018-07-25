Ext.define('erp.view.crm.marketmgr.marketresearch.MarketTaskReport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				//id:'purchaseViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 60%',
					_noc:1,
					saveUrl: 'crm/marketmgr/saveMarketTaskReport.action?caller='+caller+'&_noc=1',
					deleteUrl: 'crm/marketmgr/deleteMarketTaskReport.action?caller='+caller+'&_noc=1',
					updateUrl: 'crm/marketmgr/updateMarketTaskReport.action?caller='+caller+'&_noc=1',
					auditUrl: 'crm/marketmgr/auditMarketTaskReport.action?caller='+caller+'&_noc=1',
					resAuditUrl: 'crm/marketmgr/resAuditMarketTaskReport.action?caller='+caller+'&_noc=1',
					submitUrl: 'crm/marketmgr/submitMarketTaskReport.action?caller='+caller+'&_noc=1',
					resSubmitUrl: 'crm/marketmgr/resSubmitMarketTaskReport.action?caller='+caller+'&_noc=1',
					getIdUrl: 'common/getId.action?seq=MarketTaskReport_SEQ',
					keyField: 'mr_id',
					codeField: 'mr_code',
					statusField: 'mr_statuscode'
				},{
					_noc:1,
					xtype: 'erpGridPanel2',
					anchor: '100% 40%', 
					necessaryField: 'mrd_costname',
					keyField: 'mrd_id',
					mainField: 'mrd_mrid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});