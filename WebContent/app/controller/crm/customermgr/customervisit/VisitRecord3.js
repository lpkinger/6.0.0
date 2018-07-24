Ext.QuickTips.init();
Ext.define('erp.controller.crm.customermgr.customervisit.VisitRecord3', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.customermgr.customervisit.VisitRecord3','core.form.Panel','core.grid.Panel2','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
      		'erp.view.core.button.Down','erp.view.core.button.UpExcel','core.button.VoCreate','core.button.TurnBorrow','crm.customermgr.customervisit.RecordDetail3',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
  			'crm.customermgr.customervisit.RecordDetail','crm.customermgr.customervisit.RecordDetailDet','crm.customermgr.customervisit.Marketing','crm.customermgr.customervisit.VenderMaketing','crm.customermgr.customervisit.Rival',
  			'crm.customermgr.customervisit.Expect','crm.customermgr.customervisit.Price','crm.customermgr.customervisit.ProductPlanning','core.button.Modify',
  			'core.form.HrOrgSelectField','oa.doc.OrgTreePanel'
  	],
	init:function(){
		var me = this;
		me.gridLastSelected = null;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'recordDetailDet': { 
    			itemclick: this.onGridItemClick2
    		},
    		'recordDetail3': { 
    			itemclick: this.onGridItemClick3
    		},
    		
    		'VenderMaketing': { 
    			itemclick: this.onGridItemClick4
    		},
    		'Price': { 
    			itemclick: this.onGridItemClick5
    		},
    		'field[name=vr_newtitle]':{
    			beforerender:function(field){
    				var status = Ext.getCmp('vr_statuscode');
    				if(status && status.value == 'COMMITED'){
    					field.readOnly=false;
    				}
    			}
    		},
    		'field[name=vr_purpose]':{
    			beforerender:function(field){
    				var status = Ext.getCmp('vr_statuscode');
    				if(status && status.value == 'COMMITED'){
    					field.readOnly=false;
    				}
    			}
    		},
    		'#vr_detail':{
    			afterrender:function(field){
    				field.setHeight(230);
    			}
    		},
    		'multidbfindtrigger[name=cup_name]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='vr_cuuu';
	    			trigger.mappingKey='vt_vecode';
	    			trigger.dbMessage='请先选择原厂编号';
    			}
    		},
    		'dbfindtrigger[name=vrd_d1]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='vr_defaultorname';
	    			trigger.mappingKey='fcs_departmentname';
	    			trigger.dbMessage='请先选择所属组织';
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.beforeSave();
				}
			},
			'erpVoCreateButton':{
    			beforerender:function(btn){
    				btn.setText("复制");
    			},
    			click:function(){
    				this.autoInsert();
    			}
    		},
    		'erpModifyCommonButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value == 'AUDITED'){
						btn.show();//触发字段可编辑
					}
				}
			},
    		'erpTurnBorrowButton':{
    			beforerender:function(btn){
    				btn.setText("转差旅费用");
    			},
    			afterrender:function(btn){
    				var status = Ext.getCmp('vr_statuscode');
    				var isturn=Ext.getCmp('vr_isturnfeeplease');
    				if(status.value != 'AUDITED'){
    					btn.hide();
    				}
    				
    			},
    			click:function(){
    				this.turnFeePlease();
    			}
    		},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('vr_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.beforeUpdate();
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addVisitRecord3', '新增原厂拜访记录 ', 'jsps/crm/customermgr/customervisit/visitRecord3.jsp');
				}
			},
			
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
		
			},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				var me = this.FormUtil;
    				var form = Ext.getCmp('form');
    				if(!me.contains(form.auditUrl, '?caller=', true)){
    					form.auditUrl = form.auditUrl + "?caller=" + caller;
    				}
    				me.setLoading(true);//loading...
    				//清除流程
    				Ext.Ajax.request({
    					url : basePath + me.deleteProcess,
    			   		params: {
    			   			keyValue:Ext.getCmp('vr_id').value,
    			   			caller:caller,
    			   			_noc:1
    			   		},
    			   		method:'post',
    			   		async:false,
    			   		callback : function(options,success,response){
    		   				
    			   		}
    				});
    				Ext.Ajax.request({
    			   		url : basePath + form.auditUrl,
    			   		params: {
    			   			id: Ext.getCmp('vr_id').value,
    			   			vr_newtitle:Ext.getCmp('vr_newtitle')?Ext.getCmp('vr_newtitle').value:'',
    			   			vr_purpose:Ext.getCmp('vr_purpose')?Ext.getCmp('vr_purpose').value:'',
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    		    			if(localJson.success){
    		    				//audit成功后刷新页面进入可编辑的页面 
    		    				showMessage('提示', '审核成功!', 1000);
    		    				window.location.reload();
    			   			} else {
    		    				if(localJson.exceptionInfo){
    		    	   				var str = localJson.exceptionInfo;
    		    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    		    	   					str = str.replace('AFTERSUCCESS', '');
    		    	   					showMessage("提示", str, 1000);
    		    	   					auditSuccess(function(){
    		    	   						window.location.reload();
    		    	   					});
    		    	   				} else {
    		    	   					showError(str);return;
    		    	   				}
    		    	   			}
    		    			}
    			   		}
    				});
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('vr_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				
    				
    				var vr_visittime = Ext.getCmp('vr_visittime'),
	    				vr_visitend = Ext.getCmp('vr_visitend'),
	    				vr_nexttime = Ext.getCmp('vr_nexttime');
	    			if(typeof (f = vr_visittime) !== 'undefined' && typeof (f = vr_visitend) !== 'undefined' && typeof (f = vr_nexttime) !== 'undefined'){
	    				if(!Ext.isEmpty(vr_visittime.value)){
	    					if(!Ext.isEmpty(vr_visitend.value) && Ext.Date.format(vr_visitend.value,'Y-m-d') < Ext.Date.format(vr_visittime.value,'Y-m-d')){
	    						showError('拜访结束时间小于拜访开始时间！');
	    		                return;
	    					}
	    					if(!Ext.isEmpty(vr_nexttime.value)){
	    						if(Ext.Date.format(vr_nexttime.value,'Y-m-d') < Ext.Date.format(vr_visittime.value,'Y-m-d')){
	    							showError('下次拜访时间小于拜访开始时间！');
	    			                return;
	    						}
	    						if(Ext.Date.format(vr_nexttime.value,'Y-m-d') < Ext.Date.format(vr_visitend.value,'Y-m-d')){
	    							showError('下次拜访时间小于拜访结束时间！');
	    			                return;
	    						}
	    					}
	    				}
	    			}
    				//this.FormUtil.onSubmit(Ext.getCmp('vr_id').value);
	    			this.onSubmit(Ext.getCmp('vr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},     			
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('vr_id').value);
    			}
    		}
    	});
	},
	onGridItemClick2: function(selModel,record){
    	this.onGridItemClick(selModel,record,'recordDetailDet');
    },
	onGridItemClick3: function(selModel,record){
    	this.onGridItemClick(selModel,record,'recordDetail');
    	
    },
    onGridItemClick4: function(selModel,record){
    	this.onGridItemClick(selModel,record,'VenderMaketing');
    	
    },
    onGridItemClick5: function(selModel,record){
    	this.onGridItemClick(selModel,record,'Price');
    	
    },
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var vr_visittime = Ext.getCmp('vr_visittime'),
			vr_visitend = Ext.getCmp('vr_visitend'),
			vr_nexttime = Ext.getCmp('vr_nexttime');
		if(typeof (f = vr_visittime) !== 'undefined' && typeof (f = vr_visitend) !== 'undefined' && typeof (f = vr_nexttime) !== 'undefined'){
			if(!Ext.isEmpty(vr_visittime.value)){
				if(!Ext.isEmpty(vr_visitend.value) && Ext.Date.format(vr_visitend.value,'Y-m-d') < Ext.Date.format(vr_visittime.value,'Y-m-d')){
					showError('拜访结束时间小于拜访开始时间！');
	                return;
				}
				if(!Ext.isEmpty(vr_nexttime.value)){
					if(Ext.Date.format(vr_nexttime.value,'Y-m-d') < Ext.Date.format(vr_visittime.value,'Y-m-d')){
						showError('下次拜访时间小于拜访开始时间！');
		                return;
					}
					if(Ext.Date.format(vr_nexttime.value,'Y-m-d') < Ext.Date.format(vr_visitend.value,'Y-m-d')){
						showError('下次拜访时间小于拜访结束时间！');
		                return;
					}
				}
			}
		}
		var detail1 = Ext.getCmp('grid');
		var detail2 = Ext.getCmp('recordDetailDet');
		var detail3 = Ext.getCmp('recordDetail');
		var detail4 = Ext.getCmp('VenderMaketing');
		var detail5 = Ext.getCmp('Price');
		var param1 = new Array();
		if(detail1){
			param1 =me.GridUtil.getGridStore(detail1);
		}
		var param2 = new Array();
		if(detail2){
			param2 =me.GridUtil.getGridStore(detail2);
		}
		if(detail3){
			param3 =me.GridUtil.getGridStore(detail3);
		}
		if(detail4){
			param4 =me.GridUtil.getGridStore(detail4);
		}
		if(detail5){
			param5 =me.GridUtil.getGridStore(detail5);
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
				me.save(r, param1, param2, param3, param4, param5);
			}else{
				me.FormUtil.checkForm();
			}		
	},
	save: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		//params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));	
		params.formStore = unescape(Ext.JSON.encode(r));	
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		params.param4 = unescape(arguments[4].toString().replace(/\\/g,"%"));
		params.param5 = unescape(arguments[5].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	   			
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition+'&gridCondition=vrd_vridIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=vrd_vridIS'+value;
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

			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   		    	formCondition+'&gridCondition=vrd_vridIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=vrd_vridIS'+value;
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
	
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var vr_visittime = Ext.getCmp('vr_visittime'),
			vr_visitend = Ext.getCmp('vr_visitend'),
			vr_nexttime = Ext.getCmp('vr_nexttime');
		if(typeof (f = vr_visittime) !== 'undefined' && typeof (f = vr_visitend) !== 'undefined' && typeof (f = vr_nexttime) !== 'undefined'){
			if(!Ext.isEmpty(vr_visittime.value)){
				if(!Ext.isEmpty(vr_visitend.value) && Ext.Date.format(vr_visitend.value,'Y-m-d') < Ext.Date.format(vr_visittime.value,'Y-m-d')){
					showError('拜访结束时间小于拜访开始时间！');
	                return;
				}
				if(!Ext.isEmpty(vr_nexttime.value)){
					if(Ext.Date.format(vr_nexttime.value,'Y-m-d') < Ext.Date.format(vr_visittime.value,'Y-m-d')){
						showError('下次拜访时间小于拜访开始时间！');
		                return;
					}
					if(Ext.Date.format(vr_nexttime.value,'Y-m-d') < Ext.Date.format(vr_visitend.value,'Y-m-d')){
						showError('下次拜访时间小于拜访结束时间！');
		                return;
					}
				}
			}
		}
		var detail1 = Ext.getCmp('grid');
		var detail2 = Ext.getCmp('recordDetailDet');
		var detail3 = Ext.getCmp('recordDetail');
		var detail4 = Ext.getCmp('VenderMaketing');
		var detail5 = Ext.getCmp('Price');
		var param1 = new Array();
		if(detail1){
			param1 =me.GridUtil.getGridStore(detail1);
		}
		var param2 = new Array();
		if(detail2){
			param2 =me.GridUtil.getGridStore(detail2);
		}
		if(detail3){
			param3 =me.GridUtil.getGridStore(detail3);
		}
		if(detail4){
			param4 =me.GridUtil.getGridStore(detail4);
		}
		if(detail5){
			param5 =me.GridUtil.getGridStore(detail5);
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
				me.update(r,param1, param2, param3, param4, param5);
			}else{
				me.FormUtil.checkForm();
			}
		
	},
	update:function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		//params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.formStore = unescape(Ext.JSON.encode(r));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		params.param4 = unescape(arguments[4].toString().replace(/\\/g,"%"));
		params.param5 = unescape(arguments[5].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
		//me.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			//me.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition;
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
			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   					formCondition;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   					formCondition;
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
	contains: function(string,substr,isIgnoreCase){
	    if(isIgnoreCase){
	    	string=string.toLowerCase();
	    	substr=substr.toLowerCase();
	    }
	    var startChar=substr.substring(0,1);
	    var strLen=substr.length;
	    for(var j=0;j<string.length-strLen+1;j++){
	    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
	    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	},
	autoInsert:function(){
		var vr_cuuu=Ext.getCmp('vr_cuuu');
		if(vr_cuuu.value==''){
			return;
		}
		Ext.Ajax.request({
	   		url : basePath + 'crm/customermgr/autoSaveVisitRecord3.action',
	   		params: {vr_cuuu:vr_cuuu.value},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = localJson.vr_id;
		   		    	var formCondition = 'vr_id' + "IS" + value ;
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=vrd_vridIS'+value;
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = localJson.vr_id;
			   		    	var formCondition = 'vr_id' + "IS" + value ;
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=vrd_vridIS'+value;
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
        		} else {
        			showError(str);
	   			}
	   		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	turnFeePlease:function(){
		var vr_id=Ext.getCmp('vr_id');
		if(vr_id.value==''){
			return;
		}
		Ext.Ajax.request({
	   		url : basePath + 'crm/customermgr/turnFeePlease3.action',
	   		params: {id:vr_id.value},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				//saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
    					showMessage('提示',localJson.log);
    					window.location.reload();
    				//});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				showError(str);
	   				} 
        		}
		});
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	onSubmit: function(id, allowEmpty, errFn, scope, errFnArgs){
		var me = this;
		var form = Ext.getCmp('form');
		if(form && form.getForm().isValid()){
			var s = me.checkFormDirty(form);
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
				var param = grids[0].GridUtil.getAllGridStore(grids[0]);
				if(grids[0].necessaryField && grids[0].necessaryField.length > 0 && (param == null || param == '') && (allowEmpty !== true)){
					var errInfo = grids[0].GridUtil.getUnFinish(grids[0]);
					if(errInfo.length > 0)
						showError("明细表有必填字段未完成填写<hr>" + errInfo);
					else
						showError("明细表还未添加数据,无法提交!");
					return;
				}
				Ext.each(grids, function(grid, index){
					if(grid.GridUtil){
						var msg = grid.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
							s = s + '<br/>' + grid.GridUtil.checkGridDirty(grid);
						}
					}
				});
			}
			if(s == '' || s == '<br/>'){
				me.FormUtil.submit(id);
			} else {
				Ext.MessageBox.show({
					title:'保存修改?',
					msg: '该单据已被修改:<br/>' + s + '<br/>提交前要先保存吗？',
					buttons: Ext.Msg.YESNOCANCEL,
					icon: Ext.Msg.WARNING,
					fn: function(btn){
						if(btn == 'yes'){
							if(typeof errFn === 'function')
								errFn.call(scope, errFnArgs);
							else
								//me.onUpdate(form, true);
								//先进行更新
			    				me.beforeUpdate();
						} else if(btn == 'no'){
							me.FormUtil.submit(id);	
						} else {
							return;
						}
					}
				});
			}
		} else {
			me.checkForm();
		}
	},
	checkFormDirty: function(){
		var form = Ext.getCmp('form');
		var s = '';
		form.getForm().getFields().each(function (item,index, length){
			if(item.logic!='ignore'){
				var value = item.value == null ? "" : item.value;
				if(item.xtype == 'htmleditor') {
					value  = item.getValue();
				}
				item.originalValue = item.originalValue == null ? "" : item.originalValue;

				if(Ext.typeOf(item.originalValue) != 'object'){


					if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
						var label = item.fieldLabel || item.ownerCt.fieldLabel ||
						item.boxLabel || item.ownerCt.title;//针对fieldContainer、radio、fieldset等
						if(label){
							s = s + '&nbsp;' + label.replace(/&nbsp;/g,'');
						}
					}

				}
			}
		});
		return (s == '') ? s : ('表单字段(<font color=green>'+s+'</font>)已修改');
	},
	setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	}
});