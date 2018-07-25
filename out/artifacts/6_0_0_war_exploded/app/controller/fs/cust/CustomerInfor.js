Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.CustomerInfor', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.CustomerInfor', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField','core.button.Save',
			'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit','core.button.Audit','core.button.Close',
			'core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export',
			'core.button.Banned','core.button.ResBanned','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger',
			'core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField','core.form.FileField','core.button.CopyAll','core.button.Sync',
			'core.button.ResetSync', 'core.button.RefreshSync','core.trigger.MultiDbfindTrigger','core.button.FormsDoc'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2:not([id=changes])': { 
    			itemclick: this.onGridItemClick
    		},
    		'textfield[name=cu_paperstype]': {
    			change: function(field,newval,oldval){
    				var nationtax = Ext.getCmp('cu_nationtax');
    				var landtax = Ext.getCmp('cu_landtax');
    				var businesscode = Ext.getCmp('cu_businesscode');
    				if(newval=='组织机构代码证'){
    					businesscode.setReadOnly(false);
    					businesscode.setFieldStyle('background:#FFFAFA;color:#515151;');
    					businesscode.allowBlank = false;
    					businesscode.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;color:#FF0000';
    					nationtax.maxLength = 20;
    					landtax.maxLength = 20;
    					nationtax.maxLengthText = '税务登记证号不能超过20位';
    					landtax.maxLengthText = '税务登记证号不能超过20位';
    					nationtax.setReadOnly(false);
    					landtax.setReadOnly(false);
    					nationtax.setFieldStyle('background:#FFFAFA;color:#515151;');
    					landtax.setFieldStyle('background:#FFFAFA;color:#515151;');
    					nationtax.allowBlank = false;
    					landtax.allowBlank = false;
    					nationtax.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;color:#FF0000';
    					landtax.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;color:#FF0000';
    				}else{
    					businesscode.allowBlank = true;
    					businesscode.setReadOnly(true);
    					businesscode.setFieldStyle('background:#e0e0e0;');
    					businesscode.setValue('');
    					businesscode.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
    					nationtax.allowBlank = true;
    					landtax.allowBlank = true;
    					nationtax.maxLength = null;
    					landtax.maxLength = null;
    					nationtax.setValue('');
    					landtax.setValue('');
    					nationtax.setReadOnly(true);
    					landtax.setReadOnly(true);
    					nationtax.setFieldStyle('background:#e0e0e0;');
    					landtax.setFieldStyle('background:#e0e0e0;');
    					nationtax.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
    					landtax.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
    				}
    			}
    		},
    		'textfield[name=cu_businesscode]': {
    			afterrender: function(field){
    				var status = Ext.getCmp('cu_statuscode');
    				var paperstype = Ext.getCmp('cu_paperstype');
					if (status && status.value == 'ENTERING'&&paperstype&&paperstype.value!='组织机构代码证') {
						field.allowBlank = true;
						field.setReadOnly(true);
						field.setFieldStyle('background:#e0e0e0;');
						field.setValue('');
						field.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
					}
    			}
    		},
    		'textfield[name=cu_nationtax]': {
    			afterrender: function(field){
    				var status = Ext.getCmp('cu_statuscode');
    				var paperstype = Ext.getCmp('cu_paperstype');
					if (status && status.value == 'ENTERING'&&paperstype&&paperstype.value!='组织机构代码证') {
						field.allowBlank = true;
						field.maxLength = null;
						field.setReadOnly(true);
						field.setFieldStyle('background:#e0e0e0;');
						field.setValue('');
						field.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
					}
    			}
    		},
    		'textfield[name=cu_landtax]': {
    			afterrender: function(field){
    				var status = Ext.getCmp('cu_statuscode');
    				var paperstype = Ext.getCmp('cu_paperstype');
					if (status && status.value == 'ENTERING'&&paperstype&&paperstype.value!='组织机构代码证') {
						field.allowBlank = true;
						field.maxLength = null;
						field.setReadOnly(true);
						field.setFieldStyle('background:#e0e0e0;');
						field.setValue('');
						field.getEl().dom.firstChild.style = 'margin-right:5px;width:100px;';
					}
    			}
    		},
    		'textfield[name=cu_recordman]': {
    			afterrender: function(field){
    				var status = Ext.getCmp('cu_statuscode');
					if (status && status.value == 'ENTERING'&&field.value=='') {
						field.setValue(emname);
					}
    			}
    		},
    		'field[name=cs_name]' : {
    			focus : function(t){
    				t.setReadOnly(false);
  				   	var record = Ext.getCmp('shareholder').selModel.lastSelected,
    				type = record.data['cs_type'];
    				if(type&&type=='法人股东'){
    					t.setHideTrigger(false);
    					t.column.dbfind = 'CustomerInfor|cu_name';
    				}else if(type&&type=='个人股东'){
    					t.setHideTrigger(false);
    					t.column.dbfind = 'CustPersonInfo|cp_name';
    				}else{
    					t.setHideTrigger(true);
    					t.canblur = false;
    					t.column.dbfind = '';
    				}
    			}
    		},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('CustomerInfor', '客户信息', 'jsps/fs/cust/customerInfor.jsp');
    			}
        	},
			'erpSaveButton': {
    			click: function(btn){				
					me.beforeSave();			
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cu_id').value);
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
					var status = Ext.getCmp('cu_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('cu_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cu_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cu_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cu_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cu_id').value);
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cu_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('cu_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('cu_statuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('cu_id').value);
    			}
    		},
			'erpSyncButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('cu_statuscode');
					if(status && status.value != 'AUDITED' && status.value != 'BANNED' && status.value != 'DISABLE'){
						btn.hide();
					}
				}
    		}
		})
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	beforeSave:function(isUpdate){
		var me = this;
		var form = Ext.getCmp('form');
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
		var cu_paperstype = Ext.getCmp('cu_paperstype').value,
			cu_paperscode = Ext.getCmp('cu_paperscode').value;
		if(!Ext.isEmpty(cu_paperstype) && cu_paperstype == '统一社会信用代码'){
			if(!Ext.isEmpty(cu_paperscode) && cu_paperscode.length != 18){
				showError('证件号码必须为18位！');return;
			}else{
				Ext.getCmp('cu_paperscode').setValue(cu_paperscode.toUpperCase());
			}
		}else if(!Ext.isEmpty(cu_paperstype) && cu_paperstype == '组织机构代码证'){
			var businesscode = Ext.getCmp('cu_businesscode');
			if(businesscode&&businesscode.value.length!=15){
				showError('营业执照号应为15位！');return;
			}
		}
		
		var contactnum = Ext.getCmp('cu_contactnum').value,contactphone = Ext.getCmp('cu_contactphone').value;
		if(Ext.isEmpty(contactnum)&&Ext.isEmpty(contactphone)){
			showError('联系方式1跟联系方式2不能同时为空！');return;
		}
		
		if(isUpdate){
			var s1 = me.FormUtil.checkFormDirty(form);
			var s2 = '';
			var grids = Ext.ComponentQuery.query('gridpanel');
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
		
		var grid1 = Ext.getCmp('excutive');
		var grid2 = Ext.getCmp('shareholder');	
		var grid3 = Ext.getCmp('inverstment');
		var grid4 = Ext.getCmp('udstream');
		var grid5 = Ext.getCmp('changes');
		var ratios = 0;
		var ratio = 0;
		
		var count1 = 0;
		var count2 = 0;
		var data1 = me.getAllGridStore(grid1);
		for(var i=0;i<data1.length;i++){
			
			if(data1[i]["ce_type"]=='实际控制人'){
				count1++;
			}
			if(data1[i]["ce_type"]=='经营负责人'){
				count2++;
			}
			if(count1>1){
				showError("实际控制人必须唯一！");
				return;
			}
			if(count2>1){
				showError("经营负责人必须唯一！");
				return;
			}
		}
		var data2 = me.getAllGridStore(grid2);
		for(var i=0;i<data2.length;i++){
			if (data2[i]["cs_investratio"]) {
				ratio = parseFloat(data2[i]["cs_investratio"]);
				if (ratio<0||ratio>100) {
					showError("股东情况明细行错误,行"+data2[i]["cs_detno"]+",股东出资比例应为0~100%");
					return;
				}
				ratios += ratio;
			}
		}
		if (ratios>100) {
			showError("股东出资比例合计不能超过100%！");
			return;
		}
		
		var data3 = me.getAllGridStore(grid3);
		for(var i=0;i<data3.length;i++){
			if (data3[i]["ci_investratio"]) {
				ratio = data3[i]["ci_investratio"];
				if (ratio<0||ratio>100) {
					showError("对外股权投资情况明细行错误,行"+data3[i]["ci_detno"]+",对外控股比例应为0~100%");
					return;
				}
			}
		}
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
		var param5 = new Array();
		if(grid5){
			param5 = me.GridUtil.getGridStore(grid5);
		}
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
		param5 = param5 == null ? [] : "[" + param5.toString().replace(/\\/g,"%") + "]";
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
			me.save(r, param1, param2, param3, param4, param5, isUpdate);
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
		params.param5 = unescape(arguments[5].toString().replace(/\\/g,"%"));
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
			   					formCondition+'&gridCondition=ce_cuidIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=ce_cuidIS'+value;
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
				   		    	formCondition+'&gridCondition=ce_cuidIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=ce_cuidIS'+value;
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
	getAllGridStore: function(grid){
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
	}
});