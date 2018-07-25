Ext.define('erp.view.fs.loaded.TransactionCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 25%',
				updateUrl: 'fs/loaded/updateTransactionCheck.action?caller='+caller+'&_noc=1'
			},{
				xtype : 'erpGridPanel2',
				anchor: '100% 75%', 
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				onExport: function(caller, type, condition){
						this.BaseUtil.createExcel(caller, type, condition,'买卖双方交易检查'+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
					},
				keyField : 'lft_id',
				mainField : 'lft_liid'
			}]
		}); 
		this.callParent(arguments); 
	}
});