Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.YearCarryover', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fix.YearCarryover', 'core.button.Confirm','core.button.Close','core.form.MonthDateField'
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
        				this.confirm();
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
    	confirm: function(){
    		Ext.Ajax.request({
    			url : basePath + "fa/fix/confirmAutoDepreciation.action",
    			params:{
    				param:{date:Ext.getCmp('date').value},
    			},
    			method:'post',
    			callback:function(options,success,response){
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("操作成功！");
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
        	   					str = str.replace('AFTERSUCCESS', '');
        	   					showError(str);
        	   					postSuccess(function(){
        	   						window.location.reload();
        	    				});
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
    			url: basePath + 'fa/fix/getCurrentYearmonth.action',
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