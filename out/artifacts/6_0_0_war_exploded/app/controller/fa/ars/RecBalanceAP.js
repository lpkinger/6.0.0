Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.RecBalanceAP', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.RecBalanceAP','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Submit','core.button.ResAudit',
  			'core.button.Audit','core.button.ResSubmit','core.button.Post','core.button.ResPost','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted','core.button.StrikeBalance',
      		'core.grid.AssPanel','core.window.AssWindow','core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.button.Print','fa.ars.recbalanceap.RecBalanceAPGrid',
      		'core.button.AssDetail', 'core.form.SeparNumber','core.trigger.MultiDbfindTrigger','core.button.GetSumAmount'
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
    		'field[name=rb_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=rb_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpGetSumAmountButton':{
    			click: function(btn){
    				var grid1 = Ext.getCmp("recbalanceapGrid"),grid2 = Ext.getCmp("grid"),items1 = grid1.store.data.items,items2 = grid2.store.data.items;
    				var detailamount1 = 0;
    				var detailamount2 = 0;
    	    		Ext.each(items1,function(item,index){
    	    			if(!me.GridUtil.isBlank(grid1,item.data)) {
    	    				detailamount1 = detailamount1 + Number(item.data['rbap_nowbalance']);
    	    			}
    	    		});
    	    		Ext.each(items2,function(item,index){
    	    			if(!me.GridUtil.isBlank(grid2,item.data)) {
    	    				detailamount2 = detailamount2 + Number(item.data['rbd_nowbalance']);
    	    			}
    	    		});
    	    		
    	    		Ext.getCmp("rb_amount").setValue(detailamount1);
    	    		Ext.getCmp("rb_cmamount").setValue(detailamount2);
    			}
    		},
    		'recbalanceap': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick2(selModel, record);
        			var btn = selModel.ownerCt.down('erpAssDetailButton');
    				var ass = record.data['ca_asstype'];
    				btn.setDisabled(Ext.isEmpty(ass));
    			} 
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('RecBalance');
    				}
    				this.getAramount();
    				this.getApamount();
    				//保存之前的一些前台的逻辑判定
    				this.beforeSave(this);
    			}
    		},
    		'erpToolbar': {
    			afterrender: function(tool){
    				tool.add(new erp.view.core.button.AssDetail({
    					id: tool.id + '_assdetail',
    					disabled:true
    				}));
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
    				me.FormUtil.onDelete(Ext.getCmp('rb_id').value);
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
    				me.FormUtil.onAdd('addRecBalance', '新增应收冲应付单', 'jsps/fa/ars/recBalanceAP.jsp?whoami='+caller);
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
    		'dbfindtrigger[name=rbd_ordercode]': {
    			afterrender:function(t){
    				t.gridKey="rb_custcode|rb_cmcurrency";
    				t.mappinggirdKey="ab_custcode|ab_currency";
    				t.gridErrorMessage="请先选择客户|请选择冲应收币别";
    			}
    		},
    		'dbfindtrigger[name=rbap_ordercode]': {
    			afterrender:function(t){
    				t.gridKey="rb_vendorcode|rb_currency";
    				t.mappinggirdKey="ab_vendcode|ab_currency";
    				t.gridErrorMessage="请先选择供应商|请选择冲应付币别";
    			}
    		},
    		'multidbfindtrigger[name=rbd_ordercode]': {
    			afterrender:function(t){
    				t.gridKey="rb_custcode|rb_cmcurrency";
    				t.mappinggirdKey="ab_custcode|ab_currency";
    				t.gridErrorMessage="请先选择客户|请选择冲应收币别";
    			}
    		},
    		'multidbfindtrigger[name=rbap_ordercode]': {
    			afterrender:function(t){
    				t.gridKey="rb_vendorcode|rb_currency";
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
					var poststatus = Ext.getCmp('rb_statuscode');
					if(poststatus && poststatus.value == 'POSTED'){
						btn.setDisabled(true);
					}
    			},
    			click:function(btn){
    				var params = new Object();
    				var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('recbalanceapGrid');
    				var bars=grid.query('toolbar'),toolbar=bars[0];
      			    startdate=toolbar.items.items[2].value;
      			    enddate=toolbar.items.items[4].value;
    				var items = grid.store.data.items;
    				var array = new Array();
    				var r = form.getValues();
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
        					   		    	var condition = 'rbap_rbid='+value;
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
					var poststatus = Ext.getCmp('rb_statuscode');
					if(poststatus && poststatus.value == 'POSTED'){
						btn.setDisabled(true);
					}
    			},
    			click:function(btn){
    				var grid = Ext.getCmp('recbalanceapGrid');
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
        					   		    	var condition = 'rbap_rbid='+value;
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
    				var bars=grid.query('toolbar'),toolbar=bars[0];
      			    startdate=toolbar.items.items[2].value;
      			    enddate=toolbar.items.items[4].value;
    				var array = new Array();
    				var r = form.getValues();
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
        					   						caller:'RecBalance!RRCW',
        					   						condition:'rbd_rbid='+value
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
        					   						caller:'RecBalance!RRCW',
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
    		//计算冲账汇率
    		'field[name=rb_cmamount]': {
    			change: function(f) {
    				var a = f.ownerCt.down('#rb_amount');
    				if(!Ext.isEmpty(a.value)) {
    					var r = f.ownerCt.down('#rb_cmrate'),
    						rate = Ext.Number.toFixed(f.value/a.value, 8);
    					if(r.value != rate)
    						r.setValue(rate);
    				}
    			}
    		},
    		'field[name=rb_amount]': {
    			change: function(f) {
    				var v = f.ownerCt.down('#rb_cmamount');
    				if(!Ext.isEmpty(f.value)) {
    					var r = f.ownerCt.down('#rb_cmrate'),
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
    	var grid1 =	Ext.getCmp('recbalanceapGrid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('grid'),items2=grid2.store.data.items;
		
		var sameCurrency = true;
		var amount = Number(Ext.getCmp('rb_amount').getValue());	//冲应付金额
		var cmamount = Number(Ext.getCmp('rb_cmamount').getValue());	//冲应收金额
		var cmcurrency = Ext.getCmp('rb_cmcurrency').getValue();	//冲应收币别
		var currency = Ext.getCmp('rb_currency').getValue();		//冲应付币别
		var cmrate = Number(Ext.getCmp('rb_cmrate').getValue());	//冲账汇率
		var amountsum = 0;
		var cmamountsum = 0;
		
		/*Ext.each(items1,function(item,index){
			amountsum = amountsum+Number(item.data['rbap_nowbalance']);
			if(currency!=item.data['rbap_currency']){
				//从表币别有与主表币别不同的
				//抛出异常
				sameCurrency = false;
			}
		});
		Ext.each(items2,function(item,index){
			if(!me.GridUtil.isBlank(grid2, item.data)){
				cmamountsum = cmamountsum+Number(item.data['rbd_nowbalance']);
				if(cmcurrency!=item.data['rbd_currency']){
					//从表币别有与主表币别不同的
					//抛出异常
					sameCurrency = false;
				}
			}
		});*/
    	/*if(Math.abs(cmamount-amount*cmrate)>=0.01){
    		showError('冲账金额不正确,不能提交');return;
    	}*/
    	/*if(!sameCurrency){
    		//从表币别有与主表币别不同的
			//抛出异常
    		showError('明细行币别与币别不同,不能提交');return;
    	}*/
    	/*if(Math.abs(amount-amountsum)>=0.01){    		
    		//冲账金额与明细行本次结算总和不等
    		//抛出异常
    		showError('应付发票明细行本次结算金额与冲应付金额不等,不能提交');return;
    	}
    	if(Math.abs(cmamount-cmamountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		//抛出异常
    		showError('应收发票明细行本次结算金额与冲应收金额不等,不能提交');return;
    	}*/
    	me.FormUtil.onSubmit(Ext.getCmp('rb_id').value, false, this.beforeUpdate, this);
    },
    beforePost:function(){
    	var me = this;
    	var grid1 =	Ext.getCmp('recbalanceapGrid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('grid'),items2=grid2.store.data.items;
		
		var sameCurrency = true;
		var amount = Number(Ext.getCmp('rb_amount').getValue());	//冲应付金额
		var cmamount = Number(Ext.getCmp('rb_cmamount').getValue());	//冲应收金额
		var cmcurrency = Ext.getCmp('rb_cmcurrency').getValue();	//冲应收币别
		var currency = Ext.getCmp('rb_currency').getValue();		//冲应付币别
		var cmrate = Number(Ext.getCmp('rb_cmrate').getValue());	//冲账汇率
		var amountsum = 0;
		var cmamountsum = 0;

		Ext.each(items1,function(item,index){
			amountsum = amountsum+Number(item.data['rbap_nowbalance']);
			if(currency!=item.data['rbap_currency']){
				//从表币别有与主表币别不同的
				//抛出异常
				sameCurrency = false;
			}
		});
		Ext.each(items2,function(item,index){
			cmamountsum = cmamountsum+Number(item.data['rbd_nowbalance']);
			if(cmcurrency!=item.data['rbd_currency']){
				//从表币别有与主表币别不同的
				//抛出异常
				sameCurrency = false;
			}
		});
    	if(Math.abs(cmamount-amount*cmrate)>=0.01){
    		showError('应收冲账金额不正确,不能过账');return;
    	}
    	if(!sameCurrency){
    		//从表币别有与主表币别不同的
			//抛出异常
    		showError('明细行币别与币别不同,不能过账');return;
    	}
    	if(Math.abs(amount-amountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		//抛出异常
    		showError('应付发票明细行本次结算金额与冲应付金额不等,不能过账');return;
    	}
    	if(Math.abs(cmamount-cmamountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		//抛出异常
    		showError('应收发票明细行本次结算金额与冲应收金额不等,,不能过账');return;
    	}
    	me.FormUtil.onPost(Ext.getCmp('rb_id').value);
    },
    sumAmount:function(){
    	var grid = Ext.getCmp('recbalanceapGrid');
    	var items = grid.store.data.items;
    	var sumamount = 0;
    	var text='本次结算金额(sum):';
    	Ext.each(items,function(item,index){
        	if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
        		sumamount = sumamount + Ext.Number.from(item.data['rbap_nowbalance'],0);
        	}
    	});
    	text=text+Ext.Number.from(sumamount,0);
		if(Ext.getCmp('rbap_nowbalance_sum')){
			Ext.getCmp('rbap_nowbalance_sum').setText(text);
		}
    },
    onGridItemClick1: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('recbalanceapGrid');
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
		var form = Ext.getCmp('form');
		if(! mm.FormUtil.checkForm()){
			return;
		}
		var s1 = mm.FormUtil.checkFormDirty(form);
		var grid1 = Ext.getCmp('recbalanceapGrid'), grid2 = Ext.getCmp('grid');
		var param1 = mm.GridUtil.getGridStore(grid1);
		var param2 = mm.GridUtil.getGridStore(grid2);
		var param3 = new Array();//		RBAPtool_assdetail
		var param4 = new Array();//    toolbar_assdetail
		if(Ext.getCmp('RBAPtool_assdetail')){
			Ext.each(Ext.Object.getKeys(Ext.getCmp('RBAPtool_assdetail').cacheStoreGrid), function(key){
				Ext.each(Ext.getCmp('RBAPtool_assdetail').cacheStoreGrid[key], function(d){
					d['dass_condid'] = key;
					param3.push(d);
				});
			});
		}
		if(Ext.getCmp('toolbar_assdetail')){
			Ext.each(Ext.Object.getKeys(Ext.getCmp('toolbar_assdetail').cacheStoreGrid), function(key){
				Ext.each(Ext.getCmp('toolbar_assdetail').cacheStoreGrid[key], function(d){
					d['dass_condid'] = key;
					param4.push(d);
				});
			});
		}
		if(s1 == '' && (param1 == null || param1 == '') && (param2 == null || param2 == '') && (param3 == null || param3 == '') && (param4 == null || param4 == '')){
			warnMsg('未添加或修改数据,是否继续?', function(btn){
				if(btn == 'yes'){
					mm.onUpdate(param1, param2,param3,param4);
				} else {
					return;
				}
			});
		} else {
			mm.onUpdate(param1, param2, param3, param4);
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
		var grid1 = Ext.getCmp('recbalanceapGrid'), grid2 = Ext.getCmp('grid');
		var param1 = me.GridUtil.getGridStore(grid1);
		var param2 = me.GridUtil.getGridStore(grid2);
		
		var param3 = new Array();//		RBAPtool_assdetail
		var param4 = new Array();//    toolbar_assdetail
		
		
		if(Ext.getCmp('RBAPtool_assdetail')){
			Ext.each(Ext.Object.getKeys(Ext.getCmp('RBAPtool_assdetail').cacheStoreGrid), function(key){
				Ext.each(Ext.getCmp('RBAPtool_assdetail').cacheStoreGrid[key], function(d){
					d['dass_condid'] = key;
					param3.push(d);
				});
			});
		}
		if(Ext.getCmp('toolbar_assdetail')){
			Ext.each(Ext.Object.getKeys(Ext.getCmp('toolbar_assdetail').cacheStoreGrid), function(key){
				Ext.each(Ext.getCmp('toolbar_assdetail').cacheStoreGrid[key], function(d){
					d['dass_condid'] = key;
					param4.push(d);
				});
			});
		}
		if((param1 == null || param1 == '') && (param2 == null || param2 == '')){
			warnMsg('明细表还未添加数据,是否继续?', function(btn){
				if(btn == 'yes'){
					mm.onSave(param1, param2,param3,param4);
				} else {
					return;
				}
			});
		} else {
			mm.onSave(param1, param2,param3,param4);
		}
	},
	onUpdate:function(param1,param2,param3,param4){
		var me = this;
		var form = Ext.getCmp('form');
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : Ext.encode(param3);
		param4 = param4 == null ? [] : Ext.encode(param4);
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
			me.FormUtil.update(r, param1, param2,param3,param4);
		}else{
			me.FormUtil.checkForm();
		}
	},
	/**
	 * 单据保存
	 * @param param 传递过来的数据，比如gridpanel的数据
	 */
	onSave: function(param1,param2,param3,param4){
		var me = this;
		var form = Ext.getCmp('form');
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : Ext.encode(param3);
		param4 = param4 == null ? [] : Ext.encode(param4);
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
			me.FormUtil.save(r, param1, param2,param3,param4);
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
			if(item.data['rbd_ordercode']!=null&&item.data['rbd_ordercode']!=""){
				aramount= aramount + Number(item.data['rbd_nowbalance']);
			}
		});
		Ext.getCmp('rb_aramount').setValue(Ext.Number.toFixed(aramount, 2));
	},
	//计算冲账付款金额   并写入主表 应付挂账金额字段
	getApamount: function(){
		var grid = Ext.getCmp('recbalanceapGrid');
		var items = grid.store.data.items;
		var apamount = 0;
		Ext.each(items,function(item,index){
			if(item.data['rbap_ordercode']!=null&&item.data['rbap_ordercode']!=""){
				apamount= apamount + Number(item.data['rbap_nowbalance']);
			}
		});
		Ext.getCmp('rb_apamount').setValue(Ext.Number.toFixed(apamount, 2));
	}
});