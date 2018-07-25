Ext.define('erp.view.oa.knowledge.KnowledgeSubscibe',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpBatchDealFormPanel',
	    	  anchor: '100% 20%',
	     tbar: [{
		name: 'query',
		id: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			Ext.getCmp('dealform').onQuery();
    	}
	}, '->',  {
    	xtype: 'button',
    	text:'已阅',
    	cls: 'x-btn-gray',
    	id:'readed',
    	iconCls: 'x-button-icon-readed',
    },'-',
    {
    	xtype: 'button',
    	text:'订阅设置',
    	id:'install',
    	cls: 'x-btn-gray',
    	iconCls: 'x-button-icon-install',  	
    },'-',{
    	name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var grid = Ext.getCmp('batchDealGridPanel');
    		grid.BaseUtil.exportexcel(grid);
    	}
    },'-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	    },{
	    	 region: 'south',         
	    	 xtype:'erpDatalistGridPanel',  
	    	 anchor: '100% 80%',
	    	 selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	  })
	    }]
		});
		me.callParent(arguments); 
	}
});