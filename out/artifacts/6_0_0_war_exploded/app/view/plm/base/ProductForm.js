Ext.define('erp.view.plm.base.ProductForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpProductFormPanel',
	id: 'form', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       focusCls: 'x-form-field-cir',//fieldCls
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	saveUrl: '',
	updateUrl: '',
	deleteUrl: '',
	keyField: '',
	codeField: '',
	statusField: '',
	params: null,
	caller: null,
	Contextvalue:null,
	LastValue:null,
	enableTools: true,
	enableKeyEvents: true,
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
    	var param = {caller: this.caller || caller, condition: formCondition};
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', this.params || param);//从后台拿到formpanel的items
		this.callParent(arguments);
		//加prev、next、refresh等tool
		if(this.enableTools) {
			this.setTools();
		}
		//给页面加上ctrl+alt+s键盘事件,自动跳转form配置界面
		if(this.enableKeyEvents) {
			this.addKeyBoardEvents();
		}		
	},
    getItemsAndButtons:function(){
    	var me = this;
		Ext.Ajax.request({//拿到form的items
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		me.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		form.fo_id = res.fo_id;
        		form.fo_keyField = res.fo_keyField;
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
        		form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
        
        		//data&items
        		var items = me.setItems(form, res.items, res.data, res.limits);
        		form.add(items);      	
        		//title
        		if(res.title && res.title != ''){
        			form.setTitle(res.title);
        		}
        		//解析buttons
        		me.setButtons(form, res.buttons);
        		//form第一个可编辑框自动focus
        		me.focusFirst(form);
        	}
        });
    } ,
    setItems: function(form, items, data, limits){
		var edit = true,hasData = true,limitArr = new Array();
		if(limits != null && limits.length > 0) {//权限外字段
			limitArr = Ext.Array.pluck(limits, 'lf_field');
		}
		if (data) {
			data = Ext.decode(data);
			if(form.statuscodeField && data[form.statuscodeField] != null && 
					!(data[form.statuscodeField] == 'ENTERING' || data[form.statuscodeField] == 'UNAUDIT')){//非在录入和已提交均设置为只读// && data[form.statuscodeField] != 'COMMITED'
				form.readOnly = true;
				edit = false;
			} 
		} else {
			hasData = false;
		}
		var bool = false;
		if(items.length > 110){
			bool = true;
		}
		Ext.each(items, function(item){
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
			if(item.name == form.statusField){//状态加特殊颜色
				item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
			} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
				item.xtype = 'hidden';
			}
			if (hasData) {
				item.value = data[item.name];
				if(item.secondname){//针对合并型的字段MultiField
					item.secondvalue = data[item.secondname];
				}
				if(!edit){
					form.readOnly = true;
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				} 
				if(item.xtype == 'checkbox' && Math.abs(item.value || 0) == 1){
					item.checked = true;
				}
			}
			if(limitArr.length > 0 && Ext.Array.contains(limitArr, item.name)) {
				item.hidden = true;
			}
			if(bool) {
				item.columnWidth = item.columnWidth*0.83;
			}
		});
		return items;
	},
});