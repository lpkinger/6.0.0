Ext.define('erp.view.oa.knowledge.KnowledgeMappingGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpknowledgemappinggrid',
	layout : 'fit',
	id: 'knowledgemappingGridPanel',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    readOnly:true,
    store: [],
    columns: [],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    conditon:null,
    caller: null,
    setReadOnly:function(bool){
      this.readOnly=  bool;  
    },
	initComponent : function(){ 
	    var urlCondition = this.BaseUtil.getUrlParam('mappingCondition'); 
	    if(urlCondition){
	    urlCondition = urlCondition.replace(/IS/g,"=").replace(/NO/g,"!=");
	    }
    	var gridParam = {caller: this.caller || caller, condition: urlCondition};
    	this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments);  
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		var main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: true,
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			Ext.each(res.columns, function(column, y){
        				//render
        				if(!column.haveRendered && column.renderer != null && column.renderer != ""){
        					if(!grid.RenderUtil){
        						grid.RenderUtil = Ext.create('erp.util.RenderUtil');
        					}
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
            							if(val != null && val.toString().trim() != ''){
            								return val;
            							} else {
            								return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
            					  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
            							}
            					   };
            					}
            				} else if(logic == 'groupField'){
            					grid.groupField = column.dataIndex;
            				}
        				}
        			});
            		var data = [];
            		if(res.data.length>2){
            		  data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		}else{
            		 var array=new Array();
            		 for(var i=0;i<res.columns.length;i++){
            	        var object=new Object();
            	        var dataIndex=res.columns[i].dataIndex;
            	        array.push("'"+dataIndex+"':'"+ $I18N.common.grid.emptyText+"'");		      
            		  } 
            		 data=Ext.decode('{'+array+'}');
            		}
            		var store = Ext.create('Ext.data.Store', {
            		    storeId: 'gridStore',
            		    fields: res.fields,
            		    data: data,
            		    groupField: grid.groupField,
            		    getSum: function(records, field) {
            	            var total = 0,
            	                i = 0,
            	                len = records.length;
            	            for (; i < len; ++i) {
            	            	//重写getSum,grid在合计时，只合计填写了必要信息的行
            	            	var necessary = records[i].get(grid.necessaryField);
            	            	if(necessary != null && necessary != ''){
            	            		total += records[i].get(field);
            	            	}
            	            }
            	            return total;
            		    },
            		    getCount: function() {
            		    	var count = 0;
            		    	Ext.each(this.data.items, function(item){//重写getCount,grid在合计时，只合计填写了必要信息的行
            		    		if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
            		    			count++;
            		    		}
            		    	});
            		        return count;//this.data.length || 0;
            		    }
            		});
            		if(grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		if(res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
    				//toolbar
            		var items = [];
            		var bool = true;
            		Ext.each(grid.dockedItems.items, function(item){
        				if(item.dock == 'bottom' && item.items){//bbar已存在
        					bool = false;
        				}
        			});
            		
            		grid.reconfigure(store, res.columns);
            		var form = Ext.ComponentQuery.query('form')[0];
        			if(form){
        				if(form.readOnly){
        					grid.readOnly = true;//grid不可编辑
        				}
        			}
        		} else {
        			grid.hide();
        		}
        	}
        });
	},
});