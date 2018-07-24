/**
 * 适用于4.2版本的form
 * */
var required = '<span style="color:red;font-weight:bold" data-qtip="必填字段">*</span>';
Ext.define('erp.view.core.form.Panel2',{
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpFormPanel2',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	id:'form',
	//bodyStyle : 'background:#f9f9f9;padding:5px 5px 0',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText
	},
	border:false,
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	layout:'column',
	/*frame:true,*/
	defaultValues:null,
	defaults:{
		xtype:'textfield',
		columnWidth:0.33,
		margin:'5 5 5 5'
	},
	caller:null,
	/*buttons: [{
		xtype:'tbtext',
		text:'<div style="color:gray;">带*输入框为必填项</div>'
	},'->',{
		text: '保存',
		formBind: true,
		disabled: true,
		handler: function(btn) {
			var form = this.up('form');
			if (form.isValid()) {
				form.onSave(form,btn);
			}
		}
	},{
		text: '清空',
		handler: function() {
			this.up('form').getForm().reset();
		}
	}],*/
	initComponent : function(){
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		caller=caller==null?getUrlParam('whoami'):caller;
		var params=this.params ||{
			caller:this.caller||caller,
			condition:formCondition
		};
		this.getFormItems(this,"common/singleFormItems.action",params);
		this.callParent(arguments);
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
				if(res.title && res.title != ''){
					form.setTitle(res.title);
					var _tt = res.title;
					if(form.codeField) {
						var _c = form.down('#' + form.codeField);
						if( _c && !Ext.isEmpty(_c.value) )
							_tt += '(' + _c.value + ')';
					}
				}
				me.createToolbar(res.buttons);
				form.fireEvent('afterload', form);
			}
		});
	},
	setItems: function(form, items, data, limits, necessaryCss){
		var me=this,edit = true,hasData = true,limitArr = new Array();
		if(limits != null && limits.length > 0) {
			limitArr = Ext.Array.pluck(limits, 'lf_field');
		}
		if (data) {
			data = Ext.decode(data);
			if(form.statuscodeField && data[form.statuscodeField] != null && data[form.statuscodeField] != '' &&  
					['ENTERING', 'UNAUDIT', 'UNPOST', 'CANUSE'].indexOf(data[form.statuscodeField]) == -1){//非在录入和已提交均设置为只读// && data[form.statuscodeField] != 'COMMITED'
				form.readOnly = true;
				edit = false;
			}
			if(form.statusCode && data[form.statusCode] == 'POSTED'){//存在单据状态  并且单据状态不等于空 并且 单据状态等于已过账
				form.readOnly = true;
				edit = false;
			}
		} else {
			hasData = false;
		}
		Ext.each(items, function(item){
			item.cls="";
			item.fieldStyle="background:white repeat-x 0 0;border-width: 1px;border-style: solid;";
			if(item.group==0 && !item.groupName && item.html){
				item.html=me.setGroupLabelHtml(item.html);		
			}
			if(item.name != null) {
				if(item.name == form.statusField){//状态加特殊颜色
					item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
				} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
					item.xtype = 'hidden';
				}
			}
			//if(form.codeField && item.name==form.codeField) item.allowBlank=false;
			if(!item.allowBlank && item.fieldLabel) {
				item.afterLabelTextTpl=required;
			}
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
				item.margin = '0';
			}else if(item.xtype == 'checkbox') {
				item.focusCls = '';
				item.fieldStyle = '';
				item.uncheckedValue=0;
			}else if(item.xtype =='datetimefield'){
				item.xtype='datefield';
				item.format='Y-m-d H:i:s';
			}else if(item.xtype =='erpYnField'){
				item.xtype='checkbox';
				item.boxLabel=item.fieldLabel;	
				item.hideLabel=true;
				item.afterBoxLabelTextTpl=required;
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
	createToolbar:function(buttonString){
		var form=this;
		if(buttonString != null && buttonString.trim() != ''){
			var buttons = new Array();
			buttons.push('->');
			Ext.each(buttonString.split('#'), function(btn, index){
				var o = {};
				o.xtype = btn;
				o.height = 26;
				buttons.push(o);
				if((index + 1)%12 == 0){
					buttons.push('->');
					form.addDocked({
						xtype: 'toolbar',
						style:'background-color:red!important',
						dock: 'bottom',
						defaults: {
							style: {
								marginLeft: '10px'
							}
						},
						items: buttons
					});
					buttons = new Array();
					buttons.push('->');
				}
			});
			buttons.push('->');
			form.addDocked({
				xtype: 'toolbar',
				dock: 'bottom',
				/*ui:'footer',*/
				defaults: {
					style: {
						marginLeft: '10px'
					}
				},
				items: buttons
			});
		}
	},
	onSave: function(form,btn){
		var me = this,url,warnMsg='保存成功!',kF=null;
		if(!me.checkData()){
			return;
		}
		if(form.keyField){
			kF=Ext.getCmp(form.keyField);
			if(kF.value == null || kF.value == '' || kF.value!=kF.originalValue){
				me.getSeqId(form,me.setKeyFieldValue);
				url = form.saveUrl;
				form.saveType='ADD';
			}else {
				url = form.updateUrl;
				warnMsg='修改成功!';
				form.saveType='UPDATE';
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
				}else {
					item.inputValue='0';
					item.submitValue='0';
					item.value='0';
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
		var params=new Object();
		params.formStore= unescape(escape(Ext.JSON.encode(r)));
		if(form.emptyGrid)params.gridStore="[]";
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + form.caller;
		}
		form.setLoading(true);
		if(form.saveType='UPDATE' && form.keyField && kF.value)  me.saveBefore(form.caller,kF.value);
		Ext.Ajax.request({
			url : basePath + url,
			params : params,
			method : 'post',
			callback : function(options,success,response){
				form.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showResult('提示',warnMsg);
					me.saveAfter(form.caller,kF.value);
					form.saveSuccess(form.currentTab,btn);
				} else if(localJson.exceptionInfo){
					showResult('提示',localJson.exceptionInfo);
				} else{
					showResult('提示',"操作失败!");
				}
			}

		});
	},
	getSeqId: function(form,fn){
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
					fn.call(null, form.keyField,rs.id);
				}
			}
		});
	},
	checkData:function(){
		return true;
	},
	setGroupLabelHtml:function(html){
		return html.replace('<h6>','<span style="font-weight:bold;">').replace('</h6>','</span>');
	},
	setKeyFieldValue:function(keyField,value){
		Ext.getCmp(keyField).setValue(value);
	},
	saveBefore:function(caller,keyValue){
		Ext.Ajax.request({
			url : basePath + 'ma/sysinit/saveBefore.action',
			method : 'POST',
			async: false,
			params:{
				caller:caller,
				keyValue:keyValue
			}
		});
	},
	saveAfter:function(caller,keyValue){
		Ext.Ajax.request({
			url : basePath + 'ma/sysinit/saveAfter.action',
			method : 'POST',
			params:{
				caller:caller,
				keyValue:keyValue
			}
		});
	}

});