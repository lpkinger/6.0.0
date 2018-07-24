Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.PayBalancePRDetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.arp.PayBalance','core.grid.Panel2','core.toolbar.Toolbar','core.grid.AssPanel','core.window.AssWindow',
      			'fa.arp.paybalanceprdetail.PayBalancePRDetailGrid',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Submit',
      			'core.button.ResAudit','core.button.StrikeBalance','core.button.AssDetail','core.button.Print',
      			'core.button.Audit','core.button.ResSubmit','core.button.Post','core.button.ResPost','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted',
      			'core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger', 'core.form.SeparNumber','core.trigger.MultiDbfindTrigger'
      		
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick2(selModel, record);
        			var btn = Ext.getCmp('assdetail');
    				var ass = record.data['ca_asstype'];
    				if(!Ext.isEmpty(ass)){
    					btn.setDisabled(false);
    				} else {
    					btn.setDisabled(true);
    				}
    			} 
    		},
    		'erpGetSumAmountButton':{
    			click: function(btn){
    				var grid1 = Ext.getCmp("paybalanceprdetailGrid"),grid2 = Ext.getCmp("grid"),items1 = grid1.store.data.items,items2 = grid2.store.data.items;
    				var detailamount1 = 0;
    				var detailamount2 = 0;
    	    		Ext.each(items1,function(item,index){
    	    			if(!me.GridUtil.isBlank(grid1,item.data)) {
    	    				detailamount1 = detailamount1 + Number(item.data['pbpd_nowbalance']);
    	    			}
    	    		});
    	    		Ext.each(items2,function(item,index){
    	    			if(!me.GridUtil.isBlank(grid2,item.data)) {
    	    				detailamount2 = detailamount2 + Number(item.data['pbd_nowbalance']);
    	    			}
    	    		});
    	    		
    	    		Ext.getCmp("pb_amount").setValue(detailamount1);
    	    		Ext.getCmp("pb_vmamount").setValue(detailamount2);
    			}
    		},
    		'paybalanceprdetail': { 
    			afterrender:function(grid){
    				me.BaseUtil.getSetting('sys', 'useBillOutAP', function(bool) {
    					if(bool) Ext.getCmp('bi_code').show();
    	            });
    				grid.plugins[0].on('edit',function(e){
    					me.sumAmount();
    				});
    			},
    			itemclick: this.onGridItemClick1
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('PayBalance');
    				}
    				this.getapamount();
    				//保存之前的一些前台的逻辑判定
    				//this.beforeSavePayBalance();
    				this.beforeSave(this);
    			}
    		},
    		'erpToolbar': {
    			afterrender: function(tool){
    				tool.add({
    					xtype:'erpAssDetailButton',
    					disabled:true
    				});
    			}
    		},
    		'erpPostButton' : {
    			afterrender: function(btn){
    		        var status = Ext.getCmp('pb_statuscode');
    		        if(status && status.value != 'UNPOST'){
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
    		        var status = Ext.getCmp('pb_statuscode');
    		        if(status && status.value != 'POSTED'){
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
    				me.getapamount();
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPayBalance', '新增预付冲应付单', 'jsps/fa/arp/payBalancePRDetail.jsp?whoami='+caller);
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
    		        	poststatus = Ext.getCmp('pb_statuscode');
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
    		        poststatus = Ext.getCmp('pb_statuscode');
    		        if(status && status.value != 'COMMITED'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pb_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        poststatus = Ext.getCmp('pb_statuscode');
    		        if(status && status.value != 'COMMITED'){
    		        	btn.hide();
    		        }
    		        if(poststatus && poststatus.value == 'POSTED'){
    		        	btn.hide();
    		        }
    		    },
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pb_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    		        var status = Ext.getCmp(me.getForm(btn).statuscodeField),
    		        	postStatus = Ext.getCmp('pb_statuscode');
    		        if((status && status.value != 'AUDITED') ||(postStatus && postStatus.value == 'POSTED')){
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
    				t.gridErrorMessage="请先选择供应商|请选择冲账币别";
    			}
    		},
    		'field[name=pbpd_ordercode]': {
    			aftertrigger: function(){
    				var grid = Ext.getCmp('paybalanceprdetailGrid');
					var s  = grid.getStore().data.items;
					for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
						var data = s[i].data;
						dd = new Object();
						if(s[i].dirty && !me.GridUtil.isBlank(grid, data)){
							Ext.each(grid.columns, function(c){
								if((!c.isCheckerHd)&&(c.logic != 'ignore')){
			        				var d =data['pbpd_amount']-data['pbpd_havebalance'];
			        				s[i].set('pbpd_nowbalance', d);
									
								}
							});
						}
					}
					setTimeout(function(){
						me.sumAmount();
					},200);
    			},
    			afterrender:function(t){
    				t.gridKey="pb_vendcode|pb_currency";
    				t.mappinggirdKey="pp_vendcode|pp_vmcurrency";
    				t.gridErrorMessage="请先选择供应商|请选择币别";
    			}
    		},
    		'#bi_code':{
    			beforetrigger: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var pb_vendcode = Ext.getCmp('pb_vendcode');
    				var	pb_vmcurrency = Ext.getCmp('pb_vmcurrency');
    				if(!pb_vendcode || pb_vendcode.value==null||pb_vendcode.value=='') {
    					showError("请先选择供应商!");
    					return false;
    				} else if (!pb_vmcurrency || pb_vmcurrency.value==null||pb_vmcurrency.value==''){
    					showError("请先选择冲账币别!");
    					return false;
    				} else {
    					t.dbBaseCondition = " bi_vendcode = '" + pb_vendcode.value + "' and bi_currency='" + pb_vmcurrency.value + "'";
    				}
    			}
    		},
    		//抓取预付账款信息
    		'button[name=catchpr]':{
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
    				var grid = Ext.getCmp('paybalanceprdetailGrid');
    				  var bars=grid.query('toolbar'),toolbar=bars[0];
      			    var startdate=toolbar.items.items[2].value;
      			    var enddate=toolbar.items.items[4].value;
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
        					params['startdate'] = startdate;
        					params['enddate'] = enddate;
        					//抓取
        					Ext.Ajax.request({
        				   		url : basePath + form.catchPRUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				catchSuccess(function(){
        			    					//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   		    	var condition = 'pbpd_pbid='+value;
        					   		    	grid.getMyData(condition);
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
    		//清除预付账款信息
    		'button[name=cleanpr]':{
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
    				var grid = Ext.getCmp('paybalanceprdetailGrid');
    				warnMsg('确定清除所有明细行么?',function(t){
    					if(t=='yes'){
    						var form = Ext.getCmp('form');
    						var r = form.getValues();
    						var params = new Object();
    						params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
    						
    						Ext.Ajax.request({
        				   		url : basePath + form.cleanPRUrl,
        				   		params : params,
        				   		method : 'post',
        				   		callback : function(options,success,response){
        				   			me.FormUtil.getActiveTab().setLoading(false);
        				   			var localJson = new Ext.decode(response.responseText);
        			    			if(localJson.success){
        			    				cleanSuccess(function(){
        			    					//add成功后刷新页面进入可编辑的页面 
        					   				var value = r[form.keyField];
        					   		    	var condition = 'pbpd_pbid='+value;
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
    		//抓取发票信息
    		'button[name=catchab]':{
    			click:function(btn){
    				var params = new Object();
    				var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				  var bars=grid.query('toolbar'),toolbar=bars[0];
        			    var startdate=toolbar.items.items[3].value;
        			    var enddate=toolbar.items.items[5].value;
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
        					params.caller=caller;
        					params['startdate'] = startdate;
        					params['enddate'] = enddate;
        					params['bicode'] = Ext.getCmp('bi_code').value;
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
        					   						condition:'pbd_pbid='+value
        					   				};
        					   				grid.GridUtil.loadNewStore(grid, params);			
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
        					   						caller:'PayBalance!Arp!PADW',
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
    				var r = f.ownerCt.down('#pb_vmrate'), a = f.ownerCt.down('#pb_amount'), form = Ext.getCmp('form');
    				if(a.value != 0 && f.value != 0) {
    					var rate = Ext.Number.toFixed(f.value/a.value, 15);
    					if(form.BaseUtil.numberFormat(r.value,8) != form.BaseUtil.numberFormat(rate,8))
    						r.setValue(rate);
    				}
    				if(a.value == 0 || f.value == 0){
    					r.setValue(1);
    				}
    			}
    		},
    		'field[name=pb_amount]': {
    			change: function(f) {
    				var r = f.ownerCt.down('#pb_vmrate'), v = f.ownerCt.down('#pb_vmamount'), form = Ext.getCmp('form');
    				if(v.value != 0 && f.value != 0) {
    					var rate = Ext.Number.toFixed(v.value/f.value, 15);
    					if(form.BaseUtil.numberFormat(r.value,8) != form.BaseUtil.numberFormat(rate,8))
    						r.setValue(rate);
    				}
    				if(v.value == 0 || f.value == 0){
    					r.setValue(1);
    				}
    			}
    		}
    	});
    },
    beforeSubmit:function(){
    	var me = this, form = Ext.getCmp('form');
    	var grid1 =	Ext.getCmp('paybalanceprdetailGrid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('grid'),items2=grid2.store.data.items;
		
		var sameCurrency = true;
		var amount = Number(Ext.getCmp('pb_amount').getValue());	//预付金额
		var vmamount = Number(Ext.getCmp('pb_vmamount').getValue());	//预付金额
		var cmcurrency = Ext.getCmp('pb_vmcurrency').getValue();
		var currency = Ext.getCmp('pb_currency').getValue();
		var cmrate = Number(Ext.getCmp('pb_vmrate').getValue());	//冲账汇率
		var amountsum = 0;
		var vmamountsum = 0;
		Ext.each(items1,function(item,index){
			if(item.data[grid1.necessaryField]!=''&&item.data[grid1.necessaryField]!=null){
				amountsum = amountsum+Number(item.data['pbpd_nowbalance']);
				if(currency!=item.data['pbpd_currency']){
					//从表币别有与主表币别不同的
					sameCurrency = false;
				}
			}
		});
		Ext.each(items2,function(item,index){
			if(!me.GridUtil.isBlank(grid2, item.data)){
				vmamountsum = vmamountsum+Number(item.data['pbd_nowbalance']);
				if(cmcurrency!=item.data['pbd_currency']){
					//从表币别有与主表币别不同的
					sameCurrency = false;
				}
			}
		});
		if(!sameCurrency){
			//从表币别有与主表币别不同的
			showError('明细行币别与币别不同,不能提交');return;
		}
		vmamount = form.BaseUtil.numberFormat(vmamount,2);
		amountsum = form.BaseUtil.numberFormat(amountsum,2);
		amount = form.BaseUtil.numberFormat(amount,2);
		vmamountsum = form.BaseUtil.numberFormat(vmamountsum,2);
    	if(Math.abs(amount-amountsum)>0.001){
    		//冲账金额与明细行本次结算总和不等
    		showError('预付账款明细行本次结算与预付金额不等,不能提交');return;
    	}
    	if(Math.abs(vmamount-vmamountsum)>0.001){
    		//冲账金额与明细行本次结算总和不等
    		showError('发票明细行本次结算与冲账金额不等,不能提交');return;
    	}
    	me.FormUtil.onSubmit(Ext.getCmp('pb_id').value, false, this.beforeUpdate, this);
    },
    beforePost:function(){
    	var me = this, form = Ext.getCmp('form');
    	var grid1 =	Ext.getCmp('paybalanceprdetailGrid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('grid'),items2=grid2.store.data.items;
		
		var sameCurrency = true;
		var amount = Number(Ext.getCmp('pb_amount').getValue());	//预付金额
		var vmamount = Number(Ext.getCmp('pb_vmamount').getValue());	//预付金额
		var cmcurrency = Ext.getCmp('pb_vmcurrency').getValue();
		var currency = Ext.getCmp('pb_currency').getValue();
		var cmrate = Number(Ext.getCmp('pb_vmrate').getValue());	//冲账汇率
		var amountsum = 0;
		var vmamountsum = 0;

/*		Ext.each(items1,function(item,index){
			amountsum = amountsum+Number(item.data['pbpd_nowbalance']);
			if(currency!=item.data['pbpd_currency']){
				//从表币别有与主表币别不同的
				//抛出异常
				sameCurrency = false;
			}
		});*/
		Ext.each(items2,function(item,index){
			vmamountsum = vmamountsum+Number(item.data['pbd_nowbalance']);
/*			if(cmcurrency!=item.data['pbd_currency']){
				//从表币别有与主表币别不同的
				//抛出异常
				sameCurrency = false;
			}*/
		});
/*    	vmamount = form.BaseUtil.numberFormat(vmamount,2);
		amountsum = form.BaseUtil.numberFormat(amountsum,2);
		amount = form.BaseUtil.numberFormat(amount,2);
		vmamountsum = form.BaseUtil.numberFormat(vmamountsum,2);	
		if(Math.abs(vmamount-vmamount)>0.001){
    			showError('冲账金额不正确,不能过账');return;
    	}
		if(!sameCurrency){
    			//从表币别有与主表币别不同的
				//抛出异常
    			showError('明细行币别与币别不同,不能过账');return;
    	}
		if(Math.abs(amount-amountsum)>0.001){
    			//冲账金额与明细行本次结算总和不等
    			//抛出异常
    			showError('预付账款明细行本次结算与预付金额不等,不能过账');return;
    			
    	}
		if(Math.abs(vmamount-vmamountsum)>0.001){
    			//冲账金额与明细行本次结算总和不等
    			//抛出异常
    			showError('发票明细行本次结算与冲账金额不等,不能过账');return;
    			
    	}*/
    	me.FormUtil.onPost(Ext.getCmp('pb_id').value);
    },
    sumAmount:function(){
    	var grid = Ext.getCmp('paybalanceprdetailGrid');
    	var items = grid.store.data.items;
    	var sumamount = 0;
    	var text='本次结算金额(sum):';
    	Ext.each(items,function(item,index){
        	if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
        		sumamount = sumamount + Ext.Number.from(item.data['pbpd_nowbalance'],0);
        	}
    		
    	});
    	text=text+Ext.Number.from(sumamount,0);
		if(Ext.getCmp('pbpd_nowbalance_sum')){
			Ext.getCmp('pbpd_nowbalance_sum').setText(text);
		}
    	
    },
    onGridItemClick1: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('paybalanceprdetailGrid');
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
	beforeSavePayBalance: function(){
		
		//预付冲应收
		if(caller =='PayBalance!Arp!PADW'){
			var amount = Ext.getCmp('pb_amount').value;	//预付余额
			var vmamount = Ext.getCmp('pb_vmamount').value;	//冲账金额
			
			amount = Ext.Number.from(amount,0);
			vmamount = Ext.Number.from(vmamount,0);
			if(amount<vmamount){
				showError('预付金额不能小于冲账金额,不能保存');return;
			}
		}

		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		var warnStr='';
		Ext.each(items,function(item,index){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
				var apamount =item.data['pbd_apamount'];	//订单金额
				var havepay = item.data['pbd_havepay'];	//已预付金额
				var nowbalance = item.data['pbd_nowbalance'];	//本次预付额
				//订单金额>=已预付金额+本次预付额
				
				//此种情况不能进行保存
				if(havepay+nowbalance>apamount){
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
				showError('明细表第'+warnStr+'行，已付金额与本次付款额的合计超过发票金额,不能保存');return;
			}
		}

	},
	beforeUpdatePayBalance: function(){
		var grid = Ext.getCmp('grid');
	    Ext.Array.each(grid.store.data.items, function(item){
	    	item.set('pbd_pbid',Ext.getCmp('pb_id').value);
		});
		//采购价格不能为0
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		var warnStr='';
		Ext.each(items,function(item,index){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
				var apamount =item.data['pbd_apamount'];	//发票金额
				var havepay = item.data['pbd_havepay'];	//已付金额
				var nowbalance = item.data['pbd_nowbalance'];	//本次付款额
				//订单金额>=已预付金额+本次预付额
				//此种情况不能进行保存
				if(havepay+nowbalance>apamount){
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
			this.beforeUpdate();
		}else{
			if(warnStr != ''||warnStr.length>0){
				showError('明细表第'+warnStr+'行，已付金额与本次付款额的合计超过发票金额,不能保存');return;
			}
		}
	},
		
	beforeUpdate:function(){
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
		var grids = Ext.ComponentQuery.query('gridpanel');
		if(grids.length > 0){
			var param1 = mm.GridUtil.getGridStore(grids[0]);
			var param2 = mm.GridUtil.getGridStore();
			var param3 = new Array();
			Ext.each(Ext.Object.getKeys(Ext.getCmp('assdetail').cacheStoreGrid), function(key){
				Ext.each(Ext.getCmp('assdetail').cacheStoreGrid[key], function(d){
					d['dass_condid'] = key;
					param3.push(d);
				});
			});
			mm.onUpdate(param1,param2,param3);
		}else {
			mm.onSave([],[],[]);
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
		var grids = Ext.ComponentQuery.query('gridpanel');
		
		if(grids.length > 0){
			
			var param1 = me.GridUtil.getGridStore(grids[0]);
			
			var param2 = me.GridUtil.getGridStore();
			var param3 = new Array();
			
			Ext.each(Ext.Object.getKeys(Ext.getCmp('assdetail').cacheStoreGrid), function(key){
				Ext.each(Ext.getCmp('assdetail').cacheStoreGrid[key], function(d){
					d['dass_condid'] = key;
					param3.push(d);
				});
			});
			if(grids[1].necessaryField.length > 0 && (param2 == null || param2 == '')){
				//showError($I18N.common.grid.emptyDetail);//i18n/i18n.js
				warnMsg('明细表还未添加数据,是否继续?', function(btn){
					if(btn == 'yes'){
						mm.onSave(param1,param2);
					} else {
						return;
					}
				});
			} else {
				mm.onSave(param1,param2,param3);
			}
		}else {
			mm.onSave([],[],[]);
		}
	},
	onUpdate:function(param1,param2,param3){
		var me = this;
		var form = Ext.getCmp('form');
		param1 = param1 == null ? [] : "[" + param1.toString() + "]";
		param2 = param2 == null ? [] : "[" + param2.toString() + "]";
		param3 = param3 == null ? [] : Ext.encode(param3).replace(/\\/g,"%");
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
			me.FormUtil.update(r, param1,param2,param3);
		}else{
			me.FormUtil.checkForm();
		}
	},
	/**
	 * 单据保存
	 * @param param 传递过来的数据，比如gridpanel的数据
	 */
	onSave: function(param1,param2,param3){
		var me = this;
		var form = Ext.getCmp('form');
		param1 = param1 == null ? [] : "[" + param1.toString() + "]";
		param2 = param2 == null ? [] : "[" + param2.toString() + "]";
		param3 = param3 == null ? [] : Ext.encode(param3).replace(/\\/g,"%");
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
			me.FormUtil.save(r, param1,param2,param3);
		}else{
			me.FormUtil.checkForm();
		}
	},
	//计算冲账收款金额   并写入主表 冲账收款金额字段
	getapamount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var apamount = 0;
		Ext.each(items,function(item,index){
			if(item.data['pbd_ordercode']!=null&&item.data['pbd_ordercode']!=""){
				apamount= apamount + Number(item.data['pbd_nowbalance']);
			}
		});
		Ext.getCmp('pb_jsamount').setValue(apamount.toFixed(2));
	},
	
	//beforeSavePayBalance 之后执行      需要解析出两个grid中的数据  第一个grid如果没有数据  不进行提醒     
	//beforeSavePayBalance中还需要判断grid1中的数据是否足够预付预付金额  超过则提醒   不足则在后台自动添加
	//如果第一个grid中填写了数据   则在后台判断是否足够预付金额    如果足够预付金额 则不添加新的
	save:function(){
		
	},
	update:function(){
	}
});