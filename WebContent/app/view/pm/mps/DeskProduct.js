Ext.define('erp.view.pm.mps.DeskProduct', {
    extend: 'Ext.Viewport',
    layout: 'anchor',
    hideBorders: true,
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
                items: [{
                    xtype: "erpDeskProductFormPanel",
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
                        /** items:[{
                         xtype:'DeskProductGridPanel1',
                         height:height,
                         }]**/
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!ProductWh';
                                var item = {
                                    itemId: 'ProductWhgrid',
                                    xtype: 'DeskProductGridPanel1',
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
                        id: 'MPSPRonorder',
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!MPSPRonorder';
                                var item = {
                                    itemId: 'MPSPRonordergrid',
                                    xtype: 'DeskProductGridPanel2',
                                    height: height
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
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!MakeCommit';
                                var item = {
                                    itemId: 'MakeCommitgrid',
                                    xtype: 'DeskProductGridPanel3',
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
                        id: 'MPSNeed',
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!MPSNeed';
                                var item = {
                                    itemId: 'MPSNeedgrid',
                                    xtype: 'DeskProductGridPanel4'
                                };
                                var tabgrid = tab.getComponent('MPSNeedgrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount('Desk!MPSNeed', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    },
                    {
                        title: '供  应',
                        id: 'MPSSupply',
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!MPSSupply';
                                var item = {
                                    itemId: 'MPSSupplygrid',
                                    xtype: 'DeskProductGridPanel5',
                                    height: height,
                                    region: 'north'
                                };
                                var tabgrid = tab.getComponent('MPSSupplygrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount('Desk!MPSSupply', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    },
                    {
                        title: '运算明细',
                        id: 'MrpResultDetail',
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!MrpResultDetail';
                                var item = {
                                    itemId: 'MrpResultDetailgrid',
                                    xtype: 'DeskProductGridPanel6',
                                    height: height,
                                    region: 'north'
                                };
                                var tabgrid = tab.getComponent('MrpResultDetailgrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount('Desk!MrpResultDetail', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                    },
                    {
                        title: '历史明细',
                        id: 'MrpResultDetail!His',
                        listeners: {
                            activate: function (tab) {
                                caller = 'Desk!MrpResultDetail!His';
                                var item = {
                                    itemId: 'MrpResultDetail!Hisgrid',
                                    xtype: 'DeskProductGridPanel7',
                                    height: height,
                                    region: 'north'
                                };
                                var tabgrid = tab.getComponent('MrpResultDetail!Hisgrid');
                                if (!tabgrid) {
                                    item.LastCondition = BaseQueryCondition;
                                    tab.add(item);
                                } else {
                                    if (tabgrid.LastCondition != BaseQueryCondition) {
                                        tabgrid.getCount('Desk!MrpResultDetail!His', BaseQueryCondition);
                                        tabgrid.LastCondition = BaseQueryCondition;
                                    }
                                }
                            }
                        }
                        
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
            store: []
        });
        return grid;
    }
});