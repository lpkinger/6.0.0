Ext.define('erp.view.scm.sale.SaleDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				//saveUrl:'scm/sale/saveSaleDetailSet.action',
				updateUrl: 'scm/sale/updateSaleDetailSet.action',
				getIdUrl: 'common/getId.action?seq=SALEDETAIL_SEQ',
				keyField: 'sd_id',
				codeField: 'sd_code',
				statusField:'sd_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'sdd_detno',
				necessaryField: 'sdd_delivery',
				keyField: 'sdd_id',
				mainField: 'sdd_sdid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});