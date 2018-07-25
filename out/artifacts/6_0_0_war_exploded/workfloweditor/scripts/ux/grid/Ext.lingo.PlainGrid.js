/*
 * Ext JS Library 2.0.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 *
 * http://www.extjs.com/license
 *
 * @author Lingo
 * @since 2008-03-23
 * http://code.google.com/p/anewssystem/
 */
Ext.namespace("Ext.lingo");
/**
 * 拥有CRUD功能的表格.
 *
 * @param config    需要的配置{}
 */
Ext.lingo.PlainGrid = Ext.extend(Ext.grid.GridPanel, {
    loadMask: true,
    urlPagedQuery: "pagedQuery.do",
    pageSize: 15,

    // 初始化
    initComponent: function() {
        this.buildColumnModel();
        this.buildRecord();
        this.buildDataStore();
        this.buildToolbar();

        Ext.lingo.PlainGrid.superclass.initComponent.call(this);
    },

    // 初始化ColumnModel
    buildColumnModel: function() {
        this.sm = new Ext.grid.CheckboxSelectionModel();
        var columnHeaders = new Array();
        columnHeaders[0] = new Ext.grid.RowNumberer();
        columnHeaders[1] = this.sm;

        for (var i = 0; i < this.formConfig.length; i++) {
            var col = this.formConfig[i];
            if (col.hideGrid === true) {
                continue;
            }
            columnHeaders.push({
                header: col.fieldLabel,
                sortable: col.sortable,
                dataIndex: col.name,
                renderer: col.renderer
            });
        }
        this.cm = new Ext.grid.ColumnModel(columnHeaders);
        this.cm.defaultSortable = true;
        this.columnModel = this.cm;
    },

    buildRecord: function() {
        this.dataRecord = Ext.data.Record.create(this.formConfig);
    },

    buildDataStore: function() {
        this.store = new Ext.data.Store({
            proxy  : new Ext.data.HttpProxy({url:this.urlPagedQuery}),
            reader : new Ext.data.JsonReader({
                root          : "result",
                totalProperty : "totalCount",
                id            : "id"
            }, this.dataRecord),
            remoteSort : true
        });
        // this.store.setDefaultSort("id", "DESC");
    },

    buildToolbar: function() {
        // 把分页工具条，放在页脚
        var paging = new Ext.PagingToolbar({
            pageSize: this.pageSize,
            store: this.store,
            displayInfo: true,
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            plugins        : [new Ext.ux.PageSizePlugin()]
        });

        this.store.load({
            params:{start:0, limit:paging.pageSize}
        });
        this.bbar = paging;
    }
});

