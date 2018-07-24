Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.AttendanceManage', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.attendance.AttendanceForm','hr.attendance.AttendanceManage',
    		'core.button.Confirm','core.button.Close','core.form.ConDateField'
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
        				this.confirm(me);
        			}
        		}        	
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	confirm: function(me){
    		var start=Ext.Date.format(Ext.getCmp('searchdate_from').getValue(),'Y-m-d');
    		var end=Ext.Date.format(Ext.getCmp('searchdate_to').getValue(),'Y-m-d');
    		var toAttendanceConfirm = Ext.getCmp('toAttendanceConfirm').getValue();
    		var mb = new Ext.window.MessageBox();
	 		mb.wait('正在分析','请稍后...',{
		   		interval: 1000, //bar will move fast!
		   		duration: 1000000,
		   		increment: 20,
	            scope: this
			});
    		Ext.Ajax.request({
    			url : basePath + "hr/attendance/result.action",
    			params:{
    				startdate: start,
    				enddate: end,
    				toAttendanceConfirm:toAttendanceConfirm
    			},
    			method:'post',
    			timeout: 600000,
    			callback:function(options,success,response){
    				mb.close();
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("提示","操作成功！",function(f){
        					if(f=='ok'){
        						var url_ = basePath+'/jsps/common/query.jsp?whoami=AttendDataTotal&startdate='+start+'&enddate='+end+'&comboValue='+Ext.getCmp('searchdate').combo.value+' ';
        		            	var panel = {
        		    	    			title:'考勤汇总表',
        		    	    			tag : 'iframe',
        		    	    			frame : true,
        		    	    			border : false,
        		    	    			layout : 'fit',
        		    	    			iconCls : 'x-tree-icon-tab-tab',
        		    	    			html : '<iframe id="iframe_maindetail_'+caller+'" src="'+url_+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
        		    	    			closable : true
        		    	    	 };
        		            	me.FormUtil.openTab(panel);
        					}
        				});
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
    	},
    	getCurrentMonth: function(f, type) {
        	Ext.Ajax.request({
        		url: basePath + 'fa/getMonth.action',
        		params: {
        			type: type
        		},
        		callback: function(opt, s, r) {
        			var rs = Ext.decode(r.responseText);
        			if(rs.data) {
        				f.setValue(rs.data.PD_DETNO);
        			}
        		}
        	});
        }
    });