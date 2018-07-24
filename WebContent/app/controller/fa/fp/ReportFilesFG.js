Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.ReportFilesFG', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'fa.fp.ReportFilesFG', 'core.form.Panel','core.grid.Panel2', 'core.toolbar.Toolbar', 'core.grid.YnColumn',
			 'core.button.Add','core.button.Sync','core.button.Save', 'core.button.Close','core.button.Upload', 'core.button.Update',
			'core.button.Delete' ,'core.grid.detailAttach','core.trigger.DbfindTrigger','core.form.YnField', 'core.button.DeleteDetail',
			'core.button.Upload', 'core.form.FileField','core.trigger.MultiDbfindTrigger' ],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},
			'erpSaveButton' : {
				click : function(btn) {
					me.GridUtil.onSave(Ext.getCmp('grid'));
				}
			},
			'field[name=fo_id]' : {
				change : function(f) {
					if (f.value != null && f.value != '') {
						window.location.href = window.location.href
								.toString().split('?')[0]
								+ '?formCondition=fo_id='
								+ f.value
								+ '&gridCondition=foid=' + f.value;
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();
					}
				}

			},
			'field[name=fo_caller]' : {
				beforerender:function(f){
					f.readOnly=true;
    				f.editable=false;
				}
  		  	},
  		  	'field[name=caller]' : {
				beforerender:function(f){
					f.autoDbfind=false;
				}
  		  	},
			'erpAddButton' : {
				click : function(btn) {
					me.FormUtil.onAdd('addReportFilesFG', '新增报表文件设置',
							'jsps/fa/fp/ReportFilesFG.jsp');
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('fo_id').value);
				}
			},
			'erpDeleteDetailButton': {
				afterrender: function(btn){
					if(en_admin=='pdf'){
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
								text : '上传报表'
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
										url : basePath + 'common/uploadRP.action?em_code=' + em_code,
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
			   }
		});
	},
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});