/**
 * 自定义同步资料按钮
 */
Ext.define('erp.view.core.button.SyncSpecial', {
	extend : 'Ext.Button',
	alias : 'widget.erpSyncSpecialButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpSyncSpecialButton,
	style : {
		marginLeft : '10px'
	},
	width : 70,
	autoRefreshPower: false,
	initComponent : function() {
		this.callParent(arguments);
		this.addEvents({
			'aftersync': true
		});
	},
	handler: function(btn) {
		var win = btn.win;
		if (!win) {
			win = Ext.create('Ext.Window', {
				title: btn.text,
				width: 800,
				height: 500,
				autoScroll: true,
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
			    					if(a.id != f.id && !a.special) {
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
						btn.sync();
					}
				}, {
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					handler: function(b) {
						b.up('window').hide();
					}
				}]
			});
			btn.win = win;
			this.getMasters();
		}
		win.show();
	},
	getMasters: function() {
		var me =this;
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
							sc = rs.syncControl,
							sp = (rs.special || '').split(','),
							curMaster,
							ms = new Array(),
							items = new Array();
						if(t != 'admin' && m != null) {
							ms = m.split(',');
						}
						
						Ext.Array.each(rs.masters, function(master) {
							if (c==master.ma_name) {
								curMaster=master;
							}
						});
						
						for(var i in rs.masters) {
							var s = rs.masters[i];
							if (s.ma_name != c) {
								if (!me.checkMaster(g,curMaster,s,ms,t,sc)) {
									continue;
								}
								var o = {
										boxLabel: s.ma_function,
										ma_id: s.ma_id,
										ma_pid: s.ma_pid,
										ma_user: s.ma_user,
										ma_soncode: s.ma_soncode,
										ma_type: s.ma_type,
										special: Ext.Array.contains(sp, s.ma_name),
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
	checkMaster:function(g,curMaster,s,ms,t,sc){
		var me = this;
		var bool =true;
		if(sc!=null && sc==="1"&&curMaster && curMaster.ma_soncode == null && !me.isSameGroup(curMaster,s))
		    bool=false;
		if("true" === g && "admin" !== t && !Ext.Array.contains(ms, s.ma_name))
			bool=false;
		if(s.ma_name == 'DataCenter' && "admin" !== t) {
			bool=false;
		}
		return bool;
	},
	isSameGroup:function(curMaster,s) {
		return curMaster.ma_kind == s.ma_kind && s.ma_soncode==null;
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
	sync: function() {
		var masters = this.getCheckData(), form = Ext.getCmp('form'), w = this.win, me = this,
			datas = this.syncdatas, cal = this.caller, url=this.url||this.syncUrl;
		var grid = Ext.getCmp('batchDealGridPanel') ;
		if(!datas && form && form.keyField && Ext.getCmp(form.keyField) 
				&& Ext.getCmp(form.keyField).value > 0) {
			datas = Ext.getCmp(form.keyField).value;
		}
		if(!datas && grid){
			items= grid.getSelectionModel( ).selected.items;
			var d = new Array();
			Ext.each(items, function(r){
				d.push(r.data.pp_id);
			});
			datas=d.join(',');
		}
		if(cal == null)
			cal = caller + '!Post';
		if (!Ext.isEmpty(masters)) {
			w.setLoading(true);
			Ext.Ajax.request({
				url: basePath + (url ||'common/form/specialPost.action'),
				params: {
					caller: cal,
					data: datas,
					to: masters
				},
				timeout: 600000,
				callback: function(opt, s, r) {
					w.setLoading(false);
					if(s) {
						var rs = Ext.decode(r.responseText);
						if(rs.data) {
							showMessage('提示', rs.data);
						} else {
							alert('同步成功!');
						}
	   					w.hide();
	   					if(me.autoRefreshPower)me.refreshPower(masters);
	   					me.fireEvent('aftersync', me, cal, datas, masters);
					}
				}
			});
		}
	}
});