Ext.define('erp.view.ma.copy.CopyConfig',{ 
	extend:'Ext.Viewport',
	layout:'anchor',
	initComponent:function(){
		var me = this;
		Ext.apply(me,{
			items:[{
				xtype:'erpCopyFormPanel',
				anchor:'100% 20%'
			},
/*			{
				xtype: 'erpCopyGridPanel2',
				anchor: '100% 80%', 
				keyField: 'cc_caller',
				mainField: 'cc_caller',
				gridCondition:'',
				updateUrl: '/ma/setting/CopyConfigs.action',
				allowExtraButtons: true,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')
			    , Ext.create('erp.view.scm.purchase.plugin.Reply')]
			},*/
				{
				xtype: 'erpCopyGridPanel',
				anchor: '100% 80%', 
				keyField: 'cc_caller',
				mainField: 'cc_caller',
				allowExtraButtons: true,
				updateUrl: '/ma/setting/CopyConfigs.action'
			}]
		})
		me.callParent(arguments)
	}
});