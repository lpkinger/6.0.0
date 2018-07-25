Ext.define('erp.view.fs.loaded.FinancialCheck',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'border',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype:'tabpanel',
				region: 'north',
				height: 230,
				items:[{
					title:'资产结构指标',
					xtype : 'erpGridPanel2',
					id: 'assets',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=") +" and lfi_type='资产结构指标'":'',
					onExport: function(caller, type, condition){
						this.BaseUtil.createExcel(caller, type, condition,'财务状况检查-'+this.title+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
					},
					keyField : 'lfi_id',
					mainField : 'lfi_liid'
				},{
					title:'权益结构指标',
					xtype : 'erpGridPanel2',
					id: 'rights',
					caller: 'FinancialCheck',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=") +" and lfi_type='权益结构指标'":'',
					onExport: function(caller, type, condition){
						this.BaseUtil.createExcel(caller, type, condition,'财务状况检查-'+this.title+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
					},
				   	bbar:{xtype: 'erpToolbar',id:'toolbar1'},
					keyField : 'fi_id',
					mainField : 'fi_caid'
				},{
					title:'损益状况偿还指标',
					id: 'profit',
					caller: 'FinancialCheck',
					xtype : 'erpGridPanel2',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=") +" and lfi_type='损益状况偿还指标'":'',
					onExport: function(caller, type, condition){
						this.BaseUtil.createExcel(caller, type, condition,'财务状况检查-'+this.title+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
					},
					bbar:{xtype: 'erpToolbar',id:'toolbar2'},
					keyField : 'lfi_id',
					mainField : 'lfi_liid'
				},{
					title:'现金流量指标',
					id: 'cashflow',
					caller: 'FinancialCheck',
					xtype : 'erpGridPanel2',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=") +" and lfi_type='现金流量指标'":'',
					onExport: function(caller, type, condition){
						this.BaseUtil.createExcel(caller, type, condition,'财务状况检查-'+this.title+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
					},
					bbar:{xtype: 'erpToolbar',id:'toolbar3'},
					keyField : 'lfi_id',
					mainField : 'lfi_liid'
				},{
					title:'负债结构指标',
					id: 'liabilities',
					caller: 'FinancialCheck',
					xtype : 'erpGridPanel2',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=") +" and lfi_type='负债结构指标'":'',
					onExport: function(caller, type, condition){
						this.BaseUtil.createExcel(caller, type, condition,'财务状况检查-'+this.title+ Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
					},
					bbar:{xtype: 'erpToolbar',id:'toolbar4'},
					keyField : 'lfi_id',
					mainField : 'lfi_liid'
				}]
			},{
				xtype: 'erpFormPanel',
				region: 'center',
				updateUrl: 'fs/loaded/updateInvestReport.action?caller='+caller+'&_noc=1'
			}]
		}); 
		this.callParent(arguments); 
	}
});