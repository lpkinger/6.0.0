Ext.define('erp.view.scm.sale.SalePayment',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				_noc: 1,				
				updateUrl: 'scm/sale/updateSalePayment.action?caller=' + caller+'&_noc=1',		
				getIdUrl: 'common/getId.action?seq=SalePayment_SEQ',
				keyField: 'sp_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});