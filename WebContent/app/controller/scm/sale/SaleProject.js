Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SaleProject', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.SaleProject','core.form.Panel','core.form.MultiField',
    		'core.button.Add','core.button.Save','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
    		'core.button.Audit','core.button.ResAudit','core.button.Close','core.button.TurnSale','core.form.FileField',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.form.YnField','core.form.CheckBoxGroup','core.form.CheckBoxContainer',
			'core.form.RadioGroup','core.form.SplitTextField','core.grid.ItemGrid','core.button.TurnProject'
		
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpTurnProject':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('sp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				warnMsg("确定要转入立项申请吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						Ext.Ajax.request({
    					   		url : basePath + 'scm/sale/turnProject.action',
    					   		params: {
    					   			caller: caller,
    					   			id: Ext.getCmp('sp_id').value
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
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber(); //自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('sp_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
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
    				me.FormUtil.onAdd('addSaleProject', '新增订单评审', 'jsps/scm/sale/saleProject.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.onSubmit(Ext.getCmp('sp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('sp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sp_id').value);
				}
			},
//			'dbfindtrigger[name=ps_address]': {
//    			afterrender:function(trigger){
//	    			trigger.dbKey='ps_custcode';
//	    			trigger.mappingKey='cu_code';
//	    			trigger.dbMessage='请先选客户编号！';
//    			}
//    		},
    		'combo[name=ps_type]': {
    			delay: 200,
    			change: function(m){
					this.hidecolumns(m);

				}
    		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},

	
	/**
	 * 单据保存
	 * @param param 传递过来的数据，比如gridpanel的数据
	 */
	onSave: function(param){
		var me = this;
		var form = Ext.getCmp('form');
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
			if(!me.FormUtil.contains(form.saveUrl, '?caller=', true)){
				form.saveUrl = form.saveUrl + "?caller=" + caller;
			}
			me.save(r, param);
		}else{
			me.FormUtil.checkForm();
		}
	},
	save: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	var gridCondition = '';
		   		    	if(me.FormUtil.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition + '&gridCondition=' + gridCondition;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
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
			   		    	if(me.FormUtil.contains(window.location.href, '?', true)){
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
	/**
	 * 检查form未完善的字段
	 */
	checkForm: function(){
		var s = '';
		var form = Ext.getCmp('form');
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
	/**
	 * 单据修改
	 * @param form formpanel表
	 * @param param 传递过来的数据，比如gridpanel的数据
	 */
	onUpdate: function(me){
		var mm = this;
		var form = Ext.getCmp('form');
//		var s1 = mm.checkFormDirty(form);
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
			if(grids.length > 0){
				var param = grids[0].GridUtil.getGridStore();
				if(grids[0].necessaryField.length > 0 && (param == null || param == '')){
					warnMsg('明细表还未添加数据,是否继续?', function(btn){
						if(btn == 'yes'){
							params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
						} else {
							return;
						}
					});
				} else {
					params = unescape("[" + param.toString().replace(/\\/g,"%") + "]");
				}
			}
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
		var form = Ext.getCmp('form');
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
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
        			showError(str);return;
        		} else {
	   				updateFailure();
	   			}
	   		}
		});
	},
	/**
	 * @param allowEmpty 是否允许Grid为空
	 */
	onSubmit: function(id, allowEmpty){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
//			var s = '';
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0){//check所有grid是否已修改
				var param = grids[0].GridUtil.getAllGridStore(grids[0]);
				if(grids[0].necessaryField && grids[0].necessaryField.length > 0 && (param == null || param == '') && (allowEmpty !== true)){
					showError("明细表还未添加数据,无法提交!");
					return;
				}
				Ext.each(grids, function(grid, index){
					if(grid.GridUtil){
						var msg = grid.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
//							s = s + '<br/>' + grid.GridUtil.checkGridDirty(grid);
						}
					}
				});
			}
			me.FormUtil.submit(id);

		} else {
			me.checkForm();
		}
	}
});