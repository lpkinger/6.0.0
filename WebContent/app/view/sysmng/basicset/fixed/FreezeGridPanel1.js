Ext.define('erp.view.sysmng.basicset.fixed.FreezeGridPanel1',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.FreezeGridPanel1',
	id: 'FreezeGridPanel1', 	
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.plugin.CopyPasteMenu'],
    emptyText : $I18N.common.grid.emptyText,
    title:'<font size=2>FORMDETAIL</font>',
    titleCollapse:true,
    collapsible : true,
    columnLines : true,
    autoScroll : true,
    layout:'fit',
    border:0,
  	cls:'x-grid-header-ct',
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1}),
     		  Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    columns: [
   			 {xtype:'rownumberer',header:'序号', width: 30,align:'center'},
	        { 	        	
	        header: '冻结',
	        width: 50,	
	        fixed:true,
	        dataIndex:'FD_ISFIXED',
	        xtype: 'checkcolumn',
	        cls: 'x-grid-header-1',
	        align:'center',
	        editor:{
	        	xtype:'checkbox',
	        	cls: 'x-grid-checkheader-editor'
	        },       
	        renderer: function (v) { return '<input type="checkbox"'+(v==true?" checked":"")+'/>'; } 
	        },
	        
	        { header: 'id', dataIndex:'FD_ID',hidden:true},
	        { header: 'caller',dataIndex:'FO_CALLER',flex: 1},
	        { header: '表名', dataIndex:'FD_TABLE',flex: 1},
	        { header: '字段名', dataIndex:'FD_FIELD',flex: 1},
	        { header: '字段描述', dataIndex:'FD_CAPTION',flex: 1},	       	      
	        { header: '字段类型',dataIndex:'FD_TYPE',flex: 1}
	    ],
	  store:Ext.create('Ext.data.Store',{
			fields:[ 
			        {name:'FD_CAPTION',type:'string'},
			        {name:'FD_FIELD',type:'string'},
			        {name:'FD_ID',type:'int'},
			        {name:'FD_TABLE',type:'string'},
			       
			        {name:'FD_ISFIXED',type:'bool'},
			        {name:'FO_CALLER',type:'string'},
			        
			        {name:'FD_TYPE',type:'string'}
			        
			        
			        
			        ],

			        //autoLoad:false
			        
		}),
	
	initComponent : function(){ 
		var me = this;
		this.GridUtil = Ext.create('erp.util.GridUtil');
		var caller = {caller:"1=1"};
		me.callParent(arguments); 		
		me.getGridColumns(this, 'sysmng/singleGrid1Panel.action', caller, "",true);
		
},
	getChange: function(){
		var grid = this,items = this.store.data.items,key = grid.keyField,
		added = new Array(),deleted = new Array(),d = null,e = null;
		//console.log(items);
		Ext.each(items, function(item){			
			d = item.data;			
			
			if (item.dirty) {
				//console.log("有更改");
				if(d.FD_ISFIXED==false){
					deleted.push(d.FD_ID);
					
				}else if(d.FD_ISFIXED==true){
					added.push(d.FD_ID);
				}else{
				console.log("未更改的数据")
				}
				
				
				}
		});

		return {
			added: added,			
			deleted: deleted
		};
	},

Save: function(added,deleted,url){
		var me = this;
		var params = {addId:added,deletedId:deleted};
		me.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: params,
        	method : 'post',     
        	callback : function(options,success,response){
        		me.setLoading(false);        		
        		var res = new Ext.decode(response.responseText);       		      		
        		me.Reflash();       		            	
        	}
        });
	},
Reflash:function(){
		var form = Ext.getCmp('FreezeForm');
       	value=form.items.items[0].value;
       	if(value==null || value==""){
       		value='1=1';
       	}       	
       	var gridParam = { caller: value };
        this.getGridColumns(this, 'sysmng/singleGrid1Panel.action', gridParam, "",true);
	},
	

getGridColumns: function(grid, url, param, no,sync){
		var me = this;		
		me.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: sync?false:true,        	
        	success : function(response){
        		me.setLoading(false);
        		
        		var res = new Ext.decode(response.responseText);       		        		
        		var data = res.Grid1Detail; 
        		
        		if(data==""){
        			me.collapse();
        		}else{
        			me.expand();
	        		Ext.each(data,function(d){
	        			
	        			if(d.FD_ISFIXED==-1){        				
	        				d.FD_ISFIXED=true;        				
	        			}
	
	        		
	        		});

        		}
        		me.store.loadData(data);
        		
        		
        		
        		      
        	
        	}
        });
	}
	
 });
	