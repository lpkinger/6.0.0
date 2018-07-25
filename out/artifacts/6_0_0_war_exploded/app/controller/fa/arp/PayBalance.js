Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.PayBalance', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.arp.PayBalance','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.SeparNumber',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.ResAccounted','core.button.Accounted','core.button.StrikeBalance',
      		'core.grid.AssPanel','core.window.AssWindow','core.trigger.AddDbfindTrigger','core.trigger.CateTreeDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger','core.button.Post','core.button.ResPost','core.button.Print',
      		'core.button.Submit','core.button.ResAudit','core.button.Audit','core.button.ResSubmit','core.button.AssDetail','core.button.AssMain', 'core.trigger.MultiDbfindTrigger','core.button.GetSumAmount'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick(selModel, record);
    				if(caller == 'PayBalance'||caller =='PayBalance!Arp!PADW'||caller =='PayBalance!APRM'||caller =='PayBalance!CAID'){
    					var btn = Ext.getCmp('assdetail');
        				var ass = record.data['ca_asstype'];
        				if(!Ext.isEmpty(ass)){
        					btn.setDisabled(false);
        				} else {
        					btn.setDisabled(true);
        				}
    				}
    			},
    			afterrender:function(grid){
    				if(caller=='PayBalance!APRM'){
    					Ext.getCmp('bi_code').show();
    				}
    				 grid.plugins[0].on('beforeedit', function(args) {
    					 var bool=true;
    					 if(args.record.get('pbd_ppddid')!=null && args.record.get('pbd_ppddid') !=0 && args.record.get('pbd_ppddid') !='0' && args.record.get('pbd_ppddid') !=''){
    						 bool=false;
    					 }
    					 if(args.field == "pbd_ordercode") {
                     		if(args.record.get('pbd_ppddid')!=null && args.record.get('pbd_ppddid') !=0 && args.record.get('pbd_ppddid') !='0' && args.record.get('pbd_ppddid') !=''){
                     			bool=false;
         					}
                     		if(Ext.getCmp('pb_source')&&Ext.getCmp('pb_source').value=="付款申请"){
                     			bool=false;
                     		}
                     	}
                        return bool;
                     });
    			}
    		},
    		'erpAssMainButton':{
    			afterrender:function(btn){
    				if(caller == 'PayBalance'||caller == 'PayBalance!CAID'){
        				if(Ext.getCmp('ca_asstype').getValue()==null||Ext.getCmp('ca_asstype').getValue()==""){
        					btn.setDisabled(true);
        				}else{
        					btn.setDisabled(false);
        				}
    				}
    			}
    		},
    		'field[name=pb_amount]':{
				beforerender: function(field){
					if(caller != 'PayBalance!CAID'){
						if(Ext.getCmp('pb_sourcecode')&&Ext.getCmp('pb_sourcecode').value!=""){
							field.readOnly=true;
						}
					}
				}
			},
			'field[name=pb_vendcode]':{
				beforerender: function(field){
					if(Ext.getCmp('pb_source')&&Ext.getCmp('pb_source').value=="付款申请"){
						field.setReadOnly(true);
					}
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
    		'erpGetSumAmountButton':{
    			click: function(btn){
    				var grid = Ext.getCmp("grid"),items = grid.store.data.items;
    				var detailamount1 = 0;
    	    		Ext.each(items,function(item,index){
    	    			if(!me.GridUtil.isBlank(grid,item.data)) {
    	    				detailamount1 = detailamount1 + Number(item.data['pbd_nowbalance']);
    	    			}
    	    		});
    	    		Ext.getCmp("pb_amount").setValue(detailamount1);
    	    		Ext.getCmp("pb_vmamount").setValue(detailamount1);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					/*Ext.getCmp(form.codeField).setValue(me.BaseUtil.getRandomNumber());*///自动添加编号
    					me.BaseUtil.getRandomNumber();
    				}
    				if(caller == 'PayBalance!Arp!PADW'||caller == 'PayBalance'|| caller == 'PayBalance!CAID'|| caller == 'PayBalance!APRM'){
    					me.getApamount();
    				}
    				//保存之前的一些前台的逻辑判定
    				//this.beforeSavePayBalance();
    				me.beforeSave(this);
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				if(caller == 'PayBalance'||caller =='PayBalance!Arp!PADW'||caller =='PayBalance!APRM'||caller =='PayBalance!CAID'){
    					//辅助核算
        				btn.ownerCt.add({
        					xtype:'erpAssDetailButton',
        					disabled:true
        				});
    				}
    			},
    			beforedelete:function(data,record,btn){
    				if(caller != 'PayBalance!CAID'){
    					if(record.get('pbd_ppddid')!=null && record.get('pbd_ppddid') !=0 && record.get('pbd_ppddid') !='0' && record.get('pbd_ppddid') !=''){	
        					btn.canDelete=false;
        					showError('当前行存在来源不允许修改!');
        				}
    					if(record.get('pbd_source') && !Ext.isEmpty(record.get('pbd_source'))){	
        					btn.canDelete=false;
        					showError('当前行存在来源不允许修改!');
        				}
    				}
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
    			afterrender: function(btn){
    				var status = Ext.getCmp('pb_statuscode');
    				if(status && status.value == 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(caller != 'PayBalance!CAID'){
	    				var source = Ext.getCmp('pb_source');
	    				if(source && source.value){
	    					showError("请在来源:"+source.value+",单号："+Ext.getCmp('pb_sourcecode').value+"中进行反审核或者反记账操作！");
	    					return;
	    				}
    				}
    				me.FormUtil.onDelete(Ext.getCmp('pb_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pb_statuscode');
    				if(status && status.value == 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(caller == 'PayBalance!Arp!PADW'||caller == 'PayBalance' || caller == 'PayBalance!CAID'|| caller == 'PayBalance!APRM'){
    					me.getApamount();
    				}
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
    				me.FormUtil.onAdd('addPayBalance', title, 'jsps/fa/arp/paybalance.jsp?whoami='+caller);
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
    				var status = Ext.getCmp('pb_statuscode');
    				if(status && status.value == 'POSTED'){
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
					var status = Ext.getCmp('pb_statuscode');
    				if(status && status.value == 'POSTED'){
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
    				var status = Ext.getCmp('pb_statuscode');
    				if(status && status.value == 'POSTED'){
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
    				var status = Ext.getCmp('pb_statuscode');
    				if(status && status.value == 'POSTED'){
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
    					Ext.getCmp('pb_vmrate').setValue(Ext.Number.toFixed(v2/v1, 15));
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
    					Ext.getCmp('pb_vmrate').setValue(Ext.Number.toFixed(v2/v1, 15));
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
    				if (caller == 'PayBalance' || caller == 'PayBalance!CAID'){
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
    				t.gridKey="pb_vendcode|pb_vmcurrency";
    				t.mappinggirdKey="ab_vendcode|ab_currency";
    				t.gridErrorMessage="请先选择供应商|请选择冲账币别";
    			}
    		},
    		//抓取发票信息
    		'button[name=catchab]':{
    			afterrender:function(btn){
    				Ext.defer(function(){
    					if(Ext.getCmp('pb_ppcode') && !Ext.isEmpty(Ext.getCmp('pb_ppcode').value)){
        					btn.setDisabled(true);
        				}
    				}, 500);
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
    			    var bars=grid.query('toolbar'),toolbar=bars[0];
    			    var bicode=toolbar.items.items[2].value;
    			    var startdate=toolbar.items.items[4].value;
    			    var enddate=toolbar.items.items[6].value;
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
        					params['bicode'] = bicode;
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
    			afterrender:function(btn){
    				Ext.defer(function(){
    					if(Ext.getCmp('pb_ppcode') && !Ext.isEmpty(Ext.getCmp('pb_ppcode').value)){
        					btn.setDisabled(true);
        				}
    				}, 500);
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
        				   				showError(localJson.exceptionInfo);return;
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
    	var me = this, form = Ext.getCmp('form');
    	if(caller == 'PayBalance' || caller == 'PayBalance!CAID'){
    		var grid = Ext.getCmp('grid'),items=grid.store.data.items;
    		var sameCurrency = true;
    		var amount = Number(Ext.getCmp('pb_amount').getValue());
    		var vmamount =Number(Ext.getCmp('pb_vmamount').getValue());
    		var vmcurrency = Ext.getCmp('pb_vmcurrency').getValue();
    		var vmrate = Number(Ext.getCmp('pb_vmrate').getValue());
    		var detailamount = 0;
    		var currency = Ext.getCmp('pb_currency').getValue();
    		if(amount != 0 && vmamount != 0){
				if(form.BaseUtil.numberFormat(vmamount,2) != form.BaseUtil.numberFormat(form.BaseUtil.multiply(vmrate, amount),2)){
					showError('冲账汇率不正确,不能提交!');
					return;
				}
    		}
    		if(currency == vmcurrency){
				if(amount !=0 && vmamount !=0 && vmrate != 1){
					showError('币别相同，冲账汇率不等于1,不能提交!');
					return;
				}
			}
    		if(currency != vmcurrency){
				if(amount !=0 && vmamount !=0 && vmrate == 1){
					showError('币别不相同，冲账汇率等于1,不能提交!');
					return;
				}
			}
    		Ext.each(items,function(item,index){
    			if(!me.GridUtil.isBlank(grid, item.data)){
	    			detailamount = detailamount+Number(item.data['pbd_nowbalance']);
	    			if(vmcurrency!=item.data['pbd_currency']){
	    				//从表币别有与主表币别不同的
	    				sameCurrency = false;
	    			}
    			}
    		});
    		if(Math.abs(vmamount-amount*vmrate)>0.02){
    			showError('冲账金额不正确,不能提交');return;
    		}
    		if(!sameCurrency){
    			//从表币别有与主表币别不同的
    			showError('明细行币别与冲账币别不同,不能提交');return;
    		}
    		if(Math.abs(vmamount-detailamount)>0.02){
    			//冲账金额与明细行本次结算总和不等
    			showError('明细行本次结算与冲账金额不等,不能提交');return;
    		}
			me.FormUtil.onSubmit(Ext.getCmp('pb_id').value, false, this.beforeUpdate, this);
    	}else{
    		me.FormUtil.onSubmit(Ext.getCmp('pb_id').value, false, this.beforeUpdate, this);
    	}
    },
    beforePost:function(btn){
    	var me = this, form = Ext.getCmp('form');
    	if(caller == 'PayBalance' || caller == 'PayBalance!CAID'){
    		var grid = Ext.getCmp('grid'),items=grid.store.data.items,nodata=true;
    		var sameCurrency = true;
    		var amount = Number(Ext.getCmp('pb_amount').getValue());
    		var vmamount =Number(Ext.getCmp('pb_vmamount').getValue());
    		var vmcurrency = Ext.getCmp('pb_vmcurrency').getValue();
    		var vmrate = Number(Ext.getCmp('pb_vmrate').getValue());
    		var detailamount = 0;
    		var currency = Ext.getCmp('pb_currency').getValue();
    		if(amount != 0 && vmamount != 0){
    			if(form.BaseUtil.numberFormat(vmamount,2) != form.BaseUtil.numberFormat(form.BaseUtil.multiply(vmrate, amount),2)){
					showError('冲账汇率不正确,不能过账!');
					return;
				}
    		}
    		if(currency == vmcurrency){
				if(amount !=0 && vmamount !=0 && vmrate != 1){
					showError('币别相同，冲账汇率不等于1,不能过账!');
					return;
				}
			}
    		if(currency != vmcurrency){
				if(amount !=0 && vmamount !=0 && vmrate == 1){
					showError('币别不相同，冲账汇率等于1,不能过账!');
					return;
				}
			}
    		Ext.each(items,function(item,index){
    			if(!me.GridUtil.isBlank(grid, item.data)){
	    			detailamount = detailamount+Number(item.data['pbd_nowbalance']);
	    			nodata=false;
	    			if(vmcurrency!=item.data['pbd_currency']){
	    				//从表币别有与主表币别不同的
	    				//抛出异常
	    				sameCurrency = false;
	    			}
    			}
    		});
    		if(nodata) {
    			showError('明细行无数据,不能过账');
    		    return;
    		}
    		if(Math.abs(vmamount-amount*vmrate)>0.02){
    			showError('冲账金额不正确,不能过账');return;
    		}
    		if(!sameCurrency){
    			//从表币别有与主表币别不同的
				//抛出异常
    			showError('明细行币别与冲账币别不同,不能过账');return;
    		}
       		if(Math.abs(vmamount-detailamount)>0.02){
    			//冲账金额与明细行本次结算总和不等
    			//抛出异常
    			showError('明细行本次结算与冲账金额不等,不能过账');return;
    		}
    	}
    	me.FormUtil.onPost(Ext.getCmp('pb_id').value);
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
	beforeSavePayBalance: function(){
		var me = this;
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var bool = true;
		var warnStr='';
		//冲应付款维护界面 保存时需要判断 付款金额*冲账汇率 如果不等于冲账金额  保存提醒 
		if(caller  =='PayBalance!CAID'){
			var pb_amount = Ext.util.Format.number(Ext.Number.from(Ext.getCmp('pb_amount').getValue(),0),'0.00');//付款金额 
			var pb_vmamount = Ext.util.Format.number(Ext.Number.from(Ext.getCmp('pb_vmamount').getValue(),0),'0.00');	//	冲账金额
			var pb_vmrate = Ext.Number.from(Ext.getCmp('pb_vmrate').getValue(),0);	//冲账汇率
			
			if(Ext.util.Format.number(pb_amount*pb_vmrate,'0.00')!=pb_vmamount){
				showError('冲账金额不等于付款金额*冲账汇率,不能保存!');return;
			}
			var pbd_nowbalanceamount = 0.00;
			//判断   冲应付款单据 中  主表冲账金额是否等于从报表中本次结算的和
			Ext.each(items,function(item,index){
				if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
					var pbd_nowbalance  = Ext.Number.from(item.data['pbd_nowbalance'],0);
					pbd_nowbalanceamount=pbd_nowbalanceamount+pbd_nowbalance;
				}
			});
			if(Ext.util.Format.number(pbd_nowbalanceamount,'0.00')!=Ext.util.Format.number(pb_amount*pb_vmrate,'0.00')){
				showError('冲账金额不等于明细行本次结算总和,不能保存!');return;
			}
		}
		bool = true;
		if(bool){
			me.beforeSave(this);
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
		var grid = Ext.getCmp('grid'), key = Ext.getCmp('pb_id').value, form = Ext.getCmp('form');
		var warn = new Array();
		grid.store.each(function(item){
			if(item.dirty) {
				item.set('pbd_pbid', key);
				if((!grid.necessaryField || !Ext.isEmpty(item.get(grid.necessaryField))) && !Ext.isEmpty(item.get('pbd_ordercode'))){
					var apamount = item.get('pbd_apamount');	//订单金额
					var havepay = item.get('pbd_havepay');	//已预收金额
					var nowbalance = item.get('pbd_nowbalance');	//本次预收额
					//订单金额>=已预收金额+本次预收额
					if(Math.abs(form.BaseUtil.numberFormat(havepay, 2)) + Math.abs(form.BaseUtil.numberFormat(nowbalance, 2)) - Math.abs(form.BaseUtil.numberFormat(apamount, 2)) > 0.005){
						warn.push(item.get('pbd_detno'));
					}
				}
			}
		});
		if(warn.length == 0){
			me.onUpdate();
		} else{
			showError('明细第' + warn.join(',') + '行 已付金额与本次结算额的合计超过发票金额,不能保存!');
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