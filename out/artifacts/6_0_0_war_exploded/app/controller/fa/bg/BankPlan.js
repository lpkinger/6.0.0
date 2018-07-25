Ext.QuickTips.init();
Ext.define('erp.controller.fa.bg.BankPlan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.bg.BankPlan','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
      			'core.button.Post','core.button.ResPost','core.button.AutoInvoice',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.MonthDateField',
      			'core.trigger.CateTreeDbfindTrigger'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick(selModel, record);
    				
    				
        			
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    			
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				//保存之前的一些前台的逻辑判定
    				this.beforeSaveAPBill();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bp_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdateAPBill();
    				
    				
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
    				me.FormUtil.onAdd('addBankPlan', title, 'jsps/fa/ars/bankplan.jsp?whoami='+caller);
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
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('bp_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bp_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
    				if(auditStatus && auditStatus.value != 'COMMITED'){
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
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ab_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var reportName="PurcInvoice";
        			var id=Ext.getCmp('bp_id').value;
        			var condition = '{APBill.bp_id}=' + Ext.getCmp('bp_id').value;
        			console.log(condition);
        			me.FormUtil.onwindowsPrint(id, reportName, condition);
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
    				me.FormUtil.onPost(Ext.getCmp('bp_id').value);
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
    				var abcode=Ext.getCmp('bp_code').value;
    				var abdate=Ext.getCmp('bp_date').value;
    				Ext.Ajax.request({
    			   		url : basePath + 'fa/ars/createVoucherAPO.action',
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
    		'numberfield[name=ab_apamount]':{
    			beforerender:function(num){
    				num.minValue = Number.NEGATIVE_INFINITY;
    				num.setMinValue(num.minValue);
    				b = num.baseChars+"";
    				b += num.decimalSeparator;
    				b += "-";
    				b = Ext.String.escapeRegex(b);
    				num.maskRe = new RegExp("[" + b + "]");
    			}
    		}
    	});
    }, 
    
    //此CALLER为  应收发票维护界面 删除单据需要把明细行中开票数据还原
	//在此做还原操作
    
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
	beforeSaveAPBill: function(){
		if(caller =='APBill!CWIM'){
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
		this.BaseUtil.getPaydate('ab_paymentsmethodid','ab_date','ab_paydate');
		
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
		var param3 = new Array();
		
		if(Ext.getCmp('assdetail')){
			Ext.each(Ext.Object.getKeys(Ext.getCmp('assdetail').cacheStoreGrid), function(key){
				Ext.each(Ext.getCmp('assdetail').cacheStoreGrid[key], function(d){
					d['dass_condid'] = key;
					param2.push(d);
				});
			});
		}

		if(Ext.getCmp('assmainbutton')){
//			Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
//				Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
//					d['ass_conid'] = key;
//					param3.push(d);
//				});
//			}
		}
		Ext.each(detail.store.data.items, function(item){
			if(item.data.prd_id == null || item.data.prd_id == 0){
				item.data.prd_id = -item.index;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		me.onSave(form, param1, param2,param3);
	},
	onSave: function(form, param1, param2,param3){
		var me = this;
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
		param3 = param3 == null ? [] : Ext.encode(param3).replace(/\\/g,"%");
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			me.FormUtil.save(form.getValues(), param1, param2,param3);
		}else{
			me.FormUtil.checkForm();
		}
	},
	beforeUpdateAPBill: function(){
		var grid = Ext.getCmp('grid');
		if(caller =='APBill!CWIM'){
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
		this.BaseUtil.getPaydate('ab_paymentsmethodid','ab_date','ab_paydate');
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('abd_abid',Ext.getCmp('ab_id').value);
		});
		var bool = true;
	
		//保存
		if(bool)
			this.beforeUpdate(this);
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		var detail = Ext.getCmp('grid');
		Ext.each(detail.store.data.items, function(item){
			if(item.data.prd_id == null || item.data.prd_id == 0){
				item.data.prd_id = -item.index;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = new Array();
		var param3 = new Array();
		if(Ext.getCmp('assdetail')){
			Ext.each(Ext.Object.getKeys(Ext.getCmp('assdetail').cacheStoreGrid), function(key){
				Ext.each(Ext.getCmp('assdetail').cacheStoreGrid[key], function(d){
					d['dass_condid'] = key;
					param2.push(d);
				});
			});
		}

		if(Ext.getCmp('assmainbutton')){
//			Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
//				Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
//					d['ass_conid'] = key;
//					param3.push(d);
//				});
//			}
		}
		if(me.FormUtil.checkFormDirty(form) == '' && detail.necessaryField.length > 0 && (param1.length == 0)
				&& param2.length == 0&& param3.length == 0){
			showError($I18N.common.grid.emptyDetail);
			return;
		} else {
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : Ext.encode(param2).replace(/\\/g,"%");
			param3 = param3 == null ? [] : Ext.encode(param3).replace(/\\/g,"%");
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				me.FormUtil.update(form.getValues(), param1, param2,param3);
			}else{
				me.FormUtil.checkForm();
			}
		}
		
	},
	beforePostAPBill:function(){
		
	}
});