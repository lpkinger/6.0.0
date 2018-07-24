Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectEvaluation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','plm.project.ProjectEvaluation',
      		'core.button.Add','core.button.Save','core.button.Update','core.button.Delete','core.button.Close',
      		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.Print',
      		'core.button.Upload','core.button.TurnProject','core.button.TurnProjEvalSTANDARD','core.button.TurnProjEvalCUSTOM',
      		'core.form.CheckBoxGroup','core.trigger.DbfindTrigger','core.form.MultiField','core.form.FileField',
      		'core.trigger.TextAreaTrigger','core.form.YnField','core.form.SpecialContainField','core.form.CheckBoxGroupEndwithS',     		
      		'core.button.DeleteDetail','core.toolbar.Toolbar','core.grid.ItemGrid','core.button.PrintPDF'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpTurnProject':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('pe_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				warnMsg("确定要转入立项申请吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						Ext.Ajax.request({
    					   		url : basePath + 'plm/project/turnProject.action',
    					   		params: {
    					   			caller: caller,
    					   			id: Ext.getCmp('pe_id').value
    					   		},
    					   		method : 'post',
    					   		callback : function(options,success,response){
    					   			me.FormUtil.getActiveTab().setLoading(false);
    					   			var localJson = new Ext.decode(response.responseText);
    					   			if(localJson.exceptionInfo){
    					   				showError(localJson.exceptionInfo);
    					   			}
    				    			if(localJson.success){
    				    				turnSuccess(function(){
    				    					var id = localJson.id;
    				    					var url = "jsps/plm/project/project.jsp?formCondition=prj_id=" + id;
    				    					me.FormUtil.onAdd('Project' + id, '立项申请' + id, url);
    				    				});
    					   			}
    					   		}
    						});
    					}
    				});
    			}
    		},
    		'erpPrintPDFButton': {
    			beforePrint:function(){
    				if(Ext.getCmp('pe_refamount')){
				 		Ext.getCmp('pe_refamount').hide();
				 	}
				 	if(Ext.getCmp('pe_amount')){
				 		Ext.getCmp('pe_amount').hide();
				 	}
    			}
    		},
    		'erpSaveButton': {
    			beforerender:function(btn){btn.id=''},
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			afterrender: function(btn){
					var status = Ext.getCmp('pe_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pe_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('pe_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProjectEvaluation', '新增项目评估表', 'jsps/plm/project/projectEvaluation.jsp?whoami='+caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			},
    			afterrender:function(){
    			var grids = Ext.ComponentQuery.query('gridpanel');
    			var type=Ext.getCmp('pe_type').value;
					Ext.each(grids,function(g,index){
						if(g.xtype=='itemgrid'&&type=='STANDARD'){
							g.readOnly=true;
						}
					});
    			}
    		},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pe_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pe_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pe_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pe_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pe_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pe_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pe_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pe_id').value);
				}
			},
			'erpTurnProjEvalCUSTOMButton':{
				afterrender:function(btn){
					var id=Ext.getCmp('pe_id').value;
					var status = Ext.getCmp('pe_statuscode');
					var type=Ext.getCmp('pe_type').value;
    				if((!id||id==0)||(type!='STANDARD')||(status && status.value != 'ENTERING')){
    					btn.hide();
    				}
				},
				click:function(btn){
					me.turn(Ext.getCmp('pe_id').value,'CUSTOM');
				}
			},
			'erpTurnProjEvalSTANDARDButton':{
				afterrender:function(btn){
					var id=Ext.getCmp('pe_id').value;
					var status = Ext.getCmp('pe_statuscode');
					var type=Ext.getCmp('pe_type').value;
    				if((!id||id==0)||(type!='CUSTOM')||(status && status.value != 'ENTERING')){
    					btn.hide();
    				}
				},
				click:function(btn){
					me.turn(Ext.getCmp('pe_id').value,'STANDARD');
				}
			}
    	});
    },
    turn:function(id,type){
    	var me=this;
    	var form = Ext.getCmp('form');
    	form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'plm/project/turn.action?caller=ProjectEvaluation'+type,
			params : {
				id:id,
				type:type
			},
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					turnSuccess(function(){
						var formCondition = form.keyField + "IS" + id ;
						var gridCondition = '';
						var grid = Ext.getCmp('grid');
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + id;
						}
						if(me.FormUtil.contains(window.location.href, '?', true)){
							if(type=='CUSTOM'){
								window.location.href = window.location.href.replace('ProjectEvaluationSTANDARD','ProjectEvaluationCUSTOM').replace('pe_type=STANDARD','pe_type=CUSTOM')+ '&formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							}else if(type=='STANDARD'){
								window.location.href = window.location.href.replace('ProjectEvaluationCUSTOM','ProjectEvaluationSTANDARD').replace('pe_type=CUSTOM','pe_type=STANDARD')+ '&formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							}
						} else {
							window.location.href = window.location.href + '?whoami=ProjectEvaluation'+type+'&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
					});
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showMessage("提示", str);
							auditSuccess(function(){
								window.location.reload();
							});
						} else {
							showError(str);return;
						}
				}
			}
		});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(me, arg){
		var me=this;
		var mm = me.FormUtil;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				mm.getSeqId(form);
			}
		}
		var grids = Ext.ComponentQuery.query('gridpanel');
		var removea = new Array();
		Ext.each(grids,function(g,index){
			if(g.xtype=='itemgrid'){
				g.saveValue();
				removea.push(g);
			}
		});
		Ext.each(removea,function(r,index){
			Ext.Array.remove(grids,r);
		});
		if(grids.length > 0 && !grids[0].ignore){
			var param = me.GridUtil.getGridStore();
			if(grids[0].necessaryField&&grids[0].necessaryField.length > 0 && (param == null || param == '')){
				var errInfo = me.GridUtil.getUnFinish(grids[0]);
				if(errInfo.length > 0)
					errInfo = '明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo;
				else
					errInfo = '明细表还未添加数据, 是否继续?';
				warnMsg(errInfo, function(btn){
					if(btn == 'yes'){
						me.onSave(param, arg);
					} else {
						return;
					}
				});
			} else {
				me.onSave(param, arg);
			}
		} else {
			me.onSave([]);
		}
	},
	onSave: function(param, arg){
		var me=this;
		var mm = me.FormUtil;
		var form = Ext.getCmp('form');
		param = param == null ? [] : "[" + param.toString() + "]";
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
			var reg = /[!@#$%^&*()'":,\/?]/;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				//codeField值强制大写,自动过滤特殊字符
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().toUpperCase().replace(reg, '');
				}
			});
			me.save(r, param, arg);
		}else{
			mm.checkForm();
		}
	},
	save: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.param = unescape(arguments[1].toString());
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			if(arguments[i])
				params['param' + i] = unescape(arguments[i].toString());
		}  
		var me = this;
		var form = Ext.getCmp('form'), url = form.saveUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		}
		var type=Ext.getCmp('pe_type').value;
		form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'plm/project/saveProjectEvaluation.action?caller=ProjectEvaluation'+type,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					saveSuccess(function(){
						//add成功后刷新页面进入可编辑的页面 
						var value =r[form.keyField];
						var formCondition = form.keyField + "IS" + value ;
						var gridCondition = '';
						var grid = Ext.getCmp('grid');
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + value;
						}
						if(me.FormUtil.contains(window.location.href, '?', true)){
							window.location.href = window.location.href.replace('ProjectEvaluationSTANDARD','ProjectEvaluation'+type)+ '&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						} else {
							window.location.href = window.location.href + '?whoami=ProjectEvaluation'+type+'&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
					});
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
						str = str.replace('AFTERSUCCESS', '');
						saveSuccess(function(){
							//add成功后刷新页面进入可编辑的页面 
							var value = r[form.keyField];
							var formCondition = form.keyField + "IS" + value ;
							var gridCondition = '';
							var grid = Ext.getCmp('grid');
							if(grid && grid.mainField){
								gridCondition = grid.mainField + "IS" + value;
							}
							if(me.contains(window.location.href, '?', true)){
								window.location.href = window.location.href + '&formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							} else {
								window.location.href = window.location.href + '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							}
						});
						showError(str);
					} else {
						showError(str);
						return;
					}
				} else{
					saveFailure();//@i18n/i18n.js
				}
			}

		});
	},
	onUpdate: function(me){
		var mm = this;
		var form = Ext.getCmp('form');
		var s2 = '';
		var grids = Ext.ComponentQuery.query('gridpanel');
		var removea = new Array();
		Ext.each(grids,function(g,index){
			if(g.xtype=='itemgrid'){
				g.updateValue();
				removea.push(g);
			}
		});
		Ext.each(removea,function(r,index){
			Ext.Array.remove(grids,r);
		});
		
		if(grids.length > 0){//check所有grid是否已修改
			Ext.each(grids, function(grid, index){
				if(grid.GridUtil){
					var msg = grid.GridUtil.checkGridDirty(grid);
					if(msg.length > 0){
						s2 = s2 + '<br/>' + grid.GridUtil.checkGridDirty(grid);
					}
				}
			});
		}
		if(form && form.getForm().isValid()){
			//form里面数据
			var r = form.getValues(false, true);
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!mm.FormUtil.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			var params = [];
			mm.update(r, params);
		}else{
			mm.checkForm(form);
		}
	},
	update: function(){
		var me = this, params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
		}
		var type=Ext.getCmp('pe_type').value;
		var form = Ext.getCmp('form');
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath +'plm/project/updateProjectEvaluation.action?caller=ProjectEvaluation'+type,
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
			   		    	window.location.href = window.location.href.replace('ProjectEvaluationSTANDARD','ProjectEvaluation'+type) + '&formCondition=' + 
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
				   		    	window.location.href = window.location.href + '?whoami=ProjectEvaluation'+type+'&formCondition=' + 
				   					formCondition + '&gridCondition=' + gridCondition;
				   		    }
	    				} else {
	    					window.location.reload();
	    				}
	   				}
        			showError(str);return;
        		} else {
	   				updateFailure();
	   			}
	   		}
		});
	}
});