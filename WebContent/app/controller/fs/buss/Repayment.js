Ext.QuickTips.init();
Ext.define('erp.controller.fs.buss.Repayment', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['fs.buss.Repayment', 'core.button.Confirm', 'core.button.Close','core.trigger.DbfindTrigger','core.form.SeparNumber'],
    init: function() {
        var me = this;
        this.control({
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.onClose();
                }
            },
            'dbfindtrigger[name=re_aacode]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='re_custcode';
	    			trigger.mappingKey='aa_custcode';
	    			trigger.dbMessage='请先选择客户编号！';
    			}
    		},
            'erpConfirmButton': {
                click: function(btn) {
                	if(Ext.isEmpty(Ext.getCmp('re_custcode').value)){
                    	showError('请选择客户编号！') ;  
            			return; 
            		}
                	if(Ext.isEmpty(Ext.getCmp('re_aacode').value)){
                    	showError('请选择借据编号！') ;  
            			return; 
            		}
                	if(Ext.isEmpty(Ext.getCmp('re_thisamount').value) || Ext.getCmp('re_thisamount').value <= 0 ){
                    	showError('还款金额需大于0！') ;  
            			return; 
            		}
                	if(Ext.getCmp('re_toal').value < Ext.getCmp('re_thisamount').value){
                		showError('还款金额不能大于应还金额！') ;  
            			return; 
                	}
                	if(Ext.isEmpty(Ext.getCmp('re_backdate').value)){
                    	showError('请选择申请还款日期！') ;  
            			return; 
            		}
                    this.confirm();
                }
            }
        });
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    confirm: function() {
        var me = this;
        Ext.MessageBox.confirm('提示', '确认还款?',
        function(btn) {
            if (btn == 'yes') {
                me.FormUtil.setLoading(true);
                Ext.Ajax.request({
                    url: basePath + "fs/buss/confirmRepayment.action",
                    params: {
                        aacode: Ext.getCmp('re_aacode').value,
                        aakind: Ext.getCmp('re_kind').value,
                        thisamount: Ext.getCmp('re_thisamount').value,
                        backcustamount: Ext.getCmp('re_backcustamount').value,
                        backdate: Ext.getCmp('re_backdate').value
                    },
                    method: 'post',
                    timeout: 1200000,
                    callback: function(options, success, response) {
                    	me.FormUtil.setLoading(false);
                    	var res = Ext.decode(response.responseText);
						if (res.exceptionInfo) {
							showError(res.exceptionInfo);
							return;
						}
						if (res.success) {
							showMessage("提示", "还款成功！");
							window.location.reload();
						}
                    }
                });
            }
        });
    }
});