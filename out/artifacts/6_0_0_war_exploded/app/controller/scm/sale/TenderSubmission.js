Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.TenderSubmission', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'scm.sale.TenderSubmission','scm.sale.TenderSubmissionFormPanel','scm.sale.TenderSubmissionGridPanel','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit',
    		'core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.FileField2'
    ],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpTenderSubmissionGridPanel': {
    			afterrender:function(grid){
    				var params = new Object();
    				params.id = id;
    				Ext.Ajax.request({
			        	url : basePath + 'scm/sale/getTenderSubmission.action',
			        	params: params,
			        	method : 'post',
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.exception || res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        			return;
			        		}else{
				        		var form = grid.ownerCt.down('form');
				        		if(res.saleTender.ifAll&&res.saleTender.ifAll==1){
				        			Ext.getCmp('cycle').show();
				        			Ext.getCmp('taxrate').show();
				        		}else{
				        			form.remove('cycle');
				        			form.remove('taxrate');
				        		}
				        		Ext.Array.each(form.items.items,function(field){
				        			if(field.name=='tendattachs'){
				        				field.setValue(res.tendattachs);
				        			}
				        			if(typeof(field.setValue)=='function'){
				        				if(field.group==1){
				        					field.setValue(res.saleTender[field.name]);
				        				}else{
				        					if(field.xtype=='datefield'){
				        						field.setValue(new Date(res.saleTender.enterpriseBaseInfo[field.name]));
				        					}else{
				        						field.setValue(res.saleTender.enterpriseBaseInfo[field.name]);
				        					}
				        				}
				        			}
				        			if(field.xtype=='mfilefield2'){
			        					field.items.items[0].resetOriginalValue();
			        				}else if(typeof(field.resetOriginalValue)=='function'){
			        					field.resetOriginalValue();
			        				}
				        		});
				        		if(typeof(readOnly)!='undefined'){
				        			var main = parent.Ext.getCmp("content-panel");
				        			if(main){
										var panel = main.getActiveTab(); 
										if(panel){
											code = res.saleTender.code;
											panel.setTitle('投标单('+code+')');
										}
									}
				        		}
				        		
				        		var status = res.saleTender['st_statuscode'];
				        		var overdue = res.saleTender['overdue'];
				        		var result = res.saleTender['result'];
				        		var ifAll = res.saleTender['ifAll'];
				        		if(gridCondition!=null){
				        			Ext.ComponentQuery.query('erpCloseButton')[0].hide();
				        		}
				        		if(typeof(readOnly)=='undefined'){
									if (!overdue&&status == 'ENTERING'&&gridCondition==null) {
										var save = Ext.getCmp('save');
					        			var submit = Ext.getCmp('submit');
										save.show();submit.show();
									}
									if (!overdue &&status == 'COMMITED'&&gridCondition==null) {
										var resSubmit = Ext.getCmp('resSubmit');
					        			var audit = Ext.getCmp('auditbutton');
										resSubmit.show();audit.show();
									}
									if (!overdue &&status == 'AUDITED'&&gridCondition==null) {
										var resAudit = Ext.getCmp('resAudit');
										resAudit.show();
									}
				        		}
				        		if(ifAll&&ifAll==1){
				        			grid.columns[8].hide();
				        		}
								if(result){
									grid.columns[11].show();
									grid.columns[12].show();
								}
				        		grid.store.loadData(res.saleTender.saleTenderItems);
				        		grid.store.sort({
									property: 'index',
									direction: 'ASC'
								});
			        		}
			        	}
    				});
    			}
    		},
    		'form field[name=taxrate]' : {
    			change: function(field,newvalue,oldvalue){
    				var store = field.ownerCt.ownerCt.down('gridpanel').store;
    				store.each(function(record){
    					record.set('taxrate',newvalue);
    				});
    			}
    		},
    		'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					me.onSave(form);
				}
			},
			'erpSubmitButton' : {
				click : function(btn) {
					var field = Ext.getCmp('id');
					var form = me.getForm(btn);
					var grid = Ext.getCmp('productGrid');
					me.getUnFinish(grid,form,field.value);
				}
			},
			'erpResSubmitButton' : {
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('id').value);
				}
			},
			'erpAuditButton' : {
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('id').value);
				}
			},
			'erpResAuditButton' : {
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('id').value);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onSave: function(form,s,attachs,param){
		var me = this,url = form.saveUrl,grid = Ext.getCmp('productGrid');
		if(!s&&!attachs&&!param){
			var store = me.getGridStore(grid);
			var s = me.checkFormDirty(form);
			var ifAll = Ext.getCmp('ifAll');
			
			if(ifAll&&ifAll.value==1){
				if(store.length<grid.store.getCount()){
					showError('此招标模式为全包，请对所有明细进行投标！');
					return;
				}
			}else{
				if(store.length<1){
					showError('请选择至少一个产品进行投标！');
					return;
				}
			}
			
			var param = me.GridUtil.getGridStore(grid);
			var tenderAttaches = Ext.getCmp('attachs').items.items[0];
			var attachs =null;
			if(tenderAttaches.wasDirty){
				if(tenderAttaches.value){
					attachs=tenderAttaches.value;
				}else{
					attachs = "clearAll";
				}
			}
			if(s.length<1&&param.length<1&&attachs==null){
				showError('未修改数据！');
				return;
			}
		}
		var formStore = new Object();
		var enBaseInfo = new Object();
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(s[1]&&item.group==2){
					if(item.xtype == 'numberfield'){
						//number类型赋默认值，不然sql无法执行
						if(item.value == null || item.value == ''){
							enBaseInfo[item.name]=0;
						}
					}else if(item.xtype == 'datefield'){
						enBaseInfo[item.name]=new Date(item.value).getTime();
					}else{
						enBaseInfo[item.name]=item.value;
					}
				}else if(item.group==1&&item.logic!='ignore'){
					if(item.xtype == 'numberfield'){
						//number类型赋默认值，不然sql无法执行
						if(item.value == null || item.value == ''){
							formStore[item.name]=0;
						}else{
							formStore[item.name]=item.value;
						}
					}else{
						formStore[item.name]=item.value;
					}
				}
			});
		}
		var params = new Object();
		params.caller = caller;
		params.formStore=unescape(escape(Ext.JSON.encode(formStore)));
		if(s[1]){
			params.enBaseInfo=unescape(escape(Ext.JSON.encode(enBaseInfo)));
		}
		if(param.length>0){
			params.param = '['+unescape(param.toString())+']';
		}
		if(attachs){
			params.attachs=attachs;
		}
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					saveSuccess(function(){
						//add成功后刷新页面进入可编辑的页面 
						if(contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&id=' + id;
						} else {
							window.location.href = window.location.href + '?id=' + id;
						}
					});
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					showError(str);
					return;
				} else{
					saveFailure();//@i18n/i18n.js
				}
			}

		});
	},
	checkFormDirty: function(form,group){
		var s = new Array();
		form.getForm().getFields().each(function (item,index, length){
			if(item.logic!='ignore'&&item.xtype != 'mfilefield2'){
				var value = item.value == null ? "" : item.value;
				if(item.xtype == 'htmleditor') {
					value  = item.getValue();
				}
				item.originalValue = item.originalValue == null ? "" : item.originalValue;

				if(Ext.typeOf(item.originalValue) != 'object'){
					if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
						var label = item.fieldLabel || item.ownerCt.fieldLabel ||
						item.boxLabel || item.ownerCt.title;//针对fieldContainer、radio、fieldset等
						if(label){
							if(item.group){
								s[item.group-1] = s[item.group-1] + '&nbsp;' + label.replace(/&nbsp;/g,'');
							}else{
								s[0] = s[0] + '&nbsp;' + label.replace(/&nbsp;/g,'');
							}
						}
					}
				}
			}
		});
		return s;
	},
	getUnFinish:function(grid,form,id){
		var me = this;
		var errInfo = me.GridUtil.getUnFinish(grid);
		if(errInfo.length > 0){
			errInfo = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
			warnMsg(errInfo, function(btn){
				if(btn == 'yes'){
					me.onSubmit(grid,form,id);
				} else {
					return;
				}
			});
		}else{
			me.onSubmit(grid,form,id);
		}
	},
	onSubmit: function(grid,form,id){
		var me = this;
		var store = me.getGridStore(grid);
		var s = me.checkFormDirty(form);
		var ifAll = Ext.getCmp('ifAll');
		
		if(ifAll&&ifAll.value==1){
			if(store.length<grid.store.getCount()){
				showError('此招标模式为全包，请对所有明细进行投标！');
				return;
			}
		}else{
			if(store.length<1){
				showError('请选择至少一个产品进行投标！');
				return;
			}
		}
		var param = me.GridUtil.getGridStore(grid);
		var tenderAttaches = Ext.getCmp('attachs').items.items[0];
		var attachs =null;
		if(tenderAttaches.wasDirty){
			if(tenderAttaches.value){
				attachs=tenderAttaches.value;
			}else{
				attachs = "clearAll";
			}
		}
		if(s.length>0||param.length>0||attachs!=null){
			Ext.MessageBox.show({
				title:'保存修改?',
				msg: '该单据已被修改,提交前要先保存吗？',
				buttons: Ext.Msg.YESNOCANCEL,
				icon: Ext.Msg.WARNING,
				fn: function(btn){
					if(btn == 'yes'){
						me.onSave(form,s,attachs,param);
					} else if(btn == 'no'){
						me.submit(form,id);	
					} else {
						return;
					}
				}
			});
		}else{
			me.submit(form,id);
		}
	},
	submit: function(form,id){
		var me = this;
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + form.submitUrl,
			params: {
				id: id,
				caller:caller
			},
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
				}
				if(localJson.success){
					me.FormUtil.getMultiAssigns(id, caller,form);
				}
			}
		});
	},
	getGridStore: function(grid){
		var me = this,jsonGridData = new Array();
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			var total=0;
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(!me.GridUtil.isBlank(grid, data)){
					Ext.each(grid.columns, function(c){
						if(c.dataIndex=='totalprice'){
							total += data[c.dataIndex];
						}
						if((c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
			var totalMoney = Ext.getCmp('totalMoney');
			if(totalMoney){
				totalMoney.setValue(total);
			}
		}
		return jsonGridData;
	}
});