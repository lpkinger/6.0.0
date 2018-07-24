Ext.QuickTips.init();
Ext.define('erp.controller.plm.test.CheckList', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: [
        'core.form.Panel', 'plm.test.CheckList', 'core.grid.Panel2', 'core.toolbar.Toolbar',
        'core.button.Add', 'core.button.Submit', 'core.button.Audit', 'core.button.Save', 'core.button.Close', 'core.button.Print',
        'core.button.ResSubmit', 'core.button.Update', 'core.button.Delete', 'core.button.ResAudit', 'core.button.DeleteDetail',
        'core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger'
    ],
    init: function() {
        var me = this;
        this.control({
            'erpGridPanel2': {
                itemclick: this.onGridItemClick
            },
            'erpSaveButton': {
                click: function(btn) {
                    this.save(this);
                }
            },
            'erpCloseButton': {
                click: function(btn) {},
                scope: this,
            },
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cl_statuscode');
                    if (status && status.value != 'ENTERING') {
                        var grid = Ext.getCmp('grid');
                        grid.plugins[0].destroy();
                        btn.hide();
                    }
                },
                click: function(btn) {
                    this.FormUtil.onUpdate(this);
                }
            },
            'erpDeleteButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cl_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    this.FormUtil.onDelete(Ext.getCmp('cl_id').value);
                }
            },
            'erpAddButton': {
                click: function() {
                    me.FormUtil.onAdd('addCheckList', '创建项目', 'jsps/plm/test/checklist.jsp');
                }
            },
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cl_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onSubmit(Ext.getCmp('cl_id').value);
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cl_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResSubmit(Ext.getCmp('cl_id').value);
                }
            },
            'erpDeleteDetailButton': {
                afterrender: function(btn) {
                	var status = Ext.getCmp('cl_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.disabled = true;
                    }
                    btn.ownerCt.add({
                        id: 'fileform',
                        xtype: 'form',
                        layout: 'column',
                        height:26,
                        bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
                        items: [{
                            xtype: 'filefield',
                            name: 'file',
                            buttonOnly: true,
                            hideLabel: true,
                            disabled: true,
                            width: 90,
                            height: 17,
                            id: 'file',
                            buttonConfig: {
                                iconCls: 'x-button-icon-pic',
                                text: '上传附件',
                            },
                            listeners: {
                                change: function(field) {
                                    var filename = '';
                                    if (contains(field.value, "\\", true)) {
                                        filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
                                    } else {
                                        filename = field.value.substring(field.value.lastIndexOf('/') + 1);
                                    }
                                    field.ownerCt.getForm().submit({
                                        url: basePath + 'common/upload.action?em_code=' + em_code,
                                        waitMsg: "正在解析文件信息",
                                        success: function(fp, o) {
                                            console.log(o);
                                            if (o.result.error) {
                                                showError(o.result.error);
                                            } else {
                                                Ext.Msg.alert("恭喜", filename + " 上传成功!");
                                                field.setDisabled(true);
                                                var record = Ext.getCmp('grid').selModel.lastSelected;
                                                console.log(record);
                                                if (record) {
                                                    record.set('cld_attach', filename + ";" + o.result.filepath);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }]
                    });
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cl_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onAudit(Ext.getCmp('cl_id').value);
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cl_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResAudit(Ext.getCmp('cl_id').value);
                }

            },
            'dbfindtrigger[name=cld_newhandman]': {
                afterrender: function(trigger) {
                    /*trigger.gridKey='cl_prjplanid';
                    trigger.mappinggirdKey='tm_prjid';
                    trigger.gridErrorMessage='请选择项目计划';*/
                }
            },
        });
    },
    onGridItemClick: function(selModel, record) { //grid行选择
        var me = this;
        var status = Ext.getCmp('cl_statuscode');
        var value = record.data.cld_id;
        Ext.getCmp('file').setDisabled(false);
        if (status && status.value != 'ENTERING' && value != 0) {
            //不能修改了
            var formCondition = "cld_id  IS" + value;
            var gridCondition = "ch_cldid IS" + value;
            var panel = Ext.getCmp("cld_id=" + value);
            var caller = "Check";
            var url = basePath + "jsps/plm/test/check.jsp";
            var main = parent.Ext.getCmp("content-panel");
            if (!panel) {
                var title = "";
                if (value.toString().length > 4) {
                    title = value.toString().substring(value.toString().length - 4);
                } else {
                    title = value;
                }
                panel = {
                    //title : main.getActiveTab().title+'('+title+')',
                    title: 'check单(' + title + ')',
                    tag: 'iframe',
                    tabConfig: {
                        tooltip: 'check单(' + title + ')'
                    },
                    frame: true,
                    border: false,
                    layout: 'fit',
                    iconCls: 'x-tree-icon-tab-tab',
                    html: '<iframe id="iframe_maindetail_' + caller + "_" + value + '" src="' + url + '?formCondition=' + formCondition + '&gridCondition=' + gridCondition + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
                    closable: true,
                    listeners: {
                        close: function() {
                            main.setActiveTab(main.getActiveTab().id);
                        }
                    }
                };
                me.openTab(panel, "ch_id=" + value);
            } else {
                main.setActiveTab(panel);
            }

        } else {
            this.gridLastSelected = record;
            var grid = Ext.getCmp('grid');
            if (record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == '') {
                this.gridLastSelected.findable = true; //空数据可以在输入完code，并移开光标后，自动调出该条数据
            } else {
                this.gridLastSelected.findable = false;
            }
            this.GridUtil.onGridItemClick(selModel, record);

        }

    },
    openTab: function(panel, id) {
        var me = this;
        var o = (typeof panel == "string" ? panel : id || panel.id);
        var main = parent.Ext.getCmp("content-panel");
        var tab = main.getComponent(o);
        if (tab) {
            main.setActiveTab(tab);
        } else if (typeof panel != "string") {
            panel.id = o;
            var p = main.add(panel);
            main.setActiveTab(p);
        }
    },
    getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    save: function(btn) {
        var me = this;
        if (Ext.getCmp('cl_code').value == null || Ext.getCmp('cl_code').value == '') {
            me.BaseUtil.getRandomNumber();
            console.log(Ext.getCmp('cl_code').getValue());
            Ext.getCmp('cl_code').setValue('BL_' + Ext.getCmp('cl_code').getValue());
        }
        me.FormUtil.beforeSave(me);
    },
});