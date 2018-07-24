Ext.require(['erp.view.core.form.Panel']);
Ext.define('erp.view.plm.task.ProdTaskForm',{
	extend: 'erp.view.core.form.Panel', 
	alias: 'widget.prodForm',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	statusfield:'',
	statuscodefield:'',						
	defaultItems:[],
	keyValue:0,
	items:new Array(),
	buttonAlign : 'center',
	bodyStyle : 'background:#f9f9f9;padding:5px 5px 0',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	layout:'column',
	enableTools:true,
	defaults:{
		xtype:'textfield',
		columnWidth:0.25,
		margin:'5 5 5 5'
	},
	caller:null,
	initComponent : function(){ 
		var me=this;
		var params=this.params ||{
			caller:this.caller||caller,
			condition:this.condition
		};
		//this.getFormItems(this,"common/singleFormItems.action",params);
		this.callParent(arguments);
		if(this.enableTools) {
			this.setTools();
		}
	},
	getFormItems: function(form, url, param){
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({//拿到form的items
			url : basePath + url,
			params: param,
			method : 'post',
			callback : function(options, success, response){
				me.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				form.fo_id = res.fo_id;
				form.fo_keyField = res.keyField;
				form.tablename = res.tablename;//表名
				if(res.keyField){//主键
					form.keyField = res.keyField;
				}
				if(res.statusField){//状态
					form.statusField = res.statusField;
				}
				if(res.statuscodeField){//状态码
					form.statuscodeField = res.statuscodeField;
				}
				if(res.codeField){//Code
					form.codeField = res.codeField;
				}
				if(res.dealUrl){
					form.dealUrl = res.dealUrl;
				}
				form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
				var grids = Ext.ComponentQuery.query('gridpanel');
				var items = me.setItems(form, res.items, res.data, res.limits, {
				});
				form.add(items);
				form.fireEvent('afterload', form);
			}
		});
	},
	setItems: function(form, items, data, limits, necessaryCss){
		var edit = true,hasData = true,limitArr = new Array();
		if(limits != null && limits.length > 0) {//权限外字段
			limitArr = Ext.Array.pluck(limits, 'lf_field');
		}
		if (data) {
			data = Ext.decode(data);
		} else {
			hasData = false;
		}
		var bool = 'a';
		if(items.length > 110&&items.length <=190){
			bool = 'b';
		}else if(items.length>190){
			bool = 'c';
		}
		Ext.each(items, function(item){
			if(item.columnWidth<0.5)item.columnWidth = 0.5;
			item.cls="";
			item.fieldStyle="background:white repeat-x 0 0;border-width: 1px;border-style: solid;";
			//item.fieldStyle="background: white repeat-x 0 0;border-color: silver #d9d9d9 #d9d9d9;";
			if(item.name != null) {
				if(item.name == form.statusField){//状态加特殊颜色
					item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
				} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
					item.xtype = 'hidden';
				}
			}
			if(form.codeField && item.name==form.codeField) item.allowBlank=false;
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
				item.margin = '0';
			}else if(item.xtype == 'checkbox') {
				item.focusCls = '';
				item.fieldStyle = '';
				item.uncheckedValue=0;
			}else if(item.xtype =='erpYnField'){
				item.xtype='checkbox';
				item.boxLabel=item.fieldLabel;	
				item.hideLabel=true;
				item.fieldStyle = '';
				item.checked = Math.abs(item.value || 0) == 1;
			}else if(item.xtype=='textareatrigger') item.xtype='textareafield'; 
			if(item.maskRe!=null){
				item.maskRe=new RegExp(item.regex);
			}

			if(form.defaultValues){
				Ext.Array.each(form.defaultValues,function(v){
					if(item.name in v) item.value=v[item.name];
				});
			}
			if (hasData) {
				item.value = data[item.name];
				if(item.xtype == 'datefield' && !item.format && item.value) {
					item.value  = item.value.substring(0,10);
				}
				if(item.secondname){//针对合并型的字段MultiField
					item.secondvalue = data[item.secondname];
				}
				if(!edit){
					form.readOnly = true;
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				} 
				if(item.xtype == 'checkbox'){
					item.checked = Math.abs(item.value || 0) == 1;
					item.fieldStyle = '';
					item.uncheckedValue=0;
				}
			} 

		});
		items.push({
			xtype:'fieldset',
			title: '表单介绍',
			columnWidth: 1,
			collapsible: true,
			collapsed: true,
			html:'表单介绍'
		})
		return items;
	},
	//删除前把已审核改为在录入
	beforeDelete:function(form,id){
		var keyValue =form.down('field[name='+form.keyField.toLocaleLowerCase() +']').value;
		Ext.Ajax.request({
			url : basePath + 'ma/sysinit/beforeDelete.action',
			method : 'POST',
				params:{
					status : form.statusfield,
					statuscode : form.statuscodefield,
					table : form.tablename,
					keyValue : keyValue,
					keyField:form.keyField
				}
		});
	},
	deleteDetail:function(form,id){
		var me=this;
		me.beforeDelete(form);
		form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + form.deleteUrl,
			params: {
				id: id,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showResult('提示',localJson.exceptionInfo);return;
				}
				if(localJson.success){
					showResult('提示','删除成功!');
					var grid=form.ownerCt.down('simpleactiongrid');
					grid.getData(true);
					form.removeAll(true);
					var items=form.setItems(form, form.defaultItems,null);
					form.add(items);
					form.down("button[name='deleteButton']").setDisabled(true);
				} else {
					delFailure();
				}
			}
		});
	},
	setItems: function(form, items, data){
	},
	setItems: function(form, items, data){
		var edit = true,hasData = true,limitArr = new Array();
		hasData = false;
		var bool = 'a';
		if(items.length > 110&&items.length <=190){
			bool = 'b';
		}else if(items.length>190){
			bool = 'c';
		}
		Ext.each(items, function(item){
			if(typeof(item.allowBlank) == "undefined"){
				item.allowBlank=true;
			}
			item.cls="";
			item.fieldStyle="background:white repeat-x 0 0;border-width: 1px;border-style: solid;";
			item.fieldStyle="background: white repeat-x 0 0;border-color: silver #d9d9d9 #d9d9d9;";
			if(item.xtype =='container'){
				item.html='<div onclick="javascript:void(0);" class="x-form-group-label" id="group1" style="background-color: #bfbfbe;height:22px!important;" title="收拢"><h6>'+item.fieldLabel+'</h6></div>';
				item.cls= 'x-form-group';
			}
			if(item.xtype =='combo'){
				item.queryMode="local";
				item.displayField= "display";
				item.valueField="value";
			}
			if(item.xtype =='numberfield'){
				item.hideTrigger= true;
			}
			if(item.xtype == 'checkbox') {
				item.focusCls = '';
			}
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
				item.margin = '0';
			}else if(item.xtype == 'checkbox') {
				item.focusCls = '';
				item.fieldStyle = '';
				item.uncheckedValue=0;
			}else if(item.xtype =='erpYnField'){
				item.xtype='checkbox';
				item.boxLabel=item.fieldLabel;	
				item.hideLabel=true;
				item.fieldStyle = '';
				item.checked = Math.abs(item.value || 0) == 1;
			}else if(item.xtype=='textareatrigger') item.xtype='textareafield'; 
			if(item.maskRe!=null){
				item.maskRe=new RegExp(item.regex);
			}

			if(form.defaultValues){
				Ext.Array.each(form.defaultValues,function(v){
					if(item.name in v) item.value=v[item.name];
				});
			}
			if (hasData) {
				item.value = data[item.name];
				if(item.xtype == 'datefield' && !item.format && item.value) {
					item.value  = item.value.substring(0,10);
				}
				if(item.secondname){//针对合并型的字段MultiField
					item.secondvalue = data[item.secondname];
				}
				if(!edit){
					form.readOnly = true;
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				} 
				if(item.xtype == 'checkbox'){
					item.checked = Math.abs(item.value || 0) == 1;
					item.fieldStyle = '';
					item.uncheckedValue=0;
				}
			} 

		});
		return items;
	},
	onSave: function(form,btn){
		var me = this,url,warnMsg='保存成功!',kF=null;
		if(!me.checkData()){
			return;
		}
		if(form.codeField){
			code=form.down('textfield[name='+form.codeField+']');
			if(code.value == null || code.value  == ''){				
				if(form){
					table =form.tablename;
					caller=form.caller;
					type = 2;
					codeField = form.codeField;
					Ext.Ajax.request({
				   		url : basePath + 'common/getCodeString.action',
				   		async: false,//同步ajax请求
				   		params: {
				   			caller: caller,//如果table==null，则根据caller去form表取对应table
				   			table: table,
				   			type: type
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				showError(localJson.exceptionInfo);
				   			}
			    			if(localJson.success){
			    				code.setValue(localJson.code);
				   			}
				   		}
					});
				}
			}
		}
		if(form.keyField){
			kF=form.down('field[name='+form.keyField.toLocaleLowerCase() +']');
			if(kF.value == null || kF.value == ''||kF.value!=kF.originalValue){
				me.getSeqId(form);
				url = form.saveUrl;
				form.saveType='ADD';
			}else {
				url = form.updateUrl;
				warnMsg='修改成功!';
				form.saveType='UPDATE';
				keyValue=kF.value;
			}
		}
		Ext.each(form.items.items, function(item){
			if(item.xtype == 'numberfield'){
				//number类型赋默认值，不然sql无法执行
				if(item.value == null || item.value == ''){
					item.setValue(0);
				}
			}else if(item.xtype=='checkbox'){
				item.dirty=true;
				if(item.checked){
					item.inputValue='-1';
					item.submitValue='-1';
					item.setValue('-1');
					item.rawValue='-1';
				}else {
					item.inputValue='0';
					item.submitValue='0';
					item.setValue('0');
					item.rawValue='0';
				}
			}
		});
		var r = form.getValues();
		var keys = Ext.Object.getKeys(r), f;
		var reg = /[!@#$%^&*()'":,\/?]/;
		Ext.each(keys, function(k){
			f = form.down('#' + k);
			if(f && f.logic == 'ignore') {
				delete r[k];
			}
			//codeField值强制大写,自动过滤特殊字符
			if(k == form.codeField && !Ext.isEmpty(r[k])) {
				r[k] = r[k].trim().toUpperCase().replace(reg, '');
			}
		});
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		var rr=new Object();
		Ext.each(Ext.Object.getKeys(r),function(key){
			rr[key.toLocaleLowerCase()]=r[key];
		});
		var params=new Object();
		params.formStore= unescape(escape(Ext.JSON.encode(rr)));
		params._noc=1;
		if(form.emptyGrid)params.gridStore="[]";
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + form.caller;
		}
		form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showResult('提示',warnMsg);
					var grid=btn.ownerCt.ownerCt.ownerCt.down('simpleactiongrid');					
					var sm=grid.getSelectionModel();
					var record=sm.getSelection()[0];
					params={
						caller:me.tablename,
						fields:me.fields,
						condition:''+form.keyField+'='+keyValue
					};
					form.loadNewStore(form,params);
					grid.getData(true);
				} else if(localJson.exceptionInfo){
					showResult('提示',localJson.exceptionInfo);
				} else{
					showResult('提示',"操作失败!");
				}
			}

		});
	},
	getSeqId: function(form){
		if(!form){
			form = Ext.getCmp('form');
		}
		Ext.Ajax.request({
			url : basePath + form.getIdUrl,
			method : 'get',
			async: false,
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					form.down('field[name='+form.keyField.toLocaleLowerCase() +']').setValue(rs.id);
					this.keyValue=rs.id;
				}
			}
		});
	},
	checkData:function(){
		return true;
	},
	setKeyFieldValue:function(keyField,value){
		var form=this.getForm();
		form.down('field[name='+keyField.toLocaleLowerCase() +']').setValue(value);
	},
	loadNewStore: function(form, param){
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
			url : basePath + "common/getFieldsData.action",
			params: param,
			method : 'post',
			callback : function(options,success,response){
				me.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				if(res.data){
					var d = res.data;
					form.getForm().setValues(res.data);
					var chs = form.query('checkbox');
					form.down('field[name='+form.keyField.toLocaleLowerCase() +']').setValue(d[form.keyField]);
					Ext.each(chs, function(){
						this.setValue(Math.abs(d[this.name] || 0) == 1);
					});
					form.getForm().getFields().each(function (item,index,length){
						item.originalValue = item.value;
					});
					form.down("button[name='deleteButton']").setDisabled(false);
				}
			}
		});
	}
	});