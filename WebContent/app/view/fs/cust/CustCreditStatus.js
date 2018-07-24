Ext.define('erp.view.fs.cust.CustCreditStatus',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'border',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				readOnly:readOnly==1,
				region: 'center'
			},{
				xtype:'tabpanel',
				region: 'south',
				height: 200,
				items:[{
					title:'借款人借款情况',
					width:'100%',
					id: 'customerborrowing',
					xtype : 'erpGridPanel2',
					keyField : 'cd_id',
					mainField : 'cb_caid'
				},{
					title:'对外担保情况',
					width:'100%',
					id: 'guaranty',
					xtype : 'erpGridPanel2',
					caller:'CustCreditStatus!Guaranty',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=").replace('cb_caid','cg_caid'):'',
					bbar: {xtype: 'erpToolbar',id:'toolbar1'},
					keyField : 'cg_id',
					mainField : 'cg_caid'
				}]
			}]
		}); 
		this.callParent(arguments); 
	}
});