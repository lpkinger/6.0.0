Ext.QuickTips.init();
Ext.define('erp.controller.plm.test.CheckListBase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: [
        'core.form.Panel', 'plm.test.CheckListBase', 'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.button.Load', 'core.form.HrefField',
        'core.button.Add', 'core.button.Submit', 'core.button.Audit', 'core.button.Save', 'core.button.Close', 'core.button.Print',
        'core.button.ResSubmit', 'core.button.Update', 'core.button.Delete', 'core.button.ResAudit', 'core.button.DeleteDetail', 'core.button.UpdateTestResult',
        'core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger', 'core.button.End', 'core.button.ResEnd', 'core.button.DeleteAllDetails'
    ],
    init: function() {
        var me = this;
        this.control({
            'erpGridPanel2': {
                itemclick: this.onGridItemClick,
                render: function(p) {
                    p.getEl().dom.addEventListener('scroll', function() {}, p);
                }
            },
            'erpSaveButton': {
                click: function(btn) {
                    this.save(this);
                }
            },
            'erpCloseButton': {
                click: function(btn) {},
                scope: this
            },
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cb_statuscode');
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
            'button[id=deleteallbutton]': {
                click: function(btn) {
                    Ext.Ajax.request({
                        method: 'post',
                        url: basePath + 'plm/check/deleteAllDetails.action',
                        params: {
                            id: Ext.getCmp('cb_id').getValue()
                        },
                        callback: function(options, success, response) {
                            var localJson = new Ext.decode(response.responseText);
                            if (localJson.success) {
                                Ext.Msg.alert('提示', '清除成功!', function(btn) {
                                    //update成功后刷新页面进入可编辑的页面 
                                    window.location.reload();
                                });
                            } else if (localJson.exceptionInfo) {
                                var str = localJson.exceptionInfo;
                                showError(str);
                                return;
                            }
                        }
                    });
                },
                afterrender: function(btn) {
                    var statuscode = Ext.getCmp('cb_statuscode').getValue();
                    if (statuscode && statuscode != 'ENTERING') {
                        btn.hide();
                    }
                }
            },
            'erpDeleteButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cb_statuscode');
                    var source = Ext.getCmp('cb_source');
                    if ((status && status.value != 'ENTERING') || (source && source.value != 'add')) {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    this.FormUtil.onDelete(Ext.getCmp('cb_id').value);
                }
            },
            'erpAddButton': {
                click: function() {
                    me.FormUtil.onAdd('addCheckList', 'CheckList单', 'jsps/plm/test/checklistbase.jsp');
                }
            },
            'field[name=cb_prjcode]': {
                afterrender: function(f) {
                    f.setFieldStyle({
                        'color': 'blue'
                    });
                    f.focusCls = 'mail-attach';
                    var c = Ext.Function.bind(me.openUrl, me);
                    Ext.EventManager.on(f.inputEl, {
                        mousedown: c,
                        scope: f,
                        buffer: 100
                    });
                }
            },

            'field[name=cb_prcode]': {
                afterrender: function(f) {
                    f.setFieldStyle({
                        'color': 'blue'
                    });
                    f.focusCls = 'mail-attach';
                    var c = Ext.Function.bind(me.openUrl, me);
                    Ext.EventManager.on(f.inputEl, {
                        mousedown: c,
                        scope: f,
                        buffer: 100
                    });
                }
            },
            'erpLoadButton': {
                afterrender: function(btn) {
                    var statuscode = Ext.getCmp('cb_statuscode').getValue();
                    if (statuscode != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    //根据产品类型载入测试项 
                    var type = Ext.getCmp('cb_prodtype').getValue(),
                        keyValue = Ext.getCmp('cb_id').getValue();
                    warnMsg('重新载入测试项会清除掉明细数据,以及已产生的BUG单?', function(btn) {
                        if (btn == 'yes') {
                            me.showItemWidow(type, keyValue);
                        } else {
                            return;
                        }
                    });

                }
            },
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cb_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onSubmit(Ext.getCmp('cb_id').value);
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cb_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResSubmit(Ext.getCmp('cb_id').value);
                }
            },
            'erpEndButton': {
                afterrender: function(btn) {
                    var statuscode = Ext.getCmp('cb_statuscode').getValue();
                    if (statuscode != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onEnd(Ext.getCmp('cb_id').getValue());
                }

            },
            'erpResEndButton': {
                afterrender: function(btn) {
                    var statuscode = Ext.getCmp('cb_statuscode').getValue();
                    if (statuscode != 'FINISH') {
                        btn.hide();
                    }
                }
            },
            'erpDeleteDetailButton': {
                afterrender: function(btn) {
                	var status = Ext.getCmp('cb_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.disabled = true;
                    }
                    var me = this;
                    var a = '';
                    var b = '';
                    var c = '';
                    btn.ownerCt.add({
                        id: 'set',
                        readOnly: false,
                        xtype: 'combo',
                        fieldLabel: '结果设置',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['display', 'value'],
                            data: [{
                                    "display": '测试通过',
                                    "value": 'OK'
                                },
                                {
                                    "display": '不用测试',
                                    "value": 'NT'
                                },
                                {
                                    "display": '无此功能',
                                    "value": 'NF'
                                } /*,{"display": '测试不通过', "value": 'NG'} */
                            ] //经过确认取消最下面结果设置中不通过的选项
                        }),
                        displayField: 'display',
                        valueField: 'value',
                        queryMode: 'local',
                        editable: false,
                        dirty: false,
                        disabled: true,
                        listeners: {
                            select: function(combo, records) {
                                a = combo.value
                                //	   me.setResult(combo.value,'cbd_result');
                            }
                        }
                    });
                    btn.ownerCt.add({
                        id: 'set1',
                        readOnly: false,
                        xtype: 'combo',
                        fieldLabel: '问题等级',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['display', 'value'],
                            data: [{
                                    "display": '致命',
                                    "value": '致命'
                                },
                                {
                                    "display": '严重',
                                    "value": '严重'
                                },
                                {
                                    "display": '一般',
                                    "value": '一般'
                                },
                                {
                                    "display": '提示',
                                    "value": '提示'
                                },
                                {
                                    "display": '建议',
                                    "value": '建议'
                                }
                            ]
                        }),
                        displayField: 'display',
                        valueField: 'value',
                        queryMode: 'local',
                        editable: false,
                        dirty: false,
                        disabled: true,
                        listeners: {
                            select: function(combo, records) {
                                b = combo.value;
                                //		   me.setResult(combo.value,'cbd_problemgrade');
                            }
                        }
                    });
                    btn.ownerCt.add({
                        id: 'set2',
                        readOnly: false,
                        xtype: 'textfield',
                        fieldLabel: '问题频率',
                        editable: true,
                        dirty: false,
                        disabled: true,
                        listeners: {
                            change: function(e) {
                                if (e.value != null && e.value != '') {
                                    c = e.value;
                                    //		me.setResult(field.value,'cbd_problemrate');
                                }
                            }
                        }
                    });
                    btn.ownerCt.add({ //新增更新结果的按钮，控制三个内容的更新
                        id: 'set3',
                        xtype: 'button',
                        text: '更新结果',
                        iconCls: 'x-button-icon-confirm',
                        cls: 'x-btn-gray',
                        onClick: function(field, e) {
                            form = Ext.getCmp('form');
                            var grid = Ext.getCmp('grid');
                            var items = grid.selModel.getSelection(),
                                data = new Array();
                            //maz 获取grid勾选的id
                            Ext.each(items, function(item, index) {
                                if (this.data[grid.keyField] != null && this.data[grid.keyField] != '' &&
                                    this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) {
                                    var o = new Object();
                                    if (grid.keyField) {
                                        o[grid.keyField] = item.data[grid.keyField];
                                    }
                                    data.push(o);
                                }
                            });
                            id = Ext.getCmp(form.keyField).value;
                            me.setResult(c, 'cbd_problemrate');
                            me.setResult(a, 'cbd_result');
                            me.setResult(b, 'cbd_problemgrade');
                            Ext.Ajax.request({
                                url: basePath + 'plm/test/updateResultCheckListBase.action',
                                params: {
                                    data: Ext.encode(data),
                                    id: id
                                },
                                method: 'post',
                            })
                        }
                    });
                }
            },
            'erpUpdateTestResultButton': {
                click: function(btn) {
                    win = this.getUpdateResultWindow(btn);
                    win.show();
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cb_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onAudit(Ext.getCmp('cb_id').value);
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('cb_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.FormUtil.onResAudit(Ext.getCmp('cb_id').value);
                }

            },
            'textareatrigger': {
                change: function(trigger, newvalue, oldvalue, btn) {
                    var grid = Ext.getCmp('grid');
                    if (trigger.record && btn == 'ok') {
                        Ext.Ajax.request({
                            url: basePath + 'plm/test/updateResult.action',
                            params: {
                                data: newvalue,
                                field: trigger.name,
                                keyValue: trigger.record.data[grid.keyField]
                            },
                            method: 'post',
                            callback: function(options, success, response) {
                                var local = Ext.decode(response.responseText);
                                if (local.success) {
                                    grid.GridUtil.loadNewStore(grid, {
                                        caller: caller,
                                        condition: grid.mainField + "=" + trigger.record.data[grid.mainField]
                                    })
                                    showMessage('提示', '保存成功!', 1000);
                                } else {
                                    showError(local.exceptionInfo);
                                }
                            }
                        });
                    }
                }
            }
        });
    },
    onGridItemClick: function(selModel, record) {
        this.GridUtil.onGridItemClick(selModel, record);
        /*//grid行选择
	    	   var me = this;
	    	   var status = Ext.getCmp('cb_statuscode');
	    	   var value=record.data.cld_id;
	    	   Ext.getCmp('file').setDisabled(false);
	    	   if(status && status.value != 'ENTERING'&&value!=0){  
	    		   //不能修改了
	    		   var formCondition = "cld_id  IS" + value ;
	    		   var gridCondition="ch_cldid IS"+value;
	    		   var panel = Ext.getCmp("cld_id=" +value);  
	    		   var caller="Check";
	    		   var url=basePath+"jsps/plm/test/check.jsp";                        
	    		   var main = parent.Ext.getCmp("content-panel");
	    		   if(!panel){ 
	    			   var title = "";
	    			   if (value.toString().length>4) {
	    				   title = value.toString().substring(value.toString().length-4);	
	    			   } else {
	    				   title = value;
	    			   }
	    			   panel = { 
	    					   //title : main.getActiveTab().title+'('+title+')',
	    					   title:'check单('+title+')',
	    					   tag : 'iframe',
	    					   tabConfig:{tooltip:'check单('+title+')'},
	    					   frame : true,
	    					   border : false,
	    					   layout : 'fit',
	    					   iconCls : 'x-tree-icon-tab-tab',
	    					   html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="'+url+'?formCondition='+formCondition+'&gridCondition='+gridCondition+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    					   closable : true,
	    					   listeners : {
	    						   close : function(){
	    							   main.setActiveTab(main.getActiveTab().id); 
	    						   }
	    					   } 
	    			   };
	    			   me.openTab(panel,"ch_id=" + value); 
	    		   }else{ 
	    			   main.setActiveTab(panel); 
	    		   } 

	    	   }else{
	    		   this.gridLastSelected = record;
	    		   var grid = Ext.getCmp('grid');
	    		   if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
	    			   this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
	    		   } else {
	    			   this.gridLastSelected.findable = false;
	    		   }
	    		   this.GridUtil.onGridItemClick(selModel, record);

	    	   }             

	        */
    },
    /* 	openUrl: function(e, el, obj) {
			var f = obj.scope;
			if(f.value) {
				this.FormUtil.onAdd('ProdInOut', f.ownerCt.down('#in_source').value, 
						this.getRelativeUrl(f));
			}
		},*/
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
    setResult: function(value, name) {
        var grid = Ext.getCmp('grid');
        var params = grid.getMultiSelected(name);
        params.result = value;
        Ext.Ajax.request({
            url: basePath + 'plm/test/setItemResult.action',
            params: params,
            method: 'post',
            callback: function(options, success, response) {
                var localJson = new Ext.decode(response.responseText);
                if (localJson.exceptionInfo) {
                    showError(localJson.exceptionInfo);
                    return "";
                }
                if (localJson.success) {
                    if (localJson.log) {
                        showMessage("提示", localJson.log);
                    }
                    Ext.getCmp('set').setDisabled(true);
                    Ext.getCmp('set').setValue(null);
                    Ext.getCmp('set1').setDisabled(true);
                    Ext.getCmp('set1').setValue(null);
                    Ext.getCmp('set2').setDisabled(true);
                    Ext.getCmp('set2').setValue(null);
                    grid.multiselected = new Array();
                    grid.GridUtil.loadNewStore(grid, {
                        caller: caller,
                        condition: 'cbd_cbid=' + Ext.getCmp('cb_id').getValue()
                    });
                    showMessage('提示', '设置成功!', 1000);

                }
            }
        });
    },
    save: function(btn) {
        var me = this;
        var codefield = Ext.getCmp('cb_code');
        if (codefield.value == null || codefield.value == '') {
            me.BaseUtil.getRandomNumber();
            codefield.setValue('CL_' + codefield.getValue());
        }
        me.FormUtil.beforeSave(me);
    },
    showItemWidow: function(type, keyValue) {
        var mb = new Ext.window.MessageBox();

        var condition = "tt_productkind='" + type + "'",
            nCaller = 'CheckItem';
        var win = new Ext.window.Window({
            id: 'win',
            height: "100%",
            width: "80%",
            maximizable: true,
            buttonAlign: 'center',
            layout: 'anchor',
            items: [{
                tag: 'iframe',
                frame: true,
                anchor: '100% 100%',
                layout: 'fit',
                html: '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller +
                    "&condition=" + condition + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
            }],
            buttons: [{
                name: 'confirm',
                text: $I18N.common.button.erpConfirmButton,
                iconCls: 'x-button-icon-confirm',
                cls: 'x-btn-gray',
                listeners: {
                    buffer: 500,
                    click: function(btn) {
                        var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
                        var data = grid.getEffectData();
                        mb.wait("正在加载...");
                        Ext.Ajax.request({
                            url: basePath + 'plm/test/LoadTestItem.action',
                            params: {
                                id: keyValue,
                                kinds: Ext.encode(data),
                                producttype: type
                            },
                            method: 'post',
                            callback: function(options, success, response) {
                                var localJson = new Ext.decode(response.responseText);
                                mb.close();
                                if (localJson.exceptionInfo) {
                                    showError(localJson.exceptionInfo);
                                } else {
                                    Ext.Msg.alert('提示', '载入成功', function() {
                                        var grid = Ext.getCmp('grid');
                                        Ext.getCmp('win').destroy();
                                        var param = {
                                            caller: caller,
                                            condition: 'cbd_cbid=' + keyValue
                                        };
                                        grid.GridUtil.loadNewStore(grid, param);
                                    });
                                }
                            }
                        });
                    }
                }
            }, {
                text: $I18N.common.button.erpCloseButton,
                iconCls: 'x-button-icon-close',
                cls: 'x-btn-gray',
                handler: function() {
                    Ext.getCmp('win').close();
                }
            }]
        });
        win.show();
    },
    getUpdateResultWindow: function(record) {
        var me = this;
        /*     var date = record.data.pd_deliveryreply;
			        if (date != null) {
			            date = Ext.Date.parse(date, "Y-m-d");
			        }*/
        return Ext.create('Ext.window.Window', {
            width: 430,
            height: 250,
            closeAction: 'destroy',
            cls: 'custom-blue',
            title: '<h1>更改测试结果</h1>',
            layout: {
                type: 'fit'
            },
            items: [{
                xtype: 'form',
                frame: true,
                layout: {
                    type: 'vbox'
                },
                items: [{
                        margin: '5 0 0 5',
                        xtype: 'combo',
                        fieldLabel: '结果设置',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['display', 'value'],
                            data: [{
                                    "display": '测试通过',
                                    "value": 'OK'
                                },
                                {
                                    "display": '不用测试',
                                    "value": 'NT'
                                },
                                {
                                    "display": '无此功能',
                                    "value": 'NF'
                                }
                                /*,	
                                		        				        {"display": '测试不通过', "value": 'NG'}*/
                            ]
                        }),
                        displayField: 'display',
                        valueField: 'value',
                        queryMode: 'local',
                        name: 'result',
                        editable: false,
                        dirty: false
                    },
                    /*{
                    	margin: '5 0 0 5',
                    	xtype: 'dbfindtrigger',
                    	columnidth: 0.4,
                    	fieldLabel: '处理人',
                    	name: 'emname',
                    	id:'emname',
                    	dbfind:'Employee|em_name'
                    },{
                    	margin:'5 0 0 5',
                    	xtype:'hidden',
                    	name:'emid',
                    	id:'emid'
                    },*/
                    {
                        margin: '5 0 0 5',
                        xtype: 'textareafield',
                        fieldLabel: '备注',
                        name: 'remark',
                        hideTrigger: true,
                        id: 'remark'
                    }
                ]
            }],
            buttonAlign: 'center',
            buttons: [{
                    xtype: 'button',
                    text: '保存',
                    width: 60,
                    iconCls: 'x-button-icon-save',
                    handler: function(btn) {
                        var w = btn.up('window');
                        me.saveTestResult(w);
                        win.close();
                        win.destroy();
                    }
                },
                {
                    xtype: 'button',
                    columnWidth: 0.1,
                    text: '关闭',
                    width: 60,
                    iconCls: 'x-button-icon-close',
                    margin: '0 0 0 10',
                    handler: function(btn) {
                        var win = btn.up('window');
                        win.close();
                        win.destroy();
                    }
                }
            ]
        });
    },
    openUrl: function(e, el, obj) {
        var f = obj.scope;
        if (f.value) {
            if (f.dataIndex == 'cb_prjcode') {
                openFormUrl(f.value, 'prj_code', 'jsps/plm/project/project.jsp', '立项申请');
            } else if (f.dataIndex == 'cb_prcode') {
                openFormUrl(f.value, 'pr_code', 'jsps/plm/project/projectReview.jsp', '项目评审');
            }
        }
    },
    saveTestResult: function(w) {
        var form = w.down('form'),
            values = form.getForm().getValues(),
            grid = Ext.getCmp('grid');
        var params = grid.getMultiSelected();
        params.formdata = unescape(escape(Ext.JSON.encode(values)));
        Ext.Ajax.request({
            url: basePath + 'plm/test/batchUpdateResult.action',
            params: params,
            method: 'post',
            callback: function(options, success, response) {
                var localJson = new Ext.decode(response.responseText);
                if (localJson.exceptionInfo) {
                    showError(localJson.exceptionInfo);
                    return "";
                }
                if (localJson.success) {
                    if (localJson.log) {
                        showMessage("提示", localJson.log);
                    }
                    Ext.getCmp('set').setDisabled(true);
                    Ext.getCmp('set').setValue(null);
                    Ext.getCmp('set1').setDisabled(true);
                    Ext.getCmp('set1').setValue(null);
                    Ext.getCmp('set2').setDisabled(true);
                    Ext.getCmp('set2').setValue(null);
                    grid.multiselected = new Array();
                    grid.GridUtil.loadNewStore(grid, {
                        caller: caller,
                        condition: 'cbd_cbid=' + Ext.getCmp('cb_id').getValue()
                    });
                    showMessage('提示', '设置成功!', 1000);

                }
            }
        });
    }
});