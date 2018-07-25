Ext.define('erp.view.ma.SvnLog', {
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
					width: 1000,
					items: [{
						columnWidth: .2,
						fieldLabel: '系统当前版本',
						xtype: 'displayfield',
						id: 'svninfo'
					},{
						columnWidth: .2,
						fieldLabel: '程序最新版本',
						xtype: 'displayfield',
						id: 'svnlast'
					},{
						columnWidth: .2,
						boxLabel: '只显示未更新版本',
						xtype: 'checkbox',
						id: 'switch'
					},{
						columnWidth: .2,
						boxLabel: '只显示未评审版本',
						xtype: 'checkbox',
						id: 'unaudit'
					},{
						columnWidth: .2,
						boxLabel: '只显示未测试版本',
						xtype: 'checkbox',
						id: 'untest'
					},{
						columnWidth: .4,
						fieldLabel: '提交时间',
						labelWidth: 80,
						id: 'date',
						xtype: 'ftdatefield'
					},{
						xtype: 'fieldcontainer',
						columnWidth: .4,
						layout: 'hbox',
						defaults: {
							style: {
								marginLeft: '5px'
							}
						},
						items: [{
							xtype: 'button',
							text: '今天',
							param: ['d', 0, 'd', 0]
						},{
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
							text: '近三个月',
							param: ['m', -3, 'd', 0]
						},{
							xtype: 'button',
							text: '全部',
							param: ['y', -3, 'd', 0]
						}]
					},{
						xtype: 'textfield',
						fieldLabel: '注释',
						labelWidth: 50,
						columnWidth: .2,
						id: 'remark'
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
					text : '提交时间',
					dataIndex : 'date',
					width: 160,
					renderer: function(val) {
						return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
					}
				}, {
					text : '版本',
					dataIndex : 'version',
					width: 60
				}, {
					text : '提交人',
					dataIndex : 'man',
					width: 100
				}, {
					text : '注释',
					dataIndex : 'remark',
					width: 300,
					renderer: function(v, m, r) {
						if(r.get('test_result') === 0 && v.indexOf('\n') > -1) {
							var a = v.split('\n');
							return '<dl class="dl-horizontal"><dt>问题:</dt><dd>' + a[0] + '</dd><dt>内容:</dt><dd>' + a[1] + '</dd></dl>';
						}
						return v;
					}
				}, {
					text: '评审',
					columns: [{
						text: '人员',
						dataIndex : 'auditor',
						width: 80
					},{
						text: '评语',
						dataIndex : 'auditComment',
						width: 160
					}]
				}, {
					text: '测试',
					columns: [{
						text: '人员',
						dataIndex : 'tester',
						width: 80
					},{
						text: '结果',
						dataIndex : 'testResult',
						width: 50,
						renderer: function(v) {
							return v === 1 ? '<img src="' + basePath + 'resource/images/icon/agree.png">' :
								'<img src="' + basePath + 'resource/images/icon/unagree.png">';
						}
					},{
						text: '评语',
						dataIndex : 'testComment',
						width: 200
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
			fields : [ 'date', 'man', 'changed', 'remark', 'version', 'auditor', 'auditComment', 'tester', 'testResult', 'testComment' ],
			pageSize: 15,
			autoLoad: false,
			proxy : {
				type : 'ajax',
				url : basePath + 'ma/program/log.action',
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
					text : '查看修改内容',
					name: 'item-changedetail',
					iconCls: 'x-button-icon-content'
				},{
					xtype: 'menuseparator'
				},{
					text : '提交评审报告',
					name: 'item-audit',
					iconCls: 'x-button-icon-readed',
					disabled: true
				},{
					text : '提交测试报告',
					name: 'item-test',
					iconCls: 'x-button-icon-agree',
					disabled: true
				}]
			});
		}
		menu.showAt(e.getXY());
		menu.record = record;
	}
});