Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.UnpackApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views: ['hr.attendance.UnpackApply','core.button.SendEdi','core.button.CancelEdi', 'core.form.Panel', 'core.form.FileField', 'core.form.MultiField', 'core.form.CheckBoxGroup', 'core.button.Save', 'core.button.Add', 'core.button.Submit', 'core.button.Print', 'core.button.Upload', 'core.button.ResAudit', 'core.button.Audit', 'core.button.Close', 'core.button.Delete', 'core.button.Update', 'core.button.ResSubmit', 'core.grid.Panel2', 'core.button.TurnCustomer', 'core.button.Flow', 'core.button.DownLoad', 'core.button.Scan', 'common.datalist.Toolbar', 'core.button.Confirm', 'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger', 'core.form.YnField', 'core.trigger.AutoCodeTrigger'],
    init: function() {
        var me = this;
        this.control({
            'erpSaveButton': {
                click: function(btn) {
                    var form = me.getForm(btn);
                    if (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '') {
                        me.BaseUtil.getRandomNumber(); //自动添加编号
                    }
                    //保存之前的一些前台的逻辑判定
                    this.FormUtil.beforeSave(this);
                }
            },
            'erpDeleteButton': {
                click: function(btn) {
                    me.FormUtil.onDelete(Ext.getCmp('ua_id').value);
                }
            },
            'erpUpdateButton': {
                click: function(btn) {
                    this.FormUtil.onUpdate(this);
                }
            },
            'erpAddButton': {
                click: function() {
                    me.FormUtil.onAdd('addUnpackApply', '新增拆包申请', 'jsps/hr/attendance/unpackApply.jsp');
                }
            },
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ua_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onSubmit(Ext.getCmp('ua_id').value);
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ua_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResSubmit(Ext.getCmp('ua_id').value);
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ua_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onAudit(Ext.getCmp('ua_id').value);
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('ua_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResAudit(Ext.getCmp('ua_id').value);
                }
            },
            'erpPrintButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "cbapp";
                    var condition = '{UnpackApply.ua_id}=' + Ext.getCmp('ua_id').value + '';
                    var id = Ext.getCmp('ua_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            'erpConfirmButton': {
                afterrender: function(btn) {
                    var statu = Ext.getCmp('ua_statuscode'), confirmStatus=Ext.getCmp('ua_confirmstatus');
                    if (statu && statu.value != 'AUDITED') {
                        btn.hide();
                    }
                    if (confirmStatus && confirmStatus.value == '已处理') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.onConfirm(Ext.getCmp('ua_id').value);
                }
            }
        });
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    onConfirm: function(id) {
        var form = Ext.getCmp('form');
        Ext.Ajax.request({
            url: basePath + form.confirmUrl,
            params: {
                id: id,
                caller: caller
            },
            method: 'post',
            callback: function(options, success, response) {
                var localJson = new Ext.decode(response.responseText);
                if (localJson.success) {
                    window.location.reload();
                } else {
                    if (localJson.exceptionInfo) {
                        var str = localJson.exceptionInfo;
                        if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
                            str = str.replace('AFTERSUCCESS', '');
                            showMessage("提示", '确认成功');
                            window.location.reload();
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