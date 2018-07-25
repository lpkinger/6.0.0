Ext.define('erp.view.excel.WestPanel', {
	alias:'widget.excelWestPanel',
    extend : 'Ext.panel.Panel',	
	region: 'west',
	width:  250,
    border: false,
    floatable: false,
	collapsible:true,
    id:'west',
    style: 'border-right:1px solid silver;',
    title: '目录',
    items:[{
		xtype:'excelTree'    
    }],
    dockedItems: [
	{
	    xtype: 'toolbar',
	    dock: 'top',
	    items: [{
	    	text:'新增',
	    	xtype:'button',
	    	iconCls:'x-button-icon-add',
	    	name:'addTpl'
	    },{
	    	text:'编辑',
	    	xtype:'button',
	    	iconCls:'x-button-icon-modify',
	    	name:'editTpl'
	    },
	    {
	    	text:'解除锁定',
	    	xtype:'button',
	    	iconCls:'icon-fixed',
	    	name:'readOnlyTpl'
	    },
	    {
	    	text:'删除',
	    	xtype:'button',
	    	iconCls:'x-button-icon-delete',
	    	name:'deleTpl'
	    }
	    ,{
	    	text:'测试变量',
	    	xtype:'button',
	    	name:'test'
	    }	    
	    ]
	}
	],	
	initComponent : function(){
		this.callParent();
	}
});
