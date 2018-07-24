Ext.define('erp.view.pm.mps.DeskProductGridPanel1',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.DeskProductGridPanel1',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    height:height,
    id:'grid1',
    frame:true,
    store: [],
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	LastCondition:"",
	gridcondition:"",
	dockedItems: [{
    	id : 'pagingtoolbar1',
        xtype: 'erpMpsToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	initComponent : function(){
	    caller='Desk!ProductWh';
	    this.getCount(caller,this.condition);
    	this.callParent(arguments); 
	},
	getCount: function(c, condition){
		var me=this;
		c=c||caller;
		condition=me.getCondition(condition);
		var me = this;
		me.BaseUtil.getActiveTab().setLoading(true);
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/common/datalistCount.action',
        	params: {
        		caller: caller,
        		condition: condition,
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
        		dataCount = res.count;
        		me.getColumnsAndStore(caller, condition, page, pageSize,id);
        	}
        });
	},
	getCondition:function(condition){	    
    	if(!Ext.isEmpty(this.defaultCondition)) {
    		condition = this.defaultCondition;
    	}
    	condition=!condition?"":condition;
    	if(!Ext.isEmpty(this.filterCondition)) {
    		if(!condition || condition == '' ) {
    			condition = this.filterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
    		}
    	}    
    	condition =BaseQueryCondition==""?condition:condition+" AND "+BaseQueryCondition;
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
    	                    		if(!f.autoDim) {
    	                    			value = fn + " LIKE '" + value + "%' ";
    	                    		} else {
    	                    			value = fn + " LIKE '%" + value + "%' ";
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
	getColumnsAndStore: function(caller, condition, page, pageSize,id){	
		var me = this;
		me.gridcondition=condition;
		me.BaseUtil.getActiveTab().setLoading(true);
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
        		}else {
        			var store = Ext.create('Ext.data.Store', {
            		    fields: res.fields,
            		    data: data
            		});
        			Ext.getCmp('grid1').reconfigure(store, res.columns);
        		} 
            		Ext.getCmp('pagingtoolbar1').afterOnLoad();
            		Ext.Ajax.request({//拿到grid的columns
                       	url : basePath + 'pm/mps/getSum.action',
                       	params: {
                       		caller: caller,
                       		condition:condition,
                       		fields:'pw_onhand',
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
                       			var text=Ext.getCmp('text1');
                       			if(text){
                       				text.setText('库存数量 :'+obj['pw_onhand']);
                       			}else {
                       			Ext.getCmp('pagingtoolbar1').insert(16,{
                      				xtype:'tbtext',
                      				id:'text1',
                      				text:'库存数量 :'+obj['pw_onhand']
                      			});
                       			}
                       		}
                       	}
                       });	
        	}
        });
	},
});