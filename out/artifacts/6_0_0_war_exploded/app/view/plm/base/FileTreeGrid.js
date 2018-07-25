Ext.QuickTips.init();

Ext.define('erp.view.plm.base.FileTreeGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpFileTreeGrid',
	id : 'treegrid',
	title : '文件清单',
	bodyStyle : 'background-color:white;',
	columnLines : true,
	readOnly : false,
	nodeId : null, // 当前节点的id
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	requires : ['erp.view.core.button.UpExcel',
			'erp.view.core.button.ExportDetail',
			'erp.view.core.button.DirectImportUpExcel'],			
	plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit : 1
	})],
	viewConfig:{
	   forceFit:false
	},
	columns : [{
		header : '序号',
		dataIndex : 'detno_',
		flex : 0.5,
		editor : {
			xtype:'numberfield',
			decimalPrecision:0
		},
		minValue:1,
		hidden : false,
		renderer:function(val,meta,record){
			if(val&&val.toString().indexOf('.')>0){
				return val.substring(0,val.indexOf('.'));
			}else{
				return val;
			}
		}
	}, {
		header : '文件名称',
		dataIndex : 'name_',
		width:300,
		renderer:function(val){
			return val.replace(/\n/g,'<br>');
		},
		editor : 'textfield',
		cls : 'x-grid-header'
		
	}, {
		header : '文件编号',
		width:180,
		dataIndex : 'code_',
		editor : 'textfield',
		cls : 'x-grid-header'
	}, {
		header : 'parentid_',
		dataIndex : 'parentid_',
		hidden : true
	},{
		header:'附件',
		width:300,
		dataIndex:'attach_',
		cls : 'x-grid-header',
		renderer:function(val, meta, record, x, y, store, view){
			if(val&&!val.match(/;\d+/g)){
				return null;
			}
			if(record&&record.data["attach_"]!=null&&record.data["attach_"]!=""){		
				var attach=record.data["attach_"];
				var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
					if(record.data[field] != attach){
					record.set(field,attach);
				}
				return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="display:inline-block;width:90%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;color:green;padding-left:2px;vertical-align:middle;">' + attach.split(";")[0] + '</span>'+'<a href="' + basePath + 'common/downloadbyId.action?id='+attach.split(";")[1]+'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>';
			}else if(record&&val!=null&&val!=""){
			return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="display:inline-block;width:90%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;color:green;padding-left:2px;vertical-align:middle;">' + val.split(";")[0] + '</span>'+'<a href="' + basePath + 'common/downloadbyId.action?id='+val.split(";")[1]+'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>';
			}else return val;
		}
	},{
		header : '备注',
		width:350,
		dataIndex : 'remark_',
		editor : 'textfield',
		cls : 'x-grid-header'
	}, {
		header : '虚拟路径',
		dataIndex : 'virtualpath_',
		flex : 1,
		hidden : true
	}, {
		header : 'id_',
		dataIndex : 'id_',
		flex : 1,
		hidden : true
	}, {
		header : 'kind_',
		dataIndex : 'kind_',
		value : -1,
		flex : 1,
		hidden : true
	}],
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		ignoreRightMouseSelection : false
	}),
	store : Ext.create('Ext.data.Store', {
		storeId : 'myStore',
		pageSize : 25,
		fields : ['detno_', 'name_', 'code_', 'parentid_', 'remark_',
				'virtualpath_', 'id_', 'kind_','attach_'],
		autoLoad : false,
		proxy : {
			type : 'ajax',
			url : basePath + 'plm/base/getFileList.action',
			reader : {
				type : 'json',
				root : 'datas',
				totalProperty : 'total'
			},
			async:false
		},
		listeners : {
			beforeload : function() {
				var grid = Ext.getCmp("treegrid");
				Ext.apply(grid.getStore().proxy.extraParams, {
					productTypeCode : productTypeCode,
					id : grid.nodeId,
					kind : 0
				});
			},
			datachanged:function(){
				var grid = Ext.getCmp("treegrid");
				grid.scrollByDeltaX(1);  //为了解决grid列错位的bug
				grid.scrollByDeltaX(-1);
			}
		}
	}),
	dockedItems : [{
		xtype : 'toolbar',
		dock : 'top',
		height:36,
		layout : {
			//pack : 'left'
		},
		items : [{
			xtype : 'tbtext',
			text : '',
			id : 'toolbartext'
		}, '->',
		{
         	id:'fileform',
         	xtype:'form',
        	layout:'column',
        	height : 26,
        	bodyStyle: 'background: transparent no-repeat 0 0;border: none;height:26px;',
	  		items: [{
			xtype : 'filefield',
			name : 'file',
			buttonOnly : true,
			hideLabel : true,
			disabled : true,
			width : 90,
			id : 'file',
			buttonConfig : {
				iconCls : 'x-button-icon-pic',
				text : '上传附件',
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
					field.ownerCt.getForm().submit({
						url : basePath + 'common/upload.action?em_code=' + em_code,
						waitMsg : "正在解析文件信息",
						success : function(fp, o) {
							if (o.result.error) {
								showError(o.result.error);
							} else {
								Ext.Msg.alert("恭喜", filename + " 上传成功!");
								field.setDisabled(true);
								var record = Ext.getCmp('treegrid').selModel.lastSelected;
								if (record) {
									record.set('attach_',filename+";"+o.result.filepath);
									record.dirty = true;
									//如果是新建的文件，自动改名字
									if(record.data.id_==-1){
										record.set('name_',filename);
									}
								}
							}
						}
					});
				}
			}
	  		}]					
		},	
		{
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
		},
		{
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
						if(item.data.id_!=-1&&item.data.id_){
							ids += ',' + item.data.id_;
						}else{
							grid.store.remove(item);
						}
					});
					if(''!=ids){
						grid.deleteNode(ids.substring(1),'file');
					}
				}
			}
		},{
			xtype : 'button',
			text : '下载模板',
			id : 'exportFileButton',
			width : 90,
			iconCls : 'x-button-icon-download',
			cls : 'x-btn-gray',
			handler : function(btn) {
				var grid = Ext.getCmp("treegrid");
				var store = grid.getView().getStore();
				// 先清空数据
				store.loadData([{}]);
				grid.BaseUtil.exportGrid(grid, "文件清单");
				// 重新加载
				grid.store.load();
			}
		}, {
			xtype : 'upexcel',
			text : '导入数据',
			id : 'importFileButton',
			width : 85,
			height : 26,
			iconCls : 'x-button-icon-up',
			cls : 'x-btn-gray',
			listeners:{
				afterrender:function(form){
					var node;
					form.items.items[0].button.handler = function(btn){		
						node = Ext.getCmp('indexTree').getSelectionModel().getSelection()[0];
						if(!node){
							Ext.Msg.alert('提示','请先选择目录!',function(){
								Ext.getCmp('importFileButton').setDisabled(false);
							});
							Ext.getCmp('importFileButton').setDisabled(true);
						}
					};	
					
					var treegrid = Ext.getCmp('treegrid');
					var fileField = treegrid.query('filefield')[1];
					fileField.on('change',function(self){
						Ext.defer(function(){
							var win = Ext.getCmp('excelwin');
							var confirmBtn = Ext.getCmp('confirmimport');
							if(confirmBtn){
								confirmBtn.handler = function(){
									var tgrid = Ext.getCmp('excelgrid');
									var radioValue = win.down('radiogroup').getValue();
									if(radioValue.import_mode=='-'){ //替换模式
										if(treegrid.store.data.items.length>0){
											Ext.Msg.confirm('确认','该目录下已存在文件，需先清空，是否清空?',function(btn){
												if(btn=='yes'){
													//替换模式先删除原有数据
													Ext.Ajax.request({
														url : basePath + 'plm/base/deleteNode.action',
														async:false,
														params : {
															id : node.data.id_,
															type : 'allfile',
															productTypeCode:productTypeCode
														},
														callback : function(options, success, response) {
															var res = Ext.decode(response.responseText);
															if(res.success){
																treegrid.exportToGrid(fileField,tgrid,win);
															}
															if (res.exceptionInfo){
																showError(res.exceptionInfo);
															}
														}
													});																																							
												}else{
													return false;
												}
											});											
										}else{
											treegrid.exportToGrid(fileField,tgrid,win);
										}
									}else{
										fileField.ownerCt.exportGridToGrid(tgrid, treegrid, function(btn){
											win.close();
										});											
									}
									
								};
							}
						},600);						
					});
				}
			}
		}],
		border : false
	}, {
		xtype : 'pagingtoolbar',
		dock : 'bottom',
		displayInfo : true,
		store : Ext.data.StoreManager.lookup('myStore'),
		displayMsg:"显示{0}-{1}条数据，共{2}条数据",
		beforePageText: '第',
		afterPageText: '页,共{0}页'
	}],
	exportToGrid:function(fileField,tgrid,win){
		fileField.ownerCt.exportGridToGrid(tgrid, this, function(btn){
			win.close();
		});
		
		Ext.Array.each(this.store.data.items,function(item,index){
			item.data.id_ = "";
		});		
	},
	save : function(grid) {
		var me = this;
		var create = [];
		var update = [];
		
		var datas = grid.store.data.items;
		var max = me.getMaxDetno(datas);
		var count = 1;
		for (var i = 0; i < datas.length; i++) {
			var item = datas[i];
			//空的序号赋值
			if(item.data.detno_ == null || item.data.detno_ == ""
					|| typeof(item.data.detno_) == 'undefined'){
				item.data.detno_ = max+count;
				count++;
			}
			if (item.data.id_ == null || item.data.id_ == ""
					|| typeof(item.data.id_) == 'undefined') {
				item.data.id_ = -1;
			}
			if (item.data.parentid_ == null || item.data.parentid_ == ""
					|| typeof(item.data.parentid_) == 'undefined') {
				item.data.parentid_ = grid.nodeId;
			}
			if (item.data.prjtypecode_ == null || item.data.prjtypecode_ == ""
					|| typeof(item.data.prjtypecode_) == 'undefined') {
				item.data.prjtypecode_ = productTypeCode;
			}
			if (item.data.kind_ == null || item.data.kind_ == ""
					|| typeof(item.data.kind_) == 'undefined') {
				item.data.kind_ = 0;
			}
			if (item.data.name_ == null || item.data.name_.trim() == "") {
				showError("文件名称不能为空");
				return;
			}
			if(item.data.attach_&&!item.data.attach_.match(/;\d+/g)){
				item.data.attach_=null;
			}
			var data = {};
			data.id_ = item.data.id_;
			data.kind_ = item.data.kind_;
			data.parentid_ = item.data.parentid_;
			data.name_ = item.data.name_.trim(); // 去掉前后空格
			data.remark_ = item.data.remark_;
			data.virtualpath_ = item.data.virtualpath_;
			data.detno_ = parseInt(item.data.detno_);
			data.attach_ = item.data.attach_;
			if (item.data.code_) {
				data.code_ = item.data.code_.trim(); // 去掉前后空格
			} else {
				data.code_ = item.data.code_; // 去掉前后空格
			}
			data.prjtypecode_ = item.data.prjtypecode_;

			if (item.dirty && item.data.id_ != -1) {
				update.push(data);
			} else if (item.data.id_ == -1) {
				create.push(data);
			}
		}
		update = me.orderByDetno(update);
		create = me.orderByDetno(create);
		
		if (create.length > 0 || update.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'plm/base/saveAndUpdateTree.action',
				method : 'post',
				params : {
					create : Ext.encode(create),
					update : Ext.encode(update)
				},
				callback : function(options, success, response) {
					me.FormUtil.setLoading(false);
					var res = Ext.JSON.decode(response.responseText);
					if (res.success) {
						showMessage('提示','保存成功',1000);

						var tree = Ext.getCmp("indexTree");
						var grid = Ext.getCmp("treegrid");
						var treenode = tree.getSelectionModel().getSelection()[0];
						grid.store.load({
							params : {
								id : treenode.get("id_"),
								kind : 0
							}
						});

					} else if (res.exceptionInfo) {
						showError(res.exceptionInfo);
					}
				}
			});
		} else {
			Ext.Msg.alert("提示", "未修改或新增数据");
		}

	},
	deleteNode : function(nodes, type) {
		warnMsg('确定删除？',function(btn){
			if(btn=='yes'){
				Ext.Ajax.request({
					url : basePath + 'plm/base/deleteNode.action',
					params : {
						id : nodes,
						type : type,
						productTypeCode:productTypeCode
					},
					callback : function(options, success, response) {
						var res = Ext.decode(response.responseText);
						if (res.success) {
							showMessage('提示','删除成功',1000);
		
							var tree = Ext.getCmp("indexTree");
							var grid = Ext.getCmp("treegrid");
		
							var treenode = tree.getSelectionModel()
									.getSelection()[0];
							grid.store.load({
								params : {
									id : treenode.get("id_"),
									kind : 0
								}
							});
						} else if (res.exceptionInfo){
							showError(res.exceptionInfo);
						}
					}
				});
			}
		});
	},
	getMaxDetno:function(data){
		var max = 0;
		Ext.Array.each(data,function(d){
			if(d.data['detno_']&& parseInt(d.data['detno_'])>max){
				max =  parseInt(d.data['detno_']);
			}
		})
		return max;
	},
	orderByDetno:function(Array){
		for (var i = 0; i < Array.length-1; i++) {
			if(Array[i]['detno_']){
				var min = i;
				for (var j = i+1; j < Array.length; j++) {
					if(Array[j]['detno_']&&Array[i]['detno_']==Array[j]['detno_']){
						showError('同一父目录下文件夹序号重复，不允许重复！');
						return ;
					}else if(Array[j]['detno_']&&Array[min]['detno_']>Array[j]['detno_']){
						min = j;
					}
				}
				if(min!=i){
					var swp = Array[i];
					Array[i] = Array[min];
					Array[min] = swp;
				}
			}
		}
		return Array;
	}
});
