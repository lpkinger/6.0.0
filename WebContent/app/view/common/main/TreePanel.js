Ext.define('erp.view.common.main.TreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpTreePanel', 
	id: 'tree-panel', 
	/*region: 'west', 
	width : '20%', */
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	//title: $I18N.common.main.navigation,
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = $I18N.common.main.navigation;
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this; 
	},
	singleExpand: true,
	rootVisible: false, 
	containerScroll : true, 
	//collapsible : true, 
	autoScroll: false, 
	useArrows: true,
	bodyStyle:'background-color:#f1f1f1;',
	store:'TreeStore',store: Ext.create('Ext.data.TreeStore', {
	    fields: ['addurl', 'allowDrag', 'caller', 'children', 'cls',
	    'creator', 'creator_id', 'data', 'deleteable', 'detno', 'url',
	    'expanded', 'iconCls', 'id', 'leaf', 'length', 'num', 'otherInfo',
	    'parentId', 'qtip', 'qtitle', 'showMode', 'svnversion', 'text', 'updateflag',
	    'updatetime', 'using', 'version']
	}),
	hideHeaders: true,
	columnLines:false,
	hideTreeMenu: false, // 默认隐藏导航栏菜单
	searchCheckShowMode: false, // 搜索时为showmode为2的子节点构造成a标签
	initComponent : function(){		
		var me=this;
	 	me.columns =[{
	        xtype: 'treecolumn',
	        dataIndex: 'text',
	        flex: 1
	    },{
	        xtype: 'actioncolumn',
	        width: 26,
	        /*icon: (window.basePath || '') + 'resource/images/upgrade/blue/mainicon/tree/white-add.png',*/
	        icon: '',
	        iconCls: 'x-actioncol-add x-hidden',
	        renderer :function(val, meta, record){
	          	meta.tdCls = record.get('cls');
	          	if(record.get('addurl') || (record.raw && record.raw.addurl)) {
	          		var defaultTip = record['internalId'] == 'commonuse' ? '更多' : '查看导航图';
	          		meta.tdAttr = record.get('leaf')?'data-qtip="新增'+record.get('text')+'"':'data-qtip="'+defaultTip+'"';
	          		
	          	}
	        },
	        handler: Ext.bind(me.handleAddClick, me)
	    }];
		this.getTreeRootNode(0);//页面加载时，只将parentId = 0的节点加载进来
		this.callParent(arguments);
		me.addEvents('addclick');
	},
	getTreeRootNode: function(parentId){
		var me = this,
		em_id = getCookie('em_uu');
		if(em_id == -99999) {
			me.loadTree(parentId);
		}else {
			me.BaseUtil.getSetting('sys','hideNavigationMenu',function(val) {
				var commonuseNode = {
					text: '常用功能',
					leaf: false,
					id: 'commonuse',
					pageCount: 10,
					cls: 'x-tree-common-use',
					iconCls: 'x-tree-icon-common-use',
					url: null,
					//addurl: 'jsps/common/commonuse.jsp'
					addurl: 'jsps/common/commonModule.jsp'
				};
				
				if(val==null) {
					val = me.hideTreeMenu;
				}
				// 根据系统参数判断是否不需要展示菜单
				if(val && jspName!='ma/logic/config'){
					me.hideTreeMenu = true;
					me.store.setRootNode({
	            		text: 'root',
	            	    id: 'root',
	            		expanded: true,
	            		children: [commonuseNode]
	            	});
					me.fireEvent('itemmousedown', null, me.store.getRootNode().childNodes[0]);
				}else {
					me.hideTreeMenu = false;
					me.loadTree(parentId);
				}
			},false);
		}
	},
	tbar:{
		xtype: 'erpTreeToolbar'
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
	handleAddClick: function(gridView, rowIndex, colIndex, column, e) {
         this.fireEvent('addclick', gridView, rowIndex, colIndex, column, e);
	},
	/**优软云菜单*/
	addUUCloud:function(){
		var json={};
		Ext.Ajax.request({
		    url: basePath+'resource/uucloud/sysnavigation.json',
		    async:false,
		    success: function(response){
		        var text = response.responseText;
		        json=new Ext.decode(text);
		    }
		});
		return json;
		
	},
	/**系统维护管理*/
	addSysSetting:function(){
		var json={};
		Ext.Ajax.request({
		    url: basePath+'resource/uucloud/syssetting.json',
		    async:false,
		    success: function(response){
		        var text = response.responseText;
		        json=new Ext.decode(text);
		    }
		});
		return json;
		
	},
	/**金融服务*/
	addFinancialService:function(){
		var json={};
		Ext.Ajax.request({
		    url: basePath+'resource/uucloud/financialservice.json',
		    async:false,
		    success: function(response){
		        var text = response.responseText;
		        json=new Ext.decode(text);
		    }
		});
		return json;
		
	},
	loadTree: function(parentId) {
		var me = this;
		var condition = this.baseCondition,uucloud=this.addUUCloud(),syssetting = this.addSysSetting(),financialservice = this.addFinancialService();
		
		var commonuseNode = {
			text: '常用功能',
			leaf: false,
			id: 'commonuse',
			pageCount: 10,
			cls: 'x-tree-common-use',
			iconCls: 'x-tree-icon-common-use',
			url: null,
			//addurl: 'jsps/common/commonuse.jsp'
			addurl: 'jsps/common/commonModule.jsp'
		};
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'common/lazyTree.action',
			params: {
				parentId: parentId,
				condition: condition
			},
			callback : function(options,success,response){
				if(!response) {
					return;
				}
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
						tree.unshift(commonuseNode);
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
	searchCommonuseTree: function(value) {
		var me = this, tree = Ext.getCmp('tree-panel'),
			root = me.getRootNode(),
			commonuseRootNode = root.findChild('id', 'commonuse');
		if(!commonuseRootNode) {
			return;
		}
		commonuseRootNode.removeAll();
        tree.setLoading(true, tree.body);
		Ext.Ajax.request({
			url: basePath + 'common/searchCommonUseTree.action',
			params: {
				value: value
			},
            callback: function(options, success, response) {
                tree.setLoading(false);
                if(!response) {
                	return;
                }
                var res = new Ext.decode(response.responseText);
                if (res.tree) {
                    commonuseRootNode.appendChild(res.tree);
                    commonuseRootNode.expand(false, true); //展开
                } else if (res.exceptionInfo) {
                    showError(res.exceptionInfo);
                }
            }
		});
	}
});