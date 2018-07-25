Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.PreRec', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.PreRec','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.SeparNumber',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Submit','core.button.ResAudit',
  			'core.button.Audit','core.button.ResSubmit','core.button.Post','core.button.ResPost','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain',
      		'core.button.Accounted','core.button.StrikeBalance','core.button.SellerPreRec','core.trigger.MultiDbfindTrigger',
      		'core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.button.Print','core.button.AssDetail','core.button.GetSumAmount', 'core.form.SeparNumber'
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
    		'field[name=pr_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pr_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpGetSumAmountButton':{
    			click: function(btn){
    				var grid = Ext.getCmp("grid"),items = grid.store.data.items;
    				var detailamount1 = 0;
    	    		Ext.each(items,function(item,index){
    	    			if(!me.GridUtil.isBlank(grid,item.data)) {
    	    				detailamount1 = detailamount1 + Number(item.data['prd_nowbalance']);
    	    			}
    	    		});
    	    		Ext.getCmp("pr_amount").setValue(detailamount1);
    	    		Ext.getCmp("pr_cmamount").setValue(detailamount1);
    			}
    		},
    		'erpAssMainButton':{
    			afterrender:function(btn){
    				
    				if(Ext.getCmp('ca_asstype').getValue()==null||Ext.getCmp('ca_asstype').getValue()==""){
    					btn.setDisabled(true);
    				}else{
    					btn.setDisabled(false);
    				}
    			}
    		
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('PreRec');
    				}
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
    			}
    		},
    		'field[name=pr_amount]':{
				beforerender: function(field){
					if(Ext.getCmp('pr_source')&&Ext.getCmp('pr_source').value!=""){
						field.readOnly=true;
					}
				}
			},
			'field[name=pr_date]':{
				beforerender: function(field){
					if(Ext.getCmp('pr_source') && (Ext.getCmp('pr_source').value=="Bank" || Ext.getCmp('pr_source').value=="应收票据" || Ext.getCmp('pr_source').value=="应收票据退票" || Ext.getCmp('pr_source').value=="背书转让")){
						field.readOnly=true;
					}
				}
			},
    		'erpDeleteButton' : {
    			click: function(btn){
    				var source = Ext.getCmp('pr_source');
    				if(source && source.value && source.value != '业务员转预收'){
    					showError("请在来源:"+source.value+",单号："+Ext.getCmp('pr_sourcecode').value+"中进行反审核或者反记账操作！");
    					return;
    				}
    				me.FormUtil.onDelete(Ext.getCmp('pr_id').value);
    			}
    		},
    		'erpPostButton' : {
    			afterrender: function(btn){
    		        var status = Ext.getCmp('pr_statuscode');
    		        if(status && status.value != 'UNPOST'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				var amount = Number(Ext.getCmp('pr_amount').getValue());
    	    		var cmamount =Number(Ext.getCmp('pr_cmamount').getValue());
    	    		var cmcurrency = Ext.getCmp('pr_cmcurrency').getValue();
    	    		var cmrate = Number(Ext.getCmp('pr_cmrate').getValue());
    	    		var currency = Ext.getCmp('pr_currency').getValue();
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
    				me.FormUtil.onPost(Ext.getCmp('pr_id').value);
    			}
    		},
    		'erpResPostButton' : {
    			afterrender: function(btn){
    		        var status = Ext.getCmp('pr_statuscode');
    		        if(status && status.value != 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('pr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
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
    				me.FormUtil.onAdd('addPreRec', title, 'jsps/fa/ars/preRec.jsp?whoami='+caller);
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
    		        	poststatus = Ext.getCmp('pr_statuscode');
    		        if(status && status.value != 'ENTERING'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				var amount = Ext.Number.toFixed(Number(Ext.getCmp('pr_amount').getValue()), 2);
    	    		var cmamount =Ext.Number.toFixed(Number(Ext.getCmp('pr_cmamount').getValue()), 2);
    	    		var cmcurrency = Ext.getCmp('pr_cmcurrency').getValue();
    	    		var cmrate = Number(Ext.getCmp('pr_cmrate').getValue());
    	    		var currency = Ext.getCmp('pr_currency').getValue();
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
    	    		me.FormUtil.onSubmit(Ext.getCmp('pr_id').value, false, this.beforeUpdate, this);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        poststatus = Ext.getCmp('pr_statuscode');
    		        if(status && status.value != 'COMMITED'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pr_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        poststatus = Ext.getCmp('pr_statuscode');
    		        if(status && status.value != 'COMMITED'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        	postStatus = Ext.getCmp('pr_statuscode');
    		        if((status && status.value != 'AUDITED') ||(postStatus && postStatus.value == 'POSTED')){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pr_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var reportName="PreRec_app";
    				var id=Ext.getCmp('pr_id').value;
					var condition='{PreRec.pr_id}='+id+'';
					me.FormUtil.onwindowsPrint(id,reportName,condition);
    			}
    		},
    		'textfield[name=pr_amount]':{
    			change: me.changeCmamount
    		},
    		'textfield[name=pr_cmrate]':{
    			change: me.changeCmamount
    		},
    		'cateTreeDbfindTrigger[name=pr_accountcode]':{
    			aftertrigger:function(trigger, data){
    				Ext.getCmp('pr_accountname').setValue(data[0].raw.data.ca_name);
    				var asstype = data[0].raw.data.ca_asstype;
    				var btn = Ext.getCmp("assmainbutton");
    				if(Ext.isEmpty(asstype)){
    					btn.setDisabled(true);
    				}else{
    					btn.setDisabled(false);
    				}
    			}
    		},
    		'field[name=prd_ordercode]': {
    			afterrender: function(t){
    				t.gridKey="pr_custcode|pr_cmcurrency";
    				t.mappinggirdKey="sa_apcustcode|sa_currency";
    				t.gridErrorMessage="请先选择客户|请选择冲账币别";
    			}
    		},
    		'textfield[name=pr_amount]':{
    			change: function(){
    				me.changeCmrateOrCmamount();
    			}
    		},
    		'textfield[name=pr_cmrate]':{
    			change: function(){
    				me.changeCmrateOrCmamount();
    			}
    		},
    		'textfield[name=pr_cmamount]':{
    			change: function(){
    				me.changeCmrateOrCmamount();
    			}
    		}
    	});
    }, 
    changeCmrateOrCmamount: function () {
    	var me = this;
    	if(Ext.getCmp('pr_cmrate').readOnly){
    		me.changeCmrate();
    	}else{
    		me.changeCmamount();
    	}
    },
    changeCmrate:function () {
		if(Ext.getCmp('pr_cmamount')&&Ext.getCmp('pr_amount')&&Ext.getCmp('pr_cmrate')){
			var cmamount = Ext.getCmp('pr_cmamount').getValue();;
			var amount = Ext.getCmp('pr_amount').getValue();
			
			var rate  = Number(cmamount/amount);
			rate = Ext.Number.toFixed(rate,8);
			Ext.getCmp('pr_cmrate').setValue(rate);
		}
    	
    },
    //计算冲账金额  = 预收金额*冲账汇率
    changeCmamount:function(){
    	if(Ext.getCmp('pr_amount')&&Ext.getCmp('pr_cmrate')){
    		var pramount = Ext.getCmp('pr_amount').getValue();
    		var prcmrate = Ext.getCmp('pr_cmrate').getValue();
    		if(!Ext.isNumber(pramount)){
    			pramount = Ext.Number.from(pramount,0);
    		}
    		if(!Ext.isNumber(prcmrate)){
    			prcmrate = Ext.Number.from(prcmrate,0);
    		}
    		var cmamount = pramount*prcmrate;
    		Ext.getCmp('pr_cmamount').setValue(Ext.util.Format.number(cmamount,'0.00'));
    	}
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
		Ext.each(items,function(item,index,allitems){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
				var orderamount =item.data.prd_orderamount;	//订单金额
				var havebalance = item.data.sa_prepayamount;	//已预收金额prd_havebalance
				var nowbalance = item.data.prd_nowbalance;	//本次预收额
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
			me.beforeSave(this);
		}else{
			if(warnStr != ''||warnStr.length>0){
				showError('明细表第'+warnStr+'行 已预收金额与本次预收额的合计超过订单金额,不能保存');return;
			}
		}
	},
	
	getJsamount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var jsamount = 0;
		var detailcount = 0;
		var cateamount = 0;
		Ext.each(items,function(item,index){
			if(item.data['prd_catecode']!=null&&item.data['prd_catecode']!=""){
				cateamount = cateamount + Number(item.data['prd_nowbalance']);
			}
		});
		Ext.getCmp('pr_jsamount').setValue(Ext.Number.from(Ext.getCmp('pr_cmamount').getValue())-cateamount);
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
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		var detail = Ext.getCmp('grid');
		var items = detail.store.data.items;
		var bool = true;
		var warnStr='';
		Ext.each(items,function(item,index,allitems){
			if(item.dirty && item.data[detail.necessaryField] != null && item.data[detail.necessaryField] != ''){
				var orderamount =item.data.prd_orderamount;	//订单金额
				var havebalance = item.data.sa_prepayamount;	//已预收金额prd_havebalance
				var nowbalance = item.data.prd_nowbalance;	//本次预收额
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
					me.FormUtil.update(r, param1, param2,param3);
				}else{
					me.FormUtil.checkForm();
				}
			}
		}else{
			if(warnStr != ''||warnStr.length>0){
				showError('明细表第'+warnStr+'行 已预收金额与本次预收额的合计超过订单金额,不能保存');return;
			}
		}
	},
	//提交时的相关操作开始
	checkFormDirty: function(){
		var form = Ext.getCmp('form');
		var s = '';
		form.getForm().getFields().each(function (item,index,length){
			var value = item.value == null ? "" : item.value;
			item.originalValue = item.originalValue == null ? "" : item.originalValue;
			if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
				var label = item.fieldLabel || item.ownerCt.fieldLabel ||
					item.boxLabel || item.ownerCt.title;//针对fieldContainer、radio、fieldset等
				if(label){
					s = s + '&nbsp;' + label.replace(/&nbsp;/g,'');
				}
			}
		});
		return (s == '') ? s : ('表单字段(<font color=green>'+s+'</font>)已修改');
	}
});