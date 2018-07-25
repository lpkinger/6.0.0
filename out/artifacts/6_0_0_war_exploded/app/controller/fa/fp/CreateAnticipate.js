Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.CreateAnticipate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['fa.fp.CreateAnticipate', 'core.button.Confirm', 'core.button.Close','core.trigger.DbfindTrigger'],
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
                	if(Ext.isEmpty(Ext.getCmp('date').value)){
                    	showError('请选择截止日期！') ;  
            			return; 
            		}
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
        Ext.MessageBox.confirm('提示', '确认生成逾期数据?',
        function(btn) {
            if (btn == 'yes') {
                me.FormUtil.setLoading(true);
                Ext.Ajax.request({
                    url: basePath + "fa/fp/createAnticipate.action",
                    params: {
                        date: Ext.getCmp('date').value,
                        cucode: Ext.getCmp('cu_code').value,
                        emcode: Ext.getCmp('em_code').value,
                        dpcode: Ext.getCmp('dp_code').value
                    },
                    method: 'post',
                    timeout: 1200000,
                    callback: function(options, success, response) {
                    	var res = Ext.decode(response.responseText);
						if (res.exceptionInfo) {
							showError(res.exceptionInfo);
							return;
						}
						if (res.success) {
							showMessage("提示", "生成逾期数据成功！");
							window.location.reload();
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