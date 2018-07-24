Ext.define('erp.view.pm.make.ModifyForm',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					_noc: _noc,
					saveUrl: 'pm/make/saveModifyMaterial.action?caller=MakeMaterial!Modify',	
					updateUrl:'pm/make/deleteModifyMaterial.action'	
				}	]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});