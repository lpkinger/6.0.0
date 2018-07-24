Ext.define('erp.view.core.trigger.SearchField', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.searchfield',
	triggerCls: 'x-form-search-trigger',
	hasSearch : false,			
	paramName : 'query',
    fieldStyle: 'color:#777;background: #f1f2f5;border-color: #d9d9d9; -moz-border-radius:3px 0 0 3px; -webkit-border-radius:3px 0 0 3px; border-radius:3px 0 0 3px;',
    focusCls: 'x-form-field-cir',
    emptyText: $I18N.common.main.quickSearch,
    enableKeyEvents : true,
    listeners : {
    	specialkey : function(field, e){
    		if(e.getKey() == Ext.EventObject.ENTER){
    			this.onTriggerClick();
    		}
    	}
    },
    autoShow:true,
	initComponent : function() {
		this.callParent(arguments);
	},
	onTriggerClick: function(){
		var f = this, tree = Ext.getCmp('tree-panel')!=null?Ext.getCmp('tree-panel'):Ext.getCmp('powertree');
		var parentC = f.ownerCt.ownerCt.ownerCt;
		var isBench = parentC.xtype=='workspaceTreePanel';
		if(f.value == '' || f.value == null){
			if(!isBench){
				tree.getTreeRootNode(0);
			}
			return;
		}else f.value=f.value.replace(/\'/g,"''");
		if(isBench){
			f.searchRemoteBench(parentC, f.value);
		}else{
	        var isPower = tree.id=='tree-panel'?0:1
	        
	        if(isPower) {
	        	f.searchRemoteTree(tree, f.value, isPower);
	        }else {
	        	var em_id = getCookie('em_uu'),
	        		hideTreeMenu = tree.hideTreeMenu;
	        	
	        	if(hideTreeMenu) {
	        		if(em_id == -99999) {
	        			f.searchRemoteTree(tree, f.value, isPower);
	        		}else {
	        			tree.searchCommonuseTree(f.value);
	        		}
	        	}else {
	        		f.searchRemoteTree(tree, f.value, isPower);
	        	}
	        }
		}
	},
	searchRemoteTree: function(tree, value, isPower) {
		var f = this;
		var nodes = new Array();
		tree.setLoading(true, tree.body);
        if(isPower==1){
			Ext.Ajax.request({//拿到tree数据
	        	url : basePath + 'bench/ma/searchBenchTree.action',
	        	params: {
	        		search: f.value
	        	},
	        	async:false,
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.tree){
	        			nodes = res.tree;
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        		}
	        	}
	        });
        }
		Ext.Ajax.request({//拿到tree数据
        	url : (typeof(caller)!="undefined" && caller=='ProductKind' && f.id =='ProductKindSearch')?basePath +'scm/product/searchProductKindTree.action':basePath +'common/searchTree.action',
        	timeout:120000,
        	params: {
        		search: value,
        		isPower:isPower
        	},
        	callback : function(options,success,response){
        		tree.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			res.tree = Ext.Array.merge(nodes, res.tree, res.tree);
        			var jsonTree = f.getJsonTree(f.value);
        			res.tree = Ext.Array.merge(res.tree, jsonTree, res.tree);
        			var searchKeys = Ext.util.Cookies.get('searchKeys');
        			if(searchKeys == null || searchKeys.length == 0){
        				//searchKeys = new Array();//cookie存放数组很麻烦，就用字符串，中间用||隔开
        				searchKeys = f.value;
        			} else {
        				var bool = true;
        				Ext.Array.each(searchKeys.split('||'), function(k){
        					if(k == f.value){//已存在
	        					bool = false;
        					}
        				});
        				if(bool){
        					searchKeys = searchKeys + "||" + f.value;
        				}
        			}
        			//Ext.util.Cookies.set('searchKeys', searchKeys);//把用户的搜索条件作为关键字存入cookie
        			var root = tree.getRootNode();
        			root.removeAll();
        			var fn = function(node, ch) {
        				for(var i in ch) {
	        				var n = ch[i], chs = n.children;
	        				n.children = [];
	        				n.expanded = true;
	        				if (n.showMode == 2 && tree.searchCheckShowMode) {
                                n.text = "<a href='" + basePath + parseUrl(n.url) + "' target='_blank'>" + n.text + "</a>";
                            }
	        				var d = node.appendChild(n);
	        				if(d && chs && chs.length > 0 && String(chs) != '[]') {
	        					fn(d, chs);
	        				}
	        			}
        			};
        			fn(root, res.tree);
        		} else if(res.redirectUrl){
        			window.location.href = res.redirectUrl;
        		} else if(res.exceptionInfo){
        			Ext.Msg.alert("ERROR:" + res.exceptionInfo);
        		}
        	}
        });
	},
	getJsonTree: function(search){
		var me = this, treepanel = this.ownerCt.ownerCt,tree = new Array(),json = new Array(),nodes = new Array();
		if(treepanel.xtype != 'erpTreePanel'){
			return tree;
		}
		
		if(em_type=='admin'){
			var syssetting = treepanel.addSysSetting()
			json.push(syssetting);	
		}
		var uucloud = treepanel.addUUCloud(),financialservice = treepanel.addFinancialService();
		json.push(uucloud);
        json.push(financialservice);
 
        this.searchTree(json, search, 0, nodes);
        var list = new Array();
		Ext.Array.each(nodes, function(node){
			if(node.parentId != 0){
				me.getNodeByparentId(json, node.parentId, list, json);
			}
		});
        
        list = Ext.Array.merge(list, nodes, list);
        Ext.Array.each(list, function(node){
			if(node.parentId == 0){
				tree.push(me.getTree(list, node));
			}
		});
        return tree;
	},
	searchTree: function(json, search, parentId, nodes){
		var me = this;
		Ext.Array.each(json,function(node){
			node.parentId = parentId;
			if(node.leaf){
				if(contains(node.text, search, true)){
					nodes.push(node);
				}
			}else if(node.children && node.children.length>0){
				if(!node.id){
					node.id = Math.random();
				}
				me.searchTree(node.children, search, node.id, nodes);
			}
		});
	},
	getNodeByparentId: function(nodes, parentId, list, json){
		var me = this;
		Ext.Array.each(nodes, function(node){
			node.expanded = true;
			if(parentId == node.id){
				var bool = false;
				Ext.Array.each(list, function(l){
					if(node.id == l.id){
						bool = true;
						return false;
					}
				});
				if(!bool){
					list.push(node);
					if(node.parentId != 0){
						me.getNodeByparentId(json, node.parentId, list, json);
					}
				}
				return false;
			}else if(node.children && node.children.length>0){
				me.getNodeByparentId(node.children, parentId, list, json);
			}
		});
	},
	getTree:function(list, node){
		var me = this,children = new Array();
		Ext.Array.each(list, function(Node){
			if(node.id == Node.parentId){
				if(Node.children && Node.children.length>0){
					children.push(me.getTree(list, Node));
				} else{
					children.push(Node);
				}
			}
		});
		
		if(children.length>0){
			node.children = children;
		}
		
		return node;
	},
	searchRemoteBench: function(tree, value) {
		var f = this;
		f.isSearch = true;
		f.el.dom.classList.add('x-bench-search');
		if(f.originalValue == value){
			var win = Ext.getCmp('benchWin');
			win && win.show();
			return;
		}
		tree.setLoading(true, tree.body);
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'bench/searchBench.action',
        	params: {
        		search: value
        	},
        	async:false,
        	callback : function(options,success,response){
        		tree.setLoading(false, tree.body);
        		var res = new Ext.decode(response.responseText);
        		if(res.success){
        			f.createMenu(res.benchs);
        			f.originalValue = value;
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	createMenu:function(benchs){
		var f = this,html,view={},height=0;
		var win = Ext.getCmp('benchWin');
		win && win.destroy();
		win = null;
		if(benchs.length>0){
			Ext.Array.each(benchs,function(bench){
				height += Math.ceil(bench.items.length/3) * 23;
			});
			height += benchs.length *25;
			
			var tpl = new Ext.XTemplate(
				'<div class="x-bs-box">',
			    	'<tpl for=".">',
			    		'<div class="x-bs-group x-bs-text">{title}</div>',
		    			'<tpl for="items">',
		    				'<div id="button-{code}" class="x-bs-child x-btn x-column x-window-item x-btn-default-small x-icon-text-right x-btn-icon-text-right x-btn-default-small-icon-text-right">',
				    			'<em id="button-{code}-btnWrap">',
					    			'<button id="button-{code}-btnEl" type="button" data-qtip="{title}" hidefocus="true" role="button" autocomplete="off" class="x-btn-center">',
						    			'<span id="button-{code}-btnInnerEl" class="x-btn-inner x-bs-text" onClick="openBox(\'{type}\',\'{url}\', \'{title}\', \'{benchcode}\', \'{benchtitle}\', \'{business}\', \'{scene}\')">{title}</span>',
						    			'<tpl if="addurl">',
						    				'<span id="button-{code}-btnIconEl" class="x-btn-icon x-btn-item-add" onClick="openUrl2(parseUrl(\'{addurl}\'), \'{title}\')">&#160;</span>',
						    			'</tpl>',
					    			'</button>',
				    			'</em>',
			    			'</div>',
		    			'</tpl>',
			    	'</tpl>',
			    '</div>'
			);
		  	view = new Ext.DataView({
		  		height: height + 10,
		        width:600,
				store : Ext.create('Ext.data.Store', {
						fields: ['title', 'items'],
						data: benchs
				}),
				tpl : tpl,
				trackOver: true,
				overItemCls : 'x-bs-over',
				selectedItemCls : 'x-bs-over',
				singleSelect : true,
				itemSelector : '.x-bs-child'
			});
			
		}else{
		    view = {
		    	html:'<div class="x-grid-empty"><div class="emptytext">未搜索到结果</div></div>'
		    };
		}
        
		win = Ext.create('Ext.window.Window', {
			id: 'benchWin',
			minHeight: 210,
			autoScroll: true,
			width:616,
	  		maxHeight: screenHeight-90,
			closeAction : 'hide',
			plain: true,  
		    header: false,  
	        border: false,  
	        closable: false,  
	        draggable: false,  
		    frame:false,  
		    resizable :false,  
			items:[view],
			showSeparator: false,
			listeners:{
				blur:  {
					element: 'el', 
            		fn: function(event){ 
            			setTimeout(function(){
            				f.el.dom.classList.remove('x-bench-search');
            				win.close();
            			}, 200);
					}
				}
			}
		});
		
		win.setHeight(height+18);
		win.showAt(f.getEl().getX()+f.getEl().getWidth()-30,f.getEl().getY()+4);

	}
});
