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
Ext.lingo.EditGrid = Ext.extend(Ext.grid.EditorGridPanel, {
    closable: true,
    clicksToEdit: 1,
    loadMask: true,
    removedRecords: [],
    insertedRecords: [],
    pageSize: 15,

    // 初始化
    initComponent: function() {
        this.buildColumnModel();
        this.buildRecord();
        this.buildDataStore();
        this.buildToolbar();

        Ext.lingo.EditGrid.superclass.initComponent.call(this);

        this.store.on('load', function() {
            this.removedRecords = [];
            this.insertedRecords = [];
        }, this);

        this.store.on('beforeload', function() {

            if (this.isDirty()) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '是否保存本页的修改',
                    buttons: Ext.MessageBox.YESNOCANCEL,
                    fn: function(btn) {
                        if (btn == 'yes') {
                            this.save();
                        } else if (btn == 'no') {
                            this.cancel();
                        } else {
                        }
                    },
                    scope: this
                });
                return false;
            }
        }, this);
    },

    // 初始化ColumnModel
    buildColumnModel: function() {
        var columnHeaders = new Array();
        columnHeaders[0] = new Ext.grid.RowNumberer();

        for (var i = 0; i < this.formConfig.length; i++) {
            var col = this.formConfig[i];
            if (col.hideGrid === true) {
                continue;
            }
            var column = {
                header: col.fieldLabel,
                sortable: col.sortable,
                dataIndex: col.name
            };
            if (typeof(col.editor) != 'undefined') {
                column.editor = new Ext.grid.GridEditor(col.editor);
            }
            if (col.renderer) {
                column.renderer = col.renderer;
            }
            columnHeaders.push(column);
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
        //
        var checkItems = new Array();
        for (var i = 0; i < this.formConfig.length; i++) {
            var meta = this.formConfig[i];
            if (meta.showInGrid === false) {
                continue;
            }
            var item = new Ext.menu.CheckItem({
                text         : meta.fieldLabel,
                value        : meta.name,
                checked      : true,
                group        : "filter",
                checkHandler : this.onItemCheck.createDelegate(this)
            });
            checkItems[checkItems.length] = item;
        }

        this.filterButton = new Ext.Toolbar.MenuButton({
            iconCls  : "refresh",
            text     : this.formConfig[0].fieldLabel,
            tooltip  : "选择搜索的字段",
            menu     : checkItems,
            minWidth : 105
        });
        // 输入框
        this.filter = new Ext.form.TextField({
            'name': 'filter'
        });
        this.filter.on('specialkey', this.onFilterKey.createDelegate(this));
        //
        this.tbar = new Ext.Toolbar([{
            id: 'addMenu',
            text: '添加',
            iconCls : 'add',
            tooltip : '添加',
            handler: this.add.createDelegate(this)
        },{
            id: 'deleteMenu',
            text: '删除',
            iconCls : 'delete',
            tooltip : '删除',
            handler: this.del.createDelegate(this)
        },{
            id: 'saveMenu',
            text: '提交修改',
            iconCls : 'edit',
            tooltip : '提交修改',
            handler: this.save.createDelegate(this)
        },{
            id: 'cancelMenu',
            text: '取消',
            iconCls: 'go',
            tooltip: '取消',
            handler: this.cancel.createDelegate(this)
        },{
            id: 'refreshMenu',
            text: '刷新',
            iconCls: 'refresh',
            tooltip: '刷新',
            handler: this.refresh.createDelegate(this)
        }, '->', this.filterButton, this.filter]);

        // 把分页工具条，放在页脚
        var paging = new Ext.PagingToolbar({
            pageSize: this.pageSize,
            store: this.store,
            displayInfo: true,
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            plugins: [new Ext.ux.PageSizePlugin()]
        });

        this.store.load({
            params:{start:0, limit:paging.pageSize}
        });
        this.bbar = paging;
    },

    // 选中搜索属性选项时
    onItemCheck: function(item, checked) {
        if(checked) {
            this.filterButton.setText(item.text + ':');
            this.filter.setValue('');
            // this.store.baseParams.filterTxt = item.value;
            // this.store.baseParams.filterValue = '';
        }
    },

    // 监听模糊搜索框里的按键
    onFilterKey: function(field, e) {
        var filterTxt = this.store.baseParams.filterTxt;
        if (typeof filterTxt == 'undefined' || filterTxt == '') {
            Ext.Msg.alert('提示', '请先选择搜索的字段');
            return;
        }
        if(e.getKey() == e.ENTER && field.getValue().length > 0) {
            this.store.baseParams.filterValue = field.getValue();
        } else if (e.getKey() == e.BACKSPACE && field.getValue().length() === 0) {
            this.store.baseParams.filterValue = '';
        } else {
            return;
        }
        // delete this.store.lastOptions.params.meta;
        this.store.reload();
    },

    // 添加一行
    add: function() {
        this.stopEditing();

        var p = new this.dataRecord();

        var initValue = {};
        for (var i in p.fields.items) {
            var item = p.fields.items[i];
            if (item.name) {
                initValue[item.name] = null;
            }
        }
        p = new this.dataRecord(initValue);
        this.stopEditing();
        this.store.insert(0, p);
        this.startEditing(0, 0);

        p.dirty = true;
        p.modified = initValue;
        if(this.store.modified.indexOf(p) == -1){
            this.store.modified.push(p);
        }
        if (this.insertedRecords.indexOf(p) == -1) {
            this.insertedRecords.push(p);
        }
    },

    // 删除一行
    del: function() {
        this.stopEditing();

        Ext.Msg.confirm('信息', '确定要删除？', function(btn){
            if (btn == 'yes') {
                var sm = this.getSelectionModel();
                var cell = sm.getSelectedCell();

                var record = this.store.getAt(cell[0]);
                if(this.store.modified.indexOf(record) != -1){
                    this.store.modified.remove(record);
                }
                // 记录删除了哪些id
                var id = record.get('id');
                if (id == null && this.insertedRecords.indexOf(record) != -1) {
                    this.insertedRecords.remove(record);
                } else if (id != null && this.removedRecords.indexOf(record) == -1) {
                    this.removedRecords.push(record);
                }

                this.store.remove(record);
            }
        }, this);
    },

    // 提交修改
    save: function() {
        this.stopEditing();

        var m = this.store.modified.slice(0);

        for (var i = 0; i < m.length; i++) {
            var record = m[i];
            var fields = record.fields.keys;

            for (var j = 0; j < fields.length; j++) {
                var name = fields[j];
                var value = record.data[name];

                var colIndex = this.getColumnModel().findColumnIndex(name);
                if (colIndex == -1) {
                    continue;
                }
                var rowIndex = this.store.indexOfId(record.id);
                var editor = this.getColumnModel().getCellEditor(colIndex);

                if (editor && !editor.field.validateValue(value ? value : '')) {
                    Ext.Msg.alert('提示', '请确保输入的数据正确。', function(){
                        this.startEditing(rowIndex, colIndex);
                    }, this);
                    return;
                }
            }
        }

        // 进行到这里，说明数据都是有效的
        var data = [];
        Ext.each(m, function(p) {
            var value = {};
            for (var i in p.fields.items) {
                var item = p.fields.items[i];
                if (item.name) {
                    value[item.name] = p.get(item.name);
                }
            }
            data.push(value);
        });
        var removedIds = [];
        Ext.each(this.removedRecords, function(item) {
            removedIds.push(item.get('id'));
        });

        if (!this.isDirty()) {
            // 没有修改，不需要提交
            return;
        }

        this.loadMask.show();
        Ext.Ajax.request({
            method: 'POST',
            url: this.urlSubmit,
            success: function(response) {
                var json = Ext.decode(response.responseText)
                this.loadMask.hide();

                if (json.success) {
                    Ext.Msg.alert('信息', json.message, this.refresh.createDelegate(this));
                } else {
                    Ext.Msg.alert('错误', json.message);
                }
            }.createDelegate(this),
            failure: function(){
                Ext.Msg.alert("错误", "与后台联系的时候出现了问题");
            },
            params: 'removedIds=' + removedIds +
                '&data=' + encodeURIComponent(Ext.encode(data))
        });
    },

    // 取消修改
    cancel: function() {
        this.stopEditing();

        // 返还修改
        this.store.rejectChanges();
        // 删除添加
        if (this.insertedRecords.length > 0) {
            for (var i = 0; i < this.insertedRecords.length; i++) {
                var p = this.insertedRecords[i];
                this.store.remove(p);
            }
            this.insertedRecoreds = [];
        }
        // 回复删除
        if (this.removedRecords.length > 0) {
            for (var i = 0; i < this.removedRecords.length; i++) {
                var p = this.removedRecords[i];
                this.store.add(p);
            }
            this.removedRecords = [];
        }
    },

    // 刷新表格数据
    refresh : function() {
        this.cancel();
        this.store.reload();
    },

    // 是否有改动
    isDirty: function() {
        return this.store.modified.length != 0 || this.removedRecords.length != 0;
    }
});

