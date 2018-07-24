/**
 * 针对ERP中的标签模板设置grid
 */
Ext.define('erp.view.scm.reserve.labelPrintSetting.labelPrintSettingGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpLabelPrintSettingGrid',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	region: 'south',
	layout : 'fit',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    bbar: {xtype: 'erpToolbar',id:'toolbar'},
	tbar:{style:{background:'#fff'},margin:'0 0 5 0',items:[{
		margin:0,
		xtype:'erpSaveButton'
	},{
		margin:'0 0 0 5',
		xtype:'erpDeleteButton'
	},'->',{
		xtype:'erpCloseButton'
	}]},
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
	initComponent : function(){ 
		var gridCondition = this.BaseUtil.getUrlParam('gridCondition');
		gridCondition = (gridCondition == null || gridCondition == "null") ? "" : gridCondition;		
    	var gridParam = {caller: caller, condition: gridCondition};
    	this.getGridColumnsAndStore(gridParam);
		this.callParent(arguments); 
	},
	  getGridColumnsAndStore: function(gridParam){
		var grid = this;
		grid.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/singleGridPanel.action',
        	params:gridParam ,
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
        			grid.add10EmptyData(data,caller);
        			grid.add10EmptyData(data,caller);
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
                        		console.log(renderName);
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
                						/*column.renderer = function(val){
                							   return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
                					  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
                					   };*/
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
                		if(grid.selModel && grid.selModel.views == null){
            			   grid.selModel.views = [];
            		     }
                		if(res.dbfinds.length > 0){
                			grid.dbfinds = res.dbfinds;
                		}
                		res.columns.push({
                        	cls:'x-grid-lastrow',
                        	flex:1
                        });
            			grid.columns = res.columns;
            			if(grid.autoRowNumber) {
            				Ext.Array.insert(grid.columns, 0, [{xtype: 'rownumberer', width: 35, locked: true, cls: 'x-grid-header-1'}]);
            			}
            			grid.store = store;
            		}
        		}
        	}
        });
	},

	getGridStore: function(){
		var grid = this;
		var jsonGridData = new Array();
		var s = grid.getStore().data.items;//获取store里面的数据
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			if(s[i].dirty){
				var bool = true;
				Ext.each(grid.necessaryField, function(f){
					if(data[f] == null){
						bool = false;
						showError("有必填项未填写!代号:" + f);return;
					}
				});
				 var value = data['lps_sql'];	    					
		    	 value = value.toLowerCase();
				 if(value.indexOf("select") == -1 || value.indexOf("from") == -1){
				     showError("取值SQL语句不合法！");
					 return ;
				 }	
				if(bool){
					Ext.each(grid.columns, function(c){
						if(c.xtype == 'datecolumn'){
							if(Ext.isDate(data[c.dataIndex])){
								data[c.dataIndex] = Ext.Date.toString(data[c.dataIndex]);//在这里把GMT日期转化成Y-m-d格式日期
							} else {
								data[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d');//如果用户没输入日期，或输入有误，就给个默认日期，
								//或干脆return；并且提示一下用户
							}
						} else if(c.xtype == 'datetimecolumn'){
							if(Ext.isDate(data[c.dataIndex])){
								data[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
							} else {
								data[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
							}
						} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
							if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
								data[c.dataIndex] = '0';//也可以从data里面去掉这些字段
							}
						}
						if(c.logic =='ignore'){
							delete data[c.dataIndex];
						}
					});
					jsonGridData.push(Ext.JSON.encode(data));
				}
			}
		}
		return jsonGridData;
	},
	add10EmptyData: function(data,caller){		
			for(var i=0;i<10;i++){
				var o = new Object();
				o.lps_caller = lps_caller;
			    o.lps_laid = null;
			    o.lps_lacode = null;
			    o.lps_id = null;
			    o.lps_sql = null;
				data.push(o);
			}
	}
});