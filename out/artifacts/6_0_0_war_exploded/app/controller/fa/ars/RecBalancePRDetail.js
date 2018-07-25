Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.RecBalancePRDetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.RecBalance','core.grid.Panel2','core.toolbar.Toolbar','core.form.ConDateField',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Submit','core.button.ResAudit',
  			'core.button.Audit','core.button.ResSubmit','core.button.Post','core.button.ResPost','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted','core.button.StrikeBalance',
      		'core.grid.AssPanel','core.window.AssWindow','core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.button.Print','fa.ars.recbalanceprdetail.RecBalancePRDetailGrid',
      		'core.button.AssDetail', 'core.form.SeparNumber','core.trigger.MultiDbfindTrigger','core.button.GetSumAmount'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			afterrender: function(btn) {
    				if(caller=='RecBalance!PTAR'){
    					me.BaseUtil.getSetting('sys', 'useBillOutAR', function(bool) {
    						if (bool) {
    							me.BaseUtil.getSetting('RecBalance!PBIL', 'useAPCheck', function(bool2) {
			    	            	if(bool2) {
				    					Ext.getCmp('ac_code').show();
				    					Ext.getCmp('bi_code').hide();
			    	            	}else{
			    	            		Ext.getCmp('ac_code').hide();
				    					Ext.getCmp('bi_code').show();
			    	            	}
			    	            });
    						}
    					});
    				}
    			},
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
    		'recbalanceprdetail': { 
    			itemclick: this.onGridItemClick1,
    			afterrender:function(grid){
    				grid.plugins[0].on('edit',function(e){
    					me.sumAmount();
    				});
    			}
    		},
    		'#bi_code':{
    			beforetrigger: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.isEmpty(Ext.getCmp("rb_custcode").value)) {
    					showError("请先选择客户!");
    					return false;
    				} else if (Ext.isEmpty(Ext.getCmp("rb_cmcurrency").value)){
    					showError("请先选择冲账币别!");
    					return false;
    				} else {
    					t.dbBaseCondition = " bi_custcode = '" + Ext.getCmp("rb_custcode").value + "' and bi_currency='" + Ext.getCmp("rb_cmcurrency").value + "'";
    				}
    			}
    		},
    		'#ac_code':{
    			beforetrigger: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.isEmpty(Ext.getCmp("rb_custcode").value)) {
    					showError("请先选择客户!");
    					return false;
    				} else if (Ext.isEmpty(Ext.getCmp("rb_cmcurrency").value)){
    					showError("请先选择冲账币别!");
    					return false;
    				} else {
    					t.dbBaseCondition = " ac_custcode = '" + Ext.getCmp("rb_custcode").value + "' and ac_currency='" + Ext.getCmp("rb_cmcurrency").value + "'";
    				}
    			}
    		},
    		'erpGetSumAmountButton':{
    			click: function(btn){
    				var grid1 = Ext.getCmp("recbalanceprdetailGrid"),grid2 = Ext.getCmp("grid"),items1 = grid1.store.data.items,items2 = grid2.store.data.items;
    				var detailamount1 = 0;
    				var detailamount2 = 0;
    	    		Ext.each(items1,function(item,index){
    	    			if(!me.GridUtil.isBlank(grid1,item.data)) {
    	    				detailamount1 = detailamount1 + Number(item.data['rbpd_nowbalance']);
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
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('RecBalance');
    				}
    				this.getAramount();
    				//保存之前的一些前台的逻辑判定
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
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addRecBalance', '新增预收冲应收单', 'jsps/fa/ars/recBalancePRDetail.jsp?whoami='+caller);
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
    		'field[name=rbd_ordercode]': {
    			afterrender:function(t){
    				t.gridKey="rb_custcode|rb_cmcurrency";
    				t.mappinggirdKey="ab_custcode|ab_currency";
    				t.gridErrorMessage="请先选择客户|请选择冲账币别";
    			}
    		},
    		'field[name=rbpd_ordercode]': {
    			aftertrigger: function(){
    				var grid = Ext.getCmp('recbalanceprdetailGrid');
					var s  = grid.getStore().data.items;
					for(var i=0;i<s.length;i++){
						var data = s[i].data;
						dd = new Object();
						if(s[i].dirty && !me.GridUtil.isBlank(grid, data)){
							Ext.each(grid.columns, function(c){
								if((!c.isCheckerHd)&&(c.logic != 'ignore')){
			        				var d =data['rbpd_amount']-data['rbpd_havebalance'];
			        				s[i].set('rbpd_nowbalance', d);
									
								}
							});
						}
					}
					setTimeout(function(){
						me.sumAmount();
					},200);
    			},
    			afterrender:function(t){
    				console.log(t);
    				t.gridKey="rb_custcode|rb_currency";
    				t.mappinggirdKey="pr_custcode|pr_cmcurrency";
    				t.gridErrorMessage="请先选择客户|请选择币别";
    			}
    		},
    		//抓取预收账款信息
    		'button[name=catchpr]':{
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
    				var params = new Object(),form = Ext.getCmp('form'),grid = Ext.getCmp('recbalanceprdetailGrid'),startdate,enddate;
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
        				    var baseDate=Ext.getCmp('rb_date').value,currentmonth=baseDate.getMonth();
        				    if(enddate && enddate.getMonth()>currentmonth){
        				    	Ext.Msg.alert('提示','日期区间不能超过当前单据月份!');
        				    	return;
        				    }
        					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
        					if(startdate!=null) params.startdate=Ext.Date.format(startdate,'Y-m-d');
                            if(enddate !=null)  params.enddate=Ext.Date.format(enddate,'Y-m-d');
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
        					   		    	var condition = 'rbpd_rbid='+value;
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
    		//清除预收账款信息
    		'button[name=cleanpr]':{
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
    				var grid = Ext.getCmp('recbalanceprdetailGrid');
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
        					   		    	var condition = 'rbpd_rbid='+value;
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
    				var bars=grid.query('toolbar'),toolbar=bars[0];
    				var accode = toolbar.items.items[3];
    				var bicode = null;
					if(accode&&!accode.isHidden()){
						bicode = accode.value;
					}else{
						bicode=toolbar.items.items[2].value;
					}
       			    var startdate=toolbar.items.items[4].value;
        			var enddate=toolbar.items.items[6].value;
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
        					if(startdate!=null) params.startdate=Ext.Date.format(startdate,'Y-m-d');
                            if(enddate !=null)  params.enddate=Ext.Date.format(enddate,'Y-m-d');
                            if(bicode !=null)  params.bicode=bicode;
        					params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
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
        					   						caller:'RecBalance!PTAR',
        					   						condition:'rbd_rbid='+value
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
        					   						caller:'RecBalance!PTAR',
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
    				var a = f.ownerCt.down('#rb_amount'), r = f.ownerCt.down('#rb_cmrate'), form = Ext.getCmp('form');
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
    		'field[name=rb_amount]': {
    			change: function(f) {
    				var v = f.ownerCt.down('#rb_cmamount'), r = f.ownerCt.down('#rb_cmrate'), form = Ext.getCmp('form');
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
    	var grid1 =	Ext.getCmp('recbalanceprdetailGrid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('grid'),items2=grid2.store.data.items;
		
		var sameCurrency = true;
		var amount = Number(Ext.getCmp('rb_amount').getValue());	//预收金额
		var cmamount = Number(Ext.getCmp('rb_cmamount').getValue());	//预收金额
		var cmcurrency = Ext.getCmp('rb_cmcurrency').getValue();
		var currency = Ext.getCmp('rb_currency').getValue();
		var cmrate = Number(Ext.getCmp('rb_cmrate').getValue());	//冲账汇率
		var amountsum = 0;
		var cmamountsum = 0;
		
		Ext.each(items1,function(item,index){
			if(item.data[grid1.necessaryField]!=''&&item.data[grid1.necessaryField]!=null){
				amountsum = amountsum+Number(item.data['rbpd_nowbalance']);
				if(currency!=item.data['rbpd_currency']){
					//从表币别有与主表币别不同的
					//抛出异常
					sameCurrency = false;
				}
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
		});
		cmamount = form.BaseUtil.numberFormat(cmamount,2);
		amountsum = form.BaseUtil.numberFormat(amountsum,2);
		amount = form.BaseUtil.numberFormat(amount,2);
		cmamountsum = form.BaseUtil.numberFormat(cmamountsum,2);
    	if(Math.abs(cmamount-form.BaseUtil.numberFormat(amount*cmrate,2))>=0.01){
    		showError('冲账金额不正确,不能提交');return;
    	}
    	if(!sameCurrency){
    		//从表币别有与主表币别不同的
			//抛出异常
    		showError('明细行币别与币别不同,不能提交');return;
    	}
    	if(Math.abs(amount-amountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		//抛出异常
    		showError('预收账款明细行本次结算与预收金额不等,不能提交');return;
    	}
    	if(Math.abs(cmamount-cmamountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		//抛出异常
    		showError('发票明细行本次结算与冲账金额不等,不能提交');return;
    	}
    	me.FormUtil.onSubmit(Ext.getCmp('rb_id').value, false, this.beforeUpdate, this);
    },
    beforePost:function(){
    	var me = this, form = Ext.getCmp('form');
    	var grid1 =	Ext.getCmp('recbalanceprdetailGrid'),items1=grid1.store.data.items;
		var grid2 = Ext.getCmp('grid'),items2=grid2.store.data.items;
		
		var sameCurrency = true;
		var amount = Number(Ext.getCmp('rb_amount').getValue());	//预收金额
		var cmamount = Number(Ext.getCmp('rb_cmamount').getValue());	//预收金额
		var cmcurrency = Ext.getCmp('rb_cmcurrency').getValue();
		var currency = Ext.getCmp('rb_currency').getValue();
		var cmrate = Number(Ext.getCmp('rb_cmrate').getValue());	//冲账汇率
		var amountsum = 0;
		var cmamountsum = 0;

		Ext.each(items1,function(item,index){
			amountsum = amountsum+Number(item.data['rbpd_nowbalance']);
			if(currency!=item.data['rbpd_currency']){
				//从表币别有与主表币别不同的
				//抛出异常
				sameCurrency = false;
			}
		});
		Ext.each(items2,function(item,index){
			cmamountsum = cmamountsum+Number(item.data['rbd_nowbalance']);
			var rbd_currency=item.data['rbd_currency']
			if(cmcurrency!=rbd_currency&&rbd_currency){
				//从表币别有与主表币别不同的
				//抛出异常
				sameCurrency = false;
			}
		});
		cmamount = form.BaseUtil.numberFormat(cmamount,2);
		amountsum = form.BaseUtil.numberFormat(amountsum,2);
		amount = form.BaseUtil.numberFormat(amount,2);
		cmamountsum = form.BaseUtil.numberFormat(cmamountsum,2);
    	if(Math.abs(cmamount-form.BaseUtil.numberFormat(amount*cmrate,2))>=0.01){
    		showError('冲账金额不正确,不能过账');return;
    	}
    	if(!sameCurrency){
    		//从表币别有与主表币别不同的
			//抛出异常
    		showError('明细行币别与币别不同,不能过账');return;
    	}
    	if(Math.abs(amount-amountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		//抛出异常
    		showError('预收账款明细行本次结算与预收金额不等,不能过账');return;
    	}
    	if(Math.abs(cmamount-cmamountsum)>=0.01){
    		//冲账金额与明细行本次结算总和不等
    		//抛出异常
    		showError('发票明细行本次结算与冲账金额不等,不能过账');return;
    	}
    	me.FormUtil.onPost(Ext.getCmp('rb_id').value);
    },
    
    sumAmount:function(){
    	var grid = Ext.getCmp('recbalanceprdetailGrid');
    	var items = grid.store.data.items;
    	var sumamount = 0;
    	var text='本次结算金额(sum):';
    	Ext.each(items,function(item,index){
        	if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
        		sumamount = sumamount + Ext.Number.from(item.data['rbpd_nowbalance'],0);
        	}
    	});
    	text=text+Ext.Number.from(sumamount,0);
		if(Ext.getCmp('rbpd_nowbalance_sum')){
			Ext.getCmp('rbpd_nowbalance_sum').setText(text);
		}
    },
    
    onGridItemClick1: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('recbalanceprdetailGrid');
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
	
	beforeSaveRecBalance: function(){
		//预收冲应收
		if(caller =='RecBalance!PTAR'){
			var beginlast = Ext.getCmp('rb_beginlast').value;	//预收余额
			var cmamount = Ext.getCmp('rb_cmamount').value;	//冲账金额
			beginlast = Ext.Number.from(beginlast,0);
			cmamount = Ext.Number.from(cmamount,0);
			if(Math.abs(beginlast-cmamount)<=0.01){
				showError('预收金额不能小于冲账金额,不能保存');return;
			}
		}
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
	
	beforeUpdateRecBalance: function(){
		//预收账款维护
		if(caller =='RecBalance!PTAR'){
			var beginlast = Ext.getCmp('rb_beginlast').value;	//预收余额
			var cmamount = Ext.getCmp('rb_cmamount').value;	//冲账金额
			beginlast = Ext.Number.from(beginlast,0);
			cmamount = Ext.Number.from(cmamount,0);
			if(Math.abs(beginlast-cmamount)<=0.01){
				showError('预收金额不能小于冲账金额,不能保存');return;
			}
		}
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
				if(Math.abs(havepay+nowbalance-aramount) >= 0.01){
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
				showError('明细表第'+warnStr+'行 已收金额与本次结算额的合计超过发票金额,不能保存');return;
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
	}
});