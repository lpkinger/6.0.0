Ext.define('erp.view.b2b.sale.PurchaseForecastDown',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 	
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',				
				getIdUrl: 'common/getId.action?seq=PurchaseForecastDown_SEQ',
				keyField: 'pf_id',
				codeField: 'pf_code'				
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'pfd_detno',
				keyField: 'pfd_id',
				mainField: 'pfd_pfid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});