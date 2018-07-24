Ext.define('erp.view.pm.make.MakeMaterialCraft',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	requires: ['erp.view.core.grid.HeaderFilter'],
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
						region: 'north',   
						autoScroll:true,
						xtype: "erpMakeMaterialCraftFormPanel",  
				    	anchor: '100% 30%',
				    },{
					xtype: 'erpEditorColumnGridPanel',
					anchor: '100% 70%',
					condition:'1=2',
					version:'4.2',
					plugins: [Ext.create('erp.view.core.plugin.ProdOnhand'),
					          Ext.create('Ext.grid.plugin.CellEditing', {
					              clicksToEdit: 1
					          }), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				}]
			
			}] 
		}); 
		me.callParent(arguments); 
	} 
});