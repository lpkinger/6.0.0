Ext.define('erp.view.sysmng.basicset.fixed.FreezeGridPanel2',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.FreezeGridPanel2',
	id: 'FreezeGridPanel2', 
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
    emptyText : $I18N.common.grid.emptyText,
    title:'<font size=2>DETAILGRID</font>',
    titleCollapse:true,
    collapsible : true,
    border:0,
    layout:'fit',
    cls:'x-grid-header-ct',
    columnLines : true,   
    autoScroll : true,
     plugins: [Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1}),
     		  Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    columns: [
    		{xtype:'rownumberer',header:'序号', width: 30,align:'center'},
	        { header: '冻结',
	        fixed:true,
	        width: 50,
	         align:'center',
	        dataIndex:'DG_ISFIXED',
	        xtype: 'checkcolumn',
	        cls: 'x-grid-header-1',
	        editor:{
	        	xtype:'checkbox',
	        	cls: 'x-grid-checkheader-editor'
	        },       
	        renderer: function (v) { return '<input type="checkbox"'+(v==true?" checked":"")+'/>'; } 
	         },
	       
	        { header: 'id',dataIndex:'DG_ID',hidden:true,xtype:"numbercolumn"},
	        { header: 'caller',dataIndex:'DG_CALLER',flex: 1},
	        { header: '表名',dataIndex:'DG_TABLE',flex: 1},
	        { header: '字段名', dataIndex:'DG_FIELD',flex: 1},
	        { header: '字段描述', dataIndex:'DG_CAPTION',flex: 1},	       
	        { header: '字段类型', dataIndex:'DG_TYPE',readOnly:true,flex: 1 ,
	         renderer:function(a, b, c, x, y, s, v){
				if(!Ext.isEmpty(a)) {
					
					var g = v.ownerCt,h = g.columns[y], k;
					if ((k = (h.editor || h.filter)) && k.store) {
						var t = null,dd = k.store.data.items;
										
				   		t = Ext.Array.filter(dd, function(d, index){
				   			
						    return d.data.value == a;
					    });
					    
					    if (t && t.length > 0) {

					    	return t[0].data.display;;
					    }
					}
					else return a;
				}
		   },
	        editor:{
	        xtype:'combo',
	       	valueField:'value',
	        displayField:'display',
	        editable:false,
	        hideTrigger:true,
	        readOnly:true,
	        store:Ext.create('Ext.data.Store',{	       	
			fields:['display','value'],
			data:[{display:'字符串',value:'text'},
			{display:'日期型',value:'datecolumn'},
			{display:'时间',value:'datetimecolumn'},
			{display:'时间2',value:'datetimecolumn2'},
			{display:'下拉框',value:'combo'},	
			{display:'可编辑下拉框',value:'editcombo'},	
			{display:'数字型',value:'numbercolumn'},
			{display:'数字型2',value:'floatcolumn'},
			{display:'数字型4',value:'floatcolumn4'},
			{display:'数字型6',value:'floatcolumn6'},
			{display:'数字型8',value:'floatcolumn8'},
			{display:'是否(-1/0)',value:'yncolumn'},
			{display:'是否(T/F)',value:'tfcolumn'},					
			{display:'文本框',value:'texttrigger'},
			{display:'树型',value:'treecolumn'}

			]})}
	       }
	    ],
	    
	   
	  store:Ext.create('Ext.data.Store',{
			fields:[ 
			        {name:'DG_CAPTION',type:'string'},
			        {name:'DG_FIELD',type:'string'},
			        {name:'DG_ID',type:'int'},
			        {name:'DG_TABLE',type:'string'},
			        {name:'DG_TYPE',type:'string'},
			        {name:'DG_ISFIXED',type:'bool'},
			        {name:'DG_CALLER',type:'string'}],
			        
			        autoLoad:false
			        
		}),

		
		
	initComponent : function(){ 
		var me = this;
		this.GridUtil = Ext.create('erp.util.GridUtil');
		var caller = {caller:"1=1"};
		me.callParent(arguments); 		
		me.getGridColumns(this, 'sysmng/singleGrid2Panel.action', caller, "",true);
		
},
getChange: function(){
		var grid = this,items = this.store.data.items,key = grid.keyField,
		added = new Array(),deleted = new Array(),d = null,e = null;
		Ext.each(items, function(item){			
			d = item.data;						
			if (item.dirty) {
				if(d.DG_ISFIXED==false){
					deleted.push(d.DG_ID);
					
				}else if(d.DG_ISFIXED==true){
					added.push(d.DG_ID);
				}else{
				console.log("未更改的数据");
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
		//addId=Ext.
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
        this.getGridColumns(this, 'sysmng/singleGrid2Panel.action', gridParam, "",true);
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

        		var data = res.Grid2Detail;
        		if(data==""){
        			me.collapse();
        		}else{
        			me.expand();
	        		Ext.each(data,function(d){
	        			
	        			if(d.DG_ISFIXED==-1){        				
	        				d.DG_ISFIXED=true;        				
	        			}
	        		});
        		}  
        		me.store.loadData(data);
        	
        	}
        });
	}
	
 });
	