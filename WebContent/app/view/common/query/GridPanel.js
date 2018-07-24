Ext.require([
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.grid.PagingScroller'
]);
Ext.define('erp.view.common.query.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpQueryGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'querygrid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    columns: [],
    GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
  	features : [Ext.create('Ext.grid.feature.Grouping',{
   		hideGroupedHeader: true,
        groupHeaderTpl: '{name} (Count:{rows.length})'
    })],
     constructor: function(cfg) {
    	if(cfg) {
    		cfg.headerCt = cfg.headerCt || Ext.create("Ext.grid.header.Container", {
        		id: (cfg.id || this.id) + '-ct',
         	    forceFit: false,
                sortable: true,
                enableColumnMove:true,
                enableColumnResize:true,
                enableColumnHide: true
             });
        	cfg.plugins = cfg.plugins || [Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')];
        	cfg.selModel = cfg.selModel || Ext.create('Ext.selection.CheckboxModel', {
        		headerWidth: 0
        	}),
        	Ext.apply(this, cfg);
    	}
    	this.callParent(arguments);
     },
	initComponent : function(){
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		var gridParam = {caller: this.caller || caller, condition: condition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
		this.callParent(arguments);
		this.store.on('datachanged',this.summary,this);
		this.initRecords();
	},
	summary: function(){
		var me = this,
			store = this.store,
			items = store.buffered ? store.prefetchData.items : store.data.items,
			value;
		Ext.each(me.columns, function(c){
			if(c.summaryType == 'sum'){
                value = store.getSum(items, c.dataIndex);
                if(c.xtype == 'numbercolumn') {
    				value = Ext.util.Format.number(value, (c.format || '0,000.000'));
    			}
                me.down('tbtext[id=' + c.dataIndex + '_sum]').setText(c.text + '(sum):' + value);
			} else if(c.summaryType == 'count'){
                value = (store.filters && store.filters.length>0)?store.getCount():store.getTotalCount();
                me.down('tbtext[id=' + c.dataIndex + '_count]').setText(c.text + '(count):' + value);
			} else if(c.summaryType == 'average'){
				value = store.getAverage(c.dataIndex);
				if(c.xtype == 'numbercolumn') {
					value = Ext.util.Format.number(value, (c.format || '0,000.000'));
				}
				me.down('tbtext[id=' + c.dataIndex + '_average]').setText(c.text + '(average):' + value);
			}
		});
	},
////    loadMask: true,
    disableSelection: true,
    invalidateScrollerOnRefresh: false,
    viewConfig: {
        trackOver: false
    },
    buffered: true,
    sync: true,
	listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
			scroller.clearManagedListeners();  
			scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		/*storeloaded: function(grid, data) {
			  grid.summary();
		},*/
		'headerfilterchange': function(grid, filters) {
			var bool = false;
			if(filters){
				var items = filters.items;
				for(var i = 0 ; i<items.length ; i++ ){
					if(!(items[i].value ==""||items[i].value==null)){
						bool = true;
					}
				}
				if(bool){
					grid.store.pageSize = grid.store.totalCount+1;			
				}
			}	
        	return true;
        },
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn], f = grid.getHeaderFilterField(fn);
                    if(f.xtype == 'datefield')
                    	value = f.getRawValue();
                    if(!Ext.isEmpty(value)) {
                    	if("null"!=value){
                    	if(f.filtertype) {
                    		if (f.filtertype == 'numberfield') {
                    			value = fn + "=" + value + " ";
                    		}
                    	} else {
                    		if(Ext.isDate(value)){
                        		value = Ext.Date.toString(value);
                        		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
                        	} else {
                        		var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
                        		exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
    	                    	if(exp_d.test(value)){
    	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
    	                    	} else if(exp_t.test(value)){
    	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value.substr(0, 10) + "' ";
    	                    	} else{
    	                    		if (f.xtype == 'combo' || f.xtype == 'combofield') {
    	                    			if (value == '-所有-') {
    	                    				continue;
    	                    			} else {
    	                    				if (f.column && f.column.xtype == 'yncolumn'){
    	                    					if (value == '-无-') {
            	                    				value = fn + ' is null';
            	                    			} else {
            	                    				value = fn + ((value == '是' || value == '-1' || value == '1') ? '<>0' : '=0');
            	                    			}
             	                    		} else {
             	                    			if (value == '-无-') {
            	                    				value = 'nvl(' + fn + ',\' \')=\' \'';
            	                    			} else {
            	                    				if(value)value=value.replace(/\'/g,"''");
            	                    				value = fn + " LIKE '" + value + "%' ";
            	                    			}
             	                    		}
    	                    			}
    	                    		} else if(f.xtype == 'datefield') {
    	                    			value = "to_char(" + fn + ",'yyyy-MM-dd') like '%" + value + "%' ";
    	                    		} else if(f.column && f.column.xtype == 'numbercolumn'){
    	                    			if(f.column.format) {
    	                    				var precision = f.column.format.substr(f.column.format.indexOf('.') + 1).length;
    	                    				//防止to_char去除小数点前面的0
    	                    				if(-1<value&&value<1){
	    	                    				var number = value;
	    	                    				value = "to_char(round(" + fn + "," + precision + "),";	    	                    		
	    	                    				value += "'fm0.";
	    	                    				for(var i=0;i<precision;i++){
	    	                    					value += "0";
	    	                    				}
	    	                    				value += "') like '%" + number + "%' ";
	    	                    			}else{
	    	                    				value = "to_char(round(" + fn + "," + precision + ")) like '%" + value + "%' ";
	    	                    			}
    	                    			} else
    	                    				value = "to_char(" + fn + ") like '%" + value + "%' ";
    	                    		} else {
    	                    			/**字符串转换下简体*/
    	                    			if(value)value=value.replace(/\'/g,"''");
    	                    			var SimplizedValue=this.BaseUtil.Simplized(value);   	                    	
    	                    			//可能就是按繁体筛选  
    	                    			if(f.ignoreCase) {// 忽略大小写
        	                    			fn = 'upper(' + fn + ')';
        	                    			value = value.toUpperCase();
        	                    		}
        	                    		if(!f.autoDim) {
        	                    			if(SimplizedValue!=value){
        	                    				value = "("+fn + " LIKE '" + value + "%' or "+fn+" LIKE '"+SimplizedValue+"%')";
        	                    			}else value = fn + " LIKE '" + value + "%' ";       	                    			
        	                    			
        	                    		} else if(f.exactSearch){
        	                    			value=fn+"='"+value+"'";
        	                    		} else {
        	                    			if(SimplizedValue!=value){
        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
        	                    			}else value = fn + " LIKE '%" + value + "%' ";       	                    			        	                    			
        	                    		}
    	                    		}
    	                    	}
                        	}
                    	}
                    	}else value ="nvl("+fn+",' ')=' '";
                    	if(condition == null){
                    		condition = value;
                    	} else {
                    		condition = condition + " AND " + value;
                    	}
                    }
                }
                this.defaultCondition = condition;
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
	},
	getGridData: function() {
		var grid = this, form = Ext.getCmp('queryform');
		var d = grid.defaultCondition || '', f = d;
		if(grid.filterCondition){
			if(d == null || d == ''){
				f = grid.filterCondition;
			} else {
				f += ' AND ' + grid.filterCondition;
			}
		}
		f = form.spellCondition(f);
		if(Ext.isEmpty(f)) {
			f = grid.emptyCondition || '1=1';
		}
		this.GridUtil.loadNewStore(this, {caller: caller, condition: f});
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
	setToolbar: function(columns){
		var grid = this;
		var items = [];
		Ext.each(columns, function(column){
			if(column.summaryType == 'sum'){
				items.push('-',{
					id: column.dataIndex + '_sum',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.text + '(sum):0'
				});
			} else if(column.summaryType == 'average') {
				items.push('-',{
					id: column.dataIndex + '_average',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.text + '(average):0'
				});
			} else if(column.summaryType == 'count') {
				items.push('-',{
					id: column.dataIndex + '_count',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.text + '(count):0'
				});
			}
		});
		grid.bbar = {
			xtype: 'toolbar',
	        dock: 'bottom',
	        items: items
		};
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
	initRecords: function(){
		var records = this.store.data.items;
		var count = 0;
		Ext.each(records, function(record){
			if(!record.index){
				record.index = count++;
			}
		});
	}
});