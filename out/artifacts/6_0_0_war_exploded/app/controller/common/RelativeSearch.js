Ext.QuickTips.init();
Ext.define('erp.controller.common.RelativeSearch', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil','erp.util.GridUtil','erp.view.core.grid.HeaderFilter','erp.util.LinkUtil'],
    LinkUtil : Ext.create('erp.util.LinkUtil'),
    views:['common.RelativeSearch','core.plugin.CopyPasteMenu','core.grid.HeaderFilter'],
    init:function(){
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	this.control({
    		'tabpanel': {
    			afterrender: function(tb) {
    				this.getRelativeSearch(tb, caller);
    			},
    			add: function(t, p) {
    				p.on('activate', function(){
    					if(!p.down('gridpanel') && p.datas) {
    						var _f = p.datas.form, _g = p.datas.grid,limits=_g.limits,limitArr = new Array();
    						if(limits != null && limits.length > 0) {//权限外字段
		    					limitArr = Ext.Array.pluck(limits, 'lf_field');
		    				}
    						var _cm = (function(columns){
    							var cm = [], fn = me.link;
        						Ext.each(columns, function(c){
        							c.logic && (c.renderer = fn);
        							if(limitArr.length > 0 && Ext.Array.contains(limitArr, c.dataIndex)){
        								c.hidden=true;
        							}
        							delete c.locked;
       								cm.push(c);
        						});
        						return cm;
    						})(_g.gridColumns);
    						p.add({
    							xtype: 'grid',
    							columnLines: true,
    							layout: 'fit',
    							autoScroll : true,
    							anchor: '100% 80%',
    							selModel: Ext.create('Ext.selection.CheckboxModel',{
    								headerWidth: 0,
    								views: []
    							}),
    							plugins:[Ext.create('erp.view.core.grid.HeaderFilter',{
    							//首次加载的时候渲染Filters
    							renderFilters: function(){
    								var me = this;
							        this.destroyFilters();
							        this.fields = {};
							        this.containers = {};
							        var filters = this.grid.headerFilters;
							        if(this.stateful && this.grid[this.statusProperty] && !this.grid.ignoreSavedHeaderFilters)
							        {
							            Ext.apply(filters, this.grid[this.statusProperty]);
							        }
							        
							        var storeFilters = this.parseStoreFilters();
							        filters = Ext.apply(storeFilters, filters);
							        if(!this.grid.getView().headerCt.getGridColumns)
							        	return;
							        var columns = this.grid.columns || this.grid.headerCt.getGridColumns(true);
							        for(var c=0; c < columns.length; c++)
							        {
							            var column = columns[c];
							            if(column.filter)
							            {
							                var filterContainerConfig = {
							                    itemId: column.id + '-filtersContainer',
							                    cls: this.filterContainerCls,
							                    layout: 'anchor',
							                    bodyStyle: {'background-color': 'transparent'},
							                    border: false,
							                    width: column.getWidth(),
							                    listeners: {
							                        scope: this,
							                        element: 'el',
							                        mousedown: function(e)
							                        {
							                            e.stopPropagation();
							                        },
							                        click: function(e)
							                        {
							                            e.stopPropagation();
							                        },
							                        keydown: function(e){
							                             e.stopPropagation();
							                        },
							                        keypress: function(e){
							                             e.stopPropagation();
							                             if(e.getKey() == Ext.EventObject.ENTER)
							                             {
							                                 this.onFilterContainerEnter();
							                             }
							                        },
							                        keyup: function(e){
							                             e.stopPropagation();
							                        }
							                    },
							                    items: []
							                }
							                
							                var fca = [].concat(column.filter);
							                    
							                for(var ci = 0; ci < fca.length; ci++)
							                {
							                    var fc = fca[ci];
							                    //加入下拉框的默认值
							                    if ((fc.xtype == 'combo' || fc.xtype == 'combofield') && fc.store) {
							                    	Ext.Array.insert(fc.store.data, 0, [{
														display: '-所有-',value: '-所有-'
							                    	},{
							                    		display: '-无-',value: '-无-'
							                    	}]);
							                    }
							                    Ext.applyIf(fc, {
							                    	fieldStyle: 'background: #eee;',
							                    	focusCls: 'x-form-field-cir',
							                        filterName: column.dataIndex,
							                        fieldLabel: column.text || column.header,
							                        hideLabel: fca.length == 1
							                    });
							                    var initValue = Ext.isEmpty(filters[fc.filterName]) ? null : filters[fc.filterName];
							                    Ext.apply(fc, {
							                        cls: this.filterFieldCls,
							                        itemId: fc.filterName,
							                        anchor: '-1'
							                    });                 
							                    var filterField = Ext.ComponentManager.create(fc);                   
							                    if(filterField.xtype=='numberfield'){
							                    	filterField.maskRe =new RegExp('[0123456789\.\-\]+|(\\s)'); 
							                    }        
							                    filterField.column = column;
							                    this.setFieldValue(filterField, initValue);
							                    this.fields[filterField.filterName] = filterField;
							                    filterContainerConfig.items.push(filterField);
							                    //给下拉框添加监听，
							                    if (fc.xtype == 'combo' || fc.xtype == 'combofield') {
							                    	filterField.enableKeyEvents = true;
							                    	if(column.width<26){
							                    		filterField.hideTrigger = true;
							                    	}
								                    filterField.on('change', function(field,newValue){
								                    	if(!field.isChange){
									                    	me.applyFilters();// apply when combo change 
									                    	field.select(newValue);
								                    	}else{
								                    		field.isChange = false;
								                    	}
								                    });
								                    column.filterField = filterField;
								                    column.on('resize', function(field,newValue){
								                    	var t = field.filterField;
								                    	if((t.xtype == 'combo' || t.xtype == 'combofield')&&t.hideTrigger==true){
								                			var width = parseInt(t.getWidth());
								                			t.hideTrigger = false;
								                			t.inputEl.dom.style.width = (Number(width) - 18)+'px';
								                			t.triggerEl.item(0).dom.parentNode.style.width = '17px';
								                			t.triggerEl.item(0).dom.parentNode.style.display = 'block';
								                			t.triggerEl.item(0).setDisplayed('block');
								                			t.triggerEl.item(0).setWidth(17);
								                		}
								                    });
							                    }
							                }
							                
							                var filterContainer = Ext.create('Ext.container.Container', filterContainerConfig);
							                filterContainer.render(column.el);
							                this.containers[column.id] = filterContainer;
							                column.setPadding = Ext.Function.createInterceptor(column.setPadding, function(h){return false});
							            }
							        }
							        
							        if(this.enableTooltip)
							        {
							            this.tooltipTpl = new Ext.XTemplate(this.tooltipTpl,{text: this.bundle});
							            this.tooltip = Ext.create('Ext.tip.ToolTip',{
							                target: this.grid.headerCt.el,
							                renderTo: Ext.getBody(),
							                html: this.tooltipTpl.apply({filters: []})
							            });
							            this.tooltip.setDisabled(true);
							            this.grid.on('headerfilterchange',function(grid, filters)
							            {
							                var sf = filters.filterBy(function(filt){
							                    return !Ext.isEmpty(filt.value);
							                });
							                if(sf.length>0&&this.tooltip.disabled){
							                	this.tooltip.setDisabled(false);
							                }
							                this.tooltip.update(this.tooltipTpl.apply({filters: sf.getRange()}));
							            },this);
							        }
							        
							        this.applyFilters();
							        this.rendered = true;
							        this.grid.fireEvent('headerfiltersrender',this.grid,this.fields,this.parseFilters());
    							},
    							//触发筛选的执行的方法
    							applyFilters:function(){
    								var me = this, filters = this.parseFilters();
							       	var i=0;
							        if(this.grid.fireEvent('beforeheaderfiltersapply', this.grid, filters, this.grid.getStore()) !== false)
							        {	
							            var storeFilters = this.grid.getStore().filters, filterArr = new Array();
							            var exFilters = storeFilters.clone();
							            var change = false;
							            var active = 0;
							            for(var fn in filters)
							            {
							                var value = filters[fn];
							                
							                var sf = storeFilters.findBy(function(filter){
							                    return filter.property == fn;
							                });
							                
							                if(Ext.isEmpty(value))
							                {
							                    if(sf)
							                    {
							                        storeFilters.remove(sf);
							                        change = true;
							                    }
							                }
							                else
							                {
							                    var field = this.fields[fn];
							                    if(!sf || sf.value != filters[fn])
							                    {
							                        filterArr.push({
							                        	root: this.filterRoot,
							                        	label: field.fieldLabel,
							                        	property: fn,
							                            value: filters[fn]
							                        });
							                        if(sf)
							                        {
							                            storeFilters.remove(sf);
							                        }
							                        change = true;
							                    }
							                    active ++;
							                }
							            }
							          
							            this.grid.fireEvent('headerfiltersapply', this.grid, filters, active, this.grid.getStore());
							            if(change || storeFilters.length != filterArr.length)
							            {	//不使用filter
							                var curFilters = this.getFilters();
							                this.grid.fireEvent('headerfilterchange', this.grid, curFilters, this.lastApplyFilters, active, this.grid.getStore());
							                this.lastApplyFilters = curFilters;
							            }
							        }
							    }}),
							    Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    							dockedItems: [new Ext.toolbar.Paging(me.pagingConfig)],
    							tableName: _f.tablename,
    							defaultCondition: _f.condition,
    							fields: Ext.Array.concate(_g.gridFields, ',', 'name'),
    							store: new Ext.data.Store({
    								fields: _g.gridFields,
    								data: []
    							}),
    							// summary
    							renderSummary: function(data) {
    								var me = this, bar = me.bar;
    								if (!bar) {
    									var columns = me.view.headerCt.getGridColumns(), sitems = [];
    									Ext.each(columns, function(c){
		        							(c.summaryType == 'sum') && (sitems.push({colName: c.dataIndex, xtype: 'tbtext', colText: c.text, text: c.text + "(合计): 0", colFormat: c.format || '0,000.00'}));
		        						});
    									bar = me.bar = sitems.length > 0 ? me.addDocked({
    										xtype: 'toolbar',
    										dock: 'bottom',
    										items: sitems
    		    						})[0] : null;
    								}
    								if (bar) {
    									Ext.Array.each(bar.query('tbtext'), function(item){
    										item.setText(item.colText + "(合计): " + (data ? Ext.util.Format.number(data[item.colName] || 0, item.colFormat) : 0));
    									});
    								}
    							},
    							listeners: {
    								scrollershow: function(scroller) {
    									if (scroller && scroller.scrollEl) {
    										scroller.clearManagedListeners();  
    										scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
    									}
    								}
    							},
    							columns: _cm,
    							filterHeaderRendered: false,
    							renderFilterHeader: function() {
    								this.plugins[0].renderFilters();
    								this.filterHeaderRendered = true;
    							},
    							getGridData: function(page) {
//    								if(!this.filterHeaderRendered)
//    									this.renderFilterHeader();
    								me.query(this.ownerCt.down('form'), this, page);
    							}
    								});
    					}
    					var g = p.down('gridpanel');
    					//g.getGridData(g.plugins[0].page || 1);
    				});
    			}
    		},
    		'field' : {
    			afterrender : function(f) {
    				var name = f.name, form = f.ownerCt,
    					c = form.down('combo');
    				if (!c) {
    					var a = parent.Ext.getCmp(name);
    					if (a) {
    						f.setValue(a.getValue());
    					}
    				}
    				if(f.logic) {
    					var s = parent.Ext.getCmp(name);
    					if(s) {
    						f.setValue(s.getValue());
    					}
    				}
    			}
    		},
    		'form > combo': {
    			afterrender: function(f) {
    				var cm = parent.Ext.ComponentQuery.query('grid[relative=true]');
    				var grid = cm.length > 0 ? cm[0] : parent.Ext.getCmp('grid');
    				if(grid) {
    					var d = [];
    					grid.store.each(function(item){
    						if(!Ext.isEmpty(item.get(f.name))) {
    							d.push({
        							display: item.get(f.name),
        							value: item.get(f.name),
        							data: item.data
        						});
    						}
    					});
    					f.store = Ext.create('Ext.data.Store', {
    						fields: ['display','value','data'],
    						data: d 
    					});
    		            //根据选中行直接筛选
    					var lastselected=grid.getSelectionModel().selected.items[0];
    					if(lastselected){
    						f.setValue(lastselected.get(f.name));
    					}else if(d.length > 0)
    						f.setValue(d[0].value);
    				}
    			},
    			change: function(f) {
    				if(!Ext.isEmpty(f.value)) {
    					var form = f.ownerCt;
    					if (f.lastSelection[0])   					
    					 {   
    					     var d = f.lastSelection[0].data.data;   						
    					       form.getForm().getFields().each(function(e){
    						       typeof d[e.name] !== 'undefined' && (e.setValue(d[e.name]));
    					        });
    					 }
    					var grid = form.ownerCt.down('grid');
    					if(grid && grid.rendered) {
    						grid.plugins[0].clearFilters();
    						this.query(form, grid, 1);
    					}
    				}
    			}
    		},
    		'gridpanel': {
    			reconfigure: function(grid) {
    				grid.reconfigured = true;
    				this.query(grid.ownerCt.down('form'), grid, 1);
    			},
    			headerfilterchange:function(grid, filters){
    				var items = grid.getStore().data.items;
    				var sum1=0;
    				var sum2=0;
    				var sum3=0;
    				Ext.Array.each(items,function(item){
    					var data =item.data;
    					sum1 += data['sd_qty'];
    					sum2 += data['sd_qty-sd_sendqty'];
    					sum3 += data['sd_sendqty'];
    				});	
    				var data={'sd_qty':sum1,'sd_sendqty':sum3,'sd_qty-sd_sendqty':sum2};
    				grid.renderSummary(data);
    			},
    			headerfiltersapply: function(grid, filters) {
    				//if(grid.reconfigured){
    					var condition = null;
    					for(var fn in filters){
    						var value = filters[fn], f = grid.getHeaderFilterField(fn);
    						if(!Ext.isEmpty(value)){
    							if(f.filtertype) {
    								if (f.filtertype == 'numberfield') {
    									value = fn + "=" + value + " ";
    								}
    							} else {
    								if(Ext.isDate(value)){
    									value = Ext.Date.toString(value);
    									value = fn + "=to_date('" + value + "','yyyy-MM-dd') ";
    								} else {
    									var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
    									exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
    									if(exp_d.test(value)){
    										value = fn + "=to_date('" + value + "','yyyy-MM-dd') ";
    									} else if(exp_t.test(value)){
    										value = fn + "=to_date('" + value + "','yyyy-MM-dd HH24:mi:ss') ";
    									} else{
    										if (f.xtype == 'combo' || f.xtype == 'combofield') {
    											if (value == '-所有-') {
		    	                    				value = ' 1=1 ';
		    	                    			}else if (value == '-无-'){
		    	                    				value = fn + ' is null ';
		    	                    			}else {
		    	                    				value = fn + " LIKE '%" + value + "%' ";
		    	                    			}
    										}else {
    											value = fn + " LIKE '%" + value + "%' ";
    										}
    									}
    								}
    							}
    							if(condition == null){
    								condition = value;
    							} else {
    								condition = condition + " AND " + value;
    							}
    						}
    					}
    					grid.filterCondition = condition;
    					grid.getGridData(1);
    				//}
    				//return false;
    			}
    		},
    		'button[name=export]': {
    			click: function(btn) {
    				var grid = btn.up('gridpanel');
    				if (grid) {
    					var tb = grid.ownerCt, form = tb.down('form'), 
    						cols = grid.fields, con = this.getQueryCondition(form, grid);
    					this.BaseUtil.customExport(caller, grid, tb.title, '/common/form/relativeSearch.xls', con, {
    						_id: form.rs_id,
    						_tab: grid.tableName,
    						_fies: cols
    					});
    				}
    			}
    		}
    	});
    },
    getRelativeSearch: function(tb, cal) {
    	var me = this;
		Ext.Ajax.request({
			url: basePath + 'common/form/relativeSearch.action',
			params: {
				caller: cal
			},
			callback: function(opt, s, r) {
				if (s) {
					var rs = Ext.decode(r.responseText);
					if(rs.data) {						
						if(rs.data.length<1){					 	
							tb.add({
								title: '提示',
								xtype: 'form',								
								bodyStyle: {
   									 background: '#f1f1f1'
								},
								//html:'<div>该单据不存在关联查询</div>'	
								html:'<div style="left:35%;position:absolute;top:30%;font-weight:bold;font-size:25px;color:rgba(144, 143, 143, 0.5)">该单据不存在关联查询</div>'
							});
						}else{
							var s = null, _f = null;
							for(var i in rs.data) {
								s = rs.data[i];
								_f = s.form;
								_g = s.grid;
								for(var i in _f.items) {
									_f.items[i].fieldStyle = _f.items[i].fieldStyle + 'border: 1px solid #bdbdbd;';
								}
								tb.add({
									title: _f.title,
									datas: s,
									items: [{
										xtype: 'form',
										layout: 'column',
										items: _f.items,
										rs_id: _f.fo_id,
										cls: 'custom',
										bodyStyle: 'background: #f1f1f1;border: none;',
										fieldDefaults: {
											margin: '6 0 0 0',
											labelWidth: 70
										},
										anchor: '100% 20%',
										buttonAlign: 'left',
										buttons: [{
											text: $I18N.common.button.erpQueryButton,
											iconCls: 'x-button-icon-query',
											cls: 'x-btn-gray',
											handler: function(btn) {
												var f = btn.ownerCt.ownerCt, g = f.ownerCt.down('grid');
												me.query(f, g, 1);
											}
										},'->',{
											text: $I18N.common.button.erpCloseButton,
											iconCls: 'x-button-icon-close',
											cls: 'x-btn-gray',
											handler: function() {
												var w = parent.Ext.getCmp('ext-relative-query');
												if (w) {
													w.hide();
												}else{
													var tab = parent.Ext.getCmp('ProductWh');
													tab.close();
												}
											}
										}]
									}]
								});
							}
							if(tb.items.items.length > 0) {
								var p = tb.items.items[0];
								p.fireEvent('activate', p);
							}
						}
				}
			}
			}
		});
	},
	getQueryCondition: function(form, grid) {
		var con = grid.defaultCondition, fileter = grid.filterCondition;
		if(!Ext.isEmpty(con)) {
			con = "(" + con + ")";
		}
		if(!Ext.isEmpty(fileter)) {
			if(!Ext.isEmpty(con)) {
				con += " AND (" + fileter + ")";
			} else {
				con = fileter;
			}
		}
		form.getForm().getFields().each(function(f){
			if(f.logic) {
				if(f.logic.indexOf('to:') > -1) {
					var _field = f.logic.split('to:')[1];
					if(!Ext.isEmpty(con)) {
						if(f.xtype == 'datefield') {
							con += " AND (to_char(" + _field + ",'yyyymmdd')='" + 
								Ext.Date.format(f.value, 'Ymd') + "')";
						}else if(f.value!=''){
							var a = parent.Ext.getCmp(f.name);
	    					if(a && (a.xtype =='adddbfindtrigger' || a.xtype=='multidbfindtrigger')){//父级jsp中字段配置为多选时
	    						con += ' AND ' + _field + ' in (';
								var str=f.value,constr="";
								for(var i=0;i<str.split("#").length;i++){
									if(i<str.split("#").length-1){
										constr+="'"+str.split("#")[i]+"',";
									}else constr+="'"+str.split("#")[i]+"'";
								}
								con +=constr+")";
	    					}else{
	    						con += " AND (" + _field + "='" + f.value + "')";
	    					} 
						}
					} else {
						if(f.xtype == 'datefield') {
							con = " to_char(" + _field + ",'yyyymmdd')='" + 
								Ext.Date.format(f.value, 'Ymd') + "'";
						} else {
							if(f.value!=null && f.value!=""){
								con = _field + ' in (' ;
								var str=f.value,constr="";
								for(var i=0;i<str.split("#").length;i++){
									if(i<str.split("#").length-1){
										constr+="'"+str.split("#")[i]+"',";
									}else constr+="'"+str.split("#")[i]+"'";
								}
								con +=constr+")";
							}
						}
					}
				}
			}
		});
		return con;
	},
	pageSize: 12,
	query: function(form, grid, page) {
		var cols = grid.fields,
			pageSize = this.pageSize,
			start = (page - 1) * pageSize + 1,
			end = page * pageSize, con = this.getQueryCondition(form, grid);
		grid.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'common/form/search.action',
			params: {
				_id: form.rs_id,
				_tab: grid.tableName,
				_fies: cols,
				_start: start,
				_end: end,
				_cond: con
			},
			callback: function(opt, s, r) {
				grid.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.data) {
					grid.store.loadData(rs.data);
					grid.renderSummary(rs.summary);
					grid.down('pagingtoolbar').page = page;
					grid.down('pagingtoolbar').dataCount = rs.count;
        			grid.down('pagingtoolbar').onLoad();
				}
			}
		});
	},
	pagingConfig: {
        dock: 'bottom',
        displayInfo: true,
        pageSize: 12,
        items: ['-',{
	    	name: 'export',
	    	tooltip: $I18N.common.button.erpExportButton,
			iconCls: 'x-button-icon-excel',
			cls: 'x-btn-tb',
	    	width: 24,
	    	handler: function(){
	    		
	    	}
	    }],
        updateInfo : function(){
			var page = this.child('#inputItem').getValue() || 1;
			var me = this,
				pageSize = 12,
				dataCount = me.dataCount || 0;
 	    	var displayItem = me.child('#displayItem'),
 	    	 	pageData = me.getPageData();
            pageData.fromRecord = (page-1)*pageSize+1;
            pageData.toRecord = page*pageSize > dataCount ? dataCount : page*pageSize;
    		pageData.total = dataCount;
    		var msg;
                if (displayItem) {
                    if (me.dataCount === 0) {
                        msg = me.emptyMsg;
                    } else {
                        msg = Ext.String.format(
                            me.displayMsg,
                            pageData.fromRecord,
                            pageData.toRecord,
                            pageData.total
                        );
                    }
                    displayItem.setText(msg);
                    me.doComponentLayout();
                }
            },
            getPageData : function(){
            	var me = this,
            		totalCount = me.dataCount;
	        	return {
	        		total : totalCount,
	        		currentPage : me.page,
	        		pageCount: Math.ceil(me.dataCount / me.pageSize),
	        		fromRecord: ((me.page - 1) * me.pageSize) + 1,
	        		toRecord: Math.min(me.page * me.pageSize, totalCount)
	        	};
	        },
	        doRefresh:function(){
		    	this.moveFirst();
		    },
	        onPagingKeyDown : function(field, e){
	            var me = this,
	                k = e.getKey(),
	                pageData = me.getPageData(),
	                increment = e.shiftKey ? 10 : 1,
	                pageNum = 0;

	            if (k == e.RETURN) {
	                e.stopEvent();
	                pageNum = me.readPageFromInput(pageData);
	                if (pageNum !== false) {
	                    pageNum = Math.min(Math.max(1, pageNum), pageData.pageCount);
	                    me.child('#inputItem').setValue(pageNum);
	                    if(me.fireEvent('beforechange', me, pageNum) !== false){
	                    	me.page = pageNum;
	                    	me.ownerCt.getGridData(me.page);
	                    }
	                    
	                }
	            } else if (k == e.HOME || k == e.END) {
	                e.stopEvent();
	                pageNum = k == e.HOME ? 1 : pageData.pageCount;
	                field.setValue(pageNum);
	            } else if (k == e.UP || k == e.PAGEUP || k == e.DOWN || k == e.PAGEDOWN) {
	                e.stopEvent();
	                pageNum = me.readPageFromInput(pageData);
	                if (pageNum) {
	                    if (k == e.DOWN || k == e.PAGEDOWN) {
	                        increment *= -1;
	                    }
	                    pageNum += increment;
	                    if (pageNum >= 1 && pageNum <= pageData.pages) {
	                        field.setValue(pageNum);
	                    }
	                }
	            }
	            me.updateInfo();
	            me.resetTool(pageNum);
	        }, 
	        moveFirst : function(){
            	var me = this;
                me.child('#inputItem').setValue(1);
                value = 1;
            	me.page = value;
            	me.ownerCt.getGridData(value);
                me.updateInfo();
                me.resetTool(value);
            },
            movePrevious : function(){
                var me = this;
                me.child('#inputItem').setValue(me.child('#inputItem').getValue() - 1);
                value = me.child('#inputItem').getValue();
                me.page = value;
            	me.ownerCt.getGridData(value);
                me.updateInfo();
                me.resetTool(value);
            },
            moveNext : function(){
                var me = this,
                last = me.getPageData().pageCount;
                total = last;
                me.child('#inputItem').setValue(me.child('#inputItem').getValue() + 1);
                value = me.child('#inputItem').getValue();
                me.page = value;
            	me.ownerCt.getGridData(value);
                me.updateInfo();
                me.resetTool(value);
            },
            moveLast : function(){
                var me = this,
                last = me.getPageData().pageCount;
                total = last;
                me.child('#inputItem').setValue(last);
                value = me.child('#inputItem').getValue();
            	me.page = value;
            	me.ownerCt.getGridData(value);
                me.updateInfo();
                me.resetTool(value);
            },
            onLoad : function() {
				var e = this, d, b, c, a;
				if (!e.rendered) {
					return
				}
				d = e.getPageData();
				b = d.currentPage || 1;
				c = Math.ceil(e.dataCount / e.pageSize);
				a = Ext.String.format(e.afterPageText, isNaN(c) ? 1 : c);
				e.child("#afterTextItem").setText(a);
				e.child("#inputItem").setValue(b);
				e.child("#first").setDisabled(b === 1);
				e.child("#prev").setDisabled(b === 1);
				e.child("#next").setDisabled(b === c || c===1);//
				e.child("#last").setDisabled(b === c || c===1);
				e.child("#refresh").enable();
				e.updateInfo();
				e.fireEvent("change", e, d);
			},
			resetTool: function(value){
				var pageCount = this.getPageData().pageCount;
				this.child('#last').setDisabled(value == pageCount || pageCount == 1);
			    this.child('#next').setDisabled(value == pageCount || pageCount == 1);
			    this.child('#first').setDisabled(value <= 1);
			    this.child('#prev').setDisabled(value <= 1);
			}
	},
	link: function(val, m, record, x, y, store, view) {
		var me = this;
		var grid = view.ownerCt, column = grid.columns[y], url = column.logic;
		var render = url;
		if(contains(render, ':', true)){
			if(!this.LinkUtil){
				this.LinkUtil = Ext.create('erp.util.LinkUtil');
			}
			var args = new Array();
			var kind = '';
			Ext.each(render.split(':'), function(a, index){
				if(index == 0){
					renderName = a;
				} else {
					args.push(a);
				}
			});
			if(args[0]){
				kind = record.data[args[0]];
			}else{
				kind = args[1];
			}
			var code = record.data[column.dataIndex];
			url = me.LinkUtil.getLinks(me.LinkUtil.getLinkByKind(kind),code)
		}
		if(url) {
			var index = 0, length = url.length, s, e;
				while(index < length) {
					if((s = url.indexOf('{', index)) != -1 && (e = url.indexOf('}', s + 1)) != -1) {
						url = url.substring(0, s) + record.get(url.substring(s+1, e)) + url.substring(e+1);
						index = e + 1;
					}else{
						break;
					}
				}
			return '<a href="javascript:openUrl(\'' + url + '\');">' + val + '</a>';
		}
		return val;
	}
});