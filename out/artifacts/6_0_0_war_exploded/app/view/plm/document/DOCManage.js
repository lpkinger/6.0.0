Ext.define('erp.view.plm.document.DOCManage',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [{
					xtype:'erpDocumentTreePanel',
					region:'west',
					tbar: [{
						xtype: 'button',
						iconCls: 'tree-add',
						id:'treeadd',
						//disabled:true,
						text:'创建'
					} ,'-',{
						xtype:'button',
						text:'编辑',
						id:'treeupdate',
						//disabled:true,
						iconCls:'x-button-icon-change'
				  },'-',{
						xtype: 'button',
						id:'treedelete',
						iconCls: 'tree-delete',
						//disabled:true,
						text:'删除'
				  }],
				},{
					xtype:'erpDatalistGridPanel',
					region:'center',
					selModel: Ext.create('Ext.selection.CheckboxModel',{
					}),
					tbar: [{
						xtype: 'button',
						iconCls: 'x-button-icon-move',
						id:'move',
						text: '转移',
						style:'margin-left:10px'
					} ,{
						xtype: 'button',
						id:'update',
						iconCls: 'x-button-icon-change',
						text: $I18N.common.button.erpUpdateButton,
						style:'margin-left:10px'
					} , {
						xtype:'button',
						id:'delete',
						iconCls:'x-button-icon-delete',
						text: $I18N.common.button.erpDeleteButton,
						style:'margin-left:10px'
					},{
						xtype:'button',
						id:'print',
						iconCls:'x-button-icon-print',
						text: $I18N.common.button.erpPrintButton,
						style:'margin-left:10px'
					}],
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});