Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.RefreshAnticipateBack', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fp.RefreshAnticipateBack',
    		'core.button.Confirm','core.button.Close','core.form.MonthDateField','core.form.ConDateField'
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
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	confirm: function(){
    		var me = this;
    		var datef = Ext.getCmp('date'), 
        		from = datef ? (datef.firstVal ? Ext.Date.toString(datef.firstVal) : null ): null, 
        		to = datef ? (datef.secondVal ? Ext.Date.toString(datef.secondVal) : null ): null;
    		me.FormUtil.setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + "fa/fp/refreshAnticipateBack.action",
    			params:{
    				from : from,
    				to : to
    			},
    			timeout: 120000,
    			method:'post',
    			callback:function(options,success,r){
    				me.FormUtil.setLoading(false);
    				var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						Ext.Msg.alert("提示","刷新成功！");
						window.location.reload();
					}
    			}
    		});
    	}
    });