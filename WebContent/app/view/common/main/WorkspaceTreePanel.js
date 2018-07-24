Ext.define('erp.view.common.main.WorkspaceTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.workspaceTreePanel', 
	id: 'workspacetree-panel', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	singleExpand: true,
	rootVisible: false, 
	containerScroll : true, 
	autoScroll: false, 
	useArrows: true,
	bodyStyle:'background-color:#f1f1f1;',
	FormUtil: Ext.create('erp.util.FormUtil'),
	store: Ext.create('Ext.data.TreeStore', {
	    fields: ['addurl', 'allowDrag', 'caller', 'children', 'cls',
	    'creator', 'creator_id', 'data', 'deleteable', 'detno', 'url',
	    'expanded', 'iconCls', 'id', 'leaf', 'length', 'num', 'otherInfo',
	    'parentId', 'qtip', 'qtitle', 'showMode', 'svnversion', 'text', 'updateflag',
	    'updatetime', 'using', 'version']
	}),
	hideHeaders: true,
	columnLines:false,
	initComponent : function(){
		var me=this;
	 	me.columns =[{
	        xtype: 'treecolumn',
	        dataIndex: 'text',
	        flex: 1
	    }];
		this.getTreeRootNode(-999);//页面加载时，只将工作台节点加载进来
		this.callParent(arguments);
		me.addEvents('addclick');
	},
	tbar: [{
		xtype: 'erpTreeToolbar'
	}/*{
		xtype: 'button',
		width:170,
	    margin:'0 0 0 11',
		id: 'qmake-order-btn',
		cls: 'qmake-order-btn',
		text: '+ 快速制单',	
		style: 'top:0;',
		menu: Ext.create('Ext.menu.Menu', {
			id:'x-addmenu',
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
					height: 26,
					cls: 'x-addbtn-search-trigger',
					triggerCls: 'x-addbtn-search-trigger-icon',
					emptyText: '查找',
					enableKeyEvents: true,
					onTriggerClick: function() {
						var field = this,
							menu = Ext.getCmp('x-addmenu');
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
								var menu = Ext.getCmp('x-addmenu');
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
				},
				show: function(menu) {
					// 重设滚动条位置到上一次滚动的位置
					var menuBody = menu.el.dom.getElementsByClassName('x-vertical-box-overflow-body')[0];
					menuBody.scrollTop = menu.lastScrollTop || 0;
					var item = menuBody.getElementsByClassName('x-tree-addbtn')[0] || {offsetHeight:0};
					var itemHeight = item.offsetHeight;
					// 滚动距离设置为每次增减一个菜单项的高度
					var eFn = function(e) {
						var e=e||window.event;
			            var delte = e.wheelDelta/120 || -e.detail/3; // 向上滚动1或者向下滚动-1
			            menuBody.scrollTop = menuBody.scrollTop - delte*itemHeight;
					}
					document.onmousewheel===null?menuBody.onmousewheel=eFn:menuBody.addEventListener('DOMMouseScroll',eFn)
				},
				beforehide: function(menu) {
					// 记录滚动条位置
					menu.lastScrollTop = menu.el.dom.getElementsByClassName('x-vertical-box-overflow-body')[0].scrollTop;
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
		}),
		listeners: {
			mouseover:function(btn){
				var menu = btn.menu;
	        	if (menu.items.length > 0) {
	        		btn.showMenu();
	        	}else {
		        	btn.getMenuItems(btn, menu);
	        	}
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
		        	btn.getMenuItems(btn, menu);
	        	}
	        }
	    },
	    getMenuItems: function(btn, menu) {
	    	Ext.Ajax.request({
	        	url : basePath + 'common/getAddBtn.action',
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
								text: button.bb_text || button.TITLE,
								xtype: 'button',
								width: 160,
								iconCls: 'x-tree-icon-makeorder ' + button.ICONCLS,
								cls: 'x-tree-addbtn',
								overCls: 'x-tree-addbtn-over',
								tooltip: button.bb_text || button.TITLE,
								handler: function(btn,e){
									var workpanel = Ext.getCmp('workspacetree-panel');
									openUrl2(button.URL,button.TITLE,button.ID);
									workpanel.FormUtil.onAdd(button.ID, button.TITLE, button.URL);
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
	}*/],
	getTreeRootNode: function(parentId){
		var me = this;
		var condition = this.baseCondition;
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/lazyTree.action',
        	params: {
        		parentId: parentId,
        		condition: condition
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree=res.tree;
        			tree.push({id: 'report', parentId: 'bench', cls: 'x-tree-cls-node', iconCls: 'x-tree-icon-reportquery', text: '报表查询', leaf: true, url: 'jsps/common/reportsQuery.jsp'});
                	Ext.getCmp('workspacetree-panel').store.setRootNode({
                		text: 'root',
                		id: 'root',
                		expanded: true,
                		children: tree
                	});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
    openCloseFun: function(){
	 	  var o = Ext.getCmp("open");
	 	  var c = Ext.getCmp("close");
	 	  var tree = Ext.getCmp('workspacetree-panel');
	 		  if(o.hidden==false&&c.hidden==true){
	 			  tree.expandAll();
	 			  o.hide();
	 			  c.show();
	 		  }else{
	 			  tree.collapseAll();
	 			  o.show();
	 			  c.hide();
	 		  }
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	handleAddClick: function(gridView, rowIndex, colIndex, column, e) {
         this.fireEvent('addclick', gridView, rowIndex, colIndex, column, e);
	}
});