/**
 * ERP项目gridpanel样式5:documentCatalog专用treegrid
 */
Ext.define('erp.view.core.grid.DocCaTreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpDocCaTreeGrid',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
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
    expandedNodes: [],
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
//			var treegrid = Ext.getCmp('treegrid');
//			var items = treegrid.selModel.selected.items;
//			var bool = false;
//			Ext.Ajax.request({//拿到tree数据
//	        	url : basePath + 'oa/document/queryDocPower.action',
//	        	params:{
//	        		id : items[0].data["dc_id"],
//	        		field : "add"
//	        	},
//	        	async: false,
//	        	callback : function(options,success,response){
////	        		console.log(response);
//	        		var res = new Ext.decode(response.responseText);
////	        		alert(res.error);
//	        		if(res.error){
//	        			showError(res.error);
//	        		} else {
//	        			bool = true;
//	        		}
//	        	}
//			});
////			alert(bool);
//			if(bool){
//				if(items.length > 0 && items[0].isLeaf() == false){
//					if(items[0].data['dc_id'] == null || items[0].data['dc_id'] == ''){
//						warnMsg('如果在该文件夹下添加子文件夹，需先保存该文件夹，是否保存?', function(btn){
//							if(btn == 'yes'){
//								if(items[0].data['dc_displayname'] == null || items[0].data['dc_displayname'] == ''){
//									showError('请先描述该节点');
//									return;
//								} else {
//									treegrid.saveNodes.push(items[0]);
//									treegrid.saveNode();
//								}
//		    				} else if(btn == 'no'){
//		    					return;
//		    				} 
//						});
//					} else {
//						items[0].data['leaf'] = false;
//						items[0].data['cls'] = 'x-tree-cls-parent';
//						items[0].data['dc_isfile'] = 'F';
//						items[0].dirty = true;
//						var o = {
//								dc_parentid: items[0].data['dc_id'],
//								dc_isfile: 'T',
//								cls: "x-tree-cls-parent",
//								parentId: items[0].data['dc_id'],
//								leaf: false,
////								dc_deleteable: 'T',
////								deleteable: true,
//								allowDrag: true
//						};
//						items[0].appendChild(o);
//						items[0].expand(true);
//					}
//				} else {
//					var record = treegrid.getExpandItem();
//					if(record){
//						var o = {
//								dc_parentid: record.data['dc_id'],
//								dc_isfile: 'F',
//								cls: "x-tree-cls-parent",
//								parentId: record.data['dc_id'],
////								dc_deleteable: 'T',
////								deleteable: true,
//								leaf: false,
//								allowDrag: true
//						};
//						record.appendChild(o);
//					}
//				}
//			}
		}
	},{
		iconCls: 'tree-delete',
		text: $I18N.common.button.erpDeleteButton,
		handler: function(){
//			var treegrid = Ext.getCmp('treegrid');
//			var items = treegrid.selModel.selected.items;
//			var bool = false;
//			Ext.Ajax.request({//拿到tree数据
//	        	url : basePath + 'oa/document/queryDocPower.action',
//	        	params:{
//	        		id : items[0].data["dc_id"],
//	        		field : "delete"
//	        	},
//	        	async: false,
//	        	callback : function(options,success,response){
//	        		console.log(response);
//	        		var res = new Ext.decode(response.responseText);
//	        		if(res.error){
//	        			showError(res.error);
//	        		} else {
//	        			bool = true;
//	        		}
//	        	}
//			});
////			alert(bool);
//			if(bool){
////				console.log(items);alert(items.length);alert(items[0].isLeaf());
//				if(items.length > 0){
//					if(items[0].isLeaf() == true){
//						if(items[0].data['dc_id'] != null && items[0].data['dc_id'] != ''){
//							if(items[0].data['dc_deleteable'] == 'T'){
//								warnMsg('确定删除文件 [' + items[0].data['dc_displayname'] + "]?", function(btn){
//									if(btn == 'yes'){
////										console.log(items[0]);
//										treegrid.deleteNode(items[0]);
//				    				} else if(btn == 'no'){
//				    					return;
//				    				} 
//								});
//							} else {
//								showError('该文件不允许删除!');
//							}
//						} else {
//							items[0].remove(true);
//						}
//					} else {
//						if(items[0].data['dc_id'] != null || items[0].data['dc_id'] != ''){
//							if(items[0].data['dc_deleteable'] == 'T'){
//								Ext.each(items[0].childNodes, function(){
//									if(this.data['dc_deleteable'] == 'F'){
//										showError('该文件夹有不可删除子节点，无法删除该文件夹!');
//										return;
//									}
//								});
//								warnMsg('确定删除文件夹[' + items[0].data['dc_displayname'] + ']及其所有子文件？', function(btn){
//									if(btn == 'yes'){
//										treegrid.deleteNode(items[0]);
//				    				} else if(btn == 'no'){
//				    					return;
//				    				} 
//								});
//							} else {
//								showError('该文件夹不允许删除!');
//							}
//						} else {
//							items[0].remove(true);
//						}
//					}
//				} else {
//					var record = treegrid.getExpandItem();
//					if(record){
//						if(record.childNodes.length == 0){
//							if(record.data['dc_id'] != null && record.data['dc_id'] != ''){
//								if(record.data['dc_deleteable'] == 'T'){
//									warnMsg('确定删除文件夹[' + record.data['dc_displayname'] + ']？', function(btn){
//										if(btn == 'yes'){
//											treegrid.deleteNode(record);
//					    				} else if(btn == 'no'){
//					    					return;
//					    				} 
//									});
//								} else {
//									showError('该文件夹不允许删除!');
//								}
//							} else {
//								record.remove(true);
//							}
//						}
//					}
//				}
//			}
			
		}
	},{
		iconCls: 'tree-save',
		text: $I18N.common.button.erpSaveButton,
		handler: function(){
//			var treegrid = Ext.getCmp('treegrid'),
//			nodes = treegrid.store.tree.root.childNodes;
//			treegrid.saveNodes = [];
//			Ext.each(nodes, function(){
//				treegrid.checkChild(this);
//			});
//			treegrid.saveNode();
		}
//	},{
//		iconCls: 'x-button-icon-download',
//		text: '下载',
//		handler: function(){
//			var treegrid = Ext.getCmp('treegrid');
//			var items = treegrid.selModel.selected.items;
//			var bool = false;
//			Ext.Ajax.request({
//	        	url : basePath + 'oa/document/queryDocPower.action',
//	        	params:{
//	        		id : items[0].data["dc_id"],
//	        		field : "download"
//	        	},
//	        	async: false,
//	        	callback : function(options,success,response){
////	        		console.log(response);
//	        		var res = new Ext.decode(response.responseText);
//	        		if(res.error){
//	        			showError(res.error);
//	        		} else {
//	        			bool = true;
//	        		}
//	        	}
//			});
//			if (bool) {
//				if (items[0].data['dc_isfile'] == 'T') {
//					window.location.href = basePath + 'oa/download.action?path=' + items[0].data["dc_url"];
////					Ext.Ajax.request({
////					    url : basePath + 'oa/online.action',
////					    params : {
////					    	filepath : items[0].data["dc_url"],
////					    	isOnline : true
////					    },
////						async : false,
////						callback : function(options,success,response) {
////							console.log(response);
////							var res = new Ext.decode(response.responseText);
////							if (res.error) {
////								showError(res.error);
////							}
////						}
////					});
//     			} else {
//					Ext.Ajax.request({
//					    url : basePath + 'oa/zip.action',
//					    params : {
//					    	id : items[0].data["dc_id"]
//					    },
//						async : false,
//						callback : function(options,success,response) {
//							console.log(response);
//							var res = new Ext.decode(response.responseText);
//							if (res.error) {
//								showError(res.error);
//							} else {
//								if (res.warning) {
//									alert(res.warning);
//								}
//								alert(res.path);
//								window.location.href = basePath + 'oa/download.action?path=' + res.path;
//							}
//						}
//				   });
//			   }
////				path = items[0].data['dc_id'];	
//			}
//		}
	},{
    	iconCls: 'x-button-icon-print',
		text: "查看",
		handler: function(){
			var me = Ext.getCmp('treegrid');
			var items = me.selModel.selected.items;
			if(items[0] != null && items[0].data.dc_isfile == 'T'){
				var id = items[0].data.dc_version.split(".")[1];
				if(id != null && id != ''){
					if(items[0].data.dc_creator_id == em_uu || Ext.getCmp('treegrid').getDocumentListPower(id).see == 1){
						var panel = Ext.getCmp("documentlist" + id); 
						var main = parent.Ext.getCmp("content-panel");
						if(!panel){ 
							var title = items[0].data.dc_displayname;		        		
							panel = { 
									title : title,
									tag : 'iframe',
									tabConfig:{tooltip: items[0].data.dc_displayname},
									frame : true,
									border : false,
									layout : 'fit',
									iconCls : 'x-tree-icon-tab-tab1',
									html : '<iframe id="iframe_documentlist_' + id + '" src="' + basePath + "jsps/oa/document/documentDetail.jsp?id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
									closable : true,
									listeners : {
										close : function(){
											main.setActiveTab(main.getActiveTab().id); 
										}
									} 
							};
							me.FormUtil.openTab(panel, "documentlist" + id); 
						}else{ 
							main.setActiveTab(panel); 
						}
					} else {
						showError("亲！你没有相关操作权限哦！");return;
					}	        	 
				}				
			} else {
				showError("亲！请选择要查看的文档");return;
			}
		}
    },{
    	iconCls: 'x-button-icon-scan',
		text: "刷新",
		handler: function(){
			Ext.getCmp('treegrid').getTreeGridNode({parentId: 0});
		}
    },{
		xtype: 'tbtext',
		id: 'path',
		style: 'background: #C6d1c5'
	}],
	
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
        	url : basePath + 'common/lazyDocumentTree.action',
        	params: param,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		activeTab.setLoading(false);
        		if(res.tree){
        			var tree = res.tree;
        			console.log(response);
        			Ext.each(tree, function(t){
        				t.dc_id = t.id;
        				t.dc_parentid = t.parentId;
        				t.dc_displayname = t.text;
        				t.dc_isfile = t.leaf ? 'T' : 'F';
//        				t.dc_tabtitle = t.text;
//        				t.dc_url = t.url;
//        				t.dc_deleteable = t.deleteable ? 'T' : 'F';
        				t.dc_updatetime = t.updatetime;
//        				t.dc_filesize = t.filesize;
        				t.dc_creator = t.creator;
        				t.dc_creator_id = t.creator_id;
        				t.dc_version = t.version;
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
				if(record.data['dc_id'] == null || record.data['dc_id'] == ''){
					warnMsg('如果在节点' + record.data['dc_id'] + '下添加子节点，需先保存该节点，是否保存?', function(btn){
						if(btn == 'yes'){
							if(items[0].data['dc_displayname'] == null || items[0].data['dc_displayname'] == ''){
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
			} else if(record.childNodes.length == 0){
				if(record.data['dc_id'] == null || record.data['dc_id'] == '')
					if(record.data['dc_displayname'] == null || record.data['dc_displayname'] == ''){
						showError('请先描述该节点');
						return;
					} else {
						me.saveNodes.push(record);
						me.saveNode();
					}
			}
		} else {
			if(record.dirty){
				if(record.data['dc_id'] == null || record.data['dc_id'] == ''){
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
			if(this.data.dc_displayname != null && this.data.dc_displayname != ''){
				if(this.data.dc_tabtitle == null || this.data.dc_tabtitle == ''){
					this.data.dc_tabtitle == this.data.dc_displayname;
				}
//				if(this.data.dc_deleteable == null || this.data.dc_deleteable == ''){
//					this.data.dc_deleteable == 'T';
//				}
				var o = {
						dc_id: this.data.dc_id,
						dc_displayname: this.data.dc_displayname,
//						dc_url: this.data.dc_url,
						dc_isfile: this.data.dc_isfile,
//						dc_tabtitle: this.data.dc_tabtitle,
						dc_parentid: this.data.dc_parentid,
//						dc_deleteable: this.data.dc_deleteable
				};
				save[index++] = Ext.JSON.encode(o);
			}
		});
		index = 0;
		Ext.each(me.updateNodes, function(){
			if(this.data.dc_displayname != null && this.data.dc_displayname != ''){
				if(this.data.dc_tabtitle == null || this.data.dc_tabtitle == ''){
					this.data.dc_tabtitle == this.data.dc_displayname;
				}
//				if(this.data.dc_deleteable == null || this.data.dc_deleteable == ''){
//					this.data.dc_deleteable == 'T';
//				}
				var o = {
						dc_id: this.data.dc_id,
						dc_displayname: this.data.dc_displayname,
//						dc_url: this.data.dc_url,
						dc_isfile: this.data.dc_isfile,
//						dc_tabtitle: this.data.dc_tabtitle,
						dc_parentid: this.data.dc_parentid,
//						dc_deleteable: this.data.dc_deleteable
				};
				update[index++] = Ext.JSON.encode(o);
			}
		});
		if(save.length > 0 || update.length > 0){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			
			Ext.Ajax.request({
	        	url : basePath + 'oa/saveDocumentCatalog.action',
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
	getExpandNode: function(root){
		var treegrid = Ext.getCmp('treegrid');
		var items = treegrid.selModel.selected.items;
		var road = '';
		Ext.each(items,function(){
//			console.log(items.length);
			road += this.data['dc_id']; 
		});
		return road;
	},
	deleteNode: function(record){
		var me = this;
		if(record){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'oa/deleteDocumentCatalog.action',
	        	params: {
	        		id: Number(record.data['dc_id'])
	        	},
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
//	        		console.log(response);
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
					if(this.data['dc_id'] != null && this.data['dc_id'] != ''){
						me.updateNodes.push(this);
					}
				}
				if(this.data['leaf'] == false && this.childNodes.length > 0){
					me.getUpdateNodes(this);
				}
			});
		} else {
			if(root.dirty){
				if(root.data['dc_id'] != null && root.data['dc_id'] != ''){
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
	getExpandedItems: function(record){
		var me = this;
		me.getRecordParents(record);
		if(record.isExpanded()){
			
		} else {
			me.expandedNodes.push(record);						
		}
	},
	getRecordParents: function(record, parent){
		var me = this;
		if(!parent){
			parent = me.store.tree.root;
			me.expandedNodes = [];
		}
		if(parent.childNodes.length > 0){
			Ext.each(parent.childNodes, function(){
				if(this.isExpanded()){
					me.expandedNodes.push(this);
					if(this.childNodes.length > 0){
						me.getRecordParents(record, this);
					}
				}
			});
		}
	},
	getDocumentListPower: function(id){
		var o = new Object();
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'hr/employee/getJobDocumentListPower.action',
        	params: {
        		dcl_id: id,
        		em_id: em_uu
        	},
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		console.log(response);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.success){
        			return;
        		} else {
//        			alert(o.DELETE);
        			o.see =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_see;
        			o.edit =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_edit;
        			o.share =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_share;
        			o.del =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_delete;
        			o.download =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_download;
        			o.journal =  res.documentlistpower == null ? 0 : res.documentlistpower.dlp_journal;
        		}
        	}
        });
		return o;
	}
});