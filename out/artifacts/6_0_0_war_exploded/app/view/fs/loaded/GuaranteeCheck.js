Ext.define('erp.view.fs.loaded.GuaranteeCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				updateUrl: 'fs/loaded/updateGuaranteeCheck.action?caller='+caller+'&_noc=1'
			},{
				xtype : 'erpGridPanel2',
				anchor: '100% 65%', 
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,'担保条件检查'+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				keyField : 'lft_id',
				mainField : 'lft_liid'
			}]
		}); 
		this.callParent(arguments); 
	}
});