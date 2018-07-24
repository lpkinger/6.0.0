
Ext.ux.buildChildGrid = function(conf) {
    var windowName = conf.windowName;
    var gridName = conf.gridName;
    var fields = conf.fields;
    var queryUrl = conf.queryUrl;
    var authUrl = conf.authUrl;
    for (var i = 0; i < fields.length; i++) {
        fields[i].dataIndex = fields[i].name;
    }

    if (this.getSelections().length <= 0){
        Ext.Msg.alert('提示', '请选择需要操作的记录！');
        return;
    }
    if (this.getSelections().length > 1){
        Ext.Msg.alert('提示', '不能选择多行记录！');
        return;
    }
    var parentId = this.getSelections()[0].get('id');

    if (!this[windowName]) {
        // 建一个角色数据映射数组
        var r = Ext.data.Record.create(fields);
        //设置数据仓库
        var ds = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy({url: queryUrl}),
            reader: new Ext.data.JsonReader({
                root: '',
                totalProperty: 'totalCount'
            }, r),
            // 远端排序开关
            remoteSort: false
        });
        //创建表格头格式
        var cm = new Ext.grid.ColumnModel(fields);
        var parentGrid = this;
        var grid = new Ext.grid.GridPanel( {
            ds: ds,
            cm: cm,
            selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
            enableColLock:false,
            loadMask: false,
            viewConfig: {
                forceFit: true
            },
            bbar: new Ext.Toolbar([{
                pressed: true,
                enableToggle:true,
                text: '授权',
                toggleHandler: function(){
                    //授权事件
                    var childModel = grid.getSelections();
                    var parentModel = parentGrid.getSelections();
                    if (childModel.length <= 0) {
                        Ext.Msg.alert('提示', '请选择至少一条记录！');
                        return;
                    } else if(childModel.length == 1) {
                        var parentId = parentModel[0].get('id');
                        var childId = childModel[0].get('id');
                        Ext.Ajax.request({
                            url: authUrl,
                            success: function(response) {
                                var o = Ext.decode(response.responseText);
                                if (o.success) {
                                    Ext.Msg.alert('提示', '操作成功！');
                                    ds.reload();
                                } else {
                                    Ext.Msg.alert('提示', o.msg);
                                }
                            },
                            params: 'auth=true&parentId=' + ds.baseParams.id + '&childId=' + childId
                        });
                    } else {
                        for(var i = 0, len = childModel.length; i < len; i++){
                            var parentId = parentModel[0].get('id');
                            var childId = childModel[i].get('id');
                            Ext.Ajax.request({
                                url: authUrl,
                                success: function(response) {
                                    var o = Ext.decode(response.responseText);
                                    if (o.success) {
                                        Ext.Msg.alert('提示', '操作成功！');
                                        ds.reload();
                                    } else {
                                        Ext.Msg.alert('提示', o.msg);
                                    }
                                },
                                params: 'auth=true&parentId=' + ds.baseParams.id + '&childId=' + childId
                            });
                        }
                    }
                }
            }, '-', {
                pressed: true,
                enableToggle: true,
                text: '取消授权',
                toggleHandler: function() {
                    //授权事件
                    var childModel = grid.getSelections();
                    var parentModel = parentGrid.getSelections();
                    if (childModel.length <= 0) {
                        Ext.Msg.alert('提示', '请选择至少一条记录！');
                        return;
                    } else if(childModel.length == 1) {
                        var parentId = parentModel[0].get('id');
                        var childId = childModel[0].get('id');
                        Ext.Ajax.request({
                            url: authUrl,
                            success: function(response) {
                                var o = Ext.decode(response.responseText);
                                if (o.success) {
                                    Ext.Msg.alert('提示', '操作成功！');
                                    ds.reload();
                                } else {
                                    Ext.Msg.alert('提示', o.msg);
                                }
                            },
                            params: 'auth=false&parentId=' + ds.baseParams.id + '&childId=' + childId
                        });
                    } else {
                        for(var i = 0, len = childModel.length; i < len; i++){
                            var parentId = parentModel[0].get('id');
                            var childId = childModel[i].get('id');
                            Ext.Ajax.request({
                                url: authUrl,
                                success: function(response) {
                                    var o = Ext.decode(response.responseText);
                                    if (o.success) {
                                        Ext.Msg.alert('提示', '操作成功！');
                                        ds.reload();
                                    } else {
                                        Ext.Msg.alert('提示', o.msg);
                                    }
                                },
                                params: 'auth=false&parentId=' + ds.baseParams.id + '&childId=' + childId
                            });
                        }
                    }
                }
            }])
        });
        this[gridName] = grid;
        this[windowName] = new Ext.Window({
            layout: 'fit',
            height: 300,
            width: 400,
            closeAction: 'hide',
            items: [grid]
        });
    }
    this[windowName].show();
    this[gridName].getStore().baseParams.id = parentId;
    this[gridName].getStore().reload();
};

