Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductApproval', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),   
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','scm.product.ProductApproval','core.button.Add','core.button.Submit','core.form.CheckBoxGroup',
    		'core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close',
			'core.button.Update','core.button.Delete','core.button.ResAudit','core.form.MultiField',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.FileField',
			'scm.product.ProductApprovals.prodApprovalDetail','scm.product.ProductApprovals.prodApprovalDetailGrid',
			'scm.product.ProductApprovals.productApprovalDetailGrid','scm.product.ProductApprovals.prodAppDetail',
			'scm.product.ProductApprovals.prodAppDetailGrid','scm.product.ProductApprovals.productApprovalDetail',
			'scm.product.ProductApprovals.prodAppFinal','scm.product.ProductApprovals.ProdAppDetailsave',
			'scm.product.ProductApprovals.ProdAppFinalsave','scm.product.ProductApprovals.ProdApprovalDetailsave',
			'scm.product.ProductApprovals.ProductApprovalDetailsave'
	],
	init:function(){
		var me = this;
		this.control({
			/*'ProductApprovalDetailsaveButton':{
				click:function(btn){
					if(Ext.getCmp('pa_id').value==''){
						return;
					}
					var form=Ext.getCmp('productApprovalDetail');
					var grid=Ext.getCmp('productApprovalDetailGrid');
					me.save(form,grid,'scm/product/saveproductApprovalDetail.action');
				}
			},
			'ProdAppFinalsaveButton':{
				click:function(btn){
					if(Ext.getCmp('pa_id').value==''){
						return;
					}
					var form=Ext.getCmp('prodAppFinal');
					var grid=false;
					me.save(form,grid,'scm/product/saveApprovalResult.action');
				}
			},
			'ProdApprovalDetailsaveButton':{
				click:function(btn){
					if(Ext.getCmp('pa_id').value==''){
						return;
					}
					var form=Ext.getCmp('prodApprovalDetail');
					var grid=Ext.getCmp('prodApprovalDetailGrid');
					me.save(form,grid,'scm/product/saveprodApprovalDetail.action');
				}
			},
			'ProdAppDetailsaveButton':{
				click:function(btn){
					if(Ext.getCmp('pa_id').value==''){
						return;
					}
					var form=Ext.getCmp('prodAppDetail');
					var grid=Ext.getCmp('prodAppDetailGrid');
					me.save(form,grid,'scm/product/saveprodAppDetail.action');
				}
			},
			'#save': {
				click: function(btn){	
					var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				if(! me.FormUtil.checkForm()){
    					return;
    				}
    				if(form.keyField){
	    				if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
	    					me.FormUtil.getSeqId(form);
	    				}
    				}
    				me.saveAll(form);
				}
			},*/
			'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'mfilefield':{
    			beforerender:function(f){
    				f.readOnly=false;
    			}
    		},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pa_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addProductApproval', '新增认定单', 'jsps/scm/product/ProductApproval.jsp');
				}
			},
			'erpCloseButton': {
				afterrender:function(btn){
					
				},
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pa_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pa_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pa_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pa_id').value);
				}
			}
		});
	},
   /* save:function(form,grid,url){
    	var me=this;
    	if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
		}
    	if(form.keyField){
    		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
    			me.FormUtil.getSeqId(form);
    		}
    	}
		var r=form.getValues();
		r.pa_id=Ext.getCmp('pa_id').value;
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(me.contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		r=unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		var param = new Array();
		if(grid){
			param =this.GridUtil.getGridStore(grid);
		}
		param = param == null ? [] : "[" + param.toString().replace(/\\/g,"%") + "]";
		Ext.Ajax.request({
	   		url : basePath + url,
	   		params : {formStore:r,gridStore:param},
	   		method : 'post',
	   		callback : function(options,success,response){	   			
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
    					//window.location.reload();
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
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
    saveAll:function(form){
    	form.setLoading(true);//loading...
    	var me=this;
    	var grid1=Ext.getCmp('productApprovalDetailGrid');//productApprovalDetailGrid;
    	var grid2=Ext.getCmp('prodApprovalDetailGrid');//prodApprovalDetailGrid
    	var grid3=Ext.getCmp('prodAppDetailGrid');//prodAppDetailGrid
    	var param1=new Array();
    	var param2=new Array();
    	var param3=new Array();
    	if(grid1){
    		param1=me.GridUtil.getGridStore(grid1);
    	}
    	if(grid2){
    		param2=me.GridUtil.getGridStore(grid2);
    	}
    	if(grid3){
    		param3=me.GridUtil.getGridStore(grid3);
    	}
    	param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		var params=new Object();
		params.param1 = unescape(param1.toString().replace(/\\/g,"%"));
		params.param2 = unescape(param2.toString().replace(/\\/g,"%"));
		params.param3 = unescape(param3.toString().replace(/\\/g,"%"));
		var r = form.getValues();
		var r1=Ext.getCmp("productApprovalDetail").getValues();
		var r2=Ext.getCmp('prodApprovalDetail').getValues();
		var r3=Ext.getCmp('prodAppDetail').getValues();
		var r4=Ext.getCmp('prodAppFinal').getValues();
		for(var n in r1){
			r[n]=r1[n];
		}
		for(var n in r2){
			r[n]=r2[n];
		}
		for(var n in r3){
			r[n]=r3[n];
		}
		for(var n in r4){
			r[n]=r4[n];
		}
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(me.contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore=unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	  
	   			form.setLoading(false);//loading...
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
    updateAll:function(form){
    	form.setLoading(true);//loading...
    	var me=this;
    	var grid1=Ext.getCmp('productApprovalDetailGrid');//productApprovalDetailGrid;
    	var grid2=Ext.getCmp('prodApprovalDetailGrid');//prodApprovalDetailGrid
    	var grid3=Ext.getCmp('prodAppDetailGrid');//prodAppDetailGrid
    	var param1=new Array();
    	var param2=new Array();
    	var param3=new Array();
    	if(grid1){
    		param1=me.GridUtil.getGridStore(grid1);
    	}
    	if(grid2){
    		param2=me.GridUtil.getGridStore(grid2);
    	}
    	if(grid3){
    		param3=me.GridUtil.getGridStore(grid3);
    	}
    	param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		var params=new Object();
		params.param1 = unescape(param1.toString().replace(/\\/g,"%"));
		params.param2 = unescape(param2.toString().replace(/\\/g,"%"));
		params.param3 = unescape(param3.toString().replace(/\\/g,"%"));
		var r = form.getValues();
		var r1=Ext.getCmp("productApprovalDetail").getValues();
		var r2=Ext.getCmp('prodApprovalDetail').getValues();
		var r3=Ext.getCmp('prodAppDetail').getValues();
		var r4=Ext.getCmp('prodAppFinal').getValues();
		for(var n in r1){
			r[n]=r1[n];
		}
		for(var n in r2){
			r[n]=r2[n];
		}
		for(var n in r3){
			r[n]=r3[n];
		}
		for(var n in r4){
			r[n]=r4[n];
		}
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(me.contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore=unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	  
	   			form.setLoading(false);//loading...
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//update成功后刷新页面进入可编辑的页面 
		   		    	window.location.reload();
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	   						//update成功后刷新页面进入可编辑的页面 
			   		    	window.location.reload();
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
    },*/
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
	/*contains: function(string,substr,isIgnoreCase){
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
	}*/
});