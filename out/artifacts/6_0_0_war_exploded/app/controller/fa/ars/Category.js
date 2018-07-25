Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.Category', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.Category','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update',
      		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
      		'core.button.Banned','core.button.ResBanned','core.trigger.CateTreeDbfindTrigger',
      		'core.button.DeleteDetail', 'core.trigger.MultiDbfindTrigger','core.trigger.AddDbfindTrigger',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var code = Ext.getCmp('ca_code').value,
    					level = Ext.getCmp('ca_level').value,
    					currency = Ext.getCmp('ca_currency').value,
    					currencytype = Ext.getCmp('ca_currencytype').value,
    					iscashbank = Ext.getCmp('ca_iscashbank').value,
    					cashflow = Ext.getCmp('ca_cashflow').value,
    					err = null;
    				if(level == 1) {
    					if(code.length != 3 && code.length != 4) {
    						err = "一级科目的编号一般为3~4位,当前科目号:" + code + ".是否仍然保存?";
    					}
    				} else {
    					if(code.length <= 4) {
    						err = "下级科目的编号一般大于4位,当前科目号:" + code + ".是否仍然保存?";
    					}
    				}
    				if(err != null) {
    					warnMsg(err, function(btn){
    						if(btn == 'yes') {
    							me.checkCash(true);
    						}
    					});
    				} else {
    					me.checkCash(true);
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var code = Ext.getCmp('ca_code').value,
						level = Ext.getCmp('ca_level').value,
						err = null;
					if(level == 1) {
						if(code.length != 3 && code.length != 4) {
							err = "一级科目的编号一般为3~4位,当前科目号:" + code + ".是否仍然保存?";
						}
					} else {
						if(code.length <= 4) {
							err = "下级科目的编号一般大于4位,当前科目号:" + code + ".是否仍然保存?";
						}
					}
					if(err != null) {
						warnMsg(err, function(btn){
							if(btn == 'yes') {
								me.checkCash(false);
							}
						});
					} else {
						me.checkCash(false);
					}
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addCategory', '新增银行账户资料', 'jsps/fa/ars/category.jsp?whoami=' + caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'field[name=ca_assname]': {
    			change: function(f) {
    				if(Ext.isEmpty(f.value)) {
    					var t = f.ownerCt.down('#ca_asstype');
    					t && t.setValue(null);
    				}    			
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onBanned(Ext.getCmp('ca_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ca_statuscode');
    				if(status && status.value != 'DISABLE'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResBanned(Ext.getCmp('ca_id').value);
    			}
    		},
    		/*'checkbox[name=ca_currencytype]': {
    			change: function(f){
    				if(f.checked){
    					Ext.getCmp('ca_currency').show();
    				} else {
    					Ext.getCmp('ca_currency').hide();
    				}
    			}
    		},
    		'dbfindtrigger[name=ca_currency]': {
    			afterrender: function(f){
    				if(Ext.getCmp('ca_currencytype')){
    					if(!Ext.getCmp('ca_currencytype').checked){
    						f.hide();
    					}
    				}
    			}
    		},*/
    		'combobox[name=ca_type]': {
    			change: function(f){
    				var n = Ext.getCmp('ca_typename');
    				if(n){
    					switch(f.value){
		    				case 0:
		    					n.setValue('借');
		    					break;
		    				case 1:
		    					n.setValue('贷');
		    					break;
		    				case 2:
		    					n.setValue('借或贷');
		    					break;
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=ca_pcode]': {
    			change: function(f) {
    				if(Ext.isEmpty(f.value)) {
    					var n = Ext.getCmp('ca_level');
        				if(n){
        					n.setValue(1);
        				}
    				}
    			},
    			aftertrigger: function(f, r){
    				var n = Ext.getCmp('ca_level');
    				if(n){
    					n.setValue(n.value + 1);
    				}
    				var sCode = f.value, cf = Ext.getCmp('ca_code');
    				if(!Ext.isEmpty(cf.getValue())) {
    					return;
    				}
					Ext.Ajax.request({
				   		url : basePath + 'common/getFieldData.action',
				   		async: false,
				   		params: {
				   			caller: 'Category',
				   			field: 'count(*)',
				   			condition: 'ca_subof=(SELECT ca_id FROM category WHERE ca_code=\'' + sCode + '\')'
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showError(localJson.exceptionInfo);return null;
				   			}
				   			var str = '001';
			    			if(localJson.success && localJson.data != null){
			    				var count = localJson.data + 1;
			    				str = '' + count;
			    				if(count < 10) {
			    					str = '00' + count;
			    				} else if (count < 100) {
			    					str = '0' + count;
			    				}
				   			}
			    			cf.setValue(sCode + str);
			    		}
					});
    			}
    		},
    		'field[name=ca_iscash]': {
    			change: function(f) {
    				var s = Ext.getCmp('ca_isbank');
    				if(f.value == '-1') {
    					Ext.getCmp('ca_iscashbank').setValue('-1');
    					if(s.value == '-1')
    						Ext.getCmp('ca_isbank').setValue('0');
    				} else if(s.value == '0'){
    					Ext.getCmp('ca_iscashbank').setValue('0');
    				}
    			}
    		},
    		'field[name=ca_isbank]': {
    			change: function(f) {
    				var s = Ext.getCmp('ca_iscash');
    				if(f.value == '-1') {
    					Ext.getCmp('ca_iscashbank').setValue('-1');
    					if(s.value == '-1')	
    						Ext.getCmp('ca_iscash').setValue('0');
    				} else if(s.value == '0'){
    					Ext.getCmp('ca_iscashbank').setValue('0');
    				}
    			}
    		},
    		'field[name=ca_iscashbank]': {
    			change: function(f) {
    				if(f.value == '0') {
    					Ext.getCmp('ca_iscash').setValue('0');
    					Ext.getCmp('ca_isbank').setValue('0');
    				}
    			}
    		},
    		'cateTreeDbfindTrigger[name=ca_pcode]': {
    			change: function(f) {
    				if(Ext.isEmpty(f.value)) {
    					var n = Ext.getCmp('ca_level');
        				if(n){
        					n.setValue(1);
        				}
    				}
    			},
    			aftertrigger: function(tri, data){
    				var depth = data[0].data.depth;
    				var n = Ext.getCmp('ca_level');
    				if(depth != null){
        				if(n){
        					n.setValue(Number(depth) + 1);
        				}
    				} else {
    					n.setValue(1);
    					
    				} 
    			}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	checkCash: function(isSave){
		var me = this;
		var currency = Ext.getCmp('ca_currency').value,
			currencytype = Ext.getCmp('ca_currencytype').value,
			iscashbank = Ext.getCmp('ca_iscashbank').value,
			cashflow = Ext.getCmp('ca_cashflow').value,
			msg = '';
		if(!Ext.isEmpty(currency)&&!currencytype){
			var defaultCurrency = null;
			Ext.Ajax.request({
				url:basePath + 'fa/ars/getDefaultCurrency.action',
				method:'post',
				async: false,
				callback:function(options,success,resp){
					var res = new Ext.decode(resp.responseText);
					if(res.success){
						if(res.defaultCurrency){
							defaultCurrency = res.defaultCurrency;	
						}
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);	
					}
				}
			});
			if(currency!=defaultCurrency){
				msg+='当前科目默认币别不是本位币，请检查外币核算、期末调汇属性是否正确！</br>'
			}
		}
		if(iscashbank&&(Math.abs(iscashbank)==1)&&!cashflow){
			msg+='当前科目为银行现金类科目，请勾选现金流量相关！'
		}
		if(msg){
			var box = Ext.create('Ext.window.MessageBox', {
				buttonAlign : 'center',
				buttons: [{
					text: '继续',
					handler: function(b) {
						var scope = b.ownerCt.ownerCt;
						scope.fireEvent('hide', scope, true);
					}
				},{
					text: '取消',
					handler: function(b) {
						var scope = b.ownerCt.ownerCt;
						scope.fireEvent('hide', scope, false);
					}
				}],
				listeners: {
					hide: function(w, ok) {
						w.close();
						if(typeof ok == 'boolean') {
							if(ok){
								if(isSave){
									me.FormUtil.beforeSave(this);
								}else{
									me.FormUtil.onUpdate(this);
								}
							}else
								window.location.reload();
						}
					}
				}
			});
			box.show({
				title : $I18N.common.msg.title_prompt,
				msg : msg,
				icon : Ext.MessageBox.QUESTION
			});
		} else {
			if(isSave){
				me.FormUtil.beforeSave(this);
			}else{
				me.FormUtil.onUpdate(this);
			}
		}	
	}
});