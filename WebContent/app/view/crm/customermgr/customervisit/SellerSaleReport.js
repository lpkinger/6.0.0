Ext.define('erp.view.crm.customermgr.customervisit.SellerSaleReport',{ 
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
					saveUrl: 'crm/customermgr/saveSellerSaleReport.action',
					deleteUrl: 'crm/marketmgr/deleteSellerSaleReport.action',
					updateUrl: 'crm/marketmgr/updateSellerSaleReport.action',					
					getIdUrl: 'common/getId.action?seq=projschedule_SEQ',
					keyField: 'ps_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});