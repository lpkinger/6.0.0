/*
控制层,
所有逻辑代码都在这里写
 */
Ext.QuickTips.init();
Ext.define('erp.controller.common.Main', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil'],
    /*    refs:[ 
    {ref: 'erpTreePanel',selector: 'erpTablePanel'}, 
    {ref: 'erpTabPanel',selector:'erpTablePanel'} 
    ], */
    stores: ['TreeStore'], //声明该控制层要用到的store
    /*   models: ['TreeModel'],//声明该控制层要用到的model
     */
    views: ['common.main.Header', 'common.main.Bottom', 'common.main.TreePanel', 'common.main.WorkspaceTreePanel', 'common.main.TabPanel', 'common.main.Viewport', 'common.main.FlowPanel',
        'common.main.Toolbar', 'common.main.TreeTabPanel', 'core.trigger.SearchField', 'core.window.ReLogin','common.main.NavigationTreePanel','common.main.NavigationToolbar'
    ], //声明该控制层要用到的view
    init: function() {
        var me = this;
        var timestr = '';
        this.isTureMasterFlag = true;
        me.FormUtil = Ext.create('erp.util.FormUtil');
        this.flag = true; //防止双击时tree节点重复加载
        this.naviFlag = true; //防止双击时tree节点重复加载
        //每隔8秒刷新【网络寻呼】
        Ext.defer(function() {
            me.loadPagingRelease(me.timestr,true);
        }, 1000);
        //每隔一分钟刷新[首页]---主要考虑点开首页不做任何操作
        Ext.defer(function() {
            me.refreshDesk();
        }, 60000);
        this.control({
            'erpTreePanel': {
                itemmousedown: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                    	me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20); //防止双击时tree节点重复加载
                },
                itemclick: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                        me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20);
                },
                itemdbclick: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                        me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20);
                },
                itemmouseenter: me.showActions,
                itemmouseleave: me.hideActions,
                beforeitemmouseenter: me.showActions,
                addclick: me.handleAddClick,
                beforeitemcollapse: function() {
                	if(event && event.target && event.target.nodeName=='IMG') {
	                	return false;
                	}else {
                		return true;
                	}
                },
                beforeitemexpand: function(node, e) {
                	if(event && event.target && event.target.nodeName=='IMG') {
	                	return false;
                	}else {
                		return true;
                	}
                }
            },
            'workspaceTreePanel': {
                itemmousedown: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                    	me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20); //防止双击时tree节点重复加载
                },
                itemclick: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                        me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20);
                },
                itemdbclick: function(selModel, record) {
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                        me.flag = true;
                        me.loadTab(selModel, record);
                    }, 20);
                },
                itemmouseenter: me.showActions,
                itemmouseleave: me.hideActions,
                beforeitemmouseenter: me.showActions,
                addclick: me.handleAddClick
            },
            'panel[id=HomePage]':{
            	activate :function(tab){
            		//切账套后需打开工作台，延后刷新首页，第一次点击激活首页刷新
            		if(tab.needReload){
            			var win=iframe_homePage.contentWindow;
            			if(win && win.Ext){
            				win.location.reload();
							tab.needReload = false;
							return;
            			}
            		}
            		
            		//刷新首页模块数据
        			if(iframe_homePage){           		
            			var win=iframe_homePage.contentWindow;
            			if(win && win.Ext){
            				var desk=win.Ext.ComponentQuery.query('deskportal');
            				if(desk.length>0) desk[0].fireEvent('datarefresh',desk[0],'activeRefresh');
            			}
        			}
            	}
            },
           '#informations':{
           		activate:function(i){ 
       				i.ownerCt.tabBar.activeTab.el.dom.classList.remove('text');
       				
       				var Ext = i.items.items[0].el.dom.contentWindow.Ext || null;
       				if(Ext) {
	       				var informationgrid = Ext.getCmp('informationgrid');
	       				if(informationgrid.readStatusData){
							informationgrid.updateReadstatus(informationgrid.readStatusData);
						}
	       			}
           		}
           },
           '#jprocesscenter':{
           		activate:function(i){
           			i.ownerCt.tabBar.activeTab.el.dom.classList.remove('text');
           		}
           },
            '#taskcenter':{
           		activate:function(i){
           			i.ownerCt.tabBar.activeTab.el.dom.classList.remove('text');
           		}
           },
            'menuitem[id=lock]': {
                click: function(btn) {
                    //锁定屏幕
                    me.lockPage();
                }
            },
            'menuitem[id=addrbook]': {
                click: function() {
                    me.showAddrBook();
                }
            },
            'treepanel[id=addr-tree]': {
            	itemmouseup: function(item, record, index, e) {
            		if (record.data['leaf']) {
                        //开始寻呼
                        me.showDialogBox(null, Math.abs(record.data['id']), record.data['text']);
                    } else {
                    	var ptId=record.data.id;
        				if (record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
            			    record.collapse(true, true); //收拢
        	            } else { //未展开
            		    //看是否加载了其children
                    		if (record.childNodes.length == 0) {
                        		//从后台加载
                        		var bool=false;
                    			var tree = Ext.getCmp('addr-tree');
                       			tree.setLoading(true, tree.body);
                       			Ext.Ajax.request({ //拿到tree数据
                        		    url: basePath + 'oa/addrBook/getAddrBookTree.action',
                        		    params: {
        	                            	parentid: ptId
        	                        },
                           		 callback: function(options, success, response) {
                              		  tree.setLoading(false);
                              		  var res = new Ext.decode(response.responseText);
                             		  if (res.tree) {
                                        record.appendChild(res.tree);
                                        /*if (!(record.isExpanded()) && contains(record.data['id'], 'org', true)) {*/ //如果是组织
                                            var ch = record.childNodes,
                                                bool = false;
                                            Ext.each(ch, function(s) {
                                            	if (s.data['leaf']) {
                                                    bool = true;
                                                }
                                            });
                                            if (bool) {
                                                //刷新当前组织下人员在线状态
                                                me.checkOnline(record, e);
                                            }
                                       /* }*/
                                      /* me.checkOnline(record, e);*/
                                       record.expand(false, true);//展开
                              		  } else if (res.exceptionInfo) {
                                   		 showError(res.exceptionInfo);
                                	}
                            	}
                        	});
               		 } else {
                            record.expand(false, true); //展开
                        }
                    }
                   }
                }
            },
            'erpNavigationTreePanel': {//全功能导航
                itemmousedown: function(selModel, record) {
                    if (!this.naviFlag) {
                        return;
                    }
                    this.naviFlag = false;
                    setTimeout(function() {
                        me.naviFlag = true;
                        me.loadNavigation(selModel, record);
                    }, 20); //防止双击时tree节点重复加载
                },
                itemclick: function(selModel, record) {
                    if (!this.naviFlag) {
                        return;
                    }
                    this.naviFlag = false;
                    setTimeout(function() {
                        me.naviFlag = true;
                        me.loadNavigation(selModel, record);
                    }, 20);
                },
                itemdbclick: function(selModel, record) {
                    if (!this.naviFlag) {
                        return;
                    }
                    this.naviFlag = false;
                    setTimeout(function() {
                        me.naviFlag = true;
                        me.loadNavigation(selModel, record);
                    }, 20);
                },
                itemmouseenter: me.showSyanavigationActions,
                itemmouseleave: me.hideSyanavigationActions,
                beforeitemmouseenter: me.showSyanavigationActions,
                addclick:me.openSyanavigationDetail,
                updateclick:me.updeteSyanavigation
            },
            'menuitem[id=set-pwd]': {
                click: function() {
                    me.showPwdPanel();
                }
            },
            '#navigationtree': {
	    		itemmousedown: function(selModel, record){
		    		var tree = selModel.ownerCt;
		    		me.loadTree(tree, record);
		    	  }
	    	},
	    	'menuitem[id=tools-imp]': {
                click: function() {
                    // 配置方案导入
                	me.showImportDumpFileWin();
                }
            },
           '#changeMaster':{
           		beforerender:function(i){
           			i.addListener('getmessage',function(){    						   
						   me.getInfoCount(me.timestr,true);
						 }    
					);    
           		}
           }
        });
    },//升级
    updeteSyanavigation:function(view, rowIndex, colIndex, column, e){
	    var w = Ext.create('Ext.Window',{
	    		   title: '添加到',
	    		   height: "80%",
	    		   width: "50%",
	    		   maximizable : false,
	    		   modal: true,
	    		   buttonAlign : 'center',
	    		   layout : 'anchor',
	    		   items: [{
	    			   anchor: '100% 100%',
	    			   xtype: 'treepanel',
	    			   id:'navigationtree',
	    			   rootVisible: false,
	    			   useArrows: true,
	    			   store: Ext.create('Ext.data.TreeStore', {
	    			   	storeId:'navigationTreeStore',
	    				   root : {
	    					   text: 'root',
	    					   id: 'root',
	    					   expanded: true
	    				   }
	    			   })
	    		   }],
	    		   buttons : [{
	    			   text: '确定',
	    			   iconCls: 'x-button-icon-confirm',
	    			   cls: 'x-btn-gray',
	    			   handler: function(btn){
	    				   var t = btn.ownerCt.ownerCt.down('treepanel');
	    				   /*if(!Ext.isEmpty(t.title)) {
	    					   Ext.getCmp('fb_module').setValue(t.title);
	    				   }*/
	    				   btn.ownerCt.ownerCt.close();
	    			   }
	    		   },{
	    			   text : '关  闭',
	    			   iconCls: 'x-button-icon-close',
	    			   cls: 'x-btn-gray',
	    			   handler : function(btn){
	    				   btn.ownerCt.ownerCt.close();
	    			   }
	    		   }]
	    	   });
	    	   w.show();
	    	   this.loadTree(Ext.getCmp('navigationtree'), null);
	       
    },
    loadTree: function(tree, record){
	    var pid = 0;
	    if(record) {
	    	if (record.get('leaf')) {
	    		 return;
	    	} else {
	    		if(record.isExpanded() && record.childNodes.length > 0){
	    			record.collapse(true, true);//收拢
	    			return;
	    		} else {
	    			if(record.childNodes.length != 0){
	    				record.expand(false, true);//展开
	    			    return;
	    			}
	    		}
	    	}
	    	pid = record.get('id');
	    }
	    tree.setLoading(true);
	    Ext.Ajax.request({
	      url : basePath + 'common/lazyTree.action?_noc=1',
	      params: {
	    	parentId: pid,
	    	condition: "sn_using=1 and sn_isleaf='F'"
	      },
	      callback : function(options,success,response){
	        tree.setLoading(false);
	    	var res = new Ext.decode(response.responseText);
	    	if(res.tree){
	    		if(record) {
	    			record.appendChild(res.tree);
	    			record.expand(false,true);//展开
	    			tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
	    		} else {
	    			tree.store.setRootNode({
		    		    text: 'root',
		    			id: 'root',
		    			expanded: true,
		    			children: res.tree
		    		});
		    	}
		    } else if(res.exceptionInfo){
		    	showError(res.exceptionInfo);
		    }
		  }
	   });
	},
    openSyanavigationDetail: function(view, rowIndex, colIndex, column, e){//全功能导航
    	var record = view.getRecord(view.findTargetByEvent(e)),
		    	id=record.get('id'),title = record.get('text');
    	var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
    	height = Ext.isIE ? screen.height*0.75 : '95%';
    	var win =new Ext.window.Window({
		   		title: title+'说明',
		   		height: height,
		   		width: width,
		   		resizable:false,
		   		modal: true,
		   		layout: 'anchor',
			   	items: [{
					tag: 'iframe',
					frame: true,
					border:false,
					anchor: '100% 100%',
					layout: 'fit',
					html : '<iframe src="' + basePath + 'jsps/common/navigationDetails.jsp?id='+id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	    		}]
    	});
		win.show();
/*		var record = view.getRecord(view.findTargetByEvent(e)),
		    	id=record.get('id'),title = record.get('text');
		var panel = Ext.getCmp('SyanavigationDetail'+record.get('id'));
		if(!panel){
            var url = 'jsps/common/navigationDetails.jsp?id='+id;
            panel = {
                title: record.get('text').length > 5 ? (record.get('text').substring(0, 5) + '..') : record.get('text'),
                tag: 'iframe',
                tabConfig:{tooltip: title},
                border: false,
                frame: true,
                layout: 'fit',
                iconCls: record.data.iconCls,
                html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    			closable: true,
                listeners: {
                    close: function() {
                        var main = Ext.getCmp("content-panel");
                        main.setActiveTab(Ext.getCmp("HomePage"));
                    }
                }
            };
            this.openTab(panel, 'SyanavigationDetail'+record.get('id'), url);
       }else {
			var main = Ext.getCmp("content-panel");
			main.setActiveTab(panel);
		}*/
    },
    loadNavigation:function(selModel, record){
    	var me = this;
        if (!record.get('leaf')) {
            if (record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
                record.collapse(true, true); //收拢
                me.naviFlag = true;
            } else { //未展开
                //看是否加载了其children
                if (record.childNodes.length == 0) {
                    //从后台加载
                    var tree = Ext.getCmp('navigation-panel');
                    var condition = tree.baseCondition;
                    tree.setLoading(true, tree.body);
                    Ext.Ajax.request({ //拿到tree数据
                        url: basePath + 'common/getAllNavigation.action',
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
                                    if (!n.leaf) {
                                        n.level = record.get('level') + 1;
                                        n.iconCls = 'x-tree-icon-level-' + n.level;
                                    }
                                });
                                record.appendChild(res.tree);
                                record.expand(false, true); //展开
                                me.naviFlag = true;
                            } else if (res.exceptionInfo) {
                                showError(res.exceptionInfo);
                                me.naviFlag = true;
                            }
                        }
                    });
                } else {
                    record.expand(false, true); //展开
                    me.naviFlag = true;
                }
            }
        }else{
        	var id=record.get('id'),title = record.get('text');
        	/*var record = view.getRecord(view.findTargetByEvent(e)),
		    	id=record.get('id'),title = record.get('text');*/
		    	var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
		    	height = Ext.isIE ? screen.height*0.75 : '95%';
		    	var win =new Ext.window.Window({
			   		title: title+'说明',
			   		height: height,
			   		width: width,
			   		resizable:false,
			   		modal: true,
			   		layout: 'anchor',
				   	items: [{
						tag: 'iframe',
						frame: true,
						border:false,
						anchor: '100% 100%',
						layout: 'fit',
						html : '<iframe src="' + basePath + 'jsps/common/navigationDetails.jsp?id='+id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
		    		}]
		    	});
				win.show();
        	/*
        	var id=record.get('id'),title = record.get('text');
			var panel = Ext.getCmp('SyanavigationDetail'+record.get('id'));
			if(!panel){
        	    var url = 'jsps/common/navigationDetails.jsp?id='+id;
         	    panel = {
	                title: record.get('text').length > 5 ? (record.get('text').substring(0, 5) + '..') : record.get('text'),
	                tag: 'iframe',
	                tabConfig:{tooltip: title},
	                border: false,
	                frame: true,
	                layout: 'fit',
	                iconCls: record.data.iconCls,
	                html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			closable: true,
	                listeners: {
	                    close: function() {
	                        var main = Ext.getCmp("content-panel");
	                        main.setActiveTab(Ext.getCmp("HomePage"));
	                    }
	                }
           	 };
           		 this.openTab(panel, 'SyanavigationDetail'+record.get('id'), url);
		     }else {
					var main = Ext.getCmp("content-panel");
					main.setActiveTab(panel);
			}
        */}
    },
    showSyanavigationActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        Ext.get(icons[0]).removeCls('x-hidden');
        if (!(record.get('updateflag')==0||(record.raw &&record.raw.updateflag==0))) {
             Ext.get(icons[1]).removeCls('x-hidden');
        }
        /*if (!record.get('leaf')) {
            Ext.each(icons, function(icon) {
               Ext.get(icon).removeCls('x-hidden');
            });
        }*/
    },
    hideSyanavigationActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        Ext.each(icons, function(icon) {
            Ext.get(icon).addCls('x-hidden');
        });
    },
    loadTab: function(selModel, record) {
        var me = this;
        //text 优软商城
    	/*if(record.data.text=="优软商城"){
			me.isTureMaster();
        }*/
        me.isTureMasterFlag = true;
		if(record.raw){
	    	if((record.raw.navtype&&record.raw.navtype=='B2C')){
	    		me.isTureMaster();
	      	}
	    }
        if (record.get('leaf') || record.get('url')) {
            switch (record.data['showMode']) {
                case 0: //0-选项卡模式
                    me.openCard(record);
                    break;
                case 1: //1-弹出框式               
                    me.openBox(record);
                    break;
                case 2: //2-空白页
                    me.openBlank(record);
                    break;
                case 3: //3-窗口模式
                    me.openWin(record);
                    break;
                default:
                	if(record.raw){ //商城导航弹出模式选择
                		if(record.raw.navtype&&record.raw.navtype=='B2C'){
                			if(me.isTureMasterFlag){
                				switch (record.raw.showMode){
	                   			 case 0: //0-选项卡模式
	   			                    me.openCard(record);
	   			                    break;
	   			                case 1: //1-弹出框式               
	   			                    me.openBox(record);
	   			                    break;
	   			                case 2: //2-空白页
	   			                    me.openBlank(record);
	   			                    break;
	   			                case 3: //3-窗口模式
	   			                    me.openWin(record);
	   			                    break;
	   			                case 4:	//4-全屏幕窗口模式
	   								me.openFrameWin(record);
	   								break;
	   							 default:
	   								 me.openCard(record);
	   							  	 break;
	                   			}
                			}
                		}else me.openCard(record);
                	}else me.openCard(record);
                    break;
            }
            var w = Ext.getCmp("content-panel").items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow;
			if(!(record.get('model') == 'commonuse')) {
            	me.setCommonUse(record);
            }
            me.flag = true;
        }
        if (!record.get('leaf') && !(record.raw && record.raw.queryMode=='CLOUD')) {
            if (record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
                record.collapse(true, true); //收拢
                me.flag = true;
            } else { //未展开
                //看是否加载了其children
                if (record.childNodes.length == 0) {
                	if(record.data['parentId'] == 'commonuse') { // 如果是常用模块的子级
                		return;
                	}
                	record.removeAll();
                    //从后台加载
                    var tree = Ext.getCmp('tree-panel');
                    var condition = tree.baseCondition;
                    tree.setLoading(true, tree.body);
                    if(record.get('id') == 'commonuse') { // 如果是"常用模块"
                    	Ext.Ajax.request({

	                        url: basePath + 'common/getCommonUseTree.action',
	                        method : 'get',
	                        callback: function(options, success, response) {
	                            tree.setLoading(false);
	                            if(!response) {
	                            	return;
	                            }
	                            var res = new Ext.decode(response.responseText);
	                            if (res.tree) {
	                                if (!record.get('level')) {
	                                    record.set('level', 0);
	                                }
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
	                                    if (n.showMode == 2) { //openBlank
	                                        n.text = "<a href='" + basePath + me.parseUrl(n.url) + "' target='_blank'>" + n.text + "</a>";
	                                    }
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
                    }
                } else {
                		record.expand(false, true); //展开
                    me.flag = true;
                }
            }
        }
        if(record.raw){
        	if((record.raw.navtype&&record.raw.navtype=='B2C')&&(!me.isTureMasterFlag)){
        		Ext.defer(function(){
        			record.collapse(true); //收拢
        		},500);
        	}
        }
    },
    
    openTab: function(panel, id, url) {
        var o = (typeof panel == "string" ? panel : id || panel.id);
        var main = Ext.getCmp("content-panel");
        var tab = main.getComponent(o);
        if (tab) {
            main.setActiveTab(tab);
        } else if (typeof panel != "string") {
            panel.id = o;
            var p = main.add(panel);
             //记录最后工作台
            if(contains(url, 'common/bench/bench.jsp', false)){
            	 lastBench = o;
            }
            main.setActiveTab(p);
        }
    },
    getMyNewEmails: function() {
        Ext.Ajax.request({
            url: basePath + "oa/mail/getNewMail.action",
            method: 'post',
            callback: function(options, success, response) {
                var res = new Ext.decode(response.responseText);
                if (res.exceptionInfo) {
                    showError(res.exceptionInfo);
                    return;
                }
            }
        });
    },
    setCommonUse:function(record){
    	var url = record.get('url'),
    		id = record.get('id'),
    		addUrl = record.get('addurl');
    		caller = record.get('caller');
    	if(!id || (id+'').indexOf(' ') != -1 || isNaN(id)) {
    		return;
    	}
		Ext.Ajax.request({
			url : basePath + 'common/setCommonUse.action',
			params: {
				id: id,
				url:url,
				addUrl: addUrl,
				count:15,
				caller:caller
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exception || res.exceptionInfo){
					showError(res.exceptionInfo);
					return;
				}
			}
		});
	},
    parseUrl: function(url) {
        var id = url.substring(url.lastIndexOf('?') + 1); //将作为新tab的id
        if (id == null) {
            id = url.substring(0, url.lastIndexOf('.'));
        }
        if (contains(url, 'session:em_uu', true)) { //对url中session值的处理
            url = url.replace(/session:em_uu/g, em_uu);
        }
        if (contains(url, 'session:em_code', true)) { //对url中em_code值的处理
            url = url.replace(/session:em_code/g, "'" + em_code + "'");
        }
        if (contains(url, 'sysdate', true)) { //对url中系统时间sysdate的处理
            url = url.replace(/sysdate/g, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
        }
        if (contains(url, 'session:em_name', true)) {
            url = url.replace(/session:em_name/g, "'" + em_name + "'");
        }
        if (contains(url, 'session:em_type', true)) {
            url = url.replace(/session:em_type/g, "'" + em_type + "'");
        }
        if (contains(url, 'session:em_id', true)) {
            url = url.replace(/session:em_id/g,em_id);
        }
        if (contains(url, 'session:em_depart', true)) {
            url = url.replace(/session:em_depart/g,em_id);
        }
        if (contains(url, 'session:em_defaulthsid', true)) {
            url = url.replace(/session:em_defaulthsid/g,em_defaulthsid);
        }
        return url;
    },
    openCard: function(record) {
        var me = this;
        var panel = Ext.getCmp(record.get('id'));
        if (!panel) {
        	var url="";
        	/**是否取云端导航配置*/
        	if(record.parentNode &&  record.parentNode.raw && record.parentNode.raw.queryMode=='CLOUD'){
        		url=record.raw.url;
        		if(url) url+=(url.indexOf("?")>0?'&_config=CLOUD':'?_config=CLOUD');
        	}else if(record.parentNode &&  record.parentNode.raw && record.parentNode.raw.queryMode=='SYSSETTING'){
        		url=record.raw.url;
        	}else url=record.data['url'];
            url = me.parseUrl(url); //解析url里的特殊描述
            var jspPath = getJspName(record.get('url')) + '';
            var tabType = jspPath.substring(jspPath.lastIndexOf('/')+1); // 根据界面类型（查询/列表/批处理/新增）添加图标样式
            panel = {
                title: record.get('text')/*.length > 5 ? (record.get('text').substring(0, 5) + '..') : record.get('text')*/,
                tag: 'iframe',
                tabConfig: {
                	cls: 'x-tab-'+tabType,
                    tooltip: record.get('qtip') || record.get('text')
                },
                border: false,
                frame: false,
                layout: 'fit',
                iconCls: record.data.iconCls + (tabType ? ' x-tab-icon-'+tabType : ''),
                html: '<iframe id="iframe_' + id + '" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
                closable: true,
                listeners: {
                   /* close: function() {
                        var main = Ext.getCmp("content-panel");
                        main.setActiveTab(Ext.getCmp("HomePage"));
                    }*/
                }
            };
            if (record.get('leaf')) {
            this.openTab(panel, record.get('id'), url);}
        } else {
            var main = Ext.getCmp("content-panel");
            main.setActiveTab(panel);
        }
    },
    openBox: function(record) {
        window.open(basePath + this.parseUrl(record.data['url']), record.get('qtip'), 'width=' + (window.screen.width - 10) +
            ',height=' + (window.screen.height * 0.87) + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
    },
    openBlank: function(record) {
        //window.open(basePath + this.parseUrl(record.data['url']));
    },
    openWin: function(record) {
        if (Ext.getCmp('twin_' + record.data['id'])) {
            Ext.getCmp('twin_' + record.data['id']).show();
        } else {
            new Ext.window.Window({
                id: 'twin_' + record.data['id'],
                title: record.get('qtip').length > 5 ? (record.get('qtip').substring(0, 5) + '..') : record.get('qtip'),
                height: "100%",
                width: "80%",
                maximizable: true,
                layout: 'anchor',
                items: [{
                    tag: 'iframe',
                    frame: true,
                    anchor: '100% 100%',
                    layout: 'fit',
                    html: '<iframe id="iframe_twin" src="' + basePath + this.parseUrl(record.data['url']) + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                }]
            }).show();
        }
    },
    lockPage: function() {
        var me = this;
        Ext.Ajax.request({
            url: basePath + "common/logout.action",
            method: 'post',
            callback: function(options, success, response) {
                var res = Ext.decode(response.responseText);
                if (res.success) {
                    //弹出解锁框
                    me.showLock();
                }
            }
        });
    },
    /**
     * 显示锁屏window
     */
    showLock: function() {
        var me = this;
        var panel = Ext.create('Ext.window.Window', {
            id: 'lock-win',
            frame: true,
            closable: false,
            modal: true,
            autoShow: true,
            title: '<div style="height:25;padding-top:5px;color:blue;background: #E0EEEE url(' + basePath + 'resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif) repeat center center">&nbsp;！您的屏幕已锁定</div>',
            bodyStyle: 'background: #E0EEEE',
            width: 360,
            height: 260,
            renderTo: Ext.getBody(),
            items: [{
                xtype: 'displayfield',
                height: 130,
                labelWidth: 128,
                labelSeparator: '',
                fieldStyle: 'color:#7D9EC0;font-size:15px;font-family:隶书;',
                fieldLabel: '<img src="' + basePath + 'resource/images/screens/locked.png" style="background: #E0EEEE;">',
                value: '如需解锁，请输入您的密码'
            }, {
                xtype: 'hidden',
                name: 'username',
                id: 'username',
                value: em_code
            }, {
                xtype: 'form',
                bodyStyle: 'background: #E0EEEE',
                layout: 'column',
                items: [{
                    xtype: 'textfield',
                    labelSeparator: '',
                    columnWidth: 0.8,
                    fieldLabel: '<img src="' + basePath + 'resource/images/screens/key.png" style="background: #E0EEEE;padding-left:15px;">',
                    labelWidth: 40,
                    fieldCls: 'x-form-field-cir',
                    id: 'password',
                    name: 'password',
                    inputType: 'password'
                }, {
                    xtype: 'button',
                    columnWidth: 0.2,
                    cls: 'x-btn-blue',
                    text: '解锁',
                    handler: function() {
                        me.removeLock();
                    }
                }]
            }]
        });
        panel.el.slideIn('b', {
            duration: 1000
        });
    },
    removeLock: function() {
        var win = Ext.getCmp('lock-win');
        if (win && win.down('#password').value != null) {
            Ext.Ajax.request({
                url: basePath + "common/login.action",
                params: {
                    username: win.down('#username').value,
                    password: win.down('#password').value,
                    language: language
                },
                method: 'post',
                callback: function(options, success, response) {
                    var res = Ext.decode(response.responseText);
                    if (res.success) {
                        //弹出解锁框
                        win.close();
                    } else {
                        if (res.reason) {
                            alert(res.reason);
                            win.down('#password').setValue('');
                            win.down('#password').focus();
                        }
                    }
                }
            });
        }
    },
    /**
     * 右下角小消息提示
     * @param title 标题
     * @param fromId 发送人Id
     * @param from 发送人
     * @param date 日期
     * @param context 正文
     * @param url 消息链接
     * @param msgId 消息ID
     */
    showMsgTip: function(title, prId, fromId, from, date, context, url, msgId, master) {
        var me = this;
        var panel = Ext.getCmp('msg-win-' + prId);
        if (!panel) {
            panel = Ext.create('erp.view.core.window.MsgTip', {
                title: title,
                from: from,
                date: date,
                url: url,
                msgId: msgId,
                prId: prId,
                height: 120, //提示信息显示不全
                context: context,
                listeners: {
                    close: function() {
                        me.updatePagingStatus(msgId, 1, master);
                    },
                    check: function() {
                        me.showDialogBox(msgId, fromId, from, date, context);
                    },
                    reply: function() {
                        me.showDialogBox(msgId, fromId, from, date, context);
                    }
                }
            });
        }
    },
    transImages: function(msg) {
        msg = msg.toString();
        var faces = msg.match(/&f\d+;/g);
        Ext.each(faces, function(f) { //表情
            msg = msg.replace(f, '<img src="' + basePath + 'resource/images/face/' + f.substr(2).replace(';', '') + '.gif">');
        });
        var images = msg.match(/&img\d+;/g);
        Ext.each(images, function(m) { //图片
            msg = msg.replace(m, '');
        });
        return msg;
    },
    /**
     * 对话框
     * @param id 消息的主键值
     * @param otherId 对方人员ID
     * @param other 对方人名
     * @param date 时间
     * @param context 对话内容 
     */
    showDialogBox: function(id, otherId, other, date, context) {
        var me = this;
        var panel = Ext.getCmp('dialog-win-' + otherId);
        if (!panel) {
            panel = Ext.create('erp.view.core.window.DialogBox', {
                other: other,
                otherId: otherId
            });
        }
        if (!Ext.isEmpty(id)) {
            panel.insertDialogItem(other, date, context);
            if (Ext.getCmp('dialog-min-' + otherId)) {
                Ext.getCmp('dialog-min-' + otherId).setText("<font color=red>有新消息...</font>");
            } else {
                me.updatePagingStatus(id, 1);
            }
        }
    },
    /**
     * 循环刷新寻呼信息
     * @param cycletime 间隔时间 {快速4000(聊天过程中)、中等8000(普通模式)、慢速15000(session中断等异常情况下)}
     */
    //20170302 修改了刷新时间为20秒一次。
    loadPagingRelease: function(timestr,sync) {
        var me = this;
        me.cycletime = me.cycletime || 20000;
        if (!Ext.getCmp('lock-win')) {
            try {
                //me.getPagingRelease();
            	me.getInfoCount(me.timestr,sync);
                Ext.getCmp('process-lazy').setText('');
            } catch (e) {
                //需要try catch一下，不然，循环会因出现的异常而中断。网络中断后，如果不刷新主页，而是直接重新登录的话，就不会继续循环刷新寻呼
                me._showerr(e);
            }
        }
        setTimeout(function() {
            if (me.allowMsg)
                me.loadPagingRelease(me.timestr,true);
        }, me.cycletime);
    },
    /**
     * 循环刷新首页
     * */
    refreshDesk: function() {
        var me = this;
        var win= Ext.getCmp("content-panel").items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow;
        if(win && win.Ext){
			var desk=win.Ext.ComponentQuery.query('deskportal');
			if(desk.length>0)  desk[0].fireEvent('datarefresh',desk[0],'autoRefresh');
		}
        setTimeout(function() {
            me.refreshDesk();
        }, 120000);
    },
    _showerr: function(e) {
        var me = this;
        if (e.code == 101 || e.message == 'NETWORK_ERR' || e.message == 'NETWORK_LAZY' || e.message == 'NETWORK_LOCK' || e.message == 'NETWORK_KICK') { //NETWORK_ERR
            me.cycletime = 20000;
            if (e.message == 'NETWORK_ERR') {
                showLoginDiv(true);
                Ext.getCmp('process-lazy').setText('服务器连接中断，服务器可能已关闭或在重启，尝试连接中...');
            } else if (e.message == 'NETWORK_LAZY') {
                Ext.getCmp('process-lazy').setText('请求超时8000ms，服务器负荷过大或网络延迟，请暂缓操作，尝试恢复中...');
            } else if (e.message == 'NETWORK_LOCK') { //账号被强制锁定
                Ext.Ajax.request({
                    url: basePath + "common/logout.action",
                    method: 'post',
                    callback: function(options, success, response) {
                    	alert('您已被管理员强制注销,请重新登录!');
                        window.location.reload();
                    }
                });
            } else if (e.message == 'NETWORK_KICK') {//账号被踢
           		Ext.Ajax.request({
					url: basePath + "common/logout.action",
					method: 'post',
                    callback: function(options, success, response) {
                    	alert('您的账号在另外一台PC端已登入！');
                        window.location.reload();
                    }
           		});
            } 
        }
    },
     //获取未推送的消息的数目
    getInfoCount:function(timestr,sync){
        var me = this,
             t1 = new Date().getTime();
        Ext.Ajax.request({
            url: basePath + 'common/getInfoCount.action',
            method: 'get',
            params:{
            	timestr:timestr
            },
            async:true,
            timeout: 8000,
            callback: function(options, success, response) {
                var e = null;
                if (success == false) {
                    var lazy = new Date().getTime() - t1;
                    if (lazy > 7500) { // 表示超时引起
                        e = new Error("NETWORK_LAZY");
                    } else { // 表示服务器连接中断引起
                        e = new Error("NETWORK_ERR");
                    }
                } else {
                    me.cycletime = 20000;
                }
                var localJson = new Ext.decode(response.responseText, true);
                if (localJson.exceptionInfo) {
                    var info = localJson.exceptionInfo;
                    if (info == 'ERR_NETWORK_SESSIONOUT') {
                        e = new Error("NETWORK_ERR");
                    } else if (info == 'ERR_NETWORK_LOCKED') {
                        e = new Error("NETWORK_LOCK");
                    } else if (info == 'ERR_NETWORK_KICKED'){
                    	e = new Error("NETWORK_KICK")
                    } else {
                        showMessage('警告', info);
                    }
                }
                if (e != null) {
                    me._showerr(e);
                    return;
                }           
                if (localJson.success) {
                	var messageredpoint=document.getElementById("messageredpoint");
                	if(localJson.data.message.allmessagecount>0){
                		messageredpoint.style.display = 'block';
                	}else{
                		messageredpoint.style.display = 'none';
                	}
                	if(me.isMinStatus()&&localJson.DtRemaind&&localJson.IsRemind&&(localJson.data.message.messagecount>0||localJson.data.process.processcount>0||localJson.data.task.taskcount>0)){
                		me.allowMsg = true; 
                		if(window.Notification && Notification.permission !== "denied") {						
							Notification.requestPermission(function(status) {    // 请求权限
            					if(status === 'granted') {
                					// 弹出一个通知
                					var n = new Notification('消息通知:', {
                		   				 body : '您有来源于'+localJson.ma_user+'的新消息',
                    					 icon : (window.basePath || '') + 'resource/images/uaslogo.png'
               			 				});
		              				 // 两秒后关闭通知
					                setTimeout(function() {
					                    n.close();
					                }, 3000);
					               n.onclick = function() {
					               		window.open(window.basePath);
        								n.close();    
   									 };
          					  }
       					 });
					}
                	}else{
                		if(localJson.IsRemind){
                		me.allowMsg = true;                     
                        var data=localJson.data;  
                        me.timestr = data.time;
    					if (data.message.messagecount>0||data.process.processcount>0||data.task.taskcount>0) {    					
    						var main=Ext.getCmp("content-panel");
    						var activetitle=main.getActiveTab().title;  					
    							var messageinfo=Ext.getCmp('messageinfo'+data.time);
    							if(!messageinfo){
    								 var i=Ext.create('erp.view.core.window.MessageInfo',{    								 	
    								 	width: 330,
    								 	height:170,
										timeid:data.time
    								 });
    								 var messageinfo=Ext.getCmp('messageinfo'+data.time);
    								 
    								if(data.process.processcount>0){
    									if(activetitle=='流程中心'){
    										var iframe = window.frames['iframe_detail_process']||window.frames['iframe_ext-window'];    						
    										var w = iframe.contentWindow||iframe.window;
    										if (w.Ext) {
												//childpanel = w.Ext.ComponentQuery.query('displayfield')[3];
    											childpanel=w.Ext.getCmp('msgNotice');
    										}  
    							
    										if(childpanel.hidden){
													childpanel.hidden = false;
													childpanel.el.slideIn('t', { duration: 2000 });
												} 						
			    							
    										
    									}else{
    										Ext.Array.each(main.items.items,function(i){
    											if(i.title=="流程中心"){
    												toggleclass(i);
    											}
    										
    										});
    										
    										messageinfo.add({
    									
    										xtype:'displayfield',
    										value: '',
    										style:{
    											'margin-bottom':'0px'
    										},
    										renderer: function(val, meta, record){
    											return '<div style="margin-left:10px;font-size:14px"><div onclick="processclick()" style="cursor: pointer;">审批<span  style="color:red;">['+data.process.processcount+']</span></div>' +
    													'<div style="margin-top:2px;color:#777777;height:19px;max-width:295px;width:auto;white-space:nowrap;text-overflow:ellipsis; -o-text-overflow:ellipsis;overflow: hidden;">'+data.process.processdata+'</div>' +
    													'<div style=" width:295px;border-bottom:1px solid #C9C9C9"></div></div>'
    										}    										
    									});	
    									}   									
    								}
    								if(data.task.taskcount>0){
    									
    									if(activetitle=='任务中心'){
    										
			    							//这里写任务中心的xxx    							
			    							var iframe = window.frames['iframe_detail_task']||window.frames['iframe_ext-window'];    						
			    							var w = iframe.contentWindow||iframe.window;
			    							if (w.Ext) {
												//childpanel = w.Ext.ComponentQuery.query('displayfield')[3];
			    								childpanel=w.Ext.getCmp('msgNotice');
			    							}  			    							
			    							if(childpanel.hidden){
													childpanel.hidden = false;
													childpanel.el.slideIn('t', { duration: 2000 });
												}   							
    						
    									}else{
    										Ext.Array.each(main.items.items,function(i){
    											if(i.title=="任务中心"){
    												toggleclass(i);
    											}
    										
    										});
    										messageinfo.add({
    										xtype:'displayfield',
    										value: '',
    										style:{
    											'margin-bottom':'0px'
    										},
    										renderer: function(val, meta, record){    									
	    										  return '<div style="margin-left:10px;font-size:14px"><div onclick="taskclick()" style="cursor: pointer;">任务<span  style="color:red;">['+data.task.taskcount+']</span></div>' +
    													'<div style="margin-top:2px;color:#777777;height:19px;max-width:295px;width:auto;white-space:nowrap;text-overflow:ellipsis; -o-text-overflow:ellipsis;overflow: hidden;">'+data.task.taskdata+'</div>' +
    													'<div style=" width:295px;border-bottom:1px solid #C9C9C9"></div></div>'
    										
    										}
    									});	
    									}
    									
    								}
    								if(data.message.messagecount>0){
    									if(activetitle=='消息中心'){
    										var iframe = window.frames['iframe_detail']||window.frames['iframe_ext-window'];    						
			    							var w = iframe.contentWindow||iframe.window;
			    							if (w.Ext) {
												childpanel = w.Ext.getCmp('msgNotice');
												if(childpanel.hidden){
													childpanel.hidden = false;
													childpanel.el.slideIn('t', { duration: 2000 });
												}
			    							}  	
    						
    									}else{
    										Ext.Array.each(main.items.items,function(i){
    											if(i.title=="消息中心"){
    												toggleclass(i);
    											}
    										
    										});
    										messageinfo.add({   					
    										xtype:'displayfield',
    										value: '',
    										renderer: function(val, meta, record){   							
    										return  '<div style="margin-left:10px;font-size:14px"><div onclick="messageclick()" style="cursor: pointer;">消息<span  style="color:red;">['+data.message.messagecount+']</span></div>' +
    													'<div style="margin-top:2px;color:#777777;height:19px;max-width:295px;width:auto;white-space:nowrap;text-overflow:ellipsis; -o-text-overflow:ellipsis;overflow: hidden;">'+data.message.messagedata+'</div>' +
    													'<div style=" width:295px;border-bottom:1px solid #C9C9C9"></div></div>'
    											
    										}
    									});
    									}
    								
    								}
    								if(i.items.length>0){
    									if(i.el) {
    										i.el.slideIn('b', { duration: 1000 });
    									}
										Ext.defer(function(){
											if(i.el) {
												i.el.slideOut('b',{duration: 1000 });
											}
										},10000);
    									
    								}
    								
    								
    							}

    						
    					}
                }
                		}
                     
            }
            }
        });
    },
  isMinStatus:function() { 
	var isMin = false;
	if (window.outerWidth != undefined) { 
		isMin = window.outerWidth <= 160 && window.outerHeight <= 28; 
	} 
	else { 
		isMin = window.screenTop < -30000 && window.screenLeft < -30000; 
	} 
	return isMin; 
},  
    
    /**
     * 获取未读网络寻呼
     */
    getPagingRelease: function() {
        var me = this,
            t1 = new Date().getTime();
        Ext.Ajax.request({
            url: basePath + 'oa/info/getPagingRelease.action',
            method: 'get',
            timeout: 8000,
            callback: function(options, success, response) {
                var e = null;
                if (success == false) {
                    var lazy = new Date().getTime() - t1;
                    if (lazy > 7500) { // 表示超时引起
                        e = new Error("NETWORK_LAZY");
                    } else { // 表示服务器连接中断引起
                        e = new Error("NETWORK_ERR");
                    }
                } else {
                    me.cycletime = 20000;
                }
                var localJson = new Ext.decode(response.responseText, true);
                if (localJson.exceptionInfo) {
                    var info = localJson.exceptionInfo;
                    if (info == 'ERR_NETWORK_SESSIONOUT') {
                        e = new Error("NETWORK_ERR");
                    } else if (info == 'ERR_NETWORK_LOCKED') {
                        e = new Error("NETWORK_LOCK");
                    } else {
                        showMessage('警告', info);
                    }
                }
                if (e != null) {
                    me._showerr(e);
                    return;
                }
                if (localJson.success) {
                    if (localJson.IsRemind) {
                        me.allowMsg = true;
                        var data = Ext.decode(localJson.data);
    					if (data != null && data.length > 0) {
    						Ext.each(data, function(d) {
    							if(d.pr_istop){
                                   me.showTopWin(d);
    							}
    							else{ if (Ext.getCmp('dialog-win-' + d.pr_releaserid)) {
    								me.showDialogBox(d.prd_id, d.pr_releaserid, d.pr_releaser, Ext.Date.format(Ext.Date.parse(d.pr_date, 'Y-m-d H:i:s'), 'Y-m-d H:i:s'), d.pr_context, d.currentMaster);
    							} else {
    								me.showMsgTip('您收到了新的寻呼', d.pr_id, d.pr_releaserid, d.pr_releaser, Ext.Date.format(Ext.Date.parse(d.pr_date, 'Y-m-d H:i:s'), 'Y-m-d H:i:s'),
    										d.pr_context, 'jsps/oa/info/pagingDetail.jsp?formCondition=prd_idIS' + d.prd_id, d.prd_id, d.currentMaster);
    							}
    							}
    						});
    					}
                    } else {
                        me.allowMsg = false; // 设置为不允许弹消息窗
                    }
                }
            }
        });
    },
    /**
     * 消息框置顶
     * */
    showTopWin:function(d){
    	var iWidth=800,iHeight=550;			        	
    	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
    	var iLeft = (window.screen.availWidth-10-iWidth)/2; //
    	window.open(basePath+'oa/info/receive.action?id='+d.prd_id,'_blank','width='+iWidth+',height='+iHeight+',top='+iTop+',left='+iLeft+',target=_top,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
    },
    /**
     * @param id 明细ID
     * @param status 待修改状态
     */
    updatePagingStatus: function(id, status, master) {
        Ext.Ajax.request({
            url: basePath + 'oa/info/updateStatus.action',
            params: {
                id: id,
                status: status,
                master: master
            },
            method: 'post',
            callback: function(options, success, response) {
                var localJson = new Ext.decode(response.responseText);
                if (localJson.exceptionInfo) {
                    showError(localJson.exceptionInfo);
                    return null;
                }
            }
        });
    },
    /**
     * 通讯录
     */
    showAddrBook: function() {
        if (!Ext.getCmp('addrbook-win')) {
            Ext.create('Ext.window.Window', {
                id: 'addrbook-win',
                title: '我的通讯录',
                height: screen.height * 0.8,
                width: screen.width * 0.2,
                renderTo: Ext.getBody(),
                animCollapse: false,
                constrainHeader: true,
                bodyBorder: true,
                layout: 'accordion',
                border: false,
                autoShow: true,
                collapsible: true,
                x: screen.width * 0.8,
                items: [
                    Ext.create('erp.view.oa.addrBook.AddrBookTree', {
                        id: 'addr-tree',
                        title: '联系人'
                    }), {
                        title: '设置',
                        html: '<p>...</p>',
                        autoScroll: true
                    }, {
                        title: '我的应用',
                        html: '<p>...</p>'
                    }
                ]
            });
        } else {
            Ext.getCmp('addrbook-win').show();
        }
    },
    checkOnline: function(record, e) {
        Ext.Ajax.request({
            url: 'oa/info/checkOnline.action',
            params: {
                orgid: Number(record.data.data ? record.data.data.or_id : record.raw.data.or_id)
            },
            method: 'POST',
            callback: function(options, success, response) {
                var res = Ext.decode(response.responseText);
                var data = res.data;
                if (data) {
                    var count = 0;
                    var ems = Ext.Array.pluck(data, 'em_id');
                    Ext.each(record.childNodes, function(node) {
                        if (Ext.Array.contains(ems, Math.abs(node.data['id']))) {
                            node.set('iconCls', 'x-tree-icon-happy');
                            node.set('cls', 'x-tree-cls-node-on');
                            var d = Ext.Array.filter(data, function(dd) {
                                return dd.em_id == Math.abs(node.get('id'));
                            })[0];
                            node.set('qtip', '<table>' +
                                '<tr><td>编号:</td><td>' + d.em_code + '</td></tr>' +
                                '<tr><td>姓名:</td><td>' + d.em_name + '</td></tr>' +
                                '<tr><td>IP:</td><td>' + d.ip + '</td></tr>' +
                                '<tr><td>时间:</td><td>' + Ext.Date.format(new Date(d.date), 'Y-m-d H:i:s') + '</td></tr>' +
                                '</table>');
                            count++;
                        } else {
                            node.set('iconCls', 'x-tree-icon-sad');
                            node.set('cls', 'x-tree-cls-node-off');
                        }
                    });
                    if (count > 0) {
                        if (!record.data.oriText) {
                            record.data.oriText = record.data.text;
                        }
                        record.expand(false, true);
                    }
                }
            }
        });
    },
    showPwdPanel: function() {
        Ext.create('erp.view.core.window.PwdWindow');
    },
    showActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        if (record.get('addurl') || (record.raw && record.raw.addurl)) {
            Ext.each(icons, function(icon) {
                Ext.get(icon).removeCls('x-hidden');
            });
        }
    },
    hideActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        Ext.each(icons, function(icon) {
            Ext.get(icon).addCls('x-hidden');
        });
    },
    handleAddClick: function(view, rowIndex, colIndex, column, e) {
    	window.event? window.event.cancelBubble = true : e.stopPropagation();
    	var me=this;
        var record = view.getRecord(view.findTargetByEvent(e)),
            title = record.get('text');
        if (record.get('addurl') ||  record.raw.addurl) {
            openTable(title, record.get('addurl')|| record.raw.addurl, record.get('caller'),false);
            if(!(record.get('model') == 'commonuse')) {
            	me.setCommonUse(record);
            }
        }
    },
    showImportDumpFileWin: function() {
    	var me = this, win = Ext.create('Ext.window.Window', {
    		title: '方案导入',
    		closeAction: 'destroy',
    		autoShow: true,
    		width: 400,
    		bodyPadding: 10,
    		items: [{
    			xtype: 'form',
    			bodyStyle: 'background:#e0e0e0',
    			items: [{
    				xtype: 'filefield',
    				name: 'file',
    		        buttonOnly: true,
    		        buttonText: '点击选择.json文件'
    			},{
    				xtype: 'displayfield',
    				name: 'expDate',
    				hidden: true,
    				fieldLabel: '导出日期'
    			},{
    				xtype: 'displayfield',
    				name: 'from',
    				hidden: true,
    				fieldLabel: '来源'
    			},{
    				xtype: 'displayfield',
    				name: 'type',
    				hidden: true,
    				fieldLabel: '类型'
    			},{
    				xtype: 'displayfield',
    				name: 'desc',
    				hidden: true,
    				fieldLabel: '描述'
    			}]
    		}],
    		buttonAlign: 'center',
    		buttons: [{
    			text: '确认导入',
    			itemId: 'confirm',
    			disabled: true,
    			handler: function() {
    				me.FormUtil.setLoading(true);
    				Ext.Ajax.request({
    					url: basePath + 'common/dump/imp.action',
    					params: {
    						jsonData: Ext.JSON.encode(win.dumpfile)
    					},
    					callback: function(opt, success, res) {
    						me.FormUtil.setLoading(false);
    						var rs = Ext.JSON.decode(res.responseText);
    						if(rs.success) {
    							showMessage('提示', '导入成功');
    							win.close();
    						} else if(rs.exceptionInfo) {
    							showError(rs.exceptionInfo);
    						}
    					}
    				});
    			}
    		},{
    			text: '取消',
    			handler: function(){
    				win.close();
    			}
    		}]
    	});
    	var confirmBtn = win.down('button[itemId=confirm]');
    	// 本地读取解析文件
    	win.down('filefield').fileInputEl.dom.addEventListener('change', function(e){
    		e = e || window.event;
    	    var reader = new FileReader(), file = this.files[0];
    	    if(!/^.+\.json$/.test(file.name)) {
    	    	showError('文件格式错误');
    	    	confirmBtn.setDisabled(true);
    	    	return;
    	    }
	        reader.onload = (function(file) {
	            return function(e) {
	            	try {
	            		var dump = Ext.JSON.decode(this.result), form = win.down('form').getForm();
	            		form.setValues(dump);
	            		form.getFields().each(function(){
	            			this.show();
	            		});
	            		win.dumpfile = dump;
	            		confirmBtn.setDisabled(false);
	            	} catch (err) {
	            		showError('文件错误，无法解析');
	            		confirmBtn.setDisabled(true);
	            	}
	            };
	        })(file);
	        //读取文件内容
	        reader.readAsText(file);
    	}, false);
    },openFrameWin: function(record){
		var me = this;
		if(record.data['id'] == '2017000000') {
			me.getWelcomeStatus(record);
		} else {
			if(Ext.getCmp('twin_' + record.data['id'])) {
				Ext.getCmp('twin_' + record.data['id']).show();
			} else {
				new Ext.window.Window({
					id: 'twin_' + record.data['id'],
					title: record.get('qtip').length > 5 ? (record.get('qtip').substring(0, 5) + '..') : record.get('qtip'),
					height: "100%",
					width: "100%",
					maximizable: true,
					layout: 'anchor',
					items: [{
						tag: 'iframe',
						frame: true,
						anchor: '100% 100%',
						layout: 'fit',
						html: '<iframe id="iframe_twin" src="' + basePath + me.parseUrl(record.raw.url) +
							'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
					}]
				}).show();
			}
		}
    },
    isTureMaster:function(){
    	var me = this;
    	Ext.Ajax.request({
            url: basePath + "b2b/main/isTureMaster.action",
            method: 'get',
            async:false,
            callback: function(options, success, response) {
                var res = new Ext.decode(response.responseText);
                if(res.success){
                	me.isTureMasterFlag = true;
                }else if(res.log){
                	me.isTureMasterFlag =false;
                	showError(res.log);
                }
            }
    	});
    },getWelcomeStatus:function(record){
    	var me = this;
    	Ext.Ajax.request({
            url: basePath + "b2b/main/getWelcomeStatus.action",
            method: 'get',
            callback: function(options, success, response) {
                var res = new Ext.decode(response.responseText);
                if(res.success){
					if(Ext.getCmp('twin_' + record.data['id'])) {
						Ext.getCmp('twin_' + record.data['id']).show();
					} else {
						new Ext.window.Window({
							id: 'twin_' + record.data['id'],
							title: record.get('qtip').length > 5 ? (record.get('qtip').substring(0, 5) + '..') : record.get('qtip'),
							height: "100%",
							width: "100%",
							maximizable: true,
							layout: 'anchor',
							controllerme: me,
							treerecord: record,
							items: [{
								tag: 'iframe',
								frame: true,
								anchor: '100% 100%',
								layout: 'fit',
								html: '<iframe id="iframe_twin" src="' + basePath + me.parseUrl(record.raw.url) +
									'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
							}],
							listeners: {
								'beforeclose': function() {
									//关闭之前打开优软商城首页
									if(this.treerecord) {
										var welcomeurl = this.treerecord.raw.url.substring(this.treerecord.raw.url.indexOf('?url='));
											this.treerecord.raw.url = 'jsps/b2b/main/main.jsp'+welcomeurl;
											this.treerecord.raw.showMode = 0;
											this.controllerme.openCard(this.treerecord);
											this.controllerme.setWelcomeStatus(this.treerecord.raw.url);//记录登录状态
									}
								}
							}
						}).show();
					}
                }else if (!res.success){
	                if(record) {
	                	var welcomeurl = record.raw.url.substring(record.raw.url.indexOf('?url='));
						record.raw.url = 'jsps/b2b/main/main.jsp'+welcomeurl;
						record.raw.showMode = 0;
						me.openCard(record);
					}
                }
                if (res.exceptionInfo){
                	showError(res.exceptionInfo);
                }
            }
        });
    },setWelcomeStatus:function(url){
    	Ext.Ajax.request({
			url : basePath + 'b2b/main/setWelcomeStatus.action',
			params: {
				url:url
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exception || res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
		});
    }
});

var processclick=function(){  
  	var main=Ext.getCmp("content-panel");
	var panel=Ext.getCmp('jprocesscenter');
	if(!panel){
		var url=basePath+'jsps/common/messageCenter/JProcessCenter.jsp',
    	panel = { 
    			title : '流程中心',
    			id:'jprocesscenter',
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',    	
    			items: {xtype: 'component',
						id:'iframe_detail_process',									
						autoEl: {
								tag: 'iframe',
								style: 'height: 100%; width: 100%; border: none;',
								src: url}
    				},
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		var p = main.add(panel); 
		main.setActiveTab(p);
	}else{ 
    	main.setActiveTab(panel); 
	} 

  };
  var taskclick=function(){
  	var main=Ext.getCmp("content-panel");
	var panel=Ext.getCmp('taskcenter');
	if(!panel){
		var url=basePath+'jsps/common/messageCenter/TaskCenter.jsp',
    	panel = { 
    			title : '任务中心',
    			id:'taskcenter',
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',    	
    			items: {xtype: 'component',
						id:'iframe_detail_task',									
						autoEl: {
								tag: 'iframe',
								style: 'height: 100%; width: 100%; border: none;',
								src: url}
    				},
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		var p = main.add(panel); 
		main.setActiveTab(p);
	}else{ 
    	main.setActiveTab(panel); 
	} 
  	
  };
  var messageclick=function(){  
  	var main=Ext.getCmp("content-panel");
	var panel=Ext.getCmp('informations');
	if(!panel){
		var url=basePath+'jsps/common/messageCenter/information.jsp',
    	panel = { 
    			title : '消息中心',
    			id:'informations',
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',   	
    			items: {xtype: 'component',
						id:'iframe_detail',   					
						autoEl: {
								tag: 'iframe',
								style: 'height: 100%; width: 100%; border: none;',
								src: url}
    				},
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		var p = main.add(panel); 
		main.setActiveTab(p);
	}else{ 
    	main.setActiveTab(panel); 
	} 

  };
  var toggleclass=function(i){
  		i.tab.el.dom.classList.add("text");
  		setTimeout(function() {i.tab.el.dom.classList.remove('text')}, 30000);
  };
 
  
  