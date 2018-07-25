Ext.define('erp.view.fs.cust.HXCustSurveyBase',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 70%',
				saveUrl: 'fs/cust/saveHXSurveyBase.action?_noc=1',
				readOnly:readOnly==1
			},{
				xtype:'tabpanel',
			 	anchor : '100% 30%',
				items:[{
					title:'授信详情',  
					xtype: 'erpGridPanel2',
					id: 'hxfinancingsituation',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					mainField: 'fis_caid',
					keyField: 'fis_id',
					necessaryField: 'fis_condition'
				},{
					title:'准入公司详情',
					xtype : 'erpGridPanel2',
					caller:'AccessCompany',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=").replace('fis_caid','ac_caid'):'',
					bbar: {xtype: 'erpToolbar',id:'toolbar1'},
					id: 'accesscompany',
					keyField : 'ac_id',
					mainField : 'ac_caid'
				},{
					title:'纳税详情',
					xtype : 'erpGridPanel2',
					caller:'HXPayTaxes',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=").replace('fis_caid','ct_caid'):'',
					bbar: {xtype: 'erpToolbar',id:'toolbar1'},
					id: 'paytaxes',
					keyField : 'ct_id',
					mainField : 'ct_caid'
				}]
			}]
		}); 
		this.callParent(arguments); 
	}
});