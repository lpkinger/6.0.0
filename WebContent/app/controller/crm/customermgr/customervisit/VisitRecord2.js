Ext.QuickTips.init();
Ext.define('erp.controller.crm.customermgr.customervisit.VisitRecord2', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.customermgr.customervisit.VisitRecord2','core.form.Panel','core.grid.Panel2','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
      		'erp.view.core.button.Down','erp.view.core.button.UpExcel',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
  			'crm.customermgr.customervisit.RecordDetail','crm.customermgr.customervisit.RecordDetailDet','crm.customermgr.customervisit.ProgressGrid','crm.customermgr.customervisit.InfoGrid','crm.customermgr.customervisit.ChanceDetail',
  			'crm.customermgr.customervisit.VisitFeedBack'
  	],
	init:function(){
		var me = this;
		me.gridLastSelected = null;
		this.control({
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
					me.FormUtil.onAdd('addVisitRecord', '新增拜访记录 ', 'jsps/crm/customermgr/customervisit/visitRecord.jsp');
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
    		},
			'#vr_prjname':{
				change:function(field){
					var prjname=Ext.getCmp('vr_prjname').value;
					Ext.getCmp('vr_title').setValue(prjname);
				}
			},
    		'dbfindtrigger[name=vr_cuuu]': { 
    			/*afterrender:function(trigger){
    			trigger.dbKey='prj_code';//dbfind表 关联表 的ID
    			trigger.dbMessage='请先选择项目！';
    			}*/
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
    	this.onGridItemClick(selModel,record,'ProgressGrid');
    	
    },
    onGridItemClick5: function(selModel,record){
    	this.onGridItemClick(selModel,record,'InfoGrid');
    	
    },
    onGridItemClick6: function(selModel,record){
    	this.onGridItemClick(selModel,record,'ChanceDetail');
    	
    },
    onGridItemClick7: function(selModel,record){
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
		var detail1 = Ext.getCmp('InfoGrid');
		var detail2 = Ext.getCmp('VisitFeedBack');
		var param1 = new Array();
		if(detail1){
			param1 =me.GridUtil.getGridStore(detail1);
		}
		var param2 = new Array();
		if(detail2){
			param2 =me.GridUtil.getGridStore(detail2);
			}
		
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.save(r, param1, param2);
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
		var detail1 = Ext.getCmp('InfoGrid');
		var detail2 = Ext.getCmp('VisitFeedBack');
		var param1 = new Array();
		if(detail1){
			param1 =me.GridUtil.getGridStore(detail1);
		}
		var param2 = new Array();
		if(detail2){
			param2 =me.GridUtil.getGridStore(detail2);
		}
		
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		
			if(form.getForm().isValid()){
				Ext.each(form.items.items, function(item){
					if(item.xtype == 'numberfield'){
						if(item.value == null || item.value == ''){
							item.setValue(0);
						}
					}
				});
				var r = form.getValues();
				me.update(r, param1, param2);
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
	updateGood:function(){
		var vr_good=Ext.getCmp('vr_good');
		if(vr_good.getValue()==vr_good.originalValue){
			return;
		}
		Ext.Ajax.request({
	   		url : basePath + 'crm/customermgr/updateVisitRecordGood.action',
	   		params: {id:Ext.getCmp('vr_id').value,vr_good:vr_good.getValue()},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				updateSuccess(function(btn){
    					//update成功后刷新页面进入可编辑的页面 
    					window.location.reload();
	   				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					updateSuccess(function(btn){
	    					//update成功后刷新页面进入可编辑的页面 
	   						window.location.reload();
		   				});
	   				}
        			showError(str);return;
        		} else {
	   				updateFailure();
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
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});