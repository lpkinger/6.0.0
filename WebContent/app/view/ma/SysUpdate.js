Ext.define('erp.view.ma.SysUpdate', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this, store = me.getStore();
		Ext.apply(me, {
			items : [ {
				xtype: 'form',
				layout: {
					align: 'center',
					pack: 'center',
					type: 'vbox'
				},
				anchor : '100% 15%',
				bodyStyle: 'background:#f1f2f5;',
				items: [{
					xtype: 'fieldcontainer',
					layout: 'column',
					width: window.innerWidth,
					items: [{
						columnWidth: .3,
						fieldLabel: '创建时间',
						labelWidth: 80,
						id: 'date',
						xtype: 'ftdatefield'
					},{
						xtype: 'fieldcontainer',
						columnWidth: .3,
						layout: 'hbox',
						defaults: {
							style: {
								marginLeft: '5px'
							}
						},
						items: [{
							xtype: 'button',
							text: '近三天',
							param: ['d', -2, 'd', 0]
						},{
							xtype: 'button',
							text: '近一周',
							param: ['d', -6, 'd', 0]
						},{
							xtype: 'button',
							text: '近一个月',
							param: ['m', -1, 'd', 0]
						},{
							xtype: 'button',
							text: '全部',
							param: ['y', -3, 'd', 0]
						}]
					},{
						xtype: 'textfield',
						fieldLabel: '方案描述',
						labelWidth: 70,
						columnWidth: .3,
						id: 'title'
					}]
				}],
				buttonAlign: 'center',
				buttons: [{
					cls : 'x-btn-blue',
					id : 'refresh',
					text : $I18N.common.button.erpRefreshButton,
					width : 80,
					margin : '0 0 0 5'
				}, {
					cls : 'x-btn-blue',
					id : 'close',
					text : $I18N.common.button.erpCloseButton,
					width : 80,
					margin : '0 0 0 5'
				}]
			},{
				xtype : 'grid',
				id : 'grid',
				cls: 'custom-grid-autoheight',
				anchor : '100% 85%',
				columns : [ {
					text : '方案',
					dataIndex : 'title',
					width: 330
				},{
					text : '创建时间',
					dataIndex : 'createDate',
					width: 150,
					renderer: function(val) {
						return Ext.Date.format(new Date(val), 'Y-m-d H:i');
					},
					align: 'center'
				},{
					text : '最近修改',
					dataIndex : 'modifyDate',
					width: 150,
					renderer: function(val) {
						return val ? Ext.Date.format(new Date(val), 'Y-m-d H:i') : '';
					},
					align: 'center'
				}, {
					text : '版本',
					dataIndex : 'version',
					width: 80,
					align: 'center'
				}, {
					text : '推荐模式',
					dataIndex : 'installType',
					width: 80,
					renderer: function(val) {
						return val == 'COVER' ? '覆盖' : '修复';
					},
					align: 'center'
				}, {
					text : '我的系统',
					width: 230,
					columns: [{
						text: '升级时间',
						dataIndex: 'install_date',
						width: 150,
						renderer: function(val) {
							return val ? Ext.Date.format(new Date(val), 'Y-m-d H:i') : '';
						},
						align: 'center'
					},{
						text: '版本',
						dataIndex: 'install_version',
						width: 80,
						align: 'center'
					},{
			            xtype: 'actioncolumn',
			            width: 50,
			            align: 'center',
			            items: [{
			            	icon: basePath + 'resource/ext/resources/themes/images/gray/shared/right-btn.gif',
			                tooltip: '升级',
			            	handler: function(view, rowIndex, colIndex, opts, e) {
//			                    grid.fireEvent('action', grid, grid.getStore().getAt(rowIndex));
			                    me.onContextmenu(view, view.getStore().getAt(rowIndex), e);
			                }
			            }]
			        }]
				} ],
				columnLines : true,
				enableColumnResize : true,
				store : store,
				dockedItems: [{
			        xtype: 'pagingtoolbar',
			        store: store, 
			        dock: 'bottom',
			        displayInfo: true,
			        moveFirst : function(){
			            if (this.fireEvent('beforechange', this, 1) !== false){
			                this.store.loadPage(1, {
			                	params: {
				                	condition : this.ownerCt.condition
				                }
			                });
			            }
			        },
			        movePrevious : function(){
			            var me = this,
			                prev = me.store.currentPage - 1;

			            if (prev > 0) {
			                if (me.fireEvent('beforechange', me, prev) !== false) {
			                    this.store.previousPage({
				                	params: {
				                		condition : this.ownerCt.condition
				                	}
			                	});
			                }
			            }
			        },
			        moveNext : function(){
			            var me = this,
			                total = me.getPageData().pageCount,
			                next = me.store.currentPage + 1;

			            if (next <= total) {
			                if (me.fireEvent('beforechange', me, next) !== false) {
			                    me.store.nextPage({
				                	params: {
				                		condition : this.ownerCt.condition
				                	}
			                	});
			                }
			            }
			        },
			        moveLast : function(){
			            var me = this,
			                last = me.getPageData().pageCount;

			            if (me.fireEvent('beforechange', me, last) !== false) {
			                me.store.loadPage(last, {
				                params: {
				                	condition : this.ownerCt.condition
				                }
			                });
			            }
			        },
			        doRefresh : function(){
			            var me = this,
			                current = me.store.currentPage;

			            if (me.fireEvent('beforechange', me, current) !== false) {
			                me.store.loadPage(current, {
				                params: {
				                	condition : this.ownerCt.condition
				                }
			                });
			            }
			        }
			    }],
			    viewConfig: {
			    	listeners: {
			    		itemcontextmenu: function(view, record, item, index, e) {
	    					me.onContextmenu(view, record, e);
	    				}
			    	}
			    }
			} ]
		});
		me.callParent(arguments);
	},
	getStore: function() {
		return Ext.create('Ext.data.Store', {
			fields : [ 'id', 'title', 'createDate', 'modifyDate', 'version', 'installType', 'install', 'install_version', 'install_date' ],
			pageSize: 15,
			autoLoad: false,
			proxy : {
				type : 'ajax',
				url : basePath + 'ma/upgrade/plan.action',
				reader : {
					type : 'json',
					root : 'content',
					totalProperty: 'totalElements'
				}
			}
		});
	},
	onContextmenu: function(view, record, e) {
		e.preventDefault();
		var menu = view.contextMenu;
		if (!menu) {
			menu = view.contextMenu = new Ext.menu.Menu({
				items: [{
					text : '查看方案',
					name: 'item-find',
					iconCls: 'x-button-icon-content'
				},{
					xtype: 'menuseparator'
				},{
					text : '覆盖式升级',
					name: 'item-cover',
					iconCls: 'x-button-icon-content'
				},{
					text : '修复式升级',
					name: 'item-repair',
					iconCls: 'x-button-icon-readed'
				}]
			});
		}
		menu.showAt(e.getXY());
		menu.record = record;
	}
});