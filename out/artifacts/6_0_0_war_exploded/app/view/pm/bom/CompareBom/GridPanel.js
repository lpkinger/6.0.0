Ext.define('erp.view.pm.bom.CompareBom.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpQueryGridPanel',
	id: 'querygrid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    GridUtil: Ext.create('erp.util.GridUtil'),
    selModel: Ext.create('Ext.selection.CheckboxModel',{
		headerWidth: 0
	}),
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
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
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
		this.callParent(arguments); 
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
        'headerfiltersapply': function(grid, filters) {
        	grid.summary();
        	return true;
        },
        afterrender: function(grid){
			var me = this;
			if(Ext.isIE && !Ext.isIE11){
				document.body.attachEvent('onkeydown', function(){
					if(window.event.ctrlKey && window.event.keyCode == 67){//Ctrl + C
						var e = window.event;
						if(e.srcElement) {
							window.clipboardData.setData('text', e.srcElement.innerHTML);
						}
					}
				});
			} else {
				document.body.addEventListener("mouseover", function(e){
					if(Ext.isFF5){
						e = e || window.event;
					}
					window.mouseoverData = e.target.value;
		    	});
				document.body.addEventListener("keydown", function(e){
					if(Ext.isFF5){
						e = e || window.event;
					}
					if(e.ctrlKey && e.keyCode == 67){
						me.copyToClipboard(window.mouseoverData);
					}
					if(e.ctrlKey && e.keyCode == 67){
						me.copyToClipboard(window.mouseoverData);
					}
		    	});
			}
		}
	},
	copyToClipboard: function(txt) {
		if(window.clipboardData) { 
			window.clipboardData.clearData(); 
			window.clipboardData.setData('text', txt); 
		} else if (navigator.userAgent.indexOf('Opera') != -1) { 
			window.location = txt; 
		} else if (window.netscape) { 
			try { 
				netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect'); 
			} catch (e) { 
				alert("您的firefox安全限制限制您进行剪贴板操作，请打开'about:config'将signed.applets.codebase_principal_support'设置为true'之后重试"); 
				return false; 
			}
		}
	},
	getColumnsAndStore: function(condition){
		var grid = this;
		grid.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/singleGridPanel.action',
        	params: {
        		caller: this.caller || caller,
        		condition: condition
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = [];
        		if(!res.data || res.data.length == 2){
        			grid.GridUtil.add10EmptyData(grid.detno, data);
        			grid.GridUtil.add10EmptyData(grid.detno, data);
        		} else {
        			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        		}
        		if(grid.columns && grid.columns.length > 2){
        			grid.store.loadData(data);
        		} else {
        			if(res.columns){
            			Ext.each(res.columns, function(column, y){
            				//render
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
            				//logictype
            				var logic = column.logic;
            				if(logic != null){
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
                						column.renderer = function(val){
                							   return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
                					  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
                					   };
                					}
                				} else if(logic == 'groupField'){
                					grid.groupField = column.dataIndex;
                				}
            				}
            				/**
            				 *****如果有固定咧，grid不能分组***** 
            				 */
            			});
                		var store = Ext.create('Ext.data.Store', {
                		    storeId: 'gridStore',
                		    fields: res.fields,
                		    data: data,
                		    groupField: grid.groupField
                		});
                		if(grid.selModel.views == null){
                			grid.selModel.views = [];
                		}
                		if(res.dbfinds.length > 0){
                			grid.dbfinds = res.dbfinds;
                		}
            			grid.columns = res.columns;
            			if(grid.autoRowNumber) {
            				Ext.Array.insert(grid.columns, 0, [{xtype: 'rownumberer', width: 35, locked: true, cls: 'x-grid-header-1'}]);
            			}
            			grid.store = store;
            		}
        		}
        	}
        });
	}
});