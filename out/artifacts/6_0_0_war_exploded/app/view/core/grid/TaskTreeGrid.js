Ext.define('erp.view.core.grid.TaskTreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpTaskTreeGrid',
	region: 'south',
	layout : 'fit',
	id: 'treegrid', 
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    frame:true,
    columnLines:true,
    saveNodes: [],
    updateNodes: [],
    deleteNodes: [],
    FormUtil: Ext.create('erp.util.FormUtil'),
    store: Ext.create('Ext.data.TreeStore', {
    	fields: fields,
    	root : {
        	text: 'root',
        	id: 'root',
    		expanded: true
    	}
    }),
    columns: columns,
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}),
	tbar: [{
		iconCls: 'tree-add',
		text: $I18N.common.button.erpAddButton,
		handler: function(){
              var treegrid = Ext.getCmp('treegrid');
			var record = treegrid.selModel.selected.items[0];
			  if(record){ 
                         if(record.data.tt_id!=0&&record.data.tt_id!=null){
                         record.data['leaf'] = false;
					     record.data['cls'] = 'x-tree-cls-parent';
					     if(record.data['tt_isleaf']='T')
					     {
					     record.data['tt_isleaf'] = 'F';
					      treegrid.updateNodes.push(record); 
					     }
					     record.dirty = true;
                            var o = {
						     tt_name: 'New Task',
						     tt_ptid:ptdata.pt_id,
						     tt_ptname:ptdata.pt_name,
						     tt_parentid:record.data.tt_id,
						     tt_isleaf:'T',
						     leaf: true,   
						     allowDrag: true
				          };
                         record.appendChild(o);
                        
                         record.expand(true); 
                         }else {
                           warnMsg('如果在该节点下添加子节点，需先保存该节点，是否保存?', function(btn){
						if(btn == 'yes'){
							if(record.data['tt_name'] == null || record.data['tt_name'] == ''){
								showError('请先描述该节点');
								return;
							} else {
								record.data['leaf'] = false;
								record.data['cls'] = 'x-tree-cls-parent';
								record.data['tt_isleaf'] = 'F';
								treegrid.saveNodes.push(record);
								treegrid.saveNode();
							}
	    				  } else if(btn == 'no'){
	    					return;
	    				 } 
					});
                         }
			 }else {
                 showError("请选择添加任务的父节点!");
			 }
			}
	}	
	,{
        text: '添加父任务',
        iconCls : 'tree-add',
        handler: function () {
			    var o = {
						tt_name:'New Task',
						tt_ptid:ptdata.pt_id,
						tt_ptname:ptdata.pt_name,
						tt_parentid:0,
						tt_isleaf:'F',
						leaf: false,   
						allowDrag: true
				};
			   Ext.getCmp('treegrid').store.getRootNode().appendChild(o);                          
        }
    },
	{
		iconCls: 'tree-delete',
		text: $I18N.common.button.erpDeleteButton,
		handler: function(){
			var treegrid = Ext.getCmp('treegrid');
			var items = treegrid.selModel.selected.items;
			if(items.length > 0){
				if(items[0].isLeaf() == true){
					if(items[0].data['tt_id'] != null && items[0].data['tt_id'] !=0){
						warnMsg('确定删除节点权限[' + items[0].data['tt_name'] + "]?", function(btn){
							if(btn == 'yes'){
								treegrid.deleteNode(items[0]);
			    			} else if(btn == 'no'){
			    				return;
			    			} 
						});
					} else {
						items[0].remove(true);
					}
				} else {
					if(items[0].data['tt_id'] != null || items[0].data['tt_id'] != 0){
						warnMsg('确定删除节点[' + items[0].data['tt_name'] + ']及其子节点下所有权限？', function(btn){
							if(btn == 'yes'){
								treegrid.deleteNode(items[0]);
		    				} else if(btn == 'no'){
		    					return;
		    				} 
						});
					} else {
						items[0].remove(true);
					}
				}
			} else {
				var record = treegrid.getExpandItem();
				if(record){
					if(record.childNodes.length == 0){
						if(record.data['tt_id'] != null && record.data['tt_id'] !=0){
							warnMsg('确定删除节点[' + record.data['tt_name'] + ']？', function(btn){
								if(btn == 'yes'){
									treegrid.deleteNode(record);
			    				} else if(btn == 'no'){
			    					return;
			    				} 
							});
						} else {
							record.remove(true);
						}
					}
				}
			}
		}
	},{
		iconCls: 'tree-save',
		text: $I18N.common.button.erpSaveButton,
		handler: function(){
			var treegrid = Ext.getCmp('treegrid'),
			nodes = treegrid.store.tree.root.childNodes;
			treegrid.saveNodes = [];
			Ext.each(nodes, function(){
				treegrid.checkChild(this);
			});
			treegrid.saveNode();
		}
	}],
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		this.callParent(arguments);
		formCondition = getUrlParam('formCondition');//从url解析参数
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
    	Ext.Ajax.request({//
        	url : basePath + 'plm/project/getProjectTemplate.action',
        	params:{
        	condition:formCondition,
        	caller:'ProjectTemplate'
        	},
        	 async:false, 
		    method : 'get',
        	callback : function(options,success,response){
        	  var res = new Ext.decode(response.responseText);
        	  if(res.success){
        	  ptdata=new Ext.decode(res.data);
        	
        	  }if(res.exceptionInfo)
        	    {
	        	showError(res.exceptionInfo);return;
	            }
        	   }
        	});
	  condition=(formCondition=="")?"":formCondition.replace('pt_id','tt_ptid');
		this.getTreeGridNode({parentId:0,condition:condition});
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	getTreeGridNode: function(param){
		var me = this;
		var activeTab = me.getActiveTab();
		activeTab.setLoading(true);
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/TaskTree.action',
        	params: param,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		activeTab.setLoading(false);
        		if(res.tree){
        			var tree = res.tree;
        			Ext.each(tree, function(t){
        				t.tt_startdate=t.tt_startdate;
        				t.tt_enddate=t.tt_enddate;
        			});
        			me.store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        			Ext.each(me.store.tree.root.childNodes, function(){
        				this.dirty = false;
        			});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	checkChild: function(record){
		var me = this;
		if(!record.data['leaf']){
			if(record.childNodes.length > 0){
				if(record.data['tt_id'] == null || record.data['tt_id'] == 0){
					warnMsg('如果在节点' + record.data['tt_name'] + '下添加子节点，需先保存该节点，是否保存?', function(btn){
						if(btn == 'yes'){
							if(items[0].data['tt_name'] == null || items[0].data['tt_name'] == ''){
								showError('请先描述该节点');
								return;
							} else {
								me.saveNodes.push(items[0]);
								me.saveNode();
							}
	    				} else if(btn == 'no'){
	    					return;
	    				} 
					});
				 }
				Ext.each(record.childNodes, function(){
					me.checkChild(this);
				});	
			}
			if(record.dirty){
				if(record.data['tt_id'] == null || record.data['tt_id'] == 0){
					me.saveNodes.push(record);
				} else {
					me.updateNodes.push(record);
				}
			}
		} else {
			if(record.dirty){
				if(record.data['tt_id'] == null || record.data['tt_id'] == 0){
					me.saveNodes.push(record);
				} else {
					me.updateNodes.push(record);
				}
			}
		}
	},
	saveNode: function(){
		var me = this;
		me.getUpdateNodes();
		var save = new Array();
		var update = new Array();
		var index = 0;
		Ext.each(me.saveNodes, function(){
			if(this.data.tt_name != null && this.data.tt_name != ''){
				var o = {
						tt_id: this.data.tt_id,
						tt_name: this.data.tt_name,
					    tt_parentid:this.data.tt_parentid,
						tt_isleaf: this.data.tt_isleaf,
						tt_ptid:this.data.tt_ptid,
						tt_ptname:this.data.tt_ptname,
						tt_code: this.data.tt_code
				};
				save[index++] = Ext.JSON.encode(o);
			}
		});
		index = 0;
		Ext.each(me.updateNodes, function(){
			if(this.data.tt_name != null && this.data.tt_name != ''){
				var o = {
						tt_id: this.data.tt_id,
						tt_name: this.data.tt_name,
						tt_parentid: this.data.tt_parentid,
						tt_isleaf: this.data.tt_isleaf,
						tt_ptid:this.data.tt_ptid,
						tt_ptname:this.data.tt_ptname,
						tt_code: this.data.tt_code
				};
				update[index++] = Ext.JSON.encode(o);
			}
		});
		if(save.length > 0 || update.length > 0){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'plm/task/saveTaskTemplate.action',
	        	params: {
	        		save: unescape(save.toString().replace(/\\/g,"%")),
	        		update: unescape(update.toString().replace(/\\/g,"%"))
	        	},
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		activeTab.setLoading(false);
	        		if(res.success){
	        			me.saveNodes = [];
	        			me.updateNodes = [];
	        			me.getTreeGridNode({parentId:0,condition:condition});
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        		}
	        	}
	        });
		}
	},
	getExpandItem: function(root){
		var me = this;
		if(!root){
			root = this.store.tree.root;
		}
		var node = null;
		if(root.childNodes.length > 0){
			Ext.each(root.childNodes, function(){
				if(this.isExpanded()){
					node = this;
					if(this.childNodes.length > 0){
						var n = me.getExpandItem(this);
						node = n == null ? node : n;
					}
				}
			});
		}
		return node;
	},
	deleteNode: function(record){
		var me = this;
		if(record){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'plm/task/saveTaskTemplate.action',
	        	params: {
	        		id: Number(record.data['tt_id'])
	        	},
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		activeTab.setLoading(false);
	        		if(res.success){
	        			record.remove(true);
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        		}
	        	}
	        });
		}
	},
	getUpdateNodes: function(root){
		var me = this;
		if(!root){
			root = this.store.tree.root;
			me.updateNodes = [];
		}
		if(root.childNodes.length > 0){
			Ext.each(root.childNodes, function(){
				if(this.dirty){
					if(this.data['tt_id'] != null && this.data['tt_id'] != 0){
						me.updateNodes.push(this);
					}
				}
				if(this.data['leaf'] == false && this.childNodes.length > 0){
					me.getUpdateNodes(this);
				}
			});
		} else {
			if(root.dirty){
				if(root.data['tt_id'] != null && root.data['tt_id'] != 0){
					me.updateNodes.push(root);
				}
			}
		}
	},
	getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
    	return tab;
	}
});