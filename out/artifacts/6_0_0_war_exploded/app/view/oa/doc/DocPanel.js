Ext.define('erp.view.oa.doc.DocPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.erpDocPanel',
	id:'docpanel',
	requires:['erp.view.oa.doc.DocGrid'],
	collapsible :false,	
	defaults: { 
		autoScroll:false
	},
	style:'',
	flex: 4,
	width:800,
	layout:'fit',
	autoShow: true,
	currentItem:null,
	frame: true,
	dockedItems: [{
		xtype: 'toolbar',
		dock: 'top',
		style:'font-size:13px;font-family: "microsoft yahei", sans-serif;color: #333;border:none;',
		/*layout:'column',*/
		cls:'maintoolbar',
		height:36,
		defaults:{
			//style:'margin-left:10px;margin-top:2px;',
			style:'margin-left:10px;'
		},
		items: [/*{
			id:'upload',
			text:'上传',
			tooltip:'上传文件'	,
			xtype:'form',
			layout:'column',
			bodyStyle: 'background: transparent no-repeat 0 0;border: none;margin-left:40px;',
			items: [{
				xtype: 'filefield',
				name: 'file',
				buttonOnly: true,
				hideLabel: true,
				anchor: '100%',
				width:90,
				id:'attachfile',
				msgTarget: 'side',
				frame:false,
				buttonConfig: {
					text: '上传',
					cls: 'x-btn',
				},
				listeners: {
					change: function(field){
						var filename = '';
						if(contains(field.value, "\\", true)){
							filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
						} else {
							filename = field.value.substring(field.value.lastIndexOf('/') + 1);
						}
						field.ownerCt.getForm().submit({
							url: basePath + 'common/upload.action?em_code=' + em_code,
							waitMsg: "正在解析文件信息",
							success: function(fp,o){
								if(o.result.error){
									showError(o.result.error);
								} else {
									Ext.Msg.alert("恭喜", filename + " 上传成功!");
									field.setDisabled(true);
									var record=Ext.getCmp('grid').selModel.lastSelected;
									if(record){
										record.set('attachs',filename+";"+o.result.filepath);
									}
								}
							}	
						});
					}
				}}]

		},*/{
			xtype:'buttongroup',
			cls:'x-btn-group',
			height:26,
			items:[{
				xtype:'button',
				id:'uploadDoc',
				text:'上传',
				cls:" x-btn-gray",
			  	iconCls:'arrowup'
			}]
		},{
			xtype:'buttongroup',
			cls:'x-btn-group',
			height:26,
			items:[{
					xtype:'button',
					id:'downloadDoc',
					text:'下载',
					disabled:true,
					cls:"x-btn-gray",
					iconCls:'arrowdown'
				},{
					xtype: 'tbtext',
					text: '|',
					cls:'btn-split'
				},{
					xtype:'button',
					id:'read',
					text:'阅读',
					disabled:true,
					cls:"x-btn-gray",
					iconCls:'reading'//,
					/*handler:function(btn){
						showResult('提示','当前文件类型不支持在线预览，请先下载!',btn);
					}*/
				},{
					xtype: 'tbtext',
					text: '|',
					cls:'btn-split'
				},{
					xtype:'button',
					text:'发送',
					id:'publish',
					disabled:true,
					cls:"x-btn-gray",
					iconCls:'sending',
					hidden:true
				},{
					xtype: 'tbtext',
					text: '|',
					cls:'btn-split',
					hidden:true
				},{
					xtype:'button',
					id:'rename',
					text:'重命名',
					disabled:true,
					cls:"x-btn-gray",
					iconCls:'rename'
				},{
					xtype: 'tbtext',
					text: '|',
					cls:'btn-split'
				},{xtype:'button',
					id:'delete',
					text:'删除',
					disabled:true,
					cls:"x-btn-gray",
					iconCls:'deletedoc'
				},{
					xtype: 'tbtext',
					text: '|',
					cls:'btn-split'
				},{
					xtype:'button',
					id:'move',
					text:'移动',
					disabled:true,
					cls:'x-btn-gray',
					iconCls:'movedoc',
					handler:function(button){
						var win = Ext.getCmp('movewin');
						var docpanel=Ext.getCmp('doctab');
						if(!win){
							win=Ext.create('Ext.window.Window',{
								width: 550,
								height:350,
								closeAction: 'destroy',
								id:'movewin',
								layout:'fit',
								title:'<div align="center" class="WindowTitle">移动文件</div>', 
								listeners:{
									hide:function(win){
										win.destroy();
									}  
								},
								items:[{
									xtype: 'erpDocumentTreePanel', 	       	      
									bodyStyle:"background-color:#FAFAFA;",
									id:'foldertree',
									store: Ext.create('Ext.data.TreeStore', {
										root: {
											expanded: true,
											children: [{
												text: '公共文档',
												expanded: false,
												url:"/公共文档",
												id:0
											}]
										}
									})		    				
								}],
								buttonAlign:'center',
								buttons:[{
									cls:'x-btn-ok',
									text: '确定',
									handler:function(btn){
										var grid=Ext.getCmp('docgrid'),me=this;
										var items = grid.selModel.getSelection();
										var select= Ext.getCmp('foldertree').getSelectionModel();
										if(items.length<1) showResult('提示','请选择需要移动的文档!',btn);
										if(select.lastFocused == null || select.lastFocused == ''){
											showResult('提示','请选择需要移动到文件夹!',btn);
											return;
										}
										warnMsg('确认移动选中文档或文件夹?', function(btn){
											if(btn == 'yes'){
												var data=new Array();
												Ext.Array.each(items,function(item){
													data.push(Ext.JSON.encode(item.data));
												});
												Ext.Ajax.request({
													url : basePath + 'oa/doc/moveDoc.action',
													params : {
														data :unescape(data.toString()),
														folderId : select.lastFocused.data.id
													},
													method : 'post',	    	
													callback : function(options,success,response){
														var localJson = new Ext.decode(response.responseText);
														if(localJson.exceptionInfo){
															var str = localJson.exceptionInfo;
															showError(str);
														}else {
															var docpanel=Ext.getCmp('docpanel'),docGrid = Ext.getCmp('docgrid');
															docGrid.selModel.deselectAll(true);
															docpanel.loadNewStore(CurrentFolderId);
															docpanel.reSetButton(docpanel);
															showResult('提示','移动成功!',btn);
															me.up('window').close();
														}
													}
												});
											}
										}); 
									}
								},{
									cls:'x-btn-close',
									text: '关闭',
									handler:function(btn){
										this.up('window').close();
									}
								}]
							});
						}
						var el=button.getEl();
						button.getEl().dom.disabled = true;
						if (win.isVisible()) {
							win.hide(el, function() {
								el.dom.disabled = false;
							});
						} else {
							win.show(el, function() {
								el.dom.disabled = false;
								Ext.getBody().disabled=true;
							});
						}
					}
				},{
					xtype: 'tbtext',
					text: '|',
					cls:'btn-split'
				},{
					xtype:'button',
					id:'lockdoc',
					text:'锁定',
					disabled:true,
					cls:'x-btn-gray',
					iconCls:'lockdoc'/*,
					handler:function(button){
					
					}*/
				}/*{
					xtype:'button',
					id:'relate',
					text:'关联',
					disabled:true,
					cls:'x-btn-gray',
					//iconCls:'connetdoc',
					handler:function(button){
						var win = Ext.getCmp('relatewin');
						var docpanel=Ext.getCmp('doctab');
						if(!win){
							win=Ext.create('Ext.window.Window',{
								width: 450,
								height:200,
								closeAction: 'hide',
								id:'relatewin',
								title:'<div align="center" class="WindowTitle">关联文档</div>', 
								listeners:{
									hide:function(win){
										win.destroy();
									}  
								},
								bodyStyle:'background:#F0F0F0;color:#515151;',
								items:[{
									xtype:'textfield',
									fieldLabel:'关联编号',
									name:'relatecode',
									allowBlank:false,
									cls:'form-field-allowBlank',
									fieldStyle : "background:#FFFAFA;color:#515151;",
									id:'relatecode'
								}],
								buttonAlign:'center',
								buttons:[{
									cls:'x-btn-ok',
									handler:function(btn){
										var grid=Ext.getCmp('docgrid'),me=this,relatecode=Ext.getCmp('relatecode');
										var items = grid.selModel.getSelection();
										if(items.length<1) showResult('提示','请选择需要移动的文档!',btn);
										if(!relatecode.getValue()) showResult('提示','请先选择需要设置关联的文档!',btn);
										warnMsg('确认移动选中文档或文件夹?', function(btn){
											if(btn == 'yes'){
												var data=new Array();
												Ext.Array.each(items,function(item){
													data.push(Ext.JSON.encode(item.data));
												});
												Ext.Ajax.request({
													url : basePath + 'oa/doc/relateDoc.action',
													params : {
														data :unescape(data.toString()),
														relateCode : relatecode.getValue()
													},
													method : 'post',	    	
													callback : function(options,success,response){
														var localJson = new Ext.decode(response.responseText);
														if(localJson.exceptionInfo){
															var str = localJson.exceptionInfo;
															showError(str);
														}else {
															var docpanel=Ext.getCmp('docpanel');
															docpanel.loadNewStore(CurrentFolderId);
															showResult('提示','移动成功!',btn);
															me.up('window').close();
														}
													}
												});
											}
										}); 
									}
								},{
									cls:'x-btn-close',
									handler:function(btn){
										this.up('window').close();
									}
								}]
							});
						}
						var el=button.getEl();
						button.getEl().dom.disabled = true;
						if (win.isVisible()) {
							win.hide(el, function() {
								el.dom.disabled = false;
							});
						} else {
							win.show(el, function() {
								el.dom.disabled = false;
								Ext.getBody().disabled=true;
							});
						}
					}
				},{
					xtype: 'tbtext',
					text: '|',
					cls:'btn-split'
				},{
					xtype:'button',
					id:'switch',
					text:'切换',
					cls:'x-btn-gray',
					//iconCls:'switch',
					scanType:'list'	
					
				}*/
				]
		},{
			xtype:'buttongroup',
			cls:'x-btn-group',
			height:26,
			items:[{
				xtype:'button',
				id:'editdoc',
				text:'版本修改',
				disabled:true,
				cls:" x-btn-gray",
			  	iconCls:'editdoc'
			},{
				xtype: 'tbtext',
				text: '|',
				cls:'btn-split'
			},{
				xtype:'button',
				text:'设置权限',
				//cls:'button1 pill',
				id:'setdirpower',
				disabled:true,
				cls:" x-btn-gray",
			  	iconCls:'setdirpower'
				//style:'margin-left:20px; margin-top:5px;'
			}]
		}]
	}],
	initComponent : function(){
		this.getGridItem(this);
		this.callParent(arguments); 
	},
	getViewItem : function(panel) {
		var grid=Ext.getCmp('docgrid');
	},
	getGridItem: function(panel){
		return this.items=[{
			xtype:'docgrid'
		}];
	},
	loadNewStore:function(parentId,record){
		var me=this;
		me.items.items[0].getItemData(parentId,record);
	},
	reSetButton:function(panel,treeClick){
		var grid=Ext.getCmp('docgrid');
		var doctree=Ext.getCmp('doctree');
		var selects=grid.getSelectionModel().getSelection();
		var bar=panel.dockedItems.items[0];
		var btnsetdirpower = Ext.getCmp('setdirpower');//权限修改按钮
		Ext.Array.each(bar.items.items,function(item){
			Ext.Array.each(item.items.items,function(btnlist){
				if(btnlist.text!='上传'){
					if(btnlist.text=='解锁'||btnlist.text=='锁定'||btnlist.text=='重命名'||btnlist.text=='阅读'||btnlist.text=='版本修改'){
						btnlist.setDisabled(selects.length!=1);
					}else {
						btnlist.setDisabled(selects.length==0);
					}
					
					if(selects.length>0&&selects[0].data.dl_style=='目录'&&(btnlist.text=='阅读'||btnlist.text=='版本修改'||btnlist.text=='下载'||btnlist.text=='移动')){
						btnlist.setDisabled(true);
						//-1锁定 0不锁
					}else if(selects.length>0&&selects[0].data.dl_locked==-1){
						btnlist.setDisabled(true);
						if(btnlist.text=='锁定'){
							btnlist.text='解锁';
							btnlist.setDisabled(false);
						}else if(btnlist.text=='解锁'){
							btnlist.setDisabled(false);
						}
					}
					if(treeClick){
						btnlist.setDisabled(true);
					}
				}else{
					btnlist.setDisabled(CurrentFolderId == 5 ? true : false);
				}
			});
		});
		if(CurrentFolderId && doctree.getSelectionModel().getSelection().length==1){
			btnsetdirpower.setDisabled(false);
		}else{
			btnsetdirpower.setDisabled(true);
		}
		if(selects.length==1&&selects[0].data.dl_style=='目录'){
			btnsetdirpower.setDisabled(false);
		}else if(selects.length==1&&selects[0].data.dl_style!='目录'){
			btnsetdirpower.setDisabled(true);
		}else if(selects.length > 1){
			btnsetdirpower.setDisabled(true);
		}
		if(treeClick){
			btnsetdirpower.setDisabled(false);
		}

		/*if(selects.length > 0 && selects[0].data.dl_parentid != doctree.getSelectionModel().getSelection()[0].data.id){
			btnsetdirpower.setDisabled(false);
			Ext.Array.each(bar.items.items[1],function(item){
				item.setDisabled(true);
			});
			bar.items.items[2].items.items[0].setDisabled(true);
		}*/
	}

});