Ext.define("erp.view.excel.ExcelTree",{
	extend:'Ext.tree.Panel',
	alias:'widget.excelTree',
	id:'excelTree',
	rootVisible:true,
//	region:'west',
	width:300,
	border:0,
	margins:'0 1 0 0',
	hideHeaders: true,
	rootVisible:false,
	displayField:'text',
	animate:false,
	initComponent : function(){
		var me=this;
		 me.columns =[{
		        xtype: 'treecolumn',
		        dataIndex: 'text',
		        flex: 1
		 },{xtype: 'actioncolumn',
		    width: 24,
		    id:'newFromTpl',
		    icon: (window.basePath || '') + 'resource/images/tree/add2.png',
		    iconCls: 'x-hidden',
		    renderer :function(val, meta, record){
		    	meta.tdCls = record.get('cls');
		        meta.tdAttr = record.get('leaf')?'新增为实例数据':'';
		    },
		    handler: Ext.bind(me.handleAddClick, me)
	    }];		
		this.getTreeRootNode(0);//页面加载时，只将parentId = 0的节点加载进来
		this.callParent(arguments);
		me.addEvents('addclick');
	},
	store:Ext.create('Ext.data.TreeStore', {
	    fields: ['id','text','parentId','url','qtitle','leaf','detno','allowDrag','qtip','cls','iconCls','showMode'
	    ,'children','deleteable','updatetime','version','creator','creator_id','using','expanded','caller','data','otherInfo',
	    'addurl','length','num','svnversion','updateflag','prefixcode'
	    ],		
	    root : {
	    	text: 'root',
	    	id: 'root',
			expanded: true
		}
	}),
	handleAddClick: function(gridView, rowIndex, colIndex, column, e) {
         this.fireEvent('addclick', gridView, rowIndex, colIndex, column, e);
	},
	//一开始加载根节点
	getTreeRootNode: function(parentId){
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'Excel/template/getExcelTreeBySubof.action',
        	params: {
        		subof: parentId,
        		condition: ""
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);        	
        		if(res.tree){
/*					this.store.setRoot({
						text: 'root',
						id: 'root',
						expanded: true,
						children: res.tree
					})*/
                	Ext.getCmp('excelTree').store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: res.tree
                	});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	refreshNodeByParentId:function (parentId,tree,record){
        var me = this;
        if (parentId==0) {
        	me.getTreeRootNode(0);
        	return;
        }
        var record=record||tree.getSelectionModel().lastFocused;
        record.removeAll(true);
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'Excel/template/getExcelTreeBySubof.action',
			params: {
        		subof: parentId,
        		condition: ""
        	},
			callback : function(options,success,response){
				tree.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.tree){
				   if(res.tree.length==0){
					   if(record.get("expanded")){
						   record.collapse(true);//收拢
					   }
				   }else{
				     record.appendChild(res.tree);
				     record.expand(false,true);	
				   }					
					me.flag = true;
				} else if(res.exceptionInfo){
					showError(res.exceptionInfo);
					me.flag = true;
				}
			}
		});
	}
})