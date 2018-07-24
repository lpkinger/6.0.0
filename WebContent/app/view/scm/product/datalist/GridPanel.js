Ext.require([
    'erp.util.*' 
]);
Ext.define('erp.view.scm.product.datalist.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpDatalistGridPanel',
	layout : 'auto',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
		headerWidth: 0
	}),
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpDatalistToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    plugins: [Ext.create('erp.view.core.plugin.GridMultiHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	initComponent : function(){ 
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
    	caller = this.BaseUtil.getUrlParam('whoami');
		this.getCount(caller, condition);
		this.callParent(arguments); 
	} ,
	getColumnsAndStore: function(caller, condition, page, pageSize){
		var me = this;
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/datalist.action',
        	params: {
        		caller: caller,
        		condition:  condition, 
        		page: page,
        		pageSize: pageSize
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var store = Ext.create('Ext.data.Store', {
        		    fields: res.fields,
        		    data: res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : []//一定要去掉多余逗号，ie对此很敏感
        		});
				//处理render
                Ext.Array.each(res.columns, function(column) {           
                     if(column.renderer!=null&&column.renderer!="") 
                    column.renderer= me.RenderUtil[column.renderer];                                                              
                     }); 
                if(me.columns && me.columns.length > 2){
                	var data=res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        			me.store.loadData(data);
        			if(me.lastSelected && me.lastSelected.length > 0){//grid刷新后，仍然选中上次选中的record
            			Ext.each(me.store.data.items, function(item){
            				if(item.data[keyField] == me.lastSelected[0].data[keyField]){
            					me.selModel.select(item);
            				}
            			});
            		} 			
        		} else me.reconfigure(store, res.columns);        		
        		//修改pagingtoolbar信息
        		Ext.getCmp('pagingtoolbar').afterOnLoad();
        		//拿到datalist对应的单表的关键词
        		keyField = res.keyField;//form表主键字段
        		pfField = res.pfField;//grid表主键字段
        		url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
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
                	value = " AND " + fn + " LIKE '" + value + "%' ";
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