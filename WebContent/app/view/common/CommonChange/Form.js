Ext.define('erp.view.common.CommonChange.Form',{
	requires: ['erp.view.core.form.Panel'],
	extend: 'erp.view.core.form.Panel', 
	alias: 'widget.erpCommonChangeFormPanel',
	LogFields:null,
	changeFields:new Array(),
	changeCodeField:null,
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		//集团版
		var master=getUrlParam('newMaster');
		var datalistId = getUrlParam('datalistId');
		var datalist = parent.Ext.getCmp(datalistId);
		if(datalist){
			var record = datalist.currentRecord;
			caller=record.get('cl_caller');
		}
		var param = {caller: this.caller || caller, condition:this.formCondition||formCondition, _noc: (getUrlParam('_noc') || this._noc)};
		if(master){
			param.master=master;
		}
		this.addEvents({alladded: true});
		this.callParent(arguments);
	},
	getItemsAndButtons:function(form,url,param){
		var me = this, tab = me.FormUtil.getActiveTab(),buttons=null;
		param.condition=null;
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
				form.uulistener = res.uulistener;//uu监听字段
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
				form.fo_isPrevNext = res.fo_isPrevNext=='1'?true:false;//上一条下一条
				form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
				var grids = Ext.ComponentQuery.query('gridpanel');
				//如果该页面只有一个form，而且form字段少于8个，则布局改变
				if(!form.fixedlayout && !form.minMode && grids.length == 0 && res.items.length <= 8){
					Ext.each(res.items, function(item){
						item.columnWidth = 0.5;
					});
					form.layout = 'column';
				}
				var record=null;
				if(formCondition==null || formCondition=='') buttons=res.buttons;
				else {
					record=me.getRecord(formCondition);
					buttons=me.getbuttons(caller);
				}			
				var items=me.setItems(form,res.items,record);
				form.add(items.baseItems);
				form.add(me.getChangeGroupItem());
				form.add(items.changeItems);
				me.fireEvent('afterload', me);
				me.FormUtil.setButtons(form, buttons);
			}
		});
	},
	setItems: function(form, items, data, limits){
		var edit = true,hasData = true,limitArr = new Array(),itemId=null,o=new Object(),array=new Array(),baseItems=new Array(),changeAfterItems=new Array(),allItems=new Object();
		form.LogFields=new Array();
		if(limits != null && limits.length > 0) {//权限外字段
			limitArr = Ext.Array.pluck(limits, 'lf_field');
		}
		if (data) {
			if(form.statuscodeField && data[form.statuscodeField] != null && data[form.statuscodeField] != '' &&  
					['ENTERING', 'UNAUDIT', 'UNPOST', 'CANUSE'].indexOf(data[form.statuscodeField]) == -1){//非在录入和已提交均设置为只读// && data[form.statuscodeField] != 'COMMITED'
				form.readOnly = true;
				edit = false;
			}
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
			if(item.labelAlign&&item.labelAlign!='top'){
				item.labelAlign = 'right';
			}
			if(item.group == '0'){
				item.margin = '7 0 0 0';
			}
			item.fieldStyle = item.fieldStyle + ';background:#fff;';
			if(screen.width < 1280){//根据屏幕宽度，调整列显示宽度
				if(item.columnWidth > 0 && item.columnWidth <= 0.25){
					item.columnWidth = 1/3;
				} else if(item.columnWidth > 0.25 && item.columnWidth <= 0.5){
					item.columnWidth = 2/3;
				} else if(item.columnWidth >= 1){
					item.columnWidth = 1;
				}
			} else {
				if(item.columnWidth > 0.25 && item.columnWidth < 0.5){
					item.columnWidth = 1/3;
				} else if(item.columnWidth > 0.5 && item.columnWidth < 0.75){
					item.columnWidth = 2/3;
				}
			}
			if(item.name != null) {
				if(item.name == form.statusField){//状态加特殊颜色
					item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
				} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
					item.xtype = 'hidden';
				}
			}
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
				item.margin = '0';
			}
			if(item.xtype == 'checkbox') {
				item.focusCls = '';
			}
			if (hasData) {
				item.value = data[item.name];
				if(item.secondname){//针对合并型的字段MultiField
					item.secondvalue = data[item.secondname];
				}
				if(!edit){
					form.readOnly = true;
					item.fieldStyle = item.fieldStyle + ';background:#eeeeee;';
					item.readOnly = true;
				} 
				if(item.xtype == 'checkbox'){
					item.checked = Math.abs(item.value || 0) == 1;
					item.fieldStyle = '';
				}
			}
			if(limitArr.length > 0 && Ext.Array.contains(limitArr, item.name)) {
				item.hidden = true;
			}

			if(item.html&&item.name == null&&item.value==''){

			}else{
				if(bool == 'b') {
					item.columnWidth = item.columnWidth*0.83;
				}
				if(bool == 'c') {
					item.columnWidth = item.columnWidth*0.85;
				}
			}
			if(item.logic=='changeCodeField'){
				form.changeCodeField=item.name;
			}
			if(item.logic!='changeKeyField' && item.logic!='changeCodeField' && item.xtype=='dbfindtrigger'){
				item.readOnly=true;
			}
			if(item.id!=null && (item.table ==null || item.table !='commonchangelog') && item.logic!='changeKeyField' && item.logic!='changeCodeField'){
				o=Ext.clone(item);
				o.id=o.id+"-new";
				o.triggerName=o.name;
				o.name=o.name+"-new";
				o.isCommonChange=true;
				if (data!=null) o.value=data[o.name];
				o.readOnly=false;
				if(item.logic=='readOnly'){
					o.readOnly=true;
				}
				o.group=10;				
				array.push(o);
				if(data&&data[item.id]!=data[o.id]){
					o.labelStyle = 'color:red';
				}
				item.readOnly=true;
				form.changeFields.push(item.name);
				//变更前字段且不为changeCodeField的统一改为不必填   lidy  2017120381
				item.allowBlank = true;
			}else form.LogFields.push(item.name);

		});
		// 字段少的form
		if(form.minMode) {
			Ext.each(array, function(item){
				if(item.columnWidth >= 0 && item.columnWidth < 0.5){
					item.columnWidth = 0.5;
				} else if(item.columnWidth >= 0.5) {
					item.columnWidth = 1;
				}
			});
		}
		allItems.changeItems=array;
		allItems.baseItems=items;
		return allItems;
	},
	getChangeGroupItem:function(){
		var o=new Object();
		o.html = "<div onclick=\"javascript:collapse(" + 10+ ");\" class=\"x-form-group-label\" id=\"group"
		+ 10 + "\" style=\"background-color: #bfbfbe;height:22px!important;\" title=\"收拢\"><h6>变更后</h6></div>";
		o.columnWidth = 1;
		o.xtype = "label";
		o.cls = "";
		return  o;
	},
	getLogValues:function(form){
		var values=form.getForm.getValues(),logvalues=new Object();
		Ext.Array.each(form.LogFields,function(field){
			logvalues[field]=values[field];
		});
		return logvalues;
	},
	getRecord:function(formCondition){
		var record=null;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			params : {
				fields:'cl_data,cl_status,cl_statuscode,cl_auditman,cl_auditdate',
				caller:'commonchangelog',
				condition:formCondition
			},
			async:false,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					var data=localJson.data;
					data.cl_data.cl_status=data.cl_status;
					data.cl_data.cl_statuscode=data.cl_statuscode;
					data.cl_data.cl_auditman=data.cl_auditman;
					data.cl_data.cl_auditdate=data.cl_auditdate;
					record=data.cl_data;
				}

			}

		});
		return record;
	},
	getbuttons:function(caller){
		var buttons=null;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldData.action',
			params : {
				field:'Fo_Button4rw',
				caller:'Form',
				condition:"fo_caller='"+caller+"'"
			},
			async:false,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					buttons=localJson.data;
				}

			}

		});
		return buttons;
	},
	loadInitData:function(form,field){
		var me=this;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			async: false,
			params: {
				caller: form.tablename,
				fields: form.changeFields.join(","),
				condition: field.name+'=\'' + field.value + '\''
			},
			method : 'post',
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);return;
				} else if(r.success && r.data){
					me.setFormData(form,r.data);
				}
			}
		});
	},
	setFormData:function(form,data){
		Ext.Array.each(form.changeFields,function(f){
			data[f+'-new']=data[f];
		});
		form.getForm().setValues(data);
	}	
});