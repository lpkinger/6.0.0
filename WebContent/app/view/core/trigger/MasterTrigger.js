/**
 * 选择帐套的trigger
 * DB:MS
 */
Ext.define('erp.view.core.trigger.MasterTrigger', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.mastertrigger',
	triggerCls : 'x-form-search-trigger',
	initComponent : function() {
		this.callParent(arguments);
	},
	onTriggerClick : function() {
		var trigger = this, win = this.win;
		if (!win) {
			win = Ext.create('Ext.Window', {
				title : trigger.fieldLabel,
				width : 700,
				height : 400,
				layout : 'fit',
				items : [ {
					xtype : 'form',
					autoScroll: true,
					bodyStyle : 'background: #f1f1f1;',
					layout : 'column',
					defaults : {
						xtype : 'checkbox',
						columnWidth : .5,
						margin : '5 0 0 5'
					},
					items : [ {
						boxLabel : '全选',
						id : 'selectall',
						columnWidth : 1,
						listeners : {
							change : function(f) {
								var form = f.up('form');
								form.getForm().getFields().each(
										function(a) {
											if (a.id != f.id) {
												a.setValue(f.value);
											}
										});
							}
						}
					} ]
				} ],
				buttonAlign: 'center',
				buttons : [ {
					text : $I18N.common.button.erpConfirmButton,
					cls : 'x-btn-blue',
					handler : function(b) {
						b.up('window').hide();
						trigger.setValue(trigger.getCheckData());
					}
				}, {
					text : $I18N.common.button.erpCloseButton,
					cls : 'x-btn-blue',
					handler : function(b) {
						b.up('window').hide();
					}
				} ]
			});
			this.win = win;
			this.getMasters();
		}
		win.show();
	},
	getMasters: function() {
		if (this.win) {
			var form = this.win.down('form'), val = this.value;
			// 取帐套配置,以及帐套权限配置
			Ext.Ajax.request({
				url: basePath + 'common/getAbleMasters.action',
				method: 'GET',
				callback: function(opt, s, r) {
					if (s) {
						var rs = Ext.decode(r.responseText),
							c = rs.currentMaster,
							g = rs.group,
							t = rs._type,
							m = rs._master,
							items = new Array(),
							ms = new Array(),
							va = new Array();
						if(t != 'admin' && m != null) {
							ms = m.split(',');
						}
						if(val != null) {
							va = val.split(',');
						}
						for(var i in rs.masters) {
							var s = rs.masters[i];
							if("true" === g && "admin" !== t && !Ext.Array.contains(ms, s.ma_name))
								continue;
							if(s.ma_type == 3 || s.ma_type == 1) {
								if(t == 'admin') {
									var o = {
											boxLabel: s.ma_name + '(' + s.ma_function + ')',
											ma_user: s.ma_user
										};
									for (var j in va) {
										if (s.ma_name == va[j]) {
											o.checked = true;break;
										}
									}
									if (s.ma_name == c)
										o.checked = true;
									items.push(o);
								} else {
									for (var j in ms) {
										if(s.ma_name == ms[j]) {
											var o = {
													boxLabel: s.ma_name + '(' + s.ma_function + ')',
													ma_user: s.ma_user
												};
											for (var x in va) {
												if (s.ma_name == va[x]) {
													o.checked = true;break;
												}
											}
											if (s.ma_name == c)
												o.checked = true;
											items.push(o);break;
										}
									}
								}
							}
						}
						form.add(items);
					}
				}
			});
		}
	},
	getCheckData: function() {
		if (this.win) {
			var form = this.win.down('form'),
				items = form.query('checkbox[checked=true]'),
				data = new Array();
			Ext.each(items, function(item){
				if(item.ma_user)
					data.push(item.ma_user);
			});
			return data.join(',');
		}
		return null;
	}
});