Ext.define('erp.view.fs.cust.HXFinancCondition',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'border',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'tabpanel',
				region: 'north',
				height: 180,
				items: [{
					title:'资产运营效率',
					xtype : 'erpGridPanel2',
					id: 'fsfinanceitems_yyxl',
					caller:'HXFinancCondition',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=") + " and fi_class='核心企业额度申请' and fi_type='资产' and fi_typedet='资产运营效率'":'',
					bbar: null,
					keyField : 'pt_id',
					mainField : 'pt_caid'
				},{
					title:'偿债能力分析',
					xtype : 'erpGridPanel2',
					id: 'fsfinanceitems_cznl',
					caller:'HXFinancCondition',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=") + " and fi_class='核心企业额度申请' and fi_type='负债' and fi_typedet='偿债能力分析'":'',
					bbar: null,
					keyField : 'fi_id',
					mainField : 'fi_caid'
				},{
					title:'盈利能力分析',
					xtype : 'erpGridPanel2',
					id: 'fsfinanceitems_ylnl',
					caller:'HXFinancCondition',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=") + " and fi_class='核心企业额度申请' and fi_type='利润' and fi_typedet='盈利能力分析'":'',
					bbar: null,
					keyField : 'fi_id',
					mainField : 'fi_caid'
				}]
			},{
				xtype: 'erpFormPanel',
				readOnly:readOnly==1,
				keyField: 'fc_caid',
				region: 'center'
			}]
		}); 
		this.callParent(arguments); 
	}
});