Ext.define('erp.view.fs.loaded.SettleAccountCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%'
			},{
				xtype : 'erpGridPanel2',
				title : '转入资金',
				id:'incrash',
				anchor: '100% 30%', 
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				condition:condition!=null?condition.replace(/IS/g, "=") +" and sta_type='转入资金'":'',
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,'主要账户结算检查-'+this.title + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				keyField : 'sta_id',
				mainField : 'sta_liid'
			},{
				xtype : 'erpGridPanel2',
				title : '支付资金',
				id:'outcrash',
				anchor: '100% 30%', 
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				condition:condition!=null?condition.replace(/IS/g, "=") +" and sta_type='支付资金'":'',
				onExport: function(caller, type, condition){
					this.BaseUtil.createExcel(caller, type, condition,'主要账户结算检查-'+this.title+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				},
				bbar:{xtype: 'erpToolbar',id:'toolbar1'},
				keyField : 'sta_id',
				mainField : 'sta_liid'
			}]
		}); 
		this.callParent(arguments); 
	}
});