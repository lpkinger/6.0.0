Ext.define('erp.view.plm.task.ProjectImportPanel', {
	extend : 'Ext.window.Window',
	width: 400,
	//frame: true,
	modal:true,
	title: '导入Project文件',
	bodyPadding: '5 5 0',
	defaults: {
		anchor: '100%',
		allowBlank: false,
		msgTarget: 'side',
		labelWidth: 50
	},
	initComponent : function() {
		this.addEvents('dataavailable');
		var w = this;
		Ext.apply(this, {
			items:[new Ext.form.Panel({
				items: [{
					xtype: 'filefield',
					id: 'form-file',
					emptyText: '上传 .mpp 文件',
					fieldLabel: '选择文件',
					name: 'file',
					padding:'5 0 0 0',
					width:350,
					buttonText: '选择'
			/*		buttonConfig: {
						 createFileInput : function(isTemporary) {
						        var me = this;
						        console.log('use');
						        me.fileInputEl = me.el.createChild({
						            name: me.inputName,
						            id: !isTemporary ? me.id + '-fileInputEl' : undefined,
						            cls: me.inputCls,
						            tag: 'input',
						            type: 'file',
						            size: 1,
						            accept:"image/*"
						        });
						        me.fileInputEl.on('change', me.fireChange, me);  
						    }
					}*/
				}],
				buttonAlign:'center',
				buttons: [{
					text: '上传',
					handler: function(){
						var panel = this.up('form');
						var form = panel.getForm();
						if(form.isValid()){
							form.submit({
								url: basePath+'plm/gantt/ImportMpp.action?prjId=' + prjplanid,
								waitMsg: '正在加载数据...',
								failure : function(form, o) {
								    if(o.result.error) alert(o.result.error);
								    else alert('上传失败!');
								},
								success: function(form, o) {
								    window.location.reload() ;
								}
							});
						}
					}
				},
				{
					text: '重置',
					handler: function() {
						this.up('form').getForm().reset();
					}
				}]
			})]

		});

		this.callParent(arguments);
	}
});