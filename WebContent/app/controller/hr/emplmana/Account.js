Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Account', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.emplmana.Account','core.form.Panel','core.button.CopyAll',
    		'core.button.Save','core.button.Close','core.grid.Panel2', 'core.toolbar.Toolbar', 'core.grid.YnColumn',
    		'core.button.Update','core.form.YnField','core.button.ResAudit','oa.doc.ItemSelector',
  			'core.button.Audit','core.button.Submit','core.button.ResSubmit','core.form.HrOrgSelectField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.onUpdate(this);
    			}
    		},
    		'erpFormPanel':{
    			afterload:function(t,o){
    				var l=t.items.length;
    				t.insert(l,{
    					xtype      : 'fieldcontainer',
			            fieldLabel : '',
			            defaultType: 'radiofield',
			            columnWidth: 1,
			            layout: {
		                    type: 'hbox',
		                    align: 'middle'
		                },
			            style: 'float:center;',
						items:[  
							{
			                    boxLabel  : '仅修改UAS密码',
			                    name      : 'synchronize',
			                    id		  : 'onlyuas',
			                    inputValue: '0',
			                    margin: '0 20 0 20'
			                }, {
			                    boxLabel  : '同步密码到优软云',
			                    name      : 'synchronize',
			                    id : "btob",
			                    margin: '0 20 0 20',
			                    inputValue: '1',
			                    checked: true,
			                    listeners : {
			                    	afterrender: function(btob){
			                    		//不存在btob账号，则同步密码到优软云不可点击
			                    		var em_mobile = Ext.getCmp('em_mobile');
			                    		if(em_mobile){
			                    			var re =new RegExp("^[1|8][3-8]\\d{9}$|^([6|9])\\d{7}$|^[0][9]\\d{8}$|^[6]([8|6])\\d{5}$|^(886|0)[9]\\d{8}$");
        									if(!re.test(em_mobile.getValue())){
        										//2018040521   如果不存在手机号，把默认值设置为仅修改UAS
        										btob.setRawValue(false);
        										Ext.getCmp('onlyuas').setRawValue(true);
					                    		btob.setDisabled(true);
        									}
			                    		}
			                    	}
			                    
			                    }
			                },{
			                	xtype: 'label',
								margin: '0 20 0 20',
								html: '<a href="'+basePath+'/b2b/ucloudUrl_token.action?url=https://www.ubtob.com&urlType=ubtob"  target="_blank">了解优软云</a>'
			                }
						]
    				});
    			}
    		},
    		'erpGridPanel2': {
    			itemclick: this.GridUtil.onGridItemClick
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpCopyButton':{
    			click: function(btn){
    				var win = Ext.widget('window', {
						title: '<div align="center" class="WindowTitle">选择对象</div>',
						modal: true,
						width:'80%',
						height:'90%',
						layout:'border',
						requires:['erp.view.oa.doc.ItemSelector'],
						items:[{
							region:'center',
							layout:'border',
							items:[{
								region:'north',
								xtype:'form',
								bodyPadding: 10,
								layout:'column',
								bodyStyle:'background:#fafafa;',
								items:[{
									xtype:'textfield',
									margin:'0 0 0 20',
									fieldLabel:'快速搜索',
									labelStyle:'font-weight:bold;',
									columnWidth:0.8
								},{
									xtype:'button',
									id:'search',
									text:'搜索',
									cls:'button1 pill',
									style:'margin-left:5px;',
									width:60,
									handler:function(btn){
										btn.setDisabled(true);
										var likestring=btn.ownerCt.items.items[0].value;
										if(!likestring) {
											showMessage('提示','请输入需要搜索的信息!',1000);
											btn.setDisabled(false);
											return;
										}
										Ext.Ajax.request({//查询数据
											url : basePath + 'common/ProcessQueryPersons.action',
											params:{
												likestring:likestring
											},
											callback : function(options,success,response){
												var res = new Ext.decode(response.responseText);													
												if(res.data){
													Ext.getCmp('itemselector-field').fromField.store.loadData(res.data);
													btn.setDisabled(false);
												} else if(res.exceptionInfo){
													showError(res.exceptionInfo);
												}
											}
										});
									}
								}]
							},{
								region:'center',
								xtype: 'itemselector',			
								anchor: '100%',	
								id: 'itemselector-field',
								displayField: 'text',
								valueField: 'value',
								allowBlank: false,
								msgTarget: 'side'							
							}],
							buttonAlign:'center',
							buttons:['->',{
								cls:'button1 pill',
								style:'margin-left:5px;',
								text:'确认',
								scope:this,
								handler:function(btn){
									var itemselector=Ext.getCmp('itemselector-field');
									var value=itemselector.getRawValue();
									if(value.length<1){
                                       showMessage('提示','选择需要设置对象',1000);
									}else {
										me.copySettings(value,Ext.getCmp('em_id').value);
										btn.ownerCt.ownerCt.ownerCt.close();
									}
								}
							},{
								cls:'button1 pill',
								style:'margin-left:5px;',
								text:'关闭',
								handler:function(btn){
									btn.ownerCt.ownerCt.ownerCt.close();
								}
							},'->']
						},{
							region:'west',
							width:'40%',
							xtype:'erpOrgTreePanel',
							bodyStyle:'background:#fafafa;'
						}]
					});
					win.show();
    			}
    		 },
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('em_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('em_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('em_id').value);
    			}
    		},
    		'field[name=em_password]': {
    			afterrender: function(f) {
    				f.el.dom.getElementsByTagName('input')[0].type = "password";
    				//设置密码为空，后台判断密码为空时，不进行处理
    				f.setValue("");
    			}
    		},
    	    'dbfindtrigger[name=es_field]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false);
                    var record = Ext.getCmp('grid').selModel.getLastSelected();
                    var caller = record.data['es_pagecaller'];
                    var pagekind = record.data['es_kind'];
                    if (caller == null || caller == '') {
                        showError("请选选择关联的页面CALLER!");
                        t.setHideTrigger(true);
                        t.setReadOnly(true);
                    } else {
                        t.dbBaseCondition = "dld_caller='" + caller + "'";
                    }
                    if(pagekind=='batchdeal'){
                    	t.dbfind='DetailGrid|dg_field';
                        t.dbBaseCondition="dg_caller='"+caller+"'";
                    }
                    	
                    
                }
            },
            'dbfindtrigger[name=es_pagecaller]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false);
                    var record = Ext.getCmp('grid').selModel.getLastSelected();
                    var pagekind = record.data['es_kind'];
                    if(pagekind=='batchdeal')
                    	t.dbfind='Form|fo_caller';
                }
            }
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	copySettings:function(value,emid){
		var me=this;
		this.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'hr/emplmana/copyRelativeSettings.action',
			params : {
				toobjects:value.join(";"),
				fromemid:emid
			},
			async:false,
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage('提示','复制成功',1000);
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				} else{
					showMessage('提示','复制失败',1000);
				}
			}

		});
		
	},
	//更新方法 lidy
	onUpdate: function(me, ignoreWarn, opts, extra){
		var mm = this;
		var form = Ext.getCmp('form');
		var s1 = mm.FormUtil.checkFormDirty(form);
		var s2 = '';
		var grids = Ext.ComponentQuery.query('gridpanel');
		var removea = new Array();
		if(form.codeField && (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
			showError('编号不能为空.');
			return;
		}
		Ext.each(grids,function(g,index){
			if(g.xtype=='itemgrid'){
				g.saveValue();
				removea.push(g);
			}
		});

		if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
			Ext.each(grids, function(grid, index){
				if(grid.GridUtil){
					var msg = grid.GridUtil.checkGridDirty(grid);
					if(msg.length > 0){
						s2 = s2 + '<br/>' + msg;
					}
				}
			});
		}
		if(s1 == '' && (s2 == '' || s2 == '<br/>')){
			showError('还未添加或修改数据.');
			return;
		}
		Ext.each(removea,function(r,index){
			Ext.Array.remove(grids,r);
		});
		if(form && form.getForm().isValid()){
			//form里面数据
			var r = (opts && opts.dirtyOnly) ? form.getForm().getValues(false, true) : 
				form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){					
					if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(f && opts && opts.dirtyOnly) {
					extra = (extra || '') + 
					'\n(' + f.fieldLabel + ') old: ' + f.originalValue + ' new: ' + r[k];
				}				
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
				
				
			});
			if(opts && opts.dirtyOnly && form.keyField) {
				r[form.keyField] = form.down("#" + form.keyField).getValue();
			}
			if(!mm.FormUtil.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			var params = [];
			if(grids.length > 0 && grids[0].columns.length > 0 && !grids[0].ignore){
				if(grids[0].GridUtil.isEmpty(grids[0])) {
					warnMsg('明细还未录入数据,是否继续保存?', function(btn){
						if(btn == 'yes' || btn == 'ok'){
							mm.update(r, '[]', extra);
						} else {
							return;
						}
					});
				} else if(grids[0].GridUtil.isDirty(grids[0])) {
					var param = grids[0].GridUtil.getGridStore();
					if(grids[0].necessaryField && grids[0].necessaryField.length > 0 && (param == null || param.length == 0 || param == '') && !ignoreWarn){
						var errInfo = me.GridUtil.getUnFinish(grids[0]);
						if(errInfo.length > 0){
							errInfo = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
						warnMsg(errInfo, function(btn){
							if(btn == 'yes' || btn == 'ok'){
								params = unescape("[" + param.toString() + "]");
								mm.update(r, params, extra);
							} 
						});
						}else return;																
					} else {
						params = unescape("[" + param.toString() + "]");
						mm.update(r, params, extra);
					}
				} else {
					mm.update(r, '[]', extra);
				}
			} else {
				mm.update(r, params, extra);
			}
		}else{
			mm.FormUtil.checkForm(form);
		}
	},
	update: function(){
		var me = this, params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.param = unescape(arguments[1].toString());
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			if (arguments[i] != null)
				params['param' + i] = unescape(arguments[i].toString());
		}
		var form = Ext.getCmp('form'), url = form.updateUrl;
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
					//update成功后刷新页面进入可编辑的页面
					var u = String(window.location.href);
					if (u.indexOf('formCondition') == -1) {
						var value = r[form.keyField];
						var formCondition = form.keyField + "IS" + value ;
						var gridCondition = '';
						var grid = Ext.getCmp('grid');
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + value;
						}
						if(me.FormUtil.contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
					} else {
						window.location.reload();
					}
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					//返回的消息为json则显示msg
					try{
						var result = Ext.JSON.decode(str);
						var msg = Ext.create('Ext.window.MessageBox', {
						   	buttonAlign:'center',
	                    	layout: {
	                    		type: 'vbox',
							    align: 'center'
							},
						     buttons: [
						     {text: '仅修改UAS密码',
						     handler:function(btn){
						      	var msg = btn.up('window');
						      	msg.close();
						      	form.getForm().findField("synchronize").setValue("0");
						      	me.onUpdate();
						     }},
						     {text: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;取&nbsp;&nbsp;&nbsp;&nbsp;消&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;',
						     handler:function(btn){
						      	var msg = btn.up('window');
						      	msg.close();
						     }}
						    ]
						});
						msg.show({
						    title:'修改失败',
	                    	msg:result.error,
						    width:330,
					     	height: 250
						});
					}catch(e){
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							//update成功后刷新页面进入可编辑的页面 
							var u = String(window.location.href);
							if (u.indexOf('formCondition') == -1) {
								var value = r[form.keyField];
								var formCondition = form.keyField + "IS" + value ;
								var gridCondition = '';
								var grid = Ext.getCmp('grid');
								if(grid && grid.mainField){
									gridCondition = grid.mainField + "IS" + value;
								}
								if(me.FormUtil.contains(window.location.href, '?', true)){
									window.location.href = window.location.href + '&formCondition=' + 
									formCondition + '&gridCondition=' + gridCondition;
								} else {
									window.location.href = window.location.href + '?formCondition=' + 
									formCondition + '&gridCondition=' + gridCondition;
								}
							} else {
								window.location.reload();
							}
						}
						showError(str);
						return;
					}
				} else {
					updateFailure();
				}
			}
		});
	}
});