Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.ProduceBudgetBill', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fp.ProduceBudgetBill',
    		'core.form.Panel','core.button.ProduceBudgetBill','core.button.Close','core.form.MonthDateField'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpProduceBudgetBillButton' : {
        			click: {
        				fn:function(btn){
        					Ext.Ajax.request({
        						url : basePath
        								+ "fa/fp/ProduceBudgetBill.action",
    							params:{
    				    			yearmonth:Ext.getCmp('date').value,
    							},
        						method : 'post',
        						timeout : 300000,
        						callback : function(options, success, response) {
        							var res = Ext.decode(response.responseText);
        							if (res.exceptionInfo) {
        								showError(res.exceptionInfo);
        								return;
        							}
        							if (res.success) {
        								showMessage("提示", "生成收款预算单成功！");
        							}
        						}
        					});
            			},
            			lock:2000
        			}
    			},
        		'monthdatefield': {
        			afterrender: function(f) {
        				this.getCurrentMonth(f, "MONTH-C");
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
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