Ext.define('erp.view.sys.base.FormPortal',{
	extend: 'Ext.form.Panel', 
	alias: 'widget.formportal',
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
	defaultValues:null,
	saveType:'ADD',
	defaults:{
		xtype:'textfield',
		columnWidth:0.33,
		margin:'5 5 5 5'
	},
	caller:null,
	buttons: [{
				text:'新建',
				iconCls:'btn-add',
				handler: function() {
					this.up('form').removeAll(true);
					var me=this.ownerCt.ownerCt;
					var items=me.setItems(me, me.defaultItems,null);
					me.add(items);
				}
		}, {
			text: '保存',
			iconCls:'btn-save',
			formBind: true,
			disabled: true,
			handler: function(btn) {
				var form = this.up('form');
				if (form.isValid()) {
					form.onSave(form,btn);
				}
			}
		},{
			text: '删除',
			name:'deleteButton',
			iconCls:'btn-delete',
			disabled:true,
			handler: function(btn) {
				var form=btn.ownerCt.ownerCt;
				Ext.Msg.confirm('删除数据?', '确定要删除当前选中的 ?',
					function(choice) {
						if(choice === 'yes') {
							var values=form.getForm().getValues();
							form.deleteDetail(form,values[form.keyField.toLocaleLowerCase()]);
						}
					}
				);   
			}
		},'->',{
			xtype:'tbtext',
			text:'<div style="color:gray;">带'+required+'输入框为必填项</div>'
	}],
	initComponent : function(){ 
		var me=this;
		var items=me.setItems(me, me.defaultItems,null);
		me.items=items;
		this.callParent(arguments);
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
	/*setItems: function(form, items, data){
	},*/
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
			if(item.columnWidth<0.5)item.columnWidth = 0.33;
			item.cls="";
			item.fieldStyle="background:white repeat-x 0 0;border-width: 1px;border-style: solid;";
			item.fieldStyle="background: white repeat-x 0 0;border-color: silver #d9d9d9 #d9d9d9;";
			if(!item.allowBlank && item.fieldLabel) {
				item.afterLabelTextTpl=required;
			}
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
				if(!item.allowBlank && item.fieldLabel) {
					item.afterBoxLabelTextTpl=required;
				}
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