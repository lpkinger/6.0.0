 Ext.define('erp.view.sysmng.basicset.fixed.FreezeFormPanel',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.FreezeFormPanel',
	requires: ['erp.view.core.button.Save','erp.view.core.button.Query'],
	id: 'FreezeForm', 
    region: 'north',
   	bodyStyle:{background:'#f1f1f1'},
    
	cls:"x-panel-header-text-default",
	border:'0 0 0 0',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	padding: '0 0 0 0',
	autoScroll : false,
	buttonAlign : 'center',
	
    items: [
    	{
    	xtype:'textfield',    	
        emptyText:'请输入caller',
        name: 'caller ',
       	margin:'20 10 10 10' ,
        allowBlank: true
    },
	    {
			name: 'query',
			id: 'query',
			xtype: 'erpQueryButton',
			margin:'20 10 10 10' ,
			height: 22,
			text: $I18N.common.button.erpQueryButton,
			iconCls: 'x-button-icon-query',
	    	cls: 'x-btn-gray',
	    	handler: function(btn){
	    		
	    		btn.ownerCt.onQuery();
				//btn.ownerCt.ownerCt.onQuery();
	    	}
		}, 	
		{
	    	xtype: 'erpSaveButton',
	    	id: 'erpSaveButton',
	    	margin:'20 10 10 10' ,
	    	height: 22,
	    	hidden: false,
	    	handler: function(btn){
	    	
	    	var grid1 = Ext.getCmp('FreezeGridPanel1');
			var grid2 = Ext.getCmp('FreezeGridPanel2');
			var grid1add=grid1.getChange().added;
			var grid1deleted=grid1.getChange().deleted;
	  		grid1.Save(grid1add,grid1deleted,'sysmng/saveGrid1detail.action');
	  		
	  		var grid2add=grid2.getChange().added;
			var grid2deleted=grid2.getChange().deleted;
	  		grid2.Save(grid2add,grid2deleted,'sysmng/saveGrid2detail.action');	  		
	    	}	    	
	    }],
    
	initComponent : function(){ 
		this.callParent(arguments);		
	},
	
	/**
	 * @param select 保留原筛选行
	 */
	onQuery: function(select){
		value=this.items.items[0].value;
	
		var grid1 = Ext.getCmp('FreezeGridPanel1');
		var grid2 = Ext.getCmp('FreezeGridPanel2');
		var gridParam = { caller: value };

		if(grid1.getGridColumns){
			
			grid1.getGridColumns(grid1, 'sysmng/singleGrid1Panel.action', gridParam, "",true);
		} 
		if(grid2.getGridColumns){
			grid2.getGridColumns(grid2, 'sysmng/singleGrid2Panel.action', gridParam, "",true);
		}
		
	},
	beforeQuery: function(call, cond) {}
});