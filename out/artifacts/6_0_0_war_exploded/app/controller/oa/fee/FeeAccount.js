Ext.QuickTips.init();
Ext.define('erp.controller.oa.fee.FeeAccount', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.fee.FeeAccountForm','oa.fee.FeeAccount',
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
    		var me = this;   		
    		console.log(Ext.getCmp('date').value);
    		me.beforestartAccount(Ext.getCmp('date').value,function(data){
    			if(data.exceptionInfo){
    				console.log();
    				warnMsg("当前期间还存在已提交、在录入的单据，系统统会将这部分单据的日期变更为下个月1号，确定结转么?", function(btn){
    					if(btn == 'yes'){
    						me.Account();
    					}else{
    						return;
    					}
    				});
    			}else{
    				me.Account();
    			}
    			
    		});
    	},
    	beforestartAccount: function(f,callback) {
    		var me = this;
    		me.FormUtil.setLoading(true);
    		Ext.Ajax.request({
    			url: basePath + 'oa/fee/beforestartFeeAccount.action',
    			method: 'post',
    			params:{
    				date:f
    			},
    			callback: function(opt, s, r) {
    				me.FormUtil.setLoading(false);
    				console.log(r);
    				console.log(r.responseText);
    				var rs = Ext.decode(r.responseText);    				
    				callback.call(null,rs);
    			}
    		});
    	},
    	Account:function(){
    		var me = this;
    		me.FormUtil.setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + "oa/fee/startFeeAccount.action",
    			params:{
    				date:Ext.getCmp('date').value
    			},
    			timeout: 120000,
    			method:'post',
    			callback:function(options,success,response){
    				console.log(response);
    				console.log(response.responseText);
    				me.FormUtil.setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
    				console.log(response);
    				console.log(response.responseText);
        			if(localJson.success){
        				endArSuccess(function(){
        					window.location.reload();
        				});
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){// 特殊情况:操作成功，但是出现警告,允许刷新页面
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
    		var me = this;
    		me.FormUtil.setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + "fa/ars/overAccount.action",
    			params:{
    				param:{date:Ext.getCmp('date').value},
    			},
    			method:'post',
    			callback:function(options,success,response){
    				me.FormUtil.setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				unEndArSuccess(function(){
        					window.location.reload();
        				});
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){// 特殊情况:操作成功，但是出现警告,允许刷新页面
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
    			url: basePath + 'oa/fee/getCurrentYearmonth.action',
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