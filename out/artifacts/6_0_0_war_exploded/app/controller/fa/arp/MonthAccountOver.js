Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.MonthAccountOver', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.arp.MonthAccountOver',
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
    			url : basePath + "fa/arp/startAccount.action",
    			params:{
    				param:{date:Ext.getCmp('date').value},
    			},
    			method:'post',
    			callback:function(options,success,response){
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("提示","操作成功！");
        				window.location.reload();
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
    			url : basePath + "fa/arp/overAccount.action",
    			params:{
    				param:{date:Ext.getCmp('date').value},
    			},
    			method:'post',
    			callback:function(options,success,response){
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("提示","操作成功！");
        				window.location.reload();
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
    			url: basePath + 'fa/arp/getCurrentYearmonth.action',
    			method: 'GET',
    			callback: function(opt, s, r) {
    				var rs = Ext.decode(r.responseText);
    				if(rs.exceptionInfo) {
    					showError(rs.exceptionInfo);
    				} else if(rs.data) {
    					f.setValue(rs.data);
    				}
    			}
    		});
    	}
    });