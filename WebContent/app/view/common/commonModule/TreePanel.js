Ext.define('erp.view.common.commonModule.TreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpTreePanel',
	id: 'tree-panel', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : true, 
	split: true, 
	singleExpand: true,
	rootVisible: false, 
	containerScroll : true, 
	autoScroll: false, 
	useArrows: true,
	bodyStyle:'background-color:#f1f1f1;',
	store: Ext.create('Ext.data.TreeStore', {
	    fields: ['addurl', 'allowDrag', 'caller', 'children', 'cls',
	    'creator', 'creator_id', 'data', 'deleteable', 'detno', 'url',
	    'expanded', 'iconCls', 'id', 'leaf', 'length', 'num', 'otherInfo',
	    'parentId', 'qtip', 'qtitle', 'showMode', 'svnversion', 'text', 'updateflag',
	    'updatetime', 'using', 'version']
	}),
	hideHeaders: true,
	columnLines:false,
	tbar:{
		xtype: 'erpTreeToolbar'
	},
	initComponent : function(){		
		var me=this;
	 	me.columns =[{
	        xtype: 'treecolumn',
	        dataIndex: 'text',
	        flex: 1
	    },{
	        xtype: 'actioncolumn',
	        width: 26,
	        renderer :function(val, meta, record){
	          	if(record.get('leaf')) {
	          		var iconCls = record.get('commonuse') ? 'x-actioncol-commonuse' : 'x-actioncol-nocommonuse';
	          		var iconTip = record.get('commonuse') ? '已添加到常用模块' : '添加到常用模块';
	          		meta.tdCls = iconCls;
	          		meta.tdAttr = 'data-qtip="'+iconTip+'"';
	          	}
	        },
	        listeners: {
	        	click: function(view, el, rowIdx, colIdx, e) {
	        		me.handleAddClick(view, rowIdx, colIdx, e);
	        	}
	        }      	
	    }];
		this.getTreeRootNode(0);//页面加载时，只将parentId = 0的节点加载进来
		this.callParent(arguments);
	},
	getTreeRootNode: function(parentId){
		var me = this;
		var condition = this.baseCondition,uucloud=this.addUUCloud(),syssetting = this.addSysSetting(),financialservice = this.addFinancialService();
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/lazyTree.action',
        	params: {
        		parentId: parentId,
        		condition: condition
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);        	
        		if(res.tree){
        			var tree=Ext.Array.filter(res.tree,function(item){
        	        	return item.text!='优软云' && item.text!='优软服务'&& item.text!='系统维护' && item.text!='系统维护管理';
        	        },this);
        			if(em_type=='admin'){
        				tree.push(syssetting);	
        			}
        			tree.push(uucloud);
        			tree.push(financialservice);
        			if(jspName!='ma/logic/config') {
        				Ext.Array.each(tree, function(node, i) {
	        				if(node.id == -999) {
	        					tree.splice(i, 1);
	        					return false;
	        				}
        				});
        			}
                	me.store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
    openCloseFun: function(){
	 	  var o = Ext.getCmp("open");
	 	  var c = Ext.getCmp("close");
	 	  var tree = Ext.getCmp('tree-panel');
	 		  if(o.hidden==false&&c.hidden==true){
	 			  tree.expandAll();
	 			  o.hide();
	 			  c.show();
	 		  }else{
	 			  tree.collapseAll();
	 			  o.show();
	 			  c.hide();
	 		  }
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	handleAddClick: function(gridView, rowIndex, colIndex, e) {
		this.fireEvent('addclick', gridView, rowIndex, colIndex, e);
	},
	/**优软云菜单*/
	addUUCloud:function(){
		var me = this, json = {};
		Ext.Ajax.request({
		    url: basePath+'resource/uucloud/sysnavigation.json',
		    async:false,
		    success: function(response){
		        var text = response.responseText;
		        json=new Ext.decode(text);
		    }
		});
		me.parseTreeNode(json, me);
		return json;
		
	},
	/**系统维护管理*/
	addSysSetting:function(){
		var me = this, json = {};
		Ext.Ajax.request({
		    url: basePath+'resource/uucloud/syssetting.json',
		    async:false,
		    success: function(response){
		        var text = response.responseText;
		        json=new Ext.decode(text);
		    }
		});
		me.parseTreeNode(json, me);
		me.fromSysSetting(json, me);
		return json;
		
	},
	/**金融服务*/
	addFinancialService:function(){
		var me = this, json = {};
		Ext.Ajax.request({
		    url: basePath+'resource/uucloud/financialservice.json',
		    async:false,
		    success: function(response){
		        var text = response.responseText;
		        json=new Ext.decode(text);
		    }
		});
		me.parseTreeNode(json, me);
		return json;
		
	},
	/*
	 * 递归处理url嵌在a标签中的项目
	 */
	parseTreeNode: function(obj, treepanel) {
		if(obj.children) {
			for(var i = 0; i<obj.children.length; i++) {
				treepanel.parseTreeNode(obj.children[i],treepanel)
			}
		}else {
			obj.parentId = 'json';
			// 如果text是a标签
			if(/<a/.test(obj.text)) {
				obj.url = obj.text.match(/href='(\S*)'/)[1];
				obj.text = obj.text.match(/>(\S*)<\/a>/)[1]
			}
		}
	},
	/**
	 * 来自系统维护管理的菜单节点
	 */
	fromSysSetting: function(obj, treepanel) {
		if(obj.children) {
			for(var i = 0; i<obj.children.length; i++) {
				treepanel.fromSysSetting(obj.children[i],treepanel)
			}
		}else {
			obj.parentId = 'sys';
		}
	}
});