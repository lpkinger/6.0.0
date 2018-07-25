Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.SettleAccountCheck', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.loaded.SettleAccountCheck', 'core.grid.Panel2','core.toolbar.Toolbar','core.button.Modify',
			'core.button.Save', 'core.button.Upload','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger',
			'core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger','core.form.SeparNumber'],
	init : function() {
		var me = this;
		this.control({
			'field[name=li_sachangedesc]': {
    			beforerender : function(f) {
    				f.emptyText = '对上述结算账户有异常变动情况进行说明！';
				},
				afterrender : function(f) {
    				if(!me.hasCheck()){
    					f.hide();
    					f.height=null;
    				}
				}
    		},
    		'checkbox': {
    			change: function(f, newValue, oldValue){
    				var desc = Ext.getCmp('li_sachangedesc');
    				if(newValue&&desc&&desc.isHidden()){
    					desc.show();
    				}else if(desc&&!desc.isHidden()){
	    				if(!me.hasCheck()){
	    					desc.hide();
	    				}
    				}
    			}
    		},
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
			'erpSaveButton': {
    			afterrender:function(btn){
					var status = Ext.getCmp('li_statuscode');
					if(status&&status.value!='ENTERING'){
						btn.hide();
					}
				},
		    	click: function(btn){
		    		me.beforeSave();	
		    	}
        	}
		})
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	hasCheck: function(){
		var checks = Ext.ComponentQuery.query('form checkbox');
		var hasCheck = false;
		Ext.Array.each(checks,function(check){
			if(check.checked){
				hasCheck = true;
				return;
			}
		});
		return hasCheck;
	},
	beforeSave:function(){
		var me = this;
		var form = Ext.getCmp('form');
		
		var grid1 = Ext.getCmp('incrash');//转入资金
		var grid2 = Ext.getCmp('outcrash');//支付资金
		
		var param1 = new Array();
		if(grid1){
			param1 = me.GridUtil.getGridStore(grid1);
		}
		var param2 = new Array();
		if(grid2){
			param2 = me.GridUtil.getGridStore(grid2);
		}
		
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
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
			me.save(r, param1, param2);
		} else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var me = this;
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.caller = caller;
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'fs/loaded/saveSettleAccountCheck.action?_noc=1',
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
	}
});