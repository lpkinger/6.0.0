Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.PreVendor', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.purchase.PreVendor','core.form.Panel','core.form.FileField','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.ResSubmit','core.button.Upload','core.button.Update','core.button.Delete',
    		'core.button.ResAudit','core.button.TurnVendor','core.button.Flow','core.button.DownLoad','core.form.SplitTextField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpFormPanel':{
    			afterload :function(){
	    			var form = Ext.getCmp('form');
    				var tabid = getUrlParam("tabid");
    				var b2bdata = getUrlParam("b2bdata");
    				var condition = getUrlParam("formCondition");
    				var ve_code = Ext.getCmp('ve_code');
    				if(b2bdata && b2bdata != ""&&(ve_code&&ve_code.value == "")){
    					b2bdata = unescape(b2bdata);
    					var formData = form.getValues(),
    					 datas = Ext.decode(b2bdata);
    					formData['ve_name'] = datas['enName'];
    					formData['ve_shortname'] = datas['enShortname'];
    					formData['ve_contact'] = datas['contactMan'];
    					formData['ve_tel'] = datas['enTel'];
    					formData['ve_email'] = datas['enEmail'];
    					formData['ve_webserver'] = datas['enBussinessCode'];
    					formData['ve_add1'] = datas['enAddress'];
    					formData['ve_mobile'] = datas['contactTel'];
    					formData['ve_businessrange'] = datas['profession'];
    					formData['VE_UU'] = datas['uu'];
    					formData['ve_businessrange'] = datas['tags'];
						form.getForm().setValues(formData);
						form.getForm().getFields().each(function (item,index,length){
							item.originalValue = item.value;
						});
    				}
    				if(tabid && condition==null){
    					var tab = parent.Ext.getCmp(tabid);
        				var datas = tab.detaildatas;
    					var formData = form.getValues();
    						formData['ve_name'] = datas['en_name'];
        					formData['ve_shortname'] = datas['en_shortname'];
        					formData['ve_contact'] = datas['en_contactman'];
        					formData['ve_tel'] = datas['en_tel'];
        					formData['ve_email'] = datas['en_email'];
        					formData['ve_webserver'] = datas['en_businesscode'];
        					formData['ve_add1'] = datas['en_address'];
        					formData['ve_mobile'] = datas['en_contacttel'];
        					formData['ve_businessrange'] = datas['en_profession'];
        					formData['VE_UU'] = datas['en_uu'];
        					formData['ve_businessrange'] = datas['en_tags'];
        					formData['ve_currency'] = datas['en_currency'];
							form.getForm().setValues(formData);
							form.getForm().getFields().each(function (item,index,length){
								item.originalValue = item.value;
							});
        			}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn), kind =Ext.getCmp('ve_kind').value
    					codeField = Ext.getCmp(form.codeField);
    				if(codeField.value == null || codeField.value == ''){
						me.BaseUtil.getRandomNumber(caller);//自动添加编号
						var res = me.getCode(kind);
						if(res != null && res != ''){
							codeField.setValue(res);
						} /*else 
							me.BaseUtil.getRandomNumber('Vendor',10,null);//自动添加编号
*/    				}
    				//编号强制转成大写
					Ext.getCmp('ve_code').setValue(Ext.getCmp('ve_code').value.toUpperCase());
					//消除公司名称与简称前后空格
					var ve_name = Ext.getCmp('ve_name').value;
					var ve_shortname = Ext.getCmp('ve_shortname');
					if (ve_name) {
						ve_name = ve_name.replace(/(^\s*)|(\s*$)/g, "");
						Ext.getCmp('ve_name').setValue(ve_name);
					}
					if (ve_shortname) {
						var value = ve_shortname.value;
						if (value) {
							value = value.replace(/(^\s*)|(\s*$)/g, "");
							ve_shortname.setValue(value);
						}
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			},
    			afterrender:function(btn){
    				var panel = parent.Ext.getCmp('tree-tab');
    				if(panel && !panel.collapsed) {
						panel.toggleCollapse();
					}
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_auditstatuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				//编号强制转成大写
					Ext.getCmp('ve_code').setValue(Ext.getCmp('ve_code').value.toUpperCase());
					//消除公司名称与简称前后空格
					var ve_name = Ext.getCmp('ve_name').value;
					var ve_shortname = Ext.getCmp('ve_shortname');
					if (ve_name) {
						ve_name = ve_name.replace(/(^\s*)|(\s*$)/g, "");
						Ext.getCmp('ve_name').setValue(ve_name);
					}
					if (ve_shortname) {
						var value = ve_shortname.value;
						if (value) {
							value = value.replace(/(^\s*)|(\s*$)/g, "");
							ve_shortname.setValue(value);
						}
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPreVendor', '新增供应商引进申请', 'jsps/scm/purchase/preVendor.jsp');
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
    				var turnStatus = Ext.getCmp('ve_turnstatuscode');
    				if(status && status.value != 'AUDITED'|| turnStatus.value == 'TURNFM'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpTurnVendorButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ve_auditstatuscode');
    				var turnStatus = Ext.getCmp('ve_turnstatuscode');
    				if(status && status.value != 'AUDITED' || turnStatus.value == 'TURNFM'){
    					btn.hide();
    				}
    			},
/*    			base: function(btn){
       				warnMsg("确定要转入供应商基本资料吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/turnVendorBase.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('ve_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			console.log(localJson);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/purchase/vendor.jsp?formCondition=ve_id=" + id;
    	    		    					me.FormUtil.onAdd('Vendor' + id, '供应商' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			},*/
    			click: function(btn){
       				warnMsg("确定要转入正式供应商吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/turnVendor.action',
    	    			   		//async: false,
    	    			   		params: {
    	    			   			id: Ext.getCmp('ve_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			//console.log(localJson);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/purchase/vendor.jsp?formCondition=ve_id=" + id;
    	    		    					me.FormUtil.onAdd('Vendor' + id, '供应商' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getLeadCode: function(type) {
		var result = null;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'VendorKind',
	   			field: 'VK_EXCODE',
	   			condition: 'VK_KIND=\'' + type + '\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				result = r.data;
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