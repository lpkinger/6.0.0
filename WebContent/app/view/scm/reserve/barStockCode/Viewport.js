Ext.define('erp.view.scm.reserve.barStockCode.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',  
				xtype: 'erpBarStockCodeFormPanel',
				anchor: '100% 40%',
				getIdUrl: 'common/getId.action?seq=BARSTOCKTAKINGDETAIL_SEQ',
				keyField: 'bsd_id'
			},{
				region: 'center', 
				xtype: 'erpBarStockCodeGridPanel',
				anchor: '100% 60%', 
				detno: 'bdd_detno',
				keyField: 'bdd_id',
				mainField: 'bdd_bsdid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});