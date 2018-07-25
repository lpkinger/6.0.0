Ext.QuickTips.init();

Ext.define('erp.view.oa.flow.flowDefine.flowDefineform', {
	extend:'Ext.form.Panel',
	alias:'widget.flowDefineform',
	layout:'column',
	FormUtil: Ext.create('erp.util.FormUtil'),
	id:'flowDefineform',
	cls:'form',
	bodyStyle:'background:#e9e9e9',
	autoScroll:true,
	items:[{
	  "fieldLabel": "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>流程名称",
      "name": "fd_name",
      "id": "fd_name",
      "xtype": "textfield",
      "maxLength": 100,
      "maxLengthText": "字段长度不能超过100字符!",
      "hideTrigger": false,
      "editable": true,
      labelStyle:"color:#1e1e1e",
      "columnWidth": 0.5,
      "allowBlank": false,
      "cls": "form-field-allowBlank",
      "fieldStyle": "background:#fff;color:#1e1e1e;",
      "labelAlign": "right",
      "text": null,
      "allowDecimals": true,
      margin:'5 0 0 0'
	},{
	  "fieldLabel": "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>流程简称",
      "name": "fd_shortname",
      "id": "fd_shortname",
      "xtype": "textfield",
      "maxLength": 100,
      "maxLengthText": "字段长度不能超过100字符!",
      "hideTrigger": false,
      "editable": true,
      "columnWidth": 0.5,
      "allowBlank": false,
      "cls": "form-field-allowBlank",
      "fieldStyle": "background:#fff;color:#1e1e1e;",
      "labelAlign": "right",
      labelStyle:"color:#1e1e1e",
      "text": null,
      margin:'5 0 0 0',
      "allowDecimals": true
	},{
	  "fieldLabel": "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>描述",
      "name": "fd_remark",
      "id": "fd_remark",
      "xtype": "textareatrigger",
      "maxLength": 300,
      "maxLengthText": "字段长度不能超过300字符!",
      "hideTrigger": false,
      "editable": true,
      "columnWidth": 0.5,
      "allowBlank": true,
      "cls": "form-field-allowBlank",
      "fieldStyle": "background:#fff;color:#1e1e1e;",
      "labelAlign": "right",
      labelStyle:"color:#1e1e1e",
      "allowDecimals": true,
      margin:'5 0 0 0'
	},{
	  "fieldLabel": "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>默认责任人",
      "name": "fd_defaultdutycode",
      "id": "fd_defaultdutycode",
      "maxLength": 300,
      "maxLengthText": "字段长度不能超过300字符!",
      "hideTrigger": false,
      "editable": true,
      "columnWidth": 0.5,
      "allowBlank": true,
      "cls": "form-field-allowBlank",
      "fieldStyle": "background:#fff;color:#1e1e1e;",
      "labelAlign": "right",
      labelStyle:"color:#1e1e1e",
      "allowDecimals": true,
      margin:'5 0 0 0',
	  table:"CUSTOMTABLE",
	  xtype:"dbfindtrigger"
	},{
	  "fieldLabel": "单据前缀码",
      "name": "PrefixCode",
      "id": "PrefixCode",
      "maxLength": 300,
      "maxLengthText": "字段长度不能超过300字符!",
      "hideTrigger": false,
      "editable": true,
      "columnWidth": 0.5,
      "allowBlank": true,
      "cls": "form-field-allowBlank",
      "fieldStyle": "background:#fff;color:#1e1e1e;",
      "labelAlign": "right",
      labelStyle:"color:#1e1e1e",
      "allowDecimals": true,
      margin:'5 0 0 0',
      "xtype": "textfield",
		}],
	initComponent : function(){ 
		var me = this;
		var data;
		var code;
		if(flowCaller){
			Ext.Ajax.request({
				async:false,
				url : basePath + '/oa/flow/getDefine.action',
				params: {
					caller : flowCaller
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exceptionInfo){
						showError(localJson.exceptionInfo);return;
					}
					if(res.success){
						data = res.data[0];
					}
				}
			});
			Ext.Ajax.request({
				async:false,
				url : basePath + '/common/getFieldData.action',
				params: {
					caller:'maxnumbers',
					field:'mn_leadcode',
					condition:"mn_tablename='"+flowCaller+"'"
				},
				method : 'post',
				callback : function(options,success,response){
					var localJson = new Ext.decode(response.responseText);
					if(localJson.exceptionInfo){
						showError(localJson.exceptionInfo);return;
					}
					if(localJson.success){
						if(localJson.data && localJson.data!=null && localJson.data!=''){
							code=localJson.data;
						}
					}
				}
			});
		}
		this.callParent(arguments);
		if(data){
			Ext.getCmp('fd_name').setValue(data.FD_NAME);
			Ext.getCmp('fd_shortname').setValue(data.FD_SHORTNAME);
			Ext.getCmp('fd_remark').setValue(data.FD_REMARK);
			Ext.getCmp('fd_defaultdutycode').setValue(data.FD_DEFAULTDUTYCODE);
			Ext.getCmp('PrefixCode').setValue(code);
			me.setTitle(data.FD_NAME);
		}
	}
});
