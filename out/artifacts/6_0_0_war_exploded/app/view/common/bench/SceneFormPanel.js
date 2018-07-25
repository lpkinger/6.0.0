Ext.QuickTips.init();

Ext.define('erp.view.common.bench.SceneFormPanel', {
	extend:'Ext.form.Panel',
	alias:'widget.erpSceneFormPanel',
	bodyCls : 'x-panel-body-gray',
	padding:'0',
	cls:'form',
	margin :'0',
	requires: ['erp.view.core.button.VastReStart'],
	isList: false,
	initComponent : function(){ 
		Ext.apply(this, { 
			items:[{
				xtype:'container',
				layout:'table'
			}]
		});
		Scene = this.Scene||Scene;
		var param = {bscode: Scene, condition: (getUrlParam('urlcondition') ||this.condition), _noc: (getUrlParam('_noc') || this._noc)};
		if(!this.isList){
			param.page= page;
			param.pageSize= pageSize;
		}
		
		param._config=getUrlParam('_config');
		this.params = this.params||param;
		this.getScene('bench/getBenchSenceConfig.action', this.params);
		this.callParent(arguments);
	},
	getScene: function(url, params){
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params: params,
			method : 'post',
			callback : function(options, success, response){
				me.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);
					parent.Ext.getCmp('scene_'+Scene).on('activate',function(now,old){
						me.getScene('bench/getBenchSenceConfig.action', me.params);
			   		});
					return;
				}
				me.setSceneBatch(res);
				if(!me.isList){
					var grid = me.nextNode('erpSceneGridPanel');
					grid.setColumnAndStore(res);
					parent.Ext.getCmp('scene_'+Scene).on('activate',function(now,old){
						grid.getColumnsAndStore();
			   		});
				}else{
					Ext.getCmp('scene_'+Scene).on('activate',function(now,old){
						var win = now.getEl().down('iframe').dom.contentWindow;
		   				if(win == null || win.Ext === undefined) {
							return;
						}
						var grid = win.Ext.getCmp("grid");
						if(grid){
							grid.lastSelected = grid.selModel.getSelection();//记录当前选中的record
							grid.getColumnsAndStore();
						}
			   		});
				}
			}
		});
	},
	setSceneBatch: function(res){
		var me = this;
		var Btns = new Array();
		var buttons = res.scenebuttons;
		if(buttons.length>0){
		Ext.Array.each(buttons,function(button,index){
			var obj = {	
				data: button,
				cls: 'x-btn-batch'
			}
			var buttonString = button.sb_alias;
			if(button.sb_requesttype&&button.sb_requesttype=='page'){
				obj.xtype = 'button';
				obj.cls='x-btn-work';
				obj.text = button.sb_title;
				obj.tooltip = button.sb_description?button.sb_description:button.sb_title;
				var url = 'jsps/common/bench/batchDeal.jsp';
				if(button.sb_url!=null){
					url = button.sb_url;
				}
				if(url.indexOf('whoami')>-1){
					button.sb_relativecaller = getStringParam(url,'whoami');
				}else if(button.sb_relativecaller){
					if(contains(url, '?', true)){
						url += '&whoami='+button.sb_relativecaller;
					}else{
						url += '?whoami='+button.sb_relativecaller;
					}
				}
				
				obj.handler = function(btn,e){
					if(!button.sb_relativecaller&&!button.sb_url){
						showError('此按钮暂未配置功能，请联系管理员进行设置！');
						return;
					}
					var myurl = url;
					if(!me.isList){
						var urlcondition = '';
						var grid = me.nextNode('erpSceneGridPanel');
						if(buttonString){
							if(contains(myurl, '?', true)){
								myurl += '&buttons='+encodeURIComponent(buttonString);
							}else{
								myurl += '?buttons='+encodeURIComponent(buttonString);
							}
						}
						var count = Ext.Object.getSize(grid.selectObject);
						if(count==0){
							if(url.indexOf('bench/batchDeal.jsp')>0){
								showError('未勾选数据！');
								return;
							}
							if(btn.data.sb_condition){
								if(/@?IDS/gi.exec(btn.data.sb_condition)){
									showError('未勾选数据！');
									return;
								}else if(res.batchSet){
				    		   		var arr = res.batchSet.split(','), ff = [];
								   	for(var i=0;i<arr.length;i++){
									   	ff = arr[i].split(':');
									   	if(btn.data.sb_condition.indexOf(ff[0])>-1){
									   		showError('未勾选数据！');
											return;
									   	}
								   	}
								}
								urlcondition = btn.data.sb_condition;
							}
						}else{
							var IDS = '';
							if(!(res.batchSet&&btn.data.sb_condition)&&grid.keyField&&grid.keyField.indexOf('+') < 0){
								var arr = new Array();
								Ext.each(Ext.Object.getKeys(grid.selectObject),function(k){
									if(typeof(grid.selectObject[k][grid.keyField])=='string'){
										arr.push("'"+grid.selectObject[k][grid.keyField].replace(/'/g,"''")+"'");
									}else{
										arr.push("'"+grid.selectObject[k][grid.keyField]+"'");
									}
								});
								if(arr.length>0){
									arr = Ext.Array.unique(arr);
									IDS = arr.join(',');
								}
							}
							if(btn.data.sb_condition){
								urlcondition = btn.data.sb_condition;
								if(res.batchSet){
				    		   		var arr = res.batchSet.split(','), ff = [];
								   	Ext.Array.each(arr, function(r){
									   	ff = r.split(':');
									   	var cons = "",arr = new Array();
										Ext.each(Ext.Object.getKeys(grid.selectObject),function(k){
											if(typeof(grid.selectObject[k][ff[1]])=='string'){
												arr.push("'"+grid.selectObject[k][ff[1]].replace(/'/g,"''")+"'");
											}else{
												arr.push("'"+grid.selectObject[k][ff[1]]+"'");
											}
										});
										if(arr.length>0){
											arr = Ext.Array.unique(arr);
											cons = arr.join(',');
											urlcondition = urlcondition.replace(new RegExp(ff[0],"gm"),cons);
										}
								   	});
								}else if(grid.keyField&&grid.keyField.indexOf('+') < 0&&IDS){
									urlcondition = urlcondition.replace(/@?IDS/gi,IDS);
								}
							}else if(grid.keyField&&grid.keyField.indexOf('+') < 0&&IDS){
								urlcondition = grid.keyField +' in ('+IDS+')';
							}
							var operation = button.sb_title;
							if(urlcondition.indexOf('main')>-1){
								var main = getStringParam(urlcondition,'main');
								if(main.split(',').length>1){
									showError(button.sb_title+'一次只能操作一个单据！');
									btn.removeListener('click');
									me.down('erpSwitchButton').setActive();
									return false;
								}
								operation = button.sb_description?button.sb_description:button.sb_title;
							}else if(urlcondition.indexOf('detail')==-1){
								urlcondition = 'urlcondition=' + urlcondition;
							}else{
								operation = button.sb_description?button.sb_description:button.sb_title;
							}
						}
						if(urlcondition){
							if(contains(myurl, '?', true)){
								myurl += '&'+urlcondition;
							}else{
								myurl += '?'+urlcondition;
							}
						}
						
			    		if(contains(myurl, '?', true)){
							myurl += '&operation='+operation;
						}else{
							myurl += '?operation='+operation;
						}
					}
					
					//打开一个新的界面
					openUrl2(myurl,button.sb_title);
				}
			}else if(button.sb_requesttype&&button.sb_requesttype=='action'){
				var url = button.sb_url;
				if(buttonString&&!contains(buttonString, '#', true)){
					obj.xtype = buttonString;
					obj.width = 'auto';
					obj.style = {
			    		marginLeft: '0px'
			        };
			        obj.iconCls = '';
				}else{
					obj.xtype = 'button';
				}
				
				if(button.sb_title){
					obj.cls='x-btn-work';
					obj.text = button.sb_title;
				}else{
					obj.listeners={
						beforerender: function(btn){
							btn.text = '<span class="x-btn-text">'+btn.text+'</span>';
						}
					}
				}
				if(typeof(obj.handler)!='function'&&!me.isList){
					obj.handler = function(btn,e){
						if(!btn.sb_relativecaller&&!btn.sb_url){
							showError('此按钮暂未配置功能，请联系管理员进行设置！');
							return;
						}
					
						var grid = me.nextNode('erpSceneGridPanel');
						var myurl=url,data = new Array();
						var count = Ext.Object.getSize(grid.selectObject);
						if(count==0){
							showError('未勾选数据！');
							return;
						}
						if(grid.keyField != null && grid.keyField != ''){
							Ext.each(Ext.Object.getKeys(grid.selectObject),function(k){
								var fields = Ext.Object.getKeys(grid.selectObject[k]);
				    		   	if(grid.keyField.indexOf('+') > 0) {
				    		   		var ID = null;
				    		   		var arr = grid.keyField.split('+'), ff = [], val;
								   	Ext.Array.each(arr, function(r){
									   	ff = r.split('@');
										if(fields.indexOf(ff[1]) > -1) {
									   		if(ID==null){
									   			ID = new Object();
									   		}
										   	val = grid.selectObject[k][ff[1]];
							    		   	if(val instanceof Date)
							    			   	val = Ext.Date.format(val, 'Y-m-d');
							    			ID[ff[1]] = val;
									   }
								   	});
								   	if(ID!=null)
								   		data.push(ID);
				    		   	} else if(fields.indexOf(grid.keyField) > -1){
				    		   		var ID = new Object();
				    		   		ID[grid.keyField] = grid.selectObject[k][grid.keyField];
				    		   		data.push(ID);
				    		   	}
			    		  });
			        	}
						if(data.length<1){
							showError("未获取到数据，请检查主键设置！");
							return;
						}
						var param = new Object();
						param.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
						if(myurl.indexOf('caller')<0){
							param.caller = button.sb_relativecaller;
						}
						Ext.Ajax.request({
							url : basePath + myurl,
							params: param,
							method : 'post',
							callback : function(options, success, response){
								me.down('erpSwitchButton').setActive();
								if (!response) return;
								var res = new Ext.decode(response.responseText);
								if(res.exceptionInfo != null){
									showError(res.exceptionInfo);
									return;
								}
								if(res.success){
									showMessage('提示', btn.text+'成功!', 1000);
									grid.getColumnsAndStore();
								}
							}
						});
					};
				}
			}
			Btns.push(obj);
		});
		
		me.items.items[0].add({
				xtype:'displayfield',
				margin:'5 0 0 4',
				value:'<b>操作:</b>'
			},{
				xtype: 'erpSwitchButton',
				activeCls: 'x-btntext-switch-active',
				items: Btns
			});
			me.setHeight(50);
		}else {
			me.setHeight(10);
			repeatCount = true;
		}
	}
});
