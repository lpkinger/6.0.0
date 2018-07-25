Ext.define('erp.view.pm.mould.ModifyProcessing',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 25%',
				saveUrl: 'pm/mould/saveBOMMouldProcessing.action?caller=BOMMould!Processingssss',
				deleteUrl: 'pm/mould/deleteBOMMouldProcessing.action?caller=BOMMould!Processing',
				updateUrl: 'pm/mould/updateBOMMouldProcessing.action?caller=BOMMould!Processing',		
				getIdUrl: 'common/getId.action?seq=Bommouldprocess_SEQ',
				keyField: 'bd_id',
				codeField: 'bd_soncode',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 75%', 
				detno: 'bm_detno',
				keyField: 'bm_id',
				mainField: 'bm_bdid',
				allowExtraButtons : true
//				necessaryField: 'bm_prodcode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});