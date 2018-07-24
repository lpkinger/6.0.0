Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.BillAPCheque', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.gs.BillAPCheque','core.form.Panel','core.form.MultiField','core.form.FileField','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.CopyAll',
    		'core.button.ResAudit','core.button.TurnRecBalance','core.button.Accounted','core.button.ResAccounted',
    		'core.button.UpdateInfo','core.button.TurnBankRegister','core.button.End','core.button.ResEnd',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.SeparNumber'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=bar_duedate]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=bar_accountcode]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=bar_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=bar_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
    					showError('到期日期小于支票日期'); return;
    				}
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBillAPCheque', '新增应付支票', 'jsps/fa/gs/billAPCheque.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
    					showError('到期日期小于支票日期'); return;
    				}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
    					showError('到期日期小于支票日期'); return;
    				}
    				var cmcurrency = Ext.getCmp('bar_cmcurrency').getValue();
    				var currency = Ext.getCmp('bar_currency').getValue();
    				var cmrate = Number(Ext.getCmp('bar_cmrate').getValue());
    				if(currency == cmcurrency){
    					if(cmrate != 1){
    						showError('币别相同，冲账汇率不等于1,不能过账!');
    						return;
    					}
    				}
    	    		if(currency != cmcurrency){
    					if(cmrate == 1){
    						showError('币别不相同，冲账汇率等于1,不能过账!');
    						return;
    					}
    				}
    				me.FormUtil.onSubmit(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: {
    				fn:function(btn){
        				me.auditBillAPCheque();
        			},
        			lock:2000
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bar_id').value);
    			}
    		},
    		'erpAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
    					showError('到期日期小于支票日期'); return;
    				}
    				me.FormUtil.onAccounted(Ext.getCmp('ar_id').value);
    			}
    		},
    		'erpResAccountedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bar_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAccounted(Ext.getCmp('ar_id').value);
    			}
    		},
    		'erpCopyButton': {
    			click: function(btn) {
    				this.copy();
    			}
    		},
    		'textfield[name=bar_cmrate]':{
    			change: me.gettopaybalance
    		},
    		'textfield[name=bar_doublebalance]':{
    			change: me.gettopaybalance
    		},
    		'textfield[name=bar_topaybalance]':{
    			change: me.getcmrate
    		},
    		'erpUpdateInfoButton':{
    			click:function(){
    				var text=Ext.getCmp('bar_duedate');
    					me.updateInfo(text.value,Ext.getCmp('bar_id').value);
    			}
    		},
    		//转银行登记
    		'erpTurnBankRegisterButton':{
    			click:function(btn){
    				me.turnBankRegister();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('bar_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	var me = this, win = Ext.getCmp('end-win');
    				   	if(!win){
    				   		var reason = Ext.getCmp('bar_changereason'),
    					   	   	val = reason ? reason.value : '';
    				   		win = Ext.create('Ext.Window', {
    						   id: 'end-win',
    						   title: '支票结案',
    						   height: 200,
    						   width: 400,
    						   items: [{
    							   xtype: 'form',
    							   height: '100%',
    							   width: '100%',
    							   bodyStyle: 'background:#f1f2f5;',
    							   items: [{
    								   margin: '5 0 5 0',
    								   xtype: 'textareafield',
    								   fieldLabel: '结案原因',
    								   name:'bar_changereason',
    								   allowBlank: false
    							   }],
    							   closeAction: 'hide',
    							   buttonAlign: 'center',
    							   layout: {
    								   type: 'vbox',
    								   align: 'center'
    							   },
    							   buttons: [{
    								   text: $I18N.common.button.erpConfirmButton,
    								   cls: 'x-btn-blue',
    								   handler: function(btn) {
    									   var form = btn.ownerCt.ownerCt,
    									   a = form.down('textfield[name=bar_changereason]');
    									   if(Ext.isEmpty(a.value)){
    										   showError("请填写结案原因！");
    										   return;
    									   }
    									   if(form.getForm().isDirty()) {
    										   me.endBillAPCheque(Ext.getCmp('bar_id').value, a.value);
    									   }
    								   }
    							   }, {
    								   text: $I18N.common.button.erpCloseButton,
    								   cls: 'x-btn-blue',
    								   handler: function(btn) {
    									   btn.up('window').hide();
    								   }
    							   }]
    						   }]
    					   });
    				   }
    				   win.show();
                }
            },
            'erpResEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('bar_statuscode');
                    if (status && status.value != 'FINISH') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	warnMsg("确定反结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/gs/resEndBillAPCheque.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('bar_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				alert("反结案成功！");
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
                }
            }
    	});
    },
    gettopaybalance: function(){
    	if(Ext.getCmp('bar_doublebalance') && Ext.getCmp('bar_cmrate')) {
    		var doublebalance = Ext.Number.from(Ext.getCmp('bar_doublebalance').getValue(), 0);
    		var cmrate = Ext.Number.from(Ext.getCmp('bar_cmrate').getValue(), 0);
    		Ext.getCmp('bar_topaybalance').setValue(Ext.Number.toFixed(doublebalance*cmrate, 2));
    		if (typeof (f = Ext.getCmp('bar_leftamount')) != 'undefined' ) {
        		var v1 = (Ext.getCmp('bar_settleamount').value || 0);
        		Ext.getCmp('bar_leftamount').setValue(Ext.Number.toFixed(doublebalance-v1, 2));
        	}
    	}
    },
    getcmrate: function(){
    	if(Ext.getCmp('bar_doublebalance') && Ext.getCmp('bar_topaybalance')) {
    		var doublebalance = Ext.Number.from(Ext.getCmp('bar_doublebalance').getValue(), 0);
    		var topaybalance = Ext.Number.from(Ext.getCmp('bar_topaybalance').getValue(), 0);
    		if(doublebalance != 0){
    			Ext.getCmp('bar_cmrate').setValue(Ext.Number.toFixed(topaybalance/doublebalance, 15));
    		}
    	}
    },
    updateInfo:function(text,id){
		Ext.Ajax.request({
        	url : basePath + 'fa/gs/BillAPCheque/updateInfo.action',
        	params: {text:text,id:id},
        	method : 'post',
        	async:false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		showMessage("提示", '更新成功！');
        		window.location.reload();
        	}
        });
	},
	auditBillAPCheque:function(){
		var me = this;
		if(Ext.getCmp('bar_duedate').value < Ext.getCmp('bar_date').value){
			showError('到期日期小于支票日期'); return;
		}
		var cmcurrency = Ext.getCmp('bar_cmcurrency').getValue();
		var currency = Ext.getCmp('bar_currency').getValue();
		var cmrate = Number(Ext.getCmp('bar_cmrate').getValue());
		if(currency == cmcurrency){
			if(cmrate != 1){
				showError('币别相同，冲账汇率不等于1,不能过账!');
				return;
			}
		}
		if(currency != cmcurrency){
			if(cmrate == 1){
				showError('币别不相同，冲账汇率等于1,不能过账!');
				return;
			}
		}
		me.FormUtil.onAudit(Ext.getCmp('bar_id').value);
	},
    copy: function(){
	 	var me = this;
		var form = Ext.getCmp('form');
		var v = form.down('#bar_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/gs/copyBillAPCheque.action',
				params: {
					id: v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.ar) {
						turnSuccess(function(){
	    					var id = res.ar.bar_id;
	    					var url = "jsps/fa/gs/billAPCheque.jsp?formCondition=bar_idIS"+ id ;
	    					me.FormUtil.onAdd('BillAPCheque' + id, '应付支票' + id, url);
	    				});
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	endBillAPCheque: function(barid, val) {
		var me = this;
 	   	Ext.Ajax.request({
 		   url: basePath + 'fa/gs/endBillAPCheque.action',
 		   params: {
 			   caller: caller,
 			   id: barid,
 			   reason: val
 		   },
 		   method : 'post',
	   	   callback : function(options,success,response){
	   			me.FormUtil.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
	  			if(localJson.success){
	  				alert("结案成功！");
	  				window.location.reload();
		   		}
		   	}
 	   	});
    },
    turnBankRegister: function(){
    	var me = this;
    	var accountcode = Ext.getCmp('bar_accountcode').value, bar_id = Ext.getCmp('bar_id').value;
        if(accountcode == null || accountcode == ''){
        	Ext.Msg.alert('警告','请填写需要转银行登记的银行账户信息！');
        	return;
        }
        Ext.Ajax.request({
  		   url: basePath + 'fa/gs/apchequeToAccountRegister.action',
  		   params: {
  			   caller: caller,
  			   id: bar_id,
  			   accountcode: accountcode
  		   },
  		   method : 'post',
 	   	   callback : function(options,success,response){
 	   			me.FormUtil.getActiveTab().setLoading(false);
 	   			var localJson = new Ext.decode(response.responseText);
 	   			if(localJson.exceptionInfo){
 	   				showError(localJson.exceptionInfo);
 	   			}
 	  			if(localJson.success){
 	  				showMessage("localJson", localJson.log);
 		   		}
 		   	}
  	   	});
    }
});