Ext.define('erp.view.scm.purchase.Country',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/saveCountry.action',
				deleteUrl: 'scm/purchase/deleteCountry.action',
				updateUrl: 'scm/purchase/updateCountry.action',
				getIdUrl: 'common/getId.action?seq=Countrys_SEQ',
				keyField: 'co_id',
				codeField: 'co_code',
			}]
		}); 
		me.callParent(arguments); 
	} 
});