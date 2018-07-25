Ext.QuickTips.init();
Ext.define('erp.controller.ma.bench.SceneSet', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
	views:[
			'ma.bench.SceneSet','core.form.Panel','ma.bench.SceneGridPanel','core.grid.Panel2','core.button.CopyAll','core.form.YnField',
			'core.button.Add','core.button.Save','core.button.Close','core.button.ComboButton','core.button.Upload','core.button.Update',
			'core.button.Delete','core.button.Sync','core.button.DeleteDetail','core.trigger.TextAreaTrigger','core.toolbar.Toolbar',
			'core.grid.YnColumn'],
  	init:function(){
  		var me = this;
   		this.control({
   			'erpFormPanel':{
   				afterload: function(form){
   					var islist = Ext.getCmp('bs_islist');
   					me.isListHide(islist&&islist.value==-1,true);
   				}
   			},
   			'field[name=bs_islist]': {
   				change: function(field,value){
   					me.isListHide(value==-1);
   				}
   			},
   			'dbfindtrigger[name = bs_caller]': {
   				aftertrigger: function(t,data){
   					var islist = Ext.getCmp('bs_islist');
   					if(islist&&islist.value==0){
	   					warnMsg('根据CALLER生成配置', function(btn){
							if(btn == 'yes'){
								me.FormUtil.setLoading(true);
								Ext.Ajax.request({
									url: basePath + 'bench/ma/getSetByCaller.action',
									params: {
										caller: t.value
									},
									callback: function(options, success, response) {
										me.FormUtil.setLoading(false);
										if (!response) return;
										var res = new Ext.decode(response.responseText);
										if(res.success){
											var grid = Ext.getCmp('grid');
											if(bscode == ''){
												grid.store.loadData(res.data);
											}else{
												grid.store.each(function(record){
													record.set('deploy',false);
												});
												Ext.each(res.data, function(d,index){
													var record = grid.store.getAt(index);
													if(record){
														d.deploy = true;
														Ext.each(Ext.Object.getKeys(d),function(k){
															if(k!=grid.keyField){
																record.set(k,d[k]);
															}
														});
													}else{
														grid.store.add(d);
														var record = grid.store.getAt(index);
														record.set('deploy',true);
													}
												});								
											}
										}
										if(res.exceptionInfo){
											showError(res.exceptionInfo);
											return;
										}
									}
								});
							}
						});
	   				}
   				}
   			},
    		'erpSaveButton': {
    			click: function(btn){
    				var f=me.check();
    				if(f){
	    				me.save(me);				
    				};	
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var f=me.check(true);
    				if(f){
    					me.update();
    				}
    			}
    		},
    		'erpCopyButton': {
    			click: function(btn){
    				me.copyConfig();
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.addNewScene();
    			}
    		},
    		'erpDeleteButton' :{
    			click:function(btn){
    				me.onDelete();
    			}
    		},
    		'erpSyncButton': {
    			afterrender: function(btn){
    				if(isSaas){btn.hide();};
    				btn.autoClearCache = true;
    				btn.checkMaster = function(g,curMaster,s,ms,t,sc){
						var me = this;
						var bool =true;
						if("true" === g && "admin" !== t && !Ext.Array.contains(ms, s.ma_name))
							bool=false;
						if(s.ma_name == 'DataCenter' && "admin" !== t) {
							bool=false;
						}
						return bool;
					};
					btn.sync = function() {
						var masters = this.getCheckData(), form = Ext.getCmp('form'), w = this.win, me = this;
						if (!Ext.isEmpty(masters)) {
							w.setLoading(true);
							Ext.Ajax.request({
								url: basePath + 'common/form/vastPost.action',
								params: {
									caller: caller + '!Post',
									data: bscode,
									to: masters
								},
								timeout: 600000,
								callback: function(opt, s, r) {
									w.setLoading(false);
									if(s) {
										var rs = Ext.decode(r.responseText);
										if(rs.data) {
											showMessage('提示', rs.data);
										} else {
											alert('同步成功!');
										}
					   					w.hide();
					   					if(me.autoClearCache) {
					   						me.clearCache();
					   					}
									}
								}
							});
						}
					};
    			}
    		},
    		'sceneGridPanel' : {
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			},
    			select:function(selModel,record){
 					var grid=selModel.view.ownerCt.ownerCt;
    				if(record && record.data.sg_type == 'C'){
    					grid.down('erpComboButton').setDisabled(false);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    				}
    			}
    		},
    		'#SceneButtonSet' : {
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'erpFormPanel textfield[name=bs_detno]': {
    			afterrender: function(field){
    				if(!formCondition&&!field.value){
    					var tab = parent.Ext.getCmp('bench_'+bench);
    					if(tab)
    					field.setValue(tab.maxDetno+1);
    				}
    			}
    		},
    		'erpFormPanel textfield[name=bs_table]': {
    			change: function(field,newVal,oldVal){//主表bs_table值变更时,对应从表字段也变更
    				if(newVal.split(' ')[0].toUpperCase!=oldVal.split(' ')[0].toUpperCase){
	    				var grid = Ext.getCmp('grid');
						Ext.Array.each(grid.store.data.items, function(item){
							item.set('sg_table',newVal.split(' ')[0]);
						});
	    			}
    			}
    		},
    		'erpComboButton': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected; 
    				if(record) {
    					if(record.data.sg_type == 'C') { 	
	    					btn.comboSet(bscode, record.data.sg_field);
	    				}
    				}
    			}
    		},
    		'#erpSetComboButton': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt,
    					record = grid.selModel.lastSelected;
    				if(record && record.get('sg_type') == 'C') {
    					warnMsg('确定重置下拉框数据?', function(b){
    						if(b == 'ok' || b == 'yes') {
    							Ext.Ajax.request({
    								url: basePath + 'bench/ma/resetCombo.action',
    								params: {
    									caller: bscode,
    									field: record.get('sg_field')
    								},
    								callback: function(opt, s, r) {
    									if(s) {
    										var rs = Ext.decode(r.responseText);
    										if(rs.error) {
    											alert(rs.error);
    										} else {
    											alert('设置成功!');
    										}
    									}
    								}
    							});
    						}
    					});
    				}
    			}
    		}
		});
   	},
   	check:function(update){//检查table主键中字段是否配置
   		var me = this,grid = Ext.getCmp('grid');
   		if(!grid || grid.isHidden()){
   			return true;
   		}
	    var keyField=Ext.getCmp('bs_keyfield').value;
	 	var keyArr=new Array();
	 	if(keyField != null && keyField != ''){
			if(keyField.indexOf('+') > 0) {
			    var arr = keyField.split('+');
			    Ext.Array.each(arr, function(r){
					ff = r.split('@');
					keyArr.push(ff[1]);
				});
			} else {
			    keyArr.push(keyField);
			}
		}
	    var items = grid.store.data.items,dd= new Array(),flag=true;
	    Ext.Array.each(items, function(item){
			d = item.data;
			if(update){
				if(d['deploy'] == true){
		    		dd.push(d);
		    	}
			}else{
				if(!Ext.isEmpty(d['sg_field'])){
					dd.push(d);
				}
			}
		});
		var batchSet = Ext.getCmp('bs_batchset').value,batchArr = new Array();
		if(batchSet){
	   		var arr = batchSet.split(','), ff = [];
		   	Ext.Array.each(arr, function(r){
			   	ff = r.split(':');
			   	batchArr.push(ff[1]);
		   	});
		}
		if(dd.length > 0) {
			Ext.Array.each(keyArr,function(key){
			    flag=false;
				for(var i=0;i<dd.length;i++){
					if(key==dd[i]['sg_field']){
						flag=true;
						break;
					}
				}
				if(!flag){
					showError('未配置table主键字段：'+key);
					return flag;
				}
			});
			Ext.Array.each(batchArr,function(key){
			    flag=false;
				for(var i=0;i<dd.length;i++){
					if(key==dd[i]['sg_field']){
						flag=true;
						break;
					}
				}
				if(!flag){
					showError('未配置批量条件字段：'+key);
					return flag;
				}
			});
		} else {
			showError('请至少配置一个有效字段!');
			return false;
		}
		return flag;
	},
	save: function(){
		var me = this,form = Ext.getCmp('form'),detail = Ext.getCmp('grid');
		if(! me.FormUtil.checkForm()){
			return;
		}
		var params = new Object();
		var r = form.getValues();
		r['bs_bccode'] = bench;
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		
		if(detail && !detail.isHidden()){
			var records = detail.store.data.items,field = Ext.getCmp('bs_table'),val = field.value.split(' ')[0]
			Ext.Array.each(records, function(item){
				if(item.data['sg_field'] != null && item.data['sg_field'] != '' && Ext.isEmpty('sg_table')){
					item.set('sg_table', val);
				}
			});
			var gridStore = me.GridUtil.getGridStore();
			if(gridStore){
				gridStore = me.GridUtil.getAllGridStore();
			}
			params.param = '['+gridStore+']';
		}else{
			params.param = '[]';
		}
		
		var gridStore1 = me.GridUtil.getGridStore(Ext.getCmp('SceneButtonSet'));
		params.param1 = '['+gridStore1+']';
				
		var url = form.saveUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		}
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + url,
			params: params,
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage('提示', '保存成功!', 1000);
					var value = localJson.bs_code;
					var index = localJson.index;
					var formCondition = "bs_codeIS'" + value + "'";
					var gridCondition = "sg_bscodeIS'" + value + "'";
					var tab = parent.Ext.getCmp('bench_'+bench);
					if(tab){
						tab.maxDetno = localJson.maxDetno;
						var newpanel = tab.insert(index-1,{
							tag : 'iframe',
							title : r['bs_title'],
							id : value,
							border : false,
							layout : 'fit',
							html : '<iframe id = "iframe_scene_'+value+'" src="'+basePath+'jsps/ma/bench/sceneSet.jsp?bench='+bench+'&formCondition='+formCondition+'&gridCondition='+gridCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'						
						});
						var panel = tab.getActiveTab();
						tab.setActiveTab(newpanel); 
						panel.close();
					}else{
						window.location.href = window.location.href + '&formCondition=' + formCondition + '&gridCondition=' + gridCondition;
					}
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				} else {
					saveFailure();
				}
			}
		});
    },
    update: function(){
		var me = this,form = Ext.getCmp('form'),detail = Ext.getCmp('grid');
		var me = this;
		if(! me.FormUtil.checkForm()){
			return;
		}
		
		var params = new Object();
		var r = form.getValues();
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		if(detail && !detail.isHidden()){
			var records = detail.store.data.items, field = Ext.getCmp('bs_table'),val = field.value.split(' ')[0];
			Ext.Array.each(records, function(item){
				if(item.data['sg_field'] != null && item.data['sg_field'] != '' && Ext.isEmpty('sg_table')){
					item.set('sg_table', val);
				}
			});
			var de = detail.getChange();
			params.param = Ext.encode(de.added);
			params.param1 = Ext.encode(de.updated);
			params.param2 = Ext.encode(de.deleted);
		}else{
			params.param = '[]';
			params.param1 = '[]';
			params.param2 = '[]';
		}
		var gridStore1 = me.GridUtil.getGridStore(Ext.getCmp('SceneButtonSet'));
		params.param3 = '['+gridStore1+']';		
		var url = form.updateUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		}
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + url,
			params: params,
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage('提示', '更新成功!', 1000);
					var newurl = '';
					if(localJson.bs_code){
						var url = window.location.href;
						url = url.substring(0, url.indexOf('&'));
						url += "&formCondition=bs_codeIS'" + localJson.bs_code + "'&gridCondition=sg_bscodeIS'" + localJson.bs_code + "'";
						newurl = url;
					}
					
					var tab = parent.Ext.getCmp('bench_'+bench);
					if(tab){
						tab.maxDetno = localJson.maxDetno;
						var from = localJson.fromIndex;
						var to = localJson.toIndex;
						
						if(from!=to){
							if(from > to){
								to = to-1;
							}
							var value = r['bs_code'];
							var formCondition = "bs_codeIS'" + value + "'";
							var gridCondition = "sg_bscodeIS'" + value + "'";
							
							var panel = tab.getActiveTab();
							var newpanel = tab.insert(to,{
								tag : 'iframe',
								title : r['bs_title'],
								border : false,
								layout : 'fit',
								html : '<iframe id = "iframe_scene_'+value+'" src="'+basePath+'jsps/ma/bench/sceneSet.jsp?bench='+bench+'&formCondition='+formCondition+'&gridCondition='+gridCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'						
							});
							tab.setActiveTab(newpanel); 
							panel.close();
						}else{
							var panel = tab.getActiveTab();
							panel.setTitle(r['bs_title']);
							if(newurl){
								window.location.href =  newurl;
							}else{
								window.location.reload();
							}
						}
					}else{
						if(newurl){
							window.location.href =  newurl;
						}else{
							window.location.reload();
						}
					}
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				} else {
					updateFailure();
				}
			}
		});
    },
   	copyConfig: function(caller){
    	var me = this;
    	if(bscode){
	    	Ext.create('Ext.window.Window', {
				title : '请输入新场景标题',
				closeAction: 'destroy',
				modal : true,
				width : 300,
				height: 54,
				layout: {
			        type: 'hbox',
			        pack: 'center',
			        align: 'middle'
	    		},
				items: [{	
					xtype:'textfield',
					width:270
				}],
				buttonAlign : 'center',
				buttons : [{
					text : $I18N.common.button.erpConfirmButton,
					height : 26,
					handler : function(b) {
						var newtitle = b.ownerCt.ownerCt.down('textfield').value;
						var id = Ext.getCmp('bs_id').value;
						if(id&&newtitle){
							me.FormUtil.setLoading(true);
							Ext.Ajax.request({
								url : basePath + 'bench/ma/copyDataList.action',
								params: {
									id:id,
									newtitle:newtitle
								},
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.success){
										showMessage('提示','复制成功',1000);
										b.ownerCt.ownerCt.close();
										var value = res.bs_code;
										var tab = parent.Ext.getCmp('bench_'+bench);
										if(tab){
											var formCondition = "bs_codeIS'" + value + "'";
											var gridCondition = "sg_bscodeIS'" + value + "'";
											tab.maxDetno = res.maxDetno;
											var panel = tab.add({
												tag : 'iframe',
												title : newtitle,
												id : value,
												border : false,
												layout : 'fit',
												html : '<iframe id = "iframe_scene_'+value+'" src="'+basePath+'jsps/ma/bench/sceneSet.jsp?bench='+bench+'&formCondition='+formCondition+'&gridCondition='+gridCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'						
											});
											tab.setActiveTab(panel); 
										}else{
											me.addNewScene(value);
										}
									} else if(res.exceptionInfo){					
										showError(res.exceptionInfo);
									} 
								}
							});
						}else{
							showError('场景标题不能为空！');
						}
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					height : 26,
					handler : function(b) {
						b.ownerCt.ownerCt.close();
					}
				}]
			}).show();
    	}
    },
    onDelete : function(){
    	var me = this;
		warnMsg($I18N.common.msg.ask_del_scene, function(btn){
			if(btn == 'yes'){
				var form = Ext.getCmp('form');
				if(!contains(form.deleteUrl, '?caller=', true)){
					form.deleteUrl = form.deleteUrl + "?caller=" + caller;
				}
				var scenecode = Ext.getCmp('bs_code').value;
				me.FormUtil.setLoading(true);
				Ext.Ajax.request({
					url : basePath + form.deleteUrl,
					params: {
						scenecode: scenecode
					},
					method : 'post',
					callback : function(options,success,response){
						me.FormUtil.setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
							showError(localJson.exceptionInfo);return;
						}
						if(localJson.success){
							delSuccess(function(){
								me.onClose();							
							});//@i18n/i18n.js
						} else {
							delFailure();
						}
					}
				});
			}
		});
    },
    onClose: function(){
    	var tab = parent.Ext.getCmp('bench_'+bench);
    	if(tab){
	    	if(tab.items.items.length==1){
	    		var panel = tab.getActiveTab();
	    		tab.maxDetno = 0; 
	    		var newpanel = {
					tag : 'iframe',
					title : '新场景',
					id : bench+'_newScene',
					border : false,
					layout : 'fit',
					html : '<iframe src="'+basePath+'jsps/ma/bench/sceneSet.jsp?bench='+bench+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'						
				};
				newpanel = tab.add(newpanel);
				tab.setActiveTab(newpanel); 
	    		panel.close();
	    	}else{
	    		var panel = tab.getActiveTab();
	    		panel.close();
	    	}
    	}else{
    		tab = parent.Ext.getCmp("content-panel");
    		tab.getActiveTab().close();
    	}
    },
    addNewScene: function(code){
    	var tab = parent.Ext.getCmp('bench_'+bench),panel = null,title= '新场景',id = bench+'_newScene',url = 'jsps/ma/bench/sceneSet.jsp?bench='+bench;
    	if(tab){
    		panel = tab.down('#'+bench+'_newScene');
    	}else{
    		var tab = parent.Ext.getCmp("content-panel");
    		url = 'jsps/ma/bench/sceneSet.jsp?bench='+bench;
    		if(code){
    			url += "&formCondition=bs_codeIS'" + code + "'" + 
						"&gridCondition=sg_bscodeIS'" + code + "'";
				title = '场景维护(' + code + ')';
    			if(!tab){
					var tab = parent.parent.Ext.getCmp("content-panel");
					if(tab){
						id = tab.activeTab.id;
						id = id.substring(0,id.indexOf('_')) + code;
					}
				}else{
					id = tab.activeTab.id;
					id = id.substring(0,id.indexOf('_')) + code;
				}
    		}else{
    			id = caller;
    			title = '新增场景';
    		}
    		panel = parent.Ext.getCmp(id); 
    	}
		if(!panel){
			panel = { 
	    			id:id,
	    			title : title,
	    			tag : 'iframe',
	    			tabConfig:{tooltip: title},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',
	    			html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	tab.setActiveTab(tab.getActiveTab().id); 
	    				}
	    			} 
	    	};
			panel = tab.add(panel);
		}
		tab.setActiveTab(panel); 
    },
    isListHide:function(isList,first){
    	var tab = Ext.getCmp('tab');
    	var grid = Ext.getCmp('grid');
    	if(isList){
    		grid && grid.tab.hide();
    		if(tab.getActiveTab() && tab.getActiveTab().id!='SceneButtonSet'){
                tab.setActiveTab('SceneButtonSet');
			}

    		var table = Ext.getCmp('bs_table');
			table && table.setReadOnly(true); 
			var fieldlabel = table.fieldLabel;
			fieldlabel = fieldlabel.substring(fieldlabel.lastIndexOf('*</font>')+8);
			table.getEl().dom.firstChild.innerHTML = fieldlabel; 
			table.fieldLabel = fieldlabel;
			table && table.setFieldStyle('background:#f3f3f3;');
			table.allowBlank = true;
			
			var bs_caller = Ext.getCmp('bs_caller');
			bs_caller && bs_caller.setReadOnly(false);
			var fieldlabel = '<font color="red" style="position:relative; top:2px;right:2px; font-weight: bolder;">*</font>'+bs_caller.fieldLabel;
			bs_caller.getEl().dom.firstChild.innerHTML = fieldlabel;
			bs_caller.fieldLabel = fieldlabel;
			bs_caller && bs_caller.setFieldStyle('background:#fff;color:#313131;');
			bs_caller.allowBlank = false;
			
			var bs_condition = Ext.getCmp('bs_condition');
			bs_condition && bs_condition.setReadOnly(true); 
			bs_condition && bs_condition.setFieldStyle('background:#f3f3f3;');
			
			var groupby = Ext.getCmp('bs_groupby');
			groupby && groupby.setReadOnly(true); 
			groupby && groupby.setFieldStyle('background:#f3f3f3;');
			groupby.allowBlank = true;
			
			var orderby = Ext.getCmp('bs_orderby');
			orderby && orderby.setReadOnly(true); 
			orderby && orderby.setFieldStyle('background:#f3f3f3;');
			
			var bs_keyfield = Ext.getCmp('bs_keyfield');
			bs_keyfield && bs_keyfield.setReadOnly(true); 
			var fieldlabel = bs_keyfield.fieldLabel;
			fieldlabel = fieldlabel.substring(fieldlabel.lastIndexOf('*</font>')+8);
			bs_keyfield.getEl().dom.firstChild.innerHTML = fieldlabel; 
			bs_keyfield.fieldLabel = fieldlabel;
			bs_keyfield && bs_keyfield.setFieldStyle('background:#f3f3f3;');
			bs_keyfield.allowBlank = true;
			
			var bs_selffield = Ext.getCmp('bs_selffield');
			bs_selffield && bs_selffield.setReadOnly(true); 
			bs_selffield && bs_selffield.setFieldStyle('background:#f3f3f3;');
			
			var bs_batchset = Ext.getCmp('bs_batchset');
			bs_batchset && bs_batchset.setReadOnly(true); 
			bs_batchset && bs_batchset.setFieldStyle('background:#f3f3f3;');
    	}else{
    		if(!grid){
    			tab.insert(0,{
					id: 'grid',
					title :'场景列表',
					xtype : 'sceneGridPanel',
					detno : 'sg_detno',
					necessaryField : 'sg_field'
				});
    		}else{
    			grid.tab.show();
    		}
			tab.setActiveTab('grid');
			if(first){
				return;
			}
    		var table = Ext.getCmp('bs_table');
			table && table.setReadOnly(false); 
			var fieldlabel = '<font color="red" style="position:relative; top:2px;right:2px; font-weight: bolder;">*</font>'+table.fieldLabel;
			table.getEl().dom.firstChild.innerHTML = fieldlabel;
			table.fieldLabel = fieldlabel;
			table && table.setFieldStyle('background:#fff;color:#313131;');
			table.allowBlank = false;
			
			var bs_caller = Ext.getCmp('bs_caller');
			bs_caller && bs_caller.setReadOnly(false); 
			var fieldlabel = bs_caller.fieldLabel;
			fieldlabel = fieldlabel.substring(fieldlabel.lastIndexOf('*</font>')+8);
			bs_caller.getEl().dom.firstChild.innerHTML = fieldlabel; 
			bs_caller.fieldLabel = fieldlabel;
			bs_caller && bs_caller.setFieldStyle('background:#fff;color:#313131;');
			bs_caller.allowBlank = false;
			
			var bs_condition = Ext.getCmp('bs_condition');
			bs_condition && bs_condition.setReadOnly(false); 
			bs_condition && bs_condition.setFieldStyle('background:#fff;color:#313131;');
			
			var groupby = Ext.getCmp('bs_groupby');
			groupby && groupby.setReadOnly(false); 
			groupby && groupby.setFieldStyle('background:#fff;color:#313131;');
			groupby.allowBlank = true;
			
			var orderby = Ext.getCmp('bs_orderby');
			orderby && orderby.setReadOnly(false); 
			orderby && orderby.setFieldStyle('background:#fff;color:#313131;');
			
			var bs_keyfield = Ext.getCmp('bs_keyfield');
			bs_keyfield && bs_keyfield.setReadOnly(false); 
			var fieldlabel = '<font color="red" style="position:relative; top:2px;right:2px; font-weight: bolder;">*</font>'+bs_keyfield.fieldLabel;
			bs_keyfield.getEl().dom.firstChild.innerHTML = fieldlabel;
			bs_keyfield.fieldLabel = fieldlabel;
			bs_keyfield && bs_keyfield.setFieldStyle('background:#fff;color:#313131;');
			bs_keyfield.allowBlank = false;
			
			var bs_selffield = Ext.getCmp('bs_selffield');
			bs_selffield && bs_selffield.setReadOnly(false); 
			bs_selffield && bs_selffield.setFieldStyle('background:#fff;color:#313131;');
			
			var bs_batchset = Ext.getCmp('bs_batchset');
			bs_batchset && bs_batchset.setReadOnly(false); 
			bs_batchset && bs_batchset.setFieldStyle('background:#fff;color:#313131;');
    	}
    }
});