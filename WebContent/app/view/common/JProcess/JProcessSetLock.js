Ext.define('erp.view.common.JProcess.JProcessSetLock', {
	extend : 'erp.view.core.form.Panel',
	alias : 'widget.jprocesssetlock',
	hideBorders : true,
	id : 'form',
	title : null,
	bodyPadding : '1 0 0 1',
	autoScroll : true,
	enableTools : false,
	defaults : {
		margin:'7 0 3 0',
		xtype : 'textfield',
		/*autoWidth:true,
		autoHeight:true, */
		/*maxLength:80,//能够输入的内容的最大长度
		maxLengthText:"字符长度不能超过80字符",//超出最大长度的设置信息
*/		//columnWidth:0.5,
		labelAlign: "right",  
		cls: "form-field-allowBlank", 
		fieldStyle: 'background:#ffffff;color:#515151;'
	},
	layout : 'column',
	initComponent : function() {
		var me = this;
		//var color=me.FormUtil.setItems(form);
		me.FormUtil = Ext.create('erp.util.FormUtil');
		me.GridUtil = Ext.create('erp.util.GridUtil');
		me.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.callParent(arguments); 
		formCondition = getUrlParam('formCondition');
		if (formCondition) {
			this.getData();
		}
		if (this.enableTools) {
			me.setTools();
		}
		var buttons = 'erpAddButton#erpDeleteButton#erpSaveButton#erpUpdateButton#erpCloseButton';
		me.FormUtil.setButtons(this, buttons);
	},	
	getItemsAndButtons:function(){
		return ;
	},

	items : [{name : 'js_id',
		fieldLabel : '流程ID',
		maxLength:22,//能够输入的内容的最大长度
		maxLengthText:"字符长度不能超过22字符",//超出最大长度的设置信息
		id : 'js_id',
		hidden:true,	
		},{
		name : 'js_caller',
		fieldLabel : '<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>流程Caller',
		id : 'js_caller',
		labelStyle:null,	  
		allowBlank : false,
		columnWidth:0.5,
		maxLength:80,
		maxLengthText:"字符长度不能超过80字符",
		//labelStyle:'color'
		
	}, {
		name : 'js_formKeyName',
		fieldLabel : '<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>主表关键字',
		id : 'js_formKeyName',
		labelStyle:null,		
		allowBlank : false,
		columnWidth:0.25,
		maxLength:20,
		maxLengthText:"字符长度不能超过20字符",
	}, {
		name : 'js_formStatusName',
		fieldLabel : '<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>状态字段',
		id : 'js_formStatusName',
		labelStyle:null,		
		allowBlank : false,
		columnWidth:0.25,
		maxLength:20,
		maxLengthText:"字符长度不能超过20字符",
	}, {
		name : 'js_table',
		fieldLabel : '<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>流程对应表名',
		id : 'js_table',
		labelStyle:null,		
		allowBlank : false,
		columnWidth:0.5,
		maxLength:800,
		maxLengthText:"字符长度不能超过800字符",
	}, {
		name : 'js_formDetailKey',
		fieldLabel : '关联主表字段',
		id : 'js_formDetailKey',
		columnWidth:0.5,
		maxLength:20,
		maxLengthText:"字符长度不能超过20字符",
	}, {
		name : 'js_decisionCondition',
		fieldLabel : '分支条件字段',
		id : 'js_decisionCondition',
		columnWidth:0.5
	}, {
		name : 'js_decisionVariables',
		fieldLabel : '分支条件变量',
		id : 'js_decisionVariables',
		columnWidth:0.5
	}, {
		name : 'js_bean',
		fieldLabel : 'BEAN',
		id : 'js_bean',
		columnWidth:0.5,
		maxLength:50,
		maxLengthText:"字符长度不能超过50字符",
	}, {
		name : 'js_formurl',
		fieldLabel : '<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>表单对应url',
		id : 'js_formurl',
		labelStyle:null,		
		allowBlank : false,
		columnWidth:0.5,
		maxLength:150,
		maxLengthText:"字符长度不能超过150字符",
	}, {
		name : 'js_serviceclass',
		fieldLabel : '业务类',
		id : 'js_serviceclass',
		columnWidth:0.5,
		maxLength:100,
		maxLengthText:"字符长度不能超过100字符",
	}, {
		name : 'js_auditmethod',
		fieldLabel : '审核方法',
		id : 'js_auditmethod',
		columnWidth:0.5,
		maxLength:50,
		maxLengthText:"字符长度不能超过50字符",
	}, {
		name : 'js_notefields',
		fieldLabel : '备注字段',
		id : 'js_notefields',
		columnWidth:0.5
	}, {
		name : 'js_codefield',
		fieldLabel : '编号字段',
		id : 'js_codefield',
		columnWidth:0.5,
		maxLength:40,
		maxLengthText:"字符长度不能超过40字符",
	}, {
		name : 'js_groupby',
		fieldLabel : '分组设置',
		id : 'js_groupby',
		columnWidth:0.5
	}
	],
	buttonAlign:'center',
	/*buttons:[{
		xtype: 'erpAddButton',	
		//hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpDeleteButton',
		//hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpSaveButton',
		//hidden:!Ext.isEmpty(formCondition)
	},{
		xtype: 'erpUpdateButton',
		//hidden:Ext.isEmpty(formCondition)
	},{
		xtype: 'erpCloseButton',
		id:'close'
	}],*/
	getData:function(){
		var me = this;
		//从url解析参数
		if(formCondition != null && formCondition != '')
			formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
			this.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'common/getFormDataByformCondition.action',
	        	params: {
	        		formCondition:formCondition, 
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		//getbyUUid=false;
	        		me.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}else{
	        			me.setFormValues(res.datas);
	        		}
	        	}
	        });
	},		
	setFormValues : function(data){
		var form = Ext.getCmp('form');
		//var data= Ext.decode(data);
		form.getForm().setValues(data);
		//var o = {};
		/*var status = Ext.getCmp('statuscode_');
		if (status && status.value != 'ENTERING') {
			form.readOnly = true;
			form.getForm().getFields().each(function(field) {  
		        field.setReadOnly(true);    
		    })  
		    form.fireEvent('afterload', form);
		}*/
		form.fireEvent('afterload', form);
		/*form.getForm().getFields();
		form.loadRecord(data);*/
	},
	addKeyBoardEvents: function(){
		return ;
	}
});