Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.Periodsdetailcancelfreeze', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    	'core.form.Panel','scm.reserve.Periodsdetailcancelfreeze','core.button.Close',
		'core.button.CancelFreeze','core.form.MonthDateField'
	],
	init:function(){
		var me = this;
		this.control({ 
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.onClose();
				}
			},
			'erpCancelFreezeButton':{
				click: function(btn){
					Ext.Ajax.request({
						url : basePath + "scm/reserves/Periodsdetailcancelfreeze.action",
						method : 'post',
    		        	callback : function(options,success,response){
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo != null){
    		        			showError(res.exceptionInfo);
    		        			return;
    		        		}
    		        		if(res.success){
    		        			Ext.Msg.alert("提示","取消冻结成功！");
    		        			window.location.reload();
    		        		}
    		        	}
					});
				}
			},
			'field[name=date]':{
				afterrender:function(f){
					me.getFreezeDetno(f);
				}
			}
		});
	},
	getCurrentMonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-P'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    },
    getFreezeDetno: function(f) {
		Ext.Ajax.request({
			url: basePath + 'scm/reserve/getFreezeDetno.action',
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data);
				} else {
					f.setValue('无');
				}
			}
		});
	}
});