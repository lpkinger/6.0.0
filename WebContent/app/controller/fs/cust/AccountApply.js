Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.AccountApply', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.AccountApply', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export','core.button.TurnBankRegister','core.button.PrintByCondition',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField','core.trigger.AddDbfindTrigger',
			'core.trigger.MultiDbfindTrigger', 'core.form.SeparNumber','core.button.FormsDoc'],
	init : function() {
		var me = this;
		this.control({
			'erpFormPanel' : {
    			afterload : function(form) {
    				this.hidecolumns();
				}
    		},
			'field[name=aa_catecode]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
    		'textfield[name=aa_thispayamount]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			},
			'field[name=cq_nomfcust]':{
				afterrender:function(t){
    				var trigger = Ext.getCmp('aa_mfcustcode');
					if(trigger){
						var value = t.value;
						if(value&&value!=0){
							trigger.dbKey=null;
			    			trigger.mappingKey=null;
			    			trigger.dbMessage=null;
							trigger.dbCaller =caller+'!MFCust';
							
						}else{
							trigger.dbKey='aa_cacode';
			    			trigger.mappingKey='cq_code';
			    			trigger.dbMessage='请先选择额度编号！';
							trigger.dbCaller =caller;
						}
					}
    			},
				change: function(field, newValue, oldValue){
					var trigger = Ext.getCmp('aa_mfcustcode');
					if(trigger){
						if(newValue&&newValue!=0){
							trigger.dbKey=null;
			    			trigger.mappingKey=null;
			    			trigger.dbMessage=null;
							trigger.dbCaller =caller+'!MFCust';
						}else{
							trigger.dbKey='aa_cacode';
			    			trigger.mappingKey='cq_code';
			    			trigger.dbMessage='请先选择额度编号！';
							trigger.dbCaller =caller;
						}
					}
				}
			},
			'field[name=aa_thispaydate]':{
				beforerender: function(field){
					field.readOnly = false;
				}
			},
			'dbfindtrigger[name=aas_sacode]':{
    			afterrender:function(t){
    				if(caller == 'AccountApply'){
    					t.gridKey="aa_custcode|aa_mfcustcode|aa_currency";
        				t.mappinggirdKey="sa_custcode|sa_mfcustcode|sa_currency";
        				t.gridErrorMessage="请先选择客户编号|请先选择买方客户编号|请先选择币别";
    				} else if (caller == 'AccountApply!HX'){
    					t.gridKey="aa_custname|aa_mfcustname|aa_currency";
        				t.mappinggirdKey="sa_mfcustname|sa_custname|sa_currency";
        				t.gridErrorMessage="请先选择买方客户|请先选择卖方客户|请先选择币别";
    				}
    			},
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
					t.dbBaseCondition = " nvl(sa_total,0)>nvl(sa_usedamount,0)";
    			}
    		},
    		'dbfindtrigger[name=ab_drawer]':{
    			afterrender:function(t){
    				t.gridKey="aa_cacode";
    				t.mappinggirdKey="ca_code";
    				t.gridErrorMessage="请先选择额度编号！";
    			}
    		},
    		'field[name=aa_downpay]': {
    			delay: 200,
    			change: function(){
					this.hidecolumns();
				}
    		},
    		'field[name=aa_interestpaymethod]': {
    			delay: 200,
    			change: function(f){
    				if(Ext.getCmp('reimbursementplan')){
    					if(f.value == '不规则还本付息：先息+等额本息' || f.value == '不规则还本付息：先息+等额本金' || f.value == '不规则还本付息：等额本息+等额本金'){
       					 	Ext.getCmp('reimbursementplan').readOnly = false;
	       				} else {
	       					Ext.getCmp('reimbursementplan').readOnly = true;
	       				}
    				}
					this.hidecolumns();
				}
    		},
    		'field[name=aa_margin]': {
    			delay: 200,
    			change: function(){
					this.hidecolumns();
				}
    		},
    		'field[name=aa_graceperiod]': {
    			delay: 200,
    			change: function(){
					this.hidecolumns();
				}
    		},
    		'dbfindtrigger[name=aa_mfcustcode]':{
    			afterrender:function(trigger){
    				if (caller == 'AccountApply') {
    					trigger.dbKey='aa_cacode';
    	    			trigger.mappingKey='cq_code';
    	    			trigger.dbMessage='请先选择额度编号！';
    				}
    			}
    		},
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'#reimbursementplan': { 
    			reconfigure: function(grid){
    				Ext.defer(function(){
    					grid.readOnly = true;
    					var aa_interestpaymethod = Ext.getCmp('aa_interestpaymethod');
        				if(aa_interestpaymethod && (aa_interestpaymethod.value == '不规则还本付息：先息+等额本息' || aa_interestpaymethod.value == '不规则还本付息：先息+等额本金' || aa_interestpaymethod.value == '不规则还本付息：等额本息+等额本金')) {
        					grid.readOnly = false;
        				}
    				}, 500);
    			},
    			itemclick: this.onGridItemClick
    		},
    		'erpPrintByConditionButton':{
    			afterrender:function(btn){
    				var statuscode=Ext.getCmp('aa_statuscode').value;
    				if(statuscode&&statuscode=='ENTERING'){
    					btn.hide();
    				}
    			}
    		},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd(caller, '出账申请', 'jsps/fs/cust/accountApply.jsp?whoami='+caller);
    			}
        	},
			'erpSaveButton': {
    			click: function(btn){
    				var form = Ext.getCmp('form'); 
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				me.beforeSave();			
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('aa_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					me.beforeSave(true);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('aa_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					var aa_dueamount = Ext.getCmp('aa_dueamount').value, aa_factoring = Ext.getCmp('aa_factoring').value;
					if(Ext.isEmpty(aa_dueamount) || aa_dueamount == 0){
						showError('出账金额不能为0！');return;
					}
					if (me.BaseUtil.numberFormat(aa_factoring,2) < me.BaseUtil.numberFormat(aa_dueamount,2)) {
						showError('出账金额不能大于保理金额！');
						return;
					}
					if(Ext.Date.format(Ext.getCmp('aa_maturitydate').value,'Y-m-d') <= Ext.Date.format(new Date(),'Y-m-d')){
    					showError('到期日期不能小于等于当前日期！'); return;
    				}
					if(Ext.Date.format(Ext.getCmp('aa_maturitydate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('aa_loandate').value,'Y-m-d')){
    					showError('到期日期不能小于放款日期！'); return;
    				}
					if(Ext.getCmp('aa_graceperiod').value == '申请宽限期'){
						var aa_gracedays = Ext.getCmp('aa_gracedays').value;
						if(Ext.isEmpty(aa_gracedays) || aa_gracedays == 0){
							showError('请填写宽限期天数！');return;
						} else {
							if(aa_gracedays > 30){
								showError('宽限期不能大于30天！');return;
							}
						}
					}
					me.FormUtil.onSubmit(Ext.getCmp('aa_id').value, false, this.beforeSave, this, true);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('aa_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('aa_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('aa_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					if(Ext.Date.format(Ext.getCmp('aa_maturitydate').value,'Y-m-d') <= Ext.Date.format(new Date(),'Y-m-d')){
						showError('到期日期不能小于等于当前日期！'); return;
					}
					if(Ext.Date.format(Ext.getCmp('aa_maturitydate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('aa_loandate').value,'Y-m-d')){
						showError('到期日期不能小于放款日期！'); return;
					}
					me.FormUtil.onAudit(Ext.getCmp('aa_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('aa_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('aa_id').value);
				}
			},
			'field[name=aa_dueamount]' : {
    			change: function(f, newValue, oldValue){
    				var form = Ext.getCmp('form'),
    				aa_factoring = Ext.getCmp('aa_factoring');
    				if(aa_factoring) {
    		    		var factoring = Ext.Number.from(aa_factoring.getValue(), 0);
    		    		if (me.BaseUtil.numberFormat(factoring,2) < me.BaseUtil.numberFormat(f.value,2)) {
    						showError('保理首付款不能大于保理金额！');
    						f.setValue(oldValue);
    						return;
    					}
    		    	}
    			}
    		},
    		//转银行登记
    		'erpTurnBankRegisterButton':{
    			click:function(btn){
    				me.turnBankRegister();
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		//保理转让款(元)
    		'field[name = aa_transferamount]' : {
    			change : function(f, newValue, oldValue){
    				var form = Ext.getCmp('form');
    				if(f.value==0){
    					showError('保理转让款不能为0！');
    					f.setValue(oldValue);
						return;
    				}
    				
    				var	aa_handrate = Ext.getCmp('aa_handrate');//手续费率
    				var	aa_wantamount = Ext.getCmp('aa_wantamount');//拟融资金额
    				//融资比例=拟融资金额/保理转让款
    				if(aa_wantamount){
    					var wantamount = Ext.Number.from(aa_wantamount.getValue(), 0);
    					var aa_lendrate = Ext.getCmp('aa_lendrate');
    					if(caller=='AccountApply!HX'){
	    					aa_wantamount.setValue(f.value);
	    					aa_lendrate && aa_lendrate.setValue(100);
	    				}else if (me.BaseUtil.numberFormat(wantamount,2) > me.BaseUtil.numberFormat(f.value,2)) {
    						showError('拟融资金融不能大于保理转让款！');
    						f.setValue(oldValue);
    						return;
	    				}else{
    						aa_lendrate && aa_lendrate.setValue(form.BaseUtil.numberFormat(wantamount/f.value*100,2));
	    				}
    				}
    				if(aa_handrate){
    					var aa_hand = Ext.getCmp('aa_hand');
    					aa_hand && aa_hand.setValue(form.BaseUtil.numberFormat(f.value*Ext.Number.from(aa_handrate.value,0)/100,2));
    				}
    			}
    		},
    		//拟融资金融(元)
    		'field[name = aa_wantamount]' : {
    			change : function(f, newValue, oldValue){
    				var form = Ext.getCmp('form');
    				var aa_transferamount = Ext.getCmp('aa_transferamount');//合同总金额(元)
    				if(!aa_transferamount){
						return;
    				}else{
    					var transferamount = Ext.Number.from(aa_transferamount.getValue(), 0);
    					var aaid = Ext.getCmp('aa_id');
    					var bool = caller=='AccountApply!HX' && aaid && Ext.isEmpty(aaid.value);
    					if (!bool && me.BaseUtil.numberFormat(transferamount,2) < me.BaseUtil.numberFormat(f.value,2)) {
    						showError('拟融资金融不能大于保理转让款！');
    						f.setValue(oldValue);
    						return;
	    				}
	    				
	    				if(transferamount!=0){
	    					//融资比例=拟融资金额/保理转让款
		    				var aa_lendrate = Ext.getCmp('aa_lendrate');
		    				aa_lendrate && aa_lendrate.setValue(form.BaseUtil.numberFormat(f.value/transferamount*100,2));  
	    				}
    				}
    			}
    		},
    		'field[name = aa_handrate]' : {
    			change : function(f, newValue, oldValue){
    				var form = Ext.getCmp('form');
    				var aa_transferamount = Ext.getCmp('aa_transferamount');		//保理转让款
    				if(aa_transferamount){
    					var transferamount = Ext.Number.from(aa_transferamount.getValue(), 0);
	    				var aa_hand = Ext.getCmp('aa_hand');
	    				aa_hand && aa_hand.setValue(form.BaseUtil.numberFormat(f.value*transferamount/100,2));
    				}
    			}
    		},
    		'dbfindtrigger[name=ab_barcode]': {
    			afterrender: function(t){
    				t.gridKey = "aa_mfcustcode";
    				t.mappinggirdKey = "bar_custcode";
    				t.gridErrorMessage = "请先选择卖方客户编号!";
    			}
    		}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	beforeSave:function(isUpdate){
		var me = this;
		var form = Ext.getCmp('form'), aa_dueamount = Ext.getCmp('aa_dueamount').value,
			aa_factoring = Ext.getCmp('aa_factoring').value;
		if(!me.FormUtil.checkForm()){
			return;
		}
		if(isUpdate){
			if(form.codeField && (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
				showError('编号不能为空.');
				return;
			}
		}else{
			if(form.keyField && (Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == '')){
				me.FormUtil.getSeqId(form);
			}
		}
		if(Ext.isEmpty(aa_dueamount) || aa_dueamount == 0){
			showError('出账金额不能为0！');return;
		}
		if (me.BaseUtil.numberFormat(aa_factoring,2) < me.BaseUtil.numberFormat(aa_dueamount,2)) {
			showError('出账金额不能大于保理金额！');
			return;
		}
		if(Ext.getCmp('aa_graceperiod').value == '申请宽限期'){
			var aa_gracedays = Ext.getCmp('aa_gracedays').value;
			if(Ext.isEmpty(aa_gracedays) || aa_gracedays == 0){
				showError('请填写宽限天数！');return;
			} else {
				if(aa_gracedays > 30){
					showError('宽限期不能大于30天！');return;
				}
			}
		}
		if(Ext.Date.format(Ext.getCmp('aa_maturitydate').value,'Y-m-d') <= Ext.Date.format(new Date(),'Y-m-d')){
			showError('到期日期不能小于等于当前日期！'); return;
		}
		if(Ext.Date.format(Ext.getCmp('aa_maturitydate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('aa_loandate').value,'Y-m-d')){
			showError('到期日期不能小于放款日期！'); return;
		}
		var grids = Ext.ComponentQuery.query('gridpanel');
		if(isUpdate){
			var s1 = me.FormUtil.checkFormDirty(form);
			var s2 = '';
			if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
				Ext.each(grids, function(grid, index){
					if(me.GridUtil){
						var msg = me.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
							s2 = s2 + '<br/>' + msg;
						}
					}
				});
			}
			if(s1 == '' && (s2 == '' || s2 == '<br/>')){
				showError('还未添加或修改数据.');
				return;
			}
		}
		
		var grid1 = Ext.getCmp('accountapplysa');
		var grid2 = Ext.getCmp('accountapplyinv');	
		var grid3 = Ext.getCmp('accountapplybill');
		var grid4 = Ext.getCmp('reimbursementplan');
		var param1 = new Array();
		if(grid1){
			param1 = me.GridUtil.getGridStore(grid1);
		}
		var param2 = new Array();
		if(grid2){
			param2 = me.GridUtil.getGridStore(grid2);
		}
		var param3 = new Array();
		if(grid3){
			param3 = me.GridUtil.getGridStore(grid3);
		}
		var param4 = new Array();
		if(grid4){
			param4 = me.GridUtil.getGridStore(grid4);
		}
		
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			
			me.save(r, param1, param2, param3, param4, isUpdate);
		}else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0],isUpdate = arguments[arguments.length-1];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.caller = caller;
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		params.param4 = unescape(arguments[4].toString().replace(/\\/g,"%"));
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + (isUpdate?form.updateUrl:form.saveUrl),
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	
	   			me.FormUtil.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition+'&gridCondition=aas_aaidIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=aas_aaidIS'+value;
			   		    }
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;

			   		    	if(contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   		    	formCondition+'&gridCondition=aas_aaidIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=aas_aaidIS'+value;
				   		    }
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
		});
	},
	/*getAllGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,GridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(!me.GridUtil.isBlank(grid, data)){
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						data[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					GridData.push(dd);
				}
			}
		}
		return GridData;
	},*/
	turnBankRegister: function(){
    	var grid = Ext.getCmp('grid'), catecode = Ext.getCmp('aa_catecode').value;
        var thisamount = Ext.getCmp('aa_thispayamount').value, amount = 0;
		var amount = Ext.getCmp('aa_dueamount').value, yamount = Ext.getCmp('aa_yamount').value;
		var thispaydate = Ext.getCmp('aa_thispaydate').value;
		if(Ext.isEmpty(thispaydate)){
			Ext.Msg.alert('警告','请填写本次付款日期！');
			return;
		}
		if(Math.abs(thisamount)-(Math.abs(amount)-Math.abs(yamount))>0.01){
			Ext.Msg.alert('警告','本次出账金额超过剩余未转金额！未转金额：'+ (amount-value));
			return;
		}
        if(catecode == null || catecode == ''){
        	Ext.Msg.alert('警告','请填写需要转银行登记的付款方信息！');
        	return;
        }
		if(thisamount == null || thisamount=='' || thisamount==0){
			Ext.Msg.alert('警告','本次付款金额未填写！');
			return;
		}
    	var me = this, form = Ext.getCmp('form');
    	me.FormUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + 'fs/accountApplyController/turnBankRegister.action',
	   		params: {
	   			caller: caller,
	   			formStore: unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%"))
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.getActiveTab().setLoading(false);
	   			var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
	   			if(r.success){
    				if(r.content && r.content.ar_id){
    					showMessage("提示", "转入成功,银行登记: <a href=\"javascript:openUrl2('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + r.content.ar_id
    							 + "&gridCondition=ard_aridIS" + r.content.ar_id + "&whoami=AccountRegister!Bank','银行登记','ar_id'," + r.content.ar_id
    							 + ")\">" + r.content.ar_code + "</a>");
    				}
    				window.location.reload();
	   			}
	   		}
		});
    },
    hidecolumns:function(){
    	var form = Ext.get('form'), f = Ext.getCmp('aa_interestpaymethod');
		form.down('#aa_downpaydesc') && form.down('#aa_downpaydesc').hide();
		form.down('#aa_marginamount') && form.down('#aa_marginamount').hide();
		form.down('#aa_margindate') && form.down('#aa_margindate').hide();
		form.down('#aa_gracedays') && form.down('#aa_gracedays').hide();
		form.down('#aa_gracerate') && form.down('#aa_gracerate').hide();
		form.down('#aa_firstdate') && form.down('#aa_firstdate').hide();
		form.down('#aa_debxdate') && form.down('#aa_debxdate').hide();
		form.down('#aa_debjdate') && form.down('#aa_debjdate').hide();
		if(Ext.getCmp('aa_downpay').value == '限定用途'){
			form.down('#aa_downpaydesc') && form.down('#aa_downpaydesc').show();
		} else {
			form.down('#aa_downpaydesc') && form.down('#aa_downpaydesc').hide();
		}
		if(Ext.getCmp('aa_margin').value == '卖方须缴纳保证金'){
			form.down('#aa_marginamount') && form.down('#aa_marginamount').show();
			form.down('#aa_margindate') && form.down('#aa_margindate').show();
		} else {
			form.down('#aa_marginamount') && form.down('#aa_marginamount').hide();
			form.down('#aa_margindate') && form.down('#aa_margindate').hide();
		}
		if(Ext.getCmp('aa_graceperiod').value == '申请宽限期'){
			form.down('#aa_gracedays') && form.down('#aa_gracedays').show();
			form.down('#aa_gracerate') && form.down('#aa_gracerate').show();
		} else {
			form.down('#aa_gracedays') && form.down('#aa_gracedays').hide();
			form.down('#aa_gracerate') && form.down('#aa_gracerate').hide();
		}
		if(f.value == '不规则还本付息：先息+等额本息' || f.value == '不规则还本付息：先息+等额本金' || f.value == '不规则还本付息：等额本息+等额本金'){
			form.down('#aa_firstdate') && form.down('#aa_firstdate').show();
			form.down('#aa_debxdate') && form.down('#aa_debxdate').hide();
			form.down('#aa_debjdate') && form.down('#aa_debjdate').hide();
		} else {
			form.down('#aa_firstdate') && form.down('#aa_firstdate').hide();
			form.down('#aa_debxdate') && form.down('#aa_debxdate').hide();
			form.down('#aa_debjdate') && form.down('#aa_debjdate').hide();
		}
	}
});