Ext.define('erp.view.ma.LogoSet',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				title:'上传Logo',
				width: 605,
				height: 300,
				xtype:'form',
				frame:true,	
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				layout:'vbox',
				fieldDefaults : {
					margin : '2 2 2 2',
					fieldStyle : "background:#FFFAFA;color:#515151;",
					labelAlign : "right",
					blankText : $I18N.common.form.blankText
				},
				items:[{
					xtype: 'filefield',
					columnWidth:1,
					name: 'file',
					labelWidth:300,
                    labelSeparator:'',
					fieldLabel:'<div style="color:blue;padding-left:10px;">Logo目前只支持小于8K图片(像素:85*30)</div>',
					allowBlank: false,
					buttonOnly:true,
					buttonConfig:{
						text:'选择图片',
						iconCls:'x-button-icon-pic'
					},
					listeners: {
						change: function(field){
							field.ownerCt.upload(field);
						}
					}
				},{
					xtype: 'image',
					width: 85,
					height: 30,
					id:'logo',
					margins:'30 255 50 255'
				}],
				buttonAlign:'center',
				buttons:[{
					text: $I18N.common.button.erpDeleteButton,
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray',
					width: 60,
					handler: function(btn){
						var logo=Ext.getCmp('logo');
						if(!logo.src) alert('未上传任何logo，无法删除!');
						else  {
							Ext.Ajax.request({
								url: basePath + 'ma/logo/del.action',
								success: function(fp, o){
									Ext.Msg.alert('提示','删除成功!');
									logo.hide();									    		
								}
							})
						}
					}
				},{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					width: 60,
					handler: function(btn){
						var main = parent.Ext.getCmp("content-panel"); 
						if(main){
							main.getActiveTab().close();
						} 
					}
				}],
				upload: function(field){
					if(field.ownerCt.down('image').hidden){
						field.ownerCt.getForm().submit({
							url: basePath + 'ma/logo/save.action',
							waitMsg:'正在上传',
							success: function(fp, o){
								if(o.result.success){
									Ext.Msg.alert('提示','更新成功');  
									var logo=Ext.getCmp('logo');
									logo.show();
									logo.setSrc(basePath+'ma/logo/get.action');
								}else Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
								
							},
							failure:function(fp,o){
								Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
							}
						 
						});
					}else  Ext.Msg.alert('提示','已存在Logo,请先删除!');
					
				}
			}]
		});
		me.callParent(arguments); 
	} 
});