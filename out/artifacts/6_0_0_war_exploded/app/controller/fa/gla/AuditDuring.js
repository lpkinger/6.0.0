Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.AuditDuring', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.gla.AuditDuring','core.form.YearDateField',
    		'core.button.Confirm','core.button.Close'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpConfirmButton': {
        			click: function(btn){
        				this.confirm(btn.ownerCt.ownerCt);
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	confirm: function(form){
    		var me = this, year = Ext.getCmp('year').value
    			myear = Ext.getCmp('myear').value, eyear = Ext.getCmp('eyear').value;
    		if(Ext.isEmpty(year)){
    			showError('请选择需要审计期间的年份！');
    			return;
    		}
    		Ext.Ajax.request({
    			url : basePath + "fa/gla/auditDuring.action",
    			params:{
    				year: year,
    				myear: myear,
    				eyear: eyear
    			},
    			method:'post',
    			callback:function(options,success,response){
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("提示","操作成功！");
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
        	   					str = str.replace('AFTERSUCCESS', '');
        	   					showError(str);
        	   				} else {
        	   					showError(str);return;
        	   				}
        	   			}
        			}
    			}
    		});
    	}
    });