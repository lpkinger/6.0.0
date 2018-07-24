Ext.QuickTips.init();
Ext.define('erp.controller.oa.flow.FlowCenter', {
	extend : 'Ext.app.Controller',
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'oa.flow.flowCenter.viewport','oa.flow.flowCenter.flowCenterFormPanel','oa.flow.flowCenter.flowCenterGridPanel',
			  'core.button.StatButton','core.button.SwitchButton','core.button.ProcessRemind','core.form.BtnDateField'],
	init : function() {
		var me = this;
		this.control({
			'erpFlowCenterGridPanel':{
    			afterrender:function(grid){
    				me.reconfigureGrid(grid);						
					me.loadGridData();
    			},
    			'headerfiltersapply': function() {//触发筛选时调用函数
					me.headerfiltersapplyFn();
				}
    		},
    		'erpStatButton':{
				click:function(btn){	
					var grid = Ext.getCmp('flowCenterGrid');
					me.reconfigureGrid(grid);						
					me.loadGridData();						
				}
    		},
    		'button[id=addFlow]':{
    			beforerender:function(btn){
    				btn.menu = Ext.create('Ext.menu.Menu', {
						id:'addFlowMenu',
						bodyStyle:'text-align:left;',
						autoScroll:true,
						maxHeight: (Ext.isIE?screen.height:window.innerHeight)*0.6,
						width: 166,
						dockedItems: [{
						    xtype: 'toolbar',
						    dock: 'top',
						    cls: 'x-addbtn-search-toolbar',
						    items: [{
						    	xtype: 'triggerfield',
						    	width: 160,
								height: 24,
								cls: 'x-addbtn-search-trigger',
								triggerCls: 'x-form-search-trigger',
								emptyText: '查找',
								enableKeyEvents: true,
								onTriggerClick: function() {
									var field = this,
										menu = Ext.getCmp('addFlowMenu');
									menu.filter(field.getValue());
								},
								listeners: {
									keydown: function(field, e) {
										// 阻止事件冒泡
										if( e && e.stopPropagation ) {
										    e.stopPropagation(); 
										}else {
										    window.event.cancelBubble = true; 
										}
										if(e.keyCode == 13) { // 回车键
											var menu = Ext.getCmp('addFlowMenu');
											menu.filter(field.getValue());
										}
									}
								}
						    }]
						}],
						listeners: {
							mouseleave: function(menu, e) {
								var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
								var box = menu.el.dom.getBoundingClientRect();
								if( cx <= (box.left) || cx >= (box.left+box.width) || cy <= (box.top-15) || cy >= (box.top+box.height) ) {
									menu.hide();
								}
							}
						},
						filter: function(keyword) {
							var menu = this;
							var menuItems = menu.allItems;
							menu.removeAll();
							var filterItems = [];
							if(!keyword) {
								filterItems = menuItems;
							}else {
								Ext.Array.each(menuItems, function(item) {
									if(item.text.indexOf(keyword) != -1) {
										filterItems.push(item);
									}
								});
							}
							menu.add(filterItems);
							//重新设置高度 达到动态变动的效果
							var menuBody = menu.el.dom.getElementsByClassName('x-vertical-box-overflow-body')[0];
							var item = menuBody.getElementsByClassName('x-tree-addbtn')[0] || {offsetHeight:0};
							var itemHeight = item.offsetHeight;
							var nowHeight = filterItems.length * (itemHeight + 1) + 36;
							if(nowHeight > menu.maxHeight) {
								menu.setHeight(menu.maxHeight);
							}else {
								menu.setHeight(nowHeight);
							}
						}
					})
    			},
				mouseout: function(btn, e) {
					var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
					var btnLayout = btn.getBox();
					if(cx <= btnLayout.x || cx >= btnLayout.x+btnLayout.width || cy <= btnLayout.y) {
						btn.hideMenu();
					}
				},
		        click: function(btn,e){
		        	var menu = btn.menu;
		        	if (menu.allItems) {
		        		if(menu.items.length > 0) {
			        		btn.showMenu();
		        		}else {
		        			menu.down('triggerfield').setValue();
		        			menu.add(menu.allItems);
		        			btn.showMenu();
		        		}
		        	}else {
			        	me.getMenuItems(btn, menu);
		        	}
		        }
    		}
		});
	},
	 loadGridData:function(){
    	var me = this;
		var type = me.getType();
		var grid = Ext.getCmp('flowCenterGrid');
		grid.store.loadPage(1,{
			callback:me.callbackFn
		});				
    },
    callbackFn:function(options,response,success){
		var res = Ext.decode(response.response.responseText);
		if(res.success){
			var form = Ext.getCmp('flowCenterForm');
			var statBtns = form.query('erpStatButton');
			Ext.Array.each(statBtns,function(btn){
				if(res[btn.type + 'Count']||res[btn.type + 'Count']>=0){
					var count = res[btn.type + 'Count'];
					btn.setStat(count);
					if(count>99){
						btn.setTooltip(count);
					}					
				}					
			});
		}else if(res.exceptionInfo){
			showError(res.exceptionInfo);
		}
	},
    getType:function(){
    	var type = '';
		var form = Ext.getCmp('flowCenterForm');
		var switchButton = form.query('erpSwitchButton')[0];					
		type = form.processType = switchButton.activeButton.type;   
		return type;
    },
    showTip:function(){
		var df = Ext.getCmp('msgNotice');
		if(df.hidden){
			df.hidden = false;
			df.el.slideIn('t', { duration: 2000 });
		}
    },
    onActivateEvent:function(tab){
    	var me = this;
    	var grid = Ext.getCmp('flowCenterGrid');
		tab.on('activate',function(tab){
			//进行刷新		
			Ext.getCmp('flowCenterGrid').store.loadPage(1,{
				callback:me.callbackFn
			});						
		});  
    },
	reconfigureGrid:function(grid){
    	var me = this;
		var group = me.getGroup();
		grid.reconfigureColumn(group);       	
    },
    getGroup:function(){
    	var group = '';
		var form = Ext.getCmp('flowCenterForm');
		var switchButtons = form.query('erpSwitchButton');					
		if(switchButtons.length>0){
			Ext.Array.each(switchButtons,function(item){
					group += '&' + item.activeButton.id;	
			});							
		}
		return group.substring(1);        	
    },
    headerfiltersapplyFn: function() {
		var me = this;
		var form = Ext.getCmp('flowCenterForm');
		var grid = Ext.getCmp('flowCenterGrid');
		var statBtns = form.query('erpStatButton');
		var filters = grid.gridFilters;
		var type = me.getType();
		var likestr = me.getLikeStr(grid, form, filters);
		//设置将所有的过滤操作推迟到服务器
		grid.store.remoteFilter=true;
		var count = grid.store.getCount();
		Ext.Array.each(statBtns,function(btn){
			if(btn.type == type){
				if(count>=0){
					btn.setStat(count);
					if(count>99){
						btn.setTooltip(count);
					}					
				}
			}
		});
	},
	getLikeStr: function(grid, form, filters) {
		var likestr = '';
		var me = this;
		for(var fn in filters) {
			var value = filters[fn],
				f = grid.getHeaderFilterField(fn);
			if(!Ext.isEmpty(value)) {
				if("null" != value) {
					if(f.originalxtype == 'numberfield') {
						if(value.indexOf('>=') == 0 || value.indexOf('<=') == 0 || value.indexOf('>') == 0 || value.indexOf('<') == 0 || value.indexOf('!=') == 0 || value.indexOf('=') == 0) {
							if(value.indexOf('!=') == 0) {
								value = "(" + fn + value + " or " + fn + " is null) ";
							} else {
								value = fn + value + " ";
							}
						} else if(value.indexOf('~') > -1) {
							var arr = value.split('~');
							value = fn + " between " + arr[0] + " and " + arr[1] + " ";
						} else {
							value = fn + "=" + value + " ";
						}
					} else if(f.originalxtype == 'datefield') {
						if(value.indexOf('=') > -1) {
							var valueX = value.split('=')[1];
							var length = valueX.split('-').length;
							if(length < 3) {
								if(length == 1) {
									var value1 = Ext.Date.toString(new Date(valueX + '-01-01'));
									var value2 = Ext.Date.toString(new Date(valueX + '-12-31'));
									value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '" + value2 + "'";
								} else if(length == 2) {
									var day = new Date(valueX.split('-')[0], valueX.split('-')[1], 0);
									var value1 = Ext.Date.toString(new Date(valueX + '-01'));
									var value2 = Ext.Date.toString(new Date(valueX + '-' + day.getDate()));
									value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '" + value2 + "'";
								}
							} else {
								if(value.indexOf('>=') == 0) {
									value = Ext.Date.toString(new Date(valueX));
									value = "to_char(" + fn + ",'yyyy-MM-dd')>='" + value + "' ";
								} else if(value.indexOf('<=') == 0) {
									value = Ext.Date.toString(new Date(valueX));
									value = "to_char(" + fn + ",'yyyy-MM-dd')<='" + value + "' ";
								} else {
									value = Ext.Date.toString(new Date(valueX));
									value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
								}
							}
						} else if(value.indexOf('~') > -1) {
							var value1 = Ext.Date.toString(new Date(value.split('~')[0]));
							var value2 = Ext.Date.toString(new Date(value.split('~')[1]));
							value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '" + value2 + "'";
						} else {
							value = Ext.Date.toString(new Date(value));
							value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
						}
					} else {
						var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
							exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
						if(exp_d.test(value)) {
							value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
						} else if(exp_t.test(value)) {
							value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value.substr(0, 10) + "' ";
						} else {
							if(f.xtype == 'combo' || f.xtype == 'combofield') {
								if(value == '-所有-') {
									value = ' 1=1 ';
								} else if(value == 'ptzh' && fn == 'IH_FROM') {
									value = fn + ' is null ';
								} else {
									if(f.column && f.column.xtype == 'yncolumn') {
										if(value == '-无-') {
											value = fn + ' is null ';
										} else {
											value = fn + ((value == '是' || value == '-1' || value == '1') ? '<>0' : '=0');
										}
									} else {
										if(value == 'none') {
											value = 'nvl(to_char(' + fn + '),\' \')=\' \'';
										} else {
											if(value) value = value.replace(/\'/g, "''");
											value = fn + " LIKE '" + value + "%' ";
										}
									}
								}
							} else if(f.xtype == 'datefield') {
								value = "to_char(" + fn + ",'yyyy-MM-dd') like '%" + value + "%' ";
							} else if(f.column && f.column.xtype == 'numbercolumn') {
								if(f.column.format) {
									var precision = f.column.format.substr(f.column.format.indexOf('.') + 1).length;
									//防止to_char去除小数点前面的0
									if(-1 < value && value < 1) {
										var number = value;
										value = "to_char(round(" + fn + "," + precision + "),";
										value += "'fm0.";
										for(var i = 0; i < precision; i++) {
											value += "0";
										}
										value += "') like '%" + number + "%' ";
									} else {
										value = "to_char(round(" + fn + "," + precision + ")) like '%" + value + "%' ";
									}
								} else
									value = "to_char(" + fn + ") like '%" + value + "%' ";
							} else {
								/**字符串转换下简体*/
								if(value) value = value.replace(/\'/g, "''");
								var SimplizedValue = me.BaseUtil.Simplized(value);
								//可能就是按繁体筛选  
								if(f.ignoreCase) { // 忽略大小写
									fn = 'upper(' + fn + ')';
									value = value.toUpperCase();
								}
								if(!f.autoDim) {
									if(SimplizedValue != value) {
										value = "(" + fn + " LIKE '" + value + "%' or " + fn + " LIKE '" + SimplizedValue + "%')";
									} else value = fn + " LIKE '" + value + "%' ";

								} else if(f.filterSelect || f.inputEl.dom.disabled || (f.rawValue == '' && f.emptyText == value)) {
									if(f.filterType == 'direct') {
										value = fn + "='" + value + "'";
									} else if(f.filterType == 'nodirect') {
										value = "nvl(" + fn + ",' ')<>'" + value + "'";
									} else if(f.filterType == 'head') {
										value = fn + " LIKE '" + value + "%' ";
									} else if(f.filterType == 'end') {
										value = fn + " LIKE '%" + value + "' ";
									} else if(f.filterType == 'null') {
										value = fn + " is null";
									} else if(f.filterType == 'novague') {
										if(SimplizedValue != value) {
											value = "(" + fn + " not LIKE '%" + value + "%' and " + fn + " not LIKE '%" + SimplizedValue + "%' or " + fn + " is null)";
										} else value = "(" + fn + " not LIKE '%" + value + "%' or " + fn + " is null)";
									} else {
										if(SimplizedValue != value) {
											value = "(" + fn + " LIKE '%" + value + "%' or " + fn + " LIKE '%" + SimplizedValue + "%')";
										} else value = fn + " LIKE '%" + value + "%' ";
										f.filterType = '';
									}
									f.filterSelect = false;
								} else {
									
									if(SimplizedValue != value) {
											value = "(" + fn + " LIKE '%" + value + "%' or " + fn + " LIKE '%" + SimplizedValue + "%')";
										
									} else {
									value = fn + " LIKE '%" + value + "%' "
									};
									f.filterType = '';
								}
							}
						}
					}
				} else value = "nvl(" + fn + ",' ')=' '";
				if(likestr == '') {
					likestr = value;
				} else {
					likestr = likestr + " and " + value;
				}

			}
		}
		form.likestr = likestr;
		return likestr;
	},
	 getMenuItems: function(btn, menu) {
    	Ext.Ajax.request({
        	url : basePath + 'oa/flow/getAddFlow.action',
        	async: false,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.data) {
	        		var items = new Array();
					Ext.Array.each(res.data,function(button, i){
						var item = {
							text: button.NAME,
							xtype: 'button',
							width: 160,
							iconCls: 'x-button-icon-add',
							cls: 'x-tree-addbtn',
							overCls: 'x-tree-addbtn-over',
							handler: function(btn,e){
								openUrl2('jsps/oa/flow/Flow.jsp?whoami='+button.CALLER,'新增'+button.NAME,'btn'+button.CALLER);
								/*workpanel.FormUtil.onAdd(button.ID, button.TITLE, button.URL);*/
							}
						};
						items.push(item);
					});
					menu.allItems = items;
					menu.add(items);
					menu.show();
        		}
        	}
        });
    }
});