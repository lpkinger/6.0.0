Ext.QuickTips.init();
Ext.define('erp.controller.sys.alert.AlertItem', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'sys.alert.AlertItem','sys.alert.AlertItemGrid','core.form.Panel','core.grid.Panel2','core.button.CatchWorkContent',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.form.DetailTextField','core.form.FileField','common.datalist.GridPanel','common.datalist.Toolbar',
    		'core.form.HrOrgSelectField','core.button.Back','core.form.YnField','core.trigger.TextAreaTrigger',
    		'core.button.Banned','core.button.ResBanned'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'#grid': { 
    			itemclick: function(selModel, record){								
					this.onGridItemClick(selModel, record);
    			}
    		},
    		'#alertOutput': { 
    			itemclick: function(selModel, record){								
					this.onGridItemClick(selModel, record);
					
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('AlertItem', '新增预警项目', 'jsps/sys/alert/alertItem.jsp?whoami='+caller);
    			}
        	},
        	'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					this.beforeSave(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					this.beforeUpdate();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('ai_id').value));
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('ai_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('ai_id').value, true, this.beforeUpdate, this);
    			}
    		},
    		'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ai_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ai_id').value);
				}
			},
			'erpAuditButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('ai_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ai_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('ai_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ai_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ai_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}else{
	    				var enable = Ext.getCmp('ai_enable');
	    				if(enable && enable.value != '0'){
	    					btn.hide();
	    				}
    				}
    			},
    			click: function(btn){
    				this.resBanned();
    			}
    		},
    		'erpBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ai_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}else{
	    				var enable = Ext.getCmp('ai_enable');
	    				if(enable && enable.value != '-1'){
	    					btn.hide();
	    				}
    				}
    			},
    			click: function(btn){
    				this.banned();
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	contains: function(string,substr,isIgnoreCase){
	    if(isIgnoreCase){
	    	string=string.toLowerCase();
	    	substr=substr.toLowerCase();
	    }
	    var startChar=substr.substring(0,1);
	    var strLen=substr.length;
	    for(var j=0;j<string.length-strLen+1;j++){
	    	if(string.charAt(j)==startChar){
	    		if(string.substring(j,j+strLen)==substr){
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	},
    beforeSave: function(me, ignoreWarn, opts, extra){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		
		var params1 = new Array();
		var params2 = new Array();
		var data = Ext.getCmp('grid').getStore().data.items;
		for(var i=0;i<data.length;i++){
			data[i].dirty=true;
		}
		var data1 = me.GridUtil.getGridStore(Ext.getCmp('grid'));
		params1[0] = data1 == null ? [] : "[" + data1.toString().replace(/\\/g,"%") + "]";
		var data2 = me.GridUtil.getGridStore(Ext.getCmp('alertOutput'));
		params2[0] = data2 == null ? [] : "[" + data2.toString().replace(/\\/g,"%") + "]";
		
		if(form.getForm().isValid()){
			//form里面数据
			var r = (opts && opts.dirtyOnly) ? form.getForm().getValues(false, true) : 
				form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){					
					if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(f && opts && opts.dirtyOnly) {
					extra = (extra || '') + 
					'\n(' + f.fieldLabel + ') old: ' + f.originalValue + ' new: ' + r[k];
				}				
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			if(opts && opts.dirtyOnly && form.keyField) {
				r[form.keyField] = form.down("#" + form.keyField).getValue();
			}
			if(!me.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			me.save(r, params1,params2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	save: function(r,params1,params2){
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
    	var formStore = unescape(escape(Ext.JSON.encode(r)));
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params :{
	   			params1:unescape(params1.toString()),
	   			params2:unescape(params2.toString()),
	   			formStore:formStore,
	   			caller:caller
	   		},
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
			   					formCondition+'&gridCondition=AA_AIIDIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=AA_AIIDIS'+value;
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
				   		    	formCondition+'&gridCondition=AA_AIIDIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=AA_AIIDIS'+value;
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
	beforeUpdate: function(me, ignoreWarn, opts, extra){
    	var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var params1 = new Array();	
		var params2 = new Array();
		var data = Ext.getCmp('grid').getStore().data.items;
		for(var i=0;i<data.length;i++){
			data[i].dirty=true;
		}
		var data1 = me.GridUtil.getGridStore(Ext.getCmp('grid'));
		params1[0] = data1 == null ? [] : "[" + data1.toString().replace(/\\/g,"%") + "]";
		var data2 = me.GridUtil.getGridStore(Ext.getCmp('alertOutput'));
		params2[0] = data2 == null ? [] : "[" + data2.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			//form里面数据
			var r = (opts && opts.dirtyOnly) ? form.getForm().getValues(false, true) : 
				form.getValues();
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'itemgrid'){					
					if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(f && opts && opts.dirtyOnly) {
					extra = (extra || '') + 
					'\n(' + f.fieldLabel + ') old: ' + f.originalValue + ' new: ' + r[k];
				}				
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			if(opts && opts.dirtyOnly && form.keyField) {
				r[form.keyField] = form.down("#" + form.keyField).getValue();
			}
			if(!me.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			me.update(r,params1,params2);
		}else{
			me.FormUtil.checkForm();
		}
	},
	update:function(r,params1,params2){
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		var formStore = unescape(escape(Ext.JSON.encode(r)));
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params : {
	   			params1:unescape(params1.toString()),
	   			params2:unescape(params2.toString()),
	   			formStore:formStore
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
		   		var localJson = new Ext.decode(response.responseText);
	   			if(localJson.success){
					showMessage('提示', '更新成功!', 1000);
	   				var value = r[form.keyField];
	   		    	var formCondition = form.keyField + "IS" + value ;
	   		    	if(me.contains(window.location.href, '?', true)){
		   		    	window.location.href = window.location.href.split('?')[0] +'?whoami='+caller +'&formCondition=' + 
		   					formCondition+'&gridCondition=AA_AIIDIS'+value;
		   		    } else {
		   		    	window.location.href = window.location.href + '?whoami='+caller+'&formCondition=' + 
		   					formCondition+'&gridCondition=AA_AIIDIS'+value;
		   		    }
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					showMessage('提示', '更新成功!', 1000);
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href.split('?')[0] +'?whoami='+caller +'&formCondition=' + 
			   					formCondition+'&gridCondition=AA_AIIDIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?whoami='+caller+'&formCondition=' + 
			   					formCondition+'&gridCondition=AA_AIIDIS'+value;
			   		    }
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();
	   			}
   			}  		
		});
	},
	resBanned: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var value = Ext.getCmp('ai_id').value;
		Ext.Ajax.request({
	   		url : basePath + form.resBannedUrl,
	   		params : {
	   			id:value,
	   			caller: caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
		   		var localJson = new Ext.decode(response.responseText);
		   		if(localJson.success){
		   			alert('反禁用成功');
	   		    	window.location.reload();
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					alert('反禁用成功');
		   		    	window.location.reload();
	   					str = str.replace('AFTERSUCCESS', '');
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			}
	   		}
		});
	},
	banned: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var value = Ext.getCmp('ai_id').value;
		Ext.Ajax.request({
	   		url : basePath + form.bannedUrl,
	   		params : {
	   			id:value,
	   			caller: caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
		   		var localJson = new Ext.decode(response.responseText);
		   		if(localJson.success){
		   			alert('禁用成功');
	   		    	window.location.reload();
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					alert('禁用成功');
	   					window.location.reload();
	   					str = str.replace('AFTERSUCCESS', '');
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			}
	   		}
		});
	}
});