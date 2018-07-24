Ext.define('erp.view.ma.update.EmpgridLeft',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.empgridleft',
	layout:'column',
	id: 'dbfindGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store:[],
    columns:[],
    defaultcolumns: [{align: "left",cls: "x-grid-header-1",dataIndex: "em_code",flex: 1,header: "员工编号",text: "员工编号",filter:{autoDim: true,dataIndex: 'em_code',
    					displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
    		 },{align: "left",cls: "x-grid-header-1",dataIndex: "em_name",flex: 1,header: "员工姓名",text: "员工姓名",filter:{autoDim: true,dataIndex: 'em_name',
    					displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
    		},{align: "left",cls: "x-grid-header-1",dataIndex: "em_defaulthsname",flex: 1,header: "岗位",text: "岗位",filter:{autoDim: true,dataIndex: 'em_defalthsname',
    					displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
    		},{align: "left",cls: "x-grid-header-1",dataIndex: "em_defaultorname",flex: 1,header: "组织",text: "组织",filter:{autoDim: true,dataIndex: 'em_defaultorname',
    					displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}}],
    multiselected: new Array(),
    selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	ignoreRightMouseSelection : false,
	    	checkOnly: true
	}),
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('Ext.ux.grid.GridHeaderFilters')],
	initComponent : function(){ 
		this.callParent(arguments);
		this.getCount();
	},
	RenderUtil: Ext.create('erp.util.RenderUtil'),
	getColumnsAndStore: function(c, d, g, s, callback){
		var me = this;
		c = c || caller;
		d = d || condition;
		g = g || page;
		s = s || pageSize;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		me.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'ma/update/getEmpdbfindData.action',
        	method : 'post',
        	params : {
        		fields:'em_code,em_name,em_defaulthsname,em_defaultorname',
	   			condition: f,
	   			page: g,
	   			pagesize: s
	   		},
        	callback : function(options, success, response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        		if(callback)
        			callback.call(me, data);
        		else {
        			if(me.columns && me.columns.length > 2){
        				me.store.loadData(data);
            			if(me.store.data.items.length != data.length){
            				me.store.removeAll();
            				me.store.add(data);
            			}
        			}else{
        				var grid = this;
            			me.reconfigure(Ext.create('Ext.data.Store', {
                		    fields:[{name: 'em_code',type: 'string'},{name: 'em_name',type: 'string'},
       					 		{name: 'em_defaulthsname',type: 'string'},{name: 'em_defaultorname',type: 'string'}],
                		    data: data
                		}),me.defaultcolumns);
                		if(me.store.data.items.length != data.length){
            				me.store.removeAll();
            				me.store.add(data);
            			}
        			}
        			Ext.getCmp('pagingtoolbar').afterOnLoad();
        		}
        	}
        });
	},
	getCount: function(c, d, callback){
		var me = this;
		c = 'UpdateScheme';
		d = d|| condition;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + 'common/getCountByTable.action',
        	params : {
        		tablename: 'employee',
				condition: f
	   		},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		dataCount = res.count;
        		if(callback) 
        			callback.call(me, dataCount, c, d);
        		else
        			me.getColumnsAndStore(c, d);
        	}
        });
	},
	selectDefault: function(){
		var grid = this;
	},
	addToRight:function(){
		 	var gridL=Ext.getCmp('dbfindGridPanel'),gridR=Ext.getCmp('selectgrid');
		 	var o1=new Object(),o2=new Object(),a1=new Array(),a2=new Array();
		 	Ext.each(gridL.selModel.getSelection(),function(ss){
	            o1[ss.data.em_code]=ss.data;
		 	});	
			Ext.each(gridR.store.data.items,function(ss){
			if(ss.data.em_code!=''){
				 o1[ss.data.em_code]=ss.data;
			}
		 	});	
		 	var keys=Ext.Object.getKeys(o1);
			Ext.each(keys, function(k){
				a1.push(o1[k]);
			});
		 	gridR.getStore().loadData(a1);
	       	gridL.getSelectionModel().select([]);
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
                this.getCount();
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    }
});