Ext.require([
    'erp.util.*' 
]);
Ext.define('erp.view.common.datalist.GridPanel2',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpDatalistGridPanel2',
	layout : 'fit',
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    condition1:'',
    keyF:'',
    ur:'',
    page:'',
    codeF:'',
    pagesize:'',
    buttons: [
              { 
            	text: '编辑',
            	handler:function(){
            		if(!this.up('grid').getSelectionModel().selected.items[0]){
            			alert("请选择要编辑的行");
            			return;
            		}
            		var data=this.up('grid').getSelectionModel().selected.items[0].data;
            		var s=this.up('grid').ur+'?formCondition='+this.up('grid').keyF+'IS'+data[this.up('grid').keyF];
            		var html='<iframe width=100% height=100% src="'+s+'"/>';
            		var grid=this.up('grid');
            		var win=new Ext.window.Window({
        	    		height:400,
        	    		width:800,
        	    		modal:true,
        	    		listeners : {
    	    				close : function(){
    	    					grid.getColumnsAndStore();
    	    				}
    	    			},
        	    		html:html});
        	    	win.show();
            	}
            	  },{
            		  text: '添加',
            			handler:function(){
            				var grid=this.up('grid');
            				var u='';
            				if(/\?/.test(grid.ur)){
            					u+=grid.ur+'&dbfind='+grid.condition1;
            				}else{
            					u+=grid.ur+'?dbfind='+grid.condition1;
            				}
            				var win=new Ext.window.Window({
                	    		height:400,
                	    		width:800,
                	    		modal:true,
                	    		listeners : {
            	    				close : function(){
            	    					grid.getColumnsAndStore();
            	    				}
            	    			},
                	    		html:'<iframe id="iframe_add" width=100% height=100% src="'+u+'"/>'});
                	    	win.show();
            			}
                	}
            	  
            ],
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	//RenderUtil:Ext.create('erp.util.RenderUtil'),
	initComponent : function(){
		var con=this.BaseUtil.getUrlParam('formCondition');
		if(con){
			if(/IS/g.test(con)){//datalist页面跳转的参数是带'IS'的，query页面跳转的参数是用'='的
				if(/id/g.test(this.codeF)){
					con=this.codeF+'='+con.split('IS')[1];
				}else{
					con=this.codeF+"='"+con.split('IS')[1]+"'";
				}
			}else{
				if(/id/g.test(this.codeF)){
					con=this.codeF+'='+con.split('=')[1];
				}else{
					con=this.codeF+"='"+con.split('=')[1]+"'";
				}
			}
			
		}
		this.condition1=this.condition1==''?con:this.condition1;
		this.defaultCondition = this.condition1;//固定条件；从url里面获取
    	this.caller=this.caller|| this.BaseUtil.getUrlParam('whoami');
		this.getCount(this.caller, this.condition1);
		this.callParent(arguments); 
		this.addEvents({
		    keydown: true
		});
	} ,
	getColumnsAndStore: function(c, d, g, s){
		c = c || this.caller;
		d = d || this.condition1;
		g = g || this.page;
		s = s || this.pageSize;
		var me = this;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		this.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/datalist.action',
        	params: {
        		caller: c,
        		condition:  f, 
        		page: g,
        		pageSize: s
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];//一定要去掉多余逗号，ie对此很敏感
        		if(me.columns && me.columns.length > 2){
        			me.store.loadData(data);
        			if(me.lastSelected && me.lastSelected.length > 0){//grid刷新后，仍然选中上次选中的record
            			Ext.each(me.store.data.items, function(item){
            				if(item.data[keyField] == me.lastSelected[0].data[keyField]){
            					me.selModel.select(item);
            				}
            			});
            		}
        		} else {
        			if(!Ext.isChrome){
        				Ext.each(res.fields, function(f){
        					if(f.type == 'date'){
        						f.dateFormat = 'Y-m-d H:i:s';
        					}
        				});
        			}
        			var store = Ext.create('Ext.data.Store', {
            		    fields: res.fields,
            		    data: data,
            		    //模糊查询的结果在Ext.Array.filter()方法之后，部分数据被过滤掉,设置为false不调用该方法
            		    //yingp
            		    filterOnLoad: false 
            		});
    				//处理render
        			var width=0;
        			var grid = this;
                    Ext.Array.each(res.columns, function(column, y) {
                    	width+=column.width;
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
                    if(width<grid.innerWidth){
                    	 Ext.Array.each(res.columns, function(column) {
                    		 column.width=column.width*grid.innerWidth/width;
                    	 });
                    }
            		me.reconfigure(store, res.columns);//用这个方法每次都会add一个checkbox列
        		}
        		//拿到datalist对应的单表的关键词
        		keyField = res.keyField;//form表主键字段
        		pfField = res.pfField;//grid表主键字段
        		url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
        		me.ur=url;
        		relative = res.relative;
        	}
        });
	},
	getCount: function(c, d){
		c = c || caller;
		d = d || condition;
		var me = this;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/common/datalistCount.action',
        	params: {
        		caller: c,
        		condition: f
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		dataCount = res.count;
        		me.getColumnsAndStore(c, d);
        	}
        });
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
                        		value = fn + "=to_date('" + value + "','yyyy-MM-dd') ";
                        	} else {
                        		var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
                        		exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
    	                    	if(exp_d.test(value)){
    	                    		value = fn + "=to_date('" + value + "','yyyy-MM-dd') ";
    	                    	} else if(exp_t.test(value)){
    	                    		value = fn + "=to_date('" + value + "','yyyy-MM-dd HH24:mi:ss') ";
    	                    	} else{
    	                    		value = fn + " LIKE '%" + value + "%' ";
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
                this.getCount();
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    },
    reconfigure: function(store, columns){
    	//改写reconfigure方法
    	var d = this.headerCt;
    	if (this.columns.length <= 1 && columns) {//this.columns.length > 1表示grid的columns已存在，没必要remove再add
			d.suspendLayout = true;
			d.removeAll();
			d.add(columns);
		}
		if (store) {
			this.bindStore(store);
		} else {
			this.getView().refresh();
		}
		if (columns) {
			d.suspendLayout = false;
			this.forceComponentLayout();
		}
		this.fireEvent("reconfigure", this);
    },
    getCondition: function(){
    	var condition = '';
    	if(!Ext.isEmpty(this.defaultCondition)) {
    		condition = this.defaultCondition;
    	}
    	if(!Ext.isEmpty(this.filterCondition)) {
    		if(condition == '') {
    			condition = this.filterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
    		}
    	}
    	return condition.replace(/=/g, '%3D');
    }
});