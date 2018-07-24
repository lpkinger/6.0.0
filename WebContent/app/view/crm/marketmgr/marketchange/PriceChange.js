Ext.define('erp.view.crm.marketmgr.marketchange.PriceChange',{ 
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
					anchor: '100% 40%',
					saveUrl: 'crm/marketmgr/savePriceChange.action',
					deleteUrl: 'crm/marketmgr/deletePriceChange.action',
					updateUrl: 'crm/marketmgr/updatePriceChange.action',
					auditUrl: 'crm/marketmgr/auditPriceChange.action',
					printUrl: 'crm/marketmgr/printPriceChange.action',
					resAuditUrl: 'crm/marketmgr/resAuditPriceChange.action',
					submitUrl: 'crm/marketmgr/submitPriceChange.action',
					resSubmitUrl: 'crm/marketmgr/resSubmitPriceChange.action',
					getIdUrl: 'common/getId.action?seq=PriceChange_SEQ',
					keyField: 'pc_id',
					codeField: 'pc_code',
					statusField: 'pc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'pcd_detno',
					necessaryField: 'pcd_prodcode',
					keyField: 'pcd_id',
					mainField: 'pcd_pcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});