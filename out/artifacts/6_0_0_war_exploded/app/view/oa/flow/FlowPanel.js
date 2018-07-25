/**
 * 流程界面panel基类
 */
Ext.define('erp.view.oa.flow.FlowPanel',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.FlowPanel',
	layout : 'column',
	style:'background:#e9e9e9',
	cls:'flow_panel_style',
	bodyStyle:'background:#e9e9e9;border-top:none;padding-top:5px;',
	autoScroll : true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	initComponent : function(){ 
		var me = this;	
		me.caller = caller;
		me.formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		//第一个标签页的panel
		if(me._first){
			me.tbar = {xtype:'FlowToolbar',_first:true}
		}
		//新增时的panel
		if(me._add){
			me.tbar = {xtype:'FlowToolbar',_add:true}
		}
		//跳转按钮属性为 派生任务 时插入的panel
		if(me._btnType=='Task'){
			me._group = null;//不配置group逻辑
			formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
			var id = formCondition.split('=')[1];
			Ext.Ajax.request({
				url : basePath + 'oa/flow/getDerive.action',
				async: false,
				params:{
					caller : caller,
					id : id,
					foid : me._foid
				},
				callback : function(options,success,response){
					var rs = new Ext.decode(response.responseText);
					if(rs.exceptionInfo){
						showError(rs.exceptionInfo);return;
					}
					me.formCondition = null;
					me.transfer = rs.transfer;
					me._codevalue = rs.codevalue
				}
			});
		}
		//跳转按钮属性为 派生流程 时插入的panel
		if(me._btnType=='Flow'){
			me._group = null;//不配置group逻辑
			formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
			var id = formCondition.split('=')[1];
			Ext.Ajax.request({
				url : basePath + 'oa/flow/getDerive.action',
				async: false,
				params:{
					caller : caller,
					id : id,
					foid : me._foid
				},
				callback : function(options,success,response){
					var rs = new Ext.decode(response.responseText);
					if(rs.exceptionInfo){
						showError(rs.exceptionInfo);return;
					}
					me.caller = rs.transfer.FlowCaller;
					me.btnid = rs.transfer.btnid;
					me.nodeId = rs.transfer.nodeId;
					me.groupname = rs.transfer.groupname;
					me.formCondition = null;
				}
			});
		}
		this.callParent(arguments);
		if(me._first||me._add){
			this.addKeyBoardEvents();
		}
	},
	listeners:{
		beforerender:function(form){
			var p = Ext.getCmp('flow_mainpanel');
			if(p) p.setLoading(true);
			var params = {
				caller : form.caller,
				condition : form.formCondition
			}
			form.getItemsAndButtons(form,'common/singleFormItems.action',params);
		},
		afterload:function(form){
			var p = Ext.getCmp('flow_mainpanel');
			if(p){p.setLoading(false);}
		}
	},
	getItemsAndButtons: function(form, url, param){
		var me = form;
		me.setLoading(true);
		Ext.Ajax.request({//拿到form的items
			url : basePath + url,
			params: param,
			method : 'post',
			async:false,
			callback : function(options, success, response){
				me.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				form.fo_id = res.fo_id;
				form.fo_keyField = res.keyField;
				form.tablename = res.tablename;//表名
				if(res.keyField){//主键
					form.keyField = res.keyField;
				}
				if(res.statusField){//状态
					form.statusField = res.statusField;
				}
				if(res.statuscodeField){//状态码
					form.statuscodeField = res.statuscodeField;
				}
				if(res.codeField){//Code
					form.codeField = res.codeField;
				}
				if(res.dealUrl){
					form.dealUrl = res.dealUrl;
				}
				if(res.mainpercent && res.detailpercent){
					form.mainpercent = res.mainpercent;
					form.detailpercent = res.detailpercent;
				}
				form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
				//data&items
				var items = me.setItems(form, res.items, res.data, res.limits, {
					labelColor: res.necessaryFieldColor
				});
				//分组特殊处理
				var groupitems = new Array();
				if(form._group){
					var _items = new Array();//取流程字段配置--flow_groupconfig
					Ext.Ajax.request({
						url : basePath + 'oa/flow/getGroupConfig.action',
						async: false,
						params:{
							caller : caller,
							groupName : form._group.name
						},
						callback : function(options,success,response){
							var rs = new Ext.decode(response.responseText);
							if(rs.exceptionInfo){
								showError(rs.exceptionInfo);return;
							}
							_items = rs.data;
						}
					});
					Ext.Array.each(_items, function(_item){
						Ext.Array.each(items, function(item){
							//不是新增的界面不显示附件字段
							if(!form._add){
								if(item.logic == 'file'){
									return;
								}
							}
							//新增界面默认加载主键和编号字段
							if(form.keyField){
								if(_item.FGC_FIELD.toLocaleUpperCase()==form.keyField.toLocaleUpperCase()){
									if(_item.FGC_FIELD==item.id){
										//宽度调整
										if(_item.FGC_WIDTH!=null&&_item.FGC_WIDTH>=1&&_item.FGC_WIDTH<=4){
											var num = parseInt(_item.FGC_WIDTH);
											item.columnWidth = num * 0.25;
										}
										item.margin = '0 0 5 0';
										item._detno = _item.FGC_DETNO;
										item.readOnly = true;
										item.fieldStyle = 'background:#e0e0e0;';
										if(!form._edit&&!form._add){
											item.id = item.id+'_'+form._group.name;
										}
										groupitems.push(item);
										return;
									}
								}
							}
							if(form.codeField){
								if(_item.FGC_FIELD.toLocaleUpperCase()==form.codeField.toLocaleUpperCase()){
									if(_item.FGC_FIELD==item.id){
										//宽度调整
										if(_item.FGC_WIDTH!=null&&_item.FGC_WIDTH>=1&&_item.FGC_WIDTH<=4){
											var num = parseInt(_item.FGC_WIDTH);
											item.columnWidth = num * 0.25;
										}
										item.margin = '0 0 5 0';
										item._detno = _item.FGC_DETNO;
										item.readOnly = true;
										item.fieldStyle = 'background:#e0e0e0;';
										if(!form._edit&&!form._add){
											item.id = item.id+'_'+form._group.name;
										}
										groupitems.push(item);
										return;
									}
								}
							}
							if(item.id==_item.FGC_FIELD){
								//标签字段
							    if(item.xtype=='displayfield'){
							    	item.fieldLabel = null;
							    }
								//隐藏类型字段不处理
								if(item.xtype=="hidden"){
									item.id = item.id+'_'+form._group.name;
									groupitems.push(item);
									return;
								}
								//序号刷新
								item._detno = _item.FGC_DETNO;
								item.margin = '0 0 5 0';
								item.labelAlign = 'right'
								if(!item.allowBlank){
									item.labelStyle = 'color:black';
								}
								if(!form._edit&&!form._add){
									item.id = item.id+'_'+form._group.name;
									//复合字段id
									if(item.secondname!=''){
										item.secondname = item.secondname+'_'+form._group.name;
									}
								}
								if(form._edit||form._add){
									if(!item.readOnly){
										item.fieldStyle = 'background:#fff;';
									}
									//默认值
									if(form._edit&&_item.FGC_NEW!='true'&&item.value!=''){
									}
									//是否全新
									if(form._edit&&_item.FGC_NEW=='true'){
										item.value = '';
									}
									//是否只读
									if(_item.FGC_READ=='true'){
										item.readOnly = true;
										item.fieldStyle = 'background:#e0e0e0;';
									}
									//是否必填
									if(_item.FGC_REQUIREDFIELD=='true'){
										item.allowBlank = false;
										item.labelStyle = 'color:black';
										if(item.fieldLabel.indexOf('<font color')<0){
											item.fieldLabel = "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>"+item.fieldLabel;
										}
									}
								}else{
									item.readOnly = true;
									item.fieldStyle = 'background:#e0e0e0;';
								}
								//宽度调整
								if(_item.FGC_WIDTH!=null&&_item.FGC_WIDTH>=1&&_item.FGC_WIDTH<=4){
									var num = parseInt(_item.FGC_WIDTH);
									item.columnWidth = num * 0.25;
								}
								if(item.xtype=='datefield'){
									item.format = "Y-m-d"
								}
								groupitems.push(item);
							}
						});
					});
					//重新排序
					groupitems = me.sortItems(groupitems);
				}else{
					if(form._btnType=='Task'){//派生任务
						items = form.getTaskPanel(form);//派生任务固定字段
						var _items = new Array();//取派生任务映射字段配置--flow_transfer
						_items = form.transfer;
							Ext.Array.each(items, function(item){
								item.margin = '0 0 5 0';
								item.labelAlign = 'right';
								if(!item.allowBlank){
									item.labelStyle = 'color:black';
									item.fieldLabel = "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>"+item.fieldLabel;
								}
								if(!item.readOnly){
									item.fieldStyle = 'background:#fff;';
								}else{
									item.fieldStyle = 'background:#e0e0e0;';
								}
								if(item.xtype=='checkbox'){
									item.fieldStyle = null;
								}
								Ext.Array.each(_items, function(_item){
									if(item.name==_item.name){
										item.value = _item.value;
									}
								});
								if(item.name == 'sourcecode'){
									item.value = form._codevalue;
								}
								groupitems.push(item);
							});
					}else if(form._btnType=='Flow'){
							var _items = new Array();//取流程字段配置--flow_groupconfig
							Ext.Ajax.request({
								url : basePath + 'oa/flow/getGroupConfig.action',
								async: false,
								params:{
									caller : form.caller,
									groupName : form.groupname
								},
								callback : function(options,success,response){
									var rs = new Ext.decode(response.responseText);
									if(rs.exceptionInfo){
										showError(rs.exceptionInfo);return;
									}
									_items = rs.data;
								}
							});
							Ext.Array.each(_items, function(_item){
								Ext.Array.each(items, function(item){
									if(item.id==_item.FGC_FIELD){
										if(item.xtype=='multifield'||item.xtype=='dbfindtrigger'){
											item.dbCaller=form.caller;
										}
										//标签字段
									    if(item.xtype=='displayfield'){
									    	item.fieldLabel = null;
									    }
										item._detno = _item.FGC_DETNO;
										if(item.labelAlign!='top'){
											item.margin = '0 0 5 0';
										}
										if(!item.allowBlank){
											item.labelStyle = 'color:black';
										}
										if(!form._edit&&!form._add){
											item.id = item.id+'_'+form.groupname;
										}
										if(form._edit||form._add){
											if(!item.readOnly){
												item.fieldStyle = 'background:#fff;';
											}
											//是否全新
											if(_item.FGC_NEW=='true'){
												item.value = '';
											}
											//是否只读
											if(_item.FGC_READ=='true'){
												item.readOnly = true;
												item.fieldStyle = 'background:#e0e0e0;';
											}
											//是否必填
											if(_item.FGC_REQUIREDFIELD=='true'){
												item.allowBlank = false;
												item.labelStyle = 'color:black';
												if(item.fieldLabel.indexOf('<font color')<0){
													item.fieldLabel = "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>"+item.fieldLabel;
												}
											}
										}else{
											item.readOnly = true;
											item.fieldStyle = 'background:#e0e0e0;';
										}
										//宽度调整
										if(_item.FGC_WIDTH!=null&&_item.FGC_WIDTH>=1&&_item.FGC_WIDTH<=4){
											var num = parseInt(_item.FGC_WIDTH);
											item.columnWidth = num * 0.25;
										}
										if(item.xtype=='datefield'){
											item.format = "Y-m-d"
										}
										groupitems.push(item);
									}
								});
							});
							//重新排序
							groupitems = me.sortItems(groupitems);
					}
				}
				form.add(groupitems);
				//form第一个可编辑框自动focus
				me.FormUtil.focusFirst(form);
				form.fireEvent('afterload', form);
			}
		});
	},
	beforeSave: function(form, url,id,btnid){
		if(!form.checkForm(form)){
			return;
		}
		if(form.keyField&&!id){
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				form.getSeqId(form);
			}
		}
		if(form.codeField&&Ext.getCmp(form.codeField)){
			if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
				this.BaseUtil.getRandomNumber(caller,2,form.codeField);;//自动添加编号
			}
		}
		var param = [];
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			if(id){
				r[form.keyField]=id;
			}
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){
					//number类型赋默认值，不然sql无法执行
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
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			form.save(r,form,url,id,btnid);
		}else{
			form.checkForm(form);
		}
	},
	save: function(r,form,url,id,btnid){
		var params = new Object();
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.nodeId = nodeId;
		params.id = id?id:r[form.keyField];//单据id
		params.caller = caller;
		params.btnid = btnid;
		params.Status = status;
		form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					SaveOneButton('保存成功！', function(btn){
						//add成功后刷新页面进入查看页面 
						var formCondition = form.keyField + "IS" + params.id ;
						if(form.contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition;
						}
					});
				} else {
					showError(localJson.exceptionInfo);
				}
			}
		});
	},
	saveInstance: function(form,url,id,btnid){
		//派生流程来源url
		var sourceUrl = window.location.href;
		sourceUrl = sourceUrl.substring(sourceUrl.indexOf('ERP/')+4,sourceUrl.length);
		//生成派生流程的id
		var flowid = form.getSeqIdByCaller(form.caller)
		if(form.codeField&&Ext.getCmp(form.codeField)){
			if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
				this.BaseUtil.getRandomNumber(caller,2,form.codeField);;//自动添加编号
			}
		}
		if(!form.checkForm(form)){
			return;
		}
		var param = [],r;
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			r = form.getValues();
			r[form.keyField]=flowid;
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){
					//number类型赋默认值，不然sql无法执行
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
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
		}else{
			form.checkForm();
		}
		var params = new Object();
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.nodeId = form.nodeId;//派生流程nodeid
		params.preKeyValue = id;//当前单据id
		params.id = flowid;//派生流程的id
		params.preNodeId = nodeId;//当前nodeid
		params.caller = form.caller;//派生流程caller
		params.preCaller = caller;//单前的caller
		params.btnid = btnid;
		params.url = sourceUrl;//来源单据的url地址
		form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					saveSuccess(function(){
						//add成功后刷新页面进入查看页面 
						var formCondition = form.keyField + "IS" + params.id ;
						if(form.contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition;
						}
					});
				} else {
					showError(localJson.exceptionInfo);
				}
			}
		});
	},
	saveTask: function(form){
		var me = this,status=form.down('field[name=statuscode]'),url='plm/task/saveFormTask.action';
		var taskname = form.down('field[name=name]')
		var start = form.down('field[name=startdate]'),
		end = form.down('field[name=enddate]'),
		dur = form.down('field[name=duration]'),
		name = form.down('field[name=resourcename]'),taskId=form.down('field[name=id]').getValue();
		var title = Ext.getCmp('flow_mainpanel').title;
		var flowname = title.split('-')[0];
		taskname.setValue(flowname + '-' + taskname.getValue());
		dur.setValue(Ext.Number.toFixed((end.getValue().getTime() - start.getValue().getTime())/(1000*60*60), 2));
		var v = form.getValues();
		Ext.each(Ext.Object.getKeys(v), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete v[k];
			}
		});
		Ext.Ajax.request({
			url : basePath + url,
			params : {
				formStore : unescape(Ext.encode(v).replace(/\\/g,"%"))
			},
			callback : function(opt, s, res) {
				form.setLoading(false);				
				if (res.responseText=='success') {
					Ext.Msg.alert('提示','派生任务添加成功！');
				} else {
					var r = Ext.decode(res.responseText);
					showError(r.exceptionInfo);
				}
			}
		});
	},
	onClose:function(){
		var main = parent.Ext.getCmp("content-panel"),bool = false; 
			if(main){
				bool = true;
				main.getActiveTab().close();
			} else {
				var win = parent.Ext.ComponentQuery.query('window');
				if(win){
					Ext.each(win, function(){
						this.close();
					});
				} else {
					bool = true;
					window.close();
				}
			}
			if(!bool){//如果还是没关闭tab，直接关闭页面
				window.close();
			}
	},
	getSeqId: function(form){
		Ext.Ajax.request({
			url : basePath + 'common/getCommonId.action?caller=' +caller,
			method : 'get',
			async: false,
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					Ext.getCmp(form.keyField).setValue(rs.id);
				}
			}
		});
	},
	checkForm: function(form){
		var s = '';
		form.getForm().getFields().each(function (item, index, length){
			if(!item.isValid()){
				if(s != ''){
					s += ',';
				}
				if(item.fieldLabel || item.ownerCt.fieldLabel){
					s += item.fieldLabel || item.ownerCt.fieldLabel;
				}
			}
		});
		if(s == ''){
			return true;
		}
		showError($I18N.common.form.necessaryInfo1 + '(<font color=green>' + s.replace(/&nbsp;/g,'') + 
				'</font>)' + $I18N.common.form.necessaryInfo2);
		return false;
	},
	contains: function(string, substr, isIgnoreCase){
		if (string == null || substr == null) return false;
		if (isIgnoreCase === undefined || isIgnoreCase === true) {
			string = string.toLowerCase();
			substr = substr.toLowerCase();
		}
		return string.indexOf(substr) > -1;
	},
	getRandomNumber: function(form){
		Ext.Ajax.request({
	   		url : basePath + 'common/getCodeString.action',
	   		async: false,//同步ajax请求
	   		params: {
	   			caller: caller,//如果table==null，则根据caller去form表取对应table
	   			type: 2
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				Ext.getCmp(form.codeField).setValue(localJson.code);
	   			}
	   		}
		});
	},
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
					forms = Ext.ComponentQuery.query('form'), 
					grids = Ext.ComponentQuery.query('gridpanel'),
					formSet = [], gridSet = [];
					if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
					}
					if(grids.length > 0) {
						Ext.Array.each(grids, function(g){
							gridSet.push(g.caller || window.caller);
						});
						gridSet = Ext.Array.unique(gridSet);
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					//用于生成datalist 时要用的sn_lockpage
					var main = parent.Ext.getCmp("content-panel");
					if(!main){
						main = parent.parent.Ext.getCmp("content-panel");
					}
					if(main){
						main.lockPage = window.location.pathname.replace('/ERP/', '').split("?")[0];
					}
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
	},
	getTaskPanel:function(form){
		var codeValue,keyValue;
		if (form.codeField) {
			var c = form.down('#' + form.codeField);
			if (c) {
				codeValue=c.getValue();
			}
			var u = new String(window.location.href);
			u = u.substr(u.indexOf('jsps'));
			url=u;
		}
		if(form.keyField){
			var c=form.down('#'+form.keyField);
			if(c) keyValue=c.getValue();
		}
		return [{
			fieldLabel: '任务名称',
			name: 'name',
			allowBlank: false,
			columnWidth:0.25,
			xtype:'textfield'
		},{
			fieldLabel: '开始时间',
			xtype:'datefield',
			columnWidth:0.25,
			name: 'startdate',
			value:new Date(),
			allowBlank: false
		},{
			fieldLabel:'结束时间',
			xtype:'datefield',
			columnWidth:0.25,
			name:'enddate',
			allowBlank:false
		},{
			xtype:'hidden',
			name:'duration'
		},{
			fieldLabel:'执行人',
			xtype:'dbfindtrigger',
			columnWidth:0.25,
			name:'resourcename',
			id:'resourcename',
			allowBlank:false
		},{
			xtype:'hidden',
			name:'resourcecode',
			id:'resourcecode',
			columnWidth:0
		},{
			fieldLabel:'确认人',
			columnWidth:0.25,
			xtype:'dbfindtrigger',
			hidden:true,
			name:'recorder',
			id:'recorder'
		},{
			fieldLabel:'确认人ID',
			xtype:'hidden',
			name:'recorderid'
		},{
			xtype:'textareafield',
			fieldLabel:'任务描述',
			name:'description',
			columnWidth:1,
			allowBlank:false
		},{
			xtype : 'hidden',
			name : 'sourcelink',
			value:url
		},{
			xtype : 'hidden',
			name : 'sourcecode',
			value:codeValue
		},{
			xtype : 'hidden',
			name : 'sourcecaller',
		    value:caller||form.caller
		},{
			xtype:'hidden',
			name:'sourceid',
			value:keyValue
		},{
		   xtype:'hidden',
		   name:'statuscode'
		},{
			xtype:'hidden',
			name:'id'
		}]
	},
	getSeqIdByCaller: function(caller){
		var id;
		Ext.Ajax.request({
			url : basePath + 'common/getCommonId.action?caller=' +caller,
			method : 'get',
			async: false,
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					id = rs.id;
				}
			}
		});
		return id;
	},
	update : function(form){
		var r = form.getValues();
		formStore = unescape(escape(Ext.JSON.encode(r)));
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var id = formCondition.split('=')[1];
		Ext.Ajax.request({
			url : basePath + 'oa/flow/update.action',
			async: false,
			params: {
				formStore : formStore,
				caller : caller,
				id: id
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					SaveOneButton('更新成功！', function(btn){
						window.location.reload();
					});
				}
			}
		});
	},
	sortItems : function(items){
		//重新排序
		var groupitems = new Array();
		var unSetItems = new Array();
		var sortItems = new Array();
		Ext.Array.each(items, function(item){
			if(item._detno){
				sortItems.push(item)
			}else{
				unSetItems.push(item)
			}
		});
		var len = sortItems.length;
	    for (var i = 0; i < len; i++) {
	        for (var j = 0; j < len - 1 - i; j++) {
	            if (sortItems[j]._detno > sortItems[j+1]._detno) {        //相邻元素两两对比
	                var temp = sortItems[j+1];        //元素交换
	                sortItems[j+1] = sortItems[j];
	                sortItems[j] = temp;
	            }
	        }
	    }
	    groupitems.push(sortItems);
	    groupitems.push(unSetItems);
	    return groupitems;
	},
	setItems: function(form, items, data, limits, necessaryCss){
		var me = this,edit = !form.readOnly,hasData = true,limitArr = new Array();
		if(limits != null && limits.length > 0) {//权限外字段
			limitArr = Ext.Array.pluck(limits, 'lf_field');
		}
		if (data) {
			data = Ext.decode(data);
			if(form.statuscodeField && data[form.statuscodeField] != null && data[form.statuscodeField] != '' &&  
					['ENTERING', 'UNAUDIT', 'UNPOST', 'CANUSE'].indexOf(data[form.statuscodeField]) == -1){//非在录入和已提交均设置为只读// && data[form.statuscodeField] != 'COMMITED'
				form.readOnly = true;
				edit = false;
			}
			if(form.statusCode && data[form.statusCode] == 'POSTED'){//存在单据状态  并且单据状态不等于空 并且 单据状态等于已过账
				form.readOnly = true;
				edit = false;
			}
		} else {
			hasData = false;
		}
		me.FormUtil.setItemWidth(form, items);
		
		Ext.each(items, function(item){
			if(item.labelAlign&&item.labelAlign!='top'){
				item.labelAlign = 'right';
			}
			if(item.labelAlign=='top'){
				item.margin = '3 0 3 30';
			}
			if(item.group == '0'){
				item.margin = '7 0 0 0';
			}
			//基本背景颜色
			item.labelStyle = "color:#1e1e1e";
			item.fieldStyle = 'background:#fff;color:#313131;';
			if(item.xtype == 'textareafield'){
				item.grow=true;
				item.growMax=300;
			}
			if(!item.allowBlank && item.fieldLabel && necessaryCss.labelColor) {//必填
				/*item.labelStyle = 'color:#' + necessaryCss.labelColor;*/
				item.fieldLabel = "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>"+item.fieldLabel;
				if(item.xtype=='mfilefield'){//附件类型必填设置title颜色
					 this.title = '<font color=#'+necessaryCss.labelColor+'>'+this.title|| '附件'+'</font>' ;
				}
			}
			if(item.readOnly) {
				item.fieldStyle = 'background:#f3f3f3;';
			}
						
			if(item.name != null) {
				if(item.name == form.statusField){//状态加特殊颜色
					item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
				} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
					item.xtype = 'hidden';
				}
			}
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
				item.width = 0;
				item.margin = '0';
			}
			if(item.xtype == 'checkbox') {
				item.fieldStyle = '';
		        item.margin = '3 0 3 80';
		        if(item.columnWidth<0.25){
					 item.margin = '3 0 3 0';
				}
				item.focusCls = '';
			}
			if(item.maskRe!=null){
				item.maskRe=new RegExp(item.regex);
			}
			if (hasData) {
				if(data[item.name]!=''){
					item.value = data[item.name];
				}
				if(item.secondname){//针对合并型的字段MultiField
					item.secondvalue = data[item.secondname];
				}
				if(!edit){
					form.readOnly = true;
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				} 
				if(item.xtype == 'checkbox'){
					item.checked = Math.abs(item.value || 0) == 1;
					item.fieldStyle = '';
					item.margin = '3 0 3 80';
					if(item.columnWidth<0.25){
						 item.margin = '3 0 3 0';
					}
				}
			}else{
				if(form.source=='allnavigation'){
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				}
			}
			if(limitArr.length > 0 && Ext.Array.contains(limitArr, item.name)) {
				item.hidden = true;
			}
			if(item.renderfn){
				var args = new Array();
    			var arr = item.renderfn.split(':');
    			//hey start 主表字段背景颜色
    			if(arr&&arr[0]!='itemstyle'){//判断是否是itemstyle
    				if(contains(item.renderfn, ':', true)){	    		
    					Ext.each(item.renderfn.split(':'), function(a, index){
    						if(index == 0){
    							renderName = a;
    						} else {
    							args.push(a);
    						}
    					});
    				} else {renderName=item.renderfn;}
    				me[renderName](item, args, form);
    			}
    			else{
    		    	switch(arr.length)
    		    	{
    		    		case 2:						
    		    			if(data&&data[item.name]&&data[item.name]==arr[1]) item.fieldStyle = item.fieldStyle + ';background:#c0c0c0;';
    		    			break;
    		    		case 3:	 
    		    			if(data&&data[item.name]&&data[item.name]==arr[1]) item.fieldStyle = item.fieldStyle + ';background:'+arr[2]+';';
    		    			break;
    		    		default:
    		    	}	
    			}
    			//hey end 主表字段背景颜色
			}
		});
		return items;
	}
});