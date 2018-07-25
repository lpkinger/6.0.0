Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.EmpWorkDateSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','hr.attendance.EmpWorkDateSet','core.form.MultiField','core.button.Save','core.button.Close','erp.view.core.form.ConDateField',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','core.form.EmpSelectField',
      		'erp.view.core.form.CheckBoxGroup'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
    		'erpSaveButton': {
    			afterrender:function(btn){
    				btn.setText('保存设置'); 
    				btn.setWidth(110);
    			},
    			click: function(btn){
					var form = me.getForm(btn);
					if(!Ext.getCmp('ew_emids').getValue()){
						showError("请选择员工");
					}else{
						if(Ext.getCmp('ew_w1').getValue()||Ext.getCmp('ew_w2').getValue()||Ext.getCmp('ew_w3').getValue()||Ext.getCmp('ew_w4').getValue()||
						Ext.getCmp('ew_w5').getValue()||Ext.getCmp('ew_w6').getValue()||Ext.getCmp('ew_w0').getValue()){	
							me.save(this);
						}else{
							showError("本班适用于星期至少勾选一个");
						}	
					}								
				}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save:function(){	
		var params = new Object();
		var form = Ext.getCmp('form');
		var r = form.getValues();
		//去除ignore字段
		var keys = Ext.Object.getKeys(r), f;
		var reg = /[!@#$%^&*()'":,\/?]/;
		Ext.each(keys, function(k){
			f = form.down('#' + k);
			if(f && f.logic == 'ignore') {
				delete r[k];
			}
		});
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param =[];
		var me = this;
		form.setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	
	   			form.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//设置成功后刷新页面 
			   		    window.location.href = window.location.href;
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
	}	
});