Ext.define('erp.view.oa.attention.AttentionGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.erpAttentionGridPanel',
    id: 'AttentionGridPanel',
    layout: 'auto',
    emptyText: '无数据',
    columnLines: true,
    autoScroll: true,
    store: [],
    columns: [],
    multiselected: [],
    bodyStyle: 'background: #f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        bodyStyle: 'font-size:16px;height:40px;background:#EBEBEB;',
        style: 'background:#EBEBEB;height:40px;',
        layout: 'column',
        items: [{
            id: 'groupkind',
            hideLabel: true,
            readOnly: true,
            xtype: 'textfield',
            columnWidth: 0.9,
            style: 'margin-left:10px;margin-top:10px',
            fieldStyle: 'background:#EBEBEB ;border-bottom-style: none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-left:none; ',
        }, {
            xtype: 'textfield',
            id: 'groupid',
            hidden: true,
        }, '->', {
            xtype: 'combo',
            fieldLabel: '移动到',
            disabled: true,
            labelSeparator: '',
            labelAlign: 'right',
            style: 'margin-left:10px;margin-top:10px',
            fieldStyle: "background:#FFFAFA;color:#515151; height:20px",
            labelStyle: 'font-size:14px',
            id: 'moveto',
            queryMode: 'local',
            displayField: 'display',
            valueField: 'value',
            onTriggerClick: function() {
                var me = this;
                var data = null;
                var store = [];
                Ext.Ajax.request({
                    url: basePath + 'oa/addressbook/getAddressBookGroup.action',
                    async: false,
                    method: 'post',
                    callback: function(options, success, response) {
                        var res = new Ext.decode(response.responseText);
                        if (res.exceptionInfo) {
                            showError(localJson.exceptionInfo);
                            return;
                        } else {
                            data = res.tree;
                            Ext.Array.each(data, function(tr) {
                                if (tr.id != 0) {
                                    tr.display = tr.text.substring(0, tr.text.indexOf('('));
                                    tr.value = tr.id;
                                }
                            });
                        }
                    }
                });
                this.getStore().loadData(data);
                Ext.create('Ext.data.Store', {
                    fields: ['display', 'value'],
                    data: data
                });
                if (!me.readOnly && !me.disabled) {
                    if (me.isExpanded) {
                        me.collapse();
                    } else {
                        me.expand();
                    }
                    me.inputEl.focus();
                }
            }
        }, {
            xtype: 'button',
            id: 'adduser',
            iconCls: 'x-menu-adduser',
            tooltip: '添加联系人',
            style: 'margin-left:10px;margin-top:10px'
        }, {
            xtype: 'button',
            id: 'deleteuser',
            disabled: true,
            tooltip: '删除联系人',
            iconCls: 'x-menu-deleteuser',
            style: 'margin-left:10px;margin-top:10px'
        }, {
            xtype: 'button',
            id: 'export',
            tooltip: '导出',
            iconCls: 'x-button-icon-excel',
            style: 'margin-left:10px;margin-top:10px'
        }, {
            xtype: 'button',
            id: 'import',
            tooltip: '导入',
            iconCls: 'x-button-icon-excel',
            style: 'margin-left:10px;margin-top:10px'
        }, {
            xtype: 'button',
            id: 'print',
            tooltip: '打印',
            iconCls: 'x-button-icon-print',
            style: 'margin-left:10px;margin-right:20px;margin-top:10px'
        }]
    }],
    selModel: Ext.create('Ext.selection.CheckboxModel', {
        ignoreRightMouseSelection: false,
        listeners: {
            selectionchange: function(selectionModel, selected, options) {

            }
        },
        onRowMouseDown: function(view, record, item, index, e) { //改写的onRowMouseDown方法
            var me = Ext.getCmp('AttentionGridPanel');
            var bool = true;
            var items = me.selModel.getSelection();
            Ext.each(items, function(item, index) {
                if (item.data == record.data) {
                    bool = false;
                    me.selModel.deselect(record);
                    Ext.Array.remove(items, item);
                    Ext.Array.remove(me.multiselected, record);
                }
            });
            Ext.each(me.multiselected, function(item, index) {
                items.push(item);
            });
            me.selModel.select(items);
            if (bool) {
                view.el.focus();
                var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
                if (checkbox.getAttribute('class') == 'x-grid-row-checker') {
                    me.multiselected.push(record);
                    items.push(record);
                    me.selModel.select(items);
                } else {
                    me.selModel.deselect(record);
                    Ext.Array.remove(me.multiselected, record);
                }
            }
            if (items.length > 0) {
                Ext.getCmp('deleteuser').setDisabled(false);
                Ext.getCmp('moveto').setDisabled(false);
            } else {
                Ext.getCmp('deleteuser').setDisabled(true);
                Ext.getCmp('moveto').setDisabled(true);
            }
        },
        onHeaderClick: function(headerCt, header, e) {
            if (header.isCheckerHd) {
                e.stopEvent();
                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
                if (isChecked) {
                    this.deselectAll(true);
                    var grid = Ext.getCmp('AttentionGridPanel');
                    this.deselect(grid.multiselected);
                    grid.multiselected = new Array();
                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
                    Ext.each(els, function(el, index) {
                        el.setAttribute('class', 'x-grid-row-checker');
                    });
                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on'); //添加这个
                } else {
                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on'); //添加这个
                    var grid = Ext.getCmp('AttentionGridPanel');
                    this.deselect(grid.multiselected);
                    grid.multiselected = new Array();
                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
                    Ext.each(els, function(el, index) {
                        el.setAttribute('class', 'x-grid-row-checker');
                    });
                    this.selectAll(true);
                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on'); //添加这个
                }
            }
        }
    }),
    initComponent: function() {
        this.addEvents({
            mouseover: true
        });
        condition = this.BaseUtil.getUrlParam('urlcondition');
        condition = (condition == null) ? "1=1" : condition;
        condition = condition.replace(/@/, "'%").replace(/@/, "%'");
        this.defaultCondition = condition;

        var gridParam = {
            caller: caller,
            condition: condition
        };
        this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");
        this.callParent(arguments);
    },
    viewConfig: {
        stripeRows: true
    },
    getMultiSelected: function() {
        var grid = this;
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index) {
            if (this.data[grid.keyField] != null && this.data[grid.keyField] != '' &&
                this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) {
                grid.multiselected.push(item);
            }
        });
        var records = Ext.Array.unique(grid.multiselected);
        var params = new Object();
        params.caller = caller;
        var data = new Array();
        Ext.each(records, function(record, index) {
            if (grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != '' &&
                this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) {
                bool = true;
                var o = new Object();
                o[grid.keyField] = record.data[grid.keyField];
                if (grid.necessaryFields) {
                    Ext.each(grid.necessaryFields, function(f, index) {
                        var v = record.data[f];
                        if (Ext.isDate(v)) {
                            v = Ext.Date.toString(v);
                        }
                        o[f] = v;
                    });
                }
                data.push(o);
            }
        });
        params.data = Ext.encode(data);
        return params;
    },
    loadNewStore: function(grid, param) {
        var me = this;
        var main = parent.Ext.getCmp("content-panel");
        if (!main)
            main = parent.parent.Ext.getCmp("content-panel");
        if (main) {
            main.getActiveTab().setLoading(true); //loading...
        }
        Ext.Ajax.request({ //拿到grid的columns
            url: basePath + "common/loadNewGridStore.action",
            params: param,
            async: false,
            method: 'post',
            callback: function(options, success, response) {
                if (main) {
                    main.getActiveTab().setLoading(false);
                }
                var res = new Ext.decode(response.responseText);
                if (res.exceptionInfo) {
                    showError(res.exceptionInfo);
                    return;
                }
                var data = res.data;
                if (!data || data.length == 0) {
                    data = [];
                    me.add10EmptyData(grid.detno, data);
                    me.add10EmptyData(grid.detno, data); //添20条吧
                }
                grid.store.loadData(data);
                //自定义event
                grid.addEvents({
                    storeloaded: true
                });
                grid.fireEvent('storeloaded', grid);
            }
        });
    },
    /**
     * 从index行开始，往grid里面加十空行
     * @param detno 编号字段
     * @param data 需要添加空白数据的data
     */
    add10EmptyData: function(detno, data) {
        if (detno) {
            var index = data.length == 0 ? 0 : Number(data[data.length - 1][detno]);
            for (var i = 0; i < 10; i++) {
                var o = new Object();
                o[detno] = index + i + 1;
                data.push(o);
            }
        } else {
            for (var i = 0; i < 10; i++) {
                var o = new Object();
                data.push(o);
            }
        }
    },
    getSearchValue: function() {
        var me = this,
            value = Ext.getCmp('search').getValue();

        if (value === '') {
            return null;
        }
        if (!me.regExpMode) {
            value = value.replace(me.regExpProtect, function(m) {
                return '\\' + m;
            });
        } else {
            try {
                new RegExp(value);
            } catch (error) {
                me.statusBar.setStatus({
                    text: error.message,
                    iconCls: 'x-status-error'
                });
                return null;
            }
            // this is stupid
            if (value === '^' || value === '$') {
                return null;
            }
        }

        return value;
    },
});