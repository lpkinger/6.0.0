Ext.define('erp.view.pm.mps.DeskProductGridPanel3',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.DeskProductGridPanel3',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    height:height,
    id:'grid3',
    frame:true,
    store: [],
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	verticalScrollerType: 'paginggridscroller',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
    loadMask: true,
    disableSelection: true,
    invalidateScrollerOnRefresh: false,
    LastCondition:"",
    gridcondition:"",
    viewConfig: {
            trackOver: false
       },
	dockedItems: [{
    	id : 'pagingtoolbar3',
        xtype: 'erpMpsToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	initComponent : function(){
	    caller='Desk!MakeCommit';
	    this.getCount(caller,"");
    	this.callParent(arguments); 
	},
	getCount: function(c, condition){
		c=c||caller;
		condition=this.getCondition(condition);
		var me = this;
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/common/datalistCount.action',
        	params: {
        		caller: c,
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
	getCondition:function(condition){
	    condition =(!condition || condition=="")?"":condition;
	    if(condition!="" && BaseQueryCondition!=""){
	    	condition=condition+" AND "+BaseQueryCondition;
	    }else if(condition==""){
	    	condition=BaseQueryCondition;
	    }
		condition=condition.replace(/pr_code/g,'mm_prodcode');
    	if(!Ext.isEmpty(this.defaultCondition)) {
    		condition = this.defaultCondition;
    	}
    	if(!Ext.isEmpty(this.filterCondition)) {
    		if(condition == '' ) {
    			condition = this.filterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
    		}
    	}
    	return condition;
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var querycondition = null;
                for(var fn in filters){
                    var value = filters[fn],f = grid.getHeaderFilterField(fn);
                    if(!Ext.isEmpty(value)){
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
    	                    		if(!f.autoDim) {
    	                    			value = fn + " LIKE '" + value + "%' ";
    	                    		} else {
    	                    			value = fn + " LIKE '%" + value + "%' ";
    	                    		}
    	                    	}
                        	}
                    	}
                    	if(querycondition == null){
                    		querycondition = value;
                    	} else {
                    		querycondition = querycondition + " AND " + value;
                    	}
                    }
                }
                this.filterCondition = querycondition;
                page = 1;
                this.getCount(caller,"");
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    },
	getColumnsAndStore: function(caller, condition, page, pageSize){	
		var me = this;
		me.gridcondition=condition;
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/datalist.action',
        	params: {
        		caller: caller,
        		condition:  condition, 
        		page: page,
        		pageSize: pageSize,
        		_noc:1
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];//一定要去掉多余逗号，ie对此很敏感        		
        			var grid = this;
        			if(me.columns && me.columns.length > 2){
            			me.store.loadData(data);
            		}else {
            			var store = Ext.create('Ext.data.Store', {
                		    fields: res.fields,
                		    data: data
                		});
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
                          		column.renderer = grid.RenderUtil[renderName];
                          		column.haveRendered = true;
                          	}
                          });  
            			Ext.getCmp('grid3').reconfigure(store, res.columns);
            		} 	   
            	   Ext.getCmp("pagingtoolbar3").afterOnLoad();       
            	   Ext.Ajax.request({//拿到grid的columns
                   	url : basePath + 'pm/mps/getSum.action',
                   	params: {
                   		caller: caller,
                   		condition:condition,
                   		fields:'mm_qty-mm_havegetqty',
                   		_noc:1
                   	},
                   	method : 'post',
                   	callback : function(options,success,response){
                   		me.BaseUtil.getActiveTab().setLoading(false);
                   		var res = new Ext.decode(response.responseText);
                   		if(res.exception || res.exceptionInfo){
                   			showError(res.exceptionInfo);
                   			return;
                   		}else if(res.success){
                   			var obj=new Ext.decode(res.data);
                   			var text=Ext.getCmp('text3');
                   			if(text){
                   				text.setText('未领数量 :'+obj['mm_qty-mm_havegetqty']);
                   			}else {
                   			Ext.getCmp('pagingtoolbar3').insert(16,{
                  				xtype:'tbtext',
                  				id:'text3',
                  				text:'未领数量 :'+obj['mm_qty-mm_havegetqty']
                  			});
                   			}
                   		}
                   	}
                   });	
        	}
        });
	}
});