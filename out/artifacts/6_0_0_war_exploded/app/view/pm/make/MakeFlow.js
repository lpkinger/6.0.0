Ext.define('erp.view.pm.make.MakeFlow',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 60%',
				_noc: _noc,
				saveUrl: 'common/batchSave.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				printUrl:'pm/make/printMakeflow.action',
				codeField:'mf_code',
				/*saveUrl: 'pm/make/saveMakeSubMaterial.action?caller=' + parent.window.caller,
				updateUrl:'pm/make/deleteMakeSubMaterial.action?caller='+parent.window.caller*/
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 40%', 
				detno: 'mf_detno'
			}]
		}); 
		me.callParent(arguments); 
	} 
});