Ext.define('erp.view.sys.base.ParamsetPortal',{
	extend: 'Ext.form.Panel', 
	alias: 'widget.paramsetportal',
	autoScroll : true,
	labelSeparator : ':',
	buttonAlign : 'center',
	bodyStyle : 'background:#f9f9f9;padding:5px 5px 0',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	layout:'column',
	defaults:{
		xtype:'textfield',
		columnWidth:0.33,
		margin:'5 5 5 5'
	},
	caller:'sys',
	margin:'20 20 30 20',
	buttons: [{
		text: '清空',
		handler: function() {
			this.up('form').getForm().reset();
		}
	},{
		text: '保存',
		formBind: true, //only enabled once the form is valid
		disabled: true,
		handler: function() {
			var form = this.up('form').getForm();
			if (form.isValid()) {
				form.submit({
					success: function(form, action) {
						Ext.Msg.alert('Success', action.result.msg);
					},
					failure: function(form, action) {
						Ext.Msg.alert('Failed', action.result.msg);
					}
				});
			}
		}
	}],
	initComponent : function(){ 
		this.loadConfigs(this.caller, this.setConfigs,this);
		this.callParent(arguments);
	},
	loadConfigs: function(caller, callback,panel) {
		Ext.Ajax.request({
			url: basePath + 'ma/setting/configs.action?caller=' + caller,
			method: 'GET',
			callback: function(opt, s, r) {
				if(r && r.status == 200) {
					var res = Ext.JSON.decode(r.responseText);
					callback.call(null, res,panel);
				}
			}
		});
	},
	setConfigs: function(configs,panel) {
		var me = this,items = [];
		Ext.Array.each(configs, function(c, i){
			switch(c.data_type) {
			case 'YN':
				items.push({
					xtype: 'checkbox',
					boxLabel: c.title,
					name: c.code,
					id: c.id,
					checked: c.data == 1,
					columnWidth: 1,
					margin: c.help ? '4 8 0 8' : '4 8 4 8'
				});
				break;
			case 'RADIO':
				var s = [];
				Ext.Array.each(c.properties, function(p){
					s.push({
						name: c.code,
						boxLabel: p.display,
						inputValue: p.value,
						checked: p.value == c.data
					});
				});
				items.push({
					xtype: 'radiogroup',
					id: c.id,
					fieldLabel: c.title,
					columnWidth: 1,
					columns: 2,
					vertical: true,
					items: s
				});
				break;
			case 'COLOR':
				items.push({
					xtype: 'colorfield',
					fieldLabel: c.title,
					id: c.id,
					name: c.code,
					value: c.data,
					readOnly: c.editable == 0,
					editable: c.editable == 1,
					labelWidth: 150
				});
				break;
			case 'NUMBER':
				items.push({
					xtype: 'numberfield',
					fieldLabel: c.title,
					id: c.id,
					name: c.code,
					value: c.data,
					readOnly: c.editable == 0,
					labelWidth: 150
				});
				break;
			default :
				if(c.multi == 1) {
					var data = c.data ? c.data.split('\n') : [null], s = [];
					Ext.Array.each(data, function(d){
						s.push({
							xtype: (c.dbfind ? 'dbfindtrigger' : 'textfield'),
							name: c.dbfind || c.code,
							value: d,
							readOnly: !c.dbfind && c.editable == 0,
							editable: c.editable == 1,
							clearable: true
						});
					});
					s.push({
						xtype: 'button',
						text: '添加',
						width: 22,
						cls: 'x-dd-drop-ok-add',
						iconCls: 'x-dd-drop-icon',
						iconAlign: 'right',
						config: c
					});
					items.push({
						xtype: 'fieldset',
						title: c.title,
						id: c.id,
						name: c.code,
						columnWidth: 1,
						layout: 'column',
						defaults: {
							columnWidth: .25,
							margin: '4 8 4 8'
						},
						items: s
					});
				} else {
					items.push({
						xtype: (c.dbfind ? 'dbfindtrigger' : 'textfield'),
						fieldLabel: c.title,
						id: c.id,
						name: c.dbfind || c.code,
						value: c.data,
						readOnly: !c.dbfind && c.editable == 0,
						editable: c.editable == 1,
						clearable: true,
						columnWidth: .5,
						labelWidth: 150
					});
				}
			break;
			}
			if(c.help) {
				items.push({
					xtype: 'displayfield',
					value: c.help,
					columnWidth: ['NUMBER', 'VARCHAR2'].indexOf(c.data_type) > -1 ? .5 : 1,
							cls: 'help-block',
							margin: '4 8 8 8'
				});
			} else {
				if(['NUMBER', 'VARCHAR2'].indexOf(c.data_type) > -1) {
					items.push({
						xtype: 'displayfield'
					});
				}
			}
		});
		if(items.length == 0)
			items.push({
				html: '没有参数配置',
				cls: 'x-form-empty'
			});
		console.log(items);
		panel.add(items);
	}
});