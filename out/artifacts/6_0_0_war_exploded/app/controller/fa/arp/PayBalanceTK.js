Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.PayBalanceTK', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.arp.PayBalanceTK','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.SeparNumber',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.AssMain','core.button.Accounted','core.button.StrikeBalance',
      		'core.grid.AssPanel','core.window.AssWindow','core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.button.Post','core.button.ResPost','core.button.Print',
      		'core.button.Submit','core.button.ResAudit','core.button.Audit','core.button.ResSubmit','core.button.AssDetail','core.button.AssMain', 'core.trigger.MultiDbfindTrigger'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick(selModel, record);
    				if(caller == 'PayBalance!TK'){
    					var btn = Ext.getCmp('assdetail');
        				var ass = record.data['ca_asstype'];
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
    				if(caller == 'PayBalance!TK'){
        				if(Ext.getCmp('ca_asstype').getValue()==null||Ext.getCmp('ca_asstype').getValue()==""){
        					btn.setDisabled(true);
        				}else{
        					btn.setDisabled(false);
        				}
    				}

    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber('PayBalance');
    				}
    				me.getApamount();
    				me.beforeSave(this);
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				if(caller == 'PayBalance!TK'){
    					//辅助核算
        				btn.ownerCt.add({
        					xtype:'erpAssDetailButton',
        					disabled:true
        				});
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
    		'field[name=pb_date]':{
				beforerender: function(field){
					if(Ext.getCmp('pb_source') && (Ext.getCmp('pb_source').value=="Bank" || Ext.getCmp('pb_source').value=="应付票据" || Ext.getCmp('pb_source').value=="应付票据退票" || Ext.getCmp('pb_source').value=="背书转让")){
						field.readOnly=true;
					}
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
    				var source = Ext.getCmp('pb_source');
    				if(source && source.value){
    					showError("请在来源:"+source.value+",单号："+Ext.getCmp('pb_sourcecode').value+"中进行反审核或者反记账操作！");
    					return;
    				}
    				me.FormUtil.onDelete(Ext.getCmp('pb_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.getApamount();
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
    				me.FormUtil.onAdd('addPayBalance', title, 'jsps/fa/arp/paybalanceTK.jsp?whoami='+caller);
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
    				me.FormUtil.onSubmit(Ext.getCmp('pb_id').value, false, this.beforeUpdate, this);
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
    				var reportName="APPay";
    				var condition = '{PayBalance.pb_id}=' + Ext.getCmp('pb_id').value + '';
    				var id = Ext.getCmp('pb_id').value;
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'field[name=pb_amount]':{
				beforerender: function(field){
					if(Ext.getCmp('pb_sourcecode')&&Ext.getCmp('pb_sourcecode').value!=""){
						field.readOnly=true;
					}
				}
			},
    		'numberfield[name=pb_amount]':{
    			beforerender:function(num){
    				num.minValue = Number.NEGATIVE_INFINITY;
    				num.setMinValue(num.minValue);
    				b = num.baseChars+"";
    				b += num.decimalSeparator;
    				b += "-";
    				b = Ext.String.escapeRegex(b);
    				num.maskRe = new RegExp("[" + b + "]");
    			},
    			change: function(f) {
    				var v1 = (f.value || 0),
    					v2 = (Ext.getCmp('pb_vmamount').value || 0);
    				if(v1 == 0) {
    					Ext.getCmp('pb_vmrate').setValue(0);
    				} else {
    					Ext.getCmp('pb_vmrate').setValue(Ext.Number.toFixed(v2/v1, 8));
    				}
    			}
    		},
    		'numberfield[name=pb_vmamount]':{
    			beforerender:function(num){
    				num.minValue = Number.NEGATIVE_INFINITY;
    				num.setMinValue(num.minValue);
    				b = num.baseChars+"";
    				b += num.decimalSeparator;
    				b += "-";
    				b = Ext.String.escapeRegex(b);
    				num.maskRe = new RegExp("[" + b + "]");
    			},
    			change: function(f) {
    				var v1 = (Ext.getCmp('pb_amount').value || 0),
    					v2 = (f.value || 0);
    				if(v1 == 0) {
    					Ext.getCmp('pb_vmrate').setValue(0);
    				} else {
    					Ext.getCmp('pb_vmrate').setValue(Ext.Number.toFixed(v2/v1, 8));
    				}
    			}
    		},
    		'numberfield[name=pb_apamount]':{
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
    		'cateTreeDbfindTrigger[name=pb_catecode]':{
    			aftertrigger:function(trigger, data){
    				Ext.getCmp('pb_catename').setValue(data[0].raw.data.ca_name);
    				if (caller == 'PayBalance!TK'){
	    				var asstype = data[0].raw.data.ca_asstype;
	    				var btn = Ext.getCmp("assmainbutton");
	    				if(Ext.isEmpty(asstype)) {
	    					btn.setDisabled(true);
	    				} else {
	    					btn.setDisabled(false);
	    				}
    				}
    			}
    		},
    		'field[name=pbd_ordercode]':{
    			afterrender:function(t){
    				t.gridKey="pb_vendcode";
    				t.mappinggirdKey="ab_vendcode";
    				t.gridErrorMessage="请先选择供应商";
    			}
    		},
    		'textfield[name=pb_amount]':{
    			change:function(t){
    				if(caller == 'PayBalance!TK'){
        				var pb_amount = Ext.Number.from(Ext.getCmp('pb_amount').getValue(),0);//付款金额 
        				var pb_vmrate = Ext.Number.from(Ext.getCmp('pb_vmrate').getValue(),0);	//冲账汇率
        				var pb_vmamount = pb_amount*pb_vmrate;	//	冲账金额
        				pb_vmamount = Ext.util.Format.number(pb_vmamount,'0.00');
        				Ext.getCmp('pb_vmamount').setValue(pb_vmamount);
    				}
    			}
    		},
    		'textfield[name=pb_vmrate]':{
    			change:function(t){
    				if(caller == 'PayBalance!TK'){
        				var pb_amount = Ext.Number.from(Ext.getCmp('pb_amount').getValue(),0);//付款金额 
        				var pb_vmrate = Ext.Number.from(Ext.getCmp('pb_vmrate').getValue(),0);	//冲账汇率
        				var pb_vmamount = pb_amount*pb_vmrate;	//	冲账金额
        				pb_vmamount = Ext.util.Format.number(pb_vmamount,'0.00');
        				Ext.getCmp('pb_vmamount').setValue(pb_vmamount);
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
					var poststatus = Ext.getCmp('pb_statuscode');
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
    				var startdate = btn.ownerCt.items.items[2].rawValue;
    				var enddate = btn.ownerCt.items.items[4].rawValue;
    				Ext.each(items,function(item,index){
    					if(item.get('pbd_id')>0 || (item.dirty && !me.GridUtil.isBlank(grid,item.data))){
    						array.push(item);
    					}
    				});
    				
    				var pb_id = Ext.getCmp('pb_id').value;
    				if(!pb_id||(pb_id&&(pb_id == 0||pb_id==''||pb_id==null))){
     					Ext.Msg.alert('提示', '请先保存单据');
     					
     				}else{
     					if(array.length>0){
        					Ext.Msg.alert('提示', '需要先清除明细行中的数据!');
        				} else {
        					params['startdate'] = startdate;
        					params['enddate'] = enddate;
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
        					   						caller:caller,
        					   						condition:'pbd_pbid='+value
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
					var poststatus = Ext.getCmp('pb_statuscode');
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
    		}
    	});
    }, 
    beforeSubmit: function(){
    	var me = this;
    	if(caller == 'PayBalance!TK'){
    		var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    		var sameCurrency = true;
    		var amount = Number(Ext.getCmp('pb_amount').getValue());
    		var vmamount =Number(Ext.getCmp('pb_vmamount').getValue());
    		var vmcurrency = Ext.getCmp('pb_vmcurrency').getValue();
    		var vmrate = Number(Ext.getCmp('pb_vmrate').getValue());
    		var detailamount = 0;
    		var currency = Ext.getCmp('pb_currency').getValue();
    		if(amount != 0 && vmamount != 0){
	    		if(Ext.Number.toFixed(vmrate, 8)!= Ext.Number.toFixed(vmamount/amount, 8)){
	    			showError('冲账汇率不正确,不能提交!');
					return;
				}
    		}
    		if(currency == vmcurrency){
				if(vmrate != 1){
					showError('币别相同，冲账汇率不等于1,不能提交!');
					return;
				}
			}
    		if(currency != vmcurrency){
				if(vmrate == 1){
					showError('币别不相同，冲账汇率等于1,不能提交!');
					return;
				}
			}
    		Ext.each(items,function(item,index){
    			if(!me.GridUtil.isBlank(grid, item.data)){
	    			detailamount = detailamount+Number(item.data['pbd_nowbalance']);
	    			if(vmcurrency!=item.data['pbd_currency']){
	    				//从表币别有与主表币别不同的
	    				//抛出异常
	    				sameCurrency = false;
	    			}
    			}
    		});
//    		if(Ext.Number.toFixed(vmamount, 2)!= Ext.Number.toFixed(amount*vmrate, 2)){
    		if(Math.abs(vmamount-amount*vmrate)>0.01){
    			showError('冲账金额不正确,不能提交');return;
    		}
    		if(!sameCurrency){
    			//从表币别有与主表币别不同的
				//抛出异常
    			showError('明细行币别与冲账币别不同,不能提交');return;
    		}
//    		if(Ext.Number.toFixed(vmamount, 2)!= Ext.Number.toFixed(detailamount, 2)){
    		if(Math.abs(vmamount-detailamount)>0.01){
    			//冲账金额与明细行本次结算总和不等
    			//抛出异常
    			showError('明细行本次结算与冲账金额不等,不能提交');return;
    			
    		}
			me.FormUtil.onSubmit(Ext.getCmp('pb_id').value);
    		
    		}else{
    			me.FormUtil.onSubmit(Ext.getCmp('pb_id').value);
    		}
    },
    beforePost:function(btn){
    	var me = this;
		var warn = new Array();
    	if(caller == 'PayBalance!TK'){
    		var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    		var sameCurrency = true;
    		var amount = Number(Ext.getCmp('pb_amount').getValue());
    		var vmamount =Number(Ext.getCmp('pb_vmamount').getValue());
    		var vmcurrency = Ext.getCmp('pb_vmcurrency').getValue();
    		var vmrate = Number(Ext.getCmp('pb_vmrate').getValue());
    		var detailamount = 0;
    		var currency = Ext.getCmp('pb_currency').getValue();
    		if(amount != 0 && vmamount != 0){
	    		if(Ext.Number.toFixed(vmrate, 8)!= Ext.Number.toFixed(vmamount/amount, 8)){
	    			showError('冲账汇率不正确,不能过账!');
					return;
				}
    		}
    		if(currency == vmcurrency){
				if(vmrate != 1){
					showError('币别相同，冲账汇率不等于1,不能过账!');
					return;
				}
			}
    		if(currency != vmcurrency){
				if(vmrate == 1){
					showError('币别不相同，冲账汇率等于1,不能过账!');
					return;
				}
			}
    		Ext.each(items,function(item,index){
    			if(!me.GridUtil.isBlank(grid, item.data)){
	    			detailamount = detailamount+Number(item.data['pbd_nowbalance']);
					var havepay = item.get('pbd_havepay');	//已付款额
					var nowbalance = item.get('pbd_nowbalance');	//本次退款额
					var pbd_ordercode = item.get('pbd_ordercode');    //发票编号
					if(item.data['pbd_ordercode']!=null&&item.data['pbd_ordercode']!=""){
						//本次退款额 >已付款额					
						if(Math.abs(nowbalance) > Math.abs(havepay)){
							warn.push(item.get('pbd_detno'));
						}
					}
	    			if(vmcurrency!=item.data['pbd_currency']){
	    				//从表币别有与主表币别不同的
	    				//抛出异常
	    				sameCurrency = false;
	    			}
    			}
    		});
    		if(warn.length > 0){
    			showError('明细第' + warn.join(',') + '行 本次退款额超过已付款额,不能过账!');return;
    		}
    		if(Math.abs(vmamount-amount*vmrate)>0.01){
    			showError('冲账金额不正确,不能过账');return;
    		}
    		if(!sameCurrency){
    			//从表币别有与主表币别不同的
				//抛出异常
    			showError('明细行币别与冲账币别不同,不能过账');return;
    		}
    		//
       		if(Math.abs(vmamount-detailamount)>0.01){
    			//冲账金额与明细行本次结算总和不等
    			//抛出异常
    			showError('明细行本次结算与冲账金额不等,不能过账');return;
    		}
    		me.FormUtil.onPost(Ext.getCmp('pb_id').value);
    		}else{
    			me.FormUtil.onPost(Ext.getCmp('pb_id').value);
    		}
    },
	getApamount: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var apamount = 0;
		Ext.each(items,function(item,index){
			if(item.data['pbd_ordercode']!=null&&item.data['pbd_ordercode']!=""){
				apamount= apamount + Number(item.data['pbd_nowbalance']);
			}
		});
		Ext.getCmp('pb_apamount').setValue(Ext.util.Format.number(apamount,'0.00'));
		
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
		/*if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		}*/
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
		var grid = Ext.getCmp('grid'), key = Ext.getCmp('pb_id').value;
		var warn = new Array();
		grid.store.each(function(item){
			if(item.dirty) {
				item.set('pbd_pbid', key);
				if(!grid.necessaryField || !Ext.isEmpty(item.get(grid.necessaryField))){
					var apamount = item.get('pbd_apamount');	//订单金额
					var havepay = item.get('pbd_havepay');	//已付款额
					var nowbalance = item.get('pbd_nowbalance');	//本次退款额
					var pbd_ordercode = item.get('pbd_ordercode');
/*					//订单金额>=已预收金额+本次预收额
					if(apamount > 0 && havepay + nowbalance > apamount){//此种情况不能进行保存
						warn.push(item.get('pbd_detno'));
					}*/
					if(item.data['pbd_ordercode']!=null&&item.data['pbd_ordercode']!=""){
						//本次退款额 >已付款额					
						if(Math.abs(nowbalance) > Math.abs(havepay)){
							warn.push(item.get('pbd_detno'));
						}
					}
				}
			}
		});
		if(warn.length == 0){
			me.onUpdate();
		} else{
			showError('明细第' + warn.join(',') + '行 本次退款额 超过 已付款额,不能更新!');
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
		if(Ext.isEmpty(me.FormUtil.checkFormDirty(form)) && (param1.length == 0)
				&& param2.length == 0 && param3.length == 0){
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