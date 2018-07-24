Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.AttendDataCom', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.attendance.AttendDataComForm','hr.attendance.AttendDataCom','core.form.ConDateField',
    		'core.button.Close','core.button.AttendDataCom','core.trigger.DbfindTrigger'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpAttendDataComButton': {
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
    		var mb = new Ext.window.MessageBox();
    	    mb.wait('正在计算中','请稍后...',{
    		   interval: 60000, //bar will move fast!
    		   duration: 1800000,
    		   increment: 20,
    		   scope: this,
    		});
    		Ext.Ajax.request({
    			url : basePath + "hr/attendance/attendDataCom.action",
    			params:{
    					startdate:Ext.Date.toString(Ext.getCmp('searchdate').firstVal),
    					enddate:Ext.Date.toString(Ext.getCmp('searchdate').secondVal),
    					emcode:''
    			},
    			method:'post',
    			timeout: 2400000,
    			callback:function(options,success,response){
    				var localJson = new Ext.decode(response.responseText);
    				mb.close();
        			if(localJson.success){
        				attendDataComSuccess(function(){
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