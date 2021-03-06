Ext.QuickTips.init();
Ext.define('erp.controller.common.messageCenter.JProcessCenter', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'common.messageCenter.JProcessCenter','common.messageCenter.JProcessCenterFormPanel','common.messageCenter.JProcessCenterGridPanel','core.button.StatButton','core.button.SwitchButton','core.button.ProcessRemind','core.form.BtnDateField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpJProcessCenterGridPanel':{
    			afterrender:function(grid){
    				me.reconfigureGrid(grid);						
					me.loadGridData();
    			},
    			'headerfiltersapply': function() {//触发筛选时调用函数
					me.headerfiltersapplyFn();
				}
    		},
    		'erpJProcessCenterFormPanel':{
    			afterrender:function(form){
    				var tab = me.FormUtil.getActiveTab();
    				//var condition = me.getCondition();
					me.onActivateEvent(tab); //绑定onactivate事件
					var width = (form.getWidth()/2) - 128;
					form.add({
						xtype:'displayfield',
						id:'msgNotice',
						renderTo:Ext.getBody(),
						html:'您有新的消息，点击此处即会刷新列表',
						height:30,
						width:221,
						hidden:true,
						style:'font-size:12px;top:0px;left:'+width+'px;height:35px!important;background-color:#ec971f;padding:8px;color:white;position:fixed;z-index:1;width:256px!important;cursor:pointer'		
			    	});
			    	//me.addSearchField(form);
    			}
    		},
    		'erpStatButton':{
				click:function(btn){	
					var grid = Ext.getCmp('jprocessGrid');
					me.reconfigureGrid(grid);						
					me.loadGridData();						
				}
    		},
    		'checkbox[id=checkBtn]':{
    			change:function(t){
    				Ext.getCmp('undoCheck').selectAll('jprocessGrid','undoCheck',t.value);
    			}
    		},
    		'button[id=remindBtn]' : {
    			click : function(btn) {
    				var multiselected = [];
    				var grid =Ext.getCmp('jprocessGrid'),items = grid.store.data.items;
    				Ext.each(items, function(item){
    					if(item.data['undoCheck'] == true) {
    						multiselected.push(item);
    					}
    				});
    				var records = Ext.Array.unique(multiselected);
    				if (records.length > 0) {
    					var params = new Object();
    					params.caller = 'Process!Remind';
    					var data = new Array();
    					var bool = false;
    					Ext.each(records, function(record, index) {
    						var o = new Object();
    						o['jp_nodeId'] = record.data['JP_NODEID'];
    						o['dealpersoncode'] = record.data['JP_NODEDEALMAN'];
    						o['jp_name']= record.data['JP_NAME'];
    						o['jp_codevalue']= record.data['JP_CODEVALUE'];
    						data.push(o);
    						bool = true;
    					});
    					if (bool && !me.dealing) {
    						params.data = unescape(Ext.JSON.encode(data)
    								.replace(/\\/g, "%"));
    						grid.setLoading(true);// loading...
    						Ext.Ajax.request({
    							url : basePath + 'common/remindProcess.action',
    							params : params,
    							method : 'post',
    							callback : function(options, success, response) {
    								grid.setLoading(false);
    								var localJson = new Ext.decode(response.responseText);
    								if (localJson.exceptionInfo) {
    									var str = localJson.exceptionInfo;
    									if (str.trim().substr(0, 12) == 'AFTERSUCCESS') {
    										str = str.replace('AFTERSUCCESS',
    												'');
    										multiselected = [];
    									}
    									showError(str);
    									return;
    								}
    								if (localJson.success) {
    									if (localJson.log) {
    										showMessage("提示", localJson.log);
    									}
    									multiselected = [];
    								}
    							}
    						});
    					} else {
    						showError("没有需要处理的数据!");
    					}
    				} else {
    					showError("请先勾选需催办的流程!");
    				}
    			}
    		},
    		'displayfield[id=msgNotice]':{
				afterrender:function(dfield){
					var fieldDom = dfield.el.dom;
					var form = Ext.getCmp('centerForm');
					
					//dfield.el.setX((document.body.clientWidth/2) - 128 );
					dfield.el.setX((form.getWidth()/2) - 128 ); //定位到form的中心
					dfield.el.setY(-2);
					var grid = Ext.getCmp('jprocessGrid');
					fieldDom.onclick = function(){
						var msgNotice = Ext.getCmp('msgNotice');
						msgNotice.hidden = true;
						Ext.getCmp('msgNotice').el.slideOut('t', { duration: 0 });
						
						grid.store.load({
							callback:me.callbackFn
						});
					};
				}        			
    		}
    	});
    },
    reconfigureGrid:function(grid){
    	var me = this;
		var group = me.getGroup();
		grid.reconfigureColumn(group);       	
    },
    getGroup:function(){
    	var group = '';
		var form = Ext.getCmp('centerForm');
		var switchButtons = form.query('erpSwitchButton');					
		if(switchButtons.length>0){
			Ext.Array.each(switchButtons,function(item){
					group += '&' + item.activeButton.id;	
			});							
		}
		return group.substring(1);        	
    },
    loadGridData:function(){
    	var me = this;
		var type = me.getType();
		var grid = Ext.getCmp('jprocessGrid');
		grid.store.loadPage(1,{
			callback:me.callbackFn
		});				
    },
    callbackFn:function(options,response,success){
		var res = Ext.decode(response.response.responseText);
		if(res.success){
			var form = Ext.getCmp('centerForm');
			var statBtns = form.query('erpStatButton');
			Ext.Array.each(statBtns,function(btn){
				if(btn.type=='toDo'||btn.type=='toLaunch'){
					if(res[btn.type + 'Count']||res[btn.type + 'Count']>=0){
						var count = res[btn.type + 'Count'];
						btn.setStat(count);
						if(count>99){
							btn.setTooltip(count);
						}					
					}					
				}
			});
		}else if(res.exceptionInfo){
			showError(res.exceptionInfo);
		}
	},
    getType:function(){
    	var type = '';
		var form = Ext.getCmp('centerForm');
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
    	var grid = Ext.getCmp('jprocessGrid');
		tab.on('activate',function(tab){
			//进行刷新		
			Ext.getCmp('jprocessGrid').store.loadPage(1,{
				callback:me.callbackFn
			});						
		});  
    },
    addSearchField:function(form){
		form.add({
			xtype:'combo',
			emptyText:'请输入搜索关键词',
			id:'search',
			triggerCls:'custom-rest',
			width:200,
			height:25,
			store:Ext.data.StoreManager.lookup('myStore'),
			queryMode: 'local',
			enableKeyEvents:true,
			listConfig: {
                getInnerTpl: function() {
                    return '<div style="padding: 5px 10px;">' + 
                    			'<span style="font-size:120%;">' +
                    				'<tpl if="JP_NAME">{JP_NAME}<tpl else>{JP_LAUNCHERNAME}</tpl>' +
                    			'</span>' + 
                    			'<span style="float:right;">{JP_LAUNCHERNAME}</span>' + 
                    		'</div>';
                }
            },
            doLocalQuery: function(queryPlan) {
                var me = this,
                    queryString = queryPlan.query;

                // Create our filter when first needed
                if (!me.queryFilter) {
                    // Create the filter that we will use during typing to filter the Store
                    me.queryFilter = new Ext.util.Filter({
                        id: me.id + '-query-filter',
                        anyMatch: true,
                        caseSensitive: me.caseSensitive,
                        root: 'data',
                        property: "JP_NAME|JP_LAUNCHERNAME",
                        createFilterFn: function() {
		    		        var me       = this,
		    		            matcher= me.createValueMatcher(),
		    		            property = me.property;
		    		            property1= me.property.split('|')[0];
		    		            property2= me.property.split('|')[1];
		    		            me.matcher=matcher;
		    		            
		    		        if (me.operator) {
		    		            return me.operatorFns[me.operator];
		    		        } else {
		    		            return function(item) {
		    		                var record = me.getRoot(item),value1=record[property1],value2=record[property2];	
		    		                return matcher === null ? value === null : matcher.test(value1) || matcher.test(value2) ;
		    		            };
		    		        }
		    		    }
                    });
                    me.store.addFilter(me.queryFilter, false);
                }
                if (queryString || !queryPlan.forceAll) {
                    me.queryFilter.disabled = false;
                    me.queryFilter.setValue(me.enableRegEx ? new RegExp(queryString) : queryString);
                }
                else {
                    me.queryFilter.disabled = true;
                }
                me.store.filter();
                if (me.store.getCount()) {
                    me.expand();
                } else {
                    me.collapse();
                }

                me.afterQuery(queryPlan);
            },
			listeners :{
				render:function(c){										   
					c.bodyEl.applyStyles('border:solid 1px rgb(181, 184, 200);');
					c.inputEl.applyStyles('border-width:0;background:0px 0px repeat-x white;');
				},
				change:function(c,newvalue){
					if(newvalue) c.getEl().down("." + c.triggerCls).applyStyles({visibility: 'visible'});
					else {
						c.getEl().down("." + c.triggerCls).applyStyles({visibility: 'hidden'});
						Ext.getCmp('jprocessGrid').getStore().clearFilter();

					}
				},
				select:function(combo,records){
					var grid=Ext.getCmp('jprocessGrid'),record=records[0],property='JP_NAME',value;
					if(combo && combo.queryFilter.matcher){
						if(combo.queryFilter.matcher.test(record.get('JP_NAME'))){
							property='JP_NAME',value=record.get('JP_NAME');
							combo.setValue(value);
						}else {
							
							property='JP_LAUNCHERNAME',value=record.get('JP_LAUNCHERNAME');
						}
						
					}
					if(!value) value=record.get('JP_NAME');
					combo.setValue(value);
					grid.getStore().filter([{property: property, value:value}]);							 
				},
				keypress:function(f,e){
				    if(e.keyCode == e.ENTER){
				    	var grid=Ext.getCmp('jprocessGrid');
				    	grid.getStore().filter([{property: "JP_NAME|JP_LAUNCHERNAME", value:f.rawValue,
				    		 createFilterFn: function() {
				    		        var me       = this,
				    		            matcher  = me.createValueMatcher(),
				    		            property = me.property;
				    		            property1= me.property.split('|')[0];
				    		            property2= me.property.split('|')[1];
				    		        if (me.operator) {
				    		            return me.operatorFns[me.operator];
				    		        } else {
				    		            return function(item) {
				    		                var record = me.getRoot(item),value1=record[property1],value2=record[property2];	
				    		                return matcher === null ? value === null : matcher.test(value1) || matcher.test(value2) ;
				    		            };
				    		        }
				    		    }
				    	}]);
				    }
				}
			},
			onTriggerClick:function(e){
				this.setValue(null);

			}
		});    	
    }
	,// 筛选触发后台筛选并载入数据进grid
	headerfiltersapplyFn: function() {
		var me = this;
		var form = Ext.getCmp('centerForm');
		var grid = Ext.getCmp('jprocessGrid');
		var statBtns = form.query('erpStatButton');
		var filters = grid.gridFilters;
		var type = me.getType();
		var likestr = me.getLikeStr(grid, form, filters);
		//设置将所有的过滤操作推迟到服务器
		grid.store.remoteFilter=true;
		var count = grid.store.getCount( );
		Ext.Array.each(statBtns,function(btn){
			if(btn.type=='toDo'||btn.type=='toLaunch'){
				if(count>=0){
						btn.setStat(count);
						if(count>99){
							btn.setTooltip(count);
						}					
					}					
				}
			});
	},getLikeStr: function(grid, form, filters) {
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
	}
});