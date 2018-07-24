
Ext.define('erp.view.common.main.Header', { 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.erpHeader',
	id: 'erpHeader',
	initComponent: function() { 
		var me = this;
		Ext.applyIf(this, { 
			region: 'north', 
			layout: 'hbox',
			layoutConfig: {  
				padding: '5',  
				align: 'middle'  
			},  
			defaults: {margins:'3 5 0 0'}, 
			items: [{
				xtype: 'image',
				width: 85,
				height: 50,
				margins: '3 5 0 0',
				id:'uaslog',
				src : basePath + 'resource/images/uas.png',
				listeners:{
					'beforerender':function(img,opts){
						var el = img.el,src=basePath + 'resource/images/uas.png';
						Ext.Ajax.request({
							url: basePath + 'ma/logo/hasLogo.action?_noc=1',
							async:false,
							success:function(fp, o,rep){	
								if(fp.responseText=='true') src=basePath+'ma/logo/get.action?_noc=1&Nmaster='+(Ext.isObject(opts)?sob:opts);
								if(el) el.dom.src=src;
								else  img.src=src;						
							}						
						});
					}
				}
			},{
				xtype: 'tbtext',
				align: 'middle',
				cls: 'logo-spliter',
				margin: '4 4 0 0'
			},{
				xtype: 'tbtext',
				align: 'stretch',
				cls: 'systitle',
				margins: '6 0 0 0',
				text: '<font>'+$I18N.common.main.sysTitle+'</font>'
				//text:'<div style="height:30px;font-size:16px;overflow:hidden; line-height:30px;">'+$I18N.common.main.sysTitle+'</div>'
			},{
				xtype: 'tbtext',
				flex: 10,
				text: ''
			},{
				xtype: 'button',
				id: 'changeMaster',
				align: 'start',
				name: 'sob',
				height: 26,
				cls: 'x-menu-button',
				text: sobText + '&nbsp;',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
	            menu: isSaas ? null : me.getMasterMenu(),
				margins: '3 5 0 0',
				listeners:{
					mouseover:function(btn){
						btn.menu ? (btn.menu.isVisible() ? '' : btn.showMenu()) : '';
					},
					mouseout: function(btn, e) {
						var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
						var btnLayout = btn.el.dom.getBoundingClientRect();
						if(cx <= btnLayout.left || cx >= btnLayout.left+btnLayout.width || cy <= btnLayout.top) {
							btn.hideMenu();
						}
					}
				}
			},{
				xtype: 'button',
				align: 'start',
				name: 'sob',
				height: 26,
				cls: 'x-menu-button',
				text: em_name + '&nbsp;',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
	            menu: {
					items:[{
						iconCls: '',
						text: '首页设置',
						handler: function(){
							Ext.getCmp("content-panel").setActiveTab(0);
							if(!Ext.getCmp('win')){
								Ext.create('erp.view.core.window.DeskTopSet');
							}
						}
					},{
						iconCls: '',
						text: '基础设置',
						handler: function(){
							Ext.getCmp('tree-tab').openBaseConfig();      
						},
						listeners:{
							afterrender:function(b){
								me.ifadmin(b);
							}
						}
					},{
						iconCls: '',
						text: '订阅设置',
						handler: function(){
							openTable('订阅管理','jsps/common/subscribe.jsp');          
						}
					},{
						iconCls: '',
						text: '通讯录',
						id: 'addrbook'
					}, {
						iconCls: '',
						text: '修改密码',
						id: 'set-pwd'
					},{
						iconCls: '',
						text: '消息设置',
						menu: [{
							iconCls:'',
							text:'弹出消息',
							checked: true,
							xtype: 'menucheckitem',
							overflow: 'visible',
							labelAlign:'left',
							id:'set-msg',
							listeners:{
								afterrender:function(com){
									Ext.Ajax.request({
										method:'post',
										url:basePath+'common/getMsgSet.action?_noc=1',
										callback : function(options,success,response){
											var res = new Ext.decode(response.responseText);
											if(res.exceptionInfo != null){
												showError(res.exceptionInfo);return;
											}else if(res.success){
												com.setChecked(res.IsRemaind);
											}
										}	
									});
								},
								click:function(item,e){
									var msg=item.checked?'确认启用消息提醒吗？':'确认取消消息提醒吗？';
									var remind=item.checked?1:0;
									Ext.MessageBox.show({
										title:'消息设置？',
										msg: msg,
										buttons: Ext.Msg.YESNO,
										icon: Ext.Msg.WARNING,
										fn: function(btn){
											if(btn == 'yes'){				   				    
												Ext.Ajax.request({
													url:basePath+'common/setMsgRemaind.action?_noc=1',
													params: {
														remind:remind
													},
													method : 'post',
													callback : function(options,success,response){
														var local=Ext.decode(response.responseText);
														if(local.success) {
															Ext.Msg.alert('提示','设置成功!');
														}else {
															showError(local.exceptionInfo);
														}
													}
												});
											} else if(btn == 'no'){
												//不保存	
												item.setChecked(!remind);
											} else {
												return;
											}
										}
									});
								}
							}
	
						},
						{
							
							iconCls:'',
							text:'桌面提醒',
							checked: true,
							xtype: 'menucheckitem',
							overflow: 'visible',
							labelAlign:'left',
							id:'set-desktop',
							listeners:{
								afterrender:function(com){
									Ext.Ajax.request({
										method:'post',
										url:basePath+'common/getdesktopremind.action?_noc=1',
										callback : function(options,success,response){
											var res = new Ext.decode(response.responseText);
											if(res.exceptionInfo != null){
												showError(res.exceptionInfo);return;
											}else if(res.success){
												com.setChecked(res.DtRemaind);
											}
										}	
									});
								},
								click:function(item,e){
									var msg=item.checked?'确认启用桌面消息提醒吗?':'确认取消桌面消息提醒吗?';
									var remind=item.checked?-1:0;
									Ext.MessageBox.show({
										title:'桌面消息设置?',
										msg: msg,
										buttons: Ext.Msg.YESNO,
										icon: Ext.Msg.WARNING,
										fn: function(btn){
											if(btn == 'yes'){				   				    
												Ext.Ajax.request({
													url:basePath+'common/setdesktopremind.action?_noc=1',
													params: {
														remind:remind
													},
													method : 'post',
													callback : function(options,success,response){
														var local=Ext.decode(response.responseText);
														if(local.success) {
															Ext.Msg.alert('提示','设置成功!');
														}else {
															showError(local.exceptionInfo);
														}
													}
												});
											} else if(btn == 'no'){
												//不保存	
												item.setChecked(!remind);
											} else {
												return;
											}
										}
									});
								}
							}
	
						
						}]
					},{
						iconCls: '',
						text: '工具',
						hidden: true,
						menu: [{
							iconCls: '',
							text: '方案导入',
							id: 'tools-imp'
						}]
					},{
						iconCls: '',
						text: '联系客服',
						menu:{
							items:[{
								text:'<span onclick="javascript:window.open(\'http://www.usoftchina.com\');" style="text-decoration:underline;color:blue;">优软科技官网</span>'
							},{	
								text:'<span>电话:400-830-1818</span>'
							},{	
								text:'<span>邮箱:info@usoftchina.com</span>'
							}]
						}
					},{
						iconCls: '',
						text: '系统版本',
						handler: function(){
							me.sysInfoWin();
						}
					},{
						xtype: 'button',
						text: '退出系统',
						cls: 'x-button-quitsys',
						height: 32,
						handler: function(){
							logout();
						}
					}],
					listeners: {
						mouseover: function() {
							this.over = true;
						},
						mouseleave: function() {
							this.over = false;
							this.hide();
						}
					}
				},
				margins: '3 5 0 0',
				listeners:{
					mouseover:function(btn){
						btn.showMenu();
					},
					mouseout: function(btn, e) {
						var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
						var btnLayout = btn.el.dom.getBoundingClientRect();
						if(cx <= btnLayout.left || cx >= btnLayout.left+btnLayout.width || cy <= btnLayout.top) {
							btn.hideMenu();
						}
					}
				}
			},{
				xtype: 'tbtext',
				align: 'end',
				text: '|',
				margin: '4 4 0 0'
			},{
				xtype: 'button',
				align: 'end',
				height: 26,
				width: 85,
				text: '快速开账',
				iconCls: 'quick',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
				hidden: !isSaas,
				margins: '3 5 0 0',
				handler: function(){
					//openTable('快速开账','system/init.action');  
					var url='system/init.action';
		    		window.open(basePath+url,'_blank');
				}
			},{
				xtype: 'button',
				height: 26,
				width: 75,
				text: '优软云',
				iconCls: 'cloud',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
				handler: function(){
					var url = 'b2b/ucloudUrl_token.action?url=http://www.ubtob.com&urlType=ubtob';
					window.open(url,'_blank');
				}
			},{
				xtype: 'button',
				height: 26,
				width: 75,
				text: '企业圈',
				iconCls: 'loop',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
				handler: function(){
					openTable('企业圈','jsps/b2c/common/enterpriseCircle.jsp');          
				}			
			},{
				xtype: 'button',
				height: 26,
				width: 75,
				text: 'UU助手',
				iconCls: 'UUHelper',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
				handler: function(){
					window.open(''+basePath+'/ma/uuHelperList.action?page=1&pageSize=10'+'');
				}
			},{
				xtype: 'button',
				height: 26,
				width: 85,
				id : 'message',
				html: '<em class=""><button type="button" hidefocus="true" role="button" autocomplete="off" class="x-btn-center" style="overflow:visible;"><span class="x-btn-inner" style="padding-left:20px;"><font class="x-text-info" size=1>消息中心</font><i style="display:none;" id="messageredpoint"></i></span><span class="x-btn-icon info" style="top:2px;left:4px">&nbsp;</span></button></em>',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
				handler:function(){
					me.openMessageCenter();
				}
			},{
				xtype: 'button',
				align: 'end',
				height: 26,
				width: 85,
				text: '退出系统',
				iconCls: 'quit',
				hidden: true,
				style:'background:transparent;border:0;',
				overCls: 'settings9',
				handler: function(){
					logout();
				}
			}],
			height: 50
		}); 
		this.callParent(arguments); 
	},
	prefix: 'uu-',
	addUU: function(p, f, u) {
		if(!Ext.isEmpty(f.value)) {
			var me = this, enval = escape(f.value);
			var tx = '<font size=2 color=blue>' + f.value + '</font>';
			var bt = this.down('button[uumsg=' + enval + ']');
			if(!bt) {
				this.insert(3, {
					text: tx,
					name: this.prefix + u.uu_field,
					xtype: 'button',
					height: 24,
					isuu: true,
					uumsg: enval,
					relative: {
						panel: p,
						field: f,
						uu: u
					},
					cls: 'x-btn-bw',
					iconCls: 'x-btn-uu-medium',
					margins: '3 5 0 0', 
					handler: function(btn) {
						var u = btn.relative.uu;
						if(u) {
							me.openUUClient(u, unescape(btn.uumsg));
						}
					}
				});
			}
		}
	},
	clearUU: function() {
		var me = this,
		uu = me.query('button[isuu=true]');
		Ext.each(uu, function(u){
			me.remove(u);
		});
	},
	removeUU: function(p) {
		var me = this,
		uu = me.query('button[isuu=true]');
		Ext.each(uu, function(u){
			var pl = u.relative.panel;
			if(pl.id == p.id)
				me.remove(u);
		});
	},
	refreshUU: function(panel, form, uu) {
		var me = this;
		this.clearUU();
		Ext.each(uu, function(u){
			var f = form.down('#' + u.uu_field);
			if(f) {
				if(!(u.uu_ftype == 1 && f.value == em_code) && !(u.uu_ftype == 2 && f.value == em_name)) {//排除自己
					me.addUU(panel, f, u);
				}
			}
		});
	},
	openUUClient: function(u, val) {
		var ef = 'em_name', tab = 'Employee', uuf = 'em_uu';
		switch(u.uu_ftype) {
		case 0:
			ef = 'em_id';break;
		case 1:
			ef = 'em_code';break;
		case 2:
			ef = 'em_name';break;
		case 3:
			ef = 'em_uu';break;
		case 4:
			ef = 've_id';tab = "Vendor";uuf = "ve_uu";break;
		case 5:
			ef = 've_code';tab = "Vendor";uuf = "ve_uu";break;
		case 6:
			ef = 've_name';tab = "Vendor";uuf = "ve_uu";break;
		case 7:
			ef = 've_uu';tab = "Vendor";uuf = "ve_uu";break;
		case 8:
			ef = 'cu_id';tab = "Customer";uuf = "cu_uu";break;
		case 9:
			ef = 'cu_code';tab = "Customer";uuf = "cu_uu";break;
		case 10:
			ef = 'cu_name';tab = "Customer";uuf = "cu_uu";break;
		case 11:
			ef = 'cu_uu';tab = "Customer";uuf = "cu_uu";break;
		}
		if(ef == uuf) {
			window.location = 'uas:' + val + '@58.61.153.82';
		} else {
			Ext.Ajax.request({
				url : basePath + 'common/getFieldData.action',
				params: {
					caller: tab,
					field: uuf,
					condition: ef + '=\'' + val + '\''
				},
				method : 'post',
				callback : function(options,success,response){
					var rs = new Ext.decode(response.responseText);
					if(rs.exceptionInfo){
						showError(rs.exceptionInfo);
					} else if(rs.success){
						var uu = rs.data;
						if(Ext.isEmpty(uu)){
							showError(val + ' 的关联UU号为空!');
						} else {
							window.location = 'uas:' + uu + '@58.61.153.82';
						}
					}
				}
			});
		}
	},
	getMasterMenu : function() {
		var me = this;
		var menu = Ext.create('Ext.menu.Menu', {
			id:'menuMaster',
			maxMasterName:'',
			shadow:false,
			width: 'auto',
			maxHeight:(Ext.isIE?screen.height:window.innerHeight)*1-50,
			dockedItems:[{
				xtype:'toolbar',
				dock: 'top',
				id:'menuMasterToolbar',
				hidden:true,
				items:[{
					id:'searchMaster',
       				xtype: 'textfield',
       				emptyText: $I18N.common.main.quickSearch,
       				enableKeyEvents: true,
       				listeners : {
       					keydown: function(field, e) {
							// 阻止事件冒泡
							if( e && e.stopPropagation ) {
							    e.stopPropagation(); 
							}else {
							    window.event.cancelBubble = true; 
							}
						},
			        	change:function(){
				        	me.searchMaster(this.value);
			        	}
			        }
				}]
			}],
			listeners: {
				buffer: 100,
				beforeshow: function(m) {
					me.getMasters(m);
				},
				mouseover: function() {
					this.over = true;
				},
				mouseleave: function(menu, e) {
					var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
					var box = menu.el.dom.getBoundingClientRect();
					if( cx <= (box.left) || cx >= (box.left+box.width) || /*cy <= (box.top-15) ||*/ cy >= (box.top+box.height) ) {
						menu.over = false;
						menu.hide();
					}
				}
/*				mouseleave:function(self){
					var btn = Ext.getCmp('changeMaster');
					btn.hideMenu();
				},
				afterrender: function( menu ) {
					menu.tip = new Ext.ToolTip({
						target: menu.getEl().getAttribute("id"),
						delegate: ".x-menu-item",
						trackMouse: true,
						renderTo: document.body,
						text: "text",
						title: "",
						width: 160,
						height: 50,
						listeners: {
							beforeshow: function updateTip( tip ) {
								var menuItem = menu.down('#' + tip.triggerElement.id );
								if( !menuItem.initialConfig.qtip ) return false;
								tip.body.dom.innerHTML = menuItem.initialConfig.qtip;
							}
						}
					});
				}*/
			}
		});

		return menu;
	},
	masterLimit : 15,
	searchMaster:function (keyword){
		var menu = Ext.getCmp('menuMaster');
		menu.focusedItem ? menu.focusedItem.deactivate() : '';
		var menuItems = menu.allItems;
		var selectedItem = menu.selectedItem;
		menu.removeAll();
		var filterItems = [];
		if(!keyword) {
			filterItems = menuItems;
		}else {
			Ext.Array.each(menuItems, function(item) {
				if(item.qtip == selectedItem.qtip) {
					item.iconCls = 'main-select-master';
					item.disabled = true;
				}else {
					item.iconCls = '';
					item.disabled = false;
				}
				if(item.text.indexOf(keyword) != -1 || item.qtip == selectedItem.qtip) {
					filterItems.push(item);
				}
			});
		}
		menu.add(filterItems);
		//重新设置高度 达到动态变动的效果
		var menuBody = menu.el.dom.getElementsByClassName('x-vertical-box-overflow-body')[0];
		var item = menuBody.getElementsByClassName('x-menu-item')[0] || {offsetHeight:0};
		var itemHeight = item.offsetHeight;
		var nowHeight = (filterItems.length) * (itemHeight)+33;
		nowHeight = (nowHeight > menu.maxHeight) ? menu.maxHeight : nowHeight;
		menu.setHeight(nowHeight);
	},
	getMasters : function(m) {
		var me = this;
		if (m.items.items.length == 0 && !m.loaded) {
			// 取账套配置,以及账套权限配置
			Ext.Ajax.request({
				url: basePath + 'common/getAbleMasters.action',
				params:{
					isOwnerMaster:true
				},
				method: 'GET',
				callback: function(opt, s, r) {
					if (s) {
						var rs = Ext.decode(r.responseText),
						defaultSob=rs.defaultSob,
						c = rs.currentMaster,
						g = rs.group,
						_t = rs._type,
						_m = rs._master,
						_ma = new Array(),
						items = new Array();
						if(_m != null) {
							_ma = _m.split(',');
						}
						/** 账套数量大于masterLimit时，使用table布局显示3列*/
						if(rs.masters.length>me.masterLimit){
							m.layout = {
								type: 'table',
								columns: 3
							};	
							m.minWidth = m.minWidth * 3 + 40;
						}
						for(var i in rs.masters) {
							var s = rs.masters[i];
							if("true" === g && "admin" !== _t) {
								if(!Ext.Array.contains(_ma, s.ma_name)) {
									if(s.ma_type == 2) {
										if(!s.ma_soncode)
											continue;
										var h = s.ma_soncode.split(','), _b = false;
										for (j in h ) {
											if(Ext.Array.contains(_ma, h[j])) {
												_b = true;break;
											}
										};
										if(!_b) continue;
									} else
										continue;
								}
							}
							if(s.ma_name==defaultSob){
								s.ma_function=s.ma_function+"*";
							}
							var	o = {text: s.ma_function, master: s, qtip: s.ma_name,
									listeners: {
										click: function(b) {
											me.changeMaster(b);
										}
									}};
								
							if (s.ma_name == c) {
								o.iconCls = 'main-select-master';
								o.disabled = true;
								m.selectedItem = o;
							}
							if(rs.masters.length>me.masterLimit){
								o.backIconCls = true;
							}
							items.push(o);
						}
						if (items.length>10) {
							Ext.getCmp('menuMasterToolbar').show();
						}
						m.allItems = items;
						m.removeAll();
						m.add(items);
						m.show();
						m.loaded = true;
					}
				}
			});
		}
	},
	changeMaster : function(b) {
		var me = this, tab = me.ownerCt.down('tabpanel'), bt = me.ownerCt.down('erpBottom'),tree=me.ownerCt.down('erpTreePanel');
		warnMsg('确定切换到' + b.text + '?', function(t){
			if(t == 'yes' || t == 'ok') {
				var lastBusiness,lastScene;
				if(lastBench){
					var p = Ext.getCmp(lastBench);
					if(p){
						lastBenchTitle = p.title;
						var iframe = p.getEl().down('iframe').dom;
						var win = iframe.contentWindow;
						if(win){
							var benchform = win.Ext.getCmp("benchform");
							if(benchform){
								var business = benchform.down('erpSwitchButton').getActive();
								lastBusiness = business.data.bb_code;
				   				var businessPanel = win.Ext.getCmp('business_'+business.data.bb_code);
				   				var swi = businessPanel.down('erpBusinessFormPanel erpSwitchButton');
				   				var activeBtn = swi.getActive();
				   				if(activeBtn) lastScene = activeBtn.data.bs_code;
							}
				   		}
					}
				}
				
				Ext.Ajax.request({
					url: basePath + 'common/changeMaster.action',
					params: {
						to: b.qtip
					},
					callback: function(opt, s, r) {
						var rs = Ext.decode(r.responseText);
						if(rs.success) {
							updatePrintType();
							Ext.getCmp('tree-tab').checkUpgrade(false);
							var img=me.down("image");
							img.fireEvent('beforerender',img,b.qtip);
							if (tab) {
								var p = tab.plugins[0];
								if (p) {
									p.doClose(true);
								}
			                	var s = me.down('button[name=sob]');
			                	s.setText(b.text +'&nbsp;');
			                	var workspaceTree = me.ownerCt.down('workspaceTreePanel');
			                	var searchField = workspaceTree.down('erpTreeToolbar').down('searchfield');
				    			searchField.originalValue = null;
				    			var win = Ext.getCmp('benchWin');
								win && win.destroy();
			                	/*var btn = Ext.getCmp('qmake-order-btn');
			                	btn.menu.allItems = null;*/
								bt.update({sob: b.text});
								window.sob = b.qtip;
								var m = b.ownerCt, items = m.allItems;
								Ext.each(items, function(item){
									if(item.qtip == b.qtip) {
										item.iconCls = 'main-select-master';
										item.disabled = true;
										m.selectedItem = item;
									} else {
										item.iconCls = '';
										item.disabled = false;
									}
									/*if (items.length>10 && this.isHidden()) {
										this.show();
									}*/
								});
								m.removeAll();
								m.add(items);
								Ext.getCmp('searchMaster').setValue('');
								var home = tab.down('#HomePage');
								if (home&&!lastBusiness) {
									//切账套后无需打开工作台，直接刷新首页
									home.getEl().down('iframe').dom.contentWindow.location.reload();
									home.needReload = false;
								}else if(home){
									//切账套后需打开工作台，延后刷新首页，等第一次点击激活首页才刷新
									home.needReload = true;
								}
								//me.ownerCt.down('erpNavigationTreePanel').getTreeRootNode(0);
								if(rs.typeChange) {
									tree.getTreeRootNode(0);
									workspaceTree.getTreeRootNode(-999);
								}
								//跳转到切换前的最后一个工作台
								if(lastBench&&lastBusiness&&lastScene){
									setTimeout(function(){
										openBench(null, lastBench, lastBenchTitle, lastBusiness, lastScene)
									}, 200);
								}
								Ext.getCmp("changeMaster").fireEvent("getmessage");
							} else {
								window.location.reload();
							}
						} else {
							alert('切换失败，请检查您在(' + b.qtip + ')的账号和密码.');
						}
					}
				});
			}
		});
	},
	openMessageCenter: function() {
		  //openTable('即时沟通','jsps/common/messageCenter/information.jsp'); 
	  	var main=Ext.getCmp("content-panel");
		var panel=Ext.getCmp('informations');
		if(!panel){
			var url=basePath+'jsps/common/messageCenter/information.jsp',
	    	panel = { 
	    			title : '消息中心',
	    			id:'informations',
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',   	
	    			items: {xtype: 'component',
							id:'iframe_detail',   					
							autoEl: {
									tag: 'iframe',
									style: 'height: 100%; width: 100%; border: none;',
									src: url}
	    				},
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
			var p = main.add(panel); 
			main.setActiveTab(p);
		}else{ 
	    	main.setActiveTab(panel); 
		} 
	},
	ifadmin:function(v){
		Ext.Ajax.request({
			url : basePath + '/common/isVirtual.action',
			method : 'get',
			async:false,
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				} else if(!r.success){					
					v.hide();
				} 
			}
		});
	},
	sysInfoWin:function(){
		var sysInfo = {};
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'DEPLOY_TIME,VERSION',
				caller : 'SYSINFO',
				condition : '1=1 order by deploy_time desc'
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				var data = Ext.decode(rs.data);
				if(data.length==0){
					showError('当前版本信息为空!')
				}else{
					sysInfo = {
						version:data[0].VERSION,
						deploy_time:data[0].DEPLOY_TIME
					};
				}
			}
		});
		if(sysInfo.version){
			var win = new Ext.Window({
				title: '<span style="font-weight:bold;color:#115fd8;">系统版本</span>',
				draggable:true,
				height: 150,
				width: 300,
				resizable:false,
		   		modal: true,
		   		layout:'column',
		   		items:[{
		   			cls:'x-sysinfo',
		   			readOnly:true,
		   			margin:'20 0 20 0',
		   			columnWidth:1,
		   			xtype:'displayfield',
		   			value:sysInfo.version,
		   			text:sysInfo.version,
		   			fieldLabel:'版 本 号',
		   			labelAlign:'right'
		   		},{
		   			cls:'x-sysinfo',
		   			readOnly:true,
		   			columnWidth:1,
		   			xtype:'displayfield',
		   			value:sysInfo.deploy_time,
		   			text:sysInfo.deploy_time,
		   			fieldLabel:'发布时间',
		   			labelAlign:'right'
		   		}]
			});
			win.show();
		}
	}
});
