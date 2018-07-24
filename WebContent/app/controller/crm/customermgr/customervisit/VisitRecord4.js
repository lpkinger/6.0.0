Ext.QuickTips.init();
Ext.define('erp.controller.crm.customermgr.customervisit.VisitRecord4', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.customermgr.customervisit.VisitRecord4','core.form.Panel','core.grid.Panel2','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
      		'erp.view.core.button.Down','erp.view.core.button.UpExcel','core.button.VoCreate','core.button.TurnBorrow',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
  			'crm.customermgr.customervisit.RecordDetail','crm.customermgr.customervisit.RecordDetailDet','crm.customermgr.customervisit.Marketing','crm.customermgr.customervisit.VenderMaketing','crm.customermgr.customervisit.Rival',
  			'crm.customermgr.customervisit.Expect','crm.customermgr.customervisit.Price','crm.customermgr.customervisit.ProductPlanning'
  	],
	init:function(){
		var me = this;
		me.gridLastSelected = null;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'recordDetail': { 
    			itemclick: this.onGridItemClick2
    		},
    		'recordDetailDet': { 
    			itemclick: this.onGridItemClick3
    		},
    		'Marketing': { 
    			itemclick: this.onGridItemClick4
    		},
    		'VenderMaketing': { 
    			itemclick: this.onGridItemClick5
    		},
    		'Rival': { 
    			itemclick: this.onGridItemClick6
    		},
    		'Price': { 
    			itemclick: this.onGridItemClick7
    		},
    		'Expect': { 
    			itemclick: this.onGridItemClick8
    		},
    		'ProductPlanning': { 
    			itemclick: this.onGridItemClick9
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
			},'erpVoCreateButton':{
    			beforerender:function(btn){
    				btn.setText("自动生成");
    			},
    			click:function(){
    				this.autoInsert();
    			}
    		},
    		'erpTurnBorrowButton':{
    			beforerender:function(btn){
    				btn.setText("转费用报销");
    			},
    			afterrender:function(btn){
    				var status = Ext.getCmp('vr_statuscode');
    				var isturn=Ext.getCmp('vr_isturnfeeplease');
    				if(!isturn||isturn.value=='1'||status.value != 'AUDITED'){
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
					//me.updateGood();
					this.beforeUpdate();
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addVisitRecord4', '新增资源开发记录 ', 'jsps/crm/customermgr/customervisit/visitRecord4.jsp');
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
    				this.FormUtil.onAudit(Ext.getCmp('vr_id').value);
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
    				this.FormUtil.onSubmit(Ext.getCmp('vr_id').value);
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
    	this.onGridItemClick(selModel,record,'Marketing');
    	
    },
    onGridItemClick5: function(selModel,record){
    	this.onGridItemClick(selModel,record,'VenderMaketing');
    	
    },
    onGridItemClick6: function(selModel,record){
    	this.onGridItemClick(selModel,record,'Rival');
    	
    },
    onGridItemClick7: function(selModel,record){
    	this.onGridItemClick(selModel,record,'Price');
    	
    },
    onGridItemClick8: function(selModel,record){
    	this.onGridItemClick(selModel,record,'Expect');
    	
    },
    onGridItemClick9: function(selModel,record){
    	this.onGridItemClick(selModel,record,'ProductPlanning');
    	
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
		var detail1 = Ext.getCmp('grid');
		var detail2 = Ext.getCmp('recordDetailDet');
		var detail3 = Ext.getCmp('recordDetail');
		var detail4 = Ext.getCmp('Marketing');
		var detail5 = Ext.getCmp('VenderMaketing');
		var detail6 = Ext.getCmp('Rival');
		var detail7 = Ext.getCmp('Price');
		var detail8 = Ext.getCmp('Expect');
		var detail9 = Ext.getCmp('ProductPlanning');
		var param1 = new Array();
		if(detail1){
			param1 =me.GridUtil.getGridStore(detail1);
		}
		var param2 = new Array();
		if(detail2){
			param2 =me.GridUtil.getGridStore(detail2);
		}
		var param3 = new Array();
		if(detail3){
			param3 =me.GridUtil.getGridStore(detail3);
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
		var param7 = new Array();
		if(detail7){
			param7 =me.GridUtil.getGridStore(detail7);
		}
		var param8 = new Array();
		if(detail8){
			param8 =me.GridUtil.getGridStore(detail8);
		}
		var param9 = new Array();
		if(detail9){
			param9 =me.GridUtil.getGridStore(detail9);
		}
		
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
			param5 = param5 == null ? [] : "[" + param5.toString().replace(/\\/g,"%") + "]";
			param6 = param6 == null ? [] : "[" + param6.toString().replace(/\\/g,"%") + "]";
			param7 = param7 == null ? [] : "[" + param7.toString().replace(/\\/g,"%") + "]";
			param8 = param8 == null ? [] : "[" + param8.toString().replace(/\\/g,"%") + "]";
			param9 = param9 == null ? [] : "[" + param9.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.save(r, param1, param2, param3, param4, param5, param6, param7, param8, param9);
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
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		params.param4 = unescape(arguments[4].toString().replace(/\\/g,"%"));
		params.param5 = unescape(arguments[5].toString().replace(/\\/g,"%"));
		params.param6 = unescape(arguments[6].toString().replace(/\\/g,"%"));
		params.param7 = unescape(arguments[7].toString().replace(/\\/g,"%"));
		params.param8 = unescape(arguments[8].toString().replace(/\\/g,"%"));
		params.param9 = unescape(arguments[9].toString().replace(/\\/g,"%"));
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
		var detail1 = Ext.getCmp('grid');
		var detail2 = Ext.getCmp('recordDetailDet');
		var detail3 = Ext.getCmp('recordDetail');
		var detail4 = Ext.getCmp('Marketing');
		var detail5 = Ext.getCmp('VenderMaketing');
		var detail6 = Ext.getCmp('Rival');
		var detail7 = Ext.getCmp('Price');
		var detail8 = Ext.getCmp('Expect');
		var detail9 = Ext.getCmp('ProductPlanning');
		var param1 = new Array();
		if(detail1){
			param1 =me.GridUtil.getGridStore(detail1);
		}
		var param2 = new Array();
		if(detail2){
			param2 =me.GridUtil.getGridStore(detail2);
		}
		var param3 = new Array();
		if(detail3){
			param3 =me.GridUtil.getGridStore(detail3);
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
		var param7 = new Array();
		if(detail7){
			param7 =me.GridUtil.getGridStore(detail7);
		}
		var param8 = new Array();
		if(detail8){
			param8 =me.GridUtil.getGridStore(detail8);
		}
		var param9 = new Array();
		if(detail9){
			param9 =me.GridUtil.getGridStore(detail9);
		}
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
			param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
			param5 = param5 == null ? [] : "[" + param5.toString().replace(/\\/g,"%") + "]";
			param6 = param6 == null ? [] : "[" + param6.toString().replace(/\\/g,"%") + "]";
			param7 = param7 == null ? [] : "[" + param7.toString().replace(/\\/g,"%") + "]";
			param8 = param8 == null ? [] : "[" + param8.toString().replace(/\\/g,"%") + "]";
			param9 = param9 == null ? [] : "[" + param9.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.update(r,param1, param2, param3, param4, param5, param6, param7, param8, param9);
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
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		params.param4 = unescape(arguments[4].toString().replace(/\\/g,"%"));
		params.param5 = unescape(arguments[5].toString().replace(/\\/g,"%"));
		params.param6 = unescape(arguments[6].toString().replace(/\\/g,"%"));
		params.param7 = unescape(arguments[7].toString().replace(/\\/g,"%"));
		params.param8 = unescape(arguments[8].toString().replace(/\\/g,"%"));
		params.param9 = unescape(arguments[9].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
		//me.getActiveTab().setLoading(true);//loading...
//		console.log(params);
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
	   		url : basePath + 'crm/customermgr/turnFeePlease4.action',
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
	}
});