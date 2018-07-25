Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.ReportFilesG', {
	extend : 'Ext.app.Controller',
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['fa.fp.ReportFilesG.Viewport','fa.fp.ReportFilesG.ReportFilesGGrid','core.toolbar.Toolbar', 'core.button.Save', 'core.button.Add',
			'core.button.Close', 'core.button.Update','core.button.DeleteDetail', 'core.button.Delete','core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
			'core.form.YnField','core.grid.detailAttach'],
	init : function() {
		var me = this;
		this.control({
			'erpReportFilesGGrid' : {
				select : function(selModel, record, index) {
					var grid = Ext.getCmp('grid');
					if (index == grid.store.data.items.length - 1) {// 如果选择了最后一行
						me.add10EmptyData(grid);
					}
					me.itemclick(grid);
				}
			},
			'erpSaveButton' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var params = grid.getGridStore();
					if (params.length == 0) {
						showError("没有需要保存的数据！");
						return;
					}
					Ext.Ajax.request({
								url : basePath
										+ 'fa/fp/saveReportFilesG.action',
								params : {
									param : params,
									caller : caller
								},
								method : 'post',
								callback : function(options, success, response) {
									// me.getActiveTab().setLoading(false);
									var res = new Ext.decode(response.responseText);
									if (res && res.exceptionInfo) {
										showError(res.exceptionInfo);
										return;
									} else {
										gridCondition = (gridCondition == null)	? "":gridCondition.replace(/IS/g,"=");
										grid.GridUtil.loadNewStore(grid, {caller : caller,condition : gridCondition});
									}
								}
							});
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var s = grid.getStore().data.items;									
					var bool = false;
					Ext.each(s,function (item,index){
						if(this.data['id'] != ''&& this.data['id'] != '0' && this.data['id'] != 0){
							bool =  true;
						}
					});
					if(!bool){
						showError("没有需要删除的数据！");
						return;
					}
					warnMsg($I18N.common.msg.ask_del_main, function(btn){
					     if(btn == 'yes'){							
							Ext.Ajax.request({
								url : basePath+ 'fa/fp/deleteReportFilesG.action',
								params : {
									id : id,
									caller : caller
								},
								method : 'post',
								callback : function(options, success, response) {
								// me.getActiveTab().setLoading(false);
								var res = new Ext.decode(response.responseText);
									if (res && res.exceptionInfo) {
										showError(res.exceptionInfo);return;
									} else {
										delSuccess(function(){
											this.onClose();							
										});//
										gridCondition = (gridCondition == null)? "": gridCondition.replace(/IS/g,"=");
										grid.getGridColumnsAndStore({caller :caller,condition : gridCondition});}
									   }
								});
					         }
					});
				}
			},
			'erpDeleteDetailButton': {
				afterrender: function(btn){
					btn.ownerCt.add({
		                 id:'fileform',
		                 xtype:'form',
		    	        layout:'column',
		    	        bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
					  items: [{
							xtype : 'filefield',
							name : 'file',
							buttonOnly : true,
							hideLabel : true,
							disabled : false,
							width : 90,
							height : 17,
							id : 'file',
							buttonConfig : {
								iconCls : 'x-button-icon-pic',
								text : '上传附件'
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
												var record = Ext.getCmp('grid').selModel.lastSelected;
												if (record) {
													record.set('1detailAttach',filename+";"+o.result.filepath);
												}
											}
										}
									});
								}
							}
						}]
		                 });
				}
			   }

		});
	},
	add10EmptyData : function(grid) {
		var data = new Array();
		var items = grid.store.data.items;
		for (var i = 0; i < 10; i++) {
			var o = new Object();
			o.caller = null;
			o.id = null;
			o.condition = null;
			o.title = null;
			o.file_name = null;
			o.file_path = null;
			data.push(o);
		}
		grid.store.insert(items.length, data);
	},
	itemclick : function(grid) {
		var btn = grid.down('erpDeleteDetailButton');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('erpAddDetailButton');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('copydetail');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('pastedetail');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('updetail');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('downdetail');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('erpdetailAttach');
		if (btn)
			btn.setDisabled(false);
	}
});