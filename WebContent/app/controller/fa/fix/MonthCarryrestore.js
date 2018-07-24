Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.MonthCarryrestore', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fix.MonthCarryrestore',
    		'core.button.ResCarryover','core.button.Close','core.form.MonthDateField'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpResCarryoverButton': {
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
    		var me = this;
    		me.FormUtil.setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + "fa/fix/confirmMonthCarryrestore.action",
    			params:{
    				date: Ext.getCmp('date').value
    			},
    			method:'post',
    			callback:function(options,success,response){
    				me.FormUtil.setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("提示","操作成功！");
        				window.location.reload();
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
        	   					str = str.replace('AFTERSUCCESS', '');
        	   					showError(str);
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