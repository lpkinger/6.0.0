Ext.define('erp.view.oa.knowledge.KnowledgeModule',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpKnowledgeGridPanel',  
	    	  anchor: '100% 100%',
	    	  dockedItems: [{
                 xtype: 'toolbar',
                 dock: 'top',
                 style:'font-size:16px;height:40px',
			     bodyStyle: 'font-size:16px;height:40px',
                 items: [{
                 xtype: 'button',
                 iconCls: 'tree-add',
                 id:'add',
		         text: $I18N.common.button.erpAddButton,
		         style:'margin-left:10px'
	              },{
	              xtype: 'button',
	              id:'delete',
                  iconCls: 'tree-delete',
		         text: $I18N.common.button.erpDeleteButton,
		         style:'margin-left:10px'
	              },{
	              xtype:'button',
	              text:'排序',
	              id:'sort',
	              iconCls:'x-button-icon-sort',
	              style:'margin-left:10px'
	              }]
	              }]
	             }] 
		});
		me.callParent(arguments); 
	}
});