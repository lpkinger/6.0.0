Ext.define('erp.view.pm.mps.DeskProductGridPanel2',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.DeskProductGridPanel2',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    id:'grid2',
    store: [],
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	LastCondition:"",
	gridcondition:'',
	dockedItems: [{
    	id : 'pagingtoolbar2',
        xtype: 'erpMpsToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    initComponent : function(){
	    caller='Desk!MPSPRonorder';
	    this.getCount(caller,"");
    	this.callParent(arguments); 
	},
	getCount: function(c, condition){
		var me=this;
		c=c||caller;
		condition=me.getCondition(condition);
		var me = this;
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + 'pm/deskproduct/getMPSPRonorderCount.action',
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
	getCondition:function(condition){
		 condition =(!condition || condition=="")?"":condition;
		    if(condition!="" && BaseQueryCondition!=""){
		    	condition=condition+" AND "+BaseQueryCondition;
		    }else if(condition==""){
		    	condition=BaseQueryCondition;
		    }
		condition=condition.replace(/pr_code/g,'pcode');
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
	exportAction : 'pm/deskproduct/pronorder/create.xls',
	getColumnsAndStore: function(caller, condition, page, pageSize,id){	
		var me = this;
		me.gridcondition=condition;
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'pm/deskproduct/getMPSPRonorder.action',
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
            		}else {
            			var store = Ext.create('Ext.data.Store', {
                		    fields: res.fields,
                		    data: data
                		});
            			Ext.getCmp('grid2').reconfigure(store, res.columns);
            		}            	 
            	  Ext.getCmp('pagingtoolbar2').afterOnLoad();
            	  Ext.Ajax.request({//拿到grid的columns
                  	url : basePath + 'pm/mps/getSum.action',
                  	params: {
                  		caller: caller,
                  		fields:'qty',
                  		condition:condition,
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
                  			var text=Ext.getCmp('text2');
                  			if(text){
                   				text.setText('数量 :'+obj.qty);
                   			}else {
                   			Ext.getCmp('pagingtoolbar2').insert(16,{
                  				xtype:'tbtext',
                  				id:'text2',
                  				text:'数量:'+obj.qty
                  			});
                   			}                  			
                  		}
                  	}
                  });	
        	}
        });
	}
});