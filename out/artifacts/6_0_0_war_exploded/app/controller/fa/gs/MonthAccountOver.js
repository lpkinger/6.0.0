Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.MonthAccountOver', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.gs.MonthAccountOver',
    		'core.button.OverAccount','core.button.Close','core.form.MonthDateField','core.button.StartAccount'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpStartAccountButton': {
        			click: function(btn){
        				this.startAccount();
        			}
        		},
        		'erpOverAccountButton': {
        			click: function(btn){
        				this.overAccount();
        			}
        		},
        		'monthdatefield': {
        			afterrender: function(f) {
        				me.getCurrentYearmonth(f);
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	startAccount: function(){
    		Ext.Ajax.request({
    			url : basePath + "fa/gs/startAccount.action",
    			params:{
    				param:{date:Ext.getCmp('date').value},
    			},
    			method:'post',
    			callback:function(options,success,response){
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				endArSuccess(function(){
        					window.location.reload();
        				});
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
        	   					str = str.replace('AFTERSUCCESS', '');
        	   					showMessage('提示', str);
        	   					window.location.reload();
        	   				} else {
        	   					showError(str);return;
        	   				}
        	   			}
        			}
    			}
    		});
    	},
    	overAccount: function(){
    		Ext.Ajax.request({
    			url : basePath + "fa/gs/overAccount.action",
    			params:{
    				param:{date:Ext.getCmp('date').value},
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
        	   					showMessage('提示', str);
        	   					window.location.reload();
        	   				} else {
        	   					showError(str);return;
        	   				}
        	   			}
        			}
    			}
    		});
    	},
    	getCurrentYearmonth: function(f) {
    		Ext.Ajax.request({
				url : basePath + "fa/getMonth.action",
				params:{type:'MONTH-B'},
				method : 'post',
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		} else {
	        			f.setValue(res.data.PD_DETNO);
	        		}
	        	}
			});
    	}
    });