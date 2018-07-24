Ext.QuickTips.init();

Ext.define('erp.view.plm.project.ProjectFileListTreeGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpProjectFileListTreeGrid',
	id : 'prjFileListTreeGrid',
	title : '文件清单',
	bodyStyle : 'background-color:#f1f1f1;',
	columnLines : true,
	readOnly : false,
	nodeId : null, // 当前节点的id
	maintaskactive:false,
	layout:'fit',
	emptyText: '未查询到结果!', 
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	requires : ['erp.view.core.button.UpExcel',
			'erp.view.core.button.ExportDetail',
			'erp.view.core.button.DirectImportUpExcel'],
	defaults:{
		style:'text-align:center'
	},
	columns : {
		items:[{
			header : '序号',
			dataIndex : 'pd_detno',
			width : 55,
			editor : 'numberfield',
			hidden : false
		}, {
			header : '文件名称',
			dataIndex : 'pd_name',
			width:220,
			align:'left',
			renderer:function(val){
				var search = Ext.getCmp('search').getValue();
				if(isSearch&&search){
					var regexp = new RegExp(search,"g");
					return val.replace(/\n/g,'<br>').replace(regexp,'<font color=\"#FF0000\">'+search+'</font>')
				}
				return val.replace(/\n/g,'<br>');
			},
			editor : 'textfield'
		}, {
			header : '文件编号',
			width:130,
			dataIndex : 'pd_code',
			editor : 'textfield'
		}, {
			header : 'pd_parentid',
			dataIndex : 'pd_parentid',
			hidden : true
		},{
			header:'附件',
			triggerCls: 'x-form-textarea-trigger',
			width:250,
			align:'left',
			dataIndex:'pd_filepath',
			renderer:function(val, meta, record, x, y, store, view){
				if(record&&record.data["pd_filepath"]!=null&&record.data["pd_filepath"]!=""){	
					var folderId = record.get('pd_parentid');
					var attach=record.data["pd_filepath"];
					var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
						if(record.data[field] != attach){
						record.set(field,attach);
					}
					return 	'<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="display:inline-block;width:90%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;color:green;padding-left:2px;vertical-align:middle;">' + attach.split(";")[0] + '</span><span  onclick="downFile('+attach.split(";")[1]+','+folderId+')"><img src="' + basePath + 'resource/images/icon/download.png"></span>';
				}else if(record&&val!=null&&val!=""){
					var folderId = record.get('pd_parentid');
					return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="display:inline-block;width:90%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;color:green;padding-left:2px;vertical-align:middle;">' + val.split(";")[0] + '</span><span onclick="downFile('+val.split(";")[1]+','+folderId+')"><img src="' + basePath + 'resource/images/icon/download.png"></span>';
				}else return val;
			}		
		},{
			header : '备注',
			width:350,
			dataIndex : 'pd_remark',
			editor : 'textfield',
			hidden:true
		},{
			header : 'id',
			dataIndex : 'pd_id',
			hidden : true
		},{
			header : 'kind',
			dataIndex : 'pd_kind',
			value : -1,
			hidden : true
		},{
			header : '版本号',
			dataIndex : 'dl_version',
			width:60
		},{
			header : '操作时间',
			dataIndex : 'dl_createtime',
			width:150
		},{
			xtype:'actioncolumn',
			header:'历史信息',
			width:100,
			renderer:function(val,meta,record){
				if(record.data.pd_filepath&&record.data.pd_id&&record.data.pd_id!=-1&&record.data.dl_version){
					return '<span onclick="viewMore()" style="color:blue">查看历史</span>';			
				}
		}}],
		defaults:{
			style:'text-align:center',
			align:'center',
			cls : 'x-grid-header'
		}
	},
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		ignoreRightMouseSelection : false
	}),
	store : Ext.create('Ext.data.Store', {
		storeId : 'myStore',
		pageSize : 25,
		fields : ['pd_detno', 'pd_name', 'pd_code', 'pd_parentid', 'pd_remark',
				'pd_id', 'pd_kind','pd_filepath','pd_prjid','pd_virtualpath','dl_version','dl_createtime'],
		autoLoad : false,
		proxy : {
			type : 'ajax',
			url : basePath + 'plm/project/getProjectFileList.action',
			reader : {
				type : 'json',
				root : 'datas',
				totalProperty : 'total'
			},
			actionMethods: {
	            read   : 'POST'
	        }
		},
		listeners : {
			beforeload : function() {
				var grid = Ext.getCmp("prjFileListTreeGrid");
				if(isSearch){
					var search = Ext.getCmp('search').getValue();
					Ext.apply(grid.getStore().proxy.extraParams, {
						formCondition : formCondition,
						search: search,
						id: 0,
						kind: 0,
						canRead: canRead
					});
				}else{
					Ext.apply(grid.getStore().proxy.extraParams, {
						formCondition: formCondition,
						id: grid.nodeId,
						kind: 0,
						canRead: canRead
					});
				}
				
			},
			datachanged:function(){
				var grid = Ext.getCmp("prjFileListTreeGrid");
				grid.scrollByDeltaX(1);  //为了解决grid列错位的bug
				grid.scrollByDeltaX(-1);
			}
		}
	}),
	dockedItems : [{
		xtype : 'toolbar',
		dock : 'top',
		aligh:'right',
		pack:'right',
		height:36,
		layout : {
			//pack : 'right'
		},
		border : false,
		defaults:{
			style:'margin-left:5px;margin-top:5px'
		},
		items : [{
			xtype: 'triggerfield',
			id: 'search',
			triggerCls: 'x-form-search-trigger',
			width:200,
			onTriggerClick: function() {
				var trigger = this;
				var grid = trigger.ownerCt.ownerCt;
				isSearch = true;
				var addfilebtn = Ext.getCmp('addFileButton');
				var savefilbtn = Ext.getCmp('saveFileButton');
				addfilebtn.setDisabled(true);
				savefilbtn.setDisabled(true);
				grid.store.load({
					params : {
						formCondition:formCondition,
						search: trigger.getValue(),
						kind: 0,
						id: 0
					},
					callback:function(records, operation, success){
						if(success){
							var res = Ext.decode(operation.response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);return;	
							}
							if(records.length<1){
								Ext.Msg.alert('提示','未找到文件！');
								return;
							}
						}
					}
				});
			},
			listeners : { 
                specialkey : function(trigger, e) { 
                    if (e.getKey() == Ext.EventObject.ENTER) { 
                        var grid = trigger.ownerCt.ownerCt;
						isSearch = true;
						var addfilebtn = Ext.getCmp('addFileButton');
						var savefilbtn = Ext.getCmp('saveFileButton');
						addfilebtn.setDisabled(true);
						savefilbtn.setDisabled(true);
						grid.store.load({
							params : {
								formCondition:formCondition,
								search: trigger.getValue(),
								kind: 0,
								id: 0
							},
							callback:function(records, operation, success){
								if(success){
									var res = Ext.decode(operation.response.responseText);
									if(res.exceptionInfo){
										showError(res.exceptionInfo);return;	
									}
									if(records.length<1){
										Ext.Msg.alert('提示','未找到文件！');
									}
								}
							}
						});
                    } 
                } 
            } 
		},'->',{
			text : '阅读',
			id : 'readFileButton',
			width : 60,
			disabled : true,
			iconCls : 'x-button-icon-read',
			cls : 'x-btn-gray'
		},{
         	id:'fileform',
         	xtype:'form',
			height : 26,
        	layout:'column',
        	bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
	  		items: [{
			xtype : 'filefield',
			name : 'file',
			buttonOnly : true,
			hideLabel : true,
			disabled : true,
			width : 60,
			id : 'file',
			buttonConfig : {
				iconCls : 'x-button-icon-pic',
				text : '上传',
				cls : 'x-btn-filefield',
				listeners:{
					click:function(btn){
						var grid = Ext.getCmp('prjFileListTreeGrid');
						if(!grid.maintaskactive){
							Ext.getCmp('file').disable();
							Ext.Msg.alert('警告','该项目对应的产品开发任务书未激活!');							
						}
					}
				}
			},
			listeners : {
				change : function(field) {
					var filename = '';
					if (contains(field.value, "\\", true)) {
						filename = field.value.substring(field.value
								.lastIndexOf('\\')
								+ 1);
					} else {
						filename = field.value.substring(field.value
								.lastIndexOf('/')
								+ 1);
					}
					
					var grid = Ext.getCmp('prjFileListTreeGrid');
					var tree = Ext.getCmp("prjFileListTree");
					var treenode = tree.getSelectionModel().getSelection()[0];
					var select = grid.selModel.lastSelected;
					var fieldId = select.data.pd_id;
					var condition = '';
					//上传附件没有保存自动保存
					if(fieldId<=0){
						if(!grid.save(grid,'hide')){ 
							field.reset();
							return;
						}else{
							condition = 'pd_parentid='+treenode.get("pd_id")+' and pd_detno='+select.data.pd_detno+" and pd_name='"+select.data.pd_name+"'";
						}
					}
					field.ownerCt.getForm().submit({
						url : basePath + 'plm/project/upload.action',
						waitMsg : "正在解析文件信息",
						params:{
							fieldId:fieldId,
							condition:condition,
							_noc:_noc
						},
						success : function(fp, o) {
							if (o.result.error) {
								showError(o.result.error);
							} else {
								Ext.Msg.alert("恭喜", filename + " 上传成功!");
								field.setDisabled(true);
								Ext.Ajax.request({
									url:basePath + 'plm/project/getProjectFileList.action',
									params : {
										id : treenode.get("pd_id"),
										kind : 0,
										formCondition:formCondition,
										canRead: canRead,
										page:grid.store.currentPage,
										limit:grid.store.pageSize										
									},
									method:'post',
									callback:function(options,success,response){
										var res = Ext.decode(response.responseText);
										if(res.success){
											grid.store.loadData(res.datas);
										}else if(res.exceptionInfo){
											showError(res.exceptionInfo);
										}
									}
								});
							}
						},
						failure: function(fp, o){
							if (o.result.error) {
								showError(o.result.error);
							}
						}
					});
				}
			}
	  		}]					
		},{
			text : '新增',
			id : 'addFileButton',
			width : 60,
			iconCls : 'x-button-icon-add',
			cls : 'x-btn-gray'
		},{
			xtype : 'button',
			text : '保存',
			id : 'saveFileButton',
			width : 60,
			iconCls: 'x-button-icon-save',
			cls: 'x-btn-gray'
		}, {
			xtype : 'button',
			text : '删除',
			id : 'deleteFileButton',
			iconCls: 'x-button-icon-delete',
			cls: 'x-btn-gray',
			width : 60,
			handler : function(btn) {
				var grid = btn.ownerCt.ownerCt;
				var records = grid.getSelectionModel().getSelection();
				var ids = '';
				if(records.length>0){
					Ext.Array.each(records,function(item,index){
						if(item.data.pd_filepath&&item.data.pd_id&&item.data.pd_id!=-1){
							Ext.Msg.alert('提示','当前文件已经上传附件，不允许删除！');
							return false;
						}else{
							if(item.data.pd_id!=-1){
								ids += ',' + item.data.pd_id;
							}else{
								grid.store.remove(item);
							}							
						}
					});
					if(''!=ids){
						grid.deleteNode(ids.substring(1),'file');
					}
				}
			}
	}]
	}, {
		xtype : 'pagingtoolbar',
		dock : 'bottom',
		displayInfo : true,
		store : Ext.data.StoreManager.lookup('myStore'),
		displayMsg:"显示{0}-{1}条数据，共{2}条数据",
		beforePageText: '第',
		afterPageText: '页,共{0}页'
	}],
	plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit : 1
	})],
	save : function(grid,showTip) {
		var me = this;
		var create = [];
		var update = [];
		var bool = true;
		for (var i = 0; i < grid.store.data.items.length; i++) {
			var item = grid.store.data.items[i];
			if (item.data.pd_id == null || item.data.pd_id == ""
					|| typeof(item.data.pd_id) == 'undefined') {
				item.data.pd_id = -1;
			}
			if (item.data.pd_parentid == null || item.data.pd_parentid == ""
					|| typeof(item.data.pd_parentid) == 'undefined') {
				item.data.pd_parentid = grid.nodeId;
			}
			if (item.data.pd_kind == null || item.data.pd_kind == ""
					|| typeof(item.data.pd_kind) == 'undefined') {
				item.data.pd_kind = 0;
			}
			if (item.data.pd_name == null || item.data.pd_name.trim() == "") {
				showError("文件名称不能为空");
				return;
			}

			var data = {};
			data.pd_id = item.data.pd_id;
			data.pd_kind = item.data.pd_kind;
			data.pd_parentid = item.data.pd_parentid;
			data.pd_name = item.data.pd_name.trim(); // 去掉前后空格
			data.pd_remark = item.data.pd_remark;
			data.pd_detno = item.data.pd_detno;
			data.pd_virtualpath = item.data.pd_virtualpath;
			data.pd_filepath = item.data.pd_filepath;
			if (item.data.pd_code) {
				data.pd_code = item.data.pd_code.trim(); // 去掉前后空格
			} else {
				data.pd_code = item.data.pd_code; // 去掉前后空格
			}
			data.pd_prjid = item.data.pd_prjid;                

			if (item.dirty && item.data.pd_id != -1) {
				update.push(data);
			} else if (item.data.pd_id == -1) {
				create.push(data);
			}
		}
		
		if (create.length > 0 || update.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'plm/project/saveAndUpdateProjectFileList.action',
				method : 'post',
				async:false,
				params : {
					create : Ext.encode(create),
					update : Ext.encode(update),
					_noc: _noc
				},
				callback : function(options, success, response) {
					var res = Ext.JSON.decode(response.responseText);
					if (res.success) {
						if(!showTip){
							showTip = 'show';
						}
						if(showTip!='hide'){
							showMessage('提示','保存成功',1000);
							var tree = Ext.getCmp("prjFileListTree");
							var grid = Ext.getCmp("prjFileListTreeGrid");
							var treenode = tree.getSelectionModel().getSelection()[0];
							grid.store.load({
								params : {
									id : treenode.get("pd_id"),
									kind : 0
								}
							});
						}
					} else if (res.exceptionInfo) {
						showError(res.exceptionInfo);
						bool =  false;
					}
				}
			});
		} else {
			Ext.Msg.alert("提示", "未修改或新增数据");
		}
		return bool;
	},
	deleteNode : function(nodes, type) {
		Ext.Ajax.request({
			url : basePath + 'plm/project/deleteProjectFile.action',
			params : {
				id : nodes,
				type : type,
				_noc: _noc
			},
			callback : function(options, success, response) {
				var res = Ext.decode(response.responseText);
				if (res.success) {
					showMessage('提示','删除成功',1000);

					var tree = Ext.getCmp("prjFileListTree");
					var grid = Ext.getCmp("prjFileListTreeGrid");

					var treenode = tree.getSelectionModel()
							.getSelection()[0];
					grid.store.load({
						params : {
							id : treenode.get("pd_id"),
							kind : 0,
							formCondition:formCondition
						}
					});
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
				else {
					Ext.Msg.alert("提示", "未知错误");
				}
			}
		});
	},
	viewMore:function(record){
		var me = this;
		var fileList = Ext.getCmp('prjFileListTreeGrid');
		var record = fileList.selModel.lastSelected;
		var win = new Ext.window.Window({
			id : 'win',
			height : 400,
			width : 902,
			maximizable : true,
			border:false,
			buttonAlign : 'center',
			layout : 'anchor',
			title : '附件信息',
			bodyStyle : 'background:#F2F2F2;',
			items : [{
				xtype:'gridpanel',
				id:'versionGrid',
				width:'100%',
				height:'100%',				
				columns:[{
					text:'ID',
					dataIndex:'dv_id',
					width:0
				},{
					cls : "x-grid-header-1",
					header: '阅读',
					xtype:'actioncolumn',		
					align:'center',
					width:40,
					icon: basePath + 'resource/images/icon/read.png',
					tooltip: '阅读',
					handler: function(grid, rowIndex, colIndex) {	
						var select=grid.getStore().getAt(rowIndex);
						var folderId = Ext.getCmp('prjFileListTree').getSelectionModel().getSelection()[0].get('pd_id');
						var fileList = Ext.getCmp('prjFileListTreeGrid');
						var name = unescape(select.data.dv_name);
						var type = name.substring(name.lastIndexOf('.') + 1);
						fileList.readFile(select.data.dv_fpid,folderId,type);
					}
				},{
					cls : "x-grid-header-1",
					header: '下载',
					xtype:'actioncolumn',		
					align:'center',
					width:40,
					icon: basePath + 'resource/images/icon/download.png',
					tooltip: '下载',
					handler: function(grid, rowIndex, colIndex) {	
						var select=grid.getStore().getAt(rowIndex);
						var folderId = Ext.getCmp('prjFileListTree').getSelectionModel().getSelection()[0].get('pd_id');
						fileList.downFile(select.data.dv_fpid,folderId);
					}
				},{
					cls : "x-grid-header-1",
					text: '文件名称',
					dataIndex: 'dv_name',
					width:300,
					readOnly:true				
				},{
					cls : "x-grid-header-1",
					text: '修订号',
					dataIndex: 'dv_detno',
					width:60,
					align:'center',
					readOnly:true
				},{
					cls : "x-grid-header-1",
					text:'操作人',
					align:'center',
					dataIndex:'dv_man',
					width:100,
					readOnly:true
				},{
					cls:'x-grid-header-1',
					text:'操作时间',
					dataIndex:'dv_time',
					width:150,
					readOnly:true,
					xtype:"datecolumn",
					format:"Y-m-d H:i:s"
				},{
					cls : "x-grid-header-1",
					text: '修改说明',
					dataIndex: 'dv_explain',
					width:200,
					readOnly:true
				}],
				store:Ext.create('Ext.data.Store', {
					fields:[{
						name: 'dv_id',
						type: 'number'
					},{
						name:'dv_dlid',
						type:'number'
					},{
						name:'dv_detno',
						type:'number'
					},{
						name: 'dv_name',
						type: 'string'
					},{
						name:'dv_filepath',
						type:'string'
					},{
						name:'dv_man',
						type:'string'
					},{
						name:'dv_time',
						type:'date'
					},{
						name:'dv_fpid',
						type:'string'
					}],
					data:[]
				}),
				listeners:{
					afterrender:function(grid){
						me.loadNewStore(grid,{
							caller:'DocumentVersion',
							condition:"dv_dlid=(select dl_id from documentlist where dl_prjdocid="+record.data.pd_id+")"
						});								
					}
				}
			}]
		});	
		win.show();
	},
	loadNewStore: function(grid, param){
		var me = this;
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + "common/loadNewGridStore.action",
			params: param,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				var data = res.data;
				if(!data || data.length == 0){
					grid.store.removeAll();
				} else {
					grid.store.loadData(data);
				}
			}
		});
	},
	downFile: function(id,folderId){
		if (!Ext.fly('ext-attach-download')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-attach-download';  
			frm.name = id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);  
		}
		Ext.Ajax.request({
			url: basePath + 'plm/project/downloadbyId.action?id=' + id +'&folderId='+folderId+'&_noc='+_noc+'&canRead='+(canRead == null ? 0 : canRead) ,
			method: 'post',
			form: Ext.fly('ext-attach-download'),
			isUpload: true,
			callback:function(options,success,resp){
				var begin = resp.responseText.indexOf('{"exceptionInfo":"');
				if(begin>-1){
					var end = resp.responseText.indexOf("\"}");
					var str = resp.responseText.substring(begin+'{"exceptionInfo":"'.length,end);
					showError(str);	
				}
			}
		});
	},
	readFile: function(id,folderId,type){
		var me = this;
		if (type == 'doc'|| type =='docx'|| type == 'xls'|| type == 'xlsx'||type == 'pdf') {
			
			Ext.Ajax.request({
				url : basePath + 'plm/project/getHtml.action',
				params: {
					folderId:folderId,
					id:id,
					type:type,
					_noc:_noc,
					canRead:canRead
				},
				method : 'post',
				async:false,
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.exceptionInfo){
						showError(r.exceptionInfo);
					} else if(r.success){
						var url=basePath;
						var path=r.newPath;
						if(type == 'doc'|| type =='docx'|| type == 'xls'|| type == 'xlsx'){
							url += 'jsps/oa/doc/readWordOrExcel.jsp?path='+basePath+path;
						}else if (type == 'pdf') {
							url += 'jsps/oa/doc/read.jsp?path='+ path + '&folderId='+ folderId;
						}
						window.open(url);
					} 
				}
			});	
		}else if(type == 'ppt'|| type=='pptx'){
			me.getEl().mask('正在解析，请稍后...');
			Ext.Ajax.request({
				url: basePath + 'plm/project/getPPTHtml.action',
				params: {
					folderId:folderId,
					id:id,
					type:type,
					_noc:_noc,
					canRead:canRead
				},
				method : 'post',
				callback: function(opt, s, res){
					me.getEl().unmask();
					var r = Ext.decode(res.responseText);
					if(r.exceptionInfo){
						showError(r.exceptionInfo);
					}else if(r.success){
						var url=basePath,
							pageSize = r.pageSize,
							path = r.path;
						url += 'jsps/oa/doc/readPPT.jsp?pageSize='+pageSize+'&path='+path;
						window.open(url);
					}
				}
			});
		}else if(me.isImage(type)){
			me.showAttach(id);
		} else {
			showMessage('提示','当前文件类型不支持在线预览，请先下载!');
		}
	},
	showAttach : function(id) {
		var me = this,src = basePath + 'common/downloadbyId.action?id=' + id + '&_noc='+_noc;
		var img = document.createElement("img");
		img.src = src;		 
		myWindow=window.open(); 
		myWindow.document.body.appendChild(img);
		myWindow.focus();
	},
	isImage : function(type) {
		if (Ext.isEmpty(type)) {
			return false;
		}
		var images = ['gif','jpg','jpeg','png','GIF','JPG','PNG'];
		if (!Ext.Array.contains(images,type)) {
			return false;
		}
		return true;
	}
});
