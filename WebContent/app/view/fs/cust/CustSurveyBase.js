Ext.define('erp.view.fs.cust.CustSurveyBase',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 70%',
				readOnly:readOnly==1
			},{
				xtype:'tabpanel',
			 	anchor : '100% 30%',
				items:[{
					title:'买方客户',  
					xtype: 'erpGridPanel2',
					id: 'mfcust',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					mainField: 'mf_cqid',
					keyField: 'mf_id',
					allowExtraButtons : true
				},{
					title:'担保情况',
					xtype : 'erpGridPanel2',
					caller:'Guarantee',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=").replace('mf_cqid','gu_caid'):'',
					bbar: {xtype: 'erpToolbar',id:'toolbar1'},
					id: 'guarantee',
					keyField : 'gu_id',
					mainField : 'gu_caid'
				}]
			}]
		}); 
		this.callParent(arguments); 
	}
});