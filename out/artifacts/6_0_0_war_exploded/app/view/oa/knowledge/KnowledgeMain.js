Ext.define('erp.view.oa.knowledge.KnowledgeMain',{ 
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
					xtype:'erpKnowledgeTreePanel',
					region:'west',
					tbar: [{
		xtype: 'button',
        iconCls: 'tree-add',
        id:'treeadd',
        disabled:true,
		text: $I18N.common.button.erpAddButton,
		style:'margin-left:10px'
	} ,{
		 xtype: 'button',
	     id:'treedelete',
         iconCls: 'tree-delete',
          disabled:true,
		 text: $I18N.common.button.erpDeleteButton,
		 style:'margin-left:10px'
	} , {
		 xtype:'button',
	     text:$I18N.common.button.erpUpdateButton,
	    id:'treeupdate',
	     disabled:true,
	    iconCls:'x-button-icon-change',
	    style:'margin-left:10px'
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