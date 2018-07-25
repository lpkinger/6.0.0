Ext.QuickTips.init();
Ext.define('erp.controller.common.CommonModule', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    	'common.commonModule.TreePanel', 'common.main.Toolbar', 'core.trigger.SearchField', 'common.commonModule.CenterPanel', 'common.commonModule.ModuleGroupWin',
    	'common.commonModule.SynchronousWin'
    ],
    init:function(){
    	var me = this;
    	me.flag = true; //防止双击时tree节点重复加载
    	this.control({
    		'erpTreePanel': {
				render:function(view){
					var d = new Ext.dd.DragZone(view.el, {
						ddGroup: 'moduleitem',
						containerScroll : true,
						onBeforeDrag: function(data, e) {
							return !data.config.group
						},
						getTargetFromEvent: function(e) {
				            return e.getTarget('.x-module-item');
				        },
						getDragData: function(e) {
				            var sourceEl = e.getTarget(view.itemSelector, 10), d;
				            if(sourceEl.tagName==='INPUT'||!view.getSelectionModel().getSelection()[0]) return null;
							var record = view.getSelectionModel().getSelection()[0];
				            if (sourceEl) {
				                d = sourceEl.cloneNode(true);
				                d.id = Ext.id();
				                return (view.dragData = {
				                    sourceEl: sourceEl,
				                    repairXY: Ext.fly(sourceEl).getXY(),
				                    ddel: d,
				                    config: {
				                    	id: record.get('id'),
				                    	group: !record.get('leaf'),
				                    	text: record.get('text'),
				                    	url: record.get('url'),
				                    	addurl: record.get('addurl'),
				                    	parentId: record.raw?record.raw.parentId:record.get('parentId')
				                    }
				                });
				            }
				        },
				        getRepairXY: function() {
				            return this.dragData.repairXY;
				        }
					});
					view.dragZone = d;
				},
                itemmousedown: function(selModel, record) {
                    if (!me.flag) {
                        return;
                    }
                    me.flag = false;
                    setTimeout(function() {
                    	me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20); //防止双击时tree节点重复加载
                },
                itemclick: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    me.flag = false;
                    setTimeout(function() {
                        me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20);
                },
                itemdbclick: function(selModel, record) {
                    if (!me.flag) {
                        return;
                    }
                    me.flag = false;
                    setTimeout(function() {
                        me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20);
                },
                addclick: me.handleAddClick,
                itemappend: function(node, itemNode, index, eOpts) {
                	if(itemNode.get('leaf')) {
                		if(Ext.getCmp('centerpanel').getItemByID(itemNode.get('id'))) {
                        	itemNode.set('commonuse', true);
                    	}
                	}
                }
            },
            'centerpanel': {
            	afterrender: function() {
            		Ext.Ajax.request({
						url: basePath + 'commonuse/getList.action',
						method : 'get',
						async: false,
						callback : function(options,success,response){
							var rs = new Ext.decode(response.responseText);
							if(rs.exceptionInfo){
								showError(rs.exceptionInfo);return;
							}
							if(rs.success){
								var data = rs.data;
								var groups = [];
								data.map(function(d){
									d.cuid = d.cu_id;
									d.id = d.cu_itemid;
									d.parentId = d.cu_parentid;
									d.text = d.cu_text;
									d.group = d.cu_group==-1?true:false;
									d.groupid = d.cu_groupid;
									d.index = d.cu_index;
									d.expanded = d.cu_expanded==-1?true:false;
									d.url = d.cu_url;
									d.addurl = d.cu_addurl;
									
									if(d.group) {
										groups.push(d);
									}
								});
								if(groups.length == 0) {
									groups.push({
										id: '-1',
										text: '未分组',
										group: true,
										index: 0,
										expanded: false
									});
								}
								groups.map(function(group){
									group.items = [];
									data.map(function(d) {
										(d.groupid == group.id) ? group.items.push(d) : '';
									});
								});
								groups.map(function(group){
									group.items.length > 0 ? group.items.sort(function(a,b){return a.index-b.index}) : '';
								});
								groups.sort(function(a,b){return a.index-b.index});
								Ext.getCmp('centerpanel').importGroupData(groups);
							}
						}
					});
            	},
            	addDragListener: function(view) {
            		var d = new Ext.dd.DragZone(view.el, {
						ddGroup: 'moduleitem',
						containerScroll : true,
						onBeforeDrag: function(data, e) {
							return !data.config.group;
						},
						getDragData: function(e) {
				            var sourceEl = e.getTarget(view.itemSelector, 10), d;
				            if (sourceEl) {
				                d = sourceEl.cloneNode(true);
				                d.id = Ext.id();
				                var record = view.getRecord(sourceEl);
				                return (view.dragData = {
				                    sourceEl: sourceEl,
				                    repairXY: Ext.fly(sourceEl).getXY(),
				                    ddel: d,
				                    config: {
				                    	index: sourceEl.viewIndex,
				                    	cuid: record.get('cuid'),
				                    	id: record.get('id'),
				                    	parentId: record.get('parentId'),
				                    	text: record.get('text'),
				                    	group: record.get('group'),
				                    	url: record.get('url'),
				                    	addurl: record.get('addurl')
				                    }
				                });
				            }
				        },
				        getRepairXY: function() {
				            return this.dragData.repairXY;
				        },
				        onStartDrag: function() {
				        	Ext.fly(this.dragData.sourceEl).addCls('x-target-drag');
				        },
				        afterInvalidDrop: function() {
				        	Ext.fly(this.dragData.sourceEl).removeCls('x-target-drag');
				        },
				        afterValidDrop: function() {
				        	Ext.fly(this.dragData.sourceEl).removeCls('x-target-drag');
				        }
					});
					view.dragZone = d;
            	},
            	addDropListener: function(view){
            		var drop = new Ext.dd.DropZone(view.el, {
						ddGroup:'moduleitem',
					    getTargetFromEvent: function(e) {
				            return e.getTarget('.x-module-item');
				        },
				        onNodeEnter : function(target, dd, e, data){
				            //Ext.fly(target).addCls('x-target-hover');
				        },
				        onNodeOut : function(target, dd, e, data){
				            Ext.fly(target).removeCls('x-target-hover-below');
				            Ext.fly(target).removeCls('x-target-hover-above');
				        },
				        onNodeOver : function(target, dd, e, data){
				        	Ext.fly(target).removeCls('x-target-hover-below');
				            Ext.fly(target).removeCls('x-target-hover-above');
				        	var exy = e.getXY(),
				        		tbox = target.getBoundingClientRect(),
				        		trecord = view.getRecord(target);
				        		
				        	if(exy[1] >= (tbox.top+tbox.height/2)) { // 鼠标在目标项下半区域
				        		data.config.pos = 'below';
				        		Ext.fly(target).addCls('x-target-hover-below');
				        	}else { // 鼠标在目标项上半区域
				        		// 不允许添加到第一个项目之前
				        		if(target.viewIndex == 0) {
				        			data.config.pos = 'below';
				        			Ext.fly(target).addCls('x-target-hover-below');
				        			return Ext.dd.DropZone.prototype.dropAllowed;
				        		}else {
				        			data.config.pos = 'above';
				        			Ext.fly(target).addCls('x-target-hover-above');
				        		}
				        	}
				        	if(trecord.get('group')) {
				        		data.config.groupid = trecord.get('id');
				        	}else {
				        		data.config.groupid = trecord.get('groupid');
				        	}
				        	if(data.config.group) {
				        		return Ext.dd.DropZone.prototype.dropNotAllowed;
				        	}else
				            	return Ext.dd.DropZone.prototype.dropAllowed;
				        },
				        onNodeDrop : function(target, dd, e, data){
				            var store = view.getStore(),
				            	targetIndex = target.viewIndex,
				            	originIndex = data.config.index,
				            	pos = data.config.pos,
				            	index = pos=='below'?(targetIndex + 1):(targetIndex),
				            	newItem = {
				            		cuid: data.config.cuid,
				            		id: data.config.id,
				            		parentId: data.config.parentId,
				            		index: originIndex,
				            		text: data.config.text,
				            		group: data.config.group,
				            		groupid: data.config.groupid,
				            		url: data.config.url,
				            		addurl: data.config.addurl
				            	};
				            
				            if(typeof originIndex === 'number') { // 从centerpanel调整顺序
				            	if(originIndex == targetIndex) {
				            		return true;
				            	}else if(originIndex > targetIndex) {
				            		store.removeAt(originIndex);
				            		
				            		store.insert(index, newItem);
				            	}else {
				            		store.insert(index, newItem);
				            		store.removeAt(originIndex);
				            	}
				            }else { // 从左侧的树添加
				            	var testNodeItem = Ext.getCmp('centerpanel').getItemByID(newItem.id);
				            	if(testNodeItem) {
				            		var testGroup = Ext.getCmp('centerpanel').getItemByID(testNodeItem.get('groupid'));
				            		showMessage('提示', '该项目已被添加到<a style="color:red;">['+testGroup.get('text')+']</a>', 3000);
				            	}else {
				            		Ext.getCmp('tree-panel').getSelectionModel().getSelection()[0].set('commonuse', true);
					            	newItem.index = index;
					            	store.insert(index, newItem);
				            	}
				            }
				            // 重设序号
				            store.data.each(function(d,i){if(d.get('index')>=index){d.set('index', i);}});
				        }
					});
					view.dropZone = drop;	
            	},
            	resize: function(view, adjWidth, adjHeight, eOpts) {
            		var centerPanel = Ext.getCmp('dataviewpanel');
            		view.resetViewSize(centerPanel.getWidth(), centerPanel.getHeight());
            	},
            	onSave: function(panel) {
            		var data = panel.getAllGroup();
            		Ext.getBody().mask('waiting...')
            		Ext.Ajax.request({
            			url: basePath + 'commonuse/importAll.action',
            			method: 'post',
                        params: {
                            jsonstr: Ext.JSON.encode(data)
                        },
                        callback: function(options, success, response) {
                        	Ext.getBody().unmask();
                        	var res = new Ext.decode(response.responseText);
                        	if(res.success) {
                        		showMessage('提示', '保存成功!', 3000);
                        		// 如果不是是在tab中则直接退出
                        		if(!parent.Ext.getCmp('tree-tab')) {
                        			return;
                        		}
                        		// 重新设置导航中的常用功能项
	                        	var tree = parent.Ext.getCmp('tree-panel');
	                        	var record = tree.getRootNode().childNodes[0];
	                        	tree.setLoading(true);
	                        	Ext.Ajax.request({
			                        url: basePath + 'common/getCommonUseTree.action',
			                        method : 'get',
			                        callback: function(options, success, response) {
			                            tree.setLoading(false);
			                            var res = new Ext.decode(response.responseText);
			                            if (res.tree && record.get('id')=='commonuse') {
			                            	record.removeAll();
			                                record.appendChild(res.tree);
			                                record.expand(false, true); //展开
			                                me.flag = true;
			                            } else if (res.exceptionInfo) {
			                                showError(res.exceptionInfo);
			                                me.flag = true;
			                            }
			                        }
			                    });
                        	}else {
                        		showMessage('提示', '保存失败!', 5000);
                        	}
                        }
            		});
            	}
            },
            'synchronousWin': {
            	onSynchronous: function(masters) {
            		me.synchronous(masters);
            	}
            }
    	});
    },
    loadTab: function(selModel, record) {
        var me = this;
        me.isTureMasterFlag = true;
        if (!record.get('leaf') && !(record.raw && record.raw.queryMode=='CLOUD')) {
            if (record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
                record.collapse(true, true); //收拢
                me.flag = true;
            } else { //未展开
                //看是否加载了其children
                if (record.childNodes.length == 0) {
                    //从后台加载
                    var tree = Ext.getCmp('tree-panel');
                    var condition = tree.baseCondition || null;
                    tree.setLoading(true, tree.body);
                    Ext.Ajax.request({ //拿到tree数据
                        url: basePath + 'common/lazyTree.action',
                        params: {
                            parentId: record.data['id'],
                            condition: condition
                        },
                        callback: function(options, success, response) {
                            tree.setLoading(false);
                            var res = new Ext.decode(response.responseText);
                            if (res.tree) {
                                if (!record.get('level')) {
                                    record.set('level', 0);
                                }
                                Ext.each(res.tree, function(n) {
                                	n.qtip = n.qtip || n.text;
                                    if (!n.leaf) {
                                        n.level = record.get('level') + 1;
                                        n.iconCls = 'x-tree-icon-level-' + n.level;
                                    }
                                });
                                record.appendChild(res.tree);
                                record.expand(false, true); //展开
                                me.flag = true;
                            } else if (res.exceptionInfo) {
                                showError(res.exceptionInfo);
                                me.flag = true;
                            }
                        }
                    });
                } else {
                		record.expand(false, true); //展开
                    me.flag = true;
                }
            }
        }
    },
    handleAddClick: function(view, rowIndex, colIndex, e) {
    	var me=this;
        var record = view.getRecord(view.findTargetByEvent(e)),
            commonuse = record.get('commonuse'),
            id = record.get('id'),
            parentId = record.raw?record.raw.parentId:record.get('parentId'),
            text = record.get('text'),
            url = record.get('url'),
            addurl = record.get('addurl');
        if (!commonuse) {
            var win = Ext.create('Ext.window.Window', {
            	title: '添加到常用模块',
            	width: 300,
            	height: 200,
            	layout: 'fit',
            	modal: true,
            	nodeRecord: record,
            	items: [{
            		xtype: 'form',
            		//layout: 'vbox',
            		padding: 10,
            		defaults: {
            			padding: '5 0 0 0'
            		},
            		items: [{
            			xtype: 'hidden',
            			fieldLabel: 'ID',
            			name: 'id',
            			value: id
            		}, {
            			xtype: 'hidden',
            			fieldLabel: 'parentID',
            			name: 'parentId',
            			value: parentId
            		}, {
            			xtype: 'textfield',
            			fieldLabel: '名称',
            			name: 'text',
            			readOnly: true,
            			value: text
            		}, {
            			xtype: 'hidden',
            			fieldLabel: 'URL',
            			name: 'url',
            			value: url
            		}, {
            			xtype: 'hidden',
            			fieldLabel: 'ADDURL',
            			name: 'addurl',
            			value: addurl
            		}, {
            			xtype: 'combobox',
            			fieldLabel: '分组',
            			name: 'groupid',
            			editable: false,
            			store: Ext.create('Ext.data.Store', {
            				fields: ['groupname', 'groupid'],
            				data: Ext.getCmp('centerpanel').getAllGroup().map(function(g) {
            					return {
            						groupname: g.text,
            						groupid: g.id
            					}
            				})
            			}),
            			displayField: 'groupname',
            			valueField: 'groupid',
            			value: me.addgroup || '-1'
            		}]
            	}],
            	buttonAlign: 'center',
            	buttons: [{
            		xtype: 'button',
            		text: '确定',
            		handler: function(btn) {
            			var data = btn.ownerCt.ownerCt.down('form').getForm().getValues();
	        			data.group = false;
	        			Ext.getCmp('centerpanel').addItem(data);
	        			btn.ownerCt.ownerCt.nodeRecord.set('commonuse', true);
	        			me.addgroup = data.groupid;
	        			win.close();
            		}
            	}, {
            		xtype: 'button',
            		text: '取消',
            		handler: function() {
            			win.close();
            		}
            	}],
            	listeners: {
            		afterrender: function() {
            			this.down('form').down('combo').focus();
            		}
            	}
            });
            win.show();
        }else {
        	return;
        }
    },
    synchronous: function(masters) {
		var data = Ext.getCmp('centerpanel').getAllGroup();
		var sobs = masters.map(function(m) {return m.get('ma_user')});
		Ext.getBody().mask('waiting...');
		Ext.Ajax.request({
			url: basePath + 'commonuse/importAll.action',
			method: 'post',
            params: {
                jsonstr: Ext.JSON.encode(data)
            },
            callback: function(options, success, response) {
            	Ext.getBody().unmask();
            	var res = new Ext.decode(response.responseText);
            	if(res.success) {
            		Ext.Ajax.request({
            			url: basePath + 'commonuse/synchronous.action',
            			method: 'post',
            			params: {
            				sobs: sobs
            			},
                        callback: function(options, success, response) {
                        	Ext.getBody().unmask();
                        	var res = new Ext.decode(response.responseText);
                        	if(res.success) {
                    			var masterStore = Ext.getCmp('synchronousWin').down('grid').store;
                    			var successMasters = res.masters.successMasters.map(function(m) {
                    				return masterStore.getAt(masterStore.find('ma_user', m)).get('ma_function');
                    			});
                    			var failureMasters = res.masters.failureMasters.map(function(m) {
                    				return masterStore.getAt(masterStore.find('ma_user', m)).get('ma_function');
                    			});
                    			var msg = '';
                    			if(successMasters.length > 0) {
	                    			msg += '成功同步到下列账套：<br/><a style="color:red;">'+successMasters.join('</a><br/><a style="color:red;">')+'</a><br/>';
                    			}
                    			if(failureMasters.length > 0) {
                    				msg += '同步到下列账套失败：<br/><a style="color:red;">'+failureMasters.join('</a><br/><a style="color:red;">')+'</a><br/>';
                    			}
                    			showMessage('提示', msg, 8000);
                        	}else {
                        		showMessage('提示', '同步失败!', 5000);
                        	}
                        }
            		});
            	}else {
            		showMessage('提示', '保存失败!', 5000);
            	}
            }
		});
    }
});