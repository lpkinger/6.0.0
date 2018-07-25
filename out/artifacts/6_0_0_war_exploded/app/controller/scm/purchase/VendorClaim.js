Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.VendorClaim', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','scm.purchase.VendorClaim','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.FormBook',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Scan','core.form.MultiField','core.button.Confirm','core.button.Sync','core.button.TurnVCtoAPBill',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.form.CheckBoxGroup',
      		'core.form.MonthDateField','core.form.SpecialContainField','core.form.SeparNumber','core.form.ConDateHourMinuteField'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			afterrender: function(btn){
    				var form = me.getForm(btn);
    				var codeField = Ext.getCmp(form.codeField);  				
    				if(Ext.getCmp(form.codeField) && (Ext.getCmp(form.codeField).value != null && Ext.getCmp(form.codeField).value != '')){
    						btn.hide();
    					}
    			},
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
    						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('vc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    		   afterrender: function(btn){
 				   var statu = Ext.getCmp('vc_statuscode');
 				   if(statu && statu.value != 'ENTERING'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(){
 				   me.FormUtil.onAdd('addVendorClaim', '新增供应商索赔单', 'jsps/scm/purchase/vendorClaim.jsp');
 			   }
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('vc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('vc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.onAudit(Ext.getCmp('vc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('vc_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var condition="";
    				var reportName="";
    				var id = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpConfirmButton': {
    			click: function(btn){
    				me.FormUtil.onConfirm(Ext.getCmp('vc_id').value);
    			}
    		},
    		'dbfindtrigger[name=vcd_purchaserowcode]': {
                focus: function(t) {
                    t.setHideTrigger(false);
                    t.setReadOnly(false); //用disable()可以，但enable()无效
                    var record = Ext.getCmp('grid').selModel.lastSelected;
                    var code = record.data['vcd_purchasecode'];
                    if (code == null || code == '') {
                        showError("请先选择关联单号!");
                        t.setHideTrigger(true);
                        t.setReadOnly(true);
                    } else {
                        var field = 'pd_code';
                        t.dbBaseCondition = field + "='" + code + "'";
                    }
                }
            },
            'dbfindtrigger[name=vcd_purchasecode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var vendorcode = Ext.getCmp('vc_vendorcode').value;
    				if(vendorcode) {
    					t.dbBaseCondition = "pu_vendcode='"  + vendorcode + "'";
    				}
    			}
    		},
    		'field[name=vc_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=vc_recorddate]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
            'erpTurnVCtoAPBill': {
            	afterrender: function(btn){
            		btn.hide();
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				var vecheck = Ext.getCmp('vc_vendorverify');
    				var vereply = Ext.getCmp('vc_vendorreply');
    				console.log(status.value);
    				console.log(vecheck.value);
    				console.log(vereply.value);
    				if(status && status.value == 'AUDITED' && vecheck && vecheck.value == '是' && vereply && vereply.value =='同意'){
    					btn.show();
    				}
    			},
    			click: function(btn){
    				warnMsg('是否将要转单到其它应付单?', function(btn){
    					if(btn == 'yes'){ 
    						Ext.Ajax.request({
                                url : basePath + Ext.getCmp('form').turnAPBillUrl,
                                params:{
                                    id: Ext.getCmp('vc_id').value,
                                    caller:caller
                                },
                                method:'post',
                                callback:function(options,success,response){
                                	var res = new Ext.decode(response.responseText);
                                    if (res.exceptionInfo) {
                                        showError(rs.exceptionInfo);
                                    } else {
                                        if (res.log)
                                            showMessage('提示', res.log);
                                    }
                                }
                            });
    					}else{
    						return;
    					}
    				});		
    			}
    		}
    	});
    }, 
    onAudit: function(id){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			if(!me.contains(form.auditUrl, '?caller=', true)){
				form.auditUrl = form.auditUrl + "?caller=" + caller;
			}
			me.setLoading(true);//loading...
			//清除流程
			Ext.Ajax.request({
				url : basePath + me.deleteProcess,
				params: {
					keyValue:id,
					caller:caller,
					_noc:1
				},
				method:'post',
				async:false,
				callback : function(options,success,response){
	
				}
			});
			Ext.Ajax.request({
				url : basePath + form.auditUrl,
				params: {
					id: id
				},
				method : 'post',
				callback : function(options,success,response){
					me.setLoading(false);
					var localJson = new Ext.decode(response.responseText);
					if (localJson.exceptionInfo) {
                        showError(rs.exceptionInfo);
                    } else {
                        if (localJson.log)
                            showMessage('提示', localJson.log);
                    }
					if(localJson.success){
						//audit成功后刷新页面进入可编辑的页面 
						console.log(localJson.log);
						if(!localJson.log){
							showMessage('提示', '审核成功!', 1000);
						}						
						window.location.reload();
					} else {
						if(localJson.exceptionInfo){
							var str = localJson.exceptionInfo;
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								showMessage("提示", str);
								auditSuccess(function(){
									window.location.reload();
								});
							} else {
								showError(str);return;
							}
						}
					}
				}
			});
		} else {
			me.checkForm();
		}
	},
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	checkForm: function(){
		var s = '';
		var form = Ext.getCmp('form');
		form.getForm().getFields().each(function (item, index, length){
			if(!item.isValid()){
				if(s != ''){
					s += ',';
				}
				if(item.fieldLabel || item.ownerCt.fieldLabel){
					s += item.fieldLabel || item.ownerCt.fieldLabel;
				}
			}
		});
		if(s == ''){
			return true;
		}
		showError($I18N.common.form.necessaryInfo1 + '(<font color=green>' + s.replace(/&nbsp;/g,'') + 
				'</font>)' + $I18N.common.form.necessaryInfo2);
		return false;
	},
	contains: function(string, substr, isIgnoreCase){
		if (string == null || substr == null) return false;
		if (isIgnoreCase === undefined || isIgnoreCase === true) {
			string = string.toLowerCase();
			substr = substr.toLowerCase();
		}
		return string.indexOf(substr) > -1;
	},
	setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	},
});