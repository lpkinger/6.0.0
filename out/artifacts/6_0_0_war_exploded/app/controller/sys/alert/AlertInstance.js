Ext.QuickTips.init();
Ext.define('erp.controller.sys.alert.AlertInstance', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['sys.alert.AlertInsViewport', 'sys.alert.ParamForm', 'sys.alert.AssignGrid', 'sys.alert.AssignSelectWindow',
    	'sys.alert.AssignSelectWindow2', 'sys.alert.AssignSqlWindow2',
    	'sys.alert.AssignSqlWindow', 'sys.alert.ConditionSqlWindow', 'core.form.HrOrgSelectField','core.form.Panel',
    	'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.form.MultiField', 'common.datalistFilter.ConContainer',
    	'core.button.Save', 'core.button.Add', 'core.button.Submit', 'core.button.ResAudit', 'core.button.Audit',
    	'core.button.Close', 'core.button.Delete', 'core.button.Update', 'core.button.ResSubmit', 'core.button.Banned',
    	'core.button.ResBanned', 'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger', 'core.trigger.FrequencyTrigger', 
    	'core.picker.HighlightableDatePicker', 'core.form.MultiHourField', 'core.form.YnField', 'core.grid.YnColumn',
    	'core.form.StatusField', 'core.form.FileField', 'core.window.HrOrgSelectWindow'],
    init: function(){ 
    	var me = this;
    	this.control({
    		'field[name=aii_statuscode]': {
    			afterrender: function(field) {
    				var status = field.value;
    			}
    		},
    		'field[name=aii_itemid]': {
    			change: function(field) {
    				var baseForm = me.getBaseForm();
    				var paramForm = me.getParamForm();
    				var assignGrid = me.getAssignGrid();
    				store = assignGrid.getStore();
    				for(var i=0;i<store.getCount();i++) {
						rowStore = store.getAt(i);
						rowStore.set('aia_condition', '');
						rowStore.set('aia_conditionconfig', '');
						rowStore.set('aia_mansql', '');
    				}
    				paramForm.getFormItems(field.value);
    			}
    		},
    		'#paramForm': {
    			afterrender: function(grid) {
    				var paramForm = me.getParamForm();
    				paramForm.getFormItems(null, aii_id);
    			}
    		},
    		'#grid': {
    			itemclick: function(selModel, record, item, index, e, eOpts ){
    				me.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'#conditionSqlForm': {
    			beforerender:function(p){
    				var win = Ext.getCmp('conditionSqlWin'),
    					itemId = Ext.getCmp('aii_itemid').value;
					me.request('sys/alert/getOutputParams.action', {
						itemId: itemId
					}, function(res) {
			   			var localJson = new Ext.decode(res.responseText);
						if(localJson.success){
							var data = [];
							Ext.Array.each(localJson.data, function(d, i) {
								data.push({
									field: d['ao_resultname'],
									text: d['ao_resultdesc'],
									originalxtype: d['ao_resulttype'] || 'textfield'
								});
							});
							var FieldStore = new Ext.data.Store({
								fields: ['field', 'text', 'originalxtype'],
								data: data
							});
							p.FieldStore = FieldStore;
							win.insertCondition(win.initConfig);
						} else {
							delFailure();
						}
					});
				}
    		},
    		'erpAddButton': {
    			click: function(btn) {
    				me.FormUtil.onAdd('addAlertInstance', '新增预警项次', 'jsps/sys/alert/alertInstance.jsp');
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn) {
    				me.beforeSave();
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn) {
    				me.beforeUpdate();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn) {
    				var baseForm = me.getBaseForm();
    				me.FormUtil.onDelete((Ext.getCmp('aii_id').value));
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('aii_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn) {
    				var paramForm = me.getParamForm();
    				if(paramForm.getForm().isDirty()) {
    					var s1 = me.FormUtil.checkFormDirty();
    					var s2 = me.FormUtil.checkFormDirty(me.getParamForm());
    					var s3 = me.GridUtil.checkGridDirty(me.getAssignGrid());
    					Ext.MessageBox.show({
							title:'保存修改?',
							msg: '该单据已被修改:<br/>' + s1 + '<br/>'+s2+'<br/>'+s3 + '<br/>提交前要先保存吗？',
							buttons: Ext.Msg.YESNOCANCEL,
							icon: Ext.Msg.WARNING,
							fn: function(btn){
								if(btn == 'yes'){
									me.beforeUpdate();
								} else if(btn == 'no'){
									me.FormUtil.onSubmit(Ext.getCmp('aii_id').value, true, me.beforeUpdate, me);
								} else {
									return;
								}
							}
						});
    				}else {
	    				me.FormUtil.onSubmit(Ext.getCmp('aii_id').value, true, me.beforeUpdate, me);
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('aii_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
				},
    			click: function(btn) {
    				me.FormUtil.onResSubmit(Ext.getCmp('aii_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('aii_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn) {
    				var baseForm = me.getBaseForm();
    				me.FormUtil.onAudit(Ext.getCmp('aii_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('aii_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn) {
    				me.FormUtil.onResAudit(Ext.getCmp('aii_id').value);
    			}
    		},
    		'erpBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('aii_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}else{
	    				var enable = Ext.getCmp('aii_enable');
	    				if(enable && enable.value != '-1'){
	    					btn.hide();
	    				}
    				}
    			},
    			click: function(btn) {
    				me.banned();
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('aii_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}else{
	    				var enable = Ext.getCmp('aii_enable');
	    				if(enable && enable.value != '0'){
	    					btn.hide();
	    				}
    				}
    			},
    			click: function(btn) {
    				me.resBanned();
    			}
    		}
    	});
    },
    getBaseForm: function(){
		return Ext.getCmp('form');
   	},
   	getParamForm: function() {
   		return Ext.getCmp('paramForm');
   	},
   	getAssignGrid: function() {
   		return Ext.getCmp('grid');
   	},
   	parseFormStore: function(r) {
		return unescape(escape(Ext.JSON.encode(r)));
   	},
   	beforeSave: function(me, ignoreWarn, opts, extra){
		var me = this;
		var baseForm = me.getBaseForm(),
			paramForm = me.getParamForm();
			
		if(Ext.getCmp(baseForm.keyField).value == null || Ext.getCmp(baseForm.keyField).value == ''){								
		   	me.FormUtil.getSeqId(baseForm);
		}
		
		var baseFormStore = me.parseFormStore(baseForm.getBaseValues());
		var paramFormStore = me.parseFormStore(paramForm.getForm().getValues());
		var assignGridRecord = [];
		var store = me.GridUtil.getGridStore(me.getAssignGrid());
		assignGridRecord[0] = store;
		
		if(baseForm.getForm().isValid()){
			me.save(baseFormStore, paramFormStore, assignGridRecord);
		}else{
			me.FormUtil.checkForm();
		}
	},
	save: function(baseFormStore, paramFormStore, assignGridRecord){
		var me = this;
		var baseForm = me.getBaseForm();
		me.request(baseForm.saveUrl, {baseFormStore: baseFormStore, paramFormStore: paramFormStore, assignGridRecord: assignGridRecord}, function(res){
			var localJson = new Ext.decode(res.responseText);
   			if(localJson.success){
   				showMessage('提示', '保存成功!', 1000);
   				me.refresh();
   			} else if(localJson.exceptionInfo){
   				var str = localJson.exceptionInfo;
   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
   					str = str.replace('AFTERSUCCESS', '');
   					showMessage('提示', '更新成功!', 1000);
   		    		me.refresh();
   					showError(str);
   				} else {
   					showError(str);
	   				return;
   				}
   			} else{
   				saveFailure();//@i18n/i18n.js
   			}
		});
	},
	beforeUpdate: function(me, ignoreWarn, opts, extra){
		var me = this;
    	var baseForm = me.getBaseForm();
		var paramForm = me.getParamForm();
		var baseFormStore = me.parseFormStore(baseForm.getBaseValues());
		var paramFormStore = me.parseFormStore(paramForm.getForm().getValues());
		var assignGridRecord = [];
		var store = me.GridUtil.getGridStore(me.getAssignGrid());
		assignGridRecord[0] = store;
		
		if(baseForm.getForm().isValid()){
			me.update(baseFormStore, paramFormStore, assignGridRecord);
		}else{
			me.FormUtil.checkForm();
		}
	},
	update:function(baseFormStore,paramFormStore,assignGridRecord){
		var me = this;
		var baseForm = me.getBaseForm();
		me.request(baseForm.updateUrl, {baseFormStore: baseFormStore, paramFormStore: paramFormStore, assignGridRecord: assignGridRecord}, function(res){
			var localJson = new Ext.decode(res.responseText);
			if(localJson.success){
				showMessage('提示', '更新成功!', 1000);
				me.refresh();
			} else if(localJson.exceptionInfo){
				var str = localJson.exceptionInfo;
				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
					str = str.replace('AFTERSUCCESS', '');
					showMessage('提示', '更新成功!', 1000);
		    		me.refresh();
					showError(str);
				} else {
					showError(str);
	   				return;
				}
			} else{
				saveFailure();//@i18n/i18n.js
			}
		});
	},
	banned: function() {
		var me = this;
		var form = Ext.getCmp('form');
		var value = Ext.getCmp('aii_id').value;
		me.request(form.bannedUrl, {caller: caller, id: value}, function(res) {
			var localJson = new Ext.decode(res.responseText);
		   		if(localJson.success){
		   			showMessage('提示', '禁用成功!', 1000);
	   		    	window.location.reload();
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					showMessage('提示', '禁用成功!', 1000);
	   					window.location.reload();
	   					str = str.replace('AFTERSUCCESS', '');
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
					saveFailure();//@i18n/i18n.js
				}
		});
	},
	resBanned: function() {
		var me = this;
		var form = Ext.getCmp('form');
		var value = Ext.getCmp('aii_id').value;
		me.request(form.resBannedUrl, {caller: caller, id: value}, function(res) {
			var localJson = new Ext.decode(res.responseText);
		   		if(localJson.success){
		   			showMessage('提示', '启用成功!', 1000);
	   		    	window.location.reload();
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					showMessage('提示', '启用成功!', 1000);
	   					window.location.reload();
	   					str = str.replace('AFTERSUCCESS', '');
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
					saveFailure();//@i18n/i18n.js
				}
		});
	},
   	request: function(url, param, callback) {
   		var me = this;
   		param.caller = caller,
   		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params : param,
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				callback(response);
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
	    	if(string.charAt(j)==startChar){
	    		if(string.substring(j,j+strLen)==substr){
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	},
   	refresh: function() {
   		var me = this;
   		var baseForm = me.getBaseForm();
   		aii_id = baseForm.getForm().getValues()[baseForm.keyField];
    	var formCondition = baseForm.keyField + "IS" + aii_id;
    	if(me.contains(window.location.href, '?', true)){
	    	window.location.reload();
	    } else {
	    	window.location.href = window.location.href + '?formCondition=' + formCondition+'&gridCondition=AIA_AIIIDIS'+aii_id;
	    }
   	}
});
