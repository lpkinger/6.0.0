Ext.QuickTips.init();

Ext.define('erp.view.common.FormsDoc.FormsDocTreeGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpFormsDocTreeGrid',
	id : 'FormsDocTreeGrid',
	title : '文件清单',
	bodyStyle : 'background-color:#f1f1f1;',
	columnLines : true,
	readOnly : false,
	nodeId : null, // 当前节点的id
	maintaskactive:false,
	layout:'fit',
	emptyText: '未查询到结果！', 
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
			dataIndex : 'fd_detno',
			width : 55,
			editor : 'numberfield',
			hidden : false
		}, {
			header : '文件名称',
			dataIndex : 'fd_name',
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
			header : '单据编号',
			width:130,
			dataIndex : 'fd_doccode'
		}, {
			header : 'fd_caller',
			width:130,
			dataIndex : 'fd_caller',
			hidden : true
		},{
			header : 'fd_parentid',
			dataIndex : 'fd_parentid',
			hidden : true
		},{
			header:'附件',
			triggerCls: 'x-form-textarea-trigger',
			width:250,
			align:'left',
			dataIndex:'fd_filepath',
			renderer:function(val, meta, record, x, y, store, view){
				if(record&&val!=null&&val!=""){
					var folderId = record.get('fd_parentid');
					var id = val.substring(val.lastIndexOf(';')+1);
					var name = val.substring(0,val.lastIndexOf(';'));
					return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="display:inline-block;width:90%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;color:green;padding-left:2px;vertical-align:middle;">' + name + '</span><span onclick="downFile(' + id + ','+folderId+')"><img src="' + basePath + 'resource/images/icon/download.png"></span>';
				}else return val;
			}		
		},{
			header : '操作时间',
			xtype : 'datecolumn',
			format:'Y-m-d H:i:s',
			width:150,
			dataIndex : 'fd_operatime'
		},{
			header : '备注',
			width:350,
			dataIndex : 'fd_remark',
			editor : 'textfield'
		},{
			header : 'id',
			dataIndex : 'fd_id',
			hidden : true
		},{
			header : 'caller',
			dataIndex : 'fd_caller',
			hidden : true
		},{
			header : 'kind',
			dataIndex : 'fd_kind',
			hidden : true
		}],
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
		fields : ['fd_detno', 'fd_name', 'fd_doccode', 'fd_parentid', 'fd_remark',
				'fd_id', 'fd_kind','fd_filepath','fd_formsid','fd_virtualpath','fd_caller','fd_operatime'],
		autoLoad : false,
		proxy : {
			type : 'ajax',
			url : basePath + 'common/FormsDoc/getFileList.action',
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
				var grid = Ext.getCmp("FormsDocTreeGrid");
				if(isSearch){
					var search = Ext.getCmp('search').getValue();
					Ext.apply(grid.getStore().proxy.extraParams, {
						caller: caller,
						formsid: formsid,
						search: search,
						id: 0,
						kind: 0
					});
				}else{
					Ext.apply(grid.getStore().proxy.extraParams, {
						caller: caller,
						formsid: formsid,
						id: grid.nodeId,
						kind: 0
					});
				}
				
			},
			datachanged:function(){
				var grid = Ext.getCmp("FormsDocTreeGrid");
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
						caller: caller,
						formsid: formsid,
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
								caller: caller,
								formsid: formsid,
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
        	layout:'column',
        	height : 26,
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
				cls : 'x-btn-filefield'
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
					
					var grid = Ext.getCmp('FormsDocTreeGrid');
					var tree = Ext.getCmp("FormsDocTree");
					var treenode = tree.getSelectionModel().getSelection()[0];
					var select = grid.selModel.lastSelected;
					var fieldId = select.data.fd_id;
					var condition = '';
					//上传附件没有保存自动保存
					if(fieldId<=0){
						if(!grid.save(grid,'hide')){ 
							field.reset();
							return;
						}else{
							condition = 'fd_parentid='+treenode.get("fd_id")+' and fd_detno='+select.data.fd_detno+" and fd_name='"+select.data.fd_name+"'";
						}
					}
					field.ownerCt.getForm().submit({
						url : basePath + 'common/FormsDoc/upload.action',
						waitMsg : "正在解析文件信息",
						params:{
							caller: caller,
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
									url:basePath + 'common/FormsDoc/getFileList.action',
									params : {
										id : treenode.get("fd_id"),
										kind : 0,
										caller: caller,
										formsid: formsid,
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
						if(item.data.fd_id!=-1){
							ids += ',' + item.data.fd_id;
						}else{
							grid.store.remove(item);
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
			if (item.data.fd_id == null || item.data.fd_id == ""
					|| typeof(item.data.fd_id) == 'undefined') {
				item.data.fd_id = -1;
			}
			if (item.data.fd_parentid == null || item.data.fd_parentid == ""
					|| typeof(item.data.fd_parentid) == 'undefined') {
				item.data.fd_parentid = grid.nodeId;
			}
			if (item.data.fd_kind == null || item.data.fd_kind == ""
					|| typeof(item.data.fd_kind) == 'undefined') {
				item.data.fd_kind = 0;
			}
			if (item.data.fd_caller == null || item.data.fd_caller.trim() == "") {
				item.data.fd_caller =caller;
			}
			if (item.data.fd_formsid == null || item.data.fd_formsid == "") {
				item.data.fd_formsid =formsid;
			}
			if (item.data.fd_doccode == null || item.data.fd_doccode == "") {
				item.data.fd_doccode ="";
			}
			if (item.data.fd_name == null || item.data.fd_name.trim() == "") {
				showError("文件名称不能为空");
				return;
			}
			

			var data = {};
			data.fd_id = item.data.fd_id;
			data.fd_kind = item.data.fd_kind;
			data.fd_parentid = item.data.fd_parentid;
			data.fd_name = item.data.fd_name.trim(); // 去掉前后空格
			data.fd_remark = item.data.fd_remark;
			data.fd_detno = item.data.fd_detno;
			data.fd_virtualpath = item.data.fd_virtualpath;
			data.fd_filepath = item.data.fd_filepath;
			data.fd_caller = item.data.fd_caller;
			data.fd_formsid = item.data.fd_formsid;
			if (item.data.fd_doccode) {
				data.fd_doccode = item.data.fd_doccode.trim(); // 去掉前后空格
			} else {
				data.fd_doccode = item.data.fd_doccode; // 去掉前后空格
			}            

			if (item.dirty && item.data.fd_id != -1) {
				update.push(data);
			} else if (item.data.fd_id == -1) {
				create.push(data);
			}
		}
		
		if (create.length > 0 || update.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'common/FormsDoc/saveAndUpdateFormsDocFileList.action',
				method : 'post',
				async:false,
				params : {
					caller: caller,
					create : Ext.encode(create),
					update : Ext.encode(update)
				},
				callback : function(options, success, response) {
					var res = Ext.JSON.decode(response.responseText);
					if (res.success) {
						if(!showTip){
							showTip = 'show';
						}
						if(showTip!='hide'){
							showMessage('提示','保存成功',1000);
							var tree = Ext.getCmp("FormsDocTree");
							var grid = Ext.getCmp("FormsDocTreeGrid");
							var treenode = tree.getSelectionModel().getSelection()[0];
							grid.store.load({
								params : {
									id : treenode.get("fd_id"),
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
			url : basePath + 'common/FormsDoc/deleteFormsDocFile.action',
			params : {
				caller: caller,
				id : nodes
			},
			callback : function(options, success, response) {
				var res = Ext.decode(response.responseText);
				if (res.success) {
					showMessage('提示','删除成功',1000);

					var tree = Ext.getCmp("FormsDocTree");
					var grid = Ext.getCmp("FormsDocTreeGrid");

					var treenode = tree.getSelectionModel()
							.getSelection()[0];
					grid.store.load({
						params : {
							id : treenode.get("fd_id"),
							kind : 0,
							caller: caller,
							formsid: formsid
						}
					});
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}else {
					Ext.Msg.alert("提示", "未知错误");
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
			url: basePath + 'common/FormsDoc/downloadbyId.action?id=' + id + '&caller=' + caller + '&_noc='+_noc,
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
		if (type.toLowerCase() == 'doc' || type.toLowerCase() == 'docx' || 
			type.toLowerCase() == 'xls' || type.toLowerCase() == 'xlsx' || 
			type.toLowerCase() == 'ppt' || type.toLowerCase() == 'pptx' ||
			type.toLowerCase() == 'pdf') {
			
			Ext.Ajax.request({
				url : basePath + 'common/FormsDoc/getHtml.action',
				params: {
					caller: caller,
					folderId:id,
					type:type,
					_noc:_noc
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
						if(type.toLowerCase() == 'doc'|| type.toLowerCase() =='docx'|| type.toLowerCase() == 'xls'|| type.toLowerCase() == 'xlsx'){
							url += 'jsps/oa/doc/readWordOrExcel.jsp?path='+basePath+path;
						}else if (type.toLowerCase() == 'pdf') {
							url += 'jsps/oa/doc/read.jsp?path='+ path + '&folderId='+ folderId;
						}
						if(type.toLowerCase() == 'ppt'|| type.toLowerCase() == 'pptx'){
							path = r.path;
							url += 'jsps/oa/doc/readPPT.jsp?pageSize='+r.pageSize+'&path='+path;
						}
						window.open(url);
					} 
				}
			});	
		}else if(me.isImage(type)){
			me.showAttach(id);
		}else {
			showMessage('提示','当前文件类型不支持在线预览，请先下载!');
		}
	},
	showAttach : function(id) {
		var me = this,src = basePath + 'common/FormsDoc/downloadbyId.action?id=' + id + '&caller=' + caller + '&_noc='+_noc;
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