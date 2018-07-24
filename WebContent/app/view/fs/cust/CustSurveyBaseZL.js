Ext.define('erp.view.fs.cust.CustSurveyBaseZL',{ 
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
					title:'租赁物基本信息',  
					xtype: 'erpGridPanel2',
					id: 'fsleaseitem',
					caller:'FsLeaseItem',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					mainField: 'li_cqid',
					keyField: 'li_id',
					allowExtraButtons : true
				},{
					title:'融资租赁设备',
					xtype : 'erpGridPanel2',
					id: 'fsleasedevice',
					caller:'FsLeaseDevice',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=").replace('li_cqid','ld_cqid'):'',
					bbar: {xtype: 'erpToolbar',id:'toolbar1'},
					keyField : 'ld_id',
					mainField : 'ld_cqid'
				},{
					title:'担保情况',
					xtype : 'erpGridPanel2',
					caller:'Guarantee',
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
							clicksToEdit: 1
					}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					condition:condition!=null?condition.replace(/IS/g, "=").replace('li_cqid','gu_caid'):'',
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