//
Ext.ux.buildChildGridForTree = function(conf) {
    var windowName = conf.windowName;
    var gridName = conf.gridName;
    var fields = conf.fields;
    var queryUrl = conf.queryUrl;
    var authUrl = conf.authUrl;
    for (var i = 0; i < fields.length; i++) {
        fields[i].dataIndex = fields[i].name;
    }

    var node = this.getSelectionModel().getSelectedNode();
    if (node == null) {
        Ext.Msg.alert('提示', '请选择需要操作的记录！');
        return;
    }
    var parentId = node.id;

    if (!this[windowName]) {
        // 建一个角色数据映射数组
        var r = Ext.data.Record.create(fields);
        //设置数据仓库
        var ds = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy({url: queryUrl}),
            reader: new Ext.data.JsonReader({
                root: '',
                totalProperty: 'totalCount'
            }, r),
            // 远端排序开关
            remoteSort: false
        });
        //创建表格头格式
        var cm = new Ext.grid.ColumnModel(fields);
        var grid = new Ext.grid.GridPanel( {
            ds: ds,
            cm: cm,
            selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
            enableColLock:false,
            loadMask: false,
            viewConfig: {
                forceFit: true
            },
            bbar: new Ext.Toolbar([{
                pressed: true,
                enableToggle:true,
                text: '授权',
                toggleHandler: function(){
                    //授权事件
                    var childModel = grid.getSelections();
                    if (childModel.length <= 0) {
                        Ext.Msg.alert('提示', '请选择至少一条记录！');
                        return;
                    } else {
                        var childId = childModel[0].get('id');
                        Ext.Ajax.request({
                            url: authUrl,
                            success: function() {
                                Ext.Msg.alert('提示', '操作成功！');
                                ds.reload();
                            },
                            params: 'auth=true&parentId=' + ds.baseParams.id + '&childId=' + childId
                        });
                    }
                }
            }, '-', {
                pressed: true,
                enableToggle: true,
                text: '取消授权',
                toggleHandler: function() {
                    //授权事件
                    var childModel = grid.getSelections();
                    if (childModel.length <= 0) {
                        Ext.Msg.alert('提示', '请选择至少一条记录！');
                        return;
                    } else {
                        var childId = childModel[0].get('id');
                        Ext.Ajax.request({
                            url: authUrl,
                            success: function() {
                                Ext.Msg.alert('提示', '操作成功！');
                                ds.reload();
                            },
                            params: 'auth=false&parentId=' + ds.baseParams.id + '&childId=' + childId
                        });
                    }
                }
            }])
        });
        this[gridName] = grid;
        this[windowName] = new Ext.Window({
            layout: 'fit',
            height: 300,
            width: 400,
            closeAction: 'hide',
            items: [grid]
        });
    }
    this[windowName].show();
    this[gridName].getStore().baseParams.id = parentId;
    this[gridName].getStore().reload();
};