Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.PayBalanceAR', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.arp.PayBalanceAR','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Submit','core.button.ResAudit',
  			'core.button.Audit','core.button.ResSubmit','core.button.Post','core.button.ResPost','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted','core.button.StrikeBalance',
      		'core.grid.AssPanel','core.window.AssWindow','core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.button.Print','fa.arp.paybalancear.PayBalanceARGrid',
      		'core.button.AssDetail', 'core.form.SeparNumber'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick2(selModel, record);
    				var btn = selModel.ownerCt.down('erpAssDetailButton');
    				var ass = record.data['ca_asstype'];
    				if(!Ext.isEmpty(ass)){
    					btn.setDisabled(false);
    				} else {
    					btn.setDisabled(true);
    				}
    			} 
    		},
    		'field[name=pb_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pb_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'recbalanceap': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick2(selModel, record);
        			var btn = selModel.ownerCt.down('erpAssDetailButton');
    				var ass = record.data['ca_asstype'];
    				if(!Ext.isEmpty(ass)){
    					btn.setDisabled(false);
    				} else {
    					btn.setDisabled(true);
    				}
    			} 
    		},
    		'recbalanceAP': { 
    			itemclick: this.onGridItemClick1,
    			afterrender:function(grid){
    				grid.plugins[0].on('edit',function(e){
    					me.sumAmount();
    				});
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('PayBalance');
    				}
    				this.getAramount();
    				this.getApamount();
    				//保存之前的一些前台的逻辑判定
    				this.beforeSave(this);
    			}
    		},
    		'erpToolbar': {
    			afterrender: function(tool){
    				tool.add({
    					xtype:'erpAssDetailButton',
    					id: tool.id + '_assdetail',
    					disabled:true
    				});
    			}
    		},
    		'erpPostButton' : {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode);
    				if(status && status.value == 'UNPOST'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    			},
    			click: {
    				fn:function(btn){
    					me.beforePost(btn);
        			},
        			lock:2000
    			}
    		},
    		'erpResPostButton' : {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statusCode);
    				if(status && status.value == 'POSTED'){
    					btn.show();
    				}else{
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('pb_id').value);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pb_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.getAramount();
    				me.getApamount();
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPayBalance', '新增应付冲应收单', 'jsps/fa/arp/payBalanceAR.jsp?whoami='+caller);
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
    				me.beforeSubmit(btn);
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
    				me.FormUtil.onResSubmit(Ext.getCmp('pb_id').value);
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
    				me.FormUtil.onAudit(Ext.getCmp('pb_id').value);
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
    				me.FormUtil.onResAudit(Ext.getCmp('pb_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('pb_id').value);
    			}
    		},
    		'field[name=pbd_ordercode]': {
    			afterrender:function(t){
    				t.gridKey="pb_vendcode|pb_vmcurrency";
    				t.mappinggirdKey="ab_vendcode|ab_currency";
    				t.gridErrorMessage="请先选择供应商|请选择冲应付币别";
    			}
    		},
    		'field[name=pbap_ordercode]': {
    			afterrender:function(t){
    				t.gridKey="pb_vendorcode|pb_currency";
    				t.mappinggirdKey="ab_vendcode|ab_currency";
    				t.gridErrorMessage="请先选择供应商|请选择冲应付币别";
    			}
    		},
    		//抓取应付发票信息
    		'button[name=catchap]':{
    			afterrender: function(btn){
	    			var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
					if(auditStatus && auditStatus.value != 'ENTERING'){
						btn.setDisabled(true);
					}
					var poststatus = Ext.getCmp('pb_statuscode');
					if(poststatus && poststatus.value == 'POSTED'){
						btn.setDisabled(true);
					}
    			},
    			click:function(btn){
    				var params = new Object();
    				var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('paybalancearDetailGrid');
    				var items = grid.store.data.items;
    				var array = new Array();
    				var r = form.getValues();
    				Ext.each(items,function(item,index){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						array.push(item);
    					}
    				});
    				var pb_id = Ext.getCmp('pb_id').value;
    				if(!pb_id||(pb_id&&(pb_id == 0||pb_id==''||pb_id==null))){
     					Ext.Msg.alert('请先保存单据');
     					
     				}else{
     					if(array.length>0){
        					Ext.Msg.alert('需要先清除明细行中的数据!');
        				} else {
        					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
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
        			    					//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   		    	var condition = 'pbap_pbid='+value;
        					   		    	grid.getMyData(condition);
        			    				});
        				   			} else if(localJson.exceptionInfo){
        				   				
        				   			} else{
        				   				catchFailure();
        				   			}
        				   		}
        					});
        				}
     				}
    			}
    		},
    		//清除应付发票信息
    		'button[name=cleanap]':{
    			afterrender: function(btn){
	    			var auditStatus = Ext.getCmp(me.getForm(btn).auditStatusCode);
					if(auditStatus && auditStatus.value != 'ENTERING'){
						btn.setDisabled(true);
					}
					var poststatus = Ext.getCmp('pb_statuscode');
					if(poststatus && poststatus.value == 'POSTED'){
						btn.setDisabled(true);
					}
    			},
    			click:function(btn){
    				var grid = Ext.getCmp('paybalancearDetailGrid');
    				warnMsg('确定清除所有明细行么?',function(t){
    					if(t=='yes'){
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var params = new Object();
    						params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    						Ext.Ajax.request({
        				   		url : basePath + form.cleanAPUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				cleanSuccess(function(){
        			    					//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   		    	var condition = 'pbap_pbid='+value;
        					   		    	grid.getMyData(condition);
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
    		//抓取应收发票信息
    		'button[name=catchar]':{
    			click:function(btn){
    				var params = new Object();
    				var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var array = new Array();
    				var r = form.getValues();
    				Ext.each(items,function(item,index){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						array.push(item);
    					}
    				});
    				var pb_id = Ext.getCmp('pb_id').value;
    				if(!pb_id||(pb_id&&(pb_id == 0||pb_id==''||pb_id==null))){
     					Ext.Msg.alert('请先保存单据');
     				}else{
     					if(array.length>0){
        					Ext.Msg.alert('需要先清除明细行中的数据!');
        				} else {
        					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
        					//抓取
        					Ext.Ajax.request({
        				   		url : basePath + form.catchARUrl,
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
        					   						caller:'PayBalance!DWRC',
        					   						condition:'pbd_pbid='+value
        					   				};
        					   				grid.GridUtil.loadNewStore(grid, params);	
        			    				});
        				   			} else if(localJson.exceptionInfo){
        				   				
        				   			} else{
        				   				catchFailure();
        				   			}
        				   		}
        					});
        				}
     				}
    			}
    		},
    		//清除应收发票信息
    		'button[name=cleanar]':{
    			click:function(btn){
    				var grid = Ext.getCmp('grid');
    				warnMsg('确定清除所有明细行么?',function(t){
    					if(t=='yes'){
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var params = new Object();
    						params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    						Ext.Ajax.request({
        				   		url : basePath + form.cleanARUrl,
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
        					   						caller:'PayBalance!DWRC',
        					   						condition:'pbd_pbid='+value
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
    		//计算冲账汇率
    		'field[name=pb_vmamount]': {
    			change: function(f) {
    				var a = f.ownerCt.down('#pb_amount');
    				if(!Ext.isEmpty(a.value)) {
    					var r = f.ownerCt.down('#pb_vmrate'),
    						rate = Ext.Number.toFixed(f.value/a.value, 8);
    					if(r.value != rate)
    						r.setValue(rate);
    				}
    			}
    		},
    		'field[name=pb_amount]': {
    			change: function(f) {
    				var v = f.ownerCt.down('#pb_vmamount');
    				if(!Ext.isEmpty(f.value)) {
    					var r = f.ownerCt.down('#pb_vmrate'),
    						rate = Ext.Number.toFixed(v.value/f.value, 8);
    					if(r.value != rate)
    						r.setValue(rate);
    				}
    			}
    		}
    	});
    },
    beforeSubmit:function(){
    	var me = this;
    	var grid1 =	Ext.getCmp('paybalancearDetailGrid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('grid'),items2=grid2.store.data.items;
		
		var sameCurrency = true;
		var amount = Number(Ext.getCmp('pb_amount').getValue());	//冲应付金额
		var vmamount = Number(Ext.getCmp('pb_vmamount').getValue());	//冲应收金额
		var vmcurrency = Ext.getCmp('pb_vmcurrency').getValue();	//冲应收币别
		var vurrency = Ext.getCmp('pb_currency').getValue();		//冲应付币别
		var vmrate = Number(Ext.getCmp('pb_vmrate').getValue());	//冲账汇率
		var amountsum = 0;
		var vmamountsum = 0;
		
		Ext.each(items1,function(item,index){
			amountsum = amountsum+Number(item.data['pbar_nowbalance']);
			if(currency!=item.data['pbar_currency']){
				//从表币别有与主表币别不同的
				sameCurrency = false;
			}
		});
		Ext.each(items2,function(item,index){
			if(!me.GridUtil.isBlank(grid2, item.data)){
				vmamountsum = vmamountsum+Number(item.data['pbd_nowbalance']);
				if(vmcurrency!=item.data['pbd_currency']){
					//从表币别有与主表币别不同的
					sameCurrency = false;
				}
			}
		});
    	if(Math.abs(vmamount-amount*vmrate)>=0.01){
    		showError('冲账金额不正确,不能提交');return;
    	}
    	if(!sameCurrency){
    		//从表币别有与主表币别不同的
    		showError('明细行币别与币别不同,不能提交');return;
    	}
    	if(Math.abs(vmamount-vmamountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		showError('应收发票明细行本次结算金额与冲应收金额不等,不能提交');return;
    	}
    	me.FormUtil.onSubmit(Ext.getCmp('pb_id').value, false, this.beforeUpdate, this);
    },
    beforePost:function(){
    	var me = this;
    	var grid1 =	Ext.getCmp('paybalancearDetailGrid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('grid'),items2=grid2.store.data.items;
		
		var sameCurrency = true;
		var amount = Number(Ext.getCmp('pb_amount').getValue());	//冲应付金额
		var vmamount = Number(Ext.getCmp('pb_vmamount').getValue());	//冲应收金额
		var vmcurrency = Ext.getCmp('pb_vmcurrency').getValue();	//冲应收币别
		var currency = Ext.getCmp('pb_currency').getValue();		//冲应付币别
		var vmrate = Number(Ext.getCmp('pb_vmrate').getValue());	//冲账汇率
		var amountsum = 0;
		var vmamountsum = 0;

		Ext.each(items1,function(item,index){
			amountsum = amountsum+Number(item.data['pbar_nowbalance']);
			if(currency!=item.data['pbar_currency']){
				//从表币别有与主表币别不同的
				//抛出异常
				sameCurrency = false;
			}
		});
		Ext.each(items2,function(item,index){
			vmamountsum = vmamountsum+Number(item.data['rbd_nowbalance']);
			if(vmcurrency!=item.data['rbd_currency']){
				//从表币别有与主表币别不同的
				sameCurrency = false;
			}
		});
    	if(Math.abs(vmamount-amount*vmrate)>=0.01){
    		showError('应收冲账金额不正确,不能过账');return;
    	}
    	if(!sameCurrency){
    		//从表币别有与主表币别不同的
    		showError('明细行币别与币别不同,不能过账');return;
    	}
    	if(Math.abs(amount-amountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		showError('应付发票明细行本次结算金额与冲应付金额不等,不能过账');return;
    	}
    	if(Math.abs(vmamount-vmamountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		showError('应收发票明细行本次结算金额与冲应收金额不等,,不能过账');return;
    	}
    	me.FormUtil.onPost(Ext.getCmp('pb_id').value);
    },
    sumAmount:function(){
    	var grid = Ext.getCmp('paybalancearDetailGrid');
    	var items = grid.store.data.items;
    	var sumamount = 0;
    	var text='本次结算金额(sum):';
    	Ext.each(items,function(item,index){
        	if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
        		sumamount = sumamount + Ext.Number.from(item.data['pbar_nowbalance'],0);
        	}
    	});
    	text=text+Ext.Number.from(sumamount,0);
		if(Ext.getCmp('pbar_nowbalance_sum')){
			Ext.getCmp('pbar_nowbalance_sum').setText(text);
		}
    },
    onGridItemClick1: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('paybalancearDetailGrid');
    	grid.lastSelectedRecord = record;
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    onGridItemClick2: function(selModel, record){//grid行选择
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
	beforeUpdate:function(){
		var mm = this;
		if(! mm.FormUtil.checkForm()){
			return;
		}
		var s1 = mm.FormUtil.checkFormDirty(form);
		var grid1 = Ext.getCmp('paybalancearDetailGrid'), grid2 = Ext.getCmp('grid');
		var param1 = mm.GridUtil.getGridStore(grid1);
		var param2 = mm.GridUtil.getGridStore(grid2);
		if(s1 == '' && (param1 == null || param1 == '') && (param2 == null || param2 == '')){
			warnMsg('未添加或修改数据,是否继续?', function(btn){
				if(btn == 'yes'){
					mm.onUpdate(param1, param2);
				} else {
					return;
				}
			});
		} else {
			mm.onUpdate(param1, param2);
		}
	},
	
	/**
	 * 保存之前的判断
	 */
	beforeSave: function(me){
		var mm = this;
		var form = Ext.getCmp('form');
		if(! mm.FormUtil.checkForm()){
			return;
		}
		if(form.keyField){
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				mm.FormUtil.getSeqId(form);
			}
		}
		var grid1 = Ext.getCmp('paybalancearDetailGrid'), grid2 = Ext.getCmp('grid');
		var param1 = me.GridUtil.getGridStore(grid1);
		var param2 = me.GridUtil.getGridStore(grid2);
		if((param1 == null || param1 == '') && (param2 == null || param2 == '')){
			warnMsg('明细表还未添加数据,是否继续?', function(btn){
				if(btn == 'yes'){
					mm.onSave(param1, param2);
				} else {
					return;
				}
			});
		} else {
			mm.onSave(param1, param2);
		}
	},
	onUpdate:function(param1,param2){
		var me = this;
		var form = Ext.getCmp('form');
		param1 = param1 == null ? [] : "[" + param1.toString() + "]";
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
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!me.FormUtil.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			me.FormUtil.update(r, param1, param2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	/**
	 * 单据保存
	 * @param param 传递过来的数据，比如gridpanel的数据
	 */
	onSave: function(param1,param2){
		var me = this;
		var form = Ext.getCmp('form');
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
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!me.FormUtil.contains(form.saveUrl, '?caller=', true)){
				form.saveUrl = form.saveUrl + "?caller=" + caller;
			}
			me.FormUtil.save(r, param1, param2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	//计算冲账收款金额   并写入主表应收挂账金额字段
	getAramount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var aramount = 0;
		Ext.each(items,function(item,index){
			if(item.data['pbd_ordercode']!=null&&item.data['pbd_ordercode']!=""){
				aramount= aramount + Number(item.data['pbd_nowbalance']);
			}
		});
		Ext.getCmp('pb_aramount').setValue(Ext.Number.toFixed(aramount, 2));
	},
	//计算冲账付款金额   并写入主表 应付挂账金额字段
	getApamount: function(){
		var grid = Ext.getCmp('paybalancearDetailGrid');
		var items = grid.store.data.items;
		var apamount = 0;
		Ext.each(items,function(item,index){
			if(item.data['pbar_ordercode']!=null&&item.data['pbar_ordercode']!=""){
				apamount= apamount + Number(item.data['pbar_nowbalance']);
			}
		});
		Ext.getCmp('pb_apamount').setValue(Ext.Number.toFixed(apamount, 2));
	}
});