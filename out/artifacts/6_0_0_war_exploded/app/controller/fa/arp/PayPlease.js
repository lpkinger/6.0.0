Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.PayPlease', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    ppdid:0,
    views:[
      		'core.button.PrintByCondition','core.form.Panel','fa.arp.PayPlease','core.grid.Panel5','core.toolbar.Toolbar','fa.arp.payplease.PayPleaseDetailGrid','core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted','core.form.FileField',
      		'core.button.StrikeBalance','core.grid.AssPanel','core.window.AssWindow','core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Post','core.button.ResPost','core.button.Print','core.button.Submit','core.button.ResAudit','core.button.Audit','core.button.ResSubmit',
      		'erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
      		'erp.view.core.button.Down','erp.view.core.button.UpExcel','core.button.TurnPayBalance','core.button.TurnBankRegister','core.button.TurnBillAP','core.button.TurnBillARChange',
      		'core.trigger.MultiDbfindTrigger', 'core.form.SeparNumber','core.form.MultiField','core.form.MonthDateField',
      		'core.button.End','core.button.ResEnd','core.button.TurnPayBalanceCYF','core.trigger.BankNameTrigger','core.trigger.MultiDbfindTrigger'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'textfield[name=pp_thispayamount]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=pp_thispaydate]':{
				beforerender: function(field){
					field.readOnly = false;
				}
			},
			'field[name=pp_refno]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=pp_paymentcode]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
    		//第二个从表
    		'#paypleasedetaildetGrid':{
    			afterrender: function(btn) {
    				me.BaseUtil.getSetting('sys', 'useBillOutAP', function(bool) {
    					if(bool) 
    					me.BaseUtil.getSetting('PayBalance', 'useAPCheck', function(bool2) {
	    	            	if(bool2) {
		    					Ext.getCmp('ac_code').show();
		    					Ext.getCmp('bi_code').hide();
	    	            	}else{
	    	            		Ext.getCmp('ac_code').hide();
		    					Ext.getCmp('bi_code').show();
	    	            	}
	    	            });
    	            });
    			},
    			itemclick: this.onGridItemClick
    		},
    		'erpDeleteDetailButton': {
    			afterdelete: function(d, r, btn){
    				//还原发票锁定金额
    				Ext.Ajax.request({
    					url: basePath + 'fa/arp/reLockAmount.action',
    					params: {
    						id: d.ppdd_id,
    						abcode: d.ppdd_billcode,
    						amount: d.ppdd_thisapplyamount
    					},
    					callback: function(opt, s, r){
    						var res = Ext.decode(r.responseText);
    						if(res.success) {
    							
    						}
    					}
    				});
    			}
            },
    		//转付款单
    		'erpTurnPayBalanceButton':{
    			click:function(btn){
    				me.turnPayBalance();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
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
    		//应付票据付款
    		'erpTurnBillAPButton':{
    			click:function(btn){
    				me.turnBillAP();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		//应收票据背书转让
    		'erpTurnBillARChangeButton':{
    			click:function(btn){
    				me.turnBillARChange();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		//冲应收款
    		'erpTurnPayBalanceCYFButton':{
    			click:function(btn){
    				me.turnPayBalanceCYF();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				//保存之前的一些前台的逻辑判定
    				this.beforeSavePayPlease();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.beforeUpdatePayPlease();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPayBalance', '新增付款申请单', 'jsps/fa/arp/payplease.jsp?whoami='+caller);
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
    				me.beforeSubmit(btn);
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
    				me.FormUtil.onResSubmit(Ext.getCmp('pp_id').value);
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
    				me.FormUtil.onAudit(Ext.getCmp('pp_id').value);
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
    				me.FormUtil.onResAudit(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pp_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	warnMsg("确定结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/arp/endPayPlease.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('pp_id').value
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
    					}
    				});
                }
            },
            'erpResEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pp_statuscode');
                    if (status && status.value != 'FINISH') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	warnMsg("确定反结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'fa/arp/resEndPayPlease.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('pp_id').value
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
            },
    		'erpPrintButton': {
    			click: function(btn){
	    			var reportName="APPay_app";
					var condition = '{PayPlease.pp_id}=' + Ext.getCmp('pp_id').value + '';
					var id = Ext.getCmp('pp_id').value;
					me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'dbfindtrigger[name=ppdd_billcode]':{
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').store.first();
    				if(!record || !record.data['ppd_vendcode']) {
    					showError("请先选择供应商!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var code = record.data['ppd_vendcode'];
    					t.dbBaseCondition = " ab_vendcode = '" + code + "'";
    				}
    			}
    		},
    		'dbfindtrigger[name=ppd_bankaccount]':{
    			focus: function(t){
    				var vecode = t.record.data['ppd_vendcode'];
    				if(vecode != ''){
    					t.dbBaseCondition = " vpd_vecode='"+vecode+"' ";
    				}
    			}
    		},
    		'multidbfindtrigger[name=ppdd_billcode]':{
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').store.first();
    				if(!record || !record.data['ppd_vendcode']) {
    					showError("请先选择供应商!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var code = record.data['ppd_vendcode'];
    					t.dbBaseCondition = " ab_vendcode = '" + code + "'";
    				}
    			}
    		},
    		'#bi_code':{
    			beforetrigger: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').store.first();
    				if(!record || !record.data['ppd_vendcode']) {
    					showError("请先选择供应商!");
    					return false;
    				} else if (!record.data['ppd_currency']){
    					showError("请先选择币别!");
    					return false;
    				} else {
    					t.dbBaseCondition = " bi_vendcode = '" + record.data['ppd_vendcode'] + "' and bi_currency='" + record.data['ppd_currency'] + "'";
    				}
    			}
    		},
    		'#ac_code':{
    			beforetrigger: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').store.first();
    				if(!record || !record.data['ppd_vendcode']) {
    					showError("请先选择供应商!");
    					return false;
    				} else if (!record.data['ppd_currency']){
    					showError("请先选择币别!");
    					return false;
    				} else {
    					t.dbBaseCondition = " ac_vendcode = '" + record.data['ppd_vendcode'] + "' and ac_currency='" + record.data['ppd_currency'] + "'";
    				}
    			}
    		},
    		//抓取发票信息
    		'button[name=catchab]':{
    			afterrender:function(btn){
    				Ext.defer(function(){
    					if(Ext.getCmp('grid') && Ext.getCmp('grid').store.getCount() == 0){
        					btn.setDisabled(true);
        				}
    				}, 500);
    			},
    			click:function(btn){
    				var grid2 = Ext.getCmp('paypleasedetaildetGrid');
    				var form = Ext.getCmp('form');
    				var lastselect = Ext.getCmp('grid').store.first();
    				var params = new Object();
    				var thisamount = lastselect.data['ppd_applyamount'].toString();
    				if(lastselect == null){
    					//grid1  没有选择数据
    					Ext.Msg.alert('警告','请先填写供应商信息!');
    				}else{
    					//grid1 选择了数据 
    					var array = new Array();
    					Ext.each(grid2.store.data.items,function(item,index){
    						var d = item.get('ppdd_id');
    						if(d != null && Number(d) > 0){
    							array.push(item);
    						}
    					});
    					if(array.length>0){
        					Ext.Msg.alert('警告','需要先清除明细行中的数据!');
        				} else {
        					params['ppd_id'] = lastselect.data['ppd_id'].toString();
        					params['ppd_ppid'] = lastselect.data['ppd_ppid'].toString();
        					params['startdate'] = Ext.getCmp('startdate').value;
        					params['enddate'] = Ext.getCmp('enddate').value;
        					params['caller'] = caller;
        					var accode = Ext.getCmp('ac_code');
        					if(accode&&!accode.isHidden()){
        						params['bicode'] = accode.value;
        					}else{
        						params['bicode'] = Ext.getCmp('bi_code').value;
        					}
        					
        					me.FormUtil.getActiveTab().setLoading(true);
        					//抓取
        					Ext.Ajax.request({
        				   		url : basePath + form.catchAPUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				catchSuccess(function(){
        			    					window.location.reload();
        			    				});
        				   			} else if(localJson.exceptionInfo){
        				   				showError(localJson.exceptionInfo);
        				   			} else{
        				   				catchFailure();//@i18n/i18n.js
        				   			}
        				   		}
        					});
        				}
    				}
    			}
    		},
    		//清除发票信息
    		'button[name=cleanab]':{
    			afterrender:function(btn){
    				Ext.defer(function(){
    					if(Ext.getCmp('paypleasedetaildetGrid') && Ext.getCmp('paypleasedetaildetGrid').store.getCount() == 0){
        					btn.setDisabled(true);
        				}
    				}, 500);
    			},
    			click:function(btn){
    				var grid2 = Ext.getCmp('paypleasedetaildetGrid');
    				warnMsg('确定清除所有明细行么?',function(t){
    					if(t=='yes'){
    	    				var form = Ext.getCmp('form');
    	    				var lastselect = Ext.getCmp('grid').store.first();
    	    				var params = new Object();
    	    				if(lastselect == null){
    	    					Ext.Msg.alert('警告','请先填写供应商信息!');
    	    				} else {
	    	    				params['ppd_id'] = lastselect.data['ppd_id'].toString();
		    					params['ppd_ppid'] = lastselect.data['ppd_ppid'].toString();
		    					params['caller'] = caller;
	    						Ext.Ajax.request({
	        				   		url : basePath + form.cleanAPUrl,
	        				   		params : params,
	        				   		method : 'post',
	        				   		callback : function(options,success,response){
	        				   			me.FormUtil.getActiveTab().setLoading(false);
	        				   			var localJson = new Ext.decode(response.responseText);
	        			    			if(localJson.success){
	        			    				cleanSuccess(function(){
	        			    					window.location.reload();
	        			    				});
	        				   			} else if(localJson.exceptionInfo){
	        				   				showError(localJson.exceptionInfo);return;
	        				   			} else{
	        				   				cleanFailure();//@i18n/i18n.js
	        				   			}
	        				   		}
	        					});
    	    				}
    					}else{
    						return;
    					}
    				});
    			}
    		},
    		 //发票明细
    		'button[name=detail]':{
    			afterrender:function(btn){
    				Ext.defer(function(){
    					if(Ext.getCmp('paypleasedetaildetGrid') && Ext.getCmp('paypleasedetaildetGrid').store.getCount() == 0){
        					btn.setDisabled(true);
        				}
    				}, 500);
    			},
    			click:function(btn){
    				var det = Ext.getCmp('paypleasedetaildetGrid');
    				if(det.store.data.items.length == 0) {
    					showError('请先选择申请单明细!');
    					return;
    				}
    				var id = det.store.data.items[0].get('ppdd_ppdid');
    				if(id == 0) {
    					showError('请先选择申请单明细!');
    					return;
    				}
    				var win = Ext.getCmp('bill-win');
    				if(!win) {
    					var url = basePath +'jsps/common/datalist.jsp?whoami=APBill!CWIM!Detail&_noc=1';
    					url += '&urlcondition=ab_code in (select ppdd_billcode from paypleasedetaildet where ppdd_ppdid=' + 
    						id + ')';
    					win = new Ext.window.Window({
    			    		id : 'bill-win',
    					    title: '发票明细',
    					    height: '100%',
    					    width: '80%',
    					    maximizable : true,
    						buttonAlign : 'center',
    						closeAction: 'hide',
    						layout : 'anchor',
    					    items: [{
    					    	  tag : 'iframe',
    					    	  frame : true,
    					    	  anchor : '100% 100%',
    					    	  layout : 'fit',
    					    	  html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
    					    }],
    					    buttons : [{
    					    	text : '关  闭',
    					    	iconCls: 'x-button-icon-close',
    					    	cls: 'x-btn-gray',
    					    	handler : function(){
    					    		Ext.getCmp('bill-win').hide();
    					    	}
    					    }]
    					});
    				}
    				win.show();
    			}
    		}
    	});
    }, 
    
    turnPayBalance: function(){
    	var grid = Ext.getCmp('grid'), type = Ext.getCmp('pp_type').value;
    	var me = this, form = Ext.getCmp('form');
    	me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fa/PayPleaseController/turnPayBalance.action',
	   		params: {
	   			caller: caller,
	   			formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.getActiveTab().setLoading(false);
	   			var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
	   			if(r.success){
    				if(r.content && r.content.pb_id){
    					showMessage("提示", "转入成功,付款单号: <a href=\"javascript:openUrl2('jsps/fa/arp/paybalance.jsp?formCondition=pb_idIS" + r.content.pb_id
      							 + "&gridCondition=pbd_pbidIS" + r.content.pb_id + "&whoami=PayBalance','付款单','pb_id'," + r.content.pb_id
      							 + ")\">" + r.content.pb_code + "</a>");
    				}
    				window.location.reload();
	   			}
	   		}
		});
    },
    turnBankRegister: function(){
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('pp_paymentcode').value;
        var thisamount = Ext.getCmp('pp_thispayamount').value, amount = 0;
		var ppamount = Ext.getCmp('pp_total').value;
		var thispaydate = Ext.getCmp('pp_thispaydate').value;
		if(Ext.isEmpty(thispaydate)){
			Ext.Msg.alert('警告','请填写本次付款日期！');
			return;
		}
		Ext.each(grid.store.data.items,function(item,index){
			amount=amount+Number(item.data['ppd_account']);
		});
		if(Math.abs(thisamount)-(Math.abs(ppamount)-Math.abs(amount))>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额！未转金额：'+ (ppamount-amount));
			return;
		}
        if(catecode == null || catecode == ''){
        	Ext.Msg.alert('警告','请填写需要转银行登记的付款方信息！');
        	return;
        }
		if(thisamount == null || thisamount=='' || thisamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写！');
			return;
		}
    	var me = this, form = Ext.getCmp('form');
    	me.FormUtil.getActiveTab().setLoading(true);//loading...   	
		Ext.Ajax.request({
	   		url : basePath + 'fa/PayPleaseController/turnBankRegister.action',
	   		params: {
	   			caller: caller,
	   			formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.getActiveTab().setLoading(false);
	   			var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
	   			if(r.success){
    				if(r.content && r.content.ar_id){
    					showMessage("提示", "转入成功,银行登记: <a href=\"javascript:openUrl2('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + r.content.ar_id
    							 + "&gridCondition=ard_aridIS" + r.content.ar_id + "&whoami=AccountRegister!Bank','银行登记','ar_id'," + r.content.ar_id
    							 + ")\">" + r.content.ar_code + "</a>");
    				}
    				window.location.reload();
	   			}
	   		}
		});
    },
    turnBillAP: function(){
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('pp_paymentcode').value;
        var thisamount = Ext.getCmp('pp_thispayamount').value, amount = 0;
		var ppamount = Ext.getCmp('pp_total').value;
		var thispaydate = Ext.getCmp('pp_thispaydate').value;
		if(Ext.isEmpty(thispaydate)){
			Ext.Msg.alert('警告','请填写本次付款日期！');
			return;
		}
		Ext.each(grid.store.data.items,function(item,index){
			amount=amount+Number(item.data['ppd_account']);
		});
		if(Math.abs(thisamount)-(Math.abs(ppamount)-Math.abs(amount))>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额!未转金额：'+ (ppamount-amount));
			return;
		}
        if(catecode == null || catecode == ''){
        	Ext.Msg.alert('警告','请填写需要转应付票据的付款方信息!');
        	return;
        }
		if(thisamount == null || thisamount=='' || thisamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
		var me = this, form = Ext.getCmp('form');
    	me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fa/PayPleaseController/turnBillAP.action',
	   		params: {
	   			caller: caller,
	   			formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.getActiveTab().setLoading(false);
	   			var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
	   			if(r.success){
    				if(r.content && r.content.bap_id){
    					showMessage("提示", "转入成功,应付票据: <a href=\"javascript:openUrl2('jsps/fa/gs/billAP.jsp?formCondition=bap_idIS" + r.content.bap_id
    							 + "','应付票据','bap_id'," + r.content.bap_id
    							 + ")\">" + r.content.bap_code + "</a>");
    				}
    				window.location.reload();
	   			}
	   		}
		});
    },
    turnBillARChange: function(){
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('pp_paymentcode').value;
        var thisamount = Ext.getCmp('pp_thispayamount').value, amount = 0;
		var ppamount = Ext.getCmp('pp_total').value;
		var thispaydate = Ext.getCmp('pp_thispaydate').value;
		if(Ext.isEmpty(thispaydate)){
			Ext.Msg.alert('警告','请填写本次付款日期！');
			return;
		}
		Ext.each(grid.store.data.items,function(item,index){
			amount=amount+Number(item.data['ppd_account']);
		});
		if(Math.abs(thisamount)-(Math.abs(ppamount)-Math.abs(amount))>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额!未转金额：'+ (ppamount-amount));
			return;
		}
		if(thisamount == null || thisamount=='' || thisamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
		var me = this, form = Ext.getCmp('form');
    	me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fa/PayPleaseController/turnBillARChange.action',
	   		params: {
	   			caller: caller,
	   			formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.getActiveTab().setLoading(false);
	   			var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
	   			if(r.success){
	   				if(r.content && r.content.brc_id){
    					showMessage("提示", "转入成功,应收票据异动: <a href=\"javascript:openUrl2('jsps/fa/gs/billARChange.jsp?formCondition=brc_idIS" + r.content.brc_id
    							 + "&gridCondition=brd_brcidIS" + r.content.brc_id + "','应收票据异动','brc_id'," + r.content.brc_id
    							 + ")\">" + r.content.brc_code + "</a>");
    				}
    				window.location.reload();
	   			}
	   		}
		});
    },
    turnPayBalanceCYF: function(){
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('pp_paymentcode').value;
        var thisamount = Ext.getCmp('pp_thispayamount').value, amount = 0;
		var ppamount = Ext.getCmp('pp_total').value;
		var thispaydate = Ext.getCmp('pp_thispaydate').value;
		if(Ext.isEmpty(thispaydate)){
			Ext.Msg.alert('警告','请填写本次付款日期！');
			return;
		}
		Ext.each(grid.store.data.items,function(item,index){
			amount=amount+Number(item.data['ppd_account']);
		});
		if(thisamount-(ppamount-amount)>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额!未转金额：'+ (ppamount-amount));
			return;
		}
		if(thisamount == null || thisamount=='' || thisamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
		var me = this, form = Ext.getCmp('form');
    	me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fa/PayPleaseController/turnPayBalanceCYF.action',
	   		params: {
	   			caller: caller,
	   			formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.getActiveTab().setLoading(false);
	   			var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
	   			if(r.success){
	   				if(r.content && r.content.pb_id){
	   					console.log(r.content);
    					showMessage("提示", "转入成功,冲应付款单号: <a href=\"javascript:openUrl2('jsps/fa/arp/paybalance.jsp?formCondition=pb_idIS" + r.content.pb_id
    							 + "&gridCondition=pbd_pbidIS" + r.content.pb_id + "&whoami=PayBalance!CAID','冲应付款单','pb_id'," + r.content.pb_id
    							 + ")\">" + r.content.pb_code + "</a>");
    				}
    				window.location.reload();
	   			}
	   		}
		});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSavePayPlease: function(){
		var grid1 = Ext.getCmp('grid'), items1 = grid1.store.data.items,
		    grid2 = Ext.getCmp('paypleasedetaildetGrid'), items2 = grid2.store.data.items,
		    sameCurrency = true, date = Ext.getCmp('pp_date').getValue(), 
			currency1 = "";
		Ext.each(items1, function(item,index){
			if(!Ext.isEmpty(item.data['ppd_vendcode'])){
				if(!Ext.isEmpty(item.data['ppd_currency'])){
					currency1 = item.data['ppd_currency'];
				}
				if(Ext.isEmpty(item.data['ppd_startdate'])) {
					item.set('ppd_startdate', Ext.Date.getFirstDateOfMonth(date));
				}
				if(Ext.isEmpty(item.data['ppd_overdate'])) {
					item.set('ppd_overdate', Ext.Date.getLastDateOfMonth(date));
				}
			}
		});
		if(!Ext.isEmpty(currency1)){
			Ext.each(items2, function(item,index){
				if(!Ext.isEmpty(item.data['ppdd_billcode']) || !Ext.isEmpty(item.data['ppdd_pucode']) || !Ext.isEmpty(item.data['ppdd_makecode'])){
					if(item.data['ppdd_currency'] != currency1) {
						sameCurrency = false;
					}
				}
			});
		}
		if(!sameCurrency){
			showError($I18N.common.msg.failure_save_currency);
			return;
		}
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(form.keyField){
 		   if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
 			  me.FormUtil.getSeqId(form);
 		   }
 	   	}
		var param1 = me.GridUtil.getGridStore(grid1), param2 = me.GridUtil.getGridStore(grid2);
		if((param1 == null || param1 == '') && (param2 == null || param2 == '')){
			me.onSave([]);
		} else {
			me.onSave(param1,param2);
		}
	},
	onSave:function(param,param2){
	 	   var me = this;
	 	   var form = Ext.getCmp('form');
	 	   param = param == null ? [] : "[" + param.toString() + "]";
	 	   param2 = param2 == null ? [] : "[" + param2.toString() + "]";
	 	   if(form.getForm().isValid()){
	 		   //form里面数据
	 		   Ext.each(form.items.items, function(item){
	 			   if(item.xtype == 'numberfield'){
	 				   //number类型赋默认值，不然sql无法执行
	 				   if(item.value == null || item.value == ''){
	 					   item.setValue(0);
	 				   }
	 			   }
	 		   });
	 		   var r = form.getValues();
	 		   //去除ignore字段
	 		   var keys = Ext.Object.getKeys(r), f;
	 		   var reg = /[!@#$%^&*()'":,\/?]/;
	 		   Ext.each(keys, function(k){
	 			   f = form.down('#' + k);
	 			   if(f && f.logic == 'ignore') {
	 				   delete r[k];
	 			   }
	 			   //codeField值强制大写,自动过滤特殊字符
	 			   if(k == form.codeField && !Ext.isEmpty(r[k])) {
	 				   r[k] = r[k].trim().toUpperCase().replace(reg, '');
	 			   }
	 		   });
	 		   if(!me.FormUtil.contains(form.saveUrl, '?caller=', true)){
	 			   form.saveUrl = form.saveUrl + "?caller=" + caller;
	 		   }
	 		   me.FormUtil.save(r,param,param2);
	 	   }else{
	 		   me.FormUtil.checkForm();
	 	   }
	},
	beforeUpdatePayPlease:function(){
		var grid = Ext.getCmp('grid'), items1 = grid.store.data.items;
		var date = Ext.getCmp('pp_date').getValue();
		var currency1 = "";
		Ext.each(items1, function(item,index){
			if(!Ext.isEmpty(item.data['ppd_vendcode'])){
				if(!Ext.isEmpty(item.data['ppd_currency'])){
					currency1 = item.data['ppd_currency'];
				}
				if(Ext.isEmpty(item.data['ppd_startdate'])) {
					item.set('ppd_startdate', Ext.Date.getFirstDateOfMonth(date));
				}
				if(Ext.isEmpty(item.data['ppd_overdate'])) {
					item.set('ppd_overdate', Ext.Date.getLastDateOfMonth(date));
				}
			}
		});
		var grid2 = Ext.getCmp('paypleasedetaildetGrid'), items2 = grid2.store.data.items;
		var sameCurrency = true;
		if(!Ext.isEmpty(currency1)){
			Ext.each(items2, function(item,index){
				if(!Ext.isEmpty(item.data['ppdd_billcode']) || !Ext.isEmpty(item.data['ppdd_pucode']) || !Ext.isEmpty(item.data['ppdd_makecode'])){
					if(item.data['ppdd_currency'] != currency1) {
						sameCurrency = false;
					}
				}
			});
		}
		if(!sameCurrency){
			showError($I18N.common.msg.failure_save_currency);
			return;
		}
		this.beforeUpdate();
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(form.keyField){
 		   if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
 			  me.FormUtil.getSeqId(form);
 		   }
 	    }
		var grid1 = Ext.getCmp('grid'), grid2 = Ext.getCmp('paypleasedetaildetGrid'),
			param1 = me.GridUtil.getGridStore(grid1), param2 = me.GridUtil.getGridStore(grid2);
		if((param1 == null || param1 == '') && (param2 == null || param2 == '')){
			me.onUpdate([]);
		} else {
			me.onUpdate(param1,param2);
		}
    },
    onUpdate:function(param,param2){
 	   var me = this;
 	   var form = Ext.getCmp('form');
 	   param = param == null ? [] : "[" + param.toString() + "]";
 	   param2 = param2 == null ? [] : "[" + param2.toString() + "]";
 	   if(form.getForm().isValid()){
 		   //form里面数据
 		   Ext.each(form.items.items, function(item){
 			   if(item.xtype == 'numberfield'){
 				   //number类型赋默认值，不然sql无法执行
 				   if(item.value == null || item.value == ''){
 					   item.setValue(0);
 				   }
 			   }
 		   });
 		   var r = form.getValues();
 		   //去除ignore字段
 		   var keys = Ext.Object.getKeys(r), f;
 		   var reg = /[!@#$%^&*()'":,\/?]/;
 		   Ext.each(keys, function(k){
 			   f = form.down('#' + k);
 			   if(f && f.logic == 'ignore') {
 				   delete r[k];
 			   }
 			   //codeField值强制大写,自动过滤特殊字符
 			   if(k == form.codeField && !Ext.isEmpty(r[k])) {
 				   r[k] = r[k].trim().toUpperCase().replace(reg, '');
 			   }
 		   });
 		   if(!me.FormUtil.contains(form.updateUrl, '?caller=', true)){
 			   form.updateUrl = form.updateUrl + "?caller=" + caller;
 		   }
 		   me.FormUtil.update(r,param,param2);
 	   }else{
 		   me.FormUtil.checkForm();
 	   }
    },
	beforeSubmit: function(){
    	var me = this;
    	var grid1 =	Ext.getCmp('grid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('paypleasedetaildetGrid'),items2=grid2.store.data.items;
    	var applyamount = 0, nowamount=0,amount=0;
    	var bool = true;
    	Ext.each(items2,function(item,index){
			if(!me.GridUtil.isBlank(grid2, item.data)){
				nowamount = nowamount+Number(item.data['ppdd_thisapplyamount']);
				if(Math.abs(Number(item.data['ppdd_billamount']))<Math.abs(Number(item.data['ppdd_account']))){
					bool = false;
					showError('发票的已付金额大于发票金额,不能提交');return;
				}
				if(!Ext.isEmpty(item.get('ppdd_ordercode'))) {
					if(Math.abs(Number(item.data['ppdd_billamount'])-Number(item.data['ppdd_account'])) < Math.abs(Number(item.data['ppdd_thisapplyamount']))){
						bool = false;
						showError('本次申请金额大于发票未付的金额,不能提交');return;
					}
				}
			}
    	});
		var pptotal = Number(Ext.getCmp('pp_total').getValue());
    	var type=Ext.getCmp('pp_type').value;
    	if(type=='应付款'){
	    	Ext.each(items1,function(item,index){
				if(item.data[grid1.necessaryField]!=''&&item.data[grid1.necessaryField]!=null){
					applyamount = Number(item.data['ppd_applyamount']);
					amount = amount+ applyamount;
					if(Math.abs(nowamount-applyamount)>0.001){
		    			//冲账金额与明细行本次结算总和不等
		    			//抛出异常
						bool = false;
		    			showError('申请金额与发票详情中本次申请金额不等,不能提交');return;
		    		}
	        	}
	    	});
	    	
			if(amount != pptotal){
				bool = false;
				showError($I18N.common.grid.submitPayPleaseError1);return;
			 }
	    	if(bool)
	    		me.FormUtil.onSubmit(Ext.getCmp('pp_id').value);
    	} else {
    		if(bool)
	    		me.FormUtil.onSubmit(Ext.getCmp('pp_id').value);
    	}
    }
});