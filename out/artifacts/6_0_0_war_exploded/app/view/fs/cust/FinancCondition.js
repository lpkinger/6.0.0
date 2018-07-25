Ext.define('erp.view.fs.cust.FinancCondition',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'border',
	initComponent : function(){ 
			Ext.apply(this, { 
				items:[{
					xtype: 'tabpanel',
					region: 'north',
					height: 230,
					items: [{
						title:'资产情况',
						xtype : 'erpGridPanel2',
						id: 'fsfinanceitems_ldzc',
						caller:'FsFinanceItems_item',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','fi_caid') + " and fi_kind='资产' order by fi_detno":'',
						bbar: null,
						mainField : 'fi_caid'
					},{
						title:'负债情况',
						xtype : 'erpGridPanel2',
						id: 'fsfinanceitems_ldfz',
						caller:'FsFinanceItems_item',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','fi_caid') + " and fi_kind='负债' order by fi_detno ":'',
						bbar: null,
						mainField : 'fi_caid'
					},{
						title:'损益',
						xtype : 'erpGridPanel2',
						id: 'fsfinanceitems_sy',
						caller:'FsFinanceItems_item',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','fi_caid') + " and fi_kind='利润' order by fi_detno ":'',
						bbar: null,
						mainField : 'fi_caid'
					},{
						title:'应收账款',
						xtype : 'erpGridPanel2',
						id: 'al_accountinforar',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") +" and ai_kind='应收账款'":'',
						keyField : 'ai_id',
						mainField : 'ai_alid',
						allowExtraButtons: true
					},{
						title:'其他应收账款',
						xtype : 'erpGridPanel2',
						id: 'al_accountinforothar',
						caller:'AL_AccountInforOthAR',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") +" and ai_kind='其他应收账款'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar1'},
						keyField : 'ai_id',
						mainField : 'ai_alid',
						features : [{
							ftype : 'summary',
							showSummaryRow : false,//不显示默认合计行
							generateSummaryData: function(){
								return {};
							}
						}],
						listeners: {
							afterrender: function() {
								console.log(this);
							}
						}
					},{
						title:'预付账款',
						id: 'al_accountinforpp',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforPP',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") +" and ai_kind='预付账款'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar2'},
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'存货',
						id: 'al_accountinforinv',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforInv',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") +" and ai_kind='存货'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar3'},
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'固定资产',
						id: 'al_accountinforfix',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforFix',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='固定资产'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar4'},
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'短期借款',
						id: 'al_accountinforcb',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforCB',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='短期借款'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar5'},
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'应付账款',
						xtype : 'erpGridPanel2',
						id: 'al_accountinforap',
						caller:'AL_AccountInforAP',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='应付账款'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar6'},
						keyField : 'ai_id',
						mainField : 'ai_alid',
						allowExtraButtons: true
					},{
						title:'其他应付账款',
						xtype : 'erpGridPanel2',
						id: 'al_accountinforothap',
						caller:'AL_AccountInforOthAP',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='其他应付账款'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar7'},
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'长期借款',
						id: 'al_accountinforlong',
						xtype : 'erpGridPanel2',
						caller:'AL_AccountInforLong',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=") + " and ai_kind='长期借款'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar8'},
						keyField : 'ai_id',
						mainField : 'ai_alid'
					},{
						title:'银行流水情况审核',
						xtype : 'erpGridPanel2',
						id: 'bankflow',
						caller:'IncomeProfit',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','bf_caid'):'',
						bbar: {xtype: 'erpToolbar',id:'toolbar9'},
						keyField : 'bf_id',
						mainField : 'bf_caid'
					},{
						title:'纳税情况',
						xtype : 'erpGridPanel2',
						id: 'paytaxes',
						caller:'IncomeProfit!PayTaxes',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','pt_caid') :'',
						bbar: {xtype: 'erpToolbar',id:'toolbar10'},
						keyField : 'pt_id',
						mainField : 'pt_caid'
					},{
						title:'资产运营效率',
						xtype : 'erpGridPanel2',
						id: 'fsfinanceitems_yyxl',
						caller:'FsFinanceItems',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','fi_caid') + " and fi_class='保理额度申请' and fi_type='资产' and fi_typedet='资产运营效率'":'',
						bbar: null,
						keyField : 'pt_id',
						mainField : 'pt_caid'
					},{
						title:'偿债能力分析',
						xtype : 'erpGridPanel2',
						id: 'fsfinanceitems_cznl',
						caller:'FsFinanceItems',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','fi_caid') + " and fi_class='保理额度申请' and fi_type='负债' and fi_typedet='偿债能力分析'":'',
						bbar: null,
						keyField : 'fi_id',
						mainField : 'fi_caid'
					},{
						title:'盈利能力分析',
						xtype : 'erpGridPanel2',
						id: 'fsfinanceitems_ylnl',
						caller:'FsFinanceItems',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','fi_caid') + " and fi_class='保理额度申请' and fi_type='利润' and fi_typedet='盈利能力分析'":'',
						bbar: null,
						keyField : 'fi_id',
						mainField : 'fi_caid'
					},{
						title:'现金流量分析',
						xtype : 'erpGridPanel2',
						id: 'fsfinanceitems_xjll',
						caller:'FsFinanceItems',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('ai_alid','fi_caid') + " and fi_class='保理额度申请' and fi_type='现金流' and fi_typedet='结构及流动性'":'',
						bbar: null,
						keyField : 'fi_id',
						mainField : 'fi_caid'
					}]
				},{
					xtype: 'erpFormPanel',
					saveUrl: 'fs/cust/saveFinancCondition.action?_noc=1',
					readOnly:readOnly==1,
					keyField: 'fc_caid',
					region: 'center'
				}]
			}); 
		this.callParent(arguments); 
	}
});