Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.GetUUid', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.product.GetUUid.ProductB2CKindTree','scm.product.GetUUid.Viewport',
    		'scm.product.GetUUid.ComponentGrid','scm.product.GetUUid.Toolbar','core.trigger.SearchField'
    	],
    init:function(){
    	var me = this;
    	lastSelected = null;
    	me.lastCode = '';
    	me.codeisnull = true;
    	this.control({ 
    		'prodb2ckindtree': {
    			itemmousedown: function(selModel, record){
    				me.loadTab(selModel, record);
    				lastSelected = record;
    			},
    			itemdblclick: function(view, record){
    				lastSelected = record;
    				var btn = Ext.getCmp('confirm');
    				btn.fireEvent('click', btn);
    			}   		
    		},
    		'button[name=search]': {//根据原厂型号查找
    			click: function(btn){
    			  me.search();
    			}
    		},
    		'textfield[name=orispecode]':{
    			specialkey : function(field, e){
	        		if(e.getKey() == Ext.EventObject.ENTER){
	        			me.search();
	        		}
	        	}
    		},
    		'button[name=close]': {//关闭
    			click: function(){
    				parent.Ext.getCmp('uuWin').close();
    			}
    		}   		
    	});
    },
    loadTab: function(selModel, record){
    	var me = this;
    	var tree = Ext.getCmp('tree-panel');
    	var parentId='';
    	if (record.get('leaf')) {
    		parentId=record.data['parentid'];
    		//叶子节点    		
			Ext.getCmp('uuIdGrid').getGridData(record.data['id'],page,pageSize,record.data['orispecode']||'');
    	} else {
    		Ext.getCmp('uuIdGrid').store.loadData("");
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
		            tree.setLoading(true, tree.body);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + tree.getUrl(),
			        	params: {
			        		parentid: record.data['id'],
			        		type:type
			        	},
			        	async: false,
			        	callback : function(options,success,response){
			        		tree.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			record.appendChild(res.tree);
			        			record.expand(false,true);//展开
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false,true);//展开
				}
			}
    	}
    	tree.getExpandedItems(record);
    	Ext.each(tree.expandedNodes, function(){
    		if(!this.data['leaf'] && this.data['parentId']==parentId )
    		this.collapse(true,true);
    	});
    	me.lastCode = '';
    	var choose = new Array();
    	tree.getExpandedItems(record);
//    	Ext.each(tree.expandedNodes, function(){
//    		if(this.data['id']==parentId || this.data['id']==record.data['id']){
//    			me.lastCode += this.data['qtip'];		
//    			choose.push(this.data['text']);
//    		}
//    	}); 	
    	var path = record.path;
    	if(!path){
    		tree.getExpandItem();
    		path = record.path;
    	}
    	choose = path.split('/');
    	var c = Ext.getCmp('choose');
    	c.update({nodes: choose});
    	me.codeisnull = true;
    },
    getUrl: function(){
    	type = type || 'Product';
    	var url = 'scm/product/getProductKindNum.action';
    	return url;
    },
    
    search:function(){   	
    	var me = this;
		var f = Ext.getCmp("orispecode"), tree = Ext.getCmp('tree-panel');
		if(f.value == '' || f.value == null){
			tree.getTreeRootNode(0);
			return;
		}
        tree.setLoading(true, tree.body);
		Ext.Ajax.request({//拿到tree数据
        	url : basePath +'scm/product/searchByOrispecode.action?_noc=1',
        	timeout:120000,
        	params: {
        		code: f.value.replace(/(^\s*)|(\s*$)/g, ""),
        		caller:caller
        	},
        	callback : function(options,success,response){
        		tree.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		var root = tree.getRootNode();
        		root.removeAll();
        		if(res.tree){
        			var fn = function(node, ch) {
        				for(var i in ch) {
	        				var n = ch[i], chs = n.children;
	        				n.children = [];
	        				n.expanded = true;
	        				var d = node.appendChild(n);
	        				if(d && chs && chs.length > 0 && String(chs) != '[]') {
	        					fn(d, chs);
	        				}
	        			}
        			};
        			fn(root, res.tree);
        			root = tree.getRootNode().firstChild;
        			var getfirstleaft = function(ro){//获取第一个叶子节点
        				if(ro != null && ro.firstChild !=null){
        					getfirstleaft(ro.firstChild);
        				}else{
        					root = ro;
        				}
        			};
        			getfirstleaft(root);
        			//自动暂开第一个叶子节点
        			Ext.getCmp('uuIdGrid').getGridData(root.data['id'],page,pageSize,root.data['orispecode']||'');
        			me.lastCode = '';
			    	var choose = new Array();
			    	tree.getExpandedItems(root);
			    	var i =1 ;
			    	Ext.each(tree.expandedNodes, function(){
			    		if(i==100){
			    			return;
			    		}
			    		me.lastCode += this.data['qtip'];		
			    		choose.push(this.data['text']);
			    		if(root.data['text']==this.data['text']){
			    			i = 100;
			    		}
			    	}); 	
			    	var c = Ext.getCmp('choose');
			    	c.update({nodes: choose});
        		} else if(res.exceptionInfo){
        			Ext.Msg.alert("ERROR:" + res.exceptionInfo);
        		}else{
        			var c = Ext.getCmp('choose');
			    	c.show();
			    	c.update({nodes: []});
        		}
        	}
        });   			
    }
});