Ext.QuickTips.init();
Ext.define('erp.controller.hr.wage.PayAccount', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['hr.wage.PayAccount', 'core.button.Confirm', 'core.button.Close', 'core.form.MonthDateField','core.form.Panel','core.form.FileField',
            'core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2','core.grid.YnColumn',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'],
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
        Ext.MessageBox.confirm('提示', '确定要计算薪资数据吗?',
        function(btn) {
            if (btn == 'yes') {
            	var params = new Object();
            	params.formStore = unescape(escape(Ext.JSON.encode(Ext.getCmp('form').getValues())));
                me.FormUtil.setLoading(true);
                Ext.Ajax.request({
                    url: basePath + "hr/wage/PayAccount.action",
                    params: params,
                    method: 'post',
                    timeout: 1200000,
                    callback: function(options, success, response) {
                        me.FormUtil.setLoading(false);
                        var localJson = new Ext.decode(response.responseText);
                        if (localJson.success) {
                            Ext.Msg.alert("提示", "操作成功！");
                            window.location.reload();
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