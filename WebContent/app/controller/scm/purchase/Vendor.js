Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.Vendor', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.purchase.Vendor','core.form.Panel','core.form.MultiField','core.form.FileField','core.button.ExternalLink','core.button.Modify',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DownLoad','core.button.VendorLevel',
    			'core.button.ResSubmit','core.button.Banned','core.button.ResBanned','core.button.Flow','core.button.VendorUU',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField', 'core.button.Sync','core.button.RegB2B','core.button.RelativeVendor','core.trigger.BankNameTrigger'
    	],
    init:function(){
    	var me = this;
		this.control({
			'erpRelativeVendorButton' : {
				beforerender : function(btn){
					var flag = getUrlParam('flag');
					var uu = getUrlParam('uu');
					if(!flag){
						btn.hide();
					}
					if(uu){
						Ext.getCmp('ve_uu').setValue(uu);
					}
				}
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn), kind =Ext.getCmp('ve_kind').value,
						codeField = Ext.getCmp(form.codeField);
					if(codeField.value == null || codeField.value == ''){
						//var res = me.getVendorCode(kind);
						var res = me.getCode(kind);
						if(res != null && res != ''){
							codeField.setValue(res);
						} else 
							me.BaseUtil.getRandomNumber('Vendor',10,null);//自动添加编号
						}
					//编号强制转为大写
					Ext.getCmp('ve_code').setValue(Ext.getCmp('ve_code').value.toUpperCase());
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ve_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					//编号强制转成大写
					Ext.getCmp('ve_code').setValue(Ext.getCmp('ve_code').value.toUpperCase());
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					var config=_config ? '?_config=CLOUD':'';
					me.FormUtil.onAdd('addVendor', '新增供应商基本资料', 'jsps/scm/purchase/vendor.jsp'+config);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ve_auditstatuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ve_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ve_auditstatuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ve_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ve_auditstatuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ve_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ve_auditstatuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ve_id').value);
				}
			},
			'erpExternalLinkButton': {
				afterrender: function(btn){
					btn.hide();
					var status = Ext.getCmp('ve_auditstatuscode'),
						enuu = Ext.getCmp('ve_uu');
					if(status && status.value == 'AUDITED' && enuu && enuu.value.trim() != ''){
						btn.show();
					}
				},
				click: function(btn){
					var erpLink = Ext.getCmp('ve_erplink'),
						erpMaster = Ext.getCmp('ve_targetmaster'),
						erpAccesskey = Ext.getCmp('ve_accesskey');
				    if(erpAccesskey&&erpAccesskey.value.trim()==''){
				    	showError('请填写接口key字段');
				    	return;
				    }
					if(erpLink && erpLink.value != ''){
						if(erpMaster && erpMaster.value != ''){
							var linkValue = erpLink.value,
								masterValue = erpMaster.value,
								ve_id = Ext.getCmp('ve_id').value;
							Ext.Ajax.request({
								url: basePath + 'common/visitERP/getNameAndPwd.action',
								params: {
									vendorId: ve_id
								},
								callback: function(options, success, response){
									var data = Ext.decode(response.responseText);
									var form = document.createElement('form');
									form.style.display = 'none';
									form.action = linkValue + '/ERP/common/VisitERP/customer.action';
									form.method = 'post';
									form.target="_bland";
									var html = '<input type="hidden" name="username" value="'+(data.data?data.data.CC_USERNAME:"")+'" />';
									html += '<input type="hidden" name="password" value="'+(data.data?data.data.CC_PASSWORD:"")+'" />'
										 + '<input type="hidden" name="master" value="'+masterValue+'" />'
										 + '<input type="hidden" name="cu_uu" value="'+data.ve_uu+'" />'
										 + '<input type="hidden" name="success" value="'+data.success+'" />'
										 + '<input type="hidden" name="accesskey" value="'+erpAccesskey.value+'" />';
									form.innerHTML = html;
									document.body.appendChild(form);
									form.submit();
								}
								});
							}
						}
					}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('ve_id').value);
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ve_auditstatuscode');
					if(status && status.value == 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('ve_auditstatuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('ve_id').value);
    			}
    		},
            'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#ve_auditstatuscode');
                    if (s.getValue() != 'AUDITED')
                        btn.hide();
                }
            },
            'erpRegB2BButton':{
    			afterrender:function(btn){//状态为审核时显示
    			   var status=Ext.getCmp('ve_auditstatuscode');
    			   var uu=Ext.getCmp('ve_uu');
    			   if((status && status.value !='AUDITED')||(uu&&uu.value!='')){
    				   btn.hide();
    			   }
    		   	},	
    			click:function(btn){
    				var form=me.getForm(btn);
    				var id=Ext.getCmp('ve_id').value;
    				form.setLoading(true);
    				Ext.Ajax.request({
    		        	url : basePath +'scm/vendor/regB2BVendor.action',
    		        	params: {id:id},
    		        	method : 'post',
    		        	callback : function(options, success, response){
    		        		form.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo != null){
    		        			showError(res.exceptionInfo);return;
    		        		}else {
    		        			showMessage('提示', '注册成功!');
	    						window.location.reload();
    		        		}    		        
    		        	}
    		        });
    			}
    		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getVendorCode: function(type) {
		var result = null;
		Ext.Ajax.request({
	   		url : basePath + 'scm/purchase/getVendorCode.action',
	   		async: false,
	   		params: {
	   			kind:type
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				result = r.code;
	   			}
	   		}
		});
		return result;
	},
	getCode: function(type) {
		var result = null;
		Ext.Ajax.request({
	   		url : basePath + 'scm/vendor/getVendorCodeNum.action',
	   		async: false,
	   		params: {
	   			ve_kind: type
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				result = r.number;
	   			}
	   		}
		});
		return result;
	}
});