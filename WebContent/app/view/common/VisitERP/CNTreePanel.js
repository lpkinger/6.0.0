
Ext.define('erp.view.common.VisitERP.CNTreePanel',{ 
    extend: 'Ext.tree.Panel', 
    alias: 'widget.CNTreePanel',
    region: 'south',
    layout : 'fit',
    id: 'CNTreePanel', 
    useArrows: true,
    rootVisible: false,
    singleExpand: false,
    saveNodes: [],
    updateNodes: [],
    deleteNodes: [],
    title:'客户访问导航设置',
    lockable: true,
    cls: 'custom',
    columns : [ {
        "header" : "ID",
        "dbfind" : "",
        "cls" : "x-grid-header-1",
        "summaryType" : "",
        "dataIndex" : "cn_id",
        "align" : "left",
        "xtype" : "treecolumn",
        "readOnly" : false,
        "hidden" : true,
        "text" : "ID"
    },{
        header : "导航名称",
        dbfind : "",
        cls : "x-grid-header-1",
        summaryType : "",
        dataIndex : "cn_title",
        align : "left",
        sortable : true,
        xtype : "treecolumn",
        hidden : false,
        width : 250,
        text : "导航名称"
    },{
        "dbfind" : "",
        "cls" : "x-grid-header-1",
        "summaryType" : "",
        "dataIndex" : "cn_subof",
        "align" : "left",
        "xtype" : "treecolumn",
        "readOnly" : false,
        "hidden" : true,
        "width" : 0.0,
        "text" : "父节点ID"
    },{
        "dbfind" : "",
        "cls" : "x-grid-header-1",
        "summaryType" : "",
        "dataIndex" : "cn_url",
        "align" : "left",
        "readOnly" : false,
        "hidden" : false,
        "flex" : 1,
        "text" : "导航路径"
    },{
		xtype: 'actioncolumn',
		text: '操作', 
		width : 150,
		"cls" : "x-grid-header-1",
		items: [{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '添加导航',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('cn_isleaf')=='false'){
              		this.items[0].tooltip = '添加导航';
					return 'x-button-icon-add';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('cn_isleaf')=='false'){
					var tree = Ext.getCmp('CNTreePanel');
					tree.getTypeWin(rec,'add');
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '修改模块',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('cn_isleaf')=='false'){
              		this.items[0].tooltip = '修改导航';
					return 'x-button-icon-install';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('cn_isleaf')=='false'){
					var tree = Ext.getCmp('CNTreePanel');
					tree.getTypeWin(rec,'Root');
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '删除模块',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('cn_isleaf')=='false'){
              		this.items[0].tooltip = '删除模块';
					return 'x-button-icon-close';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('cn_isleaf')=='false'){
					warnMsg('确定要删除整个模块？', function(btn){
				 	   if(btn == 'yes'){
							Ext.Ajax.request({
								url : basePath + 'common/VisitERP/deleteCurnavigation.action',
								params: {
									cn_id : rec.get('cn_id'),
									type : 'model'
								},
								method : 'post',
								callback : function(options,success,response){
									var localJson = new Ext.decode(response.responseText);
									if(localJson.exceptionInfo){
										showError(localJson.exceptionInfo);return;
									}
									if(localJson.success){
										showInformation('删除成功！', function(btn){
											Ext.getCmp('CNTreePanel').getTreeGridNode({parentId: 0});
										});
									}
								}
							});
				 	   }
				 	});   
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '修改导航',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('cn_isleaf')=='true'){
              		this.items[0].tooltip = '修改导航';
					return 'x-button-icon-modify';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('cn_isleaf')=='true'){
					var tree = Ext.getCmp('CNTreePanel');
					tree.getTypeWin(rec,'update');
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '删除导航',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('cn_isleaf')=='true'){
              		this.items[0].tooltip = '删除导航';
					return 'x-button-icon-close';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('cn_isleaf')=='true'){
					var tree = Ext.getCmp('CNTreePanel');
					Ext.Ajax.request({
						url : basePath + 'common/VisitERP/deleteCurnavigation.action',
						params: {
							cn_id : rec.get('cn_id'),
							type : 'leaf'
						},
						method : 'post',
						callback : function(options,success,response){
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								showError(localJson.exceptionInfo);return;
							}
							if(localJson.success){
								showInformation('删除成功！', function(btn){
									Ext.getCmp('CNTreePanel').getTreeGridNode({parentId: 0});
								});
							}
						}
					});
				}
			}
		}]
	}],
    tbar: {id:'flowbutton',items:[
    {
		width:215,
        xtype: 'searchfield',
        cls: 'search-field',
        emptyText:'搜索导航名称',
        onTriggerClick: function(){
        	//过滤树
        	var f = this;
        	var listTree = Ext.getCmp('CNTreePanel');
        	if(f.value == '' || f.value == null){
        		listTree.getTreeGridNode({parentId: 0});
        	}else{
        		listTree.getTreeGridNode({parentId: 0,condition:' cn_title like \'%'+f.value+'%\' '});
        	}
        }    
    },{xtype:'splitter',width:10},{
        iconCls: 'tree-nav-add',
        cls: 'x-btn-gray',
        text: '新增导航模块',
        handler: function(btn){
        	var tree = Ext.getCmp('CNTreePanel');
			tree.getNewRootWin();
        }
    },'->']},
    bodyStyle:'background-color:#f1f1f1;',
    initComponent : function(){ 
        var me=this;
        Ext.override(Ext.data.AbstractStore,{
            indexOf: Ext.emptyFn
        });
        me.store=Ext.create('Ext.data.TreeStore', {
            storeId: 'CNTree',
            fields: [{"name":"cn_id","type":"string"},
                     {"name":"cn_title","type":"string"},
                     {"name":"cn_url","type":"string"},
                     {"name":"cn_icon","type":"string"},
                     {"name":"cn_subof","type":"string"},
                     {"name":"cn_isleaf","type":"string"},
                     {"name":"cn_detno","type":"string"}],
            root : {
                text: 'root',
                id: 'root',
                expanded: true
            },
            listeners:{
                beforeexpand:Ext.bind(me.handleSpExpandClick, me)                  
            } 
        });
        this.callParent(arguments);
        this.getTreeGridNode({parentId: 0});
        
    },
    listeners: {//滚动条有时候没反应，添加此监听器
        scrollershow: function(scroller) {
            if (scroller && scroller.scrollEl) {
                scroller.clearManagedListeners();  
                scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
            }
        }
    },
    getTreeGridNode: function(param){
        var me = this;
        var activeTab = me.getActiveTab();
        activeTab.setLoading(true);
        Ext.Ajax.request({//拿到tree数据
            url : basePath + 'common/VisitERP/getCNTree.action',
            params: param,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                activeTab.setLoading(false);
                if(res.tree){
                    var tree = res.tree;
                    Ext.each(tree, function(t){
                        t.cn_id = t.id;
                        t.cn_subof = t.parentId;
                        t.cn_isleaf = t.leaf;
                        t.cn_detno = t.detno;
                        t.cn_url = t.data.cn_url;
                        t.cn_title = t.data.cn_title,
                        t.cn_icon = t.data.cn_icon,
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
                } else if(res.exceptionInfo){
                    showError(res.exceptionInfo);
                }
            }
        });
    },
    handleSpExpandClick: function(record) {//自己新加的
        if(record.get('id')!='root'){
         	this.fireEvent('spcexpandclick', record);
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
	getFlowDefineWin:function(rec){
		var rec = rec;
		var win =new Ext.window.Window({
			title: '<span style="color:#115fd8;">新增流程实例</span>',
			draggable:true,
			height: '25%',
			width: '50%',
			resizable:false,
			id:'AddFlowWin',
			cls:'AddFlowWin',
			iconCls:'x-button-icon-set',
	   		modal: true,
	   		layout:'column',
	   		bbar:['->',{
	   			cls:'x-btn-gray',
	   			xtype:'button',
	   			text:'保存',
	   			handler:function(btn){
	   				var fd_remark = Ext.getCmp('fd_remark').value;
	   				if(!fd_remark){
	   					showError('请填写所有必填项');
	   					return;
	   				}
				    warnMsg('确定新建该流程的新实例吗？', function(btn){
				 	   if(btn == 'yes'){
						Ext.Ajax.request({
							url : basePath + 'oa/flow/saveDefineInstance.action',
							params: {
								remark : fd_remark,
								caller : rec.get('fd_caller')
							},
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.exceptionInfo){
									showError(localJson.exceptionInfo);return;
								}
								if(localJson.success){
									showInformation('新建流程实例成功！', function(btn){
										Ext.getCmp('CNTreePanel').getTreeGridNode({parentId: 0});
										var win = Ext.getCmp('AddFlowWin');
	   									win.close();
									});
								}
							}
						});
					   }
				    });
	   			}
	   		},{xtype:'splitter',width:10},{
	   			cls:'x-btn-gray',
	   			xtype:'button',
	   			text:'取消',
	   			handler:function(btn){
	   				var win = Ext.getCmp('AddFlowWin');
	   				win.close();
	   			}
	   		},'->'],
		   	items:[{
			  "fieldLabel": "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>描述",
		      "name": "fd_remark",
		      "id": "fd_remark",
		      "xtype": "textareatrigger",
		      "maxLength": 300,
		      "maxLengthText": "字段长度不能超过300字符!",
		      "hideTrigger": false,
		      "editable": true,
		      "columnWidth": 1,
		      "allowBlank": true,
		      "cls": "form-field-allowBlank",
		      "fieldStyle": "background:#FFFAFA;color:#515151;",
		      "labelAlign": "left",
		      "allowDecimals": true
			}]
	    });
        win.show();
	},
	checkDeleteSonDefine:function(rec){
		var rec = rec;
		var ids = new Array();
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fi_id',
				caller : 'flow_instance',
				condition : 'FI_FDSHORTNAME = \''+ rec.get('fd_shortname') +'\''
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				var rs = Ext.decode(rs.data);
				if(rs.length>0){
					Ext.Array.each(rs, function(item){
						ids.push({
							id : item.FI_ID
						});
					});
				}
			}
		});
		return ids;
	},
	checkDeleteDefine:function(rec){
		var rec = rec;
		var SonName = new Array();
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fd_shortname',
				caller : 'flow_define',
				condition : 'fd_parentid = \''+ rec.get('fd_id') +'\''
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				var rs = Ext.decode(rs.data);
				if(rs.length>0){
					Ext.Array.each(rs, function(item){
						SonName.push({
							name : item.FD_SHORTNAME
						});
					});
				}
			}
		});
		return SonName;
	},
	getTypeWin:function(rec,type){
		var rec = rec;
		var win =new Ext.window.Window({
			title: '<span style="color:#115fd8;">修改导航</span>',
			draggable:true,
			height: '25%',
			width: '50%',
			resizable:false,
			id:'UpdateWin',
			cls:'UpdateWin',
			iconCls:'x-button-icon-set',
	   		modal: true,
	   		layout:'column',
	   		bbar:['->',{
	   			cls:'x-btn-gray',
	   			xtype:'button',
	   			text:'保存',
	   			handler:function(btn){
	   				var cn_title = Ext.getCmp('cn_title').value;
	   				var cn_url = Ext.getCmp('cn_url').value;
	   				if(type=='Root'){
	   					cn_url = ' ';
	   				}
	   				if(!cn_title||!cn_url){
	   					showError('请填写所有必填项');
	   					return;
	   				}
	   				if(cn_url!=' '&&cn_url.indexOf('jsps/opensys/')==0){
	   					Ext.Ajax.request({
							url : basePath + 'common/VisitERP/updateCurnavigation.action',
							params: {
								cn_title : cn_title,
								cn_url : cn_url,
								cn_id : rec.get('cn_id'),
								type: type
							},
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.exceptionInfo){
									showError(localJson.exceptionInfo);return;
								}
								if(localJson.success){
									showInformation('导航修改成功！', function(btn){
										Ext.getCmp('CNTreePanel').getTreeGridNode({parentId: 0});
										var win = Ext.getCmp('UpdateWin');
	   									win.close();
									});
								}
							}
						});
	   				}else{
	   					showInformation('URL配置有误，请确认开头是：jsps/opensys/', function(btn){});
	   				}
	   			}
	   		},{xtype:'splitter',width:10},{
	   			cls:'x-btn-gray',
	   			xtype:'button',
	   			text:'取消',
	   			handler:function(btn){
	   				var win = Ext.getCmp('UpdateWin');
	   				win.close();
	   			}
	   		},'->'],
		   	items:[{
			  "fieldLabel": "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>名称",
		      "name": "cn_title",
		      "id": "cn_title",
		      "xtype": "textfield",
		      "maxLength": 300,
		      "maxLengthText": "字段长度不能超过300字符!",
		      "hideTrigger": false,
		      "editable": true,
		      "columnWidth": 1,
		      "allowBlank": true,
		      "cls": "form-field-allowBlank",
		      "fieldStyle": "background:#FFFAFA;color:#515151;",
		      "labelAlign": "left",
		      "allowDecimals": true,
		      value: rec.get('cn_title')
			},{
			  hidden:type=='Root'?true:false,
			  "fieldLabel": "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>URL",
		      "name": "cn_url",
		      "id": "cn_url",
		      "xtype": "textfield",
		      "maxLength": 300,
		      "maxLengthText": "字段长度不能超过300字符!",
		      "hideTrigger": false,
		      "editable": true,
		      "columnWidth": 1,
		      "allowBlank": true,
		      "cls": "form-field-allowBlank",
		      "fieldStyle": "background:#FFFAFA;color:#515151;",
		      "labelAlign": "left",
		      "allowDecimals": true,
		      value: rec.get('cn_url')
			}]
	    });
        win.show();
	},
	ChangeStatus : function(rec){
		var rec = rec;
		warnMsg('修改后使用中的流程会更改，确定修改该实例状态吗？', function(btn){
	 	   if(btn == 'yes'){
			Ext.Ajax.request({
				url : basePath + 'oa/flow/updateInstanceStatus.action',
				params: {
					status : rec.get('fd_status'),
					id : rec.get('fd_id'),
					caller : rec.get('fd_caller')
				},
				method : 'post',
				callback : function(options,success,response){
					var localJson = new Ext.decode(response.responseText);
					if(localJson.exceptionInfo){
						showError(localJson.exceptionInfo);return;
					}
					if(localJson.success){
						showInformation('实例状态修改成功！', function(btn){
							Ext.getCmp('CNTreePanel').getTreeGridNode({parentId: 0});
						});
					}
				}
			});
		   }
	    });
	},
	getNewRootWin : function(){
		var win =new Ext.window.Window({
			title: '<span style="color:#115fd8;">新增导航模块</span>',
			draggable:true,
			height: '20%',
			width: '70%',
			resizable:false,
			id:'AddNewRootWin',
			cls:'AddNewRootWin',
			iconCls:'x-button-icon-set',
	   		modal: true,
	   		layout:'column',
	   		bbar:['->',{
	   			cls:'x-btn-gray',
	   			xtype:'button',
	   			text:'保存',
	   			handler:function(btn){
	   				var cn_title = Ext.getCmp('cn_title').value;
	   				if(!cn_title){
	   					showError('请填写所有必填项');
	   					return;
	   				}
					Ext.Ajax.request({
						url : basePath + 'common/VisitERP/saveCurnavigation.action',
						params: {
							cn_title : cn_title,
							type:'Root'
						},
						method : 'post',
						callback : function(options,success,response){
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								showError(localJson.exceptionInfo);return;
							}
							if(localJson.success){
								showInformation('保存成功！', function(btn){
									Ext.getCmp('CNTreePanel').getTreeGridNode({parentId: 0});
									var win = Ext.getCmp('AddNewRootWin');
   									win.close();
								});
							}
						}
					});
	   			}
	   		},{xtype:'splitter',width:10},{
	   			cls:'x-btn-gray',
	   			xtype:'button',
	   			text:'取消',
	   			handler:function(btn){
	   				var win = Ext.getCmp('AddNewRootWin');
	   				win.close();
	   			}
	   		},'->'],
		   	items:[{
			  "fieldLabel": "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>名称",
		      "name": "cn_title",
		      "id": "cn_title",
		      "xtype": "textfield",
		      "maxLength": 300,
		      "maxLengthText": "字段长度不能超过300字符!",
		      "hideTrigger": false,
		      "editable": true,
		      "columnWidth": 1,
		      "allowBlank": true,
		      "cls": "form-field-allowBlank",
		      "fieldStyle": "background:#FFFAFA;color:#515151;",
		      "labelAlign": "left",
		      "allowDecimals": true
			}]
	    });
        win.show();
	}
});