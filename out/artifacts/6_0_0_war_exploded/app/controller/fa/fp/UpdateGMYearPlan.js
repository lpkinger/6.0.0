Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.UpdateGMYearPlan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fp.UpdateGMYearPlanForm',
    		'fa.fp.UpdateGMYearPlan',
    		'core.form.Panel','core.button.UpdateGMYearPlan','core.button.Close','core.form.MonthDateField'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpUpdateGMYearPlanButton' : {
    				click : function(btn) {
    					console.log(Ext.getCmp('date'));
    					console.log(Ext.getCmp('date').value);
    					me.FormUtil.getActiveTab().setLoading(true);
    					Ext.Ajax.request({
    						url : basePath
    								+ "fa/fp/UpdateGMYearPlan.action",
    						params:{
    				    			yearmonth:Ext.getCmp('date').value,
    				    	},
    						method : 'post',
    						timeout : 300000,
    						callback : function(options, success, response) {
    							me.FormUtil.getActiveTab().setLoading(false);
    							var res = Ext.decode(response.responseText);
    							if (res.exceptionInfo) {
    								showError(res.exceptionInfo);
    								return;
    							}
    							if (res.success) {
    								showMessage("提示", "刷新年度计划金额成功！");
    							}
    						}
    					});
    				}
    			}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	}
    });