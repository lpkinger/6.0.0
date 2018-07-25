/**
 * 明细行附件上传
 */
Ext.define('erp.view.core.grid.detailAttach', {
	extend : 'Ext.form.Panel',
	alias : 'widget.erpdetailAttach',
	id : 'fileform',
	layout : 'column',
	height:26,
	iconCls : 'x-button-icon-pic',
	filedAttach: '1detailAttach',
	bodyStyle : 'background: transparent no-repeat 0 0;border: none;',
	initComponent : function(){ 
		Ext.apply(this,{
			items : [{
				xtype : 'filefield',
				name : 'file',
				buttonOnly : true,
				hideLabel : true,
				disabled : false,
				width : 120,
				height : 17,
				id : this.fileId?this.fileId:'file',
				buttonConfig : {
					iconCls : 'x-button-icon-pic',
					text : this.filetext?this.filetext:'上传附件',
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
										record.set(this.form.filedAttach?this.form.filedAttach:'1detailAttach',filename+";"+o.result.filepath);
									}
								}
							}
						});
					}
				}
			}]
		});
	this.callParent(arguments); 	
	},
	
	
});