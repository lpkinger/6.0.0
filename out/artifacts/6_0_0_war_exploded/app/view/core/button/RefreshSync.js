/**
 * 刷新同步状态
 */
Ext.define('erp.view.core.button.RefreshSync', {
	extend : 'Ext.Button',
	alias : 'widget.erpRefreshSyncButton',
	text : $I18N.common.button.erpRefreshSyncButton,
	iconCls : 'x-button-icon-reset',
	cls : 'x-btn-gray',
	width : 110,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	},
	handler: function(btn) {
		var win = btn.win;
		if (!win) {
			this.win = win = this.createMasterWin();
			this.getMasters();
		}
		win.show();
	},
	createMasterWin : function() {
		var me = this;
		return Ext.create('Ext.Window', {
			title: '指定需要检测的账套',
			width: 700,
			height: 400,
			layout: 'fit',
			items: [{
				xtype: 'form',
				autoScroll: true,
				bodyStyle: 'background: #f1f1f1;',
				layout: 'column',
				defaults: {
					xtype: 'checkbox',
					margin: '2 10 2 10',
					columnWidth: .5
				},
				items: [{
					boxLabel: '全选',
					columnWidth: 1,
					listeners: {
						change: function(f) {
		    				var form = f.up('form');
		    				form.getForm().getFields().each(function(a){
		    					if(a.id != f.id) {
		    						a.setValue(f.value);
		    					}
		    				});
		    			}
					}
				}]
			}],
			buttonAlign: 'center',
			buttons: [{
				text: $I18N.common.button.erpConfirmButton,
				cls: 'x-btn-blue',
				handler: function(b) {
					me.refreshSync();
				}
			}, {
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-blue',
				handler: function(b) {
					b.up('window').hide();
				}
			}]
		});
	},
	getMasters: function() {
		if (this.win) {
			var form = this.win.down('form');
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
							ms = new Array(),
							items = new Array();
						if(t != 'admin' && m != null) {
							ms = m.split(',');
						}
						for(var i in rs.masters) {
							var s = rs.masters[i];
							if (s.ma_name != c) {
								if("true" === g && "admin" !== t && !Ext.Array.contains(ms, s.ma_name))
									continue;
								if(s.ma_name == 'DataCenter' && "admin" !== t) {
									continue;
								}
								var o = {
										boxLabel: s.ma_name + '(' + s.ma_function + ')',
										ma_id: s.ma_id,
										ma_pid: s.ma_pid,
										ma_user: s.ma_user,
										ma_soncode: s.ma_soncode,
										ma_type: s.ma_type,
										listeners: {
											change: function(c) {
												if(c.ma_type == 2 && !Ext.isEmpty(c.ma_soncode) && c.value) {
													var ff = c.up('form').query('checkbox[ma_pid=' + c.ma_id + ']');
													Ext.each(ff, function(i){
														i.setValue(true);
													});
												}
											}
										}
									};
								items.push(o);
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
				if(item.ma_type != 2 && item.ma_user)
					data.push(item.ma_user);
			});
			return data.join(',');
		}
		return null;
	},
	refreshSync: function() {
		var masters = this.getCheckData(), form = Ext.getCmp('form'), w = this.win,
			datas = this.syncdatas, cal = this.caller;
		if(!datas && form && form.keyField && Ext.getCmp(form.keyField) 
				&& Ext.getCmp(form.keyField).value > 0) {
			datas = Ext.getCmp(form.keyField).value;
		}
		if(cal == null)
			cal = caller + '!Reset';
		if (!Ext.isEmpty(masters)) {
			w.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'common/form/refreshsync.action',
				params: {
					caller: cal,
					data: datas,
					to: masters
				},
				callback: function(opt, s, r) {
					w.setLoading(false);
					if(s) {
						var rs = Ext.decode(r.responseText);
						if(rs.data) {
							showMessage('提示', rs.data);
						} else {
							alert('刷新成功!');
						}
	   					w.hide();
					}
				}
			});
		}
	}
});