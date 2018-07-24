Ext.define('erp.view.ma.SysCheckGrid',{
					extend : 'Ext.grid.Panel',
					alias : 'widget.erpSysCheckGrid',
					requires : [ 'erp.view.core.toolbar.Toolbar' ],
					layout : 'fit',
					id : 'sysgrid',
					emptyText : $I18N.common.grid.emptyText,
					columnLines : true,
					autoScroll : true,
					animCollapse : false,
					columns : [],
					store : [],
					bodyStyle : 'background-color:#f1f1f1;',
					plugins : [ {
						ptype : 'rowexpander',
						rowBodyTpl : [ '<p><b>包含信息:</b> {details}</p>', ]
					} ],
					features : [ {
						ftype : 'summary',
						showSummaryRow : false,// 不显示默认合计行
						generateSummaryData : function() {
							var me = this, data = {}, store = me.view.store, columns = me.view.headerCt
									.getColumnsForTpl(), i = 0, length = columns.length,
							// fieldData,
							// key,
							comp;
							// 将feature的data打印在toolbar上面
							for (i = 0, length = columns.length; i < length; ++i) {
								comp = Ext.getCmp(columns[i].id);
								data[comp.id] = me
										.getSummary(store, comp.summaryType,
												comp.dataIndex, false);
								var tb = Ext.getCmp(columns[i].dataIndex + '_'
										+ comp.summaryType);
								if (tb) {
									var val = data[comp.id];
									if (columns[i].xtype == 'numbercolumn') {
										val = Ext.util.Format.number(val,(columns[i].format || '0,000.000'));
									}
									tb.setText(tb.text.split(':')[0] + ':'
											+ val);
								}
							}
							return data;
						}
					} ],
					bbar : {
						xtype : 'erpToolbar',
						id:'systoolbar',
						listeners : {
							afterrender : function(bar) {
								bar.removeAll();
								bar.add('-', {
									text : '合计栏',
									xtype : 'tbtext',
								}, {
									xtype : 'splitter',
									width : 10
								}, {
									id : 'warncount_sum',
									itemId : 'warncount',
									xtype : 'tbtext',
									text : '提醒:0',
									margin : '0 0 10 0'
								}, {
									xtype : 'splitter',
									width : 10
								}, {
									id : 'publishcount_sum',
									itemId : 'publishcount',
									xtype : 'tbtext',
									text : '处罚:0',
									margin : '0 0 10 0'
								}, {
									xtype : 'splitter',
									width : 10
								}, {
									id : 'publishamountcount_sum',
									itemId : 'publishamountcount',
									xtype : 'tbtext',
									text : '处罚分数:0',
									margin : '0 0 10 0'
								});
							}
						}
					},
					GridUtil : Ext.create('erp.util.GridUtil'),
					BaseUtil : Ext.create('erp.util.BaseUtil'),
					necessaryField : '',// 必填字段
					detno : '',// 编号字段
					keyField : '',// 主键字段
					mainField : '',// 对应主表主键的字段
					dbfinds : [],
					caller : null,
					condition : null,
					initComponent : function() {
						this.getStore(this);
						this.callParent(arguments);
					},
					getStore : function(grid) {
						var columns = [ {
							name : 'orgid',
							dataIndex : 'orgid',
							id : 'orgid',
							header : '组织ID',
							width : 0,
							cls : 'x-grid-header-1'
						}, {
							name : 'orgname',
							id : 'orgname',
							dataIndex : 'orgname',
							header : '对象名称',
							flex:1,
							cls : 'x-grid-header-1'
						}, {
							name : 'warncount',
							id : 'warncount',
							dataIndex : 'warncount',
							header : '提醒总数',
							summaryType : 'sum',
							align:'right',
							cls : 'x-grid-header-1',
						}, {
							name : 'publishcount',
							id : 'publishcount',
							dataIndex : 'publishcount',
							header : '处罚总数',
							align:'right',
							summaryType : 'sum',
							cls : 'x-grid-header-1'
						}, {
							name : 'publishamountcount',
							id : 'publishamountcount',
							dataIndex : 'publishamountcount',
							header : '处罚分总数',
							align:'right',
							summaryType : 'sum',
							cls : 'x-grid-header-1'
						}, {
							name : 'orgheader',
							id : 'orgheader',
							dataIndex : 'orgheader',
							header : '组织负责人',
							cls : 'x-grid-header-1'
						}, {
							name : 'details',
							id : 'details',
							dataIndex : 'details',
							header : '详细信息',
							width:0,
							cls : 'x-grid-header-1'
						} ];
						Ext.Ajax.request({
									url : basePath+ 'ma/SysCheck/getDataByOrg.action',
									method : 'post',
									params : {
										_noc : 1,
										parentid : 0,
									},
									async : false,
									callback : function(options, success,
											response) {
										var res = new Ext.decode(
												response.responseText);
										if (res.exceptionInfo != null) {
											showError(res.exceptionInfo);
											return;
										} else {
											if (res.success) {
												var store = Ext
														.create(
																'Ext.data.Store',
																{
																	fields : [
																			{
																				type : 'int',
																				name : 'orgid'
																			},
																			{
																				type : 'string',
																				name : 'orgname'
																			},
																			{
																				type : 'int',
																				name : 'warncount'
																			},
																			{
																				type : 'int',
																				name : 'publishcount'
																			},
																			{
																				type : 'int',
																				name : 'publishamountcount'
																			},
																			{
																				type : 'string',
																				name : 'orgheader'
																			},
																			{
																				type : 'string',
																				name : 'details'
																			} ],
																	data : new Ext.decode(
																			res.data)
																});
												grid.columns = columns;
												grid.store = store;
											}
										}
									}
								});

					},
					getEffectiveData : function() {
						var me = this;
						var effective = new Array();
						var s = this.store.data.items;
						for ( var i = 0; i < s.length; i++) {
							var data = s[i].data;
							if (data[me.keyField] != null
									&& data[me.keyField] != "") {
								effective.push(data);
							}
						}
						return effective;
					},
					loadNewStore:function(grid,param){
						var me = this;
						var main = parent.Ext.getCmp("content-panel");
						if(!main)
							main = parent.parent.Ext.getCmp("content-panel");
						if(main){
							main.getActiveTab().setLoading(true);//loading...
						}
						Ext.Ajax.request({//拿到grid的columns
				        	url : basePath +'ma/SysCheck/getDataByOrg.action',
				        	params: param,
				        	method : 'post',
				        	callback : function(options,success,response){
				        		if(main){
				        			main.getActiveTab().setLoading(false);
				        		}
				        		var res = new Ext.decode(response.responseText);
				        		if(res.exceptionInfo){
				        			showError(res.exceptionInfo);return;
				        		}
				        		var data = [];
				        		if(!res.data || res.data.length == 2){
				        			grid.store.removeAll();
				        		} else {
				        			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
				        			grid.store.loadData(data);
				        		}
				        		//自定义event
				        		grid.addEvents({
				        		    storeloaded: true
				        		});
				        		grid.fireEvent('storeloaded', grid, data);
				        	}
				        });
					},
					setReadOnly : function(bool) {
						this.readOnly = bool;
					},
					reconfigure : function(store, columns) {
						var d = this.headerCt;
						if (this.columns.length <= 1 && columns) {
							d.suspendLayout = true;
							d.removeAll();
							d.add(columns);
						}
						if (store) {
							try {
								this.bindStore(store);
							} catch (e) {

							}
						} else {
							this.getView().refresh();
						}
						if (columns) {
							d.suspendLayout = false;
							this.forceComponentLayout();
						}
						this.fireEvent("reconfigure", this);
					},
					/**
					 * Grid上一条
					 */
					/*
					 * prev: function(grid, record){ grid = grid ||
					 * Ext.getCmp('grid'); record = record ||
					 * grid.selModel.lastSelected; if(record){ //递归查找上一条，并取到数据
					 * var d = grid.store.getAt(record.index - 1); if(d){ try {
					 * grid.selModel.select(d); return d; } catch (e){ } } else {
					 * if(record.index - 1 > 0){ return this.prev(grid, d); }
					 * else { return null; } } } },
					 *//**
						 * Grid下一条
						 */
					/*
					 * next: function(grid, record){ grid = grid ||
					 * Ext.getCmp('grid'); record = record ||
					 * grid.selModel.lastSelected; if(record){ //递归查找下一条，并取到数据
					 * var d = grid.store.getAt(record.index + 1); if(d){ try {
					 * grid.selModel.select(d); return d; } catch (e){ } } else {
					 * if(record.index + 1 < grid.store.data.items.length){
					 * return this.next(grid, d); } else { return null; } } } },
					 */
					listeners : {
						afterrender : function(grid) {
							var me = this;
							if (Ext.isIE) {
								document.body
										.attachEvent(
												'onkeydown',
												function() {
													if (window.event.ctrlKey
															&& window.event.keyCode == 67) {// Ctrl
														// + C
														var e = window.event;
														if (e.srcElement) {
															window.clipboardData
																	.setData(
																			'text',
																			e.srcElement.innerHTML);
														}
													}
												});
							} else {
								document.body
										.addEventListener(
												"mouseover",
												function(e) {
													if (Ext.isFF5) {
														e = e || window.event;
													}
													window.mouseoverData = e.target.value;
												});
								document.body
										.addEventListener(
												"keydown",
												function(e) {
													if (Ext.isFF5) {
														e = e || window.event;
													}
													if (e.ctrlKey
															&& e.keyCode == 67) {
														me
																.copyToClipboard(window.mouseoverData);
													}
													if (e.ctrlKey
															&& e.keyCode == 67) {
														me
																.copyToClipboard(window.mouseoverData);
													}
												});
							}
						}
					},
					copyToClipboard : function(txt) {
						if (window.clipboardData) {
							window.clipboardData.clearData();
							window.clipboardData.setData('text', txt);
						} else if (navigator.userAgent.indexOf('Opera') != -1) {
							window.location = txt;
						} else if (window.netscape) {
							try {
								netscape.security.PrivilegeManager
										.enablePrivilege('UniversalXPConnect');
							} catch (e) {
								alert("您的firefox安全限制限制您进行剪贴板操作，请打开'about:config'将signed.applets.codebase_principal_support'设置为true'之后重试");
								return false;
							}
						}
					}
				});