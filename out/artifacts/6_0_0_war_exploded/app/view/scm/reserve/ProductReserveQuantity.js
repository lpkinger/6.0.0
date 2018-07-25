Ext.define('erp.view.scm.reserve.ProductReserveQuantity',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',			
				updateUrl: 'scm/reserve/updateProductReserveQuantity.action',
				getIdUrl: 'common/getId.action?seq=PRODUCTWH_SEQ',
				keyField: 'pw_id',
				codeField: 'pw_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});