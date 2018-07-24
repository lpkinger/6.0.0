Ext.define('erp.view.drp.distribution.PricePolicy',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'drp/distribution/savePricePolicy.action',
					deleteUrl: 'drp/distribution/deletePricePolicy.action',
					updateUrl: 'drp/distribution/updatePricePolicy.action',
					auditUrl: 'drp/distribution/auditPricePolicy.action',
					resAuditUrl: 'drp/distribution/resAuditPricePolicy.action',
					submitUrl: 'drp/distribution/submitPricePolicy.action',
					resSubmitUrl: 'drp/distribution/resSubmitPricePolicy.action',
					bannedUrl: 'drp/distribution/bannedPricePolicy.action',
					resBannedUrl: 'drp/distribution/resBannedPricePolicy.action',
					getIdUrl: 'common/getId.action?seq=PRICEPOLICY_SEQ',
					keyField: 'pp_id',
					codeField: 'pp_code',
					statusField:'pp_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});