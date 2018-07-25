Ext.define('erp.util.GridUtil',{
	/**
	 * 取grid配置及数据
	 * 包括dbfind的配置
	 * @param no 需要自动编号的字段
	 * sortable  列排序 false 禁止列排序
	 */
	getGridColumnsAndStore: function(grid, url, param, no , sortable){
		var me = this;
		grid.setLoading(true);
		if(!param._config) param._config=getUrlParam('_config');
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	async: (grid.sync ? false : true),
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		if (!response) return;
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			var limits = res.limits, limitArr = new Array();
        			if(limits != null && limits.length > 0) {//权限外字段
        				Ext.each(limits,function(l){
        					//权限字段设置后 保存的字段为 表名.字段 字段 格式 需要分割开  
        					if(Ext.String.trim(l.lf_field).indexOf(' ')>-1){
        						var arr = Ext.String.trim(l.lf_field).split(' ');
        						l.lf_field = Ext.String.trim(l.lf_field).split(' ')[arr.length-1];
        					}
        					limitArr.push(Ext.String.trim(l.lf_field));
        				});
    				}
        			var reg =new RegExp("^yncolumn-{1}\\d{0,1}$");
        			Ext.each(res.columns, function(column, y){
        				if(sortable!=undefined){
            				column.sortable=sortable;
        				}
        				if(column.xtype=='textareatrigger'){
        					column.xtype='';
        					column.renderer='texttrigger';
        				}
        				//yncoloumn支持配置默认是/否
        				if(column.xtype &&reg.test(column.xtype)&&(column.xtype.substring(8)==-1||column.xtype.substring(8)==-0)){
        					Ext.each(res.fields, function(field, y){
                				if(field.type=='yn' && column.dataIndex==field.name){
                					field.defaultValue=0-column.xtype.substring(9);
                				}
        					});
        					column.xtype='yncolumn';
        				}
        				// column有取别名
        				if(column.dataIndex.indexOf(' ') > -1) {
        					column.dataIndex = column.dataIndex.split(' ')[1];
        				}
        				//power
        				if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
        					column.hidden = true;
        					column.hideable= false;
        				}
        				//renderer
        				me.setRenderer(grid, column);
        				//logictype
        				me.setLogicType(grid, column, {
        					headerColor: res.necessaryFieldColor
        				});
        				if (column.editor) {
        					if(column.xtype=='numbercolumn'&&column.format=='0,000.0000000000'){
        						column.editor.decimalPrecision = 10;//十位精度必须使用这个参数 只是用format无效
        					}
							column.editor.margin = '1 5 0 0';
						}
						if (column.logic == 'necessaryField') {
							column.style = 'color:#1e1e1e;';  /*color:#fb3c3c*/
							column.cls = 'x-grid-necessary';
						}else{
							column.cls = 'x-grid-normal';
						}
						if(column.dataIndex.indexOf('detno')>-1){
							column.align = 'center'
						}
        			});
        			//data
            		var data = [];
            		if(!res.data || res.data.length == 2){
            			if (grid.buffered) {
                			me.add10EmptyData(grid.detno, data);
                			me.add10EmptyData(grid.detno, data);//添加20条空白数据            				
            			} else {
            				grid.on('reconfigure', function(){// 改为Grid加载后再添加空行,节约200~700ms
                				me.add10EmptyItems(grid, 40, false);
                			});
            			}
            		} else {
            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		}
            		//store
            		var store = me.setStore(grid, res.fields, data, grid.groupField, grid.necessaryField);
            		//view
            		if(grid.selModel && grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		//dbfind
            		if(res.dbfinds && res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
            		//reconfigure
            		if(grid.sync) {//同步加载的Grid
            			grid.reconfigure(store, res.columns);
            			grid.on('afterrender', function(){
            				me.setToolbar(grid, grid.columns, grid.necessaryField, limitArr);
            			});
            		} else {
            			//toolbar
            			if (grid.generateSummaryData === undefined) {// 改为Grid加载后再添加合计,节约60ms
            				me.setToolbar(grid, res.columns, grid.necessaryField, limitArr);
            			}else{
            				grid.limitArr=limitArr;
            			}
            			grid.reconfigure(store, res.columns);
            		}
            		if(grid.buffered) {//缓冲数据的Grid
            			grid.verticalScroller = Ext.create('Ext.grid.PagingScroller', {
            				activePrefetch: false,
            				store: store
            			});
            			store.guaranteeRange(0, Math.min(store.pageSize, store.prefetchData.length) - 1);
            		}
            		var vp = grid.up('viewport'), form = (vp ? vp.down('form') : null);
        			if(form){ 
        				grid.readOnly = !!form.readOnly;//grid不可编辑
        				form.on('afterload', function(){
        					grid.readOnly = !!form.readOnly;
        				});
        			}
        			//在录入 新增状态下隐藏 间隔行
        			var form = Ext.getCmp('form');
					if(form&&form.statuscodeField){
						var status = Ext.getCmp(form.statuscodeField);	
						if(status&&status.value=='ENTERING'){
							if(grid.getView()&&grid.getView().el){
								grid.getView().el.dom.classList.add('x-grid-row-alt-hide');	
							}
						}
					}
        		} else {
        			grid.hide();
        			var vp = grid.up('viewport'), form = (vp ? vp.down('form') : null);
        			if(form && !form.isStatic) {
        				if(form.items.items.length == 0) {
        					form.on('afterload', function(){
            					me.updateFormPosition(form);//字段较少时，修改form布局
            				});
        				} else {
        					me.updateFormPosition(form);//字段较少时，修改form布局
        				}
        			}
        		}
        	}
        });
	},
	setRenderer: function(grid, column){
		if(!column.haveRendered){
			if((column.renderer != null && column.renderer != "")||(column.editor&&column.editor.xtype=='textareatrigger')) {
				if(!grid.RenderUtil){
					grid.RenderUtil = Ext.create('erp.util.RenderUtil');
				}
	    		var renderName = column.renderer;
	    		if(column.editor&&column.editor.xtype=='textareatrigger'){
	    			var form = Ext.ComponentQuery.query('form');
	    			if(form[0].readOnly){
	    				renderName = 'texttrigger';
	    			}
	    		}
	    		if(contains(column.renderer, ':', true)){
	    			var args = new Array();
	    			var arr = column.renderer.split(':');
	    			if(arr[0]!='rowstyle'){//判断是否是rowstyle
	    				Ext.each(arr, function(a, index){
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
	    			}else{//hey start 行样式加载	    				
	    		    	var arr = column.renderer.split(':');
	    		    	switch(arr.length)
	    		    	{
	    		    		case 2:						
	    		    			Ext.apply(grid.getView(),{
	    		    				 getRowClass: function(record, rowIndex, rowParams, store){	
	    		    				 	return (record.get(column.dataIndex)==arr[1]) ? 'default' : null;		
	    		    				 }
	    		    			});
	    		    			break;
	    		    		case 3:	 
	    		    			Ext.apply(grid.getView(),{
	    		    				 getRowClass: function(record, rowIndex, rowParams, store){	
	    		    				 	return (record.get(column.dataIndex)==arr[1]) ? arr[2] : null;		
	    		    				 }
	    		    			});
	    		    			break;
	    		    		default:
	    		    	}	
	    			}
	    		}
	    		//hey end 行样式加载
	    		column.renderer = grid.RenderUtil[renderName];
	    		column.renderName=renderName;
	    		column.haveRendered = true;
			} else if(column.readOnly){
				column.renderer = function(val, meta, record, x, y, store, view){
					meta.tdCls = 'x-grid-readOnly';
					meta.style = "background: #f1f1f1;";
					var form = Ext.getCmp('form');
					if(form&&form.statuscodeField){
						var status = Ext.getCmp(form.statuscodeField);	
						if(status&&status.value!='ENTERING'){
							meta.tdCls = '';
							meta.style = '';
						}
					}
					var c = this.columns[y];
					if(val != null && val.toString().trim() != ''){
						if(c.xtype == 'datecolumn' && typeof val === 'object'){
							val = Ext.Date.format(val, 'Y-m-d');
						} else if(c.xtype == 'numbercolumn' && val.toString().trim() == '0') {
							val = '';
						}
						return val;
					} else {
						if(c.xtype == 'datecolumn'){
							val = '';
						}
						return val;
					}
				};
				column.haveRendered = true;
			}
    		
    	}
	},
	setLogicType: function(grid, column, headerCss){
		var logic = column.logic;
		if(logic != null){
			if(logic == 'detno'){
				grid.detno = column.dataIndex;
				column.width = 40;
				column.align = 'center';
				column.renderer = function(val, meta) {
			        meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
			        return val;
			    };
			} else if(logic == 'keyField'){
				grid.keyField = column.dataIndex.split(" ")[0];
			} else if(logic == 'mainField'){
				grid.mainField = column.dataIndex.split(" ")[0];
			}else if(logic == 'orNecessField'){
				if(!grid.orNecessField){
					grid.orNecessField = new Array();
				}
				grid.orNecessField.push(column.dataIndex);
			}else if(logic == 'necessaryField'){
				grid.necessaryField = column.dataIndex;
				if(!grid.necessaryFields){
					grid.necessaryFields = new Array();
				}
				grid.necessaryFields.push(column.dataIndex);
				if(!column.haveRendered){
					column.renderer = function(val, meta, record, x, y, store, view){
						var c = this.columns[y];
						if(val != null && val.toString().trim() != ''){
							if(c.xtype == 'datecolumn' && typeof val === 'object'){
								val = Ext.Date.format(val, 'Y-m-d');
							} else if(c.xtype == 'numbercolumn') {
								val = Ext.util.Format.number(val, c.format || '0,000.00');
							}else if(c.xtype == 'combocolumn'){
								if(!Ext.isEmpty(val)) {
									var g = view.ownerCt,h = g.columns[y],f = h.field, k;
									if ((k = (h.editor || h.filter)) && k.store) {
										var t = null,dd = k.store.data;
								   		t = Ext.Array.filter(dd, function(d, index){
										    return d.value == val;
									    });
									    if (t && t.length > 0) {
									    	return t[0].display;
									    }
									} else if (f) {
							   	   		if(f.store) {
											var t = f.store.findRecord('value', val);
										    if (t)
										    	return t.get('display');
										} else
							   	   			return f.rawValue;
							   		}
								   return val;
								}						   
							}
							return val;
						} else {
							if(c.xtype == 'datecolumn' || c.useNull){//数字空
								val = '';
							}							
							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
						}
				   };
				}
				if(headerCss.headerColor)
					column.style = 'color:#' + headerCss.headerColor;
			} else if(logic == 'groupField'){
				grid.groupField = column.dataIndex;
			}
		}
	},
	setStore: function(grid, fields, data, groupField, necessaryField){
		var me=this;
		Ext.each(fields, function(f){
			if(f.name.indexOf(' ') > -1) {// column有取别名
				f.name = f.name.split(' ')[1];
			}
			if(!Ext.isChrome){
				if(f.type == 'date'){
					f.dateFormat = 'Y-m-d H:i:s';
				}
			}
		});
		var modelName = 'ext-model-' + grid.id;
		Ext.define(modelName, {
		    extend: 'Ext.data.Model',
		    fields: fields
		});
		var config = {
				model: modelName,
			    groupField: groupField,
			    grid:grid,
			    getSum: function(records, field) {
			    	if (arguments.length  < 2) {
			    		return 0;
			    	}
		            var total = 0,
		                i = 0,
		                len = records.length;
		            if(necessaryField) {
	            		for (; i < len; ++i) {//重写getSum,grid在合计时，只合计填写了必要信息的行
	            			var necessary = records[i].get(necessaryField);
		            		if(necessary != null && necessary != ''){
			            		total += records[i].get(field);
			            	}
	            		}
	            	} else {
	            		for (; i < len; ++i) {
	            			total += records[i].get(field);
	            		}
	            	}
		            return total;
			    },
                listeners:{
                	'update':me.syncSummaryData,
                	'remove':me.syncSummaryData
                } ,
			    getCount: function() {
			    	if(necessaryField) {
			    		var count = 0;
			    		Ext.each(this.data.items, function(item){//重写getCount,grid在合计时，只合计填写了必要信息的行
				    		if(item.data[necessaryField] != null && item.data[necessaryField] != ''){
				    			count++;
				    		}
				    	});
			    		return count;
			    	}
			    	return this.data.items.length;
			    }	
		};
		if(grid.buffered) {//grid数据缓存
			config.buffered = true;
			config.pageSize = grid.bufferSize||200;
			config.purgePageCount = 0;
			config.proxy = {
                type: 'memory'
            };
		} else {
			config.data = data;
			config.RawData = data;//记录修改前明细行数据
		}
		//取消排序
		/*if(grid.detno) {
			// sort by detno property
			config.sorters = [{
				property: grid.detno,
				direction: 'ASC'
			}];
		}*/
		var store = Ext.create('Ext.data.Store', config);
		store.each(function(item, x){
			item.index = x;
		});
		if(grid.buffered) {
			var ln = data.length, records = [], i = 0;
		    for (; i < ln; i++) {
		        records.push(Ext.create(modelName, data[i]));
		    }
		    store.cacheRecords(records);
		}
		return store;
	},
	setToolbar: function(grid, columns, necessaryField, limitArr){
		var items = [];
		var bool = true;
		if(!grid.dockedItems)
			return;
		Ext.each(grid.dockedItems.items, function(item){
			if(item.dock == 'bottom' && item.items){//bbar已存在
				bool = false;
			}
		});
		if(bool){
    		Ext.each(columns, function(column){
    			if(limitArr.length == 0 || !Ext.Array.contains(limitArr, column.dataIndex)) {
    				if(column.summaryType == 'sum'){
        				items.push('-',{
        					id: (column.dataIndex + '_sum').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(sum):000000'
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
        					id: (column.dataIndex + '_count').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(count):0'
        				});
        			}
    			}
    			if(column.dataIndex == necessaryField){
    				column.renderer = function(val){
    					if(val != null && val.toString().trim() != ''){
							return val;
						} else {
							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
						}
    				};
    			}
    		});
    		// 行数统计
    		items.push('->');
    		items.push({
    			xtype : 'tbtext',
    			itemId : 'count'
    		});
			grid.addDocked({
    			xtype: 'toolbar',
    	        dock: 'bottom',
    	        items: items
    		});
		} else {
			var bars = Ext.ComponentQuery.query('erpToolbar');
			if(bars.length > 0){
				var bar = bars[0];
				Ext.each(columns, function(column){
        			if(column.summaryType == 'sum'){
        				bar.add('-');
        				bar.add({
        					id: (column.dataIndex + '_sum').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(sum):0'
        				});
        			} else if(column.summaryType == 'average') {
        				bar.add('-');
        				bar.add({
        					id: column.dataIndex + '_average',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.text + '(average):0'
        				});
        			} else if(column.summaryType == 'count') {
        				bar.add('-');
        				bar.add({
        					id: (column.dataIndex + '_count').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(count):0'
        				});
        			}
        		});
				if(!bar.down('tbtext[itemId=count]')) {
					// 行数统计
					bar.add('->');
					bar.add({
		    			xtype : 'tbtext',
		    			itemId : 'count'
		    		});
				}
			}
		}
	},
	updateFormPosition: function(form){
		var height = window.innerHeight;
		var width = window.innerWidth;
		if(form){
			if(form.items.items.length > 12){
				form.setHeight(height);
			} else {//少于12个字段的单form页面，强制居中显示
				if(form.items.items == 0){
					form.on('afterlayout', function(){
						if(0 < form.items.items.length <= 12){
							Ext.each(form.items.items, function(item){
								if(item.columnWidth >= 0.25 && item.columnWidth < 0.6){
									item.columnWidth = 0.5;
								} else if(item.columnWidth >= 0.6) {
									item.columnWidth = 1;
								}
							});
						}
					});
				} else {
					Ext.each(form.items.items, function(item){
						if(item.columnWidth >= 0.25 && item.columnWidth < 0.6){
							item.columnWidth = 0.5;
						} else if(item.columnWidth >= 0.6) {
							item.columnWidth = 1;
						}
					});
				}
				form.setHeight('60%');
				form.setWidth('70%');
				form.el.applyStyles('margin:10% auto;border-width: 1px;');
				if(Ext.getCmp('form_toolbar')){
					Ext.getCmp('form_toolbar').el.dom.classList.add('x-singtoolbar');
				}
				form.el.dom.classList.add('x-singlepanel');
				form.el.dom.getElementsByClassName('x-panel-body')[0].classList.add('x-singlepanel-body');
			}
		}
	},
	loadNewStore: function(grid, param){
		var me = this;
		grid.setLoading(true);//loading...
		if(!param._config) param._config=getUrlParam('_config');
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		if(!data || data.length == 0){
        			grid.store.removeAll();
        			me.add10EmptyItems(grid);
        		} else {
        			if(grid.buffered) {
        				var ln = data.length, records = [], i = 0;
        			    for (; i < ln; i++) {
        			        records.push(Ext.create(grid.store.model.getName(), data[i]));
        			    }
        			    grid.store.purgeRecords();
        			    grid.store.cacheRecords(records);
        			    grid.store.totalCount = ln;
        			    grid.store.guaranteedStart = -1;
        			    grid.store.guaranteedEnd = -1;
        			    var a = grid.store.pageSize - 1;
        			    a = a > ln - 1 ? ln - 1 : a;
        			    grid.store.guaranteeRange(0, a);
        			} else {
        				grid.store.loadData(data);
        			}
        		}
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid, data);
        	}
        });
	},
	/**
	 * 从index行开始，往grid里面加十空行
	 * @param detno 编号字段
	 * @param data 需要添加空白数据的data
	 */
	add10EmptyData: function(detno, data){
		if(detno){
			var index = data.length == 0 ? 0 : Number(data[data.length-1][detno]);
			for(var i=0;i<20;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				data.push(o);
			}
		} else {
			for(var i=0;i<20;i++){
				var o = new Object();
				data.push(o);
			}
		}
	},
	/**
	 * 从index行开始，往grid里面加十空行
	 * @param grid 
	 */
	add10EmptyItems: function(grid, count, append){
		var store = grid.store, 
			items = store.data.items, arr = new Array();
		var detno = grid.detno;
		count = count || 10;
		append = append === undefined ? true : false;
		if(typeof grid.sequenceFn === 'function')
			grid.sequenceFn.call(grid, count);
		else {
			if(detno){
				var index = items.length == 0 ? 0 : Number(store.max(detno));
				for(var i=0;i < count;i++ ){
					var o = new Object();
					o[detno] = index + i + 1;
					arr.push(o);
				}
			} else {
				for(var i=0;i < count;i++ ){
					var o = new Object();
					arr.push(o);
				}
			}
			store.loadData(arr, append);
			var i = 0;
			store.each(function(item, x){
				if(item.index) {
					i = item.index;
				} else {
					if (i) {
						item.index = i++;
					} else {
						item.index = x;
					}
				}
			});
		}
	},
	isBlank: function(grid, data) {
		if(typeof grid.isEmptyRecord === 'function') {
			return grid.isEmptyRecord.call(grid, data);
		} else {
			var ff = grid.necessaryFields,bool = true;
			var of = grid.orNecessField, c;
			if(ff) {
				bool = false;
				Ext.each(ff, function(f) {
					c = grid.down('gridcolumn[dataIndex=' + f + ']');
					if(Ext.isEmpty(data[f]) || (data[f] === 0 && c 
							&& c.xtype != 'ynnvcolumn' && c.xtype != 'yncolumn' && !c.useNull )) {//数字空
						bool = true;return;
					}
				});
			} else if(of){
				Ext.each(of,function(f){
					if(!Ext.isEmpty(data[f]) && data[f] != 0){
						bool = false;
						return;
					}
				});
			} else {
				if(!grid.necessaryField || !Ext.isEmpty(data[grid.necessaryField])) {
					bool = false;
				} 
			}
			return bool;
		}
	},
	isEmpty: function(grid) {
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this, i = 0, items = grid.getStore().data.items, l = items.length;
		for(;i < l;i++) {
			if(!me.isBlank(grid, items[i].data)) {
				return false;
			}
		}
		return true;
	},
	isDirty: function(grid) {
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var i = 0, items = grid.getStore().data.items, l = items.length;
		for(;i < l;i++) {
			if( items[i].dirty ) {
				return true;
			}
		}
		return false;
	},
	/**
	  * 获取grid已编辑但部分必填字段没填写的行，并提示
	  */
	getUnFinish: function(grid) {
		var me = this, cols = {};
		Ext.Array.each(grid.columns, function(c){
			if(c.dataIndex)
				cols[c.dataIndex] = c.text || c.header;
		});
		var errs = [];
		grid.store.each(function(record){////////数字空
			if(record.dirty && me.isBlank(grid, record.data)) {
				var ff = grid.necessaryFields, s = '';
				if(ff) {
					Ext.each(ff, function(f) {
						c = grid.down('gridcolumn[dataIndex=' + f + ']');
						if(c.logic != 'ignore' && (Ext.isEmpty(record.get(f)) || (record.get(f) == 0 && c 
								&& c.xtype != 'ynnvcolumn' && c.xtype != 'yncolumn'  &&  !c.useNull ))) {//数字空
							s += '<u> ' + (c.text || c.header) + ' </u> ';
						}
					});
				} else if(grid.necessaryField) {
					var f = grid.necessaryField, c = grid.down('gridcolumn[dataIndex=' + f + ']');
					if(c.logic != 'ignore' && (Ext.isEmpty(record.get(f)) || (record.get(f) == 0 && c 
							&& c.xtype != 'ynnvcolumn' && c.xtype != 'yncolumn' &&  !c.useNull  ))) {//数字空
						s += '<u> ' + (c.text || c.header) + ' </u> ';
					}
				}
				if(s.length > 0)
					errs.push((grid.detno ? ('行: ' + record.get(grid.detno) + ', ') : '') + s);
			}
		});
		return errs.join('<br>');
	},
	/**
	  * 获取grid已保存但部分必填字段没填写的行，并提示
	  */
	getInvalid: function(grid,checkRawData) {
		if(grid.keyField) {
			var me = this, cols = {};
			Ext.Array.each(grid.columns, function(c){
				if(c.dataIndex)
					cols[c.dataIndex] = c.text || c.header;
			});
			var errs = [];
			if(checkRawData){//检查编辑前的数据
				Ext.each(grid.store.RawData,function(record){
					var key = record[grid.keyField];
					if(key && key > 0 && me.isBlank(grid, record)) {
						var ff = grid.necessaryFields, s = '';
						if(ff) {
							Ext.each(ff, function(f) {
								c = grid.down('gridcolumn[dataIndex=' + f + ']');
								if(c.logic != 'ignore' && (Ext.isEmpty(record[f]) || (record[f] == 0 && c 
										&& c.xtype != 'ynnvcolumn' && c.xtype != 'yncolumn'))) {
									s += '<u> ' + (c.text || c.header) + ' </u> ';
								}
							});
						} else if(grid.necessaryField) {
							var f = grid.necessaryField, c = grid.down('gridcolumn[dataIndex=' + f + ']');
							if(c.logic != 'ignore' && (Ext.isEmpty(record[f]) || (record[f] == 0 && c 
									&& c.xtype != 'ynnvcolumn' && c.xtype != 'yncolumn'))) {
								s += '<u> ' + (c.text || c.header) + ' </u> ';
							}
						}
						if(s.length > 0)
							errs.push((grid.detno ? ('行: ' + record[grid.detno] + ', ') : '') + s);
					}
				});
			}else{
				grid.store.each(function(record){
					var key = record.get(grid.keyField);
					if(key && key > 0 && me.isBlank(grid, record.data)) {
						var ff = grid.necessaryFields, s = '';
						if(ff) {
							Ext.each(ff, function(f) {
								c = grid.down('gridcolumn[dataIndex=' + f + ']');
								if(c.logic != 'ignore' && (Ext.isEmpty(record.get(f)) || (record.get(f) == 0 && c 
										&& c.xtype != 'ynnvcolumn' && c.xtype != 'yncolumn'))) {
									s += '<u> ' + (c.text || c.header) + ' </u> ';
								}
							});
						} else if(grid.necessaryField) {
							var f = grid.necessaryField, c = grid.down('gridcolumn[dataIndex=' + f + ']');
							if(c.logic != 'ignore' && (Ext.isEmpty(record.get(f)) || (record.get(f) == 0 && c 
									&& c.xtype != 'ynnvcolumn' && c.xtype != 'yncolumn'))) {
								s += '<u> ' + (c.text || c.header) + ' </u> ';
							}
						}
						if(s.length > 0)
							errs.push((grid.detno ? ('行: ' + record.get(grid.detno) + ', ') : '') + s);
					}
				});
			}
			return errs.join('<br>');
		}
		return null;
	},
	/**
	 * 拿到grid里面要提交的数据
	 */
	getGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,
			jsonGridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if((s[i].dirty||(getUrlParam('_copyConf')!=null && getUrlParam('gridCondition')==null)) //通用复制单据保存时数据不是dirty的
				&& !me.isBlank(grid, data)){
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if( (c.editor || !c.readOnly ) &&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
		}
		return jsonGridData;
	},
	getAllGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var columnUseNull=false;
		var jsonGridData = new Array();
		var form = Ext.getCmp('form');
		Ext.each(grid.columns,function(c){
			if(c.dataIndex==grid.necessaryField){
				if(c.useNull) columnUseNull=true;
				return false;
			} 
		});  
		grid.getStore().each(function(item){//将grid里面各行的数据获取并拼成jsonGridData
			var data = Ext.clone(item.data);
			if(data[grid.necessaryField] != null && ((columnUseNull && data[grid.necessaryField]==0) //支持数字字段0必填
					||data[grid.necessaryField] != "" )){  
				if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
					data[grid.mainField] = Ext.getCmp(form.keyField).value;
				}
				Ext.each(grid.columns, function(c){
					if(c.xtype == 'datecolumn'){
						if(Ext.isDate(data[c.dataIndex])){
							data[c.dataIndex] = Ext.Date.toString(data[c.dataIndex]);//在这里把GMT日期转化成Y-m-d格式日期
						} else {
							data[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d');//如果用户没输入日期，或输入有误，就给个默认日期，
							//或干脆return；并且提示一下用户
						}
					} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
						if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
							data[c.dataIndex] = '0';//也可以从data里面去掉这些字段
						} else {
							data[c.dataIndex] = "" + data[c.dataIndex];
						}
					}
				});
				jsonGridData.push(Ext.JSON.encode(data));
			}
		});
		return jsonGridData;
	},
	getAllGridStoreData: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,GridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(!me.isBlank(grid, data)){
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						data[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					GridData.push(dd);
				}
			}
		}
		return GridData;
	},
	/**
	 * 检查grid是否已修改
	 */
	checkGridDirty: function(grid){
		var store = grid.getStore().data.items;//获取store里面的数据
		var msg = '';
		for(var i=0;i<store.length;i++){
			if(store[i].dirty){//明细行被修改过哦
				var changed = Ext.Object.getKeys(store[i].modified);//拿到被修改了的字段
				var s = '';
				for(var j = 0;j < changed.length;j++){
					Ext.each(grid.columns, function(c, index){
						if((!c.isCheckerHd) && (c.logic != 'ignore') && (c.logic != 'detno') && 
							!((c.renderName=='formula') ||(c.renderName=='_formula') && c.readOnly)){
							if(c.dataIndex == changed[j]){//由字段dataindex匹配其text
								if(s == '') {
									s = '第' + (i+1) + '行:';
								}
								s += '<font color=blue>' + c.text + '</font>&nbsp;';
							}	
						}
					});
				}
				if(s != '')
					msg += s;
			}
		}
		if(msg != '' && msg != ';'){//明细行被修改过哦
			msg = "明细行<font color=green>" + msg.substring(0, msg.length-1) + "</font>已编辑过";
		} else {
			msg = '';
		}
		return msg;
	},
	getRecordByCode: function(param){
		Ext.Ajax.request({
        	url : basePath + 'common/getRecordByCode.action',
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		//var res = new Ext.decode(response.responseText);
        	}
        });
	},
	onGridItemClick: function(selModel, record, id){
		var me = this.GridUtil || this;
		var grid = selModel.ownerCt;
		if(grid && !grid.readOnly && !grid.NoAdd){
			var index = grid.store.indexOf(record);
			if(index == grid.store.indexOf(grid.store.last())){
				me.add10EmptyItems(grid);//就再加10行
	    	}
			var btn = grid.down('erpDeleteDetailButton');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('erpAddDetailButton');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('copydetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('pastedetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('updetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('downdetail');
			if(btn)
				btn.setDisabled(false);
			if(grid.down('tbtext[name=row]')){
				grid.down('tbtext[name=row]').setText(index+1);
			}
		}
	},
	onGridItemClickForEditGrid: function(selModel, record, id){
		var grid = id == null ? Ext.getCmp('grid') : Ext.getCmp(id);
		var index = null;
		if(grid.detno){
			index = record.data[grid.detno];
			index = index == null ? (record.index + 1) : index;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items[grid.store.data.items.length-1].data[grid.detno]){//如果选择了最后一行
				this.add10EmptyItems(grid);//就再加10行
	    	}
		} else {
			index = record.index + 1;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items[grid.store.data.items.length-1].index + 1){//如果选择了最后一行
	    		this.add10EmptyItems(grid);//就再加10行
	    	}
		}
//		var btn = grid.down('erpDeleteButton');
//		btn.setDisabled(false);

	},
	deleteDetailForEditGrid:function(btn){
		var grid = btn.ownerCt.ownerCt;
		var records = grid.selModel.getSelection();
		if(records.length > 0){
			if(grid.keyField){
				if(records[0].data[grid.keyField] != null && records[0].data[grid.keyField] > 0){
					warnMsg($I18N.common.msg.ask_del, function(btn){
						if(btn == 'yes'){
							grid.BaseUtil.getActiveTab().setLoading(true);//loading...
							Ext.Ajax.request({
						   		url : basePath + "common/deleteDetail.action",
						   		params: {
						   			caller: caller,
						   			gridcaller: caller,
						   			condition: grid.keyField + "=" + records[0].data[grid.keyField]
						   		},
						   		method : 'post',
						   		callback : function(options,success,response){
						   			grid.BaseUtil.getActiveTab().setLoading(false);
						   			var localJson = new Ext.decode(response.responseText);
						   			if(localJson.exceptionInfo){
					        			showError(localJson.exceptionInfo);return;
					        		}
					    			if(localJson.success){
					    				grid.store.remove(records[0]);
						   				delSuccess(function(){
								   										
										});//@i18n/i18n.js
						   			} else {
						   				delFailure();
						   			}
						   		}
							});
						}
					});
				} else {
					grid.store.remove(records[0]);
				}
			} else {
				if(records[0].data[grid.keyField] != null && records[0].data[grid.keyField] > 0){
					showError("grid未配置keyField，无法删除该行数据!");
				} else {
					grid.store.remove(records[0]);
				}
			}
		}
	},
	onSave: function(grid, saveUrl){
		var me = this;
		if(grid){
			var param = me.getGridStore();
			if(grid.necessaryField.length > 0 && (param == null || param == '')){
				showError('明细表还未添加数据');return;
			} else {
				var url = saveUrl || grid.saveUrl || Ext.getCmp('form').saveUrl;
				me.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params : {
			   			param: unescape(param.toString().replace(/\\/g,"%"))
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			me.getActiveTab().setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
		    			if(localJson.success){
		    				saveSuccess(function(){
		    					//add成功后刷新grid
		    					me.loadNewStore(grid, {
		    						caller: caller,
		    						condition: grid.mainField + '=' + Ext.getCmp(Ext.getCmp('form').keyField).value
		    					});
		    				});
			   			} else if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
			   					str = str.replace('AFTERSUCCESS', '');
			   					saveSuccess(function(){
			    					//add成功后刷新grid
			   						me.loadNewStore(grid, {
			    						caller: caller,
			    						condition: grid.mainField + '=' + Ext.getCmp(Ext.getCmp('form').keyField).value
			    					});
			    				});
			   					showError(str);
			   				} else {
			   					showError(str);
				   				return;
			   				}
			   			} else{
			   				saveFailure();//@i18n/i18n.js
			   			}
			   		}
			   		
				});
			}
		}
	},
	onUpdate: function(grid, updateUrl){
		var me = this;
		if(grid){
			var param = me.getGridStore();
			if(grid.necessaryField.length > 0 && (param == null || param == '')){
				showError('明细表还未添加数据');return;
			} else {
				var url = updateUrl || grid.updateUrl || Ext.getCmp('form').updateUrl;
				me.update(param, url, function(){
					me.loadNewStore(grid, {
						caller: caller,
						condition: grid.mainField + '=' + Ext.getCmp(Ext.getCmp('form').keyField).value
					});
				});
			}
		}
	},
	/**
	 * @param param
	 * @param param2
	 * ...
	 * @param updateUrl
	 * @param callback
	 */
	update: function() {
		if(arguments.length < 3)
			return;
		var me = this, params = {}, url = arguments[arguments.length - 2],
			callback = arguments[arguments.length - 1];
		params.param = unescape(arguments[0].toString().replace(/\\/g,"%"));
		for(var i=1; i<arguments.length - 2; i++) {
			if (arguments[i] != null)
				params['param' + (i + 1)] = unescape(arguments[i].toString());
		}
		me.getActiveTab().setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + url,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				updateSuccess(function(){
    					callback.call(me);
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
	   					str = str.replace('AFTERSUCCESS', '');
	   					updateSuccess(function(){
	   						callback.call(me);
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();
	   			}
	   		}
	   		
		});
	},
	getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	return tab;
	},
	getGridSelected:function(grid){
		var items = grid.selModel.getSelection(),data = new Array();
		Ext.each(items, function(item, index){
			if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
				&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
				var o = new Object();
				if(grid.keyField){
					o[grid.keyField] = item.data[grid.keyField];
				}
				if(grid.toField){
					Ext.each(grid.toField, function(f, index){
						var v = Ext.getCmp(f).value;
						if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							o[f] = v;
						} else {
							o[f] = '';
						}
					});
				}
				if(grid.necessaryFields){
					Ext.each(grid.necessaryFields, function(f, index){
						var v = item.data[f];
						if(Ext.isDate(v)){
							v = Ext.Date.toString(v);
						}
						if(Ext.isNumber(v)){
							v = (v).toString();
						}
						o[f] = v;
					});
				}
				data.push(o);
				
			}
		});
		return data;
     },
     /**同步汇总数据*/
     syncSummaryData:function(store,record,operation){
    	 var g=this.grid,cols=g.columns,bar=g.down('erpToolbar');
    	 if(bar){
    	 	Ext.Array.each(cols,function(column){
    	 	var sumItem=bar.getComponent(column.dataIndex),summaryData=0,store=g.getStore();
    		 if(column.summaryType && column.xtype=='numbercolumn' && sumItem){    			 
    			 switch(column.summaryType){
    			 case 'sum':
    				 summaryData=store.sum(column.dataIndex);    				
    				 break;
    			 case 'min':
    				 summaryData=store.min(column.dataIndex);
    				 break;
    			 case 'max':
    				 summaryData=store.average(column.dataIndex);
    				 break;	 
    			 }
    			 if(sumItem)sumItem.update(column.text+':'+summaryData);
    		 }							
    	 });  
    	 }    	 	 
     },
     autoDbfind: function(grid, field, condition) {
		var me = this,caller = '';
		Ext.Array.each(grid.columns,function(column){
			if(column.dataIndex==field){
				caller = column.dbfind.split('|')[0];
				return false;
			}
		});
		if(!caller){
			showError('字段'+field+'未配置放大镜！');
			return;
		}
		Ext.Ajax.request({
			url: basePath + 'common/getOrderChange.action',
			params: {
				which: 'grid',
				caller: caller,
				field: field,
				condition: condition,
				_config:getUrlParam('_config')
			},
			async: false,
			method: 'post',
			callback: function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				if (res.data) {
					var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					if(data.length<1){
						showError(getUrlParam('operation')+'，勾选明细不符合操作条件！');
						parent.Ext.getCmp('win').close();
					}
					grid.store.removeAll();
					me.add10EmptyItems(grid, data.length, true);
					me.autoSetValue(data, grid, field);
				}
			}
		});
	},
	autoSetValue: function(datas, grid, field) {
		var me = this,dbfinds = grid.dbfinds,records = grid.store.data.items;
		Ext.Array.each(records, function(record,index){
			var data = datas[index];
			Ext.Array.each(Ext.Object.getKeys(data),function(k) {
				Ext.Array.each(dbfinds,function(ds) {
					if (ds.trigger == field || Ext.isEmpty(ds.trigger)) {
						if (Ext.Array.contains(ds.dbGridField.split(';'), k)) {
							record.set(ds.field, data[k]);
						}
					}
				});
			});
		});
	}
});