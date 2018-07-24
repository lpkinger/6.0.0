Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.ProduceFKBudgetBill', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fp.ProduceFKBudgetBill','core.button.Close','core.button.ProduceFKBudgetBill','core.form.MonthDateField'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpProduceFKBudgetBillButton' : {
        			click: {
        				fn:function(btn){
        					Ext.Ajax.request({
        						url : basePath
        								+ "fa/fp/ProduceFKBudgetBill.action",
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
        								showMessage("提示", "生成付款预算单成功！");
        							}
        						}
        					});
            			},
            			lock:2000
        			}
    			},
        		'monthdatefield': {
        			afterrender: function(f) {
        				this.getCurrentMonth(f, "MONTH-V");
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