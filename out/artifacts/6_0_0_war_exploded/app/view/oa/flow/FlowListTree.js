
/**
 * ERP项目gridpanel样式5:FlowListTree专用treegrid
 */
Ext.define('erp.view.oa.flow.FlowListTree',{ 
    extend: 'Ext.tree.Panel', 
    alias: 'widget.erpFlowListTree',
    region: 'south',
    layout : 'fit',
    id: 'FlowListTree', 
    useArrows: true,
    rootVisible: false,
    singleExpand: false,
    saveNodes: [],
    updateNodes: [],
    deleteNodes: [],
    title:'流程设置',
    lockable: true,
    cls: 'custom',
    columns : [ {
        "header" : "ID",
        "dbfind" : "",
        "cls" : "x-grid-header-1",
        "summaryType" : "",
        "dataIndex" : "fd_id",
        "align" : "left",
        "xtype" : "treecolumn",
        "readOnly" : false,
        "hidden" : true,
        "text" : "ID"
    },{
        header : "流程名称",
        dbfind : "",
        cls : "x-grid-header-1",
        summaryType : "",
        dataIndex : "fd_shortname",
        align : "left",
        sortable : true,
        xtype : "treecolumn",
        hidden : false,
        width : 250,
        text : "流程名称"
    },{
        "dbfind" : "",
        "cls" : "x-grid-header-1",
        "summaryType" : "",
        "dataIndex" : "fd_parentid",
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
        "dataIndex" : "fd_date",
        "xtype" : "datecolumn",
        "align" : "center",
        "readOnly" : false,
        "hidden" : false,
        "width" : 150.0,
        "text" : "创建日期",
        renderer:function(value){
			return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
		}
    },{
    	"dbfind" : "",
        "cls" : "x-grid-header-1",
        "summaryType" : "",
        "dataIndex" : "fd_man",
        "align" : "left",
        "readOnly" : false,
        "hidden" : false,
        "width" : 80.0,
        "text" : "创建人"
    },{
        "dbfind" : "",
        "cls" : "x-grid-header-1",
        "summaryType" : "",
        "dataIndex" : "fd_remark",
        "align" : "left",
        "readOnly" : false,
        "hidden" : false,
        "flex" : 1,
        "text" : "描述"
    },{
        "dbfind" : "",
        "cls" : "x-grid-header-1",
        "summaryType" : "",
        "dataIndex" : "fd_defaultduty",
        "align" : "left",
        "readOnly" : false,
        "hidden" : false,
        "flex" : 0.3,
        "text" : "流程默认责任人"
    },{
		xtype: 'actioncolumn',
		text: '操作', 
		width : 150,
		"cls" : "x-grid-header-1",
		items: [{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '设置',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('fd_status')=='using'){
              		this.items[0].tooltip = '流程编辑';
					return 'x-button-icon-install';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('fd_status')=='using'){
					var fo_id;
					var params = {};
					params.caller = 'Form';
					params.field = 'fo_id';
					params.condition = 'fo_caller = \''+rec.get('fd_caller')+'\'';
					Ext.Ajax.request({
						async:false,
						url : basePath + '/common/getFieldData.action',
						params: params,
						method : 'post',
						callback : function(options,success,response){
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								showError(localJson.exceptionInfo);return;
							}
							if(localJson.success){
								fo_id = localJson.data;
							}
						}
					});
					openUrl2('jsps/oa/flow/FlowDefine.jsp?flowcaller='+rec.get('fd_caller')+'&formCondition=fo_idIS'+fo_id+'&gridCondition=fd_foidIS'+fo_id,rec.get('fd_shortname'));
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '添加实例',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('fd_isleaf')=='false'){
              		this.items[0].tooltip = '添加实例';
					return 'x-button-icon-add';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('fd_isleaf')=='false'){
					var tree = Ext.getCmp('FlowListTree');
					tree.getFlowDefineWin(rec);
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '删除实例',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('fd_isleaf')=='false'){
              		this.items[0].tooltip = '删除实例';
					return 'x-button-icon-close';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('fd_isleaf')=='false'){
					warnMsg('确定要删除整个流程？', function(btn){
				 	   if(btn == 'yes'){
					 	   	var tree = Ext.getCmp('FlowListTree');
							var name = tree.checkDeleteDefine(rec);
							if(name.length>0){
								showError("还存在子实例未删除，请先删除子实例");
							}else{
								Ext.Ajax.request({
									url : basePath + 'oa/flow/deleteDefineByCondition.action',
									params: {
										shortName : rec.get('fd_shortname'),
										condition : 'Parent',
										caller : rec.get('fd_caller')
									},
									method : 'post',
									callback : function(options,success,response){
										var localJson = new Ext.decode(response.responseText);
										if(localJson.exceptionInfo){
											showError(localJson.exceptionInfo);return;
										}
										if(localJson.success){
											showInformation('删除父实例成功！', function(btn){
												Ext.getCmp('FlowListTree').getTreeGridNode({parentId: 0});
											});
										}
									}
								});
							}
				 	   }
				 	});   
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '修改实例',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('fd_isleaf')=='true'){
              		this.items[0].tooltip = '修改实例';
					return 'x-button-icon-modify';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('fd_isleaf')=='true'){
					var tree = Ext.getCmp('FlowListTree');
					tree.getUpdateWin(rec);
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '启动/关闭实例',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('fd_isleaf')=='true'){
              		this.items[0].tooltip = '启动/关闭实例';
					return 'x-button-icon-submit';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				var status = rec.get('fd_status');
				if(status=='close'||status=='enable'){
					var tree = Ext.getCmp('FlowListTree');
					tree.ChangeStatus(rec);
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '删除实例',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('fd_isleaf')=='true'){
              		this.items[0].tooltip = '删除实例';
					return 'x-button-icon-close';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				if(rec.get('fd_isleaf')=='true'){
					var tree = Ext.getCmp('FlowListTree');
					warnMsg('确定要删除子流程实例？', function(btn){
				 	   if(btn == 'yes'){
					 	   	var tree = Ext.getCmp('FlowListTree');
							var ids = tree.checkDeleteSonDefine(rec);
							if(ids.length>0){
								showError("该子实例已生成审批流程，无法删除！");
							}else{
								Ext.Ajax.request({
									url : basePath + 'oa/flow/deleteDefineByCondition.action',
									params: {
										shortName : rec.get('fd_shortname'),
										condition : '',
										caller : rec.get('fd_caller')
									},
									method : 'post',
									callback : function(options,success,response){
										var localJson = new Ext.decode(response.responseText);
										if(localJson.exceptionInfo){
											showError(localJson.exceptionInfo);return;
										}
										if(localJson.success){
											showInformation('删除子实例成功！', function(btn){
												Ext.getCmp('FlowListTree').getTreeGridNode({parentId: 0});
											});
										}
									}
								});
							}
				 	   }
					});
				}
			}
		},{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '导入',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('fd_isleaf')=='true'&&rec.get('fd_status')=='enable'){
              		this.items[0].tooltip = '导入';
					return 'x-button-icon-excel';
               }
          	},
	        handler: function(view,record,item,index,e,opt){
	        	var flowCaller = view.store.data.items[record].data.fd_caller;
	        	var menu = Ext.create('Ext.menu.Menu', {
	        		width: 80,
	        		margin: '0 , 0, 0, 0',
	        		items: [{
	        			text: '模板下载',
	        			handler: function(){
	        				window.location.href = basePath + 'oa/flow/getExcelTemplate.action?caller=' + flowCaller;
	        			}
	        		},{
	        			text: '导入数据',
	        			xtype: 'form',
	        			iconCls: 'main-msg',
	        			scope: this,
	        			height: 30,
	        			bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
	        			items: [{
	        				xtype: 'filefield',
	        				name: 'file',
	        				buttonText: '导入数据',
		        			buttonOnly: true,
		        			hideLabel: true,
		    		        height: 17,
		    		        buttonConfig: {
		    		        	width: 114
		    		        },
		    		        bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
		    		        listeners: {
		        				change: function(field){
		        					var fileName = field.value;
		        					var type = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length);
		        					if(type != 'xls'){
		        						Ext.MessageBox.alert("消息", "选择的文件格式不正确!");
		        					}else{
		        						var form = field.ownerCt;
			        					form.getForm().submit({
			        						url:basePath + 'oa/flow/saveByExcel.action',
			        			    		waitMsg: "正在上传",
			        			    		method:'POST',
			        			    		params:{
			        			    			caller:flowCaller
			        			    		},
			        			    		success: function(form, action){
			        			    			var data = Ext.decode(action.response.responseText);
		        			    				Ext.MessageBox.alert("消息", data.data);
			        			    		},
			        			    		failure: function(form, action){
			        			    			var data = Ext.decode(action.response.responseText);
		        			    				Ext.MessageBox.alert("消息", data.data);
			        			    		}
			        					});
		        					}
		        				}
		        			}
	        			}]
	        		}]
	        	});
	        	menu.showAt(e.getXY());
	        }
		}]
	}],
    tbar: {id:'flowbutton',items:[
    {
		width:215,
        xtype: 'searchfield',
        cls: 'search-field',
        emptyText:'搜索流程名称',
        onTriggerClick: function(){
        	//过滤树
        	var f = this;
        	var listTree = Ext.getCmp('FlowListTree');
        	if(f.value == '' || f.value == null){
        		listTree.getTreeGridNode({parentId: 0});
        	}else{
        		listTree.getTreeGridNode({parentId: 0,condition:' fd_shortname like \'%'+f.value+'%\' '});
        	}
        }    
    },{xtype:'splitter',width:10},{
        iconCls: 'tree-nav-add',
        cls: 'x-btn-gray',
        text: '新增流程',
        handler: function(btn){
        	openUrl2('jsps/oa/flow/FlowDefine.jsp?_nobutton=false','新增流程');
        }
    },'->']},
    bodyStyle:'background-color:#f1f1f1;',
    initComponent : function(){ 
        var me=this;
        Ext.override(Ext.data.AbstractStore,{
            indexOf: Ext.emptyFn
        });
        me.store=Ext.create('Ext.data.TreeStore', {
            storeId: 'FlowListTree',
            fields: [{"name":"fd_id","type":"string"},
                     {"name":"fd_name","type":"string"},
                     {"name":"fd_parentid","type":"string"},
                     {"name":"fd_status","type":"string"},
                     {"name":"fd_remark","type":"string"},
                     {"name":"fd_date"},
                     {"name":"fd_url","type":"string"},
                     {"name":"fd_caller","type":"string"},
                     {"name":"fd_shortname","type":"string"},
                     {"name":"fd_man","type":"string"},
                     {"name":"fd_fcid","type":"string"},
                     {"name":"fd_isleaf","type":"string"},
                     {"name":"fd_defaultduty","type":"string"}],
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
        },
        itemdblclick : function(view, record){ 
        	if(record.get('fd_fcid')){
        		openUrl2('workfloweditor2/workfloweditor2.jsp?fd_fcid='+record.get('fd_fcid')+'&fd_id='+record.get('fd_id')+'&fd_name='+record.get('fd_name')+'&caller='+record.get('fd_caller')+'&shortName='+record.get('fd_shortname'),record.get('fd_shortname'));
        	}else{
				openUrl2('workfloweditor2/workfloweditor.jsp?fd_id='+record.get('fd_id')+'&fd_name='+record.get('fd_name')+'&caller='+record.get('fd_caller')+'&shortName='+record.get('fd_shortname'),record.get('fd_shortname'));
        	}
        	var listTree = Ext.getCmp('FlowListTree');
    		listTree.getTreeGridNode({parentId: 0});
			var panel = parent.Ext.getCmp('tree-tab');
			if(panel && !panel.collapsed) {
				panel.toggleCollapse();
			}
		}
    },
    getTreeGridNode: function(param){
        var me = this;
        var activeTab = me.getActiveTab();
        activeTab.setLoading(true);
        Ext.Ajax.request({//拿到tree数据
            url : basePath + 'oa/flow/getAllFlowTree.action',
            params: param,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                activeTab.setLoading(false);
                if(res.tree){
                    var tree = res.tree;
                    Ext.each(tree, function(t){
                        t.fd_id = t.id;
                        t.fd_parentid = t.parentId;
                        t.fd_isleaf = t.leaf;
                        t.fd_detno = t.data.fd_detno;
                        t.fd_url = t.data.fd_url;
                        t.fd_caller = t.data.fd_caller;
                        t.fd_status = t.data.fd_status,
                        t.fd_remark = t.data.fd_remark,
                        t.fd_date = t.data.fd_date,
                        t.fd_shortname = t.data.fd_shortname,
                        t.fd_man = t.data.fd_man,
                        t.fd_fcid = t.data.fd_fcid,
                        t.fd_defaultduty = t.data.fd_defaultduty,
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
										Ext.getCmp('FlowListTree').getTreeGridNode({parentId: 0});
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
	getUpdateWin:function(rec){
		var rec = rec;
		var win =new Ext.window.Window({
			title: '<span style="color:#115fd8;">修改流程实例</span>',
			draggable:true,
			height: '25%',
			width: '50%',
			resizable:false,
			id:'UpdateFlowWin',
			cls:'UpdateFlowWin',
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
				    warnMsg('确定修改实例吗？', function(btn){
				 	   if(btn == 'yes'){
						Ext.Ajax.request({
							url : basePath + 'oa/flow/updateDefineInstance.action',
							params: {
								remark : fd_remark,
								caller : rec.get('fd_caller'),
								id : rec.get('fd_id')
							},
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.exceptionInfo){
									showError(localJson.exceptionInfo);return;
								}
								if(localJson.success){
									showInformation('流程修改成功！', function(btn){
										Ext.getCmp('FlowListTree').getTreeGridNode({parentId: 0});
										var win = Ext.getCmp('UpdateFlowWin');
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
	   				var win = Ext.getCmp('UpdateFlowWin');
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
		      "fieldStyle": "background:#fff;color:#515151;",
		      "labelAlign": "left",
		      "allowDecimals": true,
		      value:rec.get('fd_remark')
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
							Ext.getCmp('FlowListTree').getTreeGridNode({parentId: 0});
						});
					}
				}
			});
		   }
	    });
	}
});