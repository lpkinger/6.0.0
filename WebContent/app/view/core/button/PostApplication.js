/**
 * 请购单抛转
 */
Ext.define('erp.view.core.button.PostApplication', {
	extend : 'Ext.Button',
	alias : 'widget.erpPostApplicationButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	id:'erpPostApplicationButton',
	text : $I18N.common.button.erpPostApplicationButton,
	style : {
		marginLeft : '10px'
	},
	width : 100,
	autoRefreshPower: false,
	initComponent : function() {
		this.callParent(arguments);
		this.addEvents({
			'aftersync': true
		});
	},
	handler: function(btn) {
		var win = btn.win;
		var me = this, grid = Ext.getCmp('batchDealGridPanel');
		var items = grid.getMultiSelected();
		var records = Ext.Array.unique(grid.getMultiSelected());
		var url = '';
		var datas = new Array();
		for(var i=0;i<records.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = records[i].data;
			if(records[i].data['ad_tqty']<=0){
				continue;
			}
			dd = new Object();
			Ext.each(grid.columns, function(c){
				dd['ad_tqty'] = records[i].data['ad_tqty'];
				dd['ad_id'] = records[i].data['ad_id'];
				dd['ad_apid'] = records[i].data['ad_apid'];
			});
			datas.push(Ext.JSON.encode(dd));
		}
		if(datas.length==0){
			showError('请勾选有效数据');
			return;
		}
		if(records.length > 0){
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
							xtype: 'radio',
							margin: '2 10 2 10',
							name:'postRadio',
							columnWidth: .5
						},
						items: [{
							fieldLabel:'<font size="2" style="font-family:Microsoft YaHei;font-weight:bold">选择一个帐套进行抛转</font></font>',
							xtype: 'displayfield',
							columnWidth: 1,
							labelWidth:280
						}]
					}],
					buttonAlign: 'center',
					buttons: [{
						text: '确认抛转',
						cls: 'x-btn-blue',
						handler: function(b) {
							btn.sync(grid, datas, url);
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
		} else {
			showError("请勾选需要的明细!");
		}
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
													var ff = c.up('form').query('radio[ma_pid=' + c.ma_id + ']');
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
				items = form.query('radio[checked=true]'),
				data = new Array();
			Ext.each(items, function(item){
				if(item.ma_type != 2 && item.ma_user)
					data.push(item.ma_user);
			});
			if(data.length>1){
				showError('只能选择一个帐套');
				return;
			}
			return data.join(',');
		}
		return null;
	},
	sync: function(grid, datas, url) {
		var masters = this.getCheckData(), form = Ext.getCmp('dealform'), w = this.win, me = this,
		cal = this.caller, url=this.url||this.syncUrl;
		var grid = Ext.getCmp('batchDealGridPanel') ;
		datas = datas == null ? [] : "[" + datas.toString().replace(/\\/g,"%") + "]";
		params = unescape(datas.toString().replace(/\\/g,"%")); 
		if(cal == null)
			cal = caller + '!Post';
		if (!Ext.isEmpty(masters)) {
			w.setLoading(true);
			Ext.Ajax.request({
				url: basePath + (url ||'scm/purchase/postApplication.action'),
				params: {
					caller: cal,
					data: params,
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