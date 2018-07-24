Ext.define('Ext.ux.Deletecheckcolumn', {
    extend: 'Ext.grid.column.Column',
    alias: 'widget.Deletecheckcolumn',
    constructor: function() {
        this.addEvents(
            'checkchange'
        );
        this.callParent(arguments);
        this.sortable = false;
        if(!this.renderer){
        	this.renderer = this.rendererFn;
        }
        var me = this;
        fn = function(ch){
        	me.selectAll(ch.getAttribute('grid'), ch.getAttribute('cm'), ch.checked);
        };
    },
    headerCheckable: false,
    singleChecked: false,
    listeners: {
    	afterrender: function(){
    		//console.log("render");
    		if(this.headerCheckable) {
    			this.setText("<input type='checkbox' id='" + this.dataIndex + "-checkbox' grid='" + 
       				 this.ownerCt.ownerCt.id + "' cm='" + this.dataIndex + "' onclick='fn(this);'/>" + this.text);
    		}
    		if(this.singleChecked) {
    			this.on('checkchange', this.onSingleCheck, this, {delay: 100});
    		}
        }
    },
    /**
     * @private
     * Process and refire events routed from the GridView's processEvent method.
     */
    processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
        	if ((view.store.data.items[recordIndex].data.sn_num)&&(view.store.data.items[recordIndex].data.sn_num)!="")
	    	{
	    		showError('该节点存在标识号，不允许勾选删除');
	    		return ;
	    	}
        	var record = null;
        	var dataIndex = this.dataIndex;
        	var checked = null;
        	if(view.panel.store.tree){//treegrid
        		var tree = Ext.ComponentQuery.query('treepanel')[0];
        		tree.getRecordByRecordIndex(recordIndex);
        		record = tree.findRecord;
        		checked = !record.get(dataIndex);
        		//如果父节点checked，就把其子孙节点checked,否则unchecked
        		tree.checkRecord(record, dataIndex, checked);
        	} else {//普通的grid
        		record = view.panel.store.getAt(recordIndex);
        		checked = !record.get(dataIndex);
        	}
            record.set(dataIndex, checked);
            this.fireEvent('checkchange', this, recordIndex, checked);
            // cancel selection.
            return false;
        } else {
            return this.callParent(arguments);
        }
        
    	
    },

    // Note: class names are not placed on the prototype bc renderer scope
    // is not in the header.
    rendererFn : function(value, m, record){
        var cssPrefix = Ext.baseCSSPrefix,
            cls = [cssPrefix + 'grid-checkheader'];

        if (value) {
            cls.push(cssPrefix + 'grid-checkheader-checked');
        }
        return '<div class="' + cls.join(' ') + '">&#160;</div>';
    },
    /**
     * (取消)全选
     */
    selectAll: function(g, c, checked){
    	var grid = Ext.getCmp(g);
    	if(!grid.store)
    		grid = grid.ownerCt;
    	if(grid && grid.store.data){
    		if(checked){
    			grid.store.each(function(){
        			if(!this.get(c)) {
        				this.set(c, true);
        			}
        		});
    		} else {
    			grid.store.each(function(){
        			if(this.get(c)) {
        				this.set(c, false);
        			}
        		});
    		}
    	} else if(grid.store.tree){//tree grid
    		var items = grid.store.tree.root.childNodes;
    		Ext.each(items, function(item){
    			
    		});
    	}
    },
    onSingleCheck: function(cm, rIdx, check) {
    	if(check) {
    		var grid = this.up('grid'), field = this.dataIndex;
        	grid.store.each(function(r, i){
        		if(i != rIdx && r.get(field)) {
        			r.set(field, false);
        		}
        	});
    	}
    }
});

/**
 * ERP项目gridpanel样式5:sysNavigation专用treegrid
 */
Ext.define('erp.view.core.grid.SysTreeGrid',{ 
    extend: 'Ext.tree.Panel', 
    alias: 'widget.erpSysTreeGrid',
    region: 'south',
    layout : 'fit',
    id: 'treegrid', 
    emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    singleExpand: true,
    /*autoScroll:true,*/
    saveNodes: [],
    updateNodes: [],
    deleteNodes: [],
    lockable: true,
    cls: 'custom',
    columns : [ {
            "header" : "ID",
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_id",
            "align" : "left",
            "xtype" : "treecolumn",
            "readOnly" : false,
            "hidden" : true,
            "text" : "ID"
            }, {
            header : "描述",
            dbfind : "",
            cls : "x-grid-header-1",
            summaryType : "",
            dataIndex : "sn_displayname",
            align : "left",
            sortable : true,
            xtype : "treecolumn",
            /* "readOnly" : false, */
            hidden : false,
            width : 260.0,
            text : "描述",
            editor : {
            	  "xtype" : "triggerfield",
                  hideTrigger:true,
                  selectOnFocus : true,
                  allowOnlyWhitespace : false
                    }
           },{
                "dbfind" : "",
                "cls" : "x-grid-header-1",
                "summaryType" : "",
                "dataIndex" : "sn_parentid",
                "align" : "left",
                "xtype" : "treecolumn",
                "readOnly" : false,
                "hidden" : true,
                "width" : 0.0,
                "text" : "父节点ID"
           },{
                "dbfind" : "",
                "cls" : "x-grid-header-1",
                "dataIndex" : "sn_num",
                "align" : "left",              
                "readOnly" : false,
                "hidden" : true,
                "width" : 0,
                "text" : "标识号"
           },{
        	   "dbfind" : "",
        	   "cls" : "x-grid-header-1",
        	   "summaryType" : "",
        	   "dataIndex" : "sn_url",
        	   "align" : "left",
        	   "readOnly" : false,
        	   "hidden" : false,
        	   "width" : 400.0,
        	   "text" : "链接",
        	   "editor" : {
                      "xtype" : "triggerfield",
                      hideTrigger:true
               }
          },{
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_detno",
            "align" : "center",
            "readOnly" : false,
            "hidden" : false,
            "width" : 40.0,
            "text" : "顺序",
            "editor" : {
                        "xtype" : "numberfield"
                        }
        }, {
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_isleaf",
            "align" : "left",
            "xtype" : "checkcolumn",
            "readOnly" : false,
            "hidden" : false,
            "width" : 60.0,
            "text" : "是否<br>叶节点",
          /*  "headerCheckable" : false,*/
           /* "editor" : {
                        "cls" : "x-grid-checkheader-editor",
                        "bodyStyle": 'background:#ffffff; padding:10px;visibility:visible;',
                        "xtype" : "checkbox"
                        }*/
        }, {
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_deleteable",
            "align" : "left",
            "xtype" : "Deletecheckcolumn",
            "readOnly" : false,
            "hidden" : false,
            "width" : 60,
            "text" : "允许<br>删除",
            "headerCheckable" : false,
           /* "editor" : {
                        "cls" : "x-grid-checkheader-editor",
                        "xtype" : "checkbox"
                        }*/
        }, {
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_using",
            "align" : "left",
            "xtype" : "checkcolumn",
            "readOnly" : false,
            "hidden" : false,
            "width" : 60.0,
            "text" : "是否<br>启用",
            "headerCheckable" : false,
            /*"editor" : {
                        "cls" : "x-grid-checkheader-editor",
                        "xtype" : "checkbox"
                        }*/
        }, {
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_limit",
            "align" : "left",
            "xtype" : "checkcolumn",
            "readOnly" : false,
            "hidden" : false,
            "width" : 60.0,
            "text" : "启用<br>权限",
            "headerCheckable" : false,
            /*"editor" : {
                        "cls" : "x-grid-checkheader-editor",
                        "xtype" : "checkbox"
                        }*/
        }, {
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_logic",
            "align" : "left",
            "xtype" : "checkcolumn",
            "headerCheckable" : false,
            "readOnly" : false,
            "hidden" : false,
            "width" : 0,
            "text" : "允许<br>扩展",
            "editor" : {
                        "cls" : "x-grid-checkheader-editor",
                        "xtype" : "checkbox"
                        }
        }, {
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_caller",
            "align" : "left",
            "readOnly" : false,
            "hidden" : false,
            "width" : 120.0,
            "text" : "页面Caller",
            "editor" : {
            	  "xtype" : "triggerfield",
                  hideTrigger:true
                        }
        }, {
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_showmode",
            "align" : "left",
            "readOnly" : false,
            "hidden" : false,
            "width" : 80.0,
            "text" : "打开模式",
            "editor" : {
                        "cls" : "x-grid-checkheader-editor",
                        "xtype" : "combo",
                        "store" : Ext.create('Ext.data.Store', {
                        fields : [ 'display', 'value' ],
                        data : [ {
                                    "display" : "选项卡模式",
                                    "value" : 0
                                }, {
                                    "display" : "弹出框式",
                                    "value" : 1
                                }, {
                                    "display" : "空白页",
                                    "value" : 2
                                }, {
                                    "display" : "窗口模式",
                                    "value" : 3
                                } ]
                            }),
                            "displayField" : 'display',
                            "valueField" : 'value',
                            "queryMode" : 'local',
                            "value" : 0
                        },
                        "renderer" : function(val) {
                            var rVal = "选项卡模式";
                            val = val || 0;
                            switch (Number(val)) {
                            case 0:
                                rVal = "选项卡模式";
                                break;
                            case 1:
                                rVal = "弹出框式";
                                break;
                            case 2:
                                rVal = "空白页";
                                break;
                            case 3:
                                rVal = "窗口模式";
                                break;
                            }
                            return rVal;
                        }
            }, {
                "dbfind" : "",
                "cls" : "x-grid-header-1",
                "summaryType" : "",
                "dataIndex" : "sn_addurl",
                "align" : "left",
                "readOnly" : false,
                "hidden" : false,
                "width" : 120.0,
                "text" : "扩展路径",
                "editor" : {
                	  "xtype" : "triggerfield",
                      hideTrigger:true
                        }
            } ,{
                "dbfind" : "",
                "cls" : "x-grid-header-1",
                "summaryType" : "",
                "dataIndex" : "sn_show",
                "align" : "left",
                "xtype" : "checkcolumn",
                "readOnly" : false,
                "hidden" : false,
                "width" : 60.0,
                "text" : "权限<br>申请",
                "headerCheckable" : false,
                "editor" : {
                            "cls" : "x-grid-checkheader-editor",
                            "xtype" : "checkbox"
                            }
            }, {
                "dbfind" : "",
                "cls" : "x-grid-header-1",
                "summaryType" : "",
                "dataIndex" : "sn_standardDesc",
                "align" : "left",
                "readOnly" : false,
                "hidden" : false,
                "width" : 120.0,
                "text" : "标准描述",
                "editor" : {
                	  "xtype" : "triggerfield",
                      hideTrigger:true
                }
            }],
    tbar: {margin:'0 0 5 0',style:{background:'#fff'},items:[{
        iconCls: 'tree-nav-add',
        cls: 'x-btn-gray',
        text: $I18N.common.button.erpAddButton,
        handler: function(){
            var treegrid = Ext.getCmp('treegrid');
            var items = treegrid.selModel.selected.items;
            if(items.length > 0 && items[0].isLeaf() == true){
                if(items[0].data['sn_id'] == null || items[0].data['sn_id'] == ''){
                    warnMsg('如果在该节点下添加子节点，需先保存该节点，是否保存?', function(btn){
                        if(btn == 'yes'){
                            if(items[0].data['sn_displayname'] == null || items[0].data['sn_displayname'] == ''){
                                showError('请先描述该节点');
                                return;
                            } else {
                                items[0].data['leaf'] = false;
                                items[0].data['cls'] = 'x-tree-cls-parent';
                                items[0].data['sn_isleaf'] = false;
                                treegrid.saveNodes.push(items[0]);
                                treegrid.saveNode();
                            }
                        } else if(btn == 'no'){
                            return;
                        } 
                    });
                } else {
                    items[0].data['leaf'] = false;
                    items[0].data['cls'] = 'x-tree-cls-parent';
                    items[0].data['sn_isleaf'] = false;
                    items[0].dirty = true;
                    var o = {
                            sn_parentid: items[0].data['sn_id'],
                            sn_isleaf: true,
                            cls: "x-tree-cls-node",
                            parentId: items[0].data['sn_id'],
                            leaf: true,
                            detno: 0,
                            sn_detno: 0,
                            sn_deleteable: true,
                            deleteable: true,
                            allowDrag: true,
                            showMode: 0,
                            sn_logic: 0,
                            sn_limit: 1,                            
                            sn_show: 1
                    };
                    items[0].appendChild(o);
                    items[0].expand(true);
                }
            } else {
                var record = treegrid.getExpandItem();
                var detno = null;
                if(record){
                    if(record.childNodes.length==0){
                        detno = 1;
                    } else {
                        detno = record.childNodes[record.childNodes.length-1].data['sn_detno'] + 1;
                    }
                    var o = {
                            sn_parentid: record.data['sn_id'],
                            sn_isleaf: true,
                            cls: "x-tree-cls-node",
                            parentId: record.data['sn_id'],
                            sn_deleteable: true,
                            deleteable: true,
                            leaf: true,
                            detno: detno,
                            sn_detno: detno,
                            allowDrag: true,
                            showMode: 0,
                            sn_logic: 0,
                            sn_limit: 1,
                            sn_show: 1
                    };
                    record.appendChild(o);
                }
            }
        }
    },{
        iconCls: 'tree-nav-add',
        cls: 'x-btn-gray',
        text: '添加根节点',
        margin:'0 0 0 5',
        handler: function(){
            var me=this;
            var save = new Array();
            treegrid = Ext.getCmp('treegrid');
            var o = {
                    sn_id: -1,
                    sn_displayname: '新添加的根节点',
                    sn_url: '',
                    sn_detno: 0,
                    sn_isleaf:'F',
                    sn_tabtitle: '',
                    sn_parentid: 0,
                    sn_deleteable: 'F',
                    sn_using: 0,
                    sn_num:'',
                    sn_limit: 1,
                    sn_showmode: 0,
                    sn_logic: 0,
                    sn_caller:'',
                    sn_addurl:'',
                    sn_show:1,
                    sn_standardDesc: '新添加的根节点'
            };
            save[0] = Ext.JSON.encode(o);
            if(save.length > 0 ){
                Ext.Ajax.request({
                    url : basePath + 'ma/addRootSysNavigation.action',
                    params: {
                        save: unescape(save.toString().replace(/\\/g,"%"))
                    },
                    callback : function(options,success,response){
                        var res = new Ext.decode(response.responseText);
                        /*activeTab.setLoading(false);*/
                        if(res.success){
                            me.saveNodes = [];
                        } else if(res.exceptionInfo){
                            showError(res.exceptionInfo);
                        }
                    }
                });
            }
            treegrid.getRootNode().appendChild(o);
            nodes = treegrid.store.tree.root.childNodes;
            
            treegrid.saveNodes = [];
            Ext.each(nodes, function(){
                treegrid.checkChild(this);
            });
            treegrid.saveNode();
        }
    },{
    	margin:'0 0 0 5',
        iconCls: 'tree-nav-delete',
        cls: 'x-btn-gray',
        text: $I18N.common.button.erpDeleteButton,
        handler: function(){
            var treegrid = Ext.getCmp('treegrid');
            var items = treegrid.selModel.selected.items;
            if(items.length > 0){
                if(items[0].isLeaf() == true){
                    if(items[0].data['sn_id'] != null && items[0].data['sn_id'] != ''){
                        if(items[0].data['sn_deleteable'] == true){
                            warnMsg('确定删除节点[' + items[0].data['sn_displayname'] + "]?", function(btn){
                                if(btn == 'yes'){
                                    treegrid.deleteNode(items[0]);
                                } else if(btn == 'no'){
                                    return;
                                } 
                            });
                        } else {
                            showError('该节点不允许删除!');
                        }
                    } else {
                        items[0].remove(true);
                    }
                } else {
                    if(items[0].data['sn_id'] != null || items[0].data['sn_id'] != ''){
                        if(items[0].data['sn_deleteable'] == true){
                            Ext.each(items[0].childNodes, function(){
                                if(this.data['sn_deleteable'] == false){
                                    showError('该节点有不可删除子节点，无法删除该节点!');
                                    return;
                                }
                            });
                            warnMsg('确定删除节点[' + items[0].data['sn_displayname'] + ']及其子节点？', function(btn){
                                if(btn == 'yes'){
                                    treegrid.deleteNode(items[0]);
                                } else if(btn == 'no'){
                                    return;
                                } 
                            });
                        } else {
                            showError('该节点不允许删除!');
                        }
                    } else {
                        items[0].remove(true);
                    }
                }
            } else {
                var record = treegrid.getExpandItem();
                if(record){
                    if(record.childNodes.length == 0){
                        if(record.data['sn_id'] != null && record.data['sn_id'] != ''){
                            if(record.data['sn_deleteable'] == true){
                                warnMsg('确定删除节点[' + record.data['sn_displayname'] + ']？', function(btn){
                                    if(btn == 'yes'){
                                        treegrid.deleteNode(record);
                                    } else if(btn == 'no'){
                                        return;
                                    } 
                                });
                            } else {
                                showError('该节点不允许删除!');
                            }
                        } else {
                            record.remove(true);
                        }
                    }
                }
            }
        }
    },{
    	margin:'0 0 0 5',
        iconCls: 'tree-nav-save',
        cls: 'x-btn-gray',
        text: $I18N.common.button.erpSaveButton,
        handler: function(){
            var treegrid = Ext.getCmp('treegrid'),
            nodes = treegrid.store.tree.root.childNodes;
            treegrid.saveNodes = [];
            Ext.each(nodes, function(){
                treegrid.checkChild(this);
            });
            treegrid.saveNode();
        }
    },'->',{
    	iconCls : 'tree-nav-close',
    	cls:'x-btn-gray',
    	text:$I18N.common.button.erpCloseButton,
    	handler:function(btn){
    		var main = parent.Ext.getCmp("content-panel");
    		main.getActiveTab().close();
    	}
    }]},
    bodyStyle:'background-color:#f1f1f1;',
    initComponent : function(){ 
        var me=this;
        Ext.override(Ext.data.AbstractStore,{
            indexOf: Ext.emptyFn
        });
        me.store=Ext.create('Ext.data.TreeStore', {
            storeId: 'systreestore',
            fields: [{"name":"sn_id","type":"string"},
                     {"name":"sn_displayname","type":"string"},
                     {"name":"sn_parentid","type":"string"},
                     {"name":"sn_url","type":"string"},
                     {"name":"sn_tabtitle","type":"string"},
                     {"name":"sn_detno","type":"number"},
                     {"name":"sn_isleaf","type":"bool"},
                     {"name":"sn_showmode","type":"string"},
                     {"name":"sn_deleteable","type":"bool"},
                     {"name":"sn_using","type":"bool"},
                     {"name":"sn_logic","type":"bool"},
                     {"name":"sn_caller","type":"string"},
                     {"name":"sn_addurl","type":"string"},
                     {"name":"sn_limit","type":"bool"},
                     {"name":"sn_show","type":"bool"},
                     {"name":"sn_standardDesc","type":"string"},
                     {"name":"sn_num","type":"string"}],
            root : {
                text: 'root',
                id: 'root',
                expanded: true
            },
            listeners:{
                     beforeexpand:Ext.bind(me.handleSpExpandClick, me)                  
                 } 
        });
        me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing',{
            clicksToEdit:1
            }
        )];
        
        this.callParent(arguments);
        this.view.onItemClick = function(record, item, index, e) {
        	
            if (e.getTarget(this.expanderSelector, item)) {
                this.toggle(record);
                return false;
            }
            return this.callParent(arguments);
        };
        this.getTreeGridNode({parentId: 0});
        
    },
    listeners: {//滚动条有时候没反应，添加此监听器
        scrollershow: function(scroller) {
            if (scroller && scroller.scrollEl) {
                scroller.clearManagedListeners();  
                scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
            }
        },
        beforeedit:function(editor,e,eOpts ){

    		
}
        
  
    },
    getTreeGridNode: function(param){
        var me = this;
        var activeTab = me.getActiveTab();
        activeTab.setLoading(true);
        Ext.Ajax.request({//拿到tree数据
            url : basePath + 'ma/lazyTree.action',
            params: param,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                activeTab.setLoading(false);
                if(res.tree){
                    var tree = res.tree;
                    Ext.each(tree, function(t){
                        t.sn_id = t.id;
                        t.sn_parentid = t.parentId;
                        t.sn_displayname = t.text;
                        t.sn_isleaf = t.leaf;
                        t.sn_num = t.data.sn_num;
                        t.sn_detno = t.detno;
                        t.sn_tabtitle = t.text;
                        t.sn_url = t.url;
                        t.sn_deleteable = t.deleteable;
                        t.sn_using = t.using;
                        t.sn_limit = t.data.sn_limit;
                        t.sn_showmode = t.showMode;
                        t.sn_logic = t.data.sn_logic;
                        t.sn_caller = t.data.sn_caller;
                        t.sn_addurl=t.data.sn_addurl;
                        t.sn_show=t.data.sn_show,
                        t.sn_standardDesc=t.data.sn_standardDesc;
                        t.data = null;
                    });
                    me.store.setRootNode({
                        text: 'root',
                        id: 'root',
                        expanded: true,
                        children: tree
                    });
                    Ext.each(me.store.tree.root.childNodes, function(){
                        this.dirty = false;
                    });
                  me.setParentNodes(me.store.tree.root, true);
                } else if(res.exceptionInfo){
                    showError(res.exceptionInfo);
                }
            }
        });
    },
    setParentNodes: function(record, isExpand){
        var tree = this, 
            grid = tree.ownerCt.down('grid'), data = new Array(), nodes = record.childNodes;
           
        Ext.each(nodes, function(node){
            data.push({
                sn_id: node.get('sn_id'),
                sn_displayname: node.get('sn_displayname'),
                sn_detno: node.get('sn_detno'),
                sn_addurl:node.get('sn_addurl')
            });
        });
        grid.store.loadData(data);
    },
    checkChild: function(record){
        var me = this;
        if(!record.data['leaf']){
            if(record.childNodes.length > 0){
                if(record.data['sn_id'] == null || record.data['sn_id'] == ''){
                    warnMsg('如果在节点' + record.data['sn_id'] + '下添加子节点，需先保存该节点，是否保存?', function(btn){
                        if(btn == 'yes'){
                            if(items[0].data['sn_displayname'] == null || items[0].data['sn_displayname'] == ''){
                                showError('请先描述该节点');
                                return;
                            } else {
                                me.saveNodes.push(items[0]);
                                me.saveNode();
                            }
                        } else if(btn == 'no'){
                            return;
                        } 
                    });
                }
                Ext.each(record.childNodes, function(){
                    me.checkChild(this);
                });
            }
        } else {
            if(record.dirty){
                if(record.data['sn_id'] == null || record.data['sn_id'] == ''){
                    me.saveNodes.push(record);
                } else {
                    me.updateNodes.push(record);
                }
            }
        }
    },
    saveNode: function(){
        var me = this;
        me.getUpdateNodes();
        var save = new Array();
        var update = new Array();
        var index = 0;
        Ext.each(me.saveNodes, function(){
            if(this.data.sn_displayname != null && this.data.sn_displayname != ''){
                if(this.data.sn_tabtitle == null || this.data.sn_tabtitle == ''){
                    this.data.sn_tabtitle == this.data.sn_displayname;
                }
                if(this.data.sn_deleteable == null || this.data.sn_deleteable == ''){
                    this.data.sn_deleteable == 'T';
                }
                var o = {
                        sn_id: this.data.sn_id,
                        sn_displayname: this.data.sn_displayname,
                        sn_url: this.data.sn_url,
                        sn_detno: this.data.sn_detno,
                        sn_isleaf: this.data.sn_isleaf ? 'T' : 'F',
                        sn_tabtitle: this.data.sn_tabtitle,
                        sn_parentid: this.data.sn_parentid,
                        sn_num: this.data.sn_num,
                        sn_deleteable: this.data.sn_deleteable ? 'T' : 'F',
                        sn_using: this.data.sn_using ? 1 : 0,
                        sn_limit: this.data.sn_limit ? 1 : 0,
                        sn_showmode: this.data.sn_showmode || 0,
                        sn_logic:  this.data.sn_logic ? 1 : 0,
                        sn_caller: this.data.sn_caller,
                        sn_addurl: this.data.sn_addurl,
                        sn_show:this.data.sn_show ? 1 : 0,
                        sn_standardDesc: this.data.sn_standardDesc
                };
                save[index++] = Ext.JSON.encode(o);
            }
        });
        index = 0;
        Ext.each(me.updateNodes, function(){
            if(this.data.sn_displayname != null && this.data.sn_displayname != ''){
                if(this.data.sn_tabtitle == null || this.data.sn_tabtitle == ''){
                    this.data.sn_tabtitle == this.data.sn_displayname;
                }
                if(this.data.sn_deleteable == null || this.data.sn_deleteable == ''){
                    this.data.sn_deleteable == 'T';
                }
                var o = {
                        sn_id: this.data.sn_id,
                        sn_displayname: this.data.sn_displayname,
                        sn_url: this.data.sn_url,
                        sn_detno: this.data.sn_detno,
                        sn_num: this.data.sn_num,
                        sn_isleaf: this.data.sn_isleaf ? 'T' : 'F',
                        sn_tabtitle: this.data.sn_tabtitle,
                        sn_parentid: this.data.sn_parentid,
                        sn_deleteable: this.data.sn_deleteable ? 'T' : 'F',
                        sn_using: this.data.sn_using ? 1 : 0,
                        sn_limit: this.data.sn_limit ? 1 : 0,
                        sn_showmode: this.data.sn_showmode || 0,
                        sn_logic: this.data.sn_logic ? 1 : 0,
                        sn_caller: this.data.sn_caller,
                        sn_addurl: this.data.sn_addurl,
                        sn_show:this.data.sn_show ? 1 : 0,
                        sn_standardDesc: this.data.sn_standardDesc
                };
                update[index++] = Ext.JSON.encode(o);
            }
        });
        var grid = me.ownerCt.down('grid'), other = new Array();
        grid.store.each(function(){
            if(this.dirty)
                other.push(this.data);
        });
        if(save.length > 0 || update.length > 0 || other.length > 0){
            var activeTab = me.getActiveTab();
            activeTab.setLoading(true);
            Ext.Ajax.request({
                url : basePath + 'ma/saveSysNavigation.action',
                params: {
                    save: unescape(save.toString().replace(/\\/g,"%")),
                    update: unescape(update.toString().replace(/\\/g,"%")),
                    other: unescape(Ext.JSON.encode(other).toString().replace(/\\/g,"%"))
                },
                callback : function(options,success,response){
                    var res = new Ext.decode(response.responseText);
                    activeTab.setLoading(false);
                    if(res.success){
                        me.saveNodes = [];
                        me.updateNodes = [];
                        me.getTreeGridNode({parentId: 0});
                    } else if(res.exceptionInfo){
                        showError(res.exceptionInfo);
                    }
                }
            });
        }
    },
    getExpandItem: function(root){
        var me = this;
        if(!root){
            root = this.store.tree.root;
        }
        var node = null;
        if(root.childNodes.length > 0){
            Ext.each(root.childNodes, function(){
                if(this.isExpanded()){
                    node = this;
                    if(this.childNodes.length > 0){
                        var n = me.getExpandItem(this);
                        node = n == null ? node : n;
                    }
                }
            });
        }
        return node;
    },
    deleteNode: function(record){
        var me = this;
        if(record){
            var activeTab = me.getActiveTab();
            activeTab.setLoading(true);
            Ext.Ajax.request({
                url : basePath + 'ma/deleteSysNavigation.action',
                params: {
                    id: Number(record.data['sn_id'])
                },
                callback : function(options,success,response){
                    var res = new Ext.decode(response.responseText);
                    activeTab.setLoading(false);
                    if(res.success){
                        record.remove(true);
                    } else if(res.exceptionInfo){
                        showError(res.exceptionInfo);
                    }
                }
            });
        }
    },
    getUpdateNodes: function(root){
        var me = this;
        if(!root){
            root = this.store.tree.root;
            me.updateNodes = [];
        }
        if(root.childNodes.length > 0){
            Ext.each(root.childNodes, function(){
                if(this.dirty){
                    if(this.data['sn_id'] != null && this.data['sn_id'] != ''){
                        me.updateNodes.push(this);
                    }
                }
                if(this.data['leaf'] == false && this.childNodes.length > 0){
                    me.getUpdateNodes(this);
                }
            });
        } else {
            if(root.dirty){
                if(root.data['sn_id'] != null && root.data['sn_id'] != ''){
                    me.updateNodes.push(root);
                }
            }
        }
    },
    getActiveTab: function(){
        var tab = null;
        if(Ext.getCmp("content-panel")){
            tab = Ext.getCmp("content-panel").getActiveTab();
        }
        if(!tab){
            var win = parent.Ext.ComponentQuery.query('window');
            if(win.length > 0){
                tab = win[win.length-1];
            }
        }
        if(!tab && parent.Ext.getCmp("content-panel"))
            tab = parent.Ext.getCmp("content-panel").getActiveTab();
        if(!tab  && parent.parent.Ext.getCmp("content-panel"))
            tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
        return tab;
    },
    /**
     * treegrid用到了checkcolumn时，由于其store的差异，根据recordIndex不能直接得到record，
     * 采用下面的方法可以在点击checkbox时，得到当前的record，再进而就可以修改checkbox的check属性等...
     */
    getRecordByRecordIndex: function(recordIndex, node){
        var me = this;
        if(!node){
            node = this.store.tree.root;
            me.findIndex = 0;
            me.findRecord = null;
        }
        if(me.findRecord == null){
            if(node.childNodes.length > 0 && node.isExpanded()){
                Ext.each(node.childNodes, function(){
                    if(me.findIndex == recordIndex){
                        me.findRecord = this;
                        me.findIndex++;
                    } else {
                        me.findIndex++;
                        me.getRecordByRecordIndex(recordIndex, this);
                    }
                });
            } else {
                if(me.findIndex == recordIndex){
                    me.findRecord = node;
                }
            }
        }
    },
    checkRecord: function(record, dataIndex, checked){
        var me = this;
        if(record.childNodes.length > 0){
            Ext.each(record.childNodes, function(){
                this.set(dataIndex, checked);
                me.checkRecord(this, dataIndex, checked);
            });
        }
    },
    handleSpExpandClick: function(record) {//自己新加的
        if(record.get('id')!='root'){
         this.fireEvent('spcexpandclick', record);
     }
    }
});