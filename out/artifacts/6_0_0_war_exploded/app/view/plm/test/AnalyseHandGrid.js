Ext.require([
    'erp.util.*' 
]);
Ext.define('erp.view.plm.test.AnalyseHandGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpAnalyseHandGridPanel',
	layout : 'fit',
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',   
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	 features: [{         
         ftype: 'summary',
         groupHeaderTpl: '{name}',
         hideGroupedHeader: true,
         enableGroupingMenu: false,
     }],
	initComponent : function(){
	    this.getColumnsAndStore(condition); 
		this.callParent(arguments); 
	} ,
	getColumnsAndStore: function(condition){
	   condition=condition==''?"1=1":condition;
	   startdate=startdate==''?'2012-04-01':startdate;
	   enddate=enddate==''?Ext.Date.format(new Date(),'Y-m-d'):enddate;
		var me = this;
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + this.url,
        	params: {       		
        		condition: condition,
        		startdate:startdate,
        		enddate:enddate 
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data=res.data;
     	if(me.columns && me.columns.length > 2){
        			me.store.loadData(data);
        		} else {
        			var store = Ext.create('Ext.data.Store', {
            		    fields: res.fields,
            		    data: data
            		});
            		var grid=this;
    				  Ext.Array.each(res.columns, function(column, y) {   
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
                    			//这里只能用column.dataIndex来标志，不能用x,y,index等，
                    			//grid在render时，checkbox占一列
                    		}
                    		column.flex=1;
                    		column.renderer = grid.RenderUtil[renderName];
                    		column.haveRendered = true;
                    		
                    	}
        				if(column.summaryType!=""){
        					if(column.summaryType=='sum'){
        						column.summaryRenderer=function(value, summaryData, dataIndex) {
                                    return  '<h2>' + value + '</h2>' ;
                                };
        					}else {
        						column.summaryRenderer=function(value, summaryData, dataIndex) {
                                    return  '<h2>共' + value + ' 项</h2>' ;
                                };
        					}
                		}
        				
                    });              
            		me.reconfigure(store, res.columns);//用这个方法每次都会add一个checkbox列
        		}
        		//keyField = res.keyField;//form表主键字段
        	}
        });
	},
	getCount: function(caller, condition){
		var me = this;
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/common/datalistCount.action',
        	params: {
        		caller: caller,
        		condition: condition,
        		_noc:1
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		dataCount = res.count;
        		me.getColumnsAndStore(caller, condition, page, pageSize);
        	}
        });
	},
	listeners: {
        'beforeheaderfiltersapply': function(grid, filters) {
        	var condition = "1=1";
            for(var fn in filters){
                var value = filters[fn];
                if(!Ext.isEmpty(value)){
                	value = " AND " + fn + " LIKE '%" + value + "%' ";
                	Ext.each(grid.columns, function(c){
    					if(c.dataIndex == fn && c.xtype == 'datecolumn'){//日期形式的特殊处理成oracle支持的格式哦
    						value = " AND " + fn + "=to_date('" + Ext.Date.toString(filters[fn]) + "','YYYY-MM-DD') ";
    					}
    				});
                    condition = condition + value;
                }
            }
            if (condition == "1=1") return false;
            this.getColumnsAndStore(caller, condition, page, pageSize);
        	return false;
        }
    },
    reconfigure: function(store, columns){
    	//改写reconfigure方法
    	var d = this.headerCt;
    	if (this.columns.length <= 1 && columns) {//this.columns.length > 1表示grid的columns已存在，没必要remove再add
    		if(Ext.isIE){//ie下，format出现NaN-NaN-NaN,暂时作string处理
    			Ext.each(columns, function(c){
    				if(c.xtype == 'datecolumn'){
    					c.xtype = "";
    					c.format = "";
    				}
    			});
    		}
			d.suspendLayout = true;
			d.removeAll();
			d.add(columns);
		}
		if (store) {
			if(Ext.isIE){//ie下，format出现NaN-NaN-NaN
				Ext.each(store.fields, function(f){
					if(f.type == 'date'){
						f.type = "string";
						f.format = "";
					}
				});
			}
			this.bindStore(store);
		} else {
			this.getView().refresh();
		}
		if (columns) {
			d.suspendLayout = false;
			this.forceComponentLayout();
		}
		this.fireEvent("reconfigure", this);
    }
});