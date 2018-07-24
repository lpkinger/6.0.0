Ext.define('erp.view.pm.mps.DeskProductGridPanel6',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.DeskProductGridPanel6',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    id:'grid6',
    store: [],
    columns: new Array(),
    height:height,
    LastCondition:"",
    gridcondition:"",
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	autoQuery: true,
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	initComponent : function(){
	    caller='Desk!MrpResultDetail';	  
	    this.getCount(caller,"");	 
    	this.callParent(arguments); 
	},
	dockedItems: [{
    	id : 'pagingtoolbar6',
        xtype: 'erpMpsToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
	getCount: function(c, condition){
		var me = this;
		 c=c||caller;
		 var findcondition=me.getCondition(condition);
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/common/datalistCount.action',
        	params: {
        		caller: c,
        		condition: findcondition,
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
        		me.getColumnsAndStore(caller, findcondition, page, pageSize);
        	}
        });
	},
	getCondition:function(condition){
		condition =(!condition || condition=="")?mrpcondition:condition+" AND "+mrpcondition;
		 condition =BaseQueryCondition==""?condition:condition+" AND "+BaseQueryCondition;
		 condition=condition.replace(/pr_code/g,'md_prodcode');
		 if(!Ext.isEmpty(this.filterCondition)) {
	    		if(condition == '') {
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
        		var condition = null;
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
    	                    		if (f.xtype == 'combo' || f.xtype == 'combofield') {
    	                    			if (value == '-所有-') {
    	                    				continue;
    	                    			} else if (value == '-无-') {
    	                    				value = 'nvl(' + fn + ',\' \')=\' \'';
    	                    			} else {
    	                    				value = fn + " LIKE '" + value + "%' ";
    	                    			}
    	                    		} else {
    	                    			if(f.ignoreCase) {// 忽略大小写
        	                    			fn = 'upper(' + fn + ')';
        	                    			value = value.toUpperCase();
        	                    		}
        	                    		if(!f.autoDim) {
        	                    			value = fn + " LIKE '" + value + "%' ";
        	                    		} else {
        	                    			value = fn + " LIKE '%" + value + "%' ";
        	                    		}
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
                this.filterCondition = condition;
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
        			
        			if(me.columns && me.columns.length > 2){
            			me.store.loadData(data);
            			Ext.getCmp('pagingtoolbar6').afterOnLoad();
            		}else {
            			var store = Ext.create('Ext.data.Store', {
                		    fields: res.fields,
                		    data: data
                		});              
                		var grid = this;
	                    Ext.Array.each(res.columns, function(column, y) {	                    	
	        				// 处理render
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
            			 Ext.getCmp('grid6').reconfigure(store, res.columns);
            		}            	  
            	   Ext.getCmp('pagingtoolbar6').afterOnLoad();
            	   Ext.Ajax.request({//拿到grid的columns
                   	url : basePath + 'pm/mps/getSum.action',
                   	params: {
                   		caller: caller,
                   		condition:condition,
                   		fields:'md_grossqty#md_usedonhand#md_usedonorder#md_netqty',
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
                   			var text=Ext.getCmp('text6');
                   			if(text){
                   				text.setText('毛需求 :'+obj.md_grossqty +'净需求:'+obj.md_netqty+' 库存分配:'+obj.md_usedonhand+' 在途分配:'+obj.md_usedonorder);
                   			}else {
                   			Ext.getCmp('pagingtoolbar6').insert(16,{
                  				xtype:'tbtext',
                  				id:'text6',
                  				text:'毛需求 :'+obj.md_grossqty +'净需求:'+obj.md_netqty+' 库存分配:'+obj.md_usedonhand+' 在途分配:'+obj.md_usedonorder
                  			});
                   			}
                   		}
                   	}
                   });	
        	}
        });
	},
});