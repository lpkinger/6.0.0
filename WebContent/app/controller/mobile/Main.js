Ext.QuickTips.init();
Ext.define('erp.controller.mobile.Main', {
	extend : 'Ext.app.Controller',
	views : [ 'mobile.Main' ],
	refs : [ {
		ref : 'treeroot',
		selector : '#treeroot'
	} , {
		ref : 'tab',
		selector : 'tabpanel'
	} , {
		ref: 'card',
		selector: '#card'
	}],
	init : function() {
		Ext.MessageBox.cls = 'custom custom-box';
		this.control({
			'#logout': {
				click: function() {
					var me = this;
            		Ext.Msg.confirm("提示", "确定退出ERP系统?", function(btn){
            			if (btn == 'yes') {
            				me.logout();
            			}
            		});
				}
			},
			'#treeroot' : {
				afterrender : function() {
					this.getTreeNode(0, 1);
				},
				itemclick: function(view, record, el, idx, e) {
					if(record.get('leaf')) {
                		this.addPanel(record.get('text'), record.get('url'), record.get('id'));
                	} else {
                		var pid = Number(record.get('id'));
                		this.getTreeNode(pid, 2, record.get('text'));
                	}
				}
			},
			'tabpanel': {
				afterrender: function(tab) {
					var me = this;
					tab.addPanel = function(a, b, c) {
						me.addPanel(a, b, c);
					};
				}
			},
		/*	'#commonuse' : {
				afterrender : function(view) {
					this.getCommonUse(view);
				},
				itemclick: function(view, record, el, idx, e) {
					this.addPanel(record.get('cu_description'), record.get('cu_url'), record.get('cu_snid'));
				}
			},*/
			'#mychoice' : {
				afterrender : function(view) {
					var me = this;
					setTimeout(function(){
						me.getMyChoice(view);
					}, 500);
				},
				itemclick: function(view, record, el, idx, e) {
					var id = record.get('id');
					if (id) {
						var url = 'jsps/common/jtaketask.jsp?formCondition=jp_nodeIdIS' + id;
						this.addPanel(record.get('taskname'), url, id);
					}
				}
			},
			'#myflow' : {
				afterrender : function(view) {
					var me = this;
					setTimeout(function(){
						me.getMyFlow(view);
					}, 500);
				},
				itemclick: function(view, record, el, idx, e) {
					var id = record.get('id');
					if (id) {
						var url = 'jsps/common/jprocessDeal.jsp?formCondition=jp_nodeIdIS' + id;
						this.addPanel(record.get('taskname'), url, id);
					}
				}
			},
			/*'#mytask' : {
				afterrender : function(view) {
					var me = this;
					setTimeout(function(){
						me.getMyTask(view);
					}, 500);
				},
				itemclick: function(view, record, el, idx, e) {
					var id = record.get('id');
					var url = 'jsps/plm/record/workrecord.jsp?formCondition=ra_idIS' + id + '&gridCondition=wr_raidIS' + id;
					this.addPanel(record.get('taskname'), url, id);
				}
			},*/
			'dataview[level=2]' : {
				itemclick: function(view, record, el, idx, e) {
					if(record.get('leaf')) {
                		this.addPanel(record.get('text'), record.get('url'), record.get('id'));
                	} else {
                		var pid = Number(record.get('id'));
                		this.getTreeNode(pid, 3, record.get('text'), record);
                	}
				}
			},
			'dataview[level=3]' : {
				itemclick: function(view, record, el, idx, e) {
					if(record.get('leaf')) {
                		this.addPanel(record.get('text'), record.get('url'), record.get('id'));
                	} else {
                		var pid = Number(record.get('id'));
                		this.getTreeNode(pid, 4, record.get('text'), record);
                	}
				}
			},
			'dataview[level=4]' : {
				itemclick: function(view, record, el, idx, e) {
					if(record.get('leaf')) {
						view.up('window').hide();
                		this.addPanel(record.get('text'), record.get('url'), record.get('id'));
                	} else {
                		var pid = Number(record.get('id'));
                		this.getTreeNode(pid, 5, record.get('text'), record);
                	}
				}
			},
			'dataview[level=5]' : {
				itemclick: function(view, record, el, idx, e) {
					if(record.get('leaf')) {
						view.up('window').hide();
                		this.addPanel(record.get('text'), record.get('url'), record.get('id'));
                	}
				}
			}
		});
	},
	setLoading : function(bool) {
		var dom = document.getElementById('loading');
		if (bool)
			dom.style.display = 'block';
		else
			dom.style.display = 'none';
	},
	cacheStore: {},
	getTreeNode : function(parentId, level, desc, record) {
		if(level == 2) {
			var p = Ext.getCmp(this.prefix + parentId);
			if(p) {
				this.addSecondCard(parentId);return;
			}
		}
		if(level == 3) {
			var nodes = this.cacheStore[parentId];
			if(nodes) {
				this.addThirdCard(parentId, nodes, record);return;
			}
		}
		if(level == 4) {
			var p = Ext.getCmp(this.prefix + parentId);
			if(p) {
				this.addForthWin(parentId);return;
			}
		}
		if(level == 5) {
			var nodes = this.cacheStore[parentId];
			if(nodes) {
				this.addFifthWin(parentId, nodes, record);return;
			}
		}
		var me = this;
		Ext.Ajax.request({
			url : basePath + 'common/lazyTree.action',
			params : {
				parentId : parentId
			},
			callback : function(opt, s, r) {
				me.setLoading(false);
				var res = new Ext.decode(r.responseText);
				if (res.tree) {
					var tree = res.tree;
					if(parentId == 0)
						me.getTreeroot().store.loadData(tree);
					else if(level == 2)
						me.addSecondCard(parentId, tree, desc);
					else if(level == 3)
						me.addThirdCard(parentId, tree, record);
					else if(level == 4)
						me.addForthWin(parentId, tree, desc);
					else if(level == 5)
						me.addFifthWin(parentId, tree, record);
				} else if (res.exceptionInfo) {
					alert(res.exceptionInfo);
				}
			}
		});
	},
	prefix: 'mob-node-',
	addSecondCard: function(pid, nodes, desc) {
		var me = this, p = Ext.getCmp(this.prefix + pid);
		if(!p) {
			p = Ext.create('Ext.container.Container', {
				id: me.prefix + pid,
				margin: '5 0 0 5',
				layout: 'border',
				height: 1000,
				cls: 'custom',
				items: [{
					xtype: 'toolbar',
					region: 'north',
					margin: '5 0 0 0',
					cls: 'custom-tb',
					height: 40,
					items: [desc, '->',{
						text: '返回&raquo;',
						cls: 'custom-button',
						handler: function(btn){
							var p = me.getCard().items.items[0];
							if(p){
								me.getCard().layout.setActiveItem(p);
							} else {
								btn.up('panel').hide();
							}
						}
					}]
				},{
					xtype: 'container',
					region: 'center',
					layout: 'hbox',
					height: 100000,
					items: [{
						xtype: 'dataview',
						cls: 'datalist',
						level: 2,
						flex: 1,
						itemSelector: '.custom-button',
				        overItemCls : 'tree-node-hover',
				        selectedItemCls : 'selected',
				        enableDragDrop: true,
				        tpl: Ext.create('Ext.XTemplate',
				            '<tpl for=".">',
				            	'<tpl if="leaf">',
					            	'<div class="custom-button leaf"',
					            		'<font>{text}</font>',
						            '</div>',
					            '</tpl>',
					            '<tpl if="leaf == false">',
						            '<div class="custom-button"',
					            		'<font>{text}</font>',
						            '</div>',
					            '</tpl>',
				            '</tpl>'
				        ),
				        store: new Ext.data.Store({
				        	fields: ['text', 'parentId', {name: 'id', type: 'number'}, 'url', 'leaf']
				        }),
				        onItemSelect: function(record) {
				            var node = this.getNode(record);
				            if (node) {
				                Ext.fly(node).addCls(this.selectedItemCls);
				            }
				        },
				        onItemDeselect: function(record) {
				            var node = this.getNode(record);
				            if (node) {
				                Ext.fly(node).removeCls(this.selectedItemCls);
				            }
				        }
					},{
						xtype: 'dataview',
						cls: 'datalist',
						level: 3,
						flex: 2,
						itemSelector: '.custom-button',
				        overItemCls : 'tree-node-hover',
				        selectedItemCls : 'selected',
				        enableDragDrop: true,
				        tpl: Ext.create('Ext.XTemplate',
				        	'<tpl for=".">',
				            	'<tpl if="leaf">',
					            	'<div class="custom-button leaf"',
					            		'<font>{text}</font>',
						            '</div>',
					            '</tpl>',
					            '<tpl if="leaf == false">',
						            '<div class="custom-button node"',
					            		'<font>{text}</font>',
						            '</div>',
					            '</tpl>',
				            '</tpl>'
				        ),
				        store: new Ext.data.Store({
				        	fields: ['text', 'parentId', {name: 'id', type: 'number'}, 'url', 'leaf']
				        }),
				        onItemSelect: function(record) {
				            var node = this.getNode(record);
				            if (node) {
				                Ext.fly(node).addCls(this.selectedItemCls);
				            }
				        },
				        onItemDeselect: function(record) {
				            var node = this.getNode(record);
				            if (node) {
				                Ext.fly(node).removeCls(this.selectedItemCls);
				            }
				        }
					}]
				}]
			});
			me.getCard().add(p);
			p.down('dataview').store.loadData(nodes);
		}
		me.getCard().layout.setActiveItem(p);
	},
	addThirdCard: function(pid, nodes, record) {
		var ca = this.getCard().layout.getActiveItem(),
			view = ca.down('dataview[level=3]');
		if(view) {
			view.store.loadData(nodes);
			this.cacheStore[pid] = nodes;
		}
	},
	addForthWin: function(pid, nodes, desc) {
		var me = this, p = Ext.getCmp(this.prefix + pid);
		if(!p) {
			p = Ext.create('Ext.Window', {
				modal: true,
				id: me.prefix + pid,
				width: '100%',
				height: '80%',
				cls: 'custom',
				title: desc,
				closeAction: 'hide',
				layout: 'anchor',
				items: [{
					xtype: 'container',
					anchor: '100% 100%',
					layout: 'hbox',
					defaults: {
						margin: '4 0 10 0'
					},
					autoScroll: true,
					items: [{
						xtype: 'dataview',
						cls: 'datalist',
						level: 4,
						flex: 1,
						itemSelector: '.custom-button',
				        overItemCls : 'tree-node-hover',
				        selectedItemCls : 'selected',
				        enableDragDrop: true,
				        tpl: Ext.create('Ext.XTemplate',
				            '<tpl for=".">',
				            	'<tpl if="leaf">',
					            	'<div class="custom-button leaf"',
					            		'<font>{text}</font>',
						            '</div>',
					            '</tpl>',
					            '<tpl if="leaf == false">',
						            '<div class="custom-button node"',
					            		'<font>{text}</font>',
						            '</div>',
					            '</tpl>',
				            '</tpl>'
				        ),
				        store: new Ext.data.Store({
				        	fields: ['text', 'parentId', {name: 'id', type: 'number'}, 'url', 'leaf']
				        }),
				        onItemSelect: function(record) {
				            var node = this.getNode(record);
				            if (node) {
				                Ext.fly(node).addCls(this.selectedItemCls);
				            }
				        },
				        onItemDeselect: function(record) {
				            var node = this.getNode(record);
				            if (node) {
				                Ext.fly(node).removeCls(this.selectedItemCls);
				            }
				        }
					},{
						xtype: 'dataview',
						cls: 'datalist',
						level: 5,
						flex: 1,
						itemSelector: '.custom-button',
				        overItemCls : 'tree-node-hover',
				        selectedItemCls : 'selected',
				        enableDragDrop: true,
				        tpl: Ext.create('Ext.XTemplate',
				        	'<tpl for=".">',
				            	'<tpl if="leaf">',
					            	'<div class="custom-button leaf"',
					            		'<font>{text}</font>',
						            '</div>',
					            '</tpl>',
					            '<tpl if="leaf == false">',
						            '<div class="custom-button node"',
					            		'<font>{text}</font>',
						            '</div>',
					            '</tpl>',
				            '</tpl>'
				        ),
				        store: new Ext.data.Store({
				        	fields: ['text', 'parentId', {name: 'id', type: 'number'}, 'url', 'leaf']
				        }),
				        onItemSelect: function(record) {
				            var node = this.getNode(record);
				            if (node) {
				                Ext.fly(node).addCls(this.selectedItemCls);
				            }
				        },
				        onItemDeselect: function(record) {
				            var node = this.getNode(record);
				            if (node) {
				                Ext.fly(node).removeCls(this.selectedItemCls);
				            }
				        }
					}]
				}]
			});
			p.down('dataview[level=4]').store.loadData(nodes);
		}
		p.show();
	},
	addFifthWin: function(pid, nodes, record) {
		var win = Ext.ComponentQuery.query('window[hidden=false]'),
			view = win[0].down('dataview[level=5]');
		if(view) {
			view.store.loadData(nodes);
			this.cacheStore[pid] = nodes;
		}
	},
	logout : function() {
		Ext.Ajax.request({
			url : basePath + 'common/logout.action',
			callback : function(opt, s, r) {
				var data = Ext.decode(r.responseText);
				if (data.success) {
					document.location.href = basePath;
				} else {
					if (data.reason) {
						Ext.Msg.alert(data.reason);
					} else {
						Ext.Msg.alert(data.exceptionInfo);
					}
				}
			}
		});
	},
	getCommonUse: function(view) {
		Ext.Ajax.request({
			url : basePath + 'common/getCommonUse.action',
			method : 'get',
			callback : function(opt, s, r){
				var res = new Ext.decode(r.responseText);
				if(res.exception || res.exceptionInfo){
					alert(res.exceptionInfo);
					return;
				}
				view.store.loadData(res.commonuse);
			}
		});
	},
	getMyChoice: function(view) {
		Ext.Ajax.request({
			url : basePath + 'common/datalist.action',
			params: {
				caller: 'JProCand',
				condition:  'jp_candidate=\'' + em_code + '\'  AND jp_status=\'待审批'+'\' AND jp_flag=1', 
				page: 1,
				pageSize: 10,
				_noc : 1
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exception || res.exceptionInfo){
					return;
				}
				var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
				if(task.length > 0){
					var data = new Array();
					Ext.Array.each(task, function(item){
						item.id = item.jp_nodeId;
						item.taskname = item.jp_form + " -> " + item.jp_nodeName;
						item.status = item.jp_status;
						item.type = '可选流程';
						item.typecode = 'procand';
						data.push(item);
					});   
					view.store.loadData(data);
				} else {
					view.store.loadData([{status : '无'}]);
				}
			}
		});
	},
	getMyFlow: function(view) {
		Ext.Ajax.request({
			url : basePath + 'common/datalist.action',
			params: {
				caller: 'JProcess!Me',
				condition:  '(jp_nodedealman=\'' + em_code + '\'  AND jp_status=\'待审批'+'\') or (jp_launcherid=\'' + em_code + '\'  AND jp_status=\'未通过'+'\') ', 
				page: 1,
				pageSize: 10,
				_noc : 1
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exception || res.exceptionInfo){
					return;
				}
				var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
				if(task.length > 0){
					var data = new Array();
					Ext.Array.each(task,function(item){
						item.id = item.jp_nodeId;
						item.taskname = item.jp_form + " -> " + item.jp_nodeName;
						item.status = item.jp_status;
						if(item.jp_status == '未通过'){
							item.type = '未同意流程';
							item.typecode = 'unprocess';
						}else{
							item.type = '待审批流程';
							item.typecode = 'process';
						}
						data.push(item);
					});	
					view.store.loadData(data);
				} else {
					view.store.loadData([{status : '无'}]);
				}
			}
		});
	},
	getMyTask: function(view) {
		Ext.Ajax.request({
			url : basePath + 'common/datalist.action',
			params: {
				caller: 'ResourceAssignment',
				condition:  'ra_emid=' + em_uu+' AND ra_statuscode!=\'FINISHED'+'\'', 
				page: 1,
				pageSize: 10,
				_noc : 1
			},
			method : 'post',				
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exception || res.exceptionInfo){
					return;
				}
				var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
				if(task.length > 0){
					var data = new Array();
					Ext.Array.each(task,function(item){
						item.id = item.ra_id;
						item.taskname = item.ra_taskname;
						item.status = item.ra_status;
						item.type = '工作任务';
						item.typecode = 'worktask';
						data.push(item);
					});	
					view.store.loadData(data);
				} else {
					view.store.loadData([{status : '无'}]);
				}
			}
		});
	},
	addPanel: function(text, url, id) {
		var me = this;
		var tab = me.getTab();
		var p = Ext.getCmp(new String(id));
		if(p) {
			tab.setActiveTab(p);return;
		}
		if(Ext.isEmpty(url))
			return;
		url = this.parseUrl(url);
		p = Ext.create('Ext.container.Container', {
			id: new String(id),
			title: text,
			closable: true,
			autoScroll: true,
        	html : '<iframe src="' + url + 
        		'" height="100%" width="100%" frameborder="0" style="border: none;" scrolling="auto"></iframe>',
            tabConfig: {tooltip: text},
            setLoading: function(bool) {
            	me.setLoading(bool);
            },
            close: function() {
            	this.destroy();
            },
            listeners: {
            	resize: function(cmp, w, h, opt) {
            		var width = Ext.getBody().dom.clientWidth, height = Ext.getBody().dom.clientHeight;
            		var myWidth = (1 + (1366 - width)/1366)*100, myHeight = (1 + (768 - height)/768)*100;
            		if(width < height) {
            			myWidth*=1.6;
            		} else {
            			myHeight*=2;
            		}
            		var iframe = cmp.getEl().down('iframe');
            		iframe.dom.width = '' + myWidth + '%';
            		iframe.dom.height = '' + myHeight + '%';
            	}
            }
		});
		tab.add(p);
		tab.setActiveTab(p);
	},
	parseUrl: function(url){
		if(url.indexOf('session:em_uu') != -1){
			url = url.replace(/session:em_uu/, em_uu);
		}
		if(url.indexOf('session:em_code') != -1){
			url = url.replace(/session:em_code/, "'" + em_code + "'");
		}
		if(url.indexOf('sysdate') != -1){
			url = url.replace(/sysdate/, "to_date('" + Ext.Date.toString(new Date()) + "','Y-m-d')");
		}
		if(url.indexOf('session:em_name') != -1){
			url = url.replace(/session:em_name/,"'"+em_name+"'" );
		}
		if(url.indexOf(basePath) == -1) {
			url = basePath + url;
		}
		return url;
    }
});