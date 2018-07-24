Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.ARBill', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.button.PrintByCondition','core.form.Panel','fa.ars.ARBill','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
      			'core.button.Post','core.button.ResPost','core.trigger.CateTreeDbfindTrigger','core.button.Modify',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.AutoInvoice',
      			'core.form.MonthDateField','core.button.AssDetail','core.button.ConfirmXJSK','core.button.ConfirmYJSK',
      			'core.button.CancelSK','core.form.SeparNumber','core.button.PrintVoucherCode', 'core.button.CopyAll', 'core.button.TicketTaxes'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick:function(selModel, record){
    				if(!grid.readOnly) {
    					me.onGridItemClick(selModel, record);
    				}
    			},
    			afterrender:function(grid){
    				grid.plugins[0].on('afteredit',function(e){
    					
    				    if(e.context.field=='abd_aramount'){
    				    	if(Number(e.context.record.data["abd_thisvoprice"]) == 0 && Number(e.context.record.data["abd_qty"]) != 0){
    				    		var price =  Number(e.context.record.data["abd_aramount"])/Number(e.context.record.data["abd_qty"]);
    				    		e.context.record.set('abd_thisvoprice',price);
    				    	}
    				    }
    					setTimeout(function(){
        					var amount = 0;
        					Ext.each(grid.store.data.items,function(item,index){
        						amount=amount+Number(item.data['abd_aramount']);
        					});
        					Ext.getCmp('ab_aramount').setValue(Ext.util.Format.number(amount,'0.00'));
    					},200);
    				});
    			}
    		},
    		'field[name=ab_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ab_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
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
    				this.beforeSaveARBill();
    			}
    		},
    		'erpDeleteButton' : {
    			afterrender: function(btn){
    				var poststatus = Ext.getCmp('ab_statuscode');
    				if(poststatus && poststatus.value == 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var poststatus = Ext.getCmp('ab_statuscode');
    				if(poststatus && poststatus.value == 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdateARBill();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				var form = Ext.getCmp('form');
    				var title = '新增';
    				if(form){
    					if(form.title){
    						title = title+form.title;
    					}
    				}
    				me.FormUtil.onAdd('addARBill', title, 'jsps/fa/ars/arbill.jsp?whoami='+caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'ENTERING'){
    					btn.hide();
    				}
    				var poststatus = Ext.getCmp('ab_statuscode');
    				if(poststatus && poststatus.value == 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ab_id').value, false, this.beforeUpdateARBill, this, me.getForm(btn));
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'COMMITED'){
    					btn.hide();
    				}
    				var poststatus = Ext.getCmp('ab_statuscode');
    				if(poststatus && poststatus.value == 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'COMMITED'){
    					btn.hide();
    				}
    				var poststatus = Ext.getCmp('ab_statuscode');
    				if(poststatus && poststatus.value == 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'AUDITED'){
    					btn.hide();
    				}
    				var poststatus = Ext.getCmp('ab_statuscode');
    				if(poststatus && poststatus.value == 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    			var reportName="SaleInvoice";
    			var id=Ext.getCmp('ab_id').value;
    			var condition = '{ARBill.ab_id}=' + Ext.getCmp('ab_id').value;
    			console.log(condition);
    			me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpPrintVoucherCodeButton': {
    			click: function(btn){
    				if(Ext.getCmp('ab_vouchercode').value ==null || Ext.getCmp('ab_vouchercode').value ==''){	
    					showError('当前发票还没有制作凭证，不能打印');
    				}else{
	    				 var reportName = '';
	   	                 reportName = "SaleInvoice";
	   	                 var condition = '{APBill.ab_vouchercode}=\'' + Ext.getCmp('ab_vouchercode').value +'\'';
	   	                 var id=Ext.getCmp('ab_id').value;
	   	                 me.FormUtil.onwindowsPrint(id, reportName, condition);
	    			}	    			 
    			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode);
    				if(status && status.value == 'UNPOST'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode);
    				if(status && status.value == 'POSTED'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpAutoInvoiceButton': {
    			click: function(btn){
    				var abcode=Ext.getCmp('ab_code').value;
    				var abdate=Ext.getCmp('ab_date').value;
    				Ext.Ajax.request({
    			   		url : basePath + 'fa/ars/createVoucherARO.action',
    			   		params: {
    			   			abcode: abcode,
    			   			abdate: abdate
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.FormUtil.getActiveTab().setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
    		    				turnSuccess(function(){
    		    					//var id = localJson.id;
    		    					//var url = "jsps/scm/purchase/purchasePrice.jsp?formCondition=pp_id=" + id + 
    		    						//"&gridCondition=ppd_ppid=" + id;
    		    					//me.FormUtil.onAdd('PurchasePrice' + id, '物料核价单' + id, url);
    		    				});
    			   			}
    			   		}
    				});
    				}
    		},
    		'erpCopyButton': {
    			click: function(btn) {
    				this.copy();
    			}
    		},
    		'erpConfirmXJSKButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode),
    					pricekind = Ext.getCmp('ab_pricekind');
    				if(status && status.value == 'POSTED'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    				if(pricekind && pricekind.value == '现金收款'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var aramount = Ext.getCmp('ab_aramount').value,
						payamount = Ext.getCmp('ab_payamount').value;
					if(Ext.isEmpty(aramount) || aramount == 0){
						showError('当前发票应收金额为0，不能确认收款！');
			            return;
					}
					if(payamount != 0){
						showError('当前发票已收款，不能确认收款！');
			            return;
					}
    				warnMsg("确定现金收款?", function(btn){
	    				if(btn == 'yes'){
    						me.FormUtil.setLoading(true);//loading...
    						Ext.Ajax.request({
    							url : basePath + 'fa/ars/confirmXJSK.action',
    							params: {
    								id: Ext.getCmp('ab_id').value,
    								catecode:Ext.getCmp('ab_catecode').value
    							},
    							method : 'post',
    							callback: function(opt, s, r) {
    								me.FormUtil.setLoading(false);
    								var rs = Ext.decode(r.responseText);
    								if(rs.exceptionInfo) {
    									showError(rs.exceptionInfo);
    								} else {
    									if(rs.log)
    										showMessage('提示', rs.log);
    								}
    							}
    						});
    					}
    				});	
    			}
    		},
    		'erpConfirmYJSKButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode),
    					pricekind = Ext.getCmp('ab_pricekind');
    				if(status && status.value == 'POSTED'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    				if(pricekind && pricekind.value == '样机收款'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var aramount = Ext.getCmp('ab_aramount').value,
						payamount = Ext.getCmp('ab_payamount').value;
    				if(Ext.isEmpty(aramount) || aramount == 0){
						showError('当前发票应收金额为0，不能确认收款！');
			            return;
					}
					if(payamount != 0){
						showError('当前发票已收款，不能确认收款！');
			            return;
					}
    				warnMsg("确定样机收款?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.setLoading(true);//loading...
    						Ext.Ajax.request({
    							url : basePath + 'fa/ars/confirmYJSK.action',
    							params: {
    								id: Ext.getCmp('ab_id').value
    							},
    							method : 'post',
    							callback: function(opt, s, r) {
    								me.FormUtil.setLoading(false);
    								var rs = Ext.decode(r.responseText);
    								if(rs.exceptionInfo) {
    									showError(rs.exceptionInfo);
    								} else {
    									if(rs.log)
    										showMessage('提示', rs.log);
    								}
    								window.location.reload();
    							}
    						});
    					}
    				});	
    			}
    		},
    		'erpCancelSKButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode),
    					pricekind = Ext.getCmp('ab_pricekind');
    				if(status && status.value == 'POSTED'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    				if(pricekind && pricekind.value){
    					btn.show();
    				} else {
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("取消收款?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.setLoading(true);//loading...
    						Ext.Ajax.request({
    							url : basePath + 'fa/ars/cancelSK.action',
    							params: {
    								id: Ext.getCmp('ab_id').value
    							},
    							method : 'post',
    							callback: function(opt, s, r) {
    								me.FormUtil.setLoading(false);
    								var rs = Ext.decode(r.responseText);
    								if(rs.exceptionInfo) {
    									showError(rs.exceptionInfo);
    								} else {
    									if(rs.log)
    										showMessage('提示', rs.log);
    								}
    								window.location.reload();
    							}
    						});
    					}
    				});	
    			}
    		},
    		'erpTicketTaxesButton': {
    			click: function(btn){
    				var me = this, win = Ext.getCmp('ticketTaxes-win');
  				   	if(!win){
  					   var ab_refno = Ext.getCmp('ab_refno').value, ab_remark = Ext.getCmp('ab_remark').value,
  					   win = Ext.create('Ext.Window', {
  						   id: 'ticketTaxes-win',
  						   title: '更新税票信息',
  						   height: 200,
  						   width: 400,
  						   items: [{
  							   xtype: 'form',
  							   height: '100%',
  							   width: '100%',
  							   bodyStyle: 'background:#f1f2f5;',
  							   items: [{
  								   margin: '10 0 0 0',
  								   xtype: 'textfield',
  								   fieldLabel: '税票编号',
  								   name:'ab_refno',
  								   value: ab_refno
  							   },{
  								   margin: '10 0 0 0',
  								   xtype: 'textfield',
  								   fieldLabel: '备注',
  								   name:'ab_remark',
  								   value: ab_remark
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
  									   a = form.down('textfield[name=ab_refno]'),
  									   b = form.down('textfield[name=ab_remark]');
  									   if(form.getForm().isDirty()) {
  										   me.updateTaxcode(Ext.getCmp('ab_id').value, a.value, b.value);
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
    		'numberfield[name=ab_aramount]':{
    			beforerender:function(num){
    				num.minValue = Number.NEGATIVE_INFINITY;
    				num.setMinValue(num.minValue);
    				b = num.baseChars+"";
    				b += num.decimalSeparator;
    				b += "-";
    				b = Ext.String.escapeRegex(b);
    				num.maskRe = new RegExp("[" + b + "]");
    			}
    		},
    		'dbfindtrigger[name=ab_paymentcode]': {
    			afterrender:function(trigger){
    				if(trigger.fieldConfig == 'PT') {
    	    			trigger.dbKey='ab_custcode';
    	    			trigger.mappingKey='cu_code';
    	    			trigger.dbMessage='请先选客户编号！';	
    				}
    			}
    		},
    		'field[name=ab_catecode]':{
    			afterrender:function(field){
    				if(caller =='ARBill!IRMA'){
    					field.setReadOnly(false);
    				}
    				
    			}
    		},
    		'dbfindtrigger[name=abd_pocode]': {
    			focus: function(t){
    				if(caller =='ARBill!OTRS'){
    					t.setHideTrigger(false);
	    				t.setReadOnly(false);//用disable()可以，但enable()无效
	    				var custcode = Ext.getCmp('ab_custcode').value;
	    				if(custcode != null && custcode != ''){
	    					t.dbBaseCondition = "sa_custcode='" + custcode + "'";
	    				}
    				}
    				
    			}
    		},
    		'field[name=ab_payments]': {
    			/*change: function(f){
    				if(f.value == null || f.value == ''){
    					f.value = 0;
    				}
    				if(Ext.getCmp('ab_date')){
    					var date = Ext.util.Format.date(Ext.getCmp('ab_date').value, 'Y-m-d');// 格式化日期控件值
    		            var payments= Ext.util.Format.date(Ext.getCmp('ab_payments').value, 'Y-m-d');// 格式化日期控件值
    		            var date = new Date(date); 
    		            var payments = new Date(payments); 
    		            var paydate = date + payments;
    					Ext.getCmp('ab_paydate').setValue(paydate);
    				}
    			}*/
    		},
    		'dbfindtrigger[name=abd_catecode]': {
    			aftertrigger: function(f){
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected;
    				var type = record.get('ca_asstype'),name = record.get('ca_assname'), ass = record.get('ass') || [];
    				if(!Ext.isEmpty(type)){
    					var oldType = Ext.Array.concate(ass, '#', 'dass_asstype');
    					if(type != oldType) {
    						var idx = me.getRecordIndex(grid, record),dd = [],names = name.split('#');
        					Ext.Array.each(type.split('#'), function(t,index){
        						dd.push({
        							dass_condid: idx,
        							dass_asstype: t,
        							dass_assname: names[index]
        						});
        					});
        					record.set('ass', dd);
        					var view = grid.view, idx = grid.store.indexOf(record), rowNode = view.getNode(idx),
        						expander = grid.plugins[2], row = Ext.fly(rowNode, '_rowExpander'), 
        						isCollapsed = row.hasCls(expander.rowCollapsedCls);
        					if(isCollapsed)
        						expander.toggleRow(idx, record);
    					}
    				} else
    					record.set('ass', null);
    			}
    		},
    		'erpModifyCommonButton': {
    			afterrender:function(btn){
					var form=Ext.getCmp('form');
					var statuscodeField=form.statuscodeField;
					var status = Ext.getCmp(statuscodeField);
					var postStatus = Ext.getCmp('ab_statuscode');
					if((status && status.value!= 'ENTERING')||(postStatus && postStatus.value== 'POSTED')){
						btn.show();
					}
				}
    		},
    		'field[name=ab_differ]':{/*
    			change:function(field){
    				if(Ext.getCmp('ab_differ')){
    					var sum = 0;
    					var v_differ = Ext.isNumber(Ext.getCmp('ab_differ').getValue())?Ext.getCmp('ab_differ').getValue():'0';
    					var grid = Ext.getCmp('grid');
    					Ext.each(grid.store.data.items,function(item,index){
    						if(item.dirty && me.GridUtil.isBlank(grid, item.data)){
    							sum += Number(item.data['abd_taxamount']);
    						}
    					});

    					sum += Number(v_differ);
    					Ext.getCmp('ab_taxamount').setValue(sum);
    				}
    			}
    			
    		*/}
    	});
    }, 
    
    //此CALLER为  应收发票维护界面  修改单据需要把明细行中开票数据还原
//	//在此做还原操作
    onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSaveARBill: function(){
		if(caller =='ARBill!IRMA'){
			var date_s = Ext.getCmp('ab_date').rawValue;
			var date_str = date_s.replace("-","");
			if(date_str.length>=6){
				date_str = date_str.substring(0,6);
			}else{
				var myDate = new Date();
				var dateString = Ext.Date.format(myDate,'Ymd');
				date_str = dateString.substring(0,6);
			}
			
			Ext.getCmp('ab_yearmonth').setValue(date_str);
		}
		this.BaseUtil.getPaydate('ab_paymentmethodid','ab_date','ab_plancollection');
		//保存ARBill
		/*if(bool)*/
			this.beforeSave(this);
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var param2 = new Array();
		detail.store.each(function(record){
			if(record.get('ca_assname')) {
				var s = record.get('ass') || [];
				Ext.Array.each(s, function(t, i){
					t.dass_id = t.dass_id || 0;
					t.dass_condid = String(t.dass_condid);
					param2.push(t);
				});
			}
		});
		
		var param1 = me.GridUtil.getGridStore(detail);
		me.onSave(form, param1, param2);
	},
	onSave: function(form, param1, param2){
		var me = this;
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
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
	 			   
	 		});
			me.FormUtil.save(r, param1, param2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	beforeUpdateARBill: function(){
		var grid = Ext.getCmp('grid');
		if(caller =='ARBill!IRMA'){
			var date_s = Ext.getCmp('ab_date').rawValue;
			var date_str = date_s.replace("-","");
			if(date_str.length>=6){
				date_str = date_str.substring(0,6);
			}else{
				var myDate = new Date();
				var dateString = Ext.Date.format(myDate,'Ymd');
				date_str = dateString.substring(0,6);
			}
			Ext.getCmp('ab_yearmonth').setValue(date_str);
		}
		this.BaseUtil.getPaydate('ab_paymentmethodid','ab_date','ab_plancollection');
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('abd_abid',Ext.getCmp('ab_id').value);
		});
		//采购价格不能为0
		var bool = true;
		//保存
		if(bool)
			this.beforeUpdate();
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		var detail = Ext.getCmp('grid');
		var param2 = new Array();
		detail.store.each(function(record){
			if(record.get('ca_assname')) {
				var s = record.get('ass') || [];
				Ext.Array.each(s, function(t, i){
					t.dass_id = t.dass_id || 0;
					t.dass_condid = String(t.dass_condid);
					param2.push(t);
				});
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		if(me.FormUtil.checkFormDirty(form) == '' && detail.necessaryField.length > 0 && (param1.length == 0)
				&& param2.length == 0){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
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
		 		});
				me.FormUtil.update(r, param1, param2);
			}else{
				me.FormUtil.checkForm();
			}
		}
	},
	updateTaxcode: function(abid, val1, val2) {
		var me = this;
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/updateARBillTaxcode.action',
    	   	params: {
    	   		caller: caller,
	    		ab_id: abid,
	    		ab_refno: val1,
	    		ab_remark: val2
    	   	},
	    	callback: function(opt, s, r) {
	    		var rs = Ext.decode(r.responseText);
		   		if(rs.exceptionInfo) {
		   			showError(rs.exceptionInfo);
		   		} else {
		   			alert('更新成功!');
		   			window.location.reload();
		   		}
   			}
    	});
	},
	copy: function(){
	 	var me = this;
		var form = Ext.getCmp('form');
		var v = form.down('#ab_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/ars/copyARBill.action',
				params: {
					id: v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.ab) {
						turnSuccess(function(){
	    					var id = res.ab.ab_id;
	    					var url = "jsps/fa/ars/arbill.jsp?formCondition=ab_idIS" + 
								 + id + "&gridCondition=abd_abidIS" + id + "&whoami="+caller;
	    					me.FormUtil.onAdd('arbill' + id, '应收' + id, url);
	    				});
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	getRecordIndex: function(grid, record) {
		var me = this, id = record.get(grid.keyField);
		if(!id || id == 0) {
			me.rowCounter = me.rowCounter || 0;
			id = --me.rowCounter;
			record.set(grid.keyField, id);
		}
		return id;
	}
});