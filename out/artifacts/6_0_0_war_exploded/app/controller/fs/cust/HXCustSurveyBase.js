Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.HXCustSurveyBase', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.HXCustSurveyBase', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField','core.button.Save',
			'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit','core.button.Audit','core.button.Close',
			'core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField',
			'core.form.FileField','core.button.CopyAll','core.button.ResetSync', 'core.button.RefreshSync'],
	init : function() {
		var me = this;
		this.control({
			'field[name=sb_caid]': {
				afterrender:function(field){
					if(formCondition){
						var id = formCondition.substring(formCondition.indexOf('=')+1);
						field.setValue(id);
					}
				}
			},
			'erpSaveButton': {
				afterrender:function(btn){
					if(readOnly==1){
						btn.hide();
					}
				},
    			click: function(btn){				
					me.beforeSave();			
    			}
        	}
		})
	},
	beforeSave:function(isUpdate){
		var me = this;
		var form = Ext.getCmp('form');
		
		var grid1 = Ext.getCmp('hxfinancingsituation');//授信详情
		var grid2 = Ext.getCmp('accesscompany');//准入公司详情
		var grid3 = Ext.getCmp('paytaxes');//纳税详情
		
		var param1 = new Array();
		if(grid1){
			param1 = me.GridUtil.getGridStore(grid1);
		}
		var param2 = new Array();
		if(grid2){
			param2 = me.GridUtil.getGridStore(grid2);
		}
		var param3 = new Array();
		if(grid3){
			param3 = me.GridUtil.getGridStore(grid3);
		}
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
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
			me.save(r, param1, param2, param3, isUpdate);
		} else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0],isUpdate = arguments[arguments.length-1];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.caller = caller;
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + form.saveUrl,
			params : params,
			method : 'post',
			callback : function(options,success,response){	   
				me.FormUtil.setLoading(false);
			   	var localJson = new Ext.decode(response.responseText);
		    	if(localJson.success){
    				showMessage('提示', '保存成功!', 1000);
    				window.location.reload();
	   			} else if(localJson.exceptionInfo){
   					showError(localJson.exceptionInfo);
	   				return;
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
			}
		});
	},
	getGridStore: function(grid){
		var msg = this.GridUtil.checkGridDirty(grid);
		if(msg == '') {
			showMessage('警告', '没有新增或修改数据.');
			return null;
		} else {
			return this.GridUtil.getGridStore(grid);
		}
	},
	onGridItemClick: function(selModel, record){//grid行选择
		if(readOnly != 1){
			this.GridUtil.onGridItemClick(selModel, record);
		}
    }
});