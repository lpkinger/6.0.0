Ext.define('erp.view.ma.LoginImg',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: "window",
				title:'上传登录背景图片',
				autoShow: true,
				closable: false,
				maximizable : true,
				width: 1000,
				height: 500,
				layout: 'fit',
				items: [{
					xtype:'form',
					frame:true,	
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
						fieldLabel:'<div style="color:blue;padding-left:10px;">仅支持小于1M的图片(推荐像素:1920*532)</div>',
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
						width: 960,
						height: 266,
						id:'logo',
						margins:'50 20 50 20'
					}],
					upload: function(field){
						field.ownerCt.getForm().submit({
							url: basePath + 'ma/loginImg/save.action?em_code='+ em_code +'&caller=LoginImg',
							waitMsg:'正在上传',
							success: function(fp, o){
								if(o.result.success){
									Ext.Msg.alert('提示','上传成功');  
									var logo=Ext.getCmp('logo');
									logo.show();
									logo.setSrc(basePath+'/loginImg/getLoginImg.action?_time='+Date.parse(new Date()));
								}else Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
								
							},
							failure:function(fp,o){
								Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
							}
						});
					}
				}],
				buttonAlign:'center',
				buttons:[{
					text: $I18N.common.button.erpDeleteButton,
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray',
					width: 60,
					handler: function(btn){
						var logo=Ext.getCmp('logo');
						if(!logo.src) alert('未上传任何背景图片，无法删除!');
						else  {
							Ext.Ajax.request({
								url: basePath + 'ma/loginImg/deleteLoginImg.action',
								success: function(fp, o){
									Ext.Msg.alert('提示','删除成功!');
									logo.setSrc(null);
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
				}]
			}]
		});
		me.callParent(arguments); 
	} 
});