Ext.define('erp.view.fa.ars.cmQuery.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpCmQueryGridPanel',
	id: 'cmquerygrid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    chkumio:false,
	tbar: {padding:'0 0 5 0',defaults:{margin:'0 5 0 0'},items:[{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray'
	}, {
		name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(btn){
			var grid = Ext.getCmp('cmquerygrid');
			grid.BaseUtil.exportGrid(grid, '应收总账');
    	}
	},{
		name: 'refresh',
		text: $I18N.common.button.erpRefreshButton,
		iconCls: 'x-button-icon-refresh',
    	cls: 'x-btn-gray'
	},'->', {
		margin:0,
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}]},
    GridUtil: Ext.create('erp.util.GridUtil'),
    selModel: Ext.create('Ext.selection.CheckboxModel',{
		headerWidth: 0
	}),
	plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
  	features : [Ext.create('Ext.grid.feature.Grouping',{
   		hideGroupedHeader: true,
        groupHeaderTpl: '{name} (Count:{rows.length})'
    }),{
        ftype : 'summary',
        showSummaryRow : false,//不显示默认合计行
        generateSummaryData: function(){
            var me = this,
	            data = {},
	            store = me.view.store,
	            columns = me.view.headerCt.getColumnsForTpl(),
	            i = 0,
	            length = columns.length,
	            //fieldData,
	            //key,
	            comp;
	        for (i = 0, length = columns.length; i < length; ++i) {
	            comp = Ext.getCmp(columns[i].id);
	            data[comp.id] = me.getSummary(store, comp.summaryType, comp.dataIndex, false);
	            var tb = Ext.getCmp(columns[i].dataIndex + '_' + comp.summaryType);
	            if(tb){
	            	tb.setText(tb.text.split(':')[0] + ':' + data[comp.id]);
	            }
	        }
	        return data;
        }
    }],
	initComponent : function(){ 
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		var gridParam = {caller: caller, condition: condition};
    	this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
    	this.callParent(arguments);
		//给页面加上ctrl+alt+s键盘事件,自动跳转form配置界面
		this.addKeyBoardEvents();//监听Ctrl+Alt+S事件
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		me.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(me.columns && me.columns.length > 2){
        			var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        			me.store.loadData(data);
        			//解决固定列左右不对齐的情况
                      var lockedView = me.view.lockedView;
                      if(lockedView){
                          var tableEl = lockedView.el.child('.x-grid-table');
                          if(tableEl){
                        	  tableEl.dom.style.marginBottom = '7px';
                          }
                      }
        			me.initRecords();
        		} else {
        			if(res.columns){
        				Ext.each(res.columns, function(column, y){
        					me.setRenderer(column);
        					var logic = column.logic;
        					if(logic != null){
        						me.setLogicType(column, logic, y);
        					}
        				});
            			//store
            			me.store = me.setDefaultStore(res.data, res.fields);
            			//view
                		if(me.selModel.views == null){
                			me.selModel.views = [];
                		}
                		if(res.dbfinds.length > 0){
                			me.dbfinds = res.dbfinds;
                		}
        				//toolbar
                		me.setToolbar(res.columns);
                		//reconfigure store&columns
                		me.columns = res.columns;
            		}
        		}
        	}
        });
	},
	setDefaultStore: function(d, f){
		var me = this;
		var data = [];
		if(!d || d.length == 2){
			me.GridUtil.add10EmptyData(me.detno, data);
			me.GridUtil.add10EmptyData(me.detno, data);
		} else {
			data = Ext.decode(d.replace(/,}/g, '}').replace(/,]/g, ']'));
		}
		var store = Ext.create('Ext.data.Store', {
		    fields: f,
		    data: data,
		    groupField: me.groupField,
		    getSum: function(field) {
	            var records = me.selModel.getSelection(),
	            	total = 0,
	                i = 0,
	                len = records.length;
	            for (; i < len; ++i) {
	            	total += records[i].get(field);
	            }
	            return total;
		    },
		    getCount: function() {
		    	var records = me.selModel.getSelection(),
		    		count = 0;
		    	Ext.each(records, function(item){
		    		count++;
		    	});
		        return count;
		    },
		    getAverage: function(field) {
		    	var records = me.selModel.getSelection(),
		    		count = 0,
		    		sum = 0;
		    	Ext.each(records, function(item){
		    		if(item.data[me.necessaryField] != null && item.data[me.necessaryField] != ''){
		    			count++;sum += item.data[field];
		    		}
		    	});
		        return Ext.Number.format(sum/count, '0.00');
		    }
		});
		return store;
	},
	setLogicType: function(column, logic, y){
		var grid = this;
		if(logic == 'detno'){
			grid.detno = column.dataIndex;
		} else if(logic == 'keyField'){
			grid.keyField = column.dataIndex;
		} else if(logic == 'mainField'){
			grid.mainField = column.dataIndex;
		} else if(logic == 'necessaryField'){
			grid.necessaryField = column.dataIndex;
			if(!grid.necessaryFields){
				grid.necessaryFields = new Array();
			}
			grid.necessaryFields.push(column.dataIndex);
			if(!column.haveRendered){
				column.renderer = function(val, meta, record, x, y, store, view){
					var c = this.columns[y];
					if(val != null && val.toString().trim() != ''){
						if(c.xtype == 'datecolumn'){
							val = Ext.Date.format(val, 'Y-m-d');
						}
						return val;
					} else {
						if(c.xtype == 'datecolumn'){
							val = '';
						}
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
			  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
					}
			   };
			}
		} else if(logic == 'groupField'){
			grid.groupField = column.dataIndex;
		}
	},
	initRecords: function(){
		var records = this.store.data.items;
		var count = 0;
		Ext.each(records, function(record){
			if(!record.index){
				record.index = count++;
			}
		});
	},
	setRenderer: function(column){
		var grid = this;
		if(!column.haveRendered && column.renderer != null && column.renderer != ""){
    		var renderName = column.renderer;
    		if(contains(column.renderer, ':', true)){
    			var args = new Array();
    			Ext.each(column.renderer.split(':'), function(a, index){
    				if(index == 0){
    					renderName = a;
    				} else {
    					args.push(a);
    				}
    			});
    			if(!grid.RenderUtil.args[renderName]){
    				grid.RenderUtil.args[renderName] = new Object();
    			}
    			grid.RenderUtil.args[renderName][column.dataIndex] = args;
    		}
    		column.renderer = grid.RenderUtil[renderName];
    		column.haveRendered = true;
    	}
	},
	setToolbar: function(columns){
		var grid = this;
		var items = [];
		Ext.each(columns, function(column){
			if(column.summaryType == 'sum'){
				items.push('-',{
					id: column.dataIndex + '_sum',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.header + '(sum):0'
				});
			} else if(column.summaryType == 'average') {
				items.push('-',{
					id: column.dataIndex + '_average',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.header + '(average):0'
				});
			} else if(column.summaryType == 'count') {
				items.push('-',{
					id: column.dataIndex + '_count',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.header + '(count):0'
				});
			}
		});
		grid.bbar = {
			xtype: 'toolbar',
	        dock: 'bottom',
	        items: items
		};
	},
	summary: function(){
		var me = this,
			store = this.store,
			value;
		Ext.each(me.columns, function(c){
			if(c.summaryType == 'sum'){
                value = store.getSum(store.data.items, c.dataIndex);
                me.down('tbtext[id=' + c.dataIndex + '_sum]').setText(c.header + '(sum):' + value);		
			} else if(c.summaryType == 'count'){
                value = store.getCount();
                me.down('tbtext[id=' + c.dataIndex + '_count]').setText(c.header + '(count):' + value);			
			} else if(c.summaryType == 'average'){
				value = store.getAverage(c.dataIndex);        		        			
				me.down('tbtext[id=' + c.dataIndex + '_average]').setText(c.header + '(average):' + value);
			}		
		});
	},
	listeners: {
        'afterrender':function(){
        	var me = this;
        	var grids = [me, me.lockedGrid, me.normalGrid];
        	for(var i in grids) {
        		if(grids[i]) {
        			grids[i].addListener('itemclick',function(grid,record){
                    	if(Number(record.data.cm_id)>0){
                    		me.FormUtil.onAdd('showCmDetail'+record.data.cm_id, "查询明细("+record.data.cm_id+")", 'jsps/fa/ars/showCmDetail.jsp?showtype='+record.data.cm_showtype+'&cmid='+record.data.cm_id+'&currency='+record.data.cm_currency+'&custcode='+record.data.cm_custcode+'&custname='+record.data.cu_name+'&yearmonth='+record.data.cm_yearmonth+'&chkumio='+me.chkumio);
                    	}
                    });
        		}
        	}
        }
	},	/**
	 * 监听一些事件,
	 * 如Ctrl+Alt+S
	 */
	addKeyBoardEvents: function(){
		var me = this;
		if(Ext.isIE && !Ext.isIE11){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					if(Ext.ComponentQuery.query('gridpanel').length > 0){//有grid
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
					}else if(Ext.ComponentQuery.query('formpanel').length == 0){
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
					} else {
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id);
					}
				}
			});
			document.body.attachEvent("onmouseover", function(){
				if(window.event.ctrlKey){
					var e = window.event;
					me.Contextvalue = e.target.textContent == "" ? e.target.value : e.target.textContent;
					textarea_text = parent.document.getElementById("textarea_text");
					textarea_text.value = me.Contextvalue;
						textarea_text.focus();
						textarea_text.select();
					}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					if(Ext.ComponentQuery.query('gridpanel').length > 0&&Ext.ComponentQuery.query('formpanel').length > 0){//有grid
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
					}else if(Ext.ComponentQuery.query('formpanel').length == 0&&Ext.ComponentQuery.query('gridpanel').length > 0){//只有form
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multigrid.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
					} else {
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id);
					}
				}
			});
			document.body.addEventListener("mouseover", function(e){
				if(Ext.isFF5){
					e = e || window.event;
				}
				if(e.ctrlKey){
					me.Contextvalue = e.target.textContent == "" ? e.target.value : e.target.textContent;
					textarea_text = parent.document.getElementById("textarea_text");
					textarea_text.value = me.Contextvalue;
						textarea_text.focus();
						textarea_text.select();
					}
			});
		}
	},
	cls: 'custom-grid',
	viewConfig: { 
        getRowClass: function(record) {
        	if(record.get('cm_showtype')=='1'){
        		return 'custom';
        	}else if(record.get('cm_showtype')=='3'){
        		return 'custom-blank';
        	}else{
        		return 'custom-alt';
        	}
//        	return record.get('cm_showtype')=='1'?'custom':'custom-alt';
//            return record.get('index')%2 == 0 ? (!Ext.isEmpty(record.get('cm_id')) ? 'custom-first' : 'custom') : 
//            	(!Ext.isEmpty(record.get('cm_id')) ? 'custom-alt-first' : 'custom-alt');
        } 
    }
});