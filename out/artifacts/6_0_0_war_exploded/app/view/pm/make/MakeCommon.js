Ext.define('erp.view.pm.make.MakeCommon',{ 
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
				saveUrl: 'pm/make/saveMakeSubMaterial.action?caller=' + parent.window.caller,
				deleteUrl:'pm/make/deleteMakeSubMaterial.action?caller='+parent.window.caller
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 40%', 
				detno: 'mp_detno'
			}]
		}); 
		me.callParent(arguments); 
	} 
});