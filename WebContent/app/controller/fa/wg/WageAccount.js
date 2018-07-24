Ext.QuickTips.init();
Ext.define('erp.controller.fa.wg.WageAccount', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['fa.wg.WageAccount', 'core.button.Confirm', 'core.button.Close', 'core.form.MonthDateField'],
    init: function() {
        var me = this;
        this.control({
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.onClose();
                }
            },
            'erpConfirmButton': {
                click: function(btn) {
                    this.confirm();
                }
            },
            'monthdatefield': {
                afterrender: function(f) {
                    this.getCurrentMonth(f, "MONTH-A");
                }
            }
        });
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    confirm: function() {
        var me = this;
        Ext.MessageBox.confirm('提示', '确实要计算佣金吗?',
        function(btn) {
            if (btn == 'yes') {
            	 var mb = new Ext.window.MessageBox();
         	     mb.wait('正在计算中','请稍后...',{
         		   interval: 10000, 
         		   duration: 1000000,
         		   increment: 20,
         		   scope: this
         		});
                Ext.Ajax.request({
                    url: basePath + "fa/wg/WageAccount.action",
                    params: {
                        date: Ext.getCmp('date').value
                    },
                    method: 'post',
                    timeout: 600000,
                    callback: function(options, success, response) {
                    	mb.close();
                        var localJson = new Ext.decode(response.responseText);
                        if (localJson.success) {
                            Ext.Msg.alert("提示", "计算成功！");
                        } else {
                            if (localJson.exceptionInfo) {
                                var str = localJson.exceptionInfo;
                                if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
                                    str = str.replace('AFTERSUCCESS', '');
                                    showError(str);
                                    postSuccess(function() {
                                        window.location.reload();
                                    });
                                } else {
                                    showError(str);
                                    return;
                                }
                            }
                        }
                    }
                });
            }
        });
    },
    getCurrentMonth: function(f, type) {
        Ext.Ajax.request({
            url: basePath + 'fa/getMonth.action',
            params: {
                type: type
            },
            callback: function(opt, s, r) {
                var rs = Ext.decode(r.responseText);
                if (rs.data) {
                    f.setValue(rs.data.PD_DETNO);
                }
            }
        });
    }
});