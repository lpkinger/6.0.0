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
 * 拥有CRUD功能的树形.
 *
 * @param config 需要的配置{}
 */
Ext.lingo.JsonTree = Ext.extend(Ext.tree.TreePanel, {
    autoScroll: true,

    initComponent: function() {
        Ext.applyIf(this, {
            rootName: '分类',
            urlGetAllTree: "getAllTree.do",
            urlInsertTree: "insertTree.do",
            urlRemoveTree: "removeTree.do",
            urlSortTree: "sortTree.do",
            urlLoadData: "loadData.do",
            urlUpdateTree: "updateTree.do"
        });

        var loader = new Ext.tree.TreeLoader({
            dataUrl: this.urlGetAllTree
        })
        var root = new Ext.tree.AsyncTreeNode({
            text      : this.rootName,
            draggable : this.draggable,
            id        : '-1'
        });
        Ext.applyIf(this, {
            root: root,
            loader: loader,
            tbar: this.buildToolbar()
        });
        Ext.lingo.JsonTree.superclass.initComponent.call(this);

        // DEL快捷键，删除节点
        this.setupDeleteKey();
        // 自动排序
        if (this.folderSort) {
            new Ext.tree.TreeSorter(this, {folderSort:true});
        }
        this.buildEditor();
        this.buildContextMenu();
        this.buildDragDrop();

        //
        this.on('render', function() {
            this.loadMask = new Ext.LoadMask(this.bwrap, {
                store: loader
            });
        }, this);
    },

    setupDeleteKey: function() {
        // DEL快捷键，删除节点
        //this.on(Ext.EventObject.DELETE, this.removeNode);
        this.on('render', function() {
            this.body.on('keyup', function(e) {
                if(e.getKey() == e.DELETE) {
                    this.removeNode();
                }
            }, this);
        }, this);
    },

    buildToolbar: function() {
        var toolbar = [{
            text    : '新增下级分类',
            iconCls : 'add',
            tooltip : '添加选中节点的下级分类',
            handler : this.createChild.createDelegate(this)
        }, {
            text    : '新增同级分类',
            iconCls : 'go',
            tooltip : '添加选中节点的同级分类',
            handler : this.createBrother.createDelegate(this)
        }, {
            text    : '修改分类',
            iconCls : 'edit',
            tooltip : '修改选中分类',
            handler : this.updateNode.createDelegate(this)
        }, {
            text    : '删除分类',
            iconCls : 'delete',
            tooltip : '删除一个分类',
            handler : this.removeNode.createDelegate(this)
        }, '-', {
            text    : '保存排序',
            iconCls : 'save',
            tooltip : '保存排序结果',
            handler : this.save.createDelegate(this)
        }, '-', {
            text    : '展开',
            iconCls : 'expand',
            tooltip : '展开所有分类',
            handler : this.expandAll.createDelegate(this)
        }, {
            text    : '合拢',
            iconCls : 'collapse',
            tooltip : '合拢所有分类',
            handler : this.collapseAll.createDelegate(this)
        }, {
            text    : '刷新',
            iconCls : 'refresh',
            tooltip : '刷新所有节点',
            handler : this.refresh.createDelegate(this)
        }];
        return toolbar;
    },

    buildEditor: function() {
        var treeEditor = new Ext.tree.TreeEditor(this, {
            allowBlank    : false,
            blankText     : '请添写名称',
            selectOnFocus : true
        });
        this.treeEditor = treeEditor;

        treeEditor.on('beforestartedit', function() {
            var node = this.treeEditor.editNode;
            if(!node.attributes.allowEdit) {
                return false;
            } else {
                node.attributes.oldText = node.text;
            }
        }.createDelegate(this));

        treeEditor.on('complete', function(editor, value, startValue) {
            var node = this.treeEditor.editNode;
            // 如果节点没有改变，就向服务器发送修改信息
            if (startValue == value) {
                return true;
            }
            var item = {
                id       : node.id,
                text     : value,
                parentId : node.parentNode.id
            };
            var params = 'id=' + node.id + '&name=' + value + '&parentId=' + node.parentNode.id;

            this.el.mask('提交数据，请稍候...', 'x-mask-loading');
            var hide = this.el.unmask.createDelegate(this.el);
            var doSuccess = function(responseObject) {
                eval("var o = " + responseObject.responseText + ";");
                this.treeEditor.editNode.id = o.info.id;
                hide();
            }.createDelegate(this);
            Ext.lib.Ajax.request(
                'POST',
                this.urlInsertTree,
                {success:doSuccess,failure:hide},
                params
            );
        }.createDelegate(this));
    },

    buildContextMenu: function() {
        // 右键菜单
        this.on('contextmenu', this.prepareContext);
        this.contextMenu = new Ext.menu.Menu({
            id    : 'copyCtx',
            items : [{
                id      : 'createChild',
                handler : this.createChild.createDelegate(this),
                iconCls : 'add',
                text    : '新增下级节点'
            },{
                id      : 'createBrother',
                handler : this.createBrother.createDelegate(this),
                iconCls : 'go',
                text    : '新增同级节点'
            },{
                id      : 'updateNode',
                handler : this.updateNode.createDelegate(this),
                iconCls : 'edit',
                text    : '修改节点'
            },{
                id      : 'remove',
                handler : this.removeNode.createDelegate(this),
                iconCls : 'delete',
                text    : '删除节点'
            },'-',{
                id      : 'expand',
                handler : this.expandAll.createDelegate(this),
                iconCls : 'expand',
                text    : '展开'
            },{
                id      : 'collapse',
                handler : this.collapseAll.createDelegate(this),
                iconCls : 'collapse',
                text    : '合拢'
            },{
                id      : 'refresh',
                handler : this.refresh.createDelegate(this),
                iconCls : 'refresh',
                text    : '刷新'
            },{
                id      : 'config',
                iconCls : 'config',
                handler : this.configInfo.createDelegate(this),
                text    : '详细配置'
            }]
        });
    },

    buildDragDrop: function() {
        // 拖拽判断
        this.on("nodedragover", function(e){
          var n = e.target;
          if (n.leaf) {
            n.leaf = false;
          }
          return true;
        });
        // 拖拽后，就向服务器发送消息，更新数据
        // 本人不喜欢这种方式
        if (this.dropUpdate) {
          this.on('nodedrop', function(e) {
            var n = e.dropNode;
            var item = {
              id       : n.id,
              text     : n.text,
              parentId : e.target.id
            };

            this.el.mask('提交数据，请稍候...', 'x-mask-loading');
            var hide = this.el.unmask.createDelegate(this.el);
            Ext.lib.Ajax.request(
              'POST',
              this.urlInsertTree,
              {success:hide,failure:hide},
              'data=' + encodeURIComponent(Ext.encode(item))
            );
          });
        } else {
          this.on('nodedrop', function(e) {
            var n = e.dropNode;
            n.ui.textNode.style.fontWeight = "bold";
            n.ui.textNode.style.color = "red";
            n.ui.textNode.style.border = "1px red dotted";
          });
        }
    },

    createChild : function() {
        var sm = this.getSelectionModel();
        var n = sm.getSelectedNode();
        if (!n) {
            n = this.getRootNode();
        } else {
            n.expand(false, false);
        }
        this.createNode(n);
    },

    createBrother : function() {
        var n = this.getSelectionModel().getSelectedNode();
        if (!n) {
            Ext.Msg.alert('提示', "请选择一个节点");
        } else if (n == this.getRootNode()) {
            Ext.Msg.alert('提示', "不能为根节点增加同级节点");
        } else {
            this.createNode(n.parentNode);
        }
    },

    createNode : function(n) {
        var node = n.appendChild(new Ext.tree.TreeNode({
            id            : Ext.id(),
            text          : '请输入分类名',
            cls           : 'album-node',
            allowDrag     : true,
            allowDelete   : true,
            allowEdit     : true,
            allowChildren : true
        }));
        this.getSelectionModel().select(node);
        setTimeout(function(){
            this.treeEditor.editNode = node;
            this.treeEditor.startEdit(node.ui.textNode);
        }.createDelegate(this), 10);
    },

    updateNode: function() {
        var n = this.getSelectionModel().getSelectedNode();
        if (!n) {
            Ext.Msg.alert('提示', "请选择一个节点");
        } else if (n == this.getRootNode()) {
            Ext.Msg.alert('提示', "不能修改根节点");
        } else {
            setTimeout(function(){
                this.treeEditor.editNode = n;
                this.treeEditor.startEdit(n.ui.textNode);
            }.createDelegate(this), 10);
        }
    },

    removeNode: function() {
        var sm = this.getSelectionModel();
        var n = sm.getSelectedNode();
        if (n == null) {
            Ext.Msg.alert('提示', "请选择一个节点");
        } else if(n.attributes.allowDelete) {
            Ext.Msg.confirm("提示", "是否确定删除？", function(btn, text) {
                if (btn == 'yes') {
                    this.getSelectionModel().selectPrevious();
                    this.el.mask('提交数据，请稍候...', 'x-mask-loading');
                    // var hide = this.treePanel.el.unmask.createDelegate(this.treePanel.el);
                    var hide = function() {
                        this.el.unmask(this.el);
                        n.parentNode.removeChild(n);
                    }.createDelegate(this);
                    Ext.lib.Ajax.request(
                        'POST',
                        this.urlRemoveTree,
                        {success:hide,failure:hide},
                        'id=' + n.id
                    );
                }
            }.createDelegate(this));
        } else {
            Ext.Msg.alert("提示", "这个节点不能删除");
        }
    },

    appendNode: function(node, array) {
        if (!node || node.childNodes.length < 1) {
            return;
        }
        for (var i = 0; i < node.childNodes.length; i++) {
            var child = node.childNodes[i];
            array.push({id:child.id,parentId:child.parentNode.id});
            this.appendNode(child, array);
        }
    },

    save: function() {
        // 向数据库发送一个json数组，保存排序信息
        this.el.mask('提交数据，请稍候...', 'x-mask-loading');
        // var hide = this.treePanel.el.unmask.createDelegate(this.treePanel.el);
        var hide = function() {
            this.el.unmask(this.el);
            this.refresh();
        }.createDelegate(this);
        var ch = [];
        this.appendNode(this.root, ch);

        Ext.lib.Ajax.request(
            'POST',
            this.urlSortTree,
            {success:hide,failure:hide},
            'data=' + encodeURIComponent(Ext.encode(ch))
        );
    },

    collapseAll: function() {
        this.contextMenu.hide();
        setTimeout(function() {
            var node = this.getSelectedNode();
            if (node == null) {
                this.getRootNode().eachChild(function(n) {
                    n.collapse(true, false);
                });
            } else {
                node.collapse(true, false);
            }
        }.createDelegate(this), 10);
    },

    expandAll: function() {
        this.contextMenu.hide();
        setTimeout(function() {
            var node = this.getSelectedNode();
            if (node == null) {
                this.getRootNode().eachChild(function(n) {
                    n.expand(false, false);
                });
            } else {
                node.expand(false, false);
            }
        }.createDelegate(this), 10);
    },

    prepareContext: function(node, e) {
        node.select();
        if (this.contextMenu.items.get('remove')) {
            this.contextMenu.items.get('remove')[node.attributes.allowDelete ? 'enable' : 'disable']();
        }
        if (this.contextMenu.items.get('updateNode')) {
            this.contextMenu.items.get('updateNode')[node.attributes.allowEdit ? 'enable' : 'disable']();
        }
        if (this.contextMenu.items.get('expand')) {
            this.contextMenu.items.get('expand')[node.attributes.leaf ? 'disable' : 'enable']();
        }
        if (this.contextMenu.items.get('collapse')) {
            this.contextMenu.items.get('collapse')[node.attributes.leaf ? 'disable' : 'enable']();
        }
        this.contextMenu.showAt(e.getXY());
    },

    refresh: function() {
        this.root.reload();
        this.root.expand(false, false);
    },

    configInfo: function() {
        if (!this.dialog) {
            this.createDialog();
        }

        var n = this.getSelectedNode();
        this.dialog.show(this.getSelectionModel().getSelectedNode().ui.textNode);
        this.formPanel.load({
            url: this.urlLoadData + "?id=" + n.id
        });
    },

    // 生成对话框
    createDialog : function() {
        var reader = new Ext.data.JsonReader({}, this.formConfig);
        this.formPanel = new Ext.form.FormPanel({
            defaultType: 'textfield',
            labelAlign: 'right',
            labelWidth: 70,
            frame: true,
            autoScroll: true,
            title: '详细信息',
            reader: reader,
            url: this.urlUpdateTree,
            items: this.formConfig,
            buttons: [{
                text: '确定',
                handler: function() {
                    this.formPanel.getForm().submit({
                        waitTitle: "请稍候",
                        waitMsg : '正在提交......',
                        success: function() {
                            this.dialog.hide();
                            this.refresh();
                        }.createDelegate(this),
                        failure: function() {
                        }
                    });
                }.createDelegate(this)
            },{
                text: '取消',
                handler: function() {
                    this.dialog.hide();
                }.createDelegate(this)
            }]
        });
        this.dialog = new Ext.Window({
            layout: 'fit',
            width: this.dlgWidth ? this.dlgWidth : 400,
            height: this.dlgHeight ? this.dlgHeight : 300,
            closeAction: 'hide',
            items: [this.formPanel]
        });
    },

    // 返回当前选中的节点，可能为null
    getSelectedNode: function() {
        return this.getSelectionModel().getSelectedNode();
    },

    onDestroy : function(){
        if(this.rendered){
            if (this.contextMenu) {
                this.contextMenu.destroy();
            }
            if (this.dialog) {
                this.dialog.destroy();
            }
        }
        Ext.lingo.JsonTree.superclass.onDestroy.call(this);
    }
});
