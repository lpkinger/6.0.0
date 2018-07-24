/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
Ext.ns('Ext.ux');
/**
 * @class Ext.ux.TabScrollerMenu
 * @extends Object
 * Plugin (ptype = 'tabscrollermenu') for adding a tab menu to a TabBar is the Tabs overflow.
 * @constructor
 * @param {Object} config Configuration options
 * @ptype tabscrollermenu
 */
Ext.define('Ext.ux.TabScrollerMenu', {
    alias: 'plugin.tabscrollermenu',

    uses: ['Ext.menu.Menu'],

    /**
     * @cfg {Number} pageSize How many items to allow per submenu.
     */
    pageSize: 10,
    /**
     * @cfg {Number} maxText How long should the title of each {@link Ext.menu.Item} be.
     */
    maxText: 15,
    /**
     * @cfg {String} menuPrefixText Text to prefix the submenus.
     */
    menuPrefixText: 'Items',
    constructor: function(config) {
        config = config || {};
        Ext.apply(this, config);
    },
    //private
    init: function(tabPanel) {
        var me = this;

        Ext.apply(tabPanel, me.parentOverrides);
        me.tabPanel = tabPanel;

        tabPanel.on({
            render: function() {
                me.tabBar = tabPanel.tabBar;
                me.layout = me.tabBar.layout;
                me.layout.overflowHandler.handleOverflow = Ext.Function.bind(me.showButton, me);
                me.layout.overflowHandler.clearOverflow = Ext.Function.createSequence(me.layout.overflowHandler.clearOverflow, me.hideButton, me);
            },
            single: true
        });
    },

    showButton: function() {
        var me = this,
            result = Ext.getClass(me.layout.overflowHandler).prototype.handleOverflow.apply(me.layout.overflowHandler, arguments);
	
        if (!me.menuButton) {
            me.menuButton = me.tabBar.body.createChild({
                cls: Ext.baseCSSPrefix + 'tab-tabmenu-right'
            }, me.tabBar.body.child('.' + Ext.baseCSSPrefix + 'box-scroller-right'));
            me.menuButton.addClsOnOver(Ext.baseCSSPrefix + 'tab-tabmenu-over');
            me.menuButton.on('click', me.showTabsMenu, me);
            me.menuButton.on('mouseout', function(e, btn){
            	// 自动隐藏菜单逻辑
            	var menu = me.tabsMenu;
            	if(menu) {
            		if(menu.isVisible()) {
	            		var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
						var layout = btn.getBoundingClientRect();
						if(cx >= layout.left+layout.width || cy <= layout.top) {
							menu.hide();
						}else if(cx <= layout.left) {
							setTimeout(function(){
								var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
								if(cx <= (layout.left-20) && cy <= (layout.top+layout.width)) {
									menu.hide();
								}
							},200);
						}
	            	}
            	}
            }, me);
        }
        me.menuButton.show();
        result.targetSize.width -= me.menuButton.getWidth();
        return result;
    },

    hideButton: function() {
        var me = this;
        if (me.menuButton) {
            me.menuButton.hide();
        }
    },

    /**
     * Returns an the current page size (this.pageSize);
     * @return {Number} this.pageSize The current page size.
     */
    getPageSize: function() {
        return this.pageSize;
    },
    /**
     * Sets the number of menu items per submenu "page size".
     * @param {Number} pageSize The page size
     */
    setPageSize: function(pageSize) {
        this.pageSize = pageSize;
    },
    /**
     * Returns the current maxText length;
     * @return {Number} this.maxText The current max text length.
     */
    getMaxText: function() {
        return this.maxText;
    },
    /**
     * Sets the maximum text size for each menu item.
     * @param {Number} t The max text per each menu item.
     */
    setMaxText: function(t) {
        this.maxText = t;
    },
    /**
     * Returns the current menu prefix text String.;
     * @return {String} this.menuPrefixText The current menu prefix text.
     */
    getMenuPrefixText: function() {
        return this.menuPrefixText;
    },
    /**
     * Sets the menu prefix text String.
     * @param {String} t The menu prefix text.
     */
    setMenuPrefixText: function(t) {
        this.menuPrefixText = t;
    },

    showTabsMenu: function(e) {
        var me = this;
        /*if (me.tabsMenu) {
            me.tabsMenu.removeAll();
        } else {
            me.tabsMenu = Ext.create('Ext.menu.Menu', {
            	bodyCls: 'x-tab-menu-body',
            	width: 'auto',
            	maxHeight:(Ext.isIE?screen.height:window.innerHeight)*0.7,
            	listeners: {
            		buffer: 100,
            		// 自动隐藏菜单逻辑
            		mouseleave: function(menu) {
            			menu.hide();
            		}
            	}
            });
            //me.tabPanel.on('destroy', me.tabsMenu.destroy, me.tabsMenu);
        }*/
		
		//是否启用搜索功能
		if (me.search) {
	        me.tabsMenu = Ext.create('Ext.menu.Menu', {
	        	bodyCls: 'x-tab-menu-body',
	        	width: 'auto',
	        	maxHeight:(Ext.isIE?screen.height:window.innerHeight)*0.7,
				dockedItems:[{
					xtype:'toolbar',
					dock: 'top',
					items:[{
		   				xtype: 'textfield',
		   				name:'search',
		   				enableKeyEvents: true,
		   				listeners : {
				        	change:function(){
					        	me.searchMenu(this.value);
				        	}
				        }
					}]
				}],	        	
	        	listeners: {
	        		// 自动隐藏菜单逻辑
	        		mouseleave: function(menu) {
//	        			menu.hide();
	        		},
	        		afterrender:function(menu){
	        			var width=menu.items.items[0].getWidth()
	        			var search = menu.dockedItems.items[0].items.items[0];
	        			search.setWidth(width);
	        		}
	        	}
	        });			
		}else{
	        me.tabsMenu = Ext.create('Ext.menu.Menu', {
	        	bodyCls: 'x-tab-menu-body',
	        	width: 'auto',
	        	maxHeight:(Ext.isIE?screen.height:window.innerHeight)*0.7,
	        	listeners: {
	        		// 自动隐藏菜单逻辑
	        		mouseleave: function(menu) {
	        			menu.hide();
	        		}
	        	}
	        });			
		}
        
        me.tabPanel.on('destroy', me.tabsMenu.destroy, me.tabsMenu);

        me.generateTabMenuItems();
        
        var target = Ext.get(e.getTarget());
        var xy = target.getXY();

        //Y param + 24 pixels
        xy[1] += 24;

        me.tabsMenu.showAt(xy);
    },

    searchMenu:function(search){
    	var me =this,
    	menu  = me.tabsMenu,
    	menuItems = menu.items.items,
    	sum=0;
		Ext.Array.each(menuItems, function(item) {
			if (item.text.indexOf(search) != -1) {
				sum=sum+1;
				if (item.hidden) {
					item.show();
				}
				
			}else{
				if (!item.hidden) {
					item.hide();
				}
			}
		});
		menu.setHeight(29+16+27*sum);
    },
    // private
    generateTabMenuItems: function() {
        var me = this,
            tabPanel = me.tabPanel,
            curActive = tabPanel.getActiveTab(),
            totalItems = tabPanel.items.getCount(),
            pageSize = me.getPageSize(),
            numSubMenus = Math.floor(totalItems / pageSize),
            remainder = totalItems % pageSize,
            i, curPage, menuItems, x, item, start, index;
       

//        if (totalItems > pageSize) {
//
//            // Loop through all of the items and create submenus in chunks of 10
//            for (i = 0; i < numSubMenus; i++) {
//                curPage = (i + 1) * pageSize;
//                menuItems = [];
//
//                for (x = 0; x < pageSize; x++) {
//                    index = x + curPage - pageSize;
//                    item = tabPanel.items.get(index);
//                    menuItems.push(me.autoGenMenuItem(item));
//                }
//
//                me.tabsMenu.add({
//                    text: me.getMenuPrefixText() + ' ' + (curPage - pageSize + 1) + ' - ' + curPage,
//                    menu: menuItems
//                });
//            }
//            
//            // remaining items
//            if (remainder > 0) {
//                start = numSubMenus * pageSize;
//                menuItems = [];
//                for (i = start; i < totalItems; i++) {
//                    item = tabPanel.items.get(i);
//                    menuItems.push(me.autoGenMenuItem(item));
//                }
//                me.tabsMenu.add({
//                    text: me.menuPrefixText + ' ' + (start + 1) + ' - ' + (start + menuItems.length),
//                    menu: menuItems
//                });
//
//            }
//        }
//        else {
          tabPanel.items.each(function(item) {
        	/*if(item.id!=curActive.id){
        		me.tabsMenu.add(me.autoGenMenuItem(item));
        	}*/
          	me.tabsMenu.add(me.autoGenMenuItem(item));
          });
          if(me.itemClosable) {
          	me.tabsMenu.add({
	        	text:'关闭所有面板' ,
		        	style: {
		        	background: '#E5E5E5'
	            },
	        	handler : function(){
	        		// tabPanel.removeAll();
	        		tabPanel.items.each(function(item) {
			        	if(item.closable){
			        		tabPanel.remove(item);
	        			} 
	        		});
	        	}
          	});
          }
    },

    // private
    autoGenMenuItem: function(item) {
        var me = this,
        	maxText = this.getMaxText(),
        	tabPanel = me.tabPanel,
            text = Ext.util.Format.ellipsis(item.title, maxText),
            curActive = tabPanel.getActiveTab(),
            backIconCls = (me.itemClosable && item.closable) ? 'x-menuitem-close' : '';
            
        return {
            text: text,
            handler: this.showTabFromMenu,
            scope: this,
            disabled: item.disabled,
            tabToShow: item,
            iconCls: item.id==curActive.id ? 'x-icon-current' : '',
            backIconCls: backIconCls
        };
    },

    // private
    showTabFromMenu: function(menuItem) {
    	if(event.target && event.target.classList.contains('x-menuitem-close')) {
    		menuItem.tabToShow.close();
    	}else {
    		this.tabPanel.setActiveTab(menuItem.tabToShow);
    	}
    }
});

