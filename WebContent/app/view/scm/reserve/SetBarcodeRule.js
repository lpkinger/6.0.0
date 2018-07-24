Ext.define('erp.view.scm.reserve.SetBarcodeRule',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpSetBarcodeRuleForm',
				anchor: '100% 100%',
				saveUrl: 'scm/reserve/saveBarcodeRule.action?caller=' +caller,
				updateUrl: 'scm/reserve/updateBarcodeRule.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=BARCODESET_SEQ',
				keyField: 'bs_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});