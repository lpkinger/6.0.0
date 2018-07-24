Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.BatchCancelGoods', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.ars.BatchCancelGoods',
    		'core.button.Confirm','core.button.Close','core.form.MonthDateField'
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
        				me.getCurrentMonth(f);
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
    			url : basePath + "fa/ars/cancelGoods.action",
    			params:{
    				date:Ext.getCmp('date').value
    			},
    			timeout: 120000,
    			method:'post',
    			callback:function(options,success,response){
    				me.FormUtil.setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert('提示', '取消成功', function(){
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
        getCurrentMonth: function(f) {
        	var me = this;
        	Ext.Ajax.request({
        		url: basePath + 'fa/getMonth.action',
        		params: {
        			type: 'MONTH-C'
        		},
        		callback: function(opt, s, r) {
        			var rs = Ext.decode(r.responseText);
        			if(rs.data) {
        				me.currentMonth = rs.data.PD_DETNO;
        				f.setValue(rs.data.PD_DETNO);
        			}
    			}
    		});
    	}
    });