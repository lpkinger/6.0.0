Ext.define('erp.view.vendbarcode.main.vendbarcodeHeader', { 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.vendbarcodeHeader',
	initComponent: function() { 
		var me = this;
		Ext.applyIf(this, { 
			region: 'north', 
			bodyStyle: "background-image: url('" + basePath + "resource/images/header_gray.png')",
			layout: 'hbox',
			layoutConfig: {  
				padding: '5',  
				align: 'middle'  
			},  
			defaults: {margins:'3 5 0 0'}, 
			items: [{
				xtype: 'image',
				width: 85,
				height: 30,
				margins: '3 5 0 0',
				id:'uaslog',
				src : basePath + 'resource/images/uas.png'
			},{
				xtype: 'tbtext',
				align: 'stretch',
				margins: '6 0 0 0',
				text: '<font size="4" color="black">'+$I18N.common.main.sysTitle+'</font>'
				//text:'<div style="height:30px;font-size:16px;overflow:hidden; line-height:30px;">'+$I18N.common.main.sysTitle+'</div>'
			},{
				xtype: 'tbtext',
				flex: 10,
				text: ''
			},{
				xtype: 'button',
				id:'changeMaster',
				align: 'end',
				height: 26,
				width: 75,
				html:'<img height="20"  src="'+basePath+'resource/images/change.png" style="vertical-align:middle;"><font size=1>切换系统</font></img>',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
				menu: me.getMasterMenu(),
				hidden: isSaas,
				margins: isSaas ? '0' : '3 5 0 0',
				listeners:{
//					mouseover:function(btn){
//						if(!btn.hasVisibleMenu()){
//							btn.showMenu();	
//						}
//					}
					menushow:function(){
						var menuMaster = Ext.getCmp('menuMaster');
						menuMaster.setWidth(menuMaster.items.items[0]?menuMaster.items.items[0].getWidth()+4:124);
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
				width: 75,
				html:'<img  height="20"  src="'+basePath+'resource/images/logout.png" style="vertical-align:middle;"><font  size=1>退出系统</font></img>',
				style:'background:transparent;border:0;',
				overCls: 'settings9',
				handler: function(){
					logout();
				}
			}],
			height: 35 
		}); 
		this.callParent(arguments); 
	},
	prefix: 'uu-',
	getMasterMenu : function() {
		var me = this;
		var menu = Ext.create('Ext.menu.Menu', {
			id:'menuMaster',
			maxMasterName:'',
			shadow:false,
			maxHeight:(Ext.isIE?screen.height:window.innerHeight)*1-29,
			dockedItems:[{
				xtype:'toolbar',
				dock: 'top',
				id:'menuMasterToolbar',
				hidden:true,
				items:[{
					id:'searchMaster',
       				xtype: 'textfield',
       				emptyText: $I18N.common.main.quickSearch,
       				listeners : {
			        	change:function(){
			        		me.searchMaster(this);
			        	}
			        }
				}]
			}],
			listeners: {
				buffer: 100,
				beforeshow: function(m) {
					me.getMasters(m);
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
	searchMaster:function (f){
		var me = this,
		m = f.ownerCt.ownerCt,
		items = m.items.items,
		num=0,
		value = f.value;
		if(value && value!=""){
			Ext.each(items, function(item){
				if ((item.text && item.text.indexOf(value) !=-1) || item.qtip == sob) {
					num++;
					if (item.isHidden()) {
						item.show();
					}
				}else{
					if (!item.isHidden()) {
						item.hide();
					}
				}
			});
			me.setMenuHeight(m,29+num*27);
		}else {
			Ext.each(items, function(item){
				if (item.isHidden()) {
					item.show();
				}
			});
			me.setMenuHeight(m,document.body.offsetHeight);
		}
	},
	setMenuHeight:function(m,h){
		m.setHeight(h);
		m.doLayout();
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
								o.iconCls = 'main-todo';
								o.disabled = true;
							}
							items.push(o);
						}
						if (items.length>10) {
							Ext.getCmp('menuMasterToolbar').show();
						}
						m.add(items);
						m.show();
						m.loaded = true;
					}
				}
			});
		}
	},
	changeMaster : function(b) {
		var me = this, tab = me.ownerCt.down('vendErpTabPanel'), bt = me.ownerCt.down('erpBottom'),tree=me.ownerCt.down('vendbarcodeTreePanel');
		warnMsg('确定切换到' + b.text + '?', function(t){
			if(t == 'yes' || t == 'ok') {
				Ext.Ajax.request({
					url: basePath + 'vendbarcode/changeMaster.action',
					params: {
						to: b.qtip
					},
					callback: function(opt, s, r) {
						var rs = Ext.decode(r.responseText);
						if(rs.success) {
							var img=me.down("image");
							img.fireEvent('beforerender',img,b.qtip);
							if (tab) {
								var p = tab.plugins[0];
								if (p) {
									p.doClose(true);
								}
								bt.update({sob: b.text});
								window.sob = b.qtip;
								var m = b.ownerCt, items = m.items.items;
								Ext.each(items, function(){
									if(this.id == b.id) {
										this.setIconCls('main-todo');
										this.setDisabled(true);
									} else {
										this.setIconCls(' ');
										this.setDisabled(false);
									}
									if (items.length>10 && this.isHidden()) {
										this.show();
									}
								});
								Ext.getCmp('searchMaster').setValue('');
								var home = tab.down('#HomePage');
								if (home) {
									home.getEl().down('iframe').dom.contentWindow.location.reload();
								}
								if(rs.typeChange) tree.getTreeRootNode(0);
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
	}
});
