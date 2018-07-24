Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.SendSample', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    	'core.form.Panel','scm.product.SendSample','core.button.Close','core.button.Save',
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.MultiField',
		'core.button.TurnProductApproval','core.button.SendToProdInout','core.form.FileField'
	],
	init:function(){
		var me = this;
		this.control({ 
			'textfield[name=ss_yfjscp]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'textfield[name=ss_yfremark]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'textfield[name=ss_appcode]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpTurnProductApprovalButton':{
				afterrender: function(btn){
					
				},
				click: function(btn){
					var id = Ext.getCmp("ss_id").value;
					var formStore = Ext.getCmp('form').getValues;
					warnMsg('确定要转认定单吗?', function(btn){
						if (btn == 'yes') {
							Ext.Ajax.request({
								url:basePath + "scm/product/turnProductApproval.action",
								params:{									
									id:id
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										/*Ext.Msg.alert("提示", "转认定单成功！");*/
										if(res.log){
					    					showMessage("提示", res.log);
					    				}
									}else{
										Ext.Msg.alert("提示", "转认定单失败！");
									}
								}
							});
						} else {
							return;
						}
					});
				}
			},
			'erpSendToProdInoutButton':{
				afterrender: function(btn){
					
				}
			/*	click: function(btn){
					var id = Ext.getCmp("ss_id").value;
					var form = me.getForm(btn);
					form.setLoading(true);
					warnMsg('确定要关联入库吗?', function(btn){
						if (btn == 'yes') {
							Ext.Ajax.request({
								url:basePath + "scm/product/SendToProdInout.action",
								params:{									
									formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "转认入库单成功！");
									}else{
										Ext.Msg.alert("提示", "转认入库单失败！");
									}
								}
							});
						} else {
							return;
						}
					});
				}*/
			}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});