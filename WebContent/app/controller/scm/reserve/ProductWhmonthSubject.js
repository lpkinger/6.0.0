Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ProductWhmonthSubject', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['scm.reserve.ProductWhmonth', 'common.datalist.GridPanel', 'common.datalist.Toolbar', 'common.batchDeal.Form', 'core.trigger.DbfindTrigger', 'core.form.ConDateField', 'core.form.MonthDateField', 'core.button.Refresh'],
    refs: [{
        ref: 'grid',
        selector: '#grid'
    }],
    init: function() {
        var me = this;
        this.control({
            'erpDatalistGridPanel': {
                itemclick: this.onGridItemClick
            },
            'button[id=query]': {
                beforerender: function(btn) {
                    btn.handler = function() {
                        var grid = me.getGrid();
                        var condition = btn.ownerCt.ownerCt.getCondition(grid);
                        grid.getCount(caller, condition);
                    };
                }
            },
            'button[id=export]': {
                beforerender: function(btn) {
                    btn.handler = function() {
                        var grid = me.getGrid();
                        var condition = btn.ownerCt.ownerCt.getCondition(grid),
                        _con = grid.getCondition();
                        if (!Ext.isEmpty(_con)) condition += ' and (' + _con + ")";
                        me.BaseUtil.createExcel(caller, 'datalist', condition, null, null, null, grid);
                    };
                }
            },
            'monthdatefield[name=pwm_yearmonth]': {
                beforerender: function(field) {
                    field.autoValue = false;
                    field.fromnow = false;
                    me.getCurrentMonth(field,
                    function() { // 取完账期再筛选
                        Ext.defer(function() {
                            var grid = me.getGrid();
                            var condition = field.ownerCt.getCondition(grid);
                            grid.getCount(caller, condition);
                        },
                        500);
                    });
                }
            },
            'erpVastDealButton': {
                click: function(btn) {
                    var currentMonth = btn.ownerCt.ownerCt.down('monthdatefield').value;
                    if (!currentMonth) {
                        showError('期间不能为空!');
                        return
                    } else {
                        me.getGrid().setLoading(true);
                        Ext.Ajax.request({
                            url: basePath + 'scm/product/RefreshProdMonthNew.action',
                            method: 'post',
                            params: {
                                currentMonth: currentMonth
                            },
                            timeout: 1200000,
                            callback: function(options, success, response) {
                                me.getGrid().setLoading(false);
                                var res = new Ext.decode(response.responseText);
                                if (res.exceptionInfo != null) {
                                    showError(res.exceptionInfo);
                                    return;
                                }
                                Ext.Msg.alert("提示", "刷新成功!",
                                function() {
                                    Ext.getCmp('query').handler();
                                });
                            }
                        });
                    }
                }
            }
        });
    },
    getCurrentMonth: function(f, fn) {
        Ext.Ajax.request({
            url: basePath + 'fa/getMonth.action',
            params: {
                type: 'MONTH-P'
            },
            callback: function(opt, s, r) {
                var rs = Ext.decode(r.responseText);
                if (rs.data) {
                    f.setValue(rs.data.PD_DETNO);
                    fn && fn.call();
                }
            }
        });
    },
    onGridItemClick: function(selModel, record) { //grid行选择
        var me = this;
        if (keyField != null && keyField != '') { //有些datalist不需要打开明细表，这些表在datalist表里面不用配dl_keyField
            var value = record.data[keyField];
            var formCondition = keyField + "IS" + value;
            var gridCondition = pfField + "IS" + value;
            var panel = Ext.getCmp(caller + keyField + "=" + value);
            var main = parent.Ext.getCmp("content-panel");
            if (!main) {
                main = parent.parent.Ext.getCmp("content-panel");
            }
            if (!panel) {
                var title = "";
                if (value.toString().length > 4) {
                    title = value.toString().substring(value.toString().length - 4);
                } else {
                    title = value;
                }
                var myurl = '';
                if (me.BaseUtil.contains(url, '?', true)) {
                    myurl = url + '&formCondition=' + formCondition + '&gridCondition=' + gridCondition;
                } else {
                    myurl = url + '?formCondition=' + formCondition + '&gridCondition=' + gridCondition;
                }
                myurl += "&datalistId=" + main.getActiveTab().id;
                main.getActiveTab().currentStore = me.getCurrentStore(value); //用于单据翻页
                panel = {
                    title: me.BaseUtil.getActiveTab().title + '(' + title + ')',
                    tag: 'iframe',
                    tabConfig: {
                        tooltip: me.BaseUtil.getActiveTab().tabConfig.tooltip + '(' + keyField + "=" + value + ')'
                    },
                    frame: true,
                    border: false,
                    layout: 'fit',
                    iconCls: 'x-tree-icon-tab-tab1',
                    html: '<iframe id="iframe_maindetail_' + caller + "_" + value + '" src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
                    closable: true,
                    listeners: {
                        close: function() {
                            if (!main) {
                                main = parent.parent.Ext.getCmp("content-panel");
                            }
                            main.setActiveTab(main.getActiveTab().id);
                        }
                    }
                };
                this.openTab(panel, caller + keyField + "=" + record.data[keyField]);
            } else {
                main.setActiveTab(panel);
            }
        }
    },
    openTab: function(panel, id) {
        var o = (typeof panel == "string" ? panel: id || panel.id);
        var main = parent.Ext.getCmp("content-panel");
        if (!main) {
            main = parent.parent.Ext.getCmp("content-panel");
        }
        var tab = main.getComponent(o);
        if (tab) {
            main.setActiveTab(tab);
        } else if (typeof panel != "string") {
            panel.id = o;
            var p = main.add(panel);
            main.setActiveTab(p);
        }
    },
    getCurrentStore: function(value) {
        var grid = Ext.getCmp('grid');
        var items = grid.store.data.items;
        var array = new Array();
        var o = null;
        Ext.each(items,
        function(item, index) {
            o = new Object();
            o.selected = false;
            if (index == 0) {
                o.prev = null;
            } else {
                o.prev = items[index - 1].data[keyField];
            }
            if (index == items.length - 1) {
                o.next = null;
            } else {
                o.next = items[index + 1].data[keyField];
            }
            var v = item.data[keyField];
            o.value = v;
            if (v == value) o.selected = true;
            array.push(o);
        });
        return array;
    },
});