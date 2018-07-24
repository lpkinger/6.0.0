Ext.define('erp.view.common.dbfind.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpDbfindGridPanel',
	layout : 'fit',
	id: 'dbfindGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
		headerWidth: 0
	}),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	bodyStyle:'background-color:#f1f1f1;',
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpDbfindToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	initComponent : function(){		
		if(likecondition)this.filterCondition=likecondition;
		this.callParent(arguments);
		this.getCount();
	},
	getColumnsAndStore: function(){
		var me = this;
		var c = condition;
		if(typeof trigger.getExtraCondition=='function'){
			var con = trigger.getExtraCondition();
	    	if(con!=''){
	    		c +=  ' AND ' + con;
	    	}
		}
		if(me.filterCondition){
			c += ' AND ' + me.filterCondition;
		}
		me.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/dbfind.action',
        	method : 'post',
        	params : {
        		which : which,
	   			caller : caller,
	   			field: key,
	   			condition: c,
	   			ob: dbOrderby,// dynamic order by 
	   			page: page,
	   			pageSize: pageSize,
	   			_config:getUrlParam('_config')
	   		},
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.autoHeight) {
        			me.addCls('custom-grid-autoheight');//自适应样式
        		}
        		var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        		if(me.columns && me.columns.length > 2){
        			me.store.loadData(data);
        			if(me.store.data.items.length != data.length){
        				me.store.removeAll();
        				me.store.add(data);
        			}
        			//修改pagingtoolbar信息
            		Ext.getCmp('pagingtoolbar').afterOnLoad();
        		} else {
        			me.reconfigure(Ext.create('Ext.data.Store', {
            		    storeId: 'dbfindGridPanelStore',
            		    fields: res.fields,
            		    data: data
            		}), res.columns);
            		//修改pagingtoolbar信息
            		Ext.getCmp('pagingtoolbar').afterOnLoad();
            		dbfinds = res.dbfinds;
            		//通用变更单dbfind 采用变更原有单据的放大镜 
            		if(trigger.isCommonChange){
            			
            			Ext.Array.each(dbfinds,function(d){
            				d.field=d.field+'-new';
            			});
            		}
            		me.resetable = res.reset;//允许重置条件
            		//me.isFast = res.Isfast;//大数据，放大镜不取count
            		trigger.dbfinds = dbfinds;
            		if(me.resetable){
            			var win = parent.Ext.getCmp('dbwin');
            			win && win.down('#reset').show();
            		}
        		}
        	}
        });
	},
	getCount: function(){
		var me = this;
		var c = condition;
		if(typeof trigger.getExtraCondition=='function'){
			var con = trigger.getExtraCondition();
	    	if(con!=''){
	    		c +=  ' AND ' + con;
	    	}
		}
		if(me.filterCondition){
			c += ' AND ' + me.filterCondition;
		}
		if(me.isFast){//解决商城询价
			dataCount = 1000*pageSize;// 直接作1000页数据处理 
    		me.getColumnsAndStore();
    		return;
		}
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + 'common/dbfindCount.action',
        	params : {
        		which : which,
	   			caller : caller,
	   			field: key,
	   			condition: c,
	   			_config:getUrlParam('_config')
	   		},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.count == -1){
        			dataCount = 1000*pageSize;// 直接作1000页数据处理
        			me.isFast=true;
        		}else{
            		dataCount = res.count;
        		}
        		me.getColumnsAndStore();
        	}
        });
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn], f = grid.getHeaderFilterField(fn);
                    if(!Ext.isEmpty(value)) {
                    	if("null"!=value){
	                    	if(f.originalxtype == 'numberfield') {
                    			if(value.indexOf('>=')==0||value.indexOf('<=')==0||value.indexOf('>')==0||value.indexOf('<')==0||value.indexOf('!=')==0||value.indexOf('=')==0){
                					if(value.indexOf('!=')==0){
                						value = "("+fn + value + " or "+fn +" is null) ";
                					}else{
                						value = fn + value + " ";
                					}
                    			}else if(value.indexOf('~')>-1){
                    				var arr = value.split('~');
                    				value = fn + " between " + arr[0] + " and "+arr[1]+" ";
                    			}else{
                					value = fn + "=" + value + " ";
                				}
	                    	} else if(f.originalxtype == 'datefield'){
	                    			if(value.indexOf('=')>-1){
	                    				var valueX = value.split('=')[1];
	                    				var length = valueX.split('-').length;
	                    				if(length<3){
	                    					if(length == 1){
	                    						var value1 = Ext.Date.toString(new Date(valueX+'-01-01'));
	                    						var value2 = Ext.Date.toString(new Date(valueX+'-12-31'));
	                    						value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    					}else if(length == 2){
	                    						var day = new Date(valueX.split('-')[0],valueX.split('-')[1],0);
	                    						var value1 = Ext.Date.toString(new Date(valueX+'-01'));
	                    						var value2 = Ext.Date.toString(new Date(valueX+'-'+day.getDate()));
	                    						value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    					}
		                    			}else {
		                    				if(value.indexOf('>=')==0){
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')>='" + value + "' ";
			                    			}else if(value.indexOf('<=')==0){
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')<='" + value + "' ";
			                    			}else {
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
			                    			}
		                    			}
	                    			}else if(value.indexOf('~')>-1){
                    					var value1 = Ext.Date.toString(new Date(value.split('~')[0]));
                        				var value2 = Ext.Date.toString(new Date(value.split('~')[1]));
	                            		value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    			}else{
	                    				value = Ext.Date.toString(new Date(value));
	                            		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
	                    			}
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
	    	                    				value = ' 1=1 ';
	    	                    			} else {
	    	                    				if (f.column && f.column.xtype == 'yncolumn'){
	    	                    					if (value == '-无-') {
	            	                    				value = fn + ' is null ';
	            	                    			} else {
	            	                    				value = fn + ((value == '是' || value == '-1' || value == '1') ? '<>0' : '=0');
	            	                    			}
	             	                    		} else {
	             	                    			if (value == '-无-') {
	            	                    				value = 'nvl(to_char(' + fn + '),\' \')=\' \'';
	            	                    			} else {
	            	                    				if(value)value=value.replace(/\'/g,"''");
	            	                    				value = fn + " LIKE '" + value + "%' ";
	            	                    			}
	             	                    		}
	    	                    			}
	    	                    		} else if(f.xtype == 'datefield') {
	    	                    			value = "to_char(" + fn + ",'yyyy-MM-dd') like '%" + value + "%' ";
	    	                    		} else if(f.column && f.column.xtype == 'numbercolumn'){
	    	                    			if(f.column.format) {
	    	                    				var precision = f.column.format.substr(f.column.format.indexOf('.') + 1).length;
	    	                    				//防止to_char去除小数点前面的0
	    	                    				if(-1<value&&value<1){
		    	                    				var number = value;
		    	                    				value = "to_char(round(" + fn + "," + precision + "),";	    	                    		
		    	                    				value += "'fm0.";
		    	                    				for(var i=0;i<precision;i++){
		    	                    					value += "0";
		    	                    				}
		    	                    				value += "') like '%" + number + "%' ";
		    	                    			}else{
		    	                    				value = "to_char(round(" + fn + "," + precision + ")) like '%" + value + "%' ";
		    	                    			}
	    	                    			} else
	    	                    				value = "to_char(" + fn + ") like '%" + value + "%' ";
	    	                    		} else {
	    	                    			/**字符串转换下简体*/
	    	                    			if(value)value=value.replace(/\'/g,"''");
	    	                    			var SimplizedValue=this.BaseUtil.Simplized(value);   	                    	
	    	                    			//可能就是按繁体筛选  
	    	                    			if(f.ignoreCase) {// 忽略大小写
	        	                    			fn = 'upper(' + fn + ')';
	        	                    			value = value.toUpperCase();
	        	                    		}
	        	                    		if(!f.autoDim) {
	        	                    			if(SimplizedValue!=value){
	        	                    				value = "("+fn + " LIKE '" + value + "%' or "+fn+" LIKE '"+SimplizedValue+"%')";
	        	                    			}else value = fn + " LIKE '" + value + "%' ";       	                    			
	        	                    			
	        	                    		} else if(f.filterSelect||f.inputEl.dom.disabled||(f.rawValue==''&&f.emptyText==value)){
		        	                    		if(f.filterType == 'direct'){
		        	                    			value=fn+"='"+value+"'";
		        	                    		} else if(f.filterType == 'nodirect'){
		        	                    			value="nvl("+fn+",' ')<>'"+value+"'";
		        	                    		} else if(f.filterType == 'head'){
		        	                    			value = fn + " LIKE '" + value + "%' ";
		        	                    		} else if(f.filterType == 'end'){
		        	                    			value = fn + " LIKE '%" + value + "' ";
		        	                    		} else if(f.filterType == 'null'){
		        	                    			value = fn + " is null";
		        	                    		} else if(f.filterType == 'novague'){
		        	                    			if(SimplizedValue!=value){
		        	                    				value = "("+fn + " not LIKE '%" + value + "%' and "+fn+" not LIKE '%"+SimplizedValue+"%' or "+fn+" is null)";
		        	                    			}else value = "("+fn + " not LIKE '%" + value + "%' or "+fn+" is null)";
		        	                    		} else{
		        	                    			if(SimplizedValue!=value){
		        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
		        	                    			}else value = fn + " LIKE '%" + value + "%' ";
		        	                    			f.filterType = '';
		        	                    		}
		        	                    		f.filterSelect = false;
	        	                    		}else {
	        	                    			if(SimplizedValue!=value){
	        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
	        	                    			}else value = fn + " LIKE '%" + value + "%' ";
	        	                    			f.filterType = '';
	        	                    		}
	    	                    		}
	    	                    	}
	                        	}
                    	}else value ="nvl("+fn+",' ')=' '";
                    	if(condition == null){
                    		condition = value;
                    	} else {
                    		condition = condition + " AND " + value;
                    	}
                    }
                }
                this.filterCondition = condition;
                this.fromHeader = true;
                this.fromFilter = true;
                page = 1;
                //考虑部分应用于查询界面，存在form条件
                var form = Ext.getCmp('dealform');
                if(form){
                  this.getCount(caller,form.getCondition(grid));
                }else this.getCount();

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
    		if(!Ext.isChrome){//ie,firefox下，format出现NaN-NaN-NaN,暂时作string处理
    			Ext.each(columns, function(c){
    				if(c.xtype == 'datecolumn'){
    					c.xtype = "";
    					c.format = "";
    				}
    			});
    		}
			d.suspendLayout = true;
			d.removeAll();
			//填充空列
			columns.push({
				filter: {xtype: 'textfield',editable:false,readOnly:true},
			    filterJson_: {},
            	id:'x-dbfind-lastrow',
            	flex:1
            });
			d.add(columns);
		}
		if (store) {
			if(!Ext.isChrome){//ie,firefox下，format出现NaN-NaN-NaN
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
    },
    /**
     * 重置条件
     * dbfindSet.ds_allowreset(1-允许)
     * dbfindSetUI.ds_allowreset(1-允许)
     */
    resetCondition: function(){
    	condition = this.getCondition();
    	this.filterCondition = '';
    	var fields = this.plugins[0].fields;
    	Ext.each(Ext.Object.getKeys(fields), function(key){
    		fields[key].reset();
    	});
    },
    getCondition: function() {
    	condition = 'upper(' +  key + ") like '%%'";
    	if(!trigger.ownerCt || trigger.column){//如果是grid的dbfind
    		condition = 'upper(' + keyField + ") like '%%'";
    		if(dbGridCondition && dbGridCondition != null){
    			condition += " AND " + decodeURIComponent(dbGridCondition).replace(/\s{1}IS\s{1}/g, '=');
    		}
    	}
    	if(dbCondition && dbCondition != null){
    		condition += " AND " + decodeURIComponent(dbCondition).replace(/\s{1}IS\s{1}/g, '=');
    	}
    	if(dbBaseCondition && dbBaseCondition != null){
    		condition += " AND " + decodeURIComponent(dbBaseCondition).replace(/\s{1}IS\s{1}/g, '=').replace(/@/g,'%');
    	}
    	return condition;
    }
});