Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.FinancingApply', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	views : [
			 'fs.cust.FinancingApplyForm','core.trigger.MultiDbfindTrigger','fs.cust.FinancingApply','core.button.Submit', 'core.button.Cancel','core.trigger.DbfindTrigger'
			],
	init : function() {
		var me = this;
		this.control({
			'form': {
				afterrender: function(form){
					var params = new Object();
					if(formCondition){
						params.condition = formCondition.replace(/IS/g,"=");
					}
					Ext.Ajax.request({
			        	url : basePath + 'fs/cust/getFinancingApply.action',
			        	params: params,
			        	method : 'get',
			        	async: false,
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.exception || res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        			return;
			        		}else{
			        			form.getForm().setValues(res);
			        			if(res.fa_busincode){
			        				busincode = res.fa_busincode
			        				var progress = Ext.getCmp('progress');
			        				if(progress){
			        					progress.url = basePath+'jsps/fs/cust/financApplyProgress.jsp?busincode='+busincode;
			        					progress.setDisabled(false);
			        				}
			        			}
			        		}
			        	}
					});
				},
				validitychange: function(form, valid, eOpts){
					var submitButton = Ext.getCmp('submit');
					if(valid){
						var agree = form.findField('agreed').getValue();
						var agree2 = form.findField('agreed2').getValue();
						if(!formCondition&&agree&&agree2){
							submitButton.setDisabled(false);
						}
					}else{
						submitButton.setDisabled(true);
					}
				}
			},
			'multidbfindtrigger[name=fa_buyer]':{
				aftertrigger: function(trigger, datas){
					var codes = '';
					var names = '';
					Ext.Array.each(datas,function(data){
						
						if(codes.length>0){
							codes += trigger.separator;
						}
						codes += data.data.cu_code;
						
						if(names.length>0){
							names += trigger.separator;
						}
						names += data.data.cu_name
					});
					Ext.getCmp('fa_buyercode').setValue(codes);
					trigger.setValue(names);
					
				}
			},
			'#agreed': {
				change: function(field, newValue, oldValue){
					var submitButton = Ext.getCmp('submit');
					var agree2 = Ext.getCmp('agreed2').getValue();
					if(!formCondition&&newValue&&agree2){
						var form = field.ownerCt;
						if(form.getForm().isValid()){
							submitButton.setDisabled(false);
						}
					}else{
						submitButton.setDisabled(true);
					}
				}
			},
			'#agreed2': {
				change: function(field, newValue, oldValue){
					var submitButton = Ext.getCmp('submit');
					var agree = Ext.getCmp('agreed').getValue();
					if(!formCondition&&newValue&&agree){
						var form = field.ownerCt;
						if(form.getForm().isValid()){
							submitButton.setDisabled(false);
						}
					}else{
						submitButton.setDisabled(true);
					}
				}
			},
			'erpSubmitButton': {
				click : function(btn) {
					var form = btn.ownerCt.ownerCt;
					me.onSubmit(form);
				}
			},
			'erpCancelButton': {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'#progress':{
				click : function(btn){
					Ext.create('Ext.window.Window', {
						title : '保理融资申请进度',
						height : "90%",
						width : "80%",
						closeAction : 'destroy',
						maximizable : true,
						modal : true,
						buttonAlign : 'center',
						layout : 'fit',
						items : [{
								xtype:'panel',
								tag : 'iframe',
								border : false,
								layout : 'fit',
								html : '<iframe id="iframe_add_progress" src="' + btn.url +'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
						}],
						buttons : [{
							text : $I18N.common.button.erpCloseButton,
							iconCls : 'x-button-icon-close',
							cls : 'x-btn-gray',
							style : 'margin-left:20px;',
							handler : function(btn) {
								btn.ownerCt.ownerCt.close();
							}
						}]
					}).show();
				}
			}
		})
	},
	onSubmit:function(form){
		var me = this;
		var r = form.getValues();
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
			f = form.down('#' + k);
			if(f && f.logic == 'ignore') {
				delete r[k];
			}
		});
		var formStore = unescape(escape(Ext.JSON.encode(r)))
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'fs/cust/submitApply.action',
			params : {formStore:formStore,caller:caller},
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					var id = localJson.id;
					var tip = '<div style="width:270px;margin:0 auto;"><div style="line-height:25px;font-size:14px;text-align:left;"><div>恭喜贵司成功提交保理融资申请!</div><div>专属客户经理会马上与贵司联系!</div><div>您可随时关注您的融资申请进度!</div></div></div>';
					var win = Ext.getCmp('win');
					if(!win){
						win = Ext.create('Ext.window.Window', {
						    title: '提示',
						    id:'win',
						    height: 150,
						    width: 360,
						    layout: 'fit',
						    html:tip,
						    buttonAlign : 'center',
							buttons: [{
								text: '确定',
								handler: function(b) {
									b.ownerCt.ownerCt.close();
								}
							}],
							listeners: {
								close:function(){
									if(contains(window.location.href, '?', true)){
										window.location.href = window.location.href + '&formCondition=fa_idIS' + id;
									} else {
										window.location.href = window.location.href + '?formCondition=fa_idIS' + id;
									}
								}
							}
						});
					}
					win.show();
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				}
			}
		});
	}
});