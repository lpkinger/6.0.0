Ext.define('erp.view.scm.reserve.BarcodeRule',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/reserve/saveNoRule.action?caller=' +caller,
				deleteUrl: 'scm/reserve/deleteNoRule.action?caller=' +caller,
				updateUrl: 'scm/reserve/updateNoRule.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=NoRule_SEQ',
				keyField: 'nr_id',
				codeField: 'nr_code',
			},{			
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'nrd_detno',
				keyField: 'nrd_id',
				mainField: 'nrd_nrid',
				allowExtraButtons : true			
			}]
		}); 
		me.callParent(arguments); 
	} 
});