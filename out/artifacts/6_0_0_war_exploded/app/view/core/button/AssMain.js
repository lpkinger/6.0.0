/**
 * 辅助核算按钮
 */
Ext.define('erp.view.core.button.AssMain', {
    extend: 'Ext.Button',
    alias: 'widget.erpAssMainButton',
    iconCls: 'x-button-icon-check',
    cls: 'x-btn-gray',
    id: 'assmainbutton',
    text: $I18N.common.button.erpAssMainButton,
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    //        disabled:true,
    cacheStoreForm: new Array(),
    cacheStoreGrid: new Array(),
    cacheStore: new Object(),
    //所有数据
    cacheAss: new Object(),
    //asstype改变时，cacheStore改变
    asstype: new Array(),
    asskind: new Array(),

    style: {
        marginLeft: '10px'
    },
    width: 85,

    listeners: {
        click: function() {
            var me = this;
            var form = me.ownerCt.ownerCt,
            id = Ext.getCmp(form.keyField).getValue();
            var win = Ext.getCmp('assmain-' + id);
            if (win) {
                win.show();
            } else {
                var grid = Ext.create('Ext.grid.Panel', {
                    id: 'assgrid-main',
                    anchor: '100% 100%',
                    columns: [{
                        text: 'ID',
                        hidden: true,
                        dataIndex: 'ass_id'
                    },
                    {
                        text: 'ASS_CONID',
                        hidden: true,
                        dataIndex: 'ass_conid'
                    },
                    {
                        text: '辅助类型',
                        dataIndex: 'ass_asstype',
                        hidden: true,
                        flex: 1
                    },
                    {
                        text: '核算项',
                        dataIndex: 'ass_assname',
                        flex: 1
                    },
                    {
                        text: '编号表达式',
                        dataIndex: 'ass_codefield',
                        flex: 1,
                        editor: {
                            xtype: 'dbfindtrigger'
                        }
                    },
                    {
                        text: '名称表达式',
                        dataIndex: 'ass_namefield',
                        flex: 1
                    }],
                    store: new Ext.data.Store({
                        fields: [{
                            name: 'ass_id',
                            type: 'number'
                        },
                        {
                            name: 'ass_conid',
                            type: 'number'
                        },
                        {
                            name: 'ass_assname',
                            type: 'string'
                        },
                        {
                            name: 'ass_asstype',
                            type: 'string'
                        },
                        {
                            name: 'ass_codefield',
                            type: 'string'
                        },
                        {
                            name: 'ass_namefield',
                            type: 'string'
                        }]
                    }),
                    columnLines: true,
                    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
                        clicksToEdit: 1
                    })],
                    listeners: {
                        afterrender: function(grid) {
                            var dbfinds = new Array();
                            dbfinds.push({
                                dbGridField: 've_code',
                                field: 'ass_codefield',
                                trigger: null
                            });
                            dbfinds.push({
                                dbGridField: 've_name',
                                field: 'ass_namefield',
                                trigger: null
                            });

                            dbfinds.push({
                                dbGridField: 'dp_code',
                                field: 'ass_codefield',
                                trigger: null
                            });
                            dbfinds.push({
                                dbGridField: 'dp_name',
                                field: 'ass_namefield',
                                trigger: null
                            });

                            dbfinds.push({
                                dbGridField: 'em_code',
                                field: 'ass_codefield',
                                trigger: null
                            });
                            dbfinds.push({
                                dbGridField: 'em_name',
                                field: 'ass_namefield',
                                trigger: null
                            });

                            dbfinds.push({
                                dbGridField: 'akd_asscode',
                                field: 'ass_codefield',
                                trigger: null
                            });
                            dbfinds.push({
                                dbGridField: 'akd_assname',
                                field: 'ass_namefield',
                                trigger: null
                            });

                            dbfinds.push({
                                dbGridField: 'wh_code',
                                field: 'ass_codefield',
                                trigger: null
                            });
                            dbfinds.push({
                                dbGridField: 'wh_description',
                                field: 'ass_namefield',
                                trigger: null
                            });

                            dbfinds.push({
                                dbGridField: 'cu_code',
                                field: 'ass_codefield',
                                trigger: null
                            });
                            dbfinds.push({
                                dbGridField: 'cu_name',
                                field: 'ass_namefield',
                                trigger: null
                            });

                            grid.dbfinds = dbfinds;
                            grid.plugins[0].on('beforeedit',
                            function(e) {

                                if (e.field == 'ass_codefield') {
                                    var column = e.column,
                                    btn = Ext.getCmp("assmainbutton"),
                                    type = e.record.get('ass_asstype'),
                                    kind;
                                    if (type) {
                                        kind = btn.asskind[type];
                                        if (kind) {
                                            column.setEditor(new erp.view.core.trigger.DbfindTrigger({
                                                findConfig: kind.ak_addkind == null ? '': ('ak_addkind=\'' + kind.ak_addkind + '\'')
                                            }));
                                            column.dbfind = kind.ak_dbfind + '|' + kind.ak_asscode;
                                            grid.dbfinds = [{
                                                field: 'ass_codefield',
                                                dbGridField: kind.ak_asscode
                                            },
                                            {
                                                field: 'ass_namefield',
                                                dbGridField: kind.ak_assname
                                            }];
                                        }

                                    }

                                }

                                /*
						    			if(e.field=='ass_codefield'){
						    				var record = e.record;
				        					var column = e.column;

				        					switch(record.data['ass_asstype']){
//				        					Vend	供应商	Vendor	Vendor	ve_code
//				        					Dept	部门	Department	Department	dp_code
//				        					Empl	员工	Emplyee	Employee	em_code
//				        					Otc	其它应收客户	AssAddData	AssAddData	aad_code
//				        					Otv	其它应付供应商	AssAddData	AssAddData	aad_code
//				        					Otp	项目	AssAddData	AssAddData	aad_code
//				        					Store	仓库	WareHouse	WareHouse	wh_code
//				        					Cust	客户	Customer	Customer	cu_code

				        						case "Vend":
				        							column.dbfind='Vendor|ve_code';
				        							break;
				        						case "Dept":
				        							column.dbfind='Department|dp_code';
				        							break;
				        						case "Empl":
				        							column.dbfind='Employee|em_code';
				        							break;
				        						case "Otc":
				        							column.dbfind='AssKindDetail|akd_asscode';
				        							break;
				        						case "Otv":
				        							column.dbfind='AssKindDetail|akd_asscode';
				        							break;
				        						case "Otp":
				        							column.dbfind='AssKindDetail|akd_asscode';
				        							break;
				        						case "Store":
				        							column.dbfind='WareHouse|wh_code';
				        							break;
				        						case "Cust":
				        							column.dbfind='Customer|cu_code';
				        							break;
				        					}
						    			}
						    		*/
                            });
                        }
                    }
                });
                win = Ext.create('Ext.Window', {
                    width: 500,
                    height: 360,
                    layout: 'anchor',
                    id: 'assmain-' + id,
                    title: '辅助核算',
                    items: [grid],
                    buttonAlign: 'center',
                    modal: true,
                    buttons: [{
                        text: $I18N.common.button.erpConfirmButton,
                        cls: 'x-btn-blue',
                        handler: function(btn) {
                            var win = btn.ownerCt.ownerCt,
                            ass = win.down('gridpanel');
                            var data = new Array();
                            ass.store.each(function(item) {
                                data.push(item.data);
                            });
                            if (data.length > 0) {
                                me.cacheStoreForm[Ext.getCmp(me.ownerCt.ownerCt.keyField).getValue()] = data;
                            }
                            win.hide();
                        }
                    },
                    {
                        text: $I18N.common.button.erpOffButton,
                        cls: 'x-btn-blue',
                        handler: function(btn) {
                            btn.ownerCt.ownerCt.hide();
                        }
                    }]
                }).show();

            }
            me.getAssForm(win.down('gridpanel'), id, form.down('#ca_assname').getValue(), form.down('#ca_asstype').getValue());

        }

    },
    getAssData: function(id) {
        var me = this;
        var grid = Ext.getCmp('assgrid-main');
        Ext.each(grid.store.data.items,
        function(item, index) {
            //				me.asstype = Ext.isEmpty(item.data['ass_asstype']) ?
            //						new Array() : item.data['ass_asstype'].toString().split('#');
            me.asstype.push(item.data['ass_asstype']);
            var type = item.data['ass_asstype'];
            if (me.asskind[type]) {
                item.set('ass_asstype', me.asskind[type].ak_name);
            } else {
                Ext.Ajax.request({
                    url: basePath + 'common/getFieldsData.action',
                    async: false,
                    params: {
                        caller: 'AssKind',
                        fields: 'ak_name,ak_table,ak_dbfind,ak_asscode,ak_assname,ak_addkind',
                        condition: "ak_code='" + type + "'"
                    },
                    method: 'post',
                    callback: function(options, success, response) {
                        var localJson = new Ext.decode(response.responseText);
                        if (localJson.exceptionInfo) {
                            showError(localJson.exceptionInfo);
                            return;
                        }
                        if (localJson.success) {
                            var d = localJson.data;
                            if (Ext.isEmpty(item.get('ass_asstype'))) item.set('ass_asstype', d.ak_name);
                            me.asskind[type] = d;
                        }
                    }
                });
            }
        });
        var data = new Array();
        Ext.each(grid.store.data.items,
        function() {
            data.push(this.data);
        });
        /*	if(data.length > 0){
				me.cacheStore[id] = data;
			}*/
    },
    getAssForm: function(grid, id, assname, asstype) {
        var me = this;
        if (!me.cacheStoreForm[id]) {
            if (id == null || id <= 0) {
                var data = new Array(),
                r = assname.split('#'),
                t = asstype.split('#');
                for (var i = 0; i < r.length; i++) {
                    var o = new Object();
                    o.ass_conid = id;
                    o.ass_assname = r[i];
                    o.ass_asstype = t[i];
                    data.push(o);
                }
                grid.store.loadData(data);
            } else {
                var condition = "ass_conid=" + id;
                Ext.Ajax.request({
                    url: basePath + 'common/getFieldsDatas.action',
                    params: {
                        caller: me.ownerCt.ownerCt.assCaller,
                        fields: 'ass_id,ass_conid,ass_assname,ass_codefield,ass_namefield,ass_asstype',
                        condition: condition
                    },
                    method: 'post',
                    callback: function(options, success, response) {
                        var res = new Ext.decode(response.responseText);
                        if (res.exception || res.exceptionInfo) {
                            showError(res.exceptionInfo);
                            return;
                        }
                        var data = Ext.decode(res.data);
                        var dd = new Array(),
                        r = assname.split('#'),
                        t = asstype.split('#');
                        for (var i = 0; i < r.length; i++) {
                            var o = new Object();
                            Ext.Array.each(data,
                            function(d) {
                                if (d.ASS_ASSNAME == r[i]) {
                                    o.ass_id = d.ASS_ID;
                                    o.ass_conid = d.ASS_CONID;
                                    o.ass_assname = d.ASS_ASSNAME;
                                    o.ass_asstype = t[i];
                                    o.ass_codefield = d.ASS_CODEFIELD;
                                    o.ass_namefield = d.ASS_NAMEFIELD;
                                    dd.push(o);
                                }
                            });
                            if (o.ass_id == null) {
                                o.ass_conid = id;
                                o.ass_assname = r[i];
                                o.ass_asstype = t[i];
                                dd.push(o);
                            }
                        }
                        if (dd.length == 0) {
                            dd = [{},
                            {},
                            {},
                            {},
                            {},
                            {}];
                        }
                        grid.store.loadData(dd);
                        me.getAssData();
                    }
                });
            }
        } else {
            grid.store.loadData(me.cacheStoreForm[id]);

        }
    },
    initComponent: function() {
        this.callParent(arguments);
    }
});