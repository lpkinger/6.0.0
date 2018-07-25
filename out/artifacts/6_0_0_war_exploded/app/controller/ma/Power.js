Ext.QuickTips.init();
Ext.define('erp.controller.ma.Power', {
	extend : 'Ext.app.Controller',
	requires: ['erp.util.FormUtil','erp.view.core.grid.HeaderFilter','erp.view.core.plugin.CopyPasteMenu'],
	views : [ 'ma.Power', 'core.button.Distribute', 'core.grid.GroupPower', 'core.trigger.DbfindTrigger',
			'core.trigger.EmpTrigger', 'core.button.Sync', 'core.trigger.MultiDbfindTrigger','core.trigger.SearchField','core.grid.YnColumn'],
	init : function() {
		var me = this;
		me.FormUtil = Ext.create('erp.util.FormUtil');
		me.BaseUtil=Ext.create('erp.util.BaseUtil');
		this.control({
			'treepanel[id=powertree]' : {
				afterrender : function(tree) {
					me.loadTree(tree);
				},
				itemmousedown : function(selModel, record) {					
					var tree = selModel.ownerCt;
					me.loadTree(tree, record);
				},
				scrollershow: function(scroller) {
					if (scroller && scroller.scrollEl) {
						scroller.clearManagedListeners();
						scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);
					}
				}
			},
			'#power_role':{ 
				change:function(checkbox, newValue, oldValue, eOpts ){
					_role = newValue;  //全局变量_role表示是否勾选按角色授权
					var job_combo = Ext.getCmp('job_combo');
					var role_combo = Ext.getCmp('role_combo');
					var bol = job_combo.isVisible()&&newValue; //搜索角色可见并且勾选，就隐藏
					var tree = Ext.getCmp('powertree');
					var grid = Ext.getCmp('grid');
					var treeCurRec = tree.getSelectionModel().getSelection()[0];
					
					job_combo.setVisible(!bol);
					role_combo.setVisible(bol);
					
					grid.store.clearFilter(true);
					
					if (treeCurRec&&treeCurRec.get('leaf')) {
						me.loadPower(treeCurRec,true);
					}else if(treeCurRec&&!treeCurRec.get('leaf')){
						if(grid.pp_caller){
							if(_role){
								grid.getRolePowerData(grid.urlType,true);
							}else{
								grid.getGroupData(grid.urlType,true);
							}
						}
					}else{
						if(_role){
							grid.reconfigure(null, grid.roleDefaultColumns);
						}else{
							grid.reconfigure(null, grid.defaultColumns);
						}
						grid.resetHeaderChecker();
	        			grid.rememberLastFilter();
	        			grid.plugins[0].renderFilters();
					}
					
					//复制权限按钮和个人权限按钮disable
					Ext.getCmp('power_copy').setDisabled(bol);
					Ext.getCmp('personal_set').setDisabled(bol);
				}
			},
			'button[id=personal_set]' : {// 个人权限设置
				click : function(btn) {
					var grid = btn.ownerCt.ownerCt;
					if (grid.pp_caller) {
						me.showPersonal(grid);
					}
				}
			},
			'button[id=special_set]': {// 设置特殊权限
				click: function(btn) {
					var grid = btn.ownerCt.ownerCt;
					if (grid.pp_caller) {
						if(grid.urlType == 'bench'){
							grid.showScenBtn();
						}else{
							me.showSpecial(grid);
						}
					}
				}
			},
			'button[id=power_copy]': {// 复制权限
				click: function(btn) {
					me.showCopyPane();
				}
			},
			'button[id=power_sync]': {// 同步权限
				click: function(btn) {
					this.showSyncWin();
				}
			},
			'button[id=power_cover]': {// 同步所有权限
    			beforerender:function(btn){
    				btn.sync=function() {
        				var masters = btn.getCheckData(), me = btn,w = this.win, 
    					datas = null, cal='PowerCover';
        				Ext.MessageBox.confirm('提示', '确认将当前帐套所有岗位权限、个人权限、特殊权限，覆盖到目标帐套？', function(but) {  
                           if(but=='yes'){
               				if (!Ext.isEmpty(masters)) {
               					w.setLoading(true);
               					Ext.Ajax.request({
               						url: basePath + 'hr/employee/vastPostPower.action',
               						params: {
               							caller: cal,
               							to: masters
               						},
               						callback: function(opt, s, r) {
               							w.setLoading(false);
               							if(s) {
               								var rs = Ext.decode(r.responseText);
               								if(rs.data) {
               									showMessage('提示', rs.data);
               								} else {
               									alert('权限覆盖成功!');
               								}
               			   					w.hide();
               			   					if(me.autoClearCache) {
               			   						me.clearCache();
               			   					}
               			   					me.fireEvent('aftersync', me, cal, datas, masters);
               							}
               						}
               					});
               				}
                           } 
                    });     				
    			};
    			} ,
    			afterrender: function(btn){
    				if(em_type && em_type != 'admin'){
    					btn.hide();
    				}
    			}
			},
			'multidbfindtrigger[name=em_position]': {				
				 afterrender: function(f){
				 	f.ownerCt.down('radio[name=synctype1]').setValue(true);						
					f.ownerCt.down('multidbfindtrigger[name=em_position]').setDisabled(false);
							
					f.ownerCt.down('radio[name=synctype2]').setValue(false);						
					f.ownerCt.down('multidbfindtrigger[name=em_code]').setValue(null);
					f.ownerCt.down('multidbfindtrigger[name=em_code]').setDisabled(true);
						
					f.ownerCt.down('radio[name=synctype3]').setValue(false);						
					f.ownerCt.down('dbfindtrigger[name=sn_title]').setValue(null);
					f.ownerCt.down('dbfindtrigger[name=sn_title]').setDisabled(true);				 					 	
				 },

				aftertrigger: function(t, rs) {					
					if(rs.length > 0) {
						var m = t.ownerCt;
						Ext.Array.each(rs, function(r, i){
							if(i == 0) {
								t.jo_id = r.jo_id||r.get('jo_id');
								t.setValue(r.jo_name||r.get('jo_name'));		
							} else {
								m.insert(m.items.length - 4, {
			    					xtype: 'multidbfindtrigger',
			    					name: 'em_position',			    					
			    					fieldLabel: '岗位名称',
			    					jo_id: r.jo_id||r.get('jo_id'),
			    					value: r.jo_name||r.get('jo_name'),
			    					p: 2,
			    					editable: false,
			    					autoDbfind: false,
			    					clearable: true,
			    					onTrigger2Click:function(){
			    						m.remove(this);	
			    						me.getSyncDatas(m);
			    					},
			    					//重写onConfirm方法
									onConfirm: function(selectAll) {
										var trigger = this;
										if(trigger.multistore){
											var grid = this.win.down('gridpanel');
											if(selectAll&&grid.store.data.length>0){//按条件全选
												Ext.each(grid.store.data.items,function(item){
													grid.multiselected.push(item.data.value);
												});
											}
											grid.multiselected=Ext.Array.unique(grid.multiselected);    
											trigger.setValue(grid.multiselected.join(trigger.separator));
											this.win.close();
										} else {
											var win = trigger.win;
											var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
											findgrid.setMultiValues();
											var k = Ext.Object.getKeys(trigger.multiValue),cp;
											Ext.each(k, function(key){
												cp = Ext.getCmp(key);
												if(cp.setValue !== undefined)
													cp.setValue(trigger.multiValue[key]);
											});
											trigger.setValue(trigger.multiValue[trigger.name]);
											if(selectAll){
												trigger.getAllData1(trigger);
											}else{
												trigger.fireEvent('aftertrigger', trigger, trigger.multiRecords);
												this.win.close();
											}
										}
									},
									getAllData1: function(trigger) {
										var win = this.win, g = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
										g.setLoading(true);
										g.getAllData(function(datas){
											g.setLoading(false);
											trigger.fireEvent('aftertrigger', trigger, datas);
											win.close();
										});
									}
			    				});
							}													
						});
					} else {
						t.setValue(null);
						t.jo_id = null;
					}					
					me.getSyncDatas(t.ownerCt);
				}
			},
			'multidbfindtrigger[name=em_code]': {
				aftertrigger: function(t, rs) {
					if(rs.length > 0) {
						var m = t.ownerCt;
						Ext.Array.each(rs, function(r, i){
							if(i == 0) {
								t.em_id = r.em_id||r.get('em_id');
								t.setValue(r.em_name||r.get('em_name'));		
							} else {
								m.insert(m.items.length - 2, {
			    					xtype: 'multidbfindtrigger',
			    					name: 'em_code',			    					
			    					fieldLabel: '员工编号',
			    					em_id: r.em_id||r.get('em_id'),
			    					value: r.em_name||r.get('em_name'),
			    					p: 2,
			    					editable: false,
			    					autoDbfind: false,
			    					clearable: true,
			    					onTrigger2Click:function(){
			    						m.remove(this);	
			    						me.getSyncDatas2(m);
			    					},
			    					//重写onConfirm方法
									onConfirm: function(selectAll) {
										var trigger = this;
										if(trigger.multistore){
											var grid = this.win.down('gridpanel');
											if(selectAll&&grid.store.data.length>0){//按条件全选
												Ext.each(grid.store.data.items,function(item){
													grid.multiselected.push(item.data.value);
												});
											}
											grid.multiselected=Ext.Array.unique(grid.multiselected);    
											trigger.setValue(grid.multiselected.join(trigger.separator));
											this.win.close();
										} else {
											var win = trigger.win;
											var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
											findgrid.setMultiValues();
											var k = Ext.Object.getKeys(trigger.multiValue),cp;
											Ext.each(k, function(key){
												cp = Ext.getCmp(key);
												if(cp.setValue !== undefined)
													cp.setValue(trigger.multiValue[key]);
											});
											trigger.setValue(trigger.multiValue[trigger.name]);
											if(selectAll){
												trigger.getAllData1(trigger);
											}else{
												trigger.fireEvent('aftertrigger', trigger, trigger.multiRecords);
												this.win.close();
											}
										}
									},
									getAllData1: function(trigger) {
										var win = this.win, g = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
										g.setLoading(true);
										g.getAllData(function(datas){
											g.setLoading(false);
											trigger.fireEvent('aftertrigger', trigger, datas);
											win.close();
										});
									}
			    				});
							}													
						});
					} else {
						t.setValue(null);
						t.em_id = null;
					}					
					me.getSyncDatas2(t.ownerCt);
				}
			},
			'dbfindtrigger[name=sn_title]': {
		 		afterrender: function(f){
				   f.onTriggerClick = function(){    				   	
					   me.getModuleTree();
				   };
				   f.autoDbfind = false;
		 		},
		 		aftertrigger: function(t, rs) {					
		 			w=t.ownerCt;					
		 			w.down('erpSyncButton[itemId=sync]').syncdatas = rs;
		 			w.down('erpSyncButton[itemId=sync]').caller ='ModulePower!Post';										
		 		}
			},			
			//dbfind查询完之后马上就进行筛选
			'multidbfindtrigger[name=ro_name]': {
				change : function(t){
					var win = Ext.ComponentQuery.query('window');
					if(win.length>1){
						win = win[win.length-1];
						if(win){
							if(!win.hidden){
								Ext.getCmp('grid').plugins[0].applyFilters();
							}
						}
					}
				}
			},
			'treepanel[id=moduletree]': {
				itemmousedown: function(selModel, record){
				    var tree = selModel.ownerCt;	    				   
				    me.loadModuleTree(tree, record);
				}
		   }		
		});
	},	
	getModuleTree: function(){
	   	var w = Ext.create('Ext.Window',{
		   title: '查找',
		   height: "100%",
		   width: "80%",
		   maximizable : true,
		   buttonAlign : 'center',
		   layout : 'anchor',
		   items: [{
			   anchor: '100% 100%',
			   xtype: 'treepanel',
			   id : 'moduletree',
			   rootVisible: false,
			   useArrows: true,
			   store: Ext.create('Ext.data.TreeStore', {
				   root : {
					   text: 'root',
					   id: 'root',
					   expanded: true
				   }
			   })
		   }],
		   buttons : [{
			   text : '关  闭',
			   iconCls: 'x-button-icon-close',
			   cls: 'x-btn-gray',
			   handler : function(btn){
				   btn.ownerCt.ownerCt.close();
			   }
		   },{
			   text: '确定',
			   iconCls: 'x-button-icon-confirm',
			   cls: 'x-btn-gray',
			   handler: function(btn){	
				   var t = btn.ownerCt.ownerCt.down('treepanel');	    				  
				   var syncButton=Ext.getCmp('sync');
				   var trigger =Ext.getCmp('sn_title');	    					    				   
				   trigger.fireEvent('aftertrigger', trigger, t.stateId); 
				   if(!Ext.isEmpty(t.title)) {	    				   		
					   Ext.getCmp('sn_title').setValue(t.title);	    					   
				   }
				   btn.ownerCt.ownerCt.close();
			   }
		   }]
	   });
	   w.show();
	   this.loadModuleTree(w.down('treepanel'), null);
   	},
   	loadModuleTree: function(tree, record){
	   	var pid = 0;
	   	if(record) {
		   if (record.get('leaf')) {
				tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
				tree.stateId = record.data.id;
				return;
			} else {
				if (record.isExpanded() && record.childNodes.length > 0) {
					record.collapse(true, true);// 收拢
					return;
				} else {
					if (record.childNodes.length != 0) {
						record.expand(false, true);// 展开
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
			   condition: 'sn_using=1'
		   },
		   callback : function(options,success,response){
			   tree.setLoading(false);
			   var res = new Ext.decode(response.responseText);
			   if(res.tree){	    			   	
				   if(record) {	    				   		
					   record.appendChild(res.tree);
					   record.expand(false,true);//展开
					   tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));	    					 
					   tree.stateId=record.data.id;	    					 	    					   
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
	loadTree : function(tree, record) {
		var pid = 0, me = this,nodes = new Array();;
		if (record) {
			if (record.get('leaf')) {				
				me.loadPower(record);
				return;
			} else {				
				if (record.isExpanded() && record.childNodes.length > 0) {
					record.collapse(true, true);// 收拢
					return;
				} else {
					if (record.childNodes.length != 0) {
						record.expand(false, true);// 展开
						return;
					}
				}
			}
			pid = record.get('id');
		}
		tree.setLoading(true);
		if(pid==0||pid=='bench'){
			var isRoot = false;
			if(pid==0){
				isRoot = true;
			}
			Ext.Ajax.request({//拿到tree数据
	        	url : basePath + 'bench/ma/getBenchTree.action',
	        	params: {
	        		isRoot: isRoot
	        	},
	        	async:false,
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.tree){
	        			if(isRoot){
	        				nodes = res.tree;
	        			}else{
	        				tree.setLoading(false);
	        				if (record) {
								record.appendChild(res.tree);
								record.expand(false, true);// 展开
	        				}
	        			}
	        			
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        		}
	        	}
	        });
	        if(!isRoot)
	        return;
		}
		Ext.Ajax.request({
			url : basePath + 'ma/lazyTree.action',
			params : {
				parentId : pid,
				condition : 'sn_limit=1'
			},
			callback : function(options, success, response) {
				tree.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if (res.tree) {
					if (record) {
						record.appendChild(res.tree);
						record.expand(false, true);// 展开
					} else {
						res.tree = Ext.Array.merge(nodes,res.tree,res.tree);
						tree.store.setRootNode({
							text : 'root',
							id : 'root',
							expanded : true,
							children : res.tree
						});
					}
				} else if (res.exceptionInfo) {
					showError(res.exceptionInfo);
				}
			}
		});
	},
	loadPower : function(record,roleSelected) {
		var caller = record.get('caller'), set = Ext.getCmp('grid');
		var urlType;
		set.pp_caller = caller;
		set.down('tbtext').setText('<font color=gray>权限名:</font>' + record.get('text'));
		if(record.get('parentId')=='bench'){
			set.pp_caller = record.get('data').bc_code;
			set.getGroupData('bench');
		}
		if (!Ext.isEmpty(caller)) {
			urlType = this.getUrlType(record.get('url'));
			if(!_role){
				set.getGroupData(urlType,roleSelected);
			}else{
				set.getRolePowerData(urlType,roleSelected);
			}
		} else {
			set.store.removeAll();
		}
	},
	getUrlType : function(url) {
		if (contains(url, 'datalist.jsp') || contains(url, 'editDatalist.jsp') || contains(url, 'vastDatalist.jsp')
				|| contains(url, 'turnGoodsSend.jsp') || contains(url, 'turnEstimate.jsp')|| contains(url, 'projectprogress.jsp')
				|| contains(url, 'deallist.jsp')) {
			return 'list';
		} else if (contains(url, 'batchDeal.jsp') || contains(url, 'query.jsp') || contains(url, 'print.jsp')
				|| contains(url, 'batchPrint.jsp') || contains(url, 'gridPage.jsp')|| contains(url, 'redirect.action')) {
			return 'deal';
		}
		return null;
	},
	showPersonal : function(grid,find) {
		var me = this;
		var win = Ext.getCmp('emp-win');
		if(win&&(typeof(find)=='undefined'&&win.find)){
			win.destroy();
			win = null;
		}
		if (!win) {
			win = Ext.create('Ext.Window', {
				id : 'emp-win',
				width : 800,
				height : 600,
				title : '员工',
				modal : true,
				closeAction:'hide',
				layout: 'anchor',
				items : [ {
					xtype : 'gridpanel',
					anchor: '100% 100%',
					autoScroller:true,
					columnLines : true,
					plugins : [Ext.create(
							'erp.view.core.grid.HeaderFilter'											
					), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					selModel : Ext.create(
							'Ext.selection.CheckboxModel', {
								checkOnly : true,
								headerWidth : 30
							}),
					columns : [ {
						text : 'ID',
						dataIndex : 'em_id',
						hidden : true
					}, {
						text : '编号',
						dataIndex : 'em_code',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'em_code'}
					}, {
						text : '姓名',
						dataIndex : 'em_name',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'em_name'}
					}, {
						text : '部门',
						dataIndex : 'em_depart',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'em_depart'}
					},{
						text : '组织',
						dataIndex : 'em_defaultorname',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'em_defaultorname'}
					}, {
						text : '职位',
						dataIndex : 'em_position',
						flex : 1,
						filter: {xtype: 'textfield', filterName: 'em_position'}
					} ],
					store:Ext.create('Ext.data.Store',{
						fields : [ {
							name : 'em_id',
							type : 'number'
						}, 'em_code', 'em_name','em_depart','em_defaultorname', 'em_position' ],
						data:[],
						autoLoad: false
					}),
					listeners: {
						afterrender: function() {
							me.getData(this);
						},
						scrollershow: function(scroller) {
							if (scroller && scroller.scrollEl) {
								scroller.clearManagedListeners();  
								scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
							}
						}
					}
				}],
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					iconCls: 'x-btn-confirm',
					handler: function(btn) {
						var win = btn.ownerCt.ownerCt;
						if(win.find){
							me.setPersonals(win.down('gridpanel'));
						}
						me.getPersonalPower(grid, win.down('gridpanel'));
						win.hide();
					}
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-btn-close',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.find = find;
		win.show();
	},
	getData:function(grid){
		var f="nvl(em_class,' ')<>'离职'";
		Ext.Ajax.request({
        	url : basePath + 'ma/update/getEmpdbfindData.action',
        	params : {
        		fields:'em_id,em_code,em_name,em_depart,em_defaultorname,em_position',
	   			condition: f,
	   			page: -1,
	   			pagesize: 0
	   		},
		    method : 'post',
		    callback : function(opt, s, res){
		       var r = new Ext.decode(res.responseText);
		       if(r.exceptionInfo){
		    		showError(r.exceptionInfo);return;
		    	} else if(r.success && r.data){
		    	var data = Ext.decode(r.data.replace(/,}/g, '}').replace(/,]/g, ']'));
		    	grid.getStore().loadData(data);		    
		    	}
		    }
		});
	},
	getPersonalPower: function(grid, gl) {
		var em = new Array();
		Ext.each(gl.selModel.getSelection(),function(r){
			if(!Ext.isEmpty(r.get('em_id')) && r.get('em_id') > 0) {
				em.push({em_id: r.get('em_id'), em_name: r.get('em_name')});
			}		
	    });
		grid.getPersonalData(grid.urlType, em);
	},
	setPersonals:function(grid){
		var ids = '',names ='';
		var id = Ext.getCmp('emid'),name = Ext.getCmp('emname');
		Ext.each(grid.selModel.getSelection(),function(r){
			if(!Ext.isEmpty(r.get('em_id')) && r.get('em_id') > 0) {
				ids += ','+r.get('em_id');
				names += ','+r.get('em_name');
			}		
	    });
	    if(ids.length>0&&names.length>0){
	    	id.setValue(ids.substring(1));
	    	name.setValue(names.substring(1));
	    }
	},
	showSpecial: function(grid) {
		var me = this, cal = grid.pp_caller;
		function showButton(value,cellmeta){
			var returnStr = "<INPUT align='center' type='button' value='删除' onclick='Delete(&quot;"+cal+"&quot;);'>";
			return returnStr;
		};
		var win = Ext.getCmp('special-win-' + cal);
		if (!win) {
			win = Ext.create('Ext.Window', {
				id : 'special-win-' + cal,
				width : 500,
				height : 360,
				title : '特殊权限',
				modal : true,
				layout: 'anchor',
				items: [{
					xtype: 'gridpanel',
					id:'special'+cal,
					anchor: '100% 100%',
					columnLines : true,
					columns : [ {
						text : 'ID',
						dataIndex : 'ssp_id',
						hidden : true
					}, {
						text : 'caller',
						dataIndex : 'ssp_caller',
						hidden : true
					}, {
						text : '描述',
						dataIndex : 'ssp_desc',
						flex : 1,
						editor: {
							xtype: 'textfield'
						}
					}, {
						text : '链接',
						dataIndex : 'ssp_action',
						flex : 1,
						editor: {
							xtype: 'textfield'
						}
					},{
			        	text: '有效',
			            xtype: 'checkcolumn',
			            dataIndex : 'ssp_valid',
			            flex : 0.3,
			            align: 'center',
			            renderer:function(val, meta, record, x, y, store, view){
                   		    var cssPrefix = Ext.baseCSSPrefix,
                            cls = [cssPrefix + 'grid-checkheader'];
                   		    if(val==-1){
                   		    	val=1;
                   		    }
                   	  	    if (val) {
		                         cls.push(cssPrefix + 'grid-checkheader-checked');
		                     }
                            return '<div class="' + cls.join(' ') + '" >&#160;</div>';
			            }
					},{
						text:'操作',
						dataIndex: 'button',
						flex:0.3,
						renderer:showButton
					}],
					store : new Ext.data.Store({
						fields : [ {
							name : 'ssp_id',
							type : 'number'
						}, 'ssp_caller', 'ssp_desc', 'ssp_action','ssp_valid' ]
					}),
					plugins : [ Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToEdit : 1
					}) ],
					listeners : {
						itemclick : function(selModel, record) {
							var grid = selModel.ownerCt, store = grid.store, idx = store.indexOf(record), len = store
									.getCount();
							if (idx == len - 1) {
								store.add([ {}, {}, {}, {}, {} ]);
							}
						}
					}
				}],
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-btn-save',
					handler: function(btn) {
						me.saveSysSpecials(cal, btn.ownerCt.ownerCt.down('gridpanel'));
						btn.ownerCt.ownerCt.destroy();
					}
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-btn-close',
					handler: function(btn) {
						btn.ownerCt.ownerCt.destroy();
					}
				}]
			});
		}
		win.show();
		this.getSysSpecialPowers(cal, win.down('grid'));
	},
	getSysSpecialPowers: function(cal, grid) {
		Ext.Ajax.request({
			url: basePath + 'ma/power/getSysSpecialPowers.action',
			params: {
				caller: cal
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data){
					if(rs.data.length==0){
						rs.data=[ {}, {}, {}, {}, {} ];
					}
					grid.store.loadData(rs.data);
				}
			}
		});
	},
	saveSysSpecials: function(cal, grid) {
		var data = new Array();
		grid.store.each(function(item){
			if(item.dirty) {
				if((!Ext.isEmpty(item.get('ssp_desc')) && !Ext.isEmpty(item.get('ssp_action'))) || 
						(item.get('ssp_id') > 0)) {
					item.data.ssp_caller = cal;
					/**
					 * wusy
					 */
					item.data.ssp_valid = item.data.ssp_valid==true?"-1":"0";
					data.push(item.data);
				}
			}
		});
		if(data.length > 0) {
			Ext.Ajax.request({
				url: basePath + 'ma/power/saveSysSpecialPowers.action',
				params: {
					caller: cal,
					data: unescape(Ext.encode(data).replace(/\\/g,"%"))
				},
				callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else if(rs.data){
						grid.store.loadData(rs.data);
					}
				}
			});
		}
	},
	showCopyPane: function(grid) {
		var me = this, win = Ext.create('Ext.Window', {
			width : 500,
			height: 300,
			title : '复制权限',
			modal : true,
			layout: 'anchor',
			items: [{
				xtype: 'container',
				anchor: '100% 100%',
				autoScroll: true,
				layout: 'column',
				padding: '10',
				defaults: {
					labelWidth: 60,
					columnWidth: 1,
					margin: '3 10 3 10'
				},
				items: [{
					xtype: 'dbfindtrigger',
					fieldLabel: '从',
					name: 'em_position',
					p: 1,
					editable: false,
					autoDbfind: false,
					clearable: true,
					listeners: {
						aftertrigger: function(f, r) {
							f.jo_id = r.get('jo_id');
							f.setValue(r.get('jo_name'));
						}
					}
				},{
					xtype: 'dbfindtrigger',
					name: 'em_position',
					fieldLabel: '复制到',
					p: 2,
					editable: false,
					autoDbfind: false,
					clearable: true,
					listeners: {
						aftertrigger: function(f, r) {
							f.jo_id = r.get('jo_id');
							f.setValue(r.get('jo_name'));
						}
					}
				},{
					xtype: 'displayfield',
					columnWidth: .75
				},{
					xtype: 'button',
					text: '添加',
					columnWidth: .25,
					cls: 'x-dd-drop-ok-add',
					iconCls: 'x-dd-drop-icon',
					iconAlign: 'right',
					handler: function(btn) {
						var f = btn.ownerCt;
	    				f.insert(f.items.length - 2, {
	    					xtype: 'dbfindtrigger',
	    					name: 'em_position',
	    					fieldLabel: '复制到',
	    					p: 2,
	    					editable: false,
	    					autoDbfind: false,
	    					clearable: true,
	    					listeners: {
	    						aftertrigger: function(f, r) {
	    							f.jo_id = r.get('jo_id');
	    							f.setValue(r.get('jo_name'));
	    						}
	    					}
	    				});
					}
				}]
			}],
			buttonAlign: 'center',
			buttons: [{
				text: $I18N.common.button.erpSaveButton,
				cls: 'x-btn-blue',
				handler: function(btn) {
					Ext.MessageBox.show({
				     	title: $I18N.common.msg.title_prompt,
				     	msg: '目标岗位的权限将会被指定岗位覆盖，确定复制?',
				     	buttons: Ext.Msg.YESNO,
				     	icon: Ext.Msg.WARNING,
				     	fn: function(b){
				     		if(b == 'ok' || b == 'yes') {
				     			var w = btn.ownerCt.ownerCt, 
				     				from = w.down('dbfindtrigger[p=1]'),
				     				to = w.query('dbfindtrigger[p=2]'), tos = [];
				     			Ext.Array.each(to, function(t){
				     				if(!Ext.isEmpty(t.getValue()))
				     					tos.push(t.jo_id);
				     			});
				     			if(tos.length > 0)
				     				me.copyPower(from.jo_id, tos.join(','));
				     			w.hide();
				     		}
				     	}
					});
				}
			},{
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-blue',
				handler: function(btn) {
					btn.ownerCt.ownerCt.hide();
				}
			}]
		});
		win.show();
	},
	showSyncWin: function() {
		var me = this, win = Ext.create('Ext.Window', {
			title: '权限同步',
			width: 320,
			height: 400,
			autoScroll:true,
			closable:false,
			defaults: {
				margin: '3 10 10 5'
			},
			items: [{
				xtype: 'radio',
				name: 'synctype1',
				boxLabel: '按岗位同步',		
				checked: true,
				listeners: {
					change: function(f) {							
						if(f.checked){
						f.ownerCt.down('radio[name=synctype1]').setValue(f.value);						
						f.ownerCt.down('multidbfindtrigger[name=em_position]').setValue(null);
						f.ownerCt.down('multidbfindtrigger[name=em_position]').setDisabled(!f.value);
							
						f.ownerCt.down('radio[name=synctype2]').setValue(!f.value);						
						f.ownerCt.down('multidbfindtrigger[name=em_code]').setValue(null);
						f.ownerCt.down('multidbfindtrigger[name=em_code]').setDisabled(f.value);
						
						f.ownerCt.down('radio[name=synctype3]').setValue(!f.value);						
						f.ownerCt.down('dbfindtrigger[name=sn_title]').setValue(null);
						f.ownerCt.down('dbfindtrigger[name=sn_title]').setDisabled(f.value);
						}
						for(var i=f.ownerCt.items.items.length-1;i>1;i--){
							var itemf = f.ownerCt.items.items[i];
							if(itemf.xtype=='multidbfindtrigger'&&itemf.fieldLabel=="岗位名称"){								
								f.ownerCt.remove(itemf);								
									}
								}					
							}
						}			
			},{
				xtype: 'multidbfindtrigger',
				name: 'em_position',
				editable: false,
				fieldLabel: '岗位名称',
				//重写onConfirm方法
				onConfirm: function(selectAll) {
					var trigger = this;
					if(trigger.multistore){
						var grid = this.win.down('gridpanel');
						if(selectAll&&grid.store.data.length>0){//按条件全选
							Ext.each(grid.store.data.items,function(item){
								grid.multiselected.push(item.data.value);
							});
						}
						grid.multiselected=Ext.Array.unique(grid.multiselected);    
						trigger.setValue(grid.multiselected.join(trigger.separator));
						this.win.close();
					} else {
						var win = trigger.win;
						var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
						findgrid.setMultiValues();
						var k = Ext.Object.getKeys(trigger.multiValue),cp;
						Ext.each(k, function(key){
							cp = Ext.getCmp(key);
							if(cp.setValue !== undefined)
								cp.setValue(trigger.multiValue[key]);
						});
						trigger.setValue(trigger.multiValue[trigger.name]);
						if(selectAll){
							trigger.getAllData1(trigger);
						}else{
							trigger.fireEvent('aftertrigger', trigger, trigger.multiRecords);
							this.win.close();
						}
					}
				},
				getAllData1: function(trigger) {
					var win = this.win, g = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
					g.setLoading(true);
					g.getAllData(function(datas){
						g.setLoading(false);
						trigger.fireEvent('aftertrigger', trigger, datas);
						win.close();
					});
				}
			},{
				xtype: 'radio',				
				name: 'synctype2',
				checked: false,
				boxLabel: '按个人同步',
				listeners: {
					change: function(f) {					
						if(f.checked){
						f.ownerCt.down('radio[name=synctype2]').setValue(f.value);						
						f.ownerCt.down('multidbfindtrigger[name=em_code]').setValue(null);
						f.ownerCt.down('multidbfindtrigger[name=em_code]').setDisabled(!f.value);
														
						f.ownerCt.down('radio[name=synctype1]').setValue(!f.value);
						f.ownerCt.down('multidbfindtrigger[name=em_position]').setValue(null);						
						f.ownerCt.down('multidbfindtrigger[name=em_position]').setDisabled(f.value);
						
						f.ownerCt.down('radio[name=synctype3]').setValue(!f.value);						
						f.ownerCt.down('dbfindtrigger[name=sn_title]').setValue(null);
						f.ownerCt.down('dbfindtrigger[name=sn_title]').setDisabled(f.value);						
						}
						for(var i=f.ownerCt.items.items.length-1;i>3;i--){
							var itemf = f.ownerCt.items.items[i];
							if(itemf.xtype=='multidbfindtrigger'&&itemf.fieldLabel=="员工编号"){
								f.ownerCt.remove(itemf);								
									}
								}							
							}
						}
			},{
				xtype: 'multidbfindtrigger',
				name: 'em_code',
				fieldLabel: '员工编号',
				//重写onConfirm方法
				onConfirm: function(selectAll) {
					var trigger = this;
					if(trigger.multistore){
						var grid = this.win.down('gridpanel');
						if(selectAll&&grid.store.data.length>0){//按条件全选
							Ext.each(grid.store.data.items,function(item){
								grid.multiselected.push(item.data.value);
							});
						}
						grid.multiselected=Ext.Array.unique(grid.multiselected);    
						trigger.setValue(grid.multiselected.join(trigger.separator));
						this.win.close();
					} else {
						var win = trigger.win;
						var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
						findgrid.setMultiValues();
						var k = Ext.Object.getKeys(trigger.multiValue),cp;
						Ext.each(k, function(key){
							cp = Ext.getCmp(key);
							if(cp.setValue !== undefined)
								cp.setValue(trigger.multiValue[key]);
						});
						trigger.setValue(trigger.multiValue[trigger.name]);
						if(selectAll){
							trigger.getAllData1(trigger);
						}else{
							trigger.fireEvent('aftertrigger', trigger, trigger.multiRecords);
							this.win.close();
						}
					}
				},
				getAllData1: function(trigger) {
					var win = this.win, g = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
					g.setLoading(true);
					g.getAllData(function(datas){
						g.setLoading(false);
						trigger.fireEvent('aftertrigger', trigger, datas);
						win.close();
					});
				}
			},
			{
				xtype: 'radio',				
				name: 'synctype3',
				checked: false,
				boxLabel: '按模块同步',
				listeners: {
					change: function(f) {						
						if(f.checked){
						f.ownerCt.down('radio[name=synctype3]').setValue(f.value);						
						f.ownerCt.down('dbfindtrigger[name=sn_title]').setValue(null);
						f.ownerCt.down('dbfindtrigger[name=sn_title]').setDisabled(!f.value);						
							
						f.ownerCt.down('radio[name=synctype1]').setValue(!f.value);
						f.ownerCt.down('multidbfindtrigger[name=em_position]').setValue(null);						
						f.ownerCt.down('multidbfindtrigger[name=em_position]').setDisabled(f.value);
						
						f.ownerCt.down('radio[name=synctype2]').setValue(!f.value);						
						f.ownerCt.down('multidbfindtrigger[name=em_code]').setValue(null);
						f.ownerCt.down('multidbfindtrigger[name=em_code]').setDisabled(f.value);				
					}}
				}
			},{
				xtype: 'dbfindtrigger',
				name: 'sn_title',
				id: 'sn_title',
				caller:'ModulePower!Post',
				fieldLabel: '模块名称'
			}],
			buttonAlign: 'center',
			buttons: [{
				cls: 'x-btn-blue',
				xtype: 'erpSyncButton',
				itemId : 'sync',
				autoRefreshPower:true,
				autoClearCache: true,
				syncUrl:'ma/power/syncPower.action'
			}, {
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-blue',
				handler: function(b) {
					b.up('window').close();
				}
			}]		 
		});
		win.show();
	},
	getAllJobs : function(b) {
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'Job',
	   			field: 'wmsys.wm_concat(jo_id)',
	   			condition: '1=1'
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			} else if(r.success && r.data){
	   				b.syncdatas = r.data;
	   			}
	   		}
		});
	},
	copyPower : function(fid, tid) {
		if(fid != tid) {
			Ext.Ajax.request({
				url: basePath + 'ma/power/copypower.action',
				params: {
					f : fid,
					t : tid
				},
				callback : function(o, s, r) {
					if( s ) {
						var e = r.responseText;
						if(e == 'success') {
							alert('复制成功!');
						} else {
							alert(e);
						}
					}
				}
			});
		}
	},
	getSyncDatas:function(w){					
		var jo_id = null, name = null;
		var j = new Array(),items=w.items.items;
		if(items && items.length >= 4) {
			Ext.each(items, function(item){
				if(item.name=='em_position'){
					jo_id = item.jo_id;
					if(jo_id!=null && !Ext.Array.contains(j, jo_id)) {
						j.push(jo_id);											
					}	
				}
			});
		}
		w.down('erpSyncButton[itemId=sync]').syncdatas = j.join(',');
		w.down('erpSyncButton[itemId=sync]').caller ='PositionPower!Post';
	},
	getSyncDatas2:function(w){					
		var em_id = null, name = null;
		var j = new Array(),items=w.items.items;
		if(items && items.length >= 4) {
			Ext.each(items, function(item){
				if(item.name=='em_code'){
					em_id = item.em_id;
					if(em_id!=null && !Ext.Array.contains(j, em_id)) {
						j.push(em_id);											
					}	
				}
			});
		}
		w.down('erpSyncButton[itemId=sync]').syncdatas = j.join(',');
		w.down('erpSyncButton[itemId=sync]').caller ='PersonalPower!Post';
	}
});