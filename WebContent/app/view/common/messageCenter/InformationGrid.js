Ext.define('erp.view.common.messageCenter.InformationGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.InformationGrid',
	id : 'informationgrid',
	defaults : {
		autoScroll : false
	},	
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [Ext.create('erp.view.core.plugin.GridMultiHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')];
			Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	gridFilters:{},
	listeners: {
		'headerfiltersapply': function(grid, filters) {
			grid.gridFilters=filters;
		}
	},
	configs : [{
				header : 'IH_ID',
				align : 'center',
				id:'IH_ID',
				dataIndex : 'IH_ID',
				hidden : true,
				group : 'all',
				filterJson_:{},
				filter:{
					dataIndex:"IH_ID",
					xtype:"textfield",
					filtertype:"",
					hideTrigger:false,
					queryMode:"local",
					displayField:"display",
					valueField:"value",
					store:null,
					autoDim:true,
					ignoreCase:false,
					exactSearch:false,
					args:null
			       }
			}, {
				header : 'IHD_ID',
				align : 'center',
				dataIndex : 'IHD_ID',
				id:'IHD_ID',
				hidden : true,
				group : 'all',
				filterJson_:{},
				filter:{
		                dataIndex:"IHD_ID",
		                xtype:"textfield",
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:null,
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			          }
			}, {
				header : 'CURRENTMASTER',
				align : 'center',
				id:'CURRENTMASTER',
				dataIndex : 'CURRENTMASTER',
				hidden : true,
				group : 'all',
				filterJson_:{},
				filter:{
		                dataIndex:"CURRENTMASTER",
		                xtype:"textfield",
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:null,
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			            }
			}, {
				header : '',
				width : 50,
				fixed : true,
				align : 'center',
				dataIndex : 'tt',
				id:'tt',
				xtype : 'checkcolumn',
				cls : 'x-grid-header-1',
				align : 'center',
				btnId:'recived',//我接收的情况下除了已读都展示勾选框
				except: 'recived&alreadyread',
				filterJson_: {},
				editor : {
					xtype : 'checkbox',
					cls : 'x-grid-checkheader-editor'
				},
				renderer : function(v, meta, record) {
					record.dirty = v;
					return '<input type="checkbox"'
							+ (v == true ? " checked" : "") + '/>';
				}
			}, {
				header : '',
				id:'readstatus',
				dataIndex : 'IHD_READSTATUS',
				width : 40,
				align : 'center',
				btnId: 'alreadyread',//只有已读展示
				filterJson_: {},
				renderer : function(v) {
					if (v == 0) {
						return '<div style="margin-top:8px;position:absolute;width:10px;height:10px;border:0px;background-color:#f34c4c;border-radius:5px;"></div>'
					} else {
						return '<div style="margin-top:8px;position:absolute;width:10px;height:10px;border:0px;background:gray;border-radius:5px;"></div>'
					}
				}
			}, {
				header : '接收人',
				align : 'left',
				id:'IHD_RECEIVE',
				dataIndex : 'IHD_RECEIVE',
				btnId : 'send',
				width : 100,
				filterJson_: {},
				filter:{
		                dataIndex:"IHD_RECEIVE",
		                xtype:"textfield",
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:null,
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			            }
			}, {
				align: 'left',
				header: '状态',
				id: 'IHD_READSTATUS',
				dataIndex: 'IHD_READSTATUS',
				btnId: 'allstatus',
				width: 70,
				xtype: 'combocolumn',
				filterJson_: {value:'-所有-'},
				filter: {
				        dataIndex: "IHD_READSTATUS",
				        xtype: "combo",
				        filterType: "",
	        			hideTrigger: false,
	        			queryMode: "local",
				        displayField: "display",
				        valueField: "value",
				        store: {
					        fields: ["display", "value"],
					        data: [{
							        "display": "未读",
							        "value": '0'
						          },
						          {
							        "display": "已读",
							        "value": '-1'
						          }
					            ]	         
				        },
				        autoDim: true,
				        ignoreCase: false,
						exactSearch: false,
						args: null
				},
				renderer: function(v) {
				    if(v == '0') {//修改状态图位置=>居中
				        return '<div style="margin-top:8px;margin-left: 20px;width:10px;height:10px;border:0px;background-color:#f34c4c;border-radius:5px;"></div>'
			        } else {
				          return '<div style="margin-top:8px;margin-left: 20px;width:10px;height:10px;border:0px;background:gray;border-radius:5px;"></div>'
			        }
		        }
			}, {
				header : '发起人',
				align : 'left',
				dataIndex : 'IH_CALL',
				except : 'send',
				width : 100,
				id:'IH_CALL',
				filterJson_: {},
				filter:{
		                dataIndex:"IH_CALL",
		                xtype:"textfield",
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:null,
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			            }
			},{
				align : 'left',
				header : '发起时间',
				dataIndex : 'IH_DATE',
				id:'IH_DATE',
				except : 'send',
				width : 150,
				filterJson_: {value:'',type:''},
				filter:{
		                dataIndex:"IH_DATE",
		                xtype: "datefield",
						format: 'Y-m-d H:i:s',
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:null,
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			            }
			},{
				align : 'left',
				header : '接收时间',
				id:'IH_DATE',
				dataIndex : 'IH_DATE',
				btnId : 'send',
				width : 150,
				filterJson_: {value:'',type:''},
				filter:{
		                dataIndex:"IH_DATE",
		                xtype: "datefield",
						format: 'Y-m-d H:i:s',
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:null,
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			            }
			}, {
				align : 'left',
				header : '消息分类',
				dataIndex : 'IH_FROM',
				id:'IH_FROM',
				xtype: 'combocolumn',
				width : 100,
				group : 'all',
				filterJson_: {value:'-所有-'},
				filter:{
		                dataIndex:"IH_FROM",
		                xtype: "combo",
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:{
		                	
				fields: ["display", "value"],
				data: [{
					"display": "知会消息",
					"value": "system"
				}, {
					"display": "CRM提醒",
					"value": "crm"
				}, {
					"display": "通知公告",
					"value": "note"
				}, {
					"display": "考勤提醒",
					"value": "kpi"
				}, {
					"display": "会议",
					"value": "meeting"
				}, {
					"display": "审批",
					"value": "process"
				}, {
					"display": "任务",
					"value": "task"
				}, {
					"display": "稽核提醒",
					"value": "job"
				}, {
					"display": "普通知会",
					"value": 'ptzh'
				}]
		                },
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			            },
				renderer : function(v) {
					switch (v) {
						case 'system' :
							return '知会消息';
							break;
						case 'crm' :
							return 'CRM提醒';
							break;
						case 'note' :
							return '通知公告';
							break;
						case 'kpi' :
							return '考勤提醒';
							break;
						case 'meeting' :
							return '会议';
							break;
						case 'process' :
							return '审批';
							break;
						case 'task' :
							return '任务';
							break;
						case 'job' :
							return '稽核提醒';
							break;
						case 'b2b' :
							return 'B2B提醒';
							break;
						case 'ptzh' :
							return '普通知会';
							break;
					}

				}

			}, {
				align : 'left',
				id : 'context',
				header : '信息详情',
				id:'IH_CONTEXT',
				dataIndex : 'IH_CONTEXT',
				flex : 2,
				style : 'text-align:left',
				group : 'all',
				filterJson_:{},
				filter:{
		                dataIndex:"IH_CONTEXT",
		                xtype:"textfield",
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:null,
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			            },
				renderer : function(v) {
					return v.replace(/font-size\s*:\s*\d+\s*p[tx]/g,'font-size:14px').replace(/javascript:openUrl\(/g,"javascript:openTmpUrl('',").replace(/javascript:parent.openUrl\(/g,"javascript:openTmpUrl('patrnt',").replace(/openMessageUrl/g,'openTmpMessageUrl');
				}
			}, {
				align : 'left',
				header : '阅读时间',
				id:'IHD_READTIME',
				dataIndex : 'IHD_READTIME',
				width : 150,
				except : 'unread',
				filterJson_: {value:'',type:''},
				filter:{
		                dataIndex:"IHD_READTIME",
		                xtype: "datefield",
						format: 'Y-m-d H:i:s',
		                filtertype:"",
		                hideTrigger:false,
		                queryMode:"local",
		                displayField:"display",
		                valueField:"value",
		                store:null,
		                autoDim:true,
		                ignoreCase:false,
		                exactSearch:false,
		                args:null
			            }			
			}],
	store : Ext.create('Ext.data.Store', {
				storeId : 'myStore',
				pageSize : 500,
				fields : [{
							name : 'IH_ID',
							type : 'string'
						}, {
							name : 'IHD_ID',
							type : 'string'
						}, {
							name : 'IHD_READSTATUS',
							type : 'string'
						}, {
							name : 'IH_CALL',
							type : 'string'
						}, {
							name : 'IHD_RECEIVE',
							type : 'string'
						}, {
							name : 'IH_DATE',
							type : 'string'
						}, {
							name : 'IH_FROM',
							type : 'string'
						}, {
							name : 'IH_CONTEXT',
							type : 'string'
						}, {
							name : 'IHD_READTIME',
							type : 'string'
						}, {
							name : 'CURRENTMASTER',
							type : 'string'
						}

				],
				autoLoad : false,
				proxy : {
					type : 'ajax',
					url : basePath + 'common/getMessageData.action',					
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'total'
					},
					actionMethods: {
	            			read   : 'POST'
	       	 		}
				},
				listeners : {
					beforeload : function() {
						var grid = Ext.getCmp("informationgrid");
						var form = Ext.getCmp('informationform');
						Ext.apply(grid.getStore().proxy.extraParams, {
									condition : form.defaultCondition,
									likestr:form.likestr
								});
					}
				}
			}),
	columns : [],
	reconfigureColumn : function(group) {
		var columns = this.configs;
		var newColumn = new Array();
		var grid = Ext.getCmp("informationgrid");
		var currentColumns = grid.columns;
		//去除已经存在的combocolumn中过滤器store中的“-所有-”和“-无-”避免重复添加
		if(currentColumns.length>0){
		Ext.Array.each(currentColumns,function(currentColumn,index1){
			for(var index2 =0;index2< columns.length;index2++){//如果下拉框已经被渲染,则去掉filter的store中data前两位（“-所有-”和“-无-”）
				if(columns[index2].dataIndex===currentColumn.dataIndex&&currentColumns[index1].xtype==='combocolumn'){
						grid.columns[index1].filter.store.data.splice(0,2);
					break;
				}
			}
		});
		}
		Ext.Array.each(columns, function(item) {
					if (item.group == 'all') {
						newColumn.push(item);
					} else {
						if (item.except && !item.btnId && group.indexOf(item.except) == -1) {							
								newColumn.push(item);							
						}else if (item.btnId && !item.except && group.indexOf(item.btnId) > -1) {
							   newColumn.push(item);
						}else if (item.btnId && item.except && group.indexOf(item.btnId) > -1 && group.indexOf(item.except) == -1) {
							   newColumn.push(item);
						}
					}
				});	
		if(this.headerFilterPlugin)
        this.headerFilterPlugin.destroyFilters();       
		this.reconfigure(null, newColumn);	
		this.ownerCt.fireResize();

		if (this.getWidth()>10){
			this.setWidth(this.getWidth()-1);
		}
		if(this.headerFilterPlugin){
			this.headerFilterPlugin.adjustFilterWidth();	
		}
		var readBtn = Ext.getCmp('readBtn');
		if (readBtn) {//当全部和未读时可供手动设置已读
			if((group.indexOf('unread') > -1 && group.indexOf('recived') > -1) || (group.indexOf('allstatus') > -1)) {
				Ext.getCmp('readBtn').show();
			} else {
				Ext.getCmp('readBtn').hide();
			}
		}
	},
	dockedItems : [{
		xtype : 'pagingtoolbar',
		dock : 'bottom',
		displayInfo : true,
		store : Ext.data.StoreManager.lookup('myStore'),
		displayMsg : "显示{0}-{1}条数据，共{2}条数据",
		beforePageText : '第',
		afterPageText : '页,共{0}页',
		listeners : {
			afterrender : function(toolbar) {
				var tbfill;
				Ext.Array.each(toolbar.items.items, function(item) {
							if (item.xtype == 'tbfill') {
								tbfill = item;
								return false;
							}
						});
				toolbar.remove(tbfill);

				toolbar.insert(0, {
					xtype : 'tbtext',
					id : 'toolbarDisplayField',
					margin : 0,
					padding : 0,
					text : '<span class="tags-flag" style="background:gray;border-radius:5px;"></span><span class="tags-font">已读</span>'
							+ '<span class="tags-flag" style="background:red;border-radius:5px;"></span><span class="tags-font">未读</span>'
				});
				toolbar.insert(1, {
							xtype : 'button',
							id : 'readBtn',
							text : '设为已读',
							cls : 'readbutton'

						});
				toolbar.insert(2, {
							xtype : 'tbfill'
						});
			}
		}
	}],
    updateReadstatus:function(data){
    	var me = this;
    	   Ext.Ajax.request({
                url: basePath + "common/updateReadstatus.action",
                params: {
                    data: data     
                },
                method: 'post',
                callback: function(options, success, response) {
                    var res = Ext.decode(response.responseText); 
            		if (res.exceptionInfo) {
						showError(res.exceptionInfo);
						return;
					}				
                    if (res.success) { 
                    var grid = Ext.getCmp('informationgrid');
                	grid.store.loadPage(1,{
						callback:me.callbackFns
					});	
                    }	
               }
            });
    }, callbackFns:function(options,response,success){
		var res = Ext.decode(response.response.responseText);
		if(res.success){
			var form = Ext.getCmp('informationform');
			var statBtns = form.query('erpStatButton');
			Ext.Array.each(statBtns,function(btn){
				
					if(res.count[btn.type]>=0){
						var count = res.count[btn.type];
						btn.setStat(count);
						if(count>99){
							btn.setTooltip(count);
						}					
					}					
				
			});
		}else if(res.exceptionInfo){
			showError(res.exceptionInfo);
		}
		var a = Ext.get('informationgrid').select('a');
		var elements = a.elements;
		for (var i = 0; i < elements.length; i++) {
			elements[i].onclick = function() {
				var informationgrid = Ext.getCmp('informationgrid');
				informationgrid.flag = 1;
			}
		}
	},
	initComponent : function() {
		this.callParent(arguments);
	}
});