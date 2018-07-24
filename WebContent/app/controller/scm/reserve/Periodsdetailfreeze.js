Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.Periodsdetailfreeze', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    	'scm.reserve.PeriodsdetailfreezeForm','scm.reserve.Periodsdetailfreeze','core.button.Close',
		'core.button.Freeze','core.form.MonthDateField'
	],
	init:function(){
		var me = this;
		this.control({ 
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.onClose();
				}
			},
			'erpFreezeButton':{
				click: function(btn){
					Ext.Ajax.request({
						url : basePath + "scm/reserves/Periodsdetailfreeze.action",
						params:{pd_detno:Ext.getCmp('pd_detno').value},
						method : 'post',
    		        	callback : function(options,success,response){
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo != null){
    		        			showError(res.exceptionInfo);
    		        			return;
    		        		}
    		        		if(res.success){
    		        			Ext.Msg.alert("提示","冻结成功！");
    		        			window.location.reload();
    		        		}
    		        	}
					});
				}
			},
			'field[name=pd_detno]':{
				afterrender:function(f){
					me.getCurrentMonth(f);
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