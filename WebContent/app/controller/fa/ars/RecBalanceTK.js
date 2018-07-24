Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.RecBalanceTK', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.RecBalanceTK','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Submit','core.button.ResAudit',
  			'core.button.Audit','core.button.ResSubmit','core.button.Post','core.button.ResPost','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted','core.button.StrikeBalance',
      		'core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.button.Print','fa.ars.recbalanceprdetail.RecBalancePRDetailGrid',
      		'core.button.AssDetail', 'core.trigger.MultiDbfindTrigger', 'core.form.SeparNumber'
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
    				if(btn){
    					if(!Ext.isEmpty(ass)){
        					btn.setDisabled(false);
        				} else {
        					btn.setDisabled(true);
        				}
    				}
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
    					me.BaseUtil.getRandomNumber();
    				}
    				this.getAramount();
    				//保存之前的一些前台的逻辑判定
    				this.beforeSaveRecBalance();
    			}
    		},
    		'field[name=rb_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=rb_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'field[name=rb_date]':{
				beforerender: function(field){
					if(Ext.getCmp('rb_source') && (Ext.getCmp('rb_source').value=="Bank" || Ext.getCmp('rb_source').value=="应收票据" || Ext.getCmp('rb_source').value=="应收票据退票" || Ext.getCmp('rb_source').value=="背书转让")){
						field.readOnly=true;
					}
				}
			},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				if(caller == 'RecBalance!TK'){
        				//辅助核算
        				btn.ownerCt.add({
        					xtype:'erpAssDetailButton',
        					disabled:true
        				});
    				}
    			}
    		},
    		'erpPostButton' : {
    			afterrender: function(btn){
    		        var status = Ext.getCmp('rb_statuscode');
    		        if(status && status.value != 'UNPOST'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.beforePost(btn);
    			}
    		},
    		'erpResPostButton' : {
    			afterrender: function(btn){
    		        var status = Ext.getCmp('rb_statuscode');
    		        if(status && status.value != 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				var source = Ext.getCmp('rb_source');
    				if(source && source.value){
    					showError("请在来源:"+source.value+",单号："+Ext.getCmp('rb_sourcecode').value+"中进行反审核或者反记账操作！");
    					return;
    				}
    				me.FormUtil.onDelete(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.getAramount();
    				me.beforeUpdateRecBalance();
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
    				me.FormUtil.onAdd('addRecBalance', title, 'jsps/fa/ars/recBalanceTK.jsp?whoami='+caller);
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
    		        	poststatus = Ext.getCmp('rb_statuscode');
    		        if(status && status.value != 'ENTERING'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.beforeSubmit(btn);
    			}
    		},
			'textfield[name=rb_amount]':{
				beforerender: function(field){
					if(Ext.getCmp('rb_source')&&Ext.getCmp('rb_source').value!=""){
						field.readOnly=true;
					}
				},
    			change: me.changecmrate
    		},
    		'textfield[name=rb_cmamount]':{
    			change: me.changecmrate
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        poststatus = Ext.getCmp('rb_statuscode');
    		        if(status && status.value != 'COMMITED'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        poststatus = Ext.getCmp('rb_statuscode');
    		        if(status && status.value != 'COMMITED'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        	postStatus = Ext.getCmp('rb_statuscode');
    		        if((status && status.value != 'AUDITED') ||(postStatus && postStatus.value == 'POSTED')){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('rb_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('rb_id').value);
    			}
    		},
    		'numberfield[name=rb_amount]':{
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
    		'numberfield[name=rb_cmamount]':{
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
    		'numberfield[name=rb_aramount]':{
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
    		'cateTreeDbfindTrigger[name=rb_catecode]':{
    			aftertrigger:function(trigger, data){
    				Ext.getCmp('rb_catename').setValue(data[0].raw.data.ca_name);
    				if(caller == 'RecBalance!IMRE'||caller == 'RecBalance!PBIL'){
    					var asstype = data[0].raw.data.ca_asstype;
        				var btn = Ext.getCmp("assmainbutton");
        				if(Ext.isEmpty(asstype)){
        					btn.setDisabled(true);
        				}else{
        					btn.setDisabled(false);
        				}
    				}
    			}
    		},
    		'field[name=rbd_ordercode]': {
    			afterrender:function(t){
    				t.gridKey="rb_custcode";
    				t.mappinggirdKey="ab_custcode";
    				t.gridErrorMessage="请先选择客户";
    			},
    			beforetrigger:function(t){
    				var currency = Ext.getCmp('rb_cmcurrency').getValue();
    				if(currency&&currency!=''){
    					t.findConfig="ab_currency='"+currency+"'";
    				}
    			}
    		},
    		//抓取发票信息
    		'button[name=catchab]':{
    			afterrender: function(btn){
	    			var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
					if(auditStatus && auditStatus.value != 'ENTERING'){
						btn.setDisabled(true);
					}
					var poststatus = Ext.getCmp('rb_statuscode');
					if(poststatus && poststatus.value == 'POSTED'){
						btn.setDisabled(true);
					}
    			},
    			click:function(btn){
    				var params = new Object();
    				var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var array = new Array();
    				var r = form.getValues();
    				var bars=grid.query('toolbar'),toolbar=bars[0],startdate,enddate;
       			    startdate=toolbar.items.items[2].value;
       			    enddate=toolbar.items.items[4].value;
    				Ext.each(items,function(item,index){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						array.push(item);
    					}
    				});
    				var rb_id = Ext.getCmp('rb_id').value;
    				if(!rb_id||(rb_id&&(rb_id == 0||rb_id==''||rb_id==null))){
     					Ext.Msg.alert('请先保存单据');
     				}else{
     					if(array.length>0){
        					Ext.Msg.alert('需要先清除明细行中的数据!');
        				} else {
        					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
        					if(startdate!=null) params.startdate=Ext.Date.format(startdate,'Y-m-d');
                            if(enddate !=null)  params.enddate=Ext.Date.format(enddate,'Y-m-d');
        					params.caller=caller;
        					//抓取
        					Ext.Ajax.request({
        				   		url : basePath + form.catchABUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				catchSuccess(function(){
        			    					//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   				var params = {
        					   						caller:caller,
        					   						condition:'rbd_rbid='+value
        					   				};
        					   				grid.GridUtil.loadNewStore(grid, params);
        			    				});
        				   			} else if(localJson.exceptionInfo){
        				   				
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
    			afterrender: function(btn){
	    			var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
					if(auditStatus && auditStatus.value != 'ENTERING'){
						btn.setDisabled(true);
					}
					var poststatus = Ext.getCmp('rb_statuscode');
					if(poststatus && poststatus.value == 'POSTED'){
						btn.setDisabled(true);
					}
    			},
    			click:function(btn){
    				var grid = Ext.getCmp('grid');
    				warnMsg('确定清除所有明细行么?',function(t){
    					if(t=='yes'){
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var params = new Object();
    						params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    						
    						Ext.Ajax.request({
        				   		url : basePath + form.cleanABUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				cleanSuccess(function(){
        			    					//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   				var params = {
        					   						caller:caller,
        					   						condition:'rbd_rbid='+value
        					   				};
        					   				grid.GridUtil.loadNewStore(grid, params);
        			    				});
        				   			} else if(localJson.exceptionInfo){
        				   				
        				   			} else{
        				   				cleanFailure();//@i18n/i18n.js
        				   			}
        				   		}
        					});
    					}else{
    						return;
    					}
    				});
    			}
    		},

    		'gridcolumn[dataIndex=rbd_currency]' : {
    			afterrender : function(c) {
    				c.defaultValue = 'RMB';
    			}
    		}
    	});
    },
    beforePost:function(btn){
    	var me = this;
    	if(caller == 'RecBalance!TK'){
    		var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    		var sameCurrency = true;
    		var amount = Number(Ext.getCmp('rb_amount').getValue());
    		var cmamount =Number(Ext.getCmp('rb_cmamount').getValue());
    		var cmcurrency = Ext.getCmp('rb_cmcurrency').getValue();
    		var cmrate = Number(Ext.getCmp('rb_cmrate').getValue());
    		var detailamount = 0;
    		var currency = Ext.getCmp('rb_currency').getValue();
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
    		Ext.each(items,function(item,index){
    			if(!Ext.isEmpty(item.get('rbd_ordercode')) || !Ext.isEmpty(item.get('rbd_catecode'))) {
    				detailamount = detailamount+Number(item.data['rbd_nowbalance']);
    				if(cmcurrency!=item.data['rbd_currency']){
        				//从表币别有与主表币别不同的
        				//抛出异常
        				sameCurrency = false;
        			}
    			}
    		});
    		if(!sameCurrency){
    			//从表币别有与主表币别不同的
				//抛出异常
    			showError('明细行币别与冲账币别不同,不能过账');return;
    		}
    		if(Math.abs(cmamount - Number(Ext.Number.toFixed(detailamount, 2))) >= 0.005){
    			//冲账金额与明细行本次结算总和不等
    			//抛出异常
    			showError('明细行本次结算与冲账金额不等,不能过账');return;
    		}
    		me.FormUtil.onPost(Ext.getCmp('rb_id').value);
    		
    		}else{
    			me.FormUtil.onPost(Ext.getCmp('rb_id').value);
    		}
    },
    beforeSubmit:function(btn){
    	var me = this;
    	if(caller == 'RecBalance!TK'){
    		var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    		var sameCurrency = true;
    		var amount = Number(Ext.getCmp('rb_amount').getValue());
    		var cmamount =Number(Ext.getCmp('rb_cmamount').getValue());
    		var cmcurrency = Ext.getCmp('rb_cmcurrency').getValue();
    		var cmrate = Number(Ext.getCmp('rb_cmrate').getValue());
    		var detailamount = 0;
    		var currency = Ext.getCmp('rb_currency').getValue();
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
    		Ext.each(items,function(item,index){
    			if(!Ext.isEmpty(item.get('rbd_ordercode')) || !Ext.isEmpty(item.get('rbd_catecode'))) {
    				detailamount = detailamount+Number(item.data['rbd_nowbalance']);
    				if(cmcurrency!=item.data['rbd_currency']){
        				//从表币别有与主表币别不同的
        				//抛出异常
        				sameCurrency = false;
        			}
    			}
    		});
    		if(!sameCurrency){
    			//从表币别有与主表币别不同的
				//抛出异常
    			showError('明细行币别与冲账币别不同,不能提交');return;
    		}
    		if(Math.abs(cmamount - Number(Ext.Number.toFixed(detailamount, 2))) >= 0.005){
    			//冲账金额与明细行本次结算总和不等
    			//抛出异常
    			showError('明细行本次结算与冲账金额不等,不能提交');return;
    		}
			me.FormUtil.onSubmit(Ext.getCmp('rb_id').value, false, this.beforeUpdateRecBalance, this);
    	}else{
    		me.FormUtil.onSubmit(Ext.getCmp('rb_id').value, false, this.beforeUpdateRecBalance, this);
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
	beforeSaveRecBalance: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		var warnStr='';
		Ext.each(items,function(item,index){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
				var aramount =item.data['rbd_aramount'];	//订单金额
				var havepay = item.data['rbd_havepay'];	//已预收金额
				var nowbalance = item.data['rbd_nowbalance'];	//本次预收额
				//订单金额>=已预收金额+本次预收额
				//此种情况不能进行保存
				if(havepay+nowbalance>aramount){
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
				showError('明细表第'+warnStr+'行 已收金额与本次结算额的合计超过发票金额,不能保存');return;
			}
		}
	},
    //冲账汇率计算  = 冲账金额/预收金额
    changecmrate: function(){
    	if(Ext.getCmp('rb_amount') && Ext.getCmp('rb_cmrate')) {
    		var rbamount = Ext.Number.from(Ext.getCmp('rb_amount').getValue(), 0);
    		var cmamount = Ext.Number.from(Ext.getCmp('rb_cmamount').getValue(), 0);
    		if(rbamount != 0)
    			Ext.getCmp('rb_cmrate').setValue(Ext.Number.toFixed(cmamount/rbamount, 8));
    	}
    },
	
	//计算冲账收款金额   并写入主表 冲账收款金额字段
	getAramount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var aramount = 0;
		Ext.each(items,function(item,index){
			if(item.data['rbd_ordercode']!=null&&item.data['rbd_ordercode']!=""){
				aramount= aramount + Number(item.data['rbd_nowbalance']);
			}
		});
		Ext.getCmp('rb_aramount').setValue(aramount);
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
		me.onSave(form, param1, param2,param3);
	},
	onSave:function(form, param1, param2,param3){
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
	beforeUpdateRecBalance: function(){
		var me = this;
		var grid = Ext.getCmp('grid');
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('rbd_rbid',Ext.getCmp('rb_id').value);
		});
		//采购价格不能为0
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		var warnStr='';
		Ext.each(items,function(item,index){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
				var aramount =item.data['rbd_aramount'];	//发票金额
				var havepay = item.data['rbd_havepay'];	//已收金额
				var nowbalance = item.data['rbd_nowbalance'];	//本次结算额
				//订单金额>=已预收金额+本次预收额
				//此种情况不能进行保存
				if(havepay+nowbalance>aramount){
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
			me.beforeUpdate();
		}else{
			if(warnStr != ''||warnStr.length>0){
				showError('明细表第'+warnStr+'行 已收金额与本次结算额的合计超过发票金额,不能保存');return;
			}
		}
	},
	beforeUpdate:function(){
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
		if(caller == 'RecBalance!PBIL'){
			
		}
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
	}
});