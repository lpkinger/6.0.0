Ext.define('erp.view.scm.sale.SaleRemark',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',	
				saveUrl: 'scm/sale/saveSaleRemark.action',
				deleteUrl: 'scm/sale/deleteSaleRemark.action',
				updateUrl: 'scm/sale/updateSaleRemark.action',
				getIdUrl: 'common/getId.action?seq=SALEREMARK_SEQ',
				keyField: 'sr_id',
				codeField:'sr_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'srd_detno',
				keyField: 'srd_id',
				mainField: 'srd_srid',
				necessaryField:'srd_remark'
			}]
		}); 
		me.callParent(arguments); 
	} 
});