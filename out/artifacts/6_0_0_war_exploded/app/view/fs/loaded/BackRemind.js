Ext.define('erp.view.fs.loaded.BackRemind',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%'
			},{
				xtype : 'erpGridPanel2',
				anchor: '100% 80%', 
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				keyField : 'lrd_id',
				mainField : 'lrd_lrid'
			}]
		}); 
		this.callParent(arguments); 
	}
});