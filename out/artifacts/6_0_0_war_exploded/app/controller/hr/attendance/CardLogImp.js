Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.CardLogImp', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.attendance.CardLogImpForm','hr.attendance.CardLogImp','core.form.MonthDateField',
    		'core.button.Close','core.button.CardLogImp','core.trigger.DbfindTrigger'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpCardLogImpButton': {
        			click: function(btn){
        				this.startAccount();
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	startAccount: function(){
    		var me = this;
    		console.log(Ext.getCmp('startdate').rawValue);
    		console.log(Ext.getCmp('enddate').rawValue);
    		console.log(Ext.getCmp('cardcode').rawValue);
    		console.log(Ext.getCmp('yearmonth').rawValue);
    		me.FormUtil.getActiveTab().setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + "hr/attendance/cardLogImp.action",
    			params:{
    			
    					startdate:Ext.getCmp('startdate').rawValue,
    					enddate:Ext.getCmp('enddate').rawValue,
    					cardcode:Ext.getCmp('cardcode').rawValue,
    					yearmonth:Ext.getCmp('yearmonth').rawValue
    					
    			},
    			method:'post',
    			callback:function(options,success,response){
    				me.FormUtil.getActiveTab().setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				cardLogImpSuccess(function(){
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
    	}
    });