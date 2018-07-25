/**
 * ERP项目gridpanel样式5:sysNavigation专用treegrid
 */
Ext.define('erp.view.common.JProcessDeploy.JCTreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpJCTreeGrid',
	region: 'south',
	layout : 'fit',
	id: 'treegrid', 
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    singleExpand: true,
    saveNodes: [],
    updateNodes: [],
    deleteNodes: [],
    store: Ext.create('Ext.data.TreeStore', {
    	fields: [{"name":"jd_selfId","type":"string"},
    	         {"name":"jd_classifiedName","type":"string"},
    	         {"name":"jd_parentId","type":"string"},
    	         {"name":"jd_formUrl","type":"string"},
    	         {"name":"jd_caller","type":"string"},
    	         {"name":"jd_processDefinitionId","type":"string"},
    	         {"name":"jd_processDefinitionName","type":"string"},
    	         {"name":"jd_processDescription","type":"string",},
    	         {"name":"jd_enabled","type":"boolean"}],
    	root : {
        	text: 'root',
        	id: 'root',
    		expanded: true
    	}
    }),
					    columns : [ {
						"header" : "ID",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_selfId",
						"align" : "left",
						"xtype" : "treecolumn",
						"readOnly" : false,
						"hidden" : true,
						"text" : "ID"
					}, {
						"header" : "流程类别",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_classifiedName",
						"align" : "left",
						"xtype" : "treecolumn",
						"readOnly" : false,
						"hidden" : false,
						"width" : 280.0,
						"text" : "流程类别",
						"editor" : {
							"xtype" : "textfield"
						}
					}, {
						"header" : "父节点ID",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_parentId",
						"align" : "left",
						"xtype" : "treecolumn",
						"readOnly" : false,
						"hidden" : true,
						"width" : 0.0,
						"text" : "父节点ID"
					}, {
						"header" : "表单路径",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_formUrl",
						"align" : "left",
						"readOnly" : false,
						"hidden" : false,
						"width" : 280.0,
						"text" : "表单路径",
						"editor" : {
							"xtype" : "textfield"
						}
					}, {
						"header" : "对应Caller",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_caller",
						"align" : "left",
						"readOnly" : false,
						"hidden" : false,
						"width" : 150.0,
						"text" : "对应Caller",
						"editor" : {
							"xtype" : "textfield"
						}
					}, {
						"header" : "流程定义ID",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_processDefinitionId",
						"align" : "left",
						//"xtype" : "checkcolumn",
						"readOnly" : false,
						"hidden" : false,
						"width" : 80.0,
						"text" : "流程定义ID",
						"editor" : {
							///"cls" : "x-grid-checkheader-editor",
							//"xtype" : "checkbox"
							"xtype" : "textfield"
						}
					}, {
						"header" : "流程名称",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_processDefinitionName",
						"align" : "right",
						"readOnly" : false,
						"hidden" : false,
						"width" : 80.0,
						"text" : "流程名称",
						"editor" : {
							"xtype" : "textfield"
							/*"cls" : "x-grid-checkheader-editor",
							"xtype" : "combo",
							"store" : Ext.create('Ext.data.Store', {
								fields : [ 'display', 'value' ],
								data : [ {
									"display" : "选项卡模式",
									"value" : 0
								}, {
									"display" : "弹出框式",
									"value" : 1
								}, {
									"display" : "空白页",
									"value" : 2
								}, {
									"display" : "窗口模式",
									"value" : 3
								} ]
							}),
							"displayField" : 'display',
							"valueField" : 'value',
							"queryMode" : 'local',
							"value" : 0*/
						}/*,*/
						/*"renderer" : function(val){
							var rVal = "选项卡模式";
							val = val || 0;
							switch (Number(val)) {
								case 0:
									rVal = "选项卡模式";break;
								case 1:
									rVal = "弹出框式";break;
								case 2:
									rVal = "空白页";break;
								case 3:
									rVal = "窗口模式";break;
							}
							return rVal;
						}*/
					}, {
						"header" : "流程描述",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_processDescription",
						"align" : "left",
						//"xtype" : "checkcolumn",
						"readOnly" : false,
						"hidden" : false,
						"width" : 90.0,
						"text" : "流程描述",
						"editor" : {
							"xtype":"textfield"
							//"cls" : "x-grid-checkheader-editor",
							//"xtype" : "checkbox"
						}
					}, {
						"header" : "是否启用",
						"dbfind" : "",
						"cls" : "x-grid-header-1",
						"summaryType" : "",
						"dataIndex" : "jd_enabled",
						"align" : "left",
						"xtype" : "checkcolumn",
						"readOnly" : false,
						"hidden" : false,
						"width" : 70.0,
						"text" : "是否启用",
						"editor" : {
							"cls" : "x-grid-checkheader-editor",
							"xtype" : "checkbox"
						}
					} ],
   /* plugins: Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}),*/
	tbar: [{
		iconCls: 'tree-add',
		text: $I18N.common.button.erpAddButton,
		handler: function(){
			var treegrid = Ext.getCmp('treegrid');
			var items = treegrid.selModel.selected.items;
			if(items.length > 0 && items[0].isLeaf() == true){
				if(items[0].data['sn_id'] == null || items[0].data['sn_id'] == ''){
					warnMsg('如果在该节点下添加子节点，需先保存该节点，是否保存?', function(btn){
						if(btn == 'yes'){
							if(items[0].data['sn_displayname'] == null || items[0].data['sn_displayname'] == ''){
								showError('请先描述该节点');
								return;
							} else {
								items[0].data['leaf'] = false;
								items[0].data['cls'] = 'x-tree-cls-parent';
								items[0].data['sn_isleaf'] = false;
								treegrid.saveNodes.push(items[0]);
								treegrid.saveNode();
							}
	    				} else if(btn == 'no'){
	    					return;
	    				} 
					});
				} else {
					items[0].data['leaf'] = false;
					items[0].data['cls'] = 'x-tree-cls-parent';
					items[0].data['sn_isleaf'] = false;
					items[0].dirty = true;
					var o = {
							sn_parentid: items[0].data['sn_id'],
							sn_isleaf: true,
							cls: "x-tree-cls-node",
							parentId: items[0].data['sn_id'],
							leaf: true,
							sn_deleteable: true,
							deleteable: true,
							allowDrag: true,
							showMode: 0
					};
					items[0].appendChild(o);
					items[0].expand(true);
				}
			} else {
				var record = treegrid.getExpandItem();
				if(record){
					var o = {
							sn_parentid: record.data['sn_id'],
							sn_isleaf: true,
							cls: "x-tree-cls-node",
							parentId: record.data['sn_id'],
							sn_deleteable: true,
							deleteable: true,
							leaf: true,
							allowDrag: true,
							showMode: 0
					};
					record.appendChild(o);
				}
			}
		}
	},{
		iconCls: 'tree-delete',
		text: $I18N.common.button.erpDeleteButton,
		handler: function(){
			var treegrid = Ext.getCmp('treegrid');
			var items = treegrid.selModel.selected.items;
			if(items.length > 0){
				if(items[0].isLeaf() == true){
					if(items[0].data['sn_id'] != null && items[0].data['sn_id'] != ''){
						if(items[0].data['sn_deleteable'] == true){
							warnMsg('确定删除节点[' + items[0].data['sn_displayname'] + "]?", function(btn){
								if(btn == 'yes'){
									treegrid.deleteNode(items[0]);
			    				} else if(btn == 'no'){
			    					return;
			    				} 
							});
						} else {
							showError('该节点不允许删除!');
						}
					} else {
						items[0].remove(true);
					}
				} else {
					if(items[0].data['sn_id'] != null || items[0].data['sn_id'] != ''){
						if(items[0].data['sn_deleteable'] == true){
							Ext.each(items[0].childNodes, function(){
								if(this.data['sn_deleteable'] == false){
									showError('该节点有不可删除子节点，无法删除该节点!');
									return;
								}
							});
							warnMsg('确定删除节点[' + items[0].data['sn_displayname'] + ']及其子节点？', function(btn){
								if(btn == 'yes'){
									treegrid.deleteNode(items[0]);
			    				} else if(btn == 'no'){
			    					return;
			    				} 
							});
						} else {
							showError('该节点不允许删除!');
						}
					} else {
						items[0].remove(true);
					}
				}
			} else {
				var record = treegrid.getExpandItem();
				if(record){
					if(record.childNodes.length == 0){
						if(record.data['sn_id'] != null && record.data['sn_id'] != ''){
							if(record.data['sn_deleteable'] == true){
								warnMsg('确定删除节点[' + record.data['sn_displayname'] + ']？', function(btn){
									if(btn == 'yes'){
										treegrid.deleteNode(record);
				    				} else if(btn == 'no'){
				    					return;
				    				} 
								});
							} else {
								showError('该节点不允许删除!');
							}
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
	},'->',{
		xtype:'dbfindtrigger',
		fieldLabel:'选择类别',
		id:'classify',
		name:'classify'
		
	},{
		xtype:'textfield',
		fieldLabel:'类别id',
		hidden: true,
		id:'classifyid'
	},{
		iconCls: 'tree-save',
		text: '移动',
		id: 'move',
		listeners: {
			afterrender: function(btn){
				btn.setDisabled(true);
			},
			click: function(){
				var treegrid = Ext.getCmp('treegrid');
				var items = treegrid.selModel.selected.items;
				if(items.length > 0 && items[0].isLeaf() == true){
					if(Ext.getCmp('classifyid').value == null || Ext.getCmp('classifyid').value == ''){
						return showError('请先指定类别');
					}
					if(Ext.getCmp('classifyid').value == items[0].data.jd_parentId){
						return showError('已经在当前类别下，无需移动');
					}
					console.log(items);
//					alert(items[0].data.id);
					var id = items[0].data.id;
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'common/updateClassify.action',
			        	params: {
			        		parentid: Ext.getCmp('classifyid').value,
			        		id: id
			        	},
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.success){
			        			window.location.href = window.location.href;
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				}
			}
		}		
	},'->'
	],
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		this.callParent(arguments);
		this.getTreeGridNode({parentId: 0});
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
        	url : basePath + 'common/getLazyJProcessDeploy.action',
        	params: param,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		activeTab.setLoading(false);
        		if(res.tree){
        			var tree = res.tree;
        			Ext.each(tree, function(t){
        				t.jd_selfId = t.id;
        				t.jd_parentId = t.parentId;
        				t.jd_classifiedName = t.text;
        			//	t.sn_isleaf = t.leaf;
        				t.jd_caller = t.creator;
        				t.jd_formUrl = t.url;
        				t.jd_processDefinitionId = t.qtitle;
        				t.jd_enabled = t.using;
        				t.jd_processDefinitionName = t.version;
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
				if(record.data['sn_id'] == null || record.data['sn_id'] == ''){
					warnMsg('如果在节点' + record.data['sn_id'] + '下添加子节点，需先保存该节点，是否保存?', function(btn){
						if(btn == 'yes'){
							if(items[0].data['sn_displayname'] == null || items[0].data['sn_displayname'] == ''){
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
		} else {
			if(record.dirty){
				if(record.data['sn_id'] == null || record.data['sn_id'] == ''){
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
			if(this.data.sn_displayname != null && this.data.sn_displayname != ''){
				if(this.data.sn_tabtitle == null || this.data.sn_tabtitle == ''){
					this.data.sn_tabtitle == this.data.sn_displayname;
				}
				if(this.data.sn_deleteable == null || this.data.sn_deleteable == ''){
					this.data.sn_deleteable == 'T';
				}
				var o = {
						sn_id: this.data.sn_id,
						sn_displayname: this.data.sn_displayname,
						sn_url: this.data.sn_url,
						sn_isleaf: this.data.sn_isleaf ? 'T' : 'F',
						sn_tabtitle: this.data.sn_tabtitle,
						sn_parentid: this.data.sn_parentid,
						sn_deleteable: this.data.sn_deleteable ? 'T' : 'F',
						sn_using: this.data.sn_using ? 1 : 0,
						sn_showmode: this.data.sn_showmode
				};
				save[index++] = Ext.JSON.encode(o);
			}
		});
		index = 0;
		Ext.each(me.updateNodes, function(){
			if(this.data.sn_displayname != null && this.data.sn_displayname != ''){
				if(this.data.sn_tabtitle == null || this.data.sn_tabtitle == ''){
					this.data.sn_tabtitle == this.data.sn_displayname;
				}
				if(this.data.sn_deleteable == null || this.data.sn_deleteable == ''){
					this.data.sn_deleteable == 'T';
				}
				var o = {
						sn_id: this.data.sn_id,
						sn_displayname: this.data.sn_displayname,
						sn_url: this.data.sn_url,
						sn_isleaf: this.data.sn_isleaf ? 'T' : 'F',
						sn_tabtitle: this.data.sn_tabtitle,
						sn_parentid: this.data.sn_parentid,
						sn_deleteable: this.data.sn_deleteable ? 'T' : 'F',
						sn_using: this.data.sn_using ? 1 : 0,
						sn_showmode: this.data.sn_showmode
				};
				update[index++] = Ext.JSON.encode(o);
			}
		});
		if(save.length > 0 || update.length > 0){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'ma/saveSysNavigation.action',
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
	        			me.getTreeGridNode({parentId: 0});
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
	        	url : basePath + 'ma/deleteSysNavigation.action',
	        	params: {
	        		id: Number(record.data['sn_id'])
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
					if(this.data['sn_id'] != null && this.data['sn_id'] != ''){
						me.updateNodes.push(this);
					}
				}
				if(this.data['leaf'] == false && this.childNodes.length > 0){
					me.getUpdateNodes(this);
				}
			});
		} else {
			if(root.dirty){
				if(root.data['sn_id'] != null && root.data['sn_id'] != ''){
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
	},
	/**
	 * treegrid用到了checkcolumn时，由于其store的差异，根据recordIndex不能直接得到record，
	 * 采用下面的方法可以在点击checkbox时，得到当前的record，再进而就可以修改checkbox的check属性等...
	 */
	getRecordByRecordIndex: function(recordIndex, node){
		var me = this;
		if(!node){
			node = this.store.tree.root;
			me.findIndex = 0;
			me.findRecord = null;
		}
		if(me.findRecord == null){
			if(node.childNodes.length > 0 && node.isExpanded()){
				Ext.each(node.childNodes, function(){
					if(me.findIndex == recordIndex){
						me.findRecord = this;
						me.findIndex++;
					} else {
						me.findIndex++;
						me.getRecordByRecordIndex(recordIndex, this);
					}
				});
			} else {
				if(me.findIndex == recordIndex){
					me.findRecord = node;
				}
			}
		}
	},
	checkRecord: function(record, dataIndex, checked){
		var me = this;
		if(record.childNodes.length > 0){
			Ext.each(record.childNodes, function(){
				this.set(dataIndex, checked);
				me.checkRecord(this, dataIndex, checked);
			});
		}
	}
});