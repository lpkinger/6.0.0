Ext.define("erp.controller.excel.Excel",{
	extend:'Ext.app.Controller',
	FormUtil:Ext.create('erp.util.FormUtil'),
	SHEET_API:Ext.create('EnterpriseSheet.api.SheetAPI'),
	views:[
		'erp.view.excel.Excel',
		'erp.view.excel.CenterPanel',
		'erp.view.excel.WestPanel',
		'erp.view.excel.ExcelTree',
		'erp.view.core.grid.Panel2',
		'erp.view.excel.GridPanel'
	],
	init: function(){
		var me = this;
		this.flag = true; // 防止双击时tree节点重复加载
		this.getSHEET_API_HD=function(){
			return 	SHEET_API_HD;	
		};
		this.control({
			'excelCenterPanel':{
				afterrender:function(){
					// 处理url
					me.dealUrl();					
				}
			},
			'excelWestPanel button[name=addTpl]':{
				click:function(btn){
					// 只允许在父节点为类别的条件下，新增模板
					var tree= Ext.getCmp('excelTree');
					if (tree.getSelectionModel().getSelection().length!=0 && tree.getSelectionModel().getSelection()[0].data.leaf) {
						Ext.MessageBox.alert("提示","模板下不允许新增！");
						return;
					}
					me.showAddWin(btn);
				}
			},
			'excelWestPanel button[name=editTpl]':{
				click:function(btn){
					var tree= Ext.getCmp('excelTree');
					var selection = tree.getSelectionModel().getSelection();
					// 判断是否选择树
					if (selection.length!=0) {
						// 判断是否为本人所创建
						if (selection[0].raw.creator!=em_name) {
							Ext.MessageBox.alert("提示","您不是该类别/模板的创建者");
							return;
						}
						// 编辑目录/模板
						me.showUpdateWin();
					}
				}
			},
			'excelWestPanel button[name=readOnlyTpl]':{
				click:function(btn){
					var tree= Ext.getCmp('excelTree');
					var selection = tree.getSelectionModel().getSelection();
					// 判断是否选择树
					if (selection.length!=0) {
						// 判断是否为本人所创建
						if (selection[0].raw.creator!=em_name) {
							Ext.MessageBox.alert("提示","您不是该模板的创建者");
							return;
						}
						if (selection[0].data.leaf) {
							// 编辑模板
							var hd = me.getSHEET_API_HD();
							me.SHEET_API.setReadOnly(hd, false);
							me.showEditButton();
						}						
					}
				}
			},
			'excelWestPanel button[name=deleTpl]':{
				click:function(btn){
					me.deleteTemplate(btn);
				}
			},
			'excelWestPanel button[name=test]':{
				click:function(btn){
// var listVariables = me.SHEET_API.getCellVariables(SHEET_API_HD);
				    SHEET_API.setValueToVariable(SHEET_API_HD, {
				        "aa":"zdw" 
				    });
//					SHEET_API.clearAllVariables(SHEET_API_HD);    
				}
			},
			// 实例文件grid
			'excelGridPanel':{
				itemdblclick:function(t,record){
					Ext.getCmp('grid_win').close();
					var hd = me.getSHEET_API_HD();
					me.loadExcel(record.data.FILEID,false);
					// 判断是否为本人所创建的文件
					var grid = t;
					var selection = grid.getSelectionModel().getSelection();
					// 判断是否为本人所创建
					if (selection[0].data.FILEMAN!=em_name) {
						Ext.MessageBox.alert("提示","您不是该文件的创建者,只能浏览文件");
					}else{
						me.SHEET_API.setReadOnly(hd, false);
					}
					me.hideEditButton();
				}
			},
			'excelGridPanel button[id=delete]':{
				click:function(t,record){
					me.deleteFile();
				}
			},
			'excelGridPanel textfield':{
            	specialkey: function(field, e){
                    if (e.getKey() == e.ENTER) {
                    	me.doSearch();
                    }
                }
			},
			'excelTree':{
			    itemclick: function(selModel, record,h,index,e,eO){
			    	if (e.xy[0]>=280) {
			    		return;
			    	}
                    if (!this.flag) {
                        return;
                    }
                    this.flag = false;
                    setTimeout(function() {
                        me.flag = true;
		    			if(record.data.leaf){
							me.loadExcel(record.data.id,true);
		    				var hd = me.getSHEET_API_HD();
							me.SHEET_API.setReadOnly(hd, true);
						}else{
							var tree = selModel.ownerCt.ownerCt;
							me.getExcelTreeBySubof(tree, record);
						}
                    }, 20);
			    },
                itemmouseenter: me.showActions,
                itemmouseleave: me.hideActions,
                addclick: me.handleAddClick
			}
		})
	},
	hideEditButton:function(){
		var me = this;
		var hd = me.getSHEET_API_HD();
		hd.toolbar.editableBtn.disable();
		hd.toolbar.borderBtn.disable();
		hd.toolbar.combineBtn.disable();
		hd.contextMenu.deleteItem.disable();
		hd.contextMenu.insertItem.disable();
		hd.contextMenu.columnWidthItem.disable();
		hd.contextMenu.rowHeightItem.disable();
		hd.arrow.disable();
		hd.sheetbar.addSheetBtn.disable();
		hd.sheetbar.sheetMenu.disable();
	},
	showEditButton:function(){
		var me = this;
		var hd = me.getSHEET_API_HD();
		hd.toolbar.editableBtn.enable();
		hd.toolbar.borderBtn.enable();
		hd.toolbar.combineBtn.enable();
		hd.contextMenu.deleteItem.enable();
		hd.contextMenu.insertItem.enable();
		hd.contextMenu.columnWidthItem.enable();
		hd.contextMenu.rowHeightItem.enable();
		hd.arrow.enable();
		hd.sheetbar.addSheetBtn.enable();
		hd.sheetbar.sheetMenu.enable();
	},
    showActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        if (record.get('leaf')) {
            Ext.each(icons, function(icon) {
                Ext.get(icon).removeCls('x-hidden');
            });
        }
    },
    hideActions: function(view, list, node, rowIndex, e) {
        var icons = Ext.DomQuery.select('.x-action-col-icon', node),
            record = view.getRecord(node);
        Ext.each(icons, function(icon) {
            Ext.get(icon).addCls('x-hidden');
        });
    },
    handleAddClick: function(view, rowIndex, colIndex, column, e) {
    	var me=this,
    	fileid_tpl = view.getStore().getAt(rowIndex).data.id,
    	leaf = view.getStore().getAt(rowIndex).data.leaf,
    	name = view.getStore().getAt(rowIndex).data.text;
    	filecaller = view.getStore().getAt(rowIndex).data.caller;
		if (leaf) {
			me.showGridWin(fileid_tpl,filecaller,name);
		}
    },
    deleteFile:function(){
    	var me = this;
		var grid = Ext.getCmp('excelGrid'),
		store = grid.getStore(),
		selection = grid.getSelectionModel().getSelection();
		if (selection.length==0) {
			Ext.MessageBox.show({
			 	   title:'提示',
		           msg: '请选择要删除的文件',
		           width:300,
		           buttons: Ext.Msg.OK
		    });
			return;
		}
		// 判断是否为本人所创建
		if (selection[0].data.FILEMAN!=em_name) {
			Ext.MessageBox.alert("提示","您不是该文件的创建者");
			return;
		}
		var id = selection[0].data.FILEID    
		Ext.MessageBox.confirm("提示", "是否要删除该文件？", function (btnId) {  
		    if (btnId == "yes") {  
				Ext.Ajax.request({
		        	url : basePath + 'Excel/file/delete.action',
		        	params: {
		        		id:id
		        	},
		        	callback : function(options,success,response){
		        		var res = new Ext.decode(response.responseText);        	
		        		if(res.success){
		        			store.reload();
		        		} else if(res.exceptionInfo){
		        			Ext.MessageBox.alert("提示",res.exceptionInfo);
		        		}
		        	}
		        });		          
		    }  
		});     
    },
	deleteTemplate:function(button){
		var me = this;
		var tree= Ext.getCmp('excelTree'),
		record=tree.getSelectionModel().lastFocused,
		selection = tree.getSelectionModel().getSelection();
		if (selection.length==0) {
			Ext.MessageBox.show({
			 	   title:'提示',
		           msg: '请选择要删除的模板或类别',
		           width:300,
		           buttons: Ext.Msg.OK
		    });
			return;
		}
		// 判断是否为本人所创建
		if (selection[0].raw.creator!=em_name) {
			Ext.MessageBox.alert("提示","您不是该类别/模板的创建者");
			return;
		}
		var isCategory = !tree.getSelectionModel().getSelection()[0].data.leaf;
		var id =tree.getSelectionModel().getSelection()[0].data.id;
		Ext.MessageBox.confirm("提示", "是否要删除该模板/类别？", function (btnId) {  
		    if (btnId == "yes") {  
				Ext.Ajax.request({
		        	url : basePath + 'Excel/template/delete.action',
		        	params: {
		        		id:id,
		        		isCategory:isCategory
		        	},
		        	callback : function(options,success,response){
		        		tree.setLoading(false);
		        		var res = new Ext.decode(response.responseText); 
		        		if(res.success){
		        			record.remove();		
		        		} else if(res.exceptionInfo){
		        			Ext.MessageBox.alert("提示",res.exceptionInfo);
		        		}
		        	}
		        });		          
		    }  
		}); 
	},
	showAddWin:function(button){
	   var me = this;	
	   var win = Ext.getCmp('add_win');
	   if(!win){
		   win=Ext.create('Ext.window.Window',{
			   width: 550,
			   height:300,
// closeAction: 'hide',
			   id:'add_win',
			   layout:'fit',
			   modal:true,
			   title:'<div align="center" class="WindowTitle">创建模板/类别</div>', 
			   items:[{
			     xtype: 'tabpanel',
			     items:[{
			     title:'创建',
			     xtype:'form',
			     items: [{
		            xtype:'fieldset',
		            height:200,
		            title: '模板/类别信息',
                    defaults:{
                      labelWidth: 40,
                      allowBlank:false,               // 不允许为空
                      blankText:'该项不能为空!'  // 显示为空的错误信息
                 	},
		            items :[{
			                fieldLabel: '名称',
			                xtype:'textfield',
			                name: 'filename'
			            },{
			                fieldLabel: '描述',
			                xtype:'textarea',
			                width:500,
			                height:100,
			                name: 'desc'
			            },{
			            	fieldLabel: '类型',
    		                xtype:'combobox',
			                store:Ext.create('Ext.data.Store',{
			                	fields:['name','value'],
			                	data:[{
			                		"name":'类别',"value":true
			                	},{
			                		"name":'模板',"value":false
			                	}]
			                	
			                }),
			                displayField: 'name',
			                valueField: 'value',
			                name: 'isCategory'
			            }]
		        	}],
		         buttonAlign:'center',
				 buttons: [{
					    text: '保存',
					    handler:function(btn){
					    	me.createTemplate(btn);
					    }
					},{
						text: '关闭',
					    handler:function(btn){
					    	Ext.getCmp('add_win').close();
					    }
					}]
				     
				 },{
			     title:'导入',
			     xtype:'form',
			     items: [{
		            xtype:'fieldset',
		            height:200,
                    defaults:{
                      allowBlank:false,               // 不允许为空
                      blankText:'该项不能为空!'  // 显示为空的错误信息
                 	},
		            items :[{
			            xtype: 'filefield',
			            id: 'ExcelFile-tpl',
			            labelWidth: 40,
			            width:500,
			            margin:'10 0 0 0',
			            emptyText: '请导入xls,xlsx格式的文件',
			            fieldLabel: '文件',
			            name: 'file',
			            buttonText: '浏览'
			        }]
		         }],
		         buttonAlign:'center',
				 buttons: [{
					    text: '上传',
					    handler:function(btn){
					    	me.importExcelTemplate(btn);
					    }
					},{
						text: '关闭',
					    handler:function(btn){
					    	Ext.getCmp('add_win').close();
					    }
					}]
				     
				 }]
			   }]
		   });
	   };
	   win.show();
	},
	createTemplate:function(btn){
		var me = this,
		form = btn.ownerCt.ownerCt.getForm() 
		if(!me.checkForm(form)){
			return;
		}
		if (!form.isValid()) {
			return;
		}
		var params = form.getValues();
		params.subof=0;
		var tree= Ext.getCmp('excelTree');
		var selection = tree.getSelectionModel().getSelection();
		if (selection.length>0) {
			params.subof=tree.getSelectionModel().getSelection()[0].data.id
		}
		Ext.getCmp('add_win').close();
		Ext.Ajax.request({
        	url : basePath + 'Excel/template/create.action',
        	params: params,
        	callback : function(options,success,response){
        		tree.setLoading(false);
        		var res = new Ext.decode(response.responseText);        	
        		if(res.success){
        			if (!params.isCategory) {
        				me.loadExcel(res.id,true);
        			}
					tree.refreshNodeByParentId(params.subof,tree);        			
        		} else if(res.exceptionInfo){
        			Ext.MessageBox.alert("提示",res.exceptionInfo);
        		}
        	}
        });
	},
	showUpdateWin:function(button){
	   var me = this,	
	   win = Ext.getCmp('update_win');
	   if(!win){
		   win=Ext.create('Ext.window.Window',{
			   width: 550,
			   height:300,
// closeAction: 'hide',
			   id:'update_win',
			   layout:'fit',
			   modal:true,
			   title:'<div align="center" class="WindowTitle">更新类别/模板</div>', 
			   items:[{
			     xtype:'form',
			     items: [{
		            xtype:'fieldset',
		            title: '类别信息',
                    defaults:{
                      allowBlank:false,               // 不允许为空
                      blankText:'该项不能为空!'  // 显示为空的错误信息
                 	},
		            items :[{
			                fieldLabel: '名称',
			                xtype:'textfield',
			                name: 'filename'
			            },
						{
			                fieldLabel: 'caller',
			                xtype:'textfield',
			                name: 'caller'
			            },			            		
			            {
			                fieldLabel: '描述',
			                xtype:'textarea',
			                width:500,
			                height:100,
			                name: 'desc'
			            }
/*
 * , { fieldLabel: '类型', xtype:'combobox', store:Ext.create('Ext.data.Store',{
 * fields:['name','value'], data:[{ "name":'类别',"value":true },{
 * "name":'模板',"value":false }]
 * 
 * }), displayField: 'name', valueField: 'value', name: 'isCategory' }
 */
			            ]
		        	}],
		         buttonAlign:'center',
				 buttons: [{
					    text: '保存',
					    margins:'0 0 50 0',
					    handler:function(btn){
					    	me.updateTemplate(btn);
					    }
					},{
						text: '关闭',
						margins:'0 0 50 0',
					    handler:function(btn){
					    	Ext.getCmp('update_win').close();
					    }
					}]
				     
				 }]
		   });
		   // 添加信息到更新form中
		   var form = win.items.items[0],
		   filename = form.getForm().findField('filename'),
		   desc = form.getForm().findField('desc'),
		   caller = form.getForm().findField('caller'),
		   tree= Ext.getCmp('excelTree'),
		   data = tree.getSelectionModel().getSelection()[0].data;
		   // 文件名
		   filename.setValue(data.text);
		   // 描述
		   desc.setValue(data.qtip);
		   // caller
		   caller.setValue(data.caller);
	   };
	   win.show();
	},
	updateTemplate:function(btn){
		var me = this,
		form = btn.ownerCt.ownerCt.getForm() 
		if(!me.checkForm(form)){
			return;
		}
		if (!form.isValid()) {
			return;
		}
		var params = form.getValues();
		var tree= Ext.getCmp('excelTree');
		var subof = tree.getSelectionModel().getSelection()[0].data.parentId;
		subof=subof=='root'?0:subof;
		params.id=tree.getSelectionModel().getSelection()[0].data.id;
		Ext.getCmp('update_win').close();
		Ext.Ajax.request({
        	url : basePath + 'Excel/template/update.action',
        	params: params,
        	callback : function(options,success,response){
        		tree.setLoading(false);
        		var res = new Ext.decode(response.responseText);        	
        		if(res.success){
					Ext.MessageBox.alert("提示","更新成功");
					tree.getTreeRootNode(0);
        		} else if(res.exceptionInfo){
        			Ext.MessageBox.alert("提示",res.exceptionInfo);
        		}
        	}
        });
	},
	checkForm: function(form){
		var s = '';
		form.getFields().each(function (item, index, length){
			if(!item.isValid()){
				if(s != ''){
					s += ',';
				}
				if(item.fieldLabel || item.ownerCt.fieldLabel){
					s += item.fieldLabel || item.ownerCt.fieldLabel;
				}
			}
		});
		if(s == ''){
			return true;
		}
		return false;
	},
	newFromTpl:function(filecaller,isManager){
		var me = this;
		Ext.Ajax.request({
        	url : basePath + 'Excel/template/newFromTpl.action',
        	params: {
        		filecaller: filecaller
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if (res.success) {
        			if (isManager) {
	        			var grid=Ext.getCmp('excelGrid'),
	        			store = grid.getStore();
	        			store.reload();
        			}
        			me.loadExcel(res.fileId,false);
        		}
        	}
        });
	},
	// 加载模板类型子树
	getExcelTreeBySubof:function(tree, record){
		var me = this;
		if (record.isExpanded() && record.childNodes.length > 0) {
		   	record.collapse(true);// 收拢
		    me.flag = true;
		}else{
			if (record.childNodes.length == 0) {
				var subof = record.data.id;
		        tree.setLoading(true, tree.body);
				Ext.Ajax.request({// 拿到tree数据
		        	url : basePath + 'Excel/template/getExcelTreeBySubof.action',
		        	params: {
		        		subof: subof,
		        		condition: ""
		        	},
		        	callback : function(options,success,response){
		        		tree.setLoading(false);
		        		var res = new Ext.decode(response.responseText);        	
		        		if(res.tree){
						   if(res.tree.length==0){
							   if(record.get("expanded")){
								   record.collapse(true);// 收拢
							   }
						   }else{
						     record.appendChild(res.tree);
						     record.expand(false,true);	
						   }
					       me.flag = true;
		        		} else if(res.exceptionInfo){
		        			Ext.MessageBox.alert("提示",res.exceptionInfo);
		        			me.flag = true;
		        		}
		        	}
		        });
			}else {
				record.expand(false,true);// 展开
				me.flag = true;
	    	}
		}
	},
	
	showGridWin:function(id,filecaller,name){
	   var me = this,
	   win = Ext.getCmp('grid_win');
	   if(!win){
		   win=Ext.create('Ext.window.Window',{
			   width: 735,
			   height:500,
// closeAction: 'hide',
			   id:'grid_win',
			   filetplsource:id,
			   filecaller:filecaller,
			   layout:'fit',
			   modal:true,
			   title:'<div align="center" class="WindowTitle">'+name+'的实例文件</div>', 
			   items:[
			   	{xtype:'excelGridPanel',
				 listeners:{
				 	afterrender:function(grid){
				 		var store = grid.getStore();
				 		var pagetool = Ext.getCmp('pagetool');
				 		pagetool.store.currentPage=1;
				 		store.load({
				 			params:{
				 				filetplsource:id,
				 				condition: "FILEMAN = '"+em_name+"'"
				 			}
				 		}),
						store.on("beforeload",function(){
							Ext.apply(store.proxy.extraParams, {
								filetplsource:id,
								condition: "FILEMAN = '"+em_name+"'"
							});
						})				 		
				 	}
				 }
				}],
				buttonAlign:'center',
				buttons: [{
					    text: '创建新文件',
					    handler:function(btn){
					    	me.newFromTpl(Ext.getCmp('grid_win').filecaller,true);
// Ext.getCmp('grid_win').close();
					    }
				},{
					xtype:'form',
					frame: false,
					border: false,
					layout:'column',
					items:[{
			            xtype: 'filefield',
			            id: 'ExcelFile',
			            columnWidth:1,
			            buttonOnly:true,
			            hideLabel:true,
			            name: 'file',
			            buttonText: '导入新文件',
			            listeners: {
			            	change:function(btn){
			            		me.importExcelFile(btn,Ext.getCmp('grid_win').filecaller,true);
			            	}
			            }
			        }]
				},{
					text: '关闭',
				    handler:function(btn){
				    	Ext.getCmp('grid_win').close();
				    }
				}]	
		   });
	   }
	   win.show();
/*
 * var el=button.getEl(); button.getEl().dom.disabled = true; if
 * (win.isVisible()) { win.hide(el, function() { el.dom.disabled = false; }); }
 * else { win.show(el, function() { el.dom.disabled = false;
 * Ext.getBody().disabled=true; }); }
 */
	},
	importExcelFile:function(btn,filecaller,isManager){
		var me = this,
		form = btn.ownerCt.getForm(),
		tree= Ext.getCmp('excelTree');
		if(form.isValid()){
            form.submit({
                url: basePath + 'Excel/upload/importExcel.action',
                waitMsg: '解析文件中',
                method:'POST',
        		params:{
	    			filecaller:filecaller
	    		},
                success: function(fp, o) {
                    var res = o.result;        	
	        		if(res.success){
	        			if (isManager) {
		        			var grid=Ext.getCmp('excelGrid'),
		        			store = grid.getStore();
		        			store.reload();
	        			}
	        			me.loadExcel(res.id,false);
	        		} 
                },
                failure: function(fp, o) {
                	var res = o.result; 
                	if (res.exceptionInfo) {
                	}
                }
            });
        }
	},
	importExcelTemplate:function(btn){
		var me = this,
		form = btn.ownerCt.ownerCt.getForm(),
		tree= Ext.getCmp('excelTree'),
		subof = tree.getSelectionModel().getSelection()[0].data.id;
		if(form.isValid()){
            form.submit({
                url: basePath + 'Excel/upload/importExcelTemplate.action',
                waitMsg: '解析文件中',
                method:'POST',
        		params:{
	    			subof:subof
	    		},
                success: function(fp, o) {
                    var res = o.result;        	
	        		if(res.success){
	        			tree.refreshNodeByParentId(subof,tree); 
	        			me.loadExcel(res.id,true);
	        			Ext.getCmp('add_win').close();
	        		} 
                },
                failure: function(fp, o) {
                	var res = o.result; 
                	if (res.exceptionInfo) {
                		Ext.MessageBox.alert("提示",res.exceptionInfo);
                		Ext.getCmp('add_win').close();
                	}
                }
            });
        }
	},
	doSearch:function(){
		var filetplsource = Ext.getCmp('grid_win').filetplsource,
		grid = Ext.getCmp('excelGrid'),
		condition="";
		if (Ext.getCmp('FILENAME').getValue()!="") {
			condition = condition +"FILENAME like '%"+Ext.getCmp('FILENAME').getValue()+"%' AND ";
		}
		if (Ext.getCmp('FILECREATETIME').getValue()!=null) {
			var date = new Date(Ext.getCmp('FILECREATETIME').getValue());
			condition = condition +"FILECREATETIME like  to_date('"+Ext.Date.format(date,'Y-m-d')+"','yyyy-MM-dd') AND ";
		}
		if(Ext.getCmp('FILEMAN').getValue()!=""){
			condition = condition + "FILEMAN like '%"+Ext.getCmp('FILEMAN').getValue()+"%' AND ";
		}
		
 		var store = grid.getStore();
 		var pagetool = Ext.getCmp('pagetool');
 		pagetool.store.currentPage=1;
 		store.load({
 			params:{
 				filetplsource:filetplsource,
 				condition: condition+" 1=1 "
 			}
 		})
	},
	loadExcel:function(id,isTpl){
		var me = this,
 		hd = me.getSHEET_API_HD();
 		isTplfile=isTpl;
        me.SHEET_API.loadFile(hd,id,function(data){
        }, this);
	},
	dealUrl:function(){
		var me=this, 
		formCondition = getUrlParam('formCondition'),
		hideTree = getUrlParam('hideTree'),
		operate = getUrlParam('operate'),
		filecaller = getUrlParam('filecaller'),
		record = getUrlParam('record');
		// 隐藏树
		if (hideTree) {
			Ext.getCmp('west').hide();
		}
		// 是否录入人模式
		if(record){
		  me.hideEditButton();
		}
		// 判断操作
		if (operate=='load') {
			// 载入实例文件
			if (formCondition) {
				me.loadExcel(formCondition.replace(/fileidIS/g,""),false);
			}			
		}else if(operate=='new'){
			// 弹窗；以当前模板创建新文件，导入任意文件
			if (filecaller) {
				
				
			var box = Ext.create('Ext.window.MessageBox', {
				buttonAlign : 'center',
				buttons: [{
					    text: '以模板创建',
					    handler:function(btn){
					    	me.newFromTpl(filecaller,false);
					    	box.close();
					    }
				},{
					xtype:'form',
					frame: false,
					border: false,
					layout:'column',
					items:[{
			            xtype: 'filefield',
			            columnWidth:1,
			            buttonOnly:true,
			            hideLabel:true,
			            name: 'file',
			            buttonText: '导入新文件',
			            listeners: {
			            	change:function(btn){
			            		me.importExcelFile(btn,filecaller,false);
			            		box.close();
			            	}
			            }
			        }]
				}]
			});
			
			setTimeout(function(){
				box.show({
					title : $I18N.common.msg.title_prompt,
					msg : '请选择创建方式'
				});	
			},10);

			}
		}
	}
	
	
	
	
	
	
	
	
});