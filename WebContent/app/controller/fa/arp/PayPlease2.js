Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.PayPlease2', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    ppdid:0,
    views:[
      		'core.button.PrintByCondition','core.form.Panel','fa.arp.PayPlease2','core.grid.Panel5','core.toolbar.Toolbar','fa.arp.payplease.PayPleaseDetailGrid','core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted','core.form.FileField',
      		'core.button.StrikeBalance','core.grid.AssPanel','core.window.AssWindow','core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Post','core.button.ResPost','core.button.Print','core.button.Submit','core.button.ResAudit','core.button.Audit','core.button.ResSubmit',
      		'erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
      		'erp.view.core.button.Down','erp.view.core.button.UpExcel','core.button.TurnPrePay','core.button.TurnBankRegister','core.button.TurnBillAP','core.button.TurnBillARChange',
      		'core.trigger.MultiDbfindTrigger', 'core.form.SeparNumber','core.form.MultiField','core.button.End','core.button.ResEnd','core.trigger.BankNameTrigger'
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
					field.readOnly=false;
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
    			afterrender: function(g) {
                    g.plugins[0].on('beforeedit', function(args) {
                    	var bool = true;
                    	if (args.field == "ppdd_pucode") {
                    		if (!Ext.isEmpty(args.record.get('ppdd_makecode'))){
                    			bool = false;
                    		}
                    		return bool;
                        }
                    	if (args.field == "ppdd_pddetno") {
                    		if (!Ext.isEmpty(args.record.get('ppdd_makecode'))){
                    			bool = false;
                    		}
                    		if(!Ext.isEmpty(args.record.get('ppdd_type')) && args.record.get('ppdd_type') != '采购单'){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "ppdd_makecode") {
                        	if (!Ext.isEmpty(args.record.get('ppdd_pucode'))){
                    			bool = false;
                    		}
                    		return bool;
                        }       
                    });
                },
    			itemclick:me.onGridItemClick
    		},
    		//转预付款单
    		'erpTurnPrePayButton':{
    			click:function(btn){
    				me.turnPrePay();
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
    				me.FormUtil.onAdd('addPayPlease', '新增预付款申请单', 'jsps/fa/arp/payplease2.jsp');
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
    		'erpPrintButton': {
    			click: function(btn){
	    			var reportName="APPay_app2";
					var condition = '{PayPlease.pp_id}=' + Ext.getCmp('pp_id').value + '';
					var id = Ext.getCmp('pp_id').value;
					me.FormUtil.onwindowsPrint(id, reportName, condition);
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
    		//应付票据付款
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
    		'dbfindtrigger[name=ppd_bankaccount]':{
    			focus: function(t){
    				var vecode = t.record.data['ppd_vendcode'];
    				if(vecode != ''){
    					t.dbBaseCondition = " vpd_vecode='"+vecode+"' ";
    				}
    			}
    		},
    		'field[name=ppdd_pucode]':{
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
    					t.dbBaseCondition = " pu_receivecode  = '" + code + "'";
    				}
    			}
    		},
    		'field[name=ppdd_makecode]':{
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
    					t.dbBaseCondition = " nvl(ma_apvendcode,ve_apvendcode) = '" + code + "'";
    				}
    			}
    		},
    		'field[name=ppdd_pddetno]':{
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
    					t.dbBaseCondition = " pu_receivecode = '" + code + "'";
    				}
    			}
    		}
    	});
    }, 
    
    turnPrePay: function(){
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('pp_paymentcode').value;
	    var thisamount = Ext.getCmp('pp_thispayamount').value, amount = 0;
		var ppamount = Ext.getCmp('pp_total').value;
		Ext.each(grid.store.data.items,function(item,index){
			amount=amount+Number(item.data['ppd_account']);
		});
		if(thisamount-(ppamount-amount)>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额!未转金额：'+ (ppamount-amount));
			return;
		}
	    if(catecode == null || catecode == ''){
	    	Ext.Msg.alert('警告','请填写需要转银行登记的付款方信息!');
	        return;
	    }
	    if(thisamount == null || thisamount=='' || thisamount==0){
	    	Ext.Msg.alert('警告','本次付款金额未填写!');
			return;
		}
    	var me = this, form = Ext.getCmp('form');
    	me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fa/PayPleaseController/turnPrePay.action',
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
    				if(r.content && r.content.pp_id){
    					showMessage("提示", "转入成功,预付账款单号: <a href=\"javascript:openUrl2('jsps/fa/arp/prepay.jsp?formCondition=pp_idIS" + r.content.pp_id
     							 + "&gridCondition=ppd_ppidIS" + r.content.pp_id + "&whoami=PrePay!Arp!PAMT','预付账款单','pp_id'," + r.content.pp_id
     							 + ")\">" + r.content.pp_code + "</a>");
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
			currency1 = "" ,param1, param2;
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
		param1 = me.GridUtil.getGridStore(grid1);
		param2 = me.GridUtil.getGridStore(grid2);
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
		var grid2 = Ext.getCmp('paypleasedetaildetGrid'),items2=grid2.store.data.items;
    	var bool = true;
    	Ext.each(items2,function(item,index){
			if(!me.GridUtil.isBlank(grid2, item.data)){
				if(Math.abs(Number(item.data['ppdd_billamount']))<Math.abs(Number(item.data['ppdd_account']))){
					bool = false;
					showError('采购单的已预付金额大于采购金额,不能提交');return;
				}
				if(!Ext.isEmpty(item.get('ppdd_ordercode'))) {
					if(Math.abs(Number(item.data['ppdd_billamount'])-Number(item.data['ppdd_account'])) < Math.abs(Number(item.data['ppdd_thisapplyamount']))){
						bool = false;
						showError('本次预付金额大于采购单未付的金额,不能提交');return;
					}
				}
			}
    	});
	    if(bool)
	    	me.FormUtil.onSubmit(Ext.getCmp('pp_id').value);
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
		if(thisamount-(ppamount-amount)>0.01){
			Ext.Msg.alert('警告','本次付款金额超过剩余未转金额!未转金额：'+ (ppamount-amount));
			return;
		}
	    if(catecode == null || catecode == ''){
	    	Ext.Msg.alert('警告','请填写需要转银行登记的付款方信息!');
	        return;
	    }
	    if(thisamount == null || thisamount=='' || thisamount==0){
	    	Ext.Msg.alert('警告','本次付款金额未填写!');
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
		if(thisamount-(ppamount-amount)>0.01){
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
    }
});