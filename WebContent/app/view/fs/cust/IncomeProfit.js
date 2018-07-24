Ext.define('erp.view.fs.cust.IncomeProfit',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				fieldDefaults : {
					fieldStyle : "background:#FFFAFA;color:#515151;",
					focusCls: 'x-form-field-cir-focus',
					labelAlign : "right",
					labelWidth : 120,
					msgTarget: 'side',
					blankText : $I18N.common.form.blankText
				},
				readOnly:readOnly==1
			},{
				xtype:'tabpanel',
				anchor: '100% 70%',
				items:[{
					title:'银行流水情况审核',
					xtype : 'erpGridPanel2',
					id: 'bankflow',
					keyField : 'bf_id',
					mainField : 'bf_caid',
					bbar:null
				},{
					title:'纳税情况情况审核',
					xtype : 'erpGridPanel2',
					caller:'IncomeProfit!PayTaxes',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=").replace('bf_caid','pt_caid'):'',
					//bbar: {xtype: 'erpToolbar',id:'toolbar1'},
					id: 'paytaxes',
					keyField : 'pt_id',
					mainField : 'pt_caid'
				}]
			}]
		}); 
		this.callParent(arguments); 
	}
});