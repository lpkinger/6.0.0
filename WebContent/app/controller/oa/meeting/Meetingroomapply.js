Ext.QuickTips.init();
Ext.define('erp.controller.oa.meeting.Meetingroomapply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'oa.meeting.Meetingroomapply','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2','core.trigger.SchedulerTrigger',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger','core.trigger.MultiDbfindTrigger',
    			'core.form.YnField','core.trigger.DbfindTrigger','core.button.Scan','oa.meeting.MeetingDetail','core.grid.YnColumn',
    			'erp.view.core.button.AddDetail','erp.view.core.button.DeleteDetail','oa.meeting.MeetingDetailbar','core.button.ConfirmMan',
    			'core.button.TurnDoc','core.form.HrOrgSelectField','core.form.ConDateHourMinuteField','core.button.ReLoad','core.button.Cancel'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'hidden[id=ma_mrcode]':{
    			change:function(f){
    				var grid=Ext.getCmp('grid');
    				var code=f.value;
    				grid.store.each(function(record){
    					if(!me.GridUtil.isBlank(grid, record.data)) {
    						record.set('mad_mrcode', code);
    					}
    				});
    			}
    		},
    		'erpGridPanel2': {    			
    			itemclick: this.onGridItemClick
    		},
    		'MeetingDetail':{
    			itemclick: this.onGridItemClick2
    		},
    		'erpTurnTurnDocButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				var isturndoc=Ext.getCmp('ma_isturndoc');
    				if(status && status.value != 'AUDITED'||isturndoc.value!='否'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				var ma_id=Ext.getCmp('ma_id').value;
    				Ext.Ajax.request({
    			   		url : basePath + 'oa/meeting/turnDoc.action',
    			   		params : {
    			   			caller:caller,
    			   			ma_id:ma_id
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){	   			
    			   			var localJson = new Ext.decode(response.responseText);
    		    			if(localJson.success){
    		    				showMessage("提示", localJson.log);
    		    				window.location.reload();
    			   			} else if(localJson.exceptionInfo){
    			   				var str = localJson.exceptionInfo;
    			   				showError(str);
    			   			}
    			   		}
    			   		
    				});
    			}
    		},
    		'erpCancelButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					this.cancel(Ext.getCmp('ma_id').value);
				}
    		},
    		'erpConfirmManButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(){
    				var nCaller='Meeting!Attend';
    				var condition='md_meid='+Ext.getCmp('ma_id').value;
    				var url='oa/meeting/confirmMan.action';
    		    	var win = new Ext.window.Window({
    			    	id : 'win',
       				    height: "100%",
       				    width: "80%",
       				    maximizable : true,
       					buttonAlign : 'center',
       					layout : 'anchor',
       				    items: [{
       				    	  tag : 'iframe',
       				    	  frame : true,
       				    	  anchor : '100% 100%',
       				    	  layout : 'fit',
       				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
       				    	  	+ "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
       				    }],
       				    buttons : [{
       				    	name: 'confirm',
       				    	text : $I18N.common.button.erpConfirmButton,
       				    	iconCls: 'x-button-icon-confirm',
       				    	cls: 'x-btn-gray',
       				    	listeners: {
       				    		buffer: 500,
       				    		click: function(btn) {
       				    			var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
       				    			btn.setDisabled(true);
       				    			grid.updateAction(url);
       				    		}
       				    	}
       				    }, {
       				    	text : $I18N.common.button.erpCloseButton,
       				    	iconCls: 'x-button-icon-close',
       				    	cls: 'x-btn-gray',
       				    	handler : function(){
       				    		Ext.getCmp('win').close();
       				    		window.location.reload();
       				    	}
       				    }]
       				});
       				win.show();
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var start=new Date(Ext.getCmp('ma_starttime').items.items[5].value);
    				var end=new Date(Ext.getCmp('ma_endtime').value);
    				var myDate = new Date();
    				if(start-end>0){
    					showError('开会时间输入有误，请检查后重新输入');
    				}else{
    					if(myDate-start>0){
    						showError('当前时间已超过申请开会时间，请确认后重新输入');
    					}else{
    						me.FormUtil.onSubmit(Ext.getCmp('ma_id').value);
    					}
    				}
				}
			},
			'SchedulerTrigger':{
				afterrender:function(trigger){
					trigger.dbCaller='Meeting';
					trigger.setFields=[{field:'ma_mrcode',mappingfield:'ID'},{field:'ma_mrname',mappingfield:'MR_NAME'},{field:'mr_site',mappingfield:'MR_SITE'}];
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ma_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var start=new Date(Ext.getCmp('ma_starttime').items.items[5].value);
    				var myDate = new Date();
    					if(myDate-start>0){ 						
    						showError('当前时间已超过申请开会时间，无法审核');
    					}else{
    						me.FormUtil.onAudit(Ext.getCmp('ma_id').value);
    					}
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
				}
			},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				var start=new Date(Ext.getCmp('ma_starttime').items.items[5].value);
    				var end=new Date(Ext.getCmp('ma_endtime').value);
    				var myDate = new Date();
    				if(start-end>0){
    					showError('开会时间输入有误，请检查后重新输入');
    				}else{
    					if(myDate-start>0){
    						showError('当前时间已超过申请开会时间，请确认后重新输入');
    					}else{
    						me.FormUtil.beforeSave(this);
    					}
    				
    				}
    			}
    		},
    		'erpReLoadButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					if(Ext.getCmp('ma_groupid').value==""){
						showError('请先选择与会人员');
					}else{
						var or=Ext.getCmp('ma_groupid').originalValue.toString();
						var val=Ext.getCmp('ma_groupid').value;
						if(or!=val){
							showError('请与会人员已更改，请先更新');
						}else{
							this.reload(Ext.getCmp('ma_id').value);
						}			
					};
				}
    		},
    		'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ma_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var start=new Date(Ext.getCmp('ma_starttime').items.items[5].value);
    				var end=new Date(Ext.getCmp('ma_endtime').value);
    				var myDate = new Date();
    				if(start-end>0){
    					showError('开会时间输入有误，请检查后重新输入');
    				}else{
    					if(myDate-start>0){
    						showError('当前时间已超过申请开会时间，请确认后重新输入');
    					}else{
    						me.FormUtil.onUpdate(this);
    					}
    				}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMeetingroomapply', '新增会议申请', 'jsps/oa/meeting/meetingroomapply.jsp');
				}
			},
			'erpPrintButton': {
                click: function(btn) {
                	 var id = Ext.getCmp('ma_id').value;
                     me.FormUtil.onwindowsPrint2(id, "", "");
                }
            },
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	Ext.getCmp('grid').setReadOnly(true);
		this.GridUtil.onGridItemClick(selModel, record);
	},
	onGridItemClick2: function(selModel,record){
    	this.onGridItemClick(selModel,record,'MeetingDetail');
    	
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
		var detail2 = Ext.getCmp('MeetingDetail');
		Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
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
			/*	r.vo_currencytype = Ext.getCmp('vo_currencytype').value ? -1 : 0;
				r.vo_errstring = r.vo_errstring == '正常' ? '' : r.vo_errstring;*/
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
		/*for(var i=2; i<arguments.length; i++) {  //兼容多参数
			params['param' + i] = unescape(arguments[i].toString().replace(/\\/g,"%"));
		}*/  
		var me = this;
		var form = Ext.getCmp('form');
		//me.getActiveTab().setLoading(true);//loading...
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
			   					formCondition+'&gridCondition=mad_maidIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=mad_maidIS'+value;
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
				   		    	formCondition+'&gridCondition=mad_maidIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=mad_maidIS'+value;
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
		var detail2 = Ext.getCmp('MeetingDetail');
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = new Array();
		if(detail2) {
			param2 = me.GridUtil.getGridStore(detail2);
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
				/*r.vo_currencytype = Ext.getCmp('vo_currencytype').value ? -1 : 0;
				r.vo_errstring = r.vo_errstring == '正常' ? '' : r.vo_errstring;*/
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
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	reload:function(id){
		var me = this;
		warnMsg("确定要重新载入吗?", function(btn){
			if(btn == 'yes'){
				me.FormUtil.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + 'oa/meeting/reLoadMeetingroomapply.action',
			   		params: {
			   			id: id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			me.FormUtil.getActiveTab().setLoading(false);
			   			var r = new Ext.decode(response.responseText);
			   			if(r.exceptionInfo){
			   				showError(r.exceptionInfo);
			   			}else{
			   				showMessage("提示", '重新载入成功');
		    				window.location.reload();
			   			}
			   		}
				});
			}
		});
	},
	cancel:function(id){
		var me=this;
		warnMsg("确定要取消会议室申请吗?", function(btn){
			if(btn == 'yes'){
				me.FormUtil.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + 'oa/meeting/cancelMeetingroomapply.action',
			   		params: {
			   			id: id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			me.FormUtil.getActiveTab().setLoading(false);
			   			var r = new Ext.decode(response.responseText);
			   			if(r.exceptionInfo){
			   				showError(r.exceptionInfo);
			   			}else{
			   				showMessage("提示", '取消成功');
		    				window.location.reload();
			   			}
			   		}
				});
			}
		});
	}
});