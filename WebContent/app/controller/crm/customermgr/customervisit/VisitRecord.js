Ext.QuickTips.init();
Ext.define('erp.controller.crm.customermgr.customervisit.VisitRecord', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.customermgr.customervisit.VisitRecord','core.form.Panel','core.grid.Panel2','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
      		'erp.view.core.button.Down','erp.view.core.button.UpExcel','core.button.VoCreate','core.button.TurnBorrow',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
  			'crm.customermgr.customervisit.RecordDetail','crm.customermgr.customervisit.RecordDetailDet','crm.customermgr.customervisit.ProgressGrid','crm.customermgr.customervisit.InfoGrid','crm.customermgr.customervisit.ChanceDetail',
  			'crm.customermgr.customervisit.VisitFeedBack', 'crm.chance.ChanceManage','core.form.DateHourMinuteField','core.button.Modify',
  			'core.form.HrOrgSelectField','oa.doc.OrgTreePanel'
  	],
	init:function(){
		var me = this;
		me.gridLastSelected = null;
		this.control({
			'field[name=vr_visitend]':{
				change:function(v){					
					var vr_visitend = Ext.getCmp('vr_visitend').value;	
					var nowtime=new Date();
					nowtime = new Date(nowtime.getTime() + 1000*60*30);
					if(vr_visitend>nowtime){
						showError("结束时间不能大于当前系统时间");
					}	
				}				
			},
			
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'recordDetail': { 
    			itemclick: this.onGridItemClick2
    		},
    		'recordDetailDet': { 
    			itemclick: this.onGridItemClick3
    		},
    		'InfoGrid': { 
    			itemclick: this.onGridItemClick4
    		},
    		'ChanceDetail': { 
    			itemclick: this.onGridItemClick5
    		},
    		'VisitFeedBack':{
    			itemclick: this.onGridItemClick6
    		},
    		'field[name=vr_newtitle]':{
    			beforerender:function(field){
    				var status = Ext.getCmp('vr_statuscode');
    				if(status && status.value == 'COMMITED'){
    					field.readOnly=false;
    				}
    			}
    		},
    		'dbfindtrigger[name=pi_prodname]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='vr_recorder';
	    			trigger.mappingKey='epd_emname';
    			}
    		},
    		'dbfindtrigger[name=vrd_d1]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='vr_defaultorname';
	    			trigger.mappingKey='fcs_departmentname';
	    			trigger.dbMessage='请先选择所属组织';
    			}
    		},
    		'dbfindtrigger[name=pi_bccode]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='vr_cuuu';
	    			trigger.mappingKey='bc_custcode';
	    			trigger.dbMessage='请先选择客户编号';
    			}
    		},
    		'multidbfindtrigger[name=cup_name]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='vr_cuuu';
	    			trigger.mappingKey='ct_cucode';
	    			trigger.dbMessage='请先选择客户编号';
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
    			},
    			initialize: function(field) {
    				// field.textareaEl.dom.value与value不一致，重新pushValue
    				field.textareaEl.dom.value = field.cleanHtml(field.value);
    				field.pushValue();
    				field.syncValue();
    				field.resetOriginalValue();
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
    		'erpTurnCustomerButton': {
    			beforerender:function(btn){
    				btn.setText("评价保存");
    			},
    			afterrender:function(btn){
    				var status = Ext.getCmp('vr_statuscode');    				
    				if(status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click:function(){
    				var me = this.FormUtil;
    				var form = Ext.getCmp('form');
    				Ext.Ajax.request({
    			   		url : basePath + form.pingjiaUrl,
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
    		    				showMessage('提示', '评价成功!', 1000);
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
    		'erpTurnBorrowButton':{
    			beforerender:function(btn){
    				btn.setText("转差旅费用");
    			},
    			afterrender:function(btn){
    				var status = Ext.getCmp('vr_statuscode');
    				var isturn=Ext.getCmp('vr_isturnfeeplease');
/*    				if(!isturn||isturn.value=='1'||status.value != 'AUDITED'){
    					btn.hide();
    				}*/
    				if(status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(){
    				this.turnFeePlease();
    			}
    		},
			'erpSaveButton': {
				click: function(btn){ 			   
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					if(Ext.getCmp('vr_visittime').value && Ext.getCmp('vr_visitend').value ){
	 					   var start=Ext.getCmp('vr_visittime').value,
	 					   end=Ext.getCmp('vr_visitend').value;
	 					   if(end.getTime()<start.getTime()){
	 						   showError('结束时间不能小于拜访时间!');
	 						   return;
	 					   }
	 				}
					if(Ext.getCmp('vr_nexttime').value && Ext.getCmp('vr_visitend').value ){
	 					   var nexttime=Ext.getCmp('vr_nexttime').value,
	 					   end=Ext.getCmp('vr_visitend').value;
	 					   if(nexttime.getTime()<end.getTime()){
	 						   showError('下次拜访时间不能小于本次拜访结束时间!');
	 						   return;
	 					   }
	 				}
					//保存之前的一些前台的逻辑判定
					this.beforeSave();
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('vr_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					if(Ext.getCmp('vr_visittime').value && Ext.getCmp('vr_visitend').value ){
	 					   var start=Ext.getCmp('vr_visittime').value,
	 					   end=Ext.getCmp('vr_visitend').value;
	 					   if(end.getTime()<start.getTime()){
	 						   showError('结束时间不能小于拜访时间!');
	 						   return;
	 					   }
	 				}
					if(Ext.getCmp('vr_nexttime').value && Ext.getCmp('vr_visitend').value ){
	 					   var nexttime=Ext.getCmp('vr_nexttime').value,
	 					   end=Ext.getCmp('vr_visitend').value;
	 					   if(nexttime.getTime()<end.getTime()){
	 						   showError('下次拜访时间不能小于本次拜访结束时间!');
	 						   return;
	 					   }
	 				}
					this.beforeUpdate();
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addVisitRecord', '新增拜访记录 ', 'jsps/crm/customermgr/customervisit/visitRecord.jsp');
				}
			},
			
			'erpCloseButton': {
				click: function(btn){
					var win=parent.Ext.getCmp('singlewin');	
					if(win)
					win.close();
					else
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
    				this.FormUtil.onSubmit(Ext.getCmp('vr_id').value, true, this.beforeUpdate, this);
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
    	this.onGridItemClick(selModel,record,'recordDetail');
    	
    },
    onGridItemClick3: function(selModel,record){
    	this.onGridItemClick(selModel,record,'recordDetailDet');
    	
    },
    onGridItemClick4: function(selModel,record){
    	this.onGridItemClick(selModel,record,'InfoGrid');
    	
    },
    onGridItemClick5: function(selModel,record){
    	this.onGridItemClick(selModel,record,'ChanceDetail');
    	
    },
    onGridItemClick6: function(selModel,record){
    	this.onGridItemClick(selModel,record,'VisitFeedBack');
    	
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
		var detail = Ext.getCmp('grid');		
		var ass = Ext.getCmp('recordDetail');
		var flow = Ext.getCmp('recordDetailDet');
		var detail4 = Ext.getCmp('InfoGrid');
		var detail5 = Ext.getCmp('ChanceDetail');
		var detail6 = Ext.getCmp('VisitFeedBack');
		Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = new Array();
		if(ass) {
			param2 = me.GridUtil.getGridStore(ass);
		}
		var param3 = new Array();
		if(flow){
			param3 =me.GridUtil.getGridStore(flow);
		}		
		var param4 = new Array();
		if(detail4){
			param4 =me.GridUtil.getGridStore(detail4);
		}
		var param5 = new Array();
		if(detail5){
			param5 =me.GridUtil.getGridStore(detail5);
		}
		var param6 = new Array();
		if(detail6){
			param6 =me.GridUtil.getGridStore(detail6);
		}
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : param3.toString().replace(/\\/g,"%");
			param6 = param6 == null ? [] : "[" + param6.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.save(r, param1, param2, param3,param4, param5, param6);
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
		params.param6 = unescape(arguments[6].toString().replace(/\\/g,"%"));
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
		var detail = Ext.getCmp('grid');		
		var ass = Ext.getCmp('recordDetail');
		var flow = Ext.getCmp('recordDetailDet');
		var detail4 = Ext.getCmp('InfoGrid');
		var detail5 = Ext.getCmp('ChanceDetail');
		var detail6 = Ext.getCmp('VisitFeedBack');
		Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = new Array();
		if(ass) {
			param2 = me.GridUtil.getGridStore(ass);
		}
		var param3 = new Array();
		if(flow) {
			param3 = me.GridUtil.getGridStore(flow);
		}	
		var param4 = new Array();
		if(detail4){
			param4 =me.GridUtil.getGridStore(detail4);
		}
		var param5 = new Array();
		if(detail5){
			param5 =me.GridUtil.getGridStore(detail5);
		}
		var param6 = new Array();
		if(detail6){
			param6 =me.GridUtil.getGridStore(detail6);
		}
		
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
			param5 = param5 == null ? [] : param5.toString().replace(/\\/g,"%");
			param6 = param6== null ? [] : "[" + param6.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.update(r, param1, param2, param3, param4, param5, param6);
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
		params.param6 = unescape(arguments[6].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
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
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	autoInsert:function(){
		var vr_cuuu=Ext.getCmp('vr_cuuu');
		if(vr_cuuu.value==''){
			return;
		}
		Ext.Ajax.request({
	   		url : basePath + 'crm/customermgr/autoSaveVisitRecord.action',
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
	turnFeePlease:function(){
		var vr_id=Ext.getCmp('vr_id');
		if(vr_id.value==''){
			return;
		}
		Ext.Ajax.request({
	   		url : basePath + 'crm/customermgr/turnFeePlease.action',
	   		params: {id:vr_id.value},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
    					showMessage('提示',localJson.log);
    					window.location.reload();
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				showError(str);
	   				} 
        		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});