Ext.define('erp.view.fs.cust.BusinessCondition',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'border',
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					keyField: 'bc_id',
					readOnly:readOnly==1,
					region: 'center'
				},{
					xtype:'tabpanel',
					region: 'south',
					height: 180,
					items:[{
						title:'主营产品/服务结构',
						xtype : 'erpGridPanel2',
						id: 'BC_ProductMix',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						keyField : 'pm_id',
						mainField : 'pm_bcid'
					},{
						title:'前五大供应商',
						xtype : 'erpGridPanel2',
						id: 'BC_UpstreamCust',
						caller:'BC_UpstreamCust',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('pm_bcid','udc_bcid')+" and udc_kind='上游'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar1'},
						keyField : 'udc_id',
						mainField : 'udc_bcid'
					},{
						title:'前五大客户',
						xtype : 'erpGridPanel2',
						id: 'BC_DownstreamCust',
						caller:'BC_DownstreamCust',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('pm_bcid','udc_bcid')+" and udc_kind='下游'":'',
						bbar: {xtype: 'erpToolbar',id:'toolbar2'},
						keyField : 'udc_id',
						mainField : 'udc_bcid'
					},{
						title:'与买方企业目前的应收账款情况',
						id: 'BC_ProposedFinance',
						xtype : 'erpGridPanel2',
						caller:'BC_ProposedFinance',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('pm_bcid','pf_bcid'):'',
						bbar: {xtype: 'erpToolbar',id:'toolbar3'},
						keyField : 'pf_id',
						mainField : 'pf_bcid'
					},{
						title:'与买方企业历史交易情况',
						id: 'BC_YearDeal',
						xtype : 'erpGridPanel2',
						caller:'BC_YearDeal',
						plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
						}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
						condition:condition!=null?condition.replace(/IS/g, "=").replace('pm_bcid','yd_bcid'):'',
						bbar: {xtype: 'erpToolbar',id:'toolbar4'},
						keyField : 'yd_id',
						mainField : 'yd_bcid'
					}]
				//在建项目及近期重大投资计划说明--BC_ProjectPlan
				//进出口业务情况调查 --BC_ImportExport
				}]
			}); 
		this.callParent(arguments); 
	}
});