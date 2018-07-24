/**
 * 新增任务
 */
Ext.define('erp.view.core.window.Task', {
	extend : 'Ext.window.Window',
	alias : 'widget.taskwindow',
	requires : ['erp.view.core.trigger.DbfindTrigger',
			'erp.view.core.form.FileField',
			'erp.view.core.form.ConDateHourMinuteField'],
	width : '80%',
	height : 410,
	cls : 'custom-blue',
	closeAction : 'destroy',
	title : '添加任务',
	sourceform : null,
	initComponent : function() {
		var me = this;
		me.items = me.items || [];
		me.items.push(me.createTaskForm());
		this.callParent(arguments);
		if (me.sourceform) {
			me.setDefaultValue(me.sourceform);
		}
	},
	createTaskForm : function() {
		var me = this;
		return Ext.create('Ext.form.Panel', {
			bodyStyle : 'background:#f1f2f5;border:none;',
			layout : 'column',
			defaults : {
				columnWidth : 1 / 3,
				margin : '2 2 2 2'
			},
			items : [{
						xtype : 'textfield',
						name : 'name',
						fieldLabel : '任务名称',
						allowBlank : false
					}, {
						xtype : 'textfield',
						name : 'sourcecode',
						fieldLabel : '关联单号',
						readOnly : true
					}, {
						xtype : 'checkbox',
						boxLabel : '是否需要确认',
						checked : true,
						name : 'type',
						inputValue : 1
					}, {
						xtype : 'condatehourminutefield',
						name : 'startdate',
						id : 'startdate',
						logic : 'enddate',
						secondname : 'enddate',
						fieldLabel : '任务处理时间',
						columnWidth : 2 / 3,
						allowBlank : false

					}, {
						xtype : 'numberfield',
						name : 'duration',
						fieldLabel : '持续时间(时)',
						hideTrigger : true,
						readOnly : true,
						value : 24
					}, {
						xtype : 'hidden',
						id : 'enddate',
						name : 'enddate',
						fieldLabel : '结束时间',
						allowBlank : false
					}, {
						xtype : 'fieldcontainer',
						fieldLabel : '处理人',
						name : 'resourcename',
						columnWidth : 1,
						layout : 'hbox',
						defaults : {
							margin : '0 2 0 2'
						},
						getSubmitData : function() {
							var c = this.query('checkbox[value=true]'), names = new Array();
							Ext.each(c, function() {
										names.push(this.boxLabel);
									});
							c = this.query('dbfindtrigger');
							Ext.each(c, function() {
										if (!Ext.isEmpty(this.value)) {
											names.push(this.value);
										}
									});
							return names.join(',');
						},
						items : [{
									xtype : 'dbfindtrigger',
									name : 'ma_recorder',
									isFormField : false,
									// margin : '0 2 0 2', 
									labelWidth : 40,
									fieldLabel : '其他',
									listeners : {
										aftertrigger : function(t, r) {
											t.setValue(r.get('em_name'));
										}
									}
								}, {
									xtype : 'button',
									iconCls : 'x-button-icon-add',
									cls : 'x-btn-tb',
									handler : function(b) {
										b.ownerCt.insert(
												b.ownerCt.items.items.length- 1, {
													xtype : 'dbfindtrigger',
													name : 'ma_recorder',
													isFormField : false,
													listeners : {
														aftertrigger : function(t, r) {
															t.setValue(r.get('em_name'));
														}
													}
												});
									}
								}]
					}, {
						xtype : 'mfilefield',
						name : 'attachs',
						columnWidth : 1,
						id : 'attachs'
					}, {
						xtype : 'textarea',
						name : 'description',
						fieldLabel : '描述',
						allowBlank : false,
						height : 160,
						columnWidth : 1,
						value : (window.errmessage || '')
					}, {
						xtype : 'hidden',
						name : 'sourcelink'
					}],
			buttonAlign : 'center',
			buttons : [{
						text : '重置',
						cls : 'x-btn-blue',
						handler : function(b) {
							b.ownerCt.ownerCt.getForm().reset();
						}
					}, {
						text : '确定',
						cls : 'x-btn-blue',
						formBind : true,
						handler : function(b) {
							var start = new Date(Ext.getCmp('startdate').items.items[5].value);
							var end = new Date(Ext.getCmp('enddate').value);
							if (start - end > 0) {
								showError('任务处理时间输入有误，请检查后重新输入');
							} else {
								me.onTaskAdd(b.ownerCt.ownerCt);
							}
						}
					}, {
						text : '关闭',
						cls : 'x-btn-blue',
						handler : function(b) {
							b.ownerCt.ownerCt.ownerCt.close();
						}
					}]
		});
	},
	onTaskAdd : function(form) {
		var me = this;
		var start = form.down('field[name=startdate]'), end = form
				.down('field[name=enddate]'), dur = form
				.down('field[name=duration]'), name = form
				.down('fieldcontainer[name=resourcename]');
		dur.setValue(Ext.Number
				.toFixed((new Date(end.getValue()).getTime() - new Date(start
								.getValue()).getTime())
								/ (1000 * 60 * 60), 2));
		var v = form.getValues();
		Ext.each(Ext.Object.getKeys(v), function(k) {// 去掉页面非表单定义字段
					if (contains(k, 'ext-', true)) {
						delete v[k];
					}
				});
		v.resourcename = name.getSubmitData();
		form.setLoading(true);
		Ext.Ajax.request({
					url : basePath + 'plm/task/addbilltask.action',
					params : {
						formStore : unescape(Ext.encode(v).replace(/\\/g, "%"))
					},
					callback : function(opt, s, res) {
						form.setLoading(false);
						var r = Ext.decode(res.responseText);
						if (r.success) {
							alert('添加成功!');
							me.close();
						} else if (r.exceptionInfo) {
							showError(r.exceptionInfo);
						}
					}
				});
	},
	setDefaultValue : function(form) {
		var me = this, resources = me.getTaskMans(form);
		me.down('textfield[name=name]').setValue(form.title);
		if (form.codeField) {
			var c = form.down('#' + form.codeField);
			if (c) {
				me.down('textfield[name=sourcecode]').setValue(c.getValue());
			}
			var u = new String(window.location.href);
			u = u.substr(u.indexOf('jsps'));
			me.down('field[name=sourcelink]').setValue(u);
		}
		if (resources.length > 0) {
			var t = me.down('fieldcontainer[name=resourcename]');
			Ext.each(resources, function(u) {
						var f = form.down('#' + u.uu_field);
						if (f) {
							if (!(u.uu_ftype == 1 && f.value == em_code)
									&& !(u.uu_ftype == 2 && f.value == em_name)) {// 排除自己
								if (f.value
										&& !t.down('checkbox[boxLabel='
												+ f.value + ']')) {
									t.insert(0, {
												xtype : 'checkbox',
												name : 'man',
												isFormField : false,
												checked : true,
												boxLabel : f.value
											});
								}
							}
						}
					});
		} else {
			me.down('dbfindtrigger[name=ma_recorder]').hideLabel = true;
			me.down('dbfindtrigger[name=ma_recorder]').margin = '0';
		}
	},
	getTaskMans : function(form) {
		var mans = new Array();
		Ext.Ajax.request({
					url : basePath + 'plm/task/getTaskResources.action',
					params : {
						caller : form.caller || caller
					},
					async : false,
					callback : function(opt, s, res) {
						form.setLoading(false);
						var r = Ext.decode(res.responseText);
						if (r.success) {
							mans = r.resources;
						}
					}
				});
		return mans;
	}
});