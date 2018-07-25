Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.PrePay', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.arp.PrePay','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.SeparNumber',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Submit','core.button.ResAudit','core.button.Print',
  			'core.button.Audit','core.button.ResSubmit','core.button.Post','core.button.ResPost','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted','core.button.StrikeBalance',
      		'core.grid.AssPanel','core.window.AssWindow','core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.button.Print','core.button.AssDetail','core.button.AssMain'
      		,'core.button.GetSumAmount','core.button.ConfirmPayB2c'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick(selModel, record);
        			var btn = Ext.getCmp('assdetail');
    				var ass = record.data['ca_asstype'];
    				if(!Ext.isEmpty(ass)){
    					btn.setDisabled(false);
    				} else {
    					btn.setDisabled(true);
    				}
    			} 
    		},
    		'field[name=pp_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pp_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=ca_assname]':{
    			change: function(m){
	    			if(Ext.isEmpty(m)){
	    				Ext.getCmp('assmainbutton').setDisabled(true);
	    			}else{
	    				Ext.getCmp('assmainbutton').setDisabled(false);
    				}
    			}
    		},
    		'field[name=pp_amount]':{
				beforerender: function(field){
					if(Ext.getCmp('pp_sourcecode') && Ext.getCmp('pp_sourcecode').value!=""){
						if(Ext.getCmp('pp_source') && Ext.getCmp('pp_source').value == '预付款申请'){
							field.readOnly=false;
						} else {
							field.readOnly=true;
						}
					}
				}
			},
			'field[name=pp_date]':{
				beforerender: function(field){
					if(Ext.getCmp('pp_source') && (Ext.getCmp('pp_source').value=="Bank" || Ext.getCmp('pp_source').value=="应付票据" || Ext.getCmp('pp_source').value=="应付票据退票" || Ext.getCmp('pp_source').value=="背书转让")){
						field.readOnly=true;
					}
				}
			},'erpConfirmPayB2cButton':{
				click: function(btn){
		    		var pp_id = Ext.getCmp('pp_id').value;
    				me.FormUtil.setLoading(true);
        			Ext.Ajax.request({
        				url: basePath + 'b2c/buyer/confirmPayB2c.action',
        				params: {
        					pp_id: pp_id
        				},
        				timeout: 600000,
        				callback: function(opt, s, r) {
        					me.FormUtil.setLoading(false);
        					var res = Ext.decode(r.responseText);
        					if(res.success) {
        						if(res.log){
        							showError(res.log);
        						}else{
        							showMessage('提示', '确认付款成功');
        						}
        					} else if(res.exceptionInfo) {
        						showError(res.exceptionInfo);
        					}
        				}
        			});
		    	}
			},
    		'erpAssMainButton':{
    			afterrender:function(btn){
    				if(Ext.getCmp('ca_assname').getValue()==null||Ext.getCmp('ca_assname').getValue()==""){
    					btn.setDisabled(true);
    				}else{
    					btn.setDisabled(false);
    				}
    			}
    		},
    		'erpGetSumAmountButton':{
    			click: function(btn){
    				var grid = Ext.getCmp("grid"),items = grid.store.data.items;
    				var detailamount1 = 0;
    	    		Ext.each(items,function(item,index){
    	    			if(!me.GridUtil.isBlank(grid,item.data)) {
    	    				detailamount1 = detailamount1 + Number(item.data['ppd_nowbalance']);
    	    			}
    	    		});
    	    		Ext.getCmp("pp_amount").setValue(detailamount1);
    	    		Ext.getCmp("pp_vmamount").setValue(detailamount1);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('PrePay');
    				}
    				me.changeVmrateOrVmamount();
    				me.getJsamount();
    				//保存之前的一些前台的逻辑判定
    				this.beforeSaveAccountRegister();
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				//辅助核算
    				btn.ownerCt.add({
    					xtype:'erpAssDetailButton',
    					disabled:true
    				});
    			},
    			beforedelete:function(data,record,btn){
    				if(record.get('pbd_pbid') !=null){
    					btn.canDelete=false;
    					showError('当前行存在来源不允许修改!');
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				var source = Ext.getCmp('pp_source');
    				if(source && source.value && source.value != '预付款申请' && source.value != '采购单'){
    					showError("请在来源:"+source.value+",单号："+Ext.getCmp('pp_sourcecode').value+"中进行反审核或者反记账操作！");
    					return;
    				}
    				me.FormUtil.onDelete(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpPostButton' : {
    			afterrender: function(btn){
    		        var status = Ext.getCmp('pp_statuscode');
    		        if(status && status.value != 'UNPOST'){
    		        	btn.hide();
    		        }
    		    },
    		    click: {
    				fn:function(btn){
        				var amount = Number(Ext.getCmp('pp_amount').getValue());
	    	    		var cmamount =Number(Ext.getCmp('pp_vmamount').getValue());
	    	    		var cmcurrency = Ext.getCmp('pp_vmcurrency').getValue();
	    	    		var cmrate = Number(Ext.getCmp('pp_vmrate').getValue());
	    	    		var currency = Ext.getCmp('pp_currency').getValue();
	    	    		if(amount != 0 && cmamount != 0){
		    	    		if(Ext.Number.toFixed(cmrate, 8)!= Ext.Number.toFixed(cmamount/amount, 8)){
		    	    			showError('冲账汇率不正确,不能过账!');
		    					return;
		    				}
	    	    		}
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
	    				me.FormUtil.onPost(Ext.getCmp('pp_id').value);
        			},
        			lock:2000
    			}
    		},
    		'erpResPostButton' : {
    			afterrender: function(btn){
    		        var status = Ext.getCmp('pp_statuscode');
    		        if(status && status.value != 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
    			var reportName='';
    		    reportName="APPrePay";
				var condition='{PrePay.pp_id}='+Ext.getCmp('pp_id').value+'';
				var id=Ext.getCmp('pp_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.changeVmrateOrVmamount();
    				me.getJsamount();
    				me.beforeUpdate();
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
    				me.FormUtil.onAdd('addPrePay', title, 'jsps/fa/arp/prepay.jsp?whoami='+caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        	poststatus = Ext.getCmp('pp_statuscode');
    		        if(status && status.value != 'ENTERING'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				var amount = Number(Ext.getCmp('pp_amount').getValue());
    	    		var cmamount =Number(Ext.getCmp('pp_vmamount').getValue());
    	    		var cmcurrency = Ext.getCmp('pp_vmcurrency').getValue();
    	    		var cmrate = Number(Ext.getCmp('pp_vmrate').getValue());
    	    		var currency = Ext.getCmp('pp_currency').getValue();
    	    		if(amount != 0 && cmamount != 0){
	    	    		if(Ext.Number.toFixed(cmrate, 8)!= Ext.Number.toFixed(cmamount/amount, 8)){
	    	    			showError('冲账汇率不正确,不能提交!');
	    					return;
	    				}
    	    		}
    	    		if(currency == cmcurrency){
    					if(cmrate != 1){
    						showError('币别相同，冲账汇率不等于1,不能提交!');
    						return;
    					}
    				}
    	    		if(currency != cmcurrency){
    					if(cmrate == 1){
    						showError('币别不相同，冲账汇率等于1,不能提交!');
    						return;
    					}
    				}
    				me.FormUtil.onSubmit(Ext.getCmp('pp_id').value, false, this.beforeUpdate, this);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        poststatus = Ext.getCmp('pp_statuscode');
    		        if(status && status.value != 'COMMITED'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
	    		click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pp_id').value);
	    		}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        poststatus = Ext.getCmp('pp_statuscode');
    		        if(status && status.value != 'COMMITED'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        	postStatus = Ext.getCmp('pp_statuscode');
    		        if((status && status.value != 'AUDITED') ||(postStatus && postStatus.value == 'POSTED')){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pp_id').value);
    			}
    		},
    		'cateTreeDbfindTrigger[name=pp_accountcode]':{
    			aftertrigger:function(trigger, data){
    				Ext.getCmp('pp_accountname').setValue(data[0].raw.data.ca_name);
    				var asstype = data[0].raw.data.ca_asstype;
    				var btn = Ext.getCmp("assmainbutton");
    				if (Ext.isEmpty(asstype)){
    					btn.setDisabled(true);
    				} else {
    					btn.setDisabled(false);
    				}
    			}
    		},
    		'field[name=ppd_ordercode]':{
    			afterrender:function(t){
    				t.gridKey="pp_vendcode|pp_vmcurrency";
    				t.mappinggirdKey="pu_receivecode|pu_currency";
    				t.gridErrorMessage="请先选择供应商|请选择冲账币别";
    			}
    		},
    		'field[name=ppd_makecode]':{
    			afterrender:function(t){
    				t.gridKey="pp_vendcode|pp_vmcurrency";
    				t.mappinggirdKey="nvl(ma_apvendcode,ve_apvendcode)|ma_currency";
    				t.gridErrorMessage="请先选择供应商|请选择冲账币别";
    			}
    		},
    		'textfield[name=pp_amount]':{
    			change: function(){
    				me.changeVmrateOrVmamount();
    			}
    		},
    		'textfield[name=pp_vmrate]':{
    			change: function(){
    				me.changeVmrateOrVmamount();
    			}
    		},
    		'textfield[name=pp_vmamount]':{
    			change: function(){
    				me.changeVmrateOrVmamount();
    			}
    		}
    	});
    }, 
    changeVmrateOrVmamount: function () {
    	var me = this;
    	if(Ext.getCmp('pp_vmrate').readOnly){
    		me.changeVmrate();
    	}else{
    		me.changeVmamount();
    	}
    },
    
    changeVmrate:function () {
		if(Ext.getCmp('pp_vmamount')&&Ext.getCmp('pp_amount')&&Ext.getCmp('pp_vmrate')){
			var vmamount = Ext.getCmp('pp_vmamount').getValue();;
			var amount = Ext.getCmp('pp_amount').getValue();
			
			var rate  = Number(vmamount/amount);
			rate = Ext.Number.toFixed(rate,8);
			Ext.getCmp('pp_vmrate').setValue(rate);
		}
    	
    	
    	
    },
    //计算冲账金额  = 预收金额*冲账汇率
    changeVmamount:function(){
    	if(Ext.getCmp('pp_amount')&&Ext.getCmp('pp_vmrate')){
    		var ppamount = Ext.getCmp('pp_amount').getValue();
    		var ppvmrate = Ext.getCmp('pp_vmrate').getValue();
    		if(!Ext.isNumber(ppamount)){
    			ppamount = Ext.Number.from(ppamount,0);
    		}
    		if(!Ext.isNumber(ppvmrate)){
    			ppvmrate = Ext.Number.from(ppvmrate,0);
    		}
    		var vmamount = ppamount*ppvmrate;
    		Ext.getCmp('pp_vmamount').setValue(Ext.util.Format.number(vmamount,'0.00'));
    	}
    },
	getJsamount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var jsamount = 0;
		var detailcount = 0;
		var cateamount = 0;
		Ext.each(items,function(item,index){

			if(item.data['ppd_catecode']!=null&&item.data['ppd_catecode']!=""){
				cateamount = cateamount + Number(item.data['ppd_nowbalance']);
			}
		});
		Ext.getCmp('pp_jsamount').setValue(Ext.Number.from(Ext.getCmp('pp_vmamount').getValue())-cateamount);
	},
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
	beforeSaveAccountRegister: function(){
		var me = this;
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		var warnStr='';
		var form = Ext.getCmp('form');
		Ext.each(items,function(item,index){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
				var orderamount =item.data['ppd_orderamount'];	//订单金额
				var havebalance = item.data['ppd_havebalance'];	//已预付金额
				var nowbalance = item.data['ppd_nowbalance'];	//本次预付额
				//订单金额>=已预收金额+本次预收额
				if(form.BaseUtil.numberFormat(havebalance+nowbalance, 2) > form.BaseUtil.numberFormat(orderamount, 2)){
					var i = index+1;
					if(warnStr ==''||warnStr.length<=0){
						warnStr = warnStr +i;
					}else{
						warnStr = warnStr +','+i;
					}
					bool = false;
				}
			}
		});
		if(bool){
			this.beforeSave(this);
		}else{
			if(warnStr != ''||warnStr.length>0){
				showError('明细表第'+warnStr+'行 已预收金额与本次预收额的合计超过订单金额,不能保存');return;
			}
		}
	},
	beforeSave:function(){
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
			Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
				Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
					d['ass_conid'] = key;
					param3.push(d);
				});
			});
		}
		Ext.each(detail.store.data.items, function(item){
			if(item.data.prd_id == null || item.data.prd_id == 0){
				item.data.prd_id = -item.index;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		}
		me.onSave(form, param1, param2,param3);
	},
	onSave: function(form, param1, param2,param3) {
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
			var r = form.getValues();
			form.getForm().getFields().each(function(){
				if(this.logic == 'ignore') {
					delete r[this.name];
				}
			});
			me.FormUtil.save(r, param1, param2,param3);
		}else{
			me.FormUtil.checkForm();
		}
	},
	beforeUpdate: function(){
		var me = this;
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
	    Ext.Array.each(items, function(item){
	    	item.set('ppd_ppid',Ext.getCmp('pp_id').value);
		});
		//采购价格不能为0
		var bool = true;
		var warnStr='';
		var form = Ext.getCmp('form');
		Ext.each(items,function(item, index){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
				var orderamount =item.data['ppd_orderamount'];	//订单金额
				var havebalance = item.data['ppd_havebalance'];	//已预付金额
				var nowbalance = item.data['ppd_nowbalance'];	//本次预付额
				//订单金额>=已预收金额+本次预收额
				if(form.BaseUtil.numberFormat(havebalance+nowbalance, 2) > form.BaseUtil.numberFormat(orderamount, 2)){
					var i = index+1;
					if(warnStr ==''||warnStr.length<=0){
						warnStr = warnStr +i;
					}else{
						warnStr = warnStr +','+i;
					}
					bool = false;
				}
			}
		});
		if(bool){
			me.onUpdate(this);
		}else{
			if(warnStr != ''||warnStr.length>0){
				showError('明细表第'+warnStr+'行 已预付金额与本次预付额的合计超过订单金额,不能保存');return;
			}
		}
	},
	onUpdate: function(){
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
			Ext.each(Ext.Object.getKeys(Ext.getCmp('assmainbutton').cacheStoreForm), function(key){
				Ext.each(Ext.getCmp('assmainbutton').cacheStoreForm[key], function(d){
					d['ass_conid'] = key;
					param3.push(d);
				});
			});
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
				var r = form.getValues();
				form.getForm().getFields().each(function(){
					if(this.logic == 'ignore') {
						delete r[this.name];
					}
				});
				me.FormUtil.update(r, param1, param2, param3);
			}else{
				me.FormUtil.checkForm();
			}
		}
	}
});