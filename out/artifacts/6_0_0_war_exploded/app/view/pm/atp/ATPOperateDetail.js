Ext.define('erp.view.pm.atp.ATPOperateDetail', {
    extend: 'Ext.Viewport',
    layout: 'anchor',
    hideBorders: true,
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
            items: [{
                items: [{
                    xtype: "erpATPOpDetailFormPanel",
                    anchor: '100% 20%'
                },
                {
                    anchor: '100% 80%',
                    xtype: "tabpanel",
                    id: 'tabpanel',
                    minTabWidth: 80,
                    layout: 'border',
                    items: [{
                        title: '库存明细',
                        id: 'ProductWh',
                        iconCls: 'workrecord-log',
                        /** items:[{
                         xtype:'DeskProductGridPanel1',
                         height:height,
                         }]**/
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!ProductWh';
                                var item = {
                                    itemId: 'ProductWhgrid',
                                    xtype: 'ATPOpDetailGridPanel1',
                                    height: height
                                };
                                var tabgrid = tab.getComponent('ProductWhgrid');
                                if (!tab.getComponent('ProductWhgrid')) {
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount("Desk!ProductWh", BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    },
                    {
                        title: '在  途',
                        iconCls: 'workrecord-log',
                        id: 'MPSPRonorder',
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!MPSPRonorder';
                                var item = {
                                    itemId: 'MPSPRonordergrid',
                                    xtype: 'ATPOpDetailGridPanel2',
                                    height: height,
                                };
                                var tabgrid = tab.getComponent('MPSPRonordergrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        //说明条件更新了
                                        tabgrid.getCount('MPSPRonordergrid', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    },
                    {
                        title: '预  约',
                        id: 'MakeCommit',
                        iconCls: 'workrecord-log',
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!MakeCommit';
                                var item = {
                                    itemId: 'MakeCommitgrid',
                                    xtype: 'ATPOpDetailGridPanel3',
                                    height: height
                                };
                                var tabgrid = tab.getComponent('MakeCommitgrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount('Desk!MakeCommit', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    },
                    {
                        title: '需  求',
                        iconCls: 'workrecord-log',
                        id: 'ATPNeed',
                        listeners: {
                            activate: function (tab) {
                                caller = 'ATPDATA';
                                var item = {
                                    itemId: 'ATPNeedgrid',
                                    xtype: 'ATPOpDetailGridPanel4'
                                };
                                var tabgrid = tab.getComponent('ATPNeedgrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount('ATPDATA', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    },
                    {
                        title: '供  应',
                        iconCls: 'workrecord-log',
                        id: 'ATPSupply',
                        listeners: {
                            activate: function (tab) {
                                caller = 'ATPDATA';
                                var item = {
                                    itemId: 'ATPSupplygrid',
                                    xtype: 'ATPOpDetailGridPanel5',
                                    height: height,
                                    region: 'north'
                                };
                                var tabgrid = tab.getComponent('ATPSupplygrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount('ATPDATA', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    },
                    {
                        title: '运算明细',
                        iconCls: 'workrecord-log',
                        id: 'ATPResultDetail',
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!ATPResultDetail';
                                var item = {
                                    itemId: 'ATPResultDetailgrid',
                                    xtype: 'ATPOpDetailGridPanel6',
                                    height: height,
                                    region: 'north'
                                };
                                var tabgrid = tab.getComponent('ATPResultDetailgrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount('Desk!ATPResultDetail', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    }
                    ]
                }]
            }]
        });
        me.callParent(arguments);
    },
    getCount: function (caller, condition) {
        var me = this;
        var info = {};
        Ext.Ajax.request({ //拿到grid的数据总数count
            url: basePath + '/common/datalistCount.action',
            async: false,
            params: {
                caller: caller,
                condition: condition,
                _noc:1
            },
            method: 'post',
            callback: function (options, success, response) {
                var res = new Ext.decode(response.responseText);
                if (res.exception || res.exceptionInfo) {
                    showError(res.exceptionInfo);
                    return;
                }
                dataCount = res.count;
                pageSize = 1000, info = me.getColumnsAndStore(caller, condition, page, pageSize);
            }
        });
        return info;
    },
    getColumnsAndStore: function (caller, condition, page, pageSize) {
        var info = {};
        var me = this;
        Ext.Ajax.request({ //拿到grid的columns
            url: basePath + 'common/datalist.action',
            async: false,
            params: {
                caller: caller,
                condition: condition,
                page: page,
                pageSize: pageSize
            },
            method: 'post',
            callback: function (options, success, response) {

                var res = new Ext.decode(response.responseText);
                if (res.exception || res.exceptionInfo) {
                    showError(res.exceptionInfo);
                    return;
                };
                var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : []; //一定要去掉多余逗号，ie对此很敏感      
                info.data = data;
                info.fields = res.fields;
                info.columns = res.columns;
            }
        });

        return info;
    },
    createGrid: function (store, column) {
        var grid = Ext.create('Ext.grid.Panel', {
            //emptyText : $I18N.common.grid.emptyText,
            columnLines: true,
            layout: 'fit',
            height: height,
            verticalScrollerType: 'paginggridscroller',
            loadMask: true,
            disableSelection: true,
            invalidateScrollerOnRefresh: false,
            viewConfig: {
                trackOver: false
            },
            id: 'grid3',
            selModel: Ext.create('Ext.selection.CheckboxModel', {
                headerWidth: 0
            }),
            xtype: 'gridpanel',
            plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
            columns: [],
            store: [],
        });
        return grid;
    }
});