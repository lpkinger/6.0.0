Ext.define('erp.view.salary.salaryHisPanel',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.salaryHisPanel',
	layout : 'fit',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 25
	}),
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpDatalistToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	showRowNum:true,
	autoQuery: true,
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [Ext.create('Ext.ux.grid.GridHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')];
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){
    	if(this.autoQuery) {
    		var fields = this.getFields();
    		this.getColumnsAndStore(gridDate, condition, '', '', '', fields);
    	}
		this.callParent(arguments); 
		this.addEvents({
		    keydown: true
		});
	} ,
	getColumnsAndStore: function(c, d, g, s, n, fields){
		c = c || gridDate;
		d = d || this.getCondition();
		g = g || page;
		s = s || pageSize;
		var me = this, rendered = (me.columns && me.columns.length > 2);
		var f = d;
		this.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'salary/getHistory.action',
        	params: {
        		date: gridDate,
        		condition :  f, 
        		page : g,
        		pageSize : s,
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		this.fromFilter = false;
        		if (!response) return;
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data = res.data;
    			me.dataCount = res.num;
        		if(rendered){
        			me.store.loadData(data);
				    var scrollers=me.query('gridscroller');
				    Ext.Array.each(scrollers,function(scroller){
				    	if(scroller.dock=="bottom"){
				    		var el = scroller.scrollEl,
							elDom = el && el.dom;
							if (elDom) {
								elDom.scrollLeft = Ext.Number.constrain(elDom.scrollLeft, 0, elDom.scrollWidth - elDom.clientWidth)-1;
							}
				    	}
				    });
        			if(me.lastSelected && me.lastSelected.length > 0){//grid刷新后，仍然选中上次选中的record
            			Ext.each(me.store.data.items, function(item){
            				if(item.data[keyField] == me.lastSelected[0].data[keyField]){
            					me.selModel.select(item);
            				}
            			});
            		}
        			//修改pagingtoolbar信息
        			me.down('erpDatalistToolbar').afterOnLoad(page);
        		} else {
        			if(!Ext.isChrome){
        				Ext.each(res.fields, function(f){
        					if(f.type == 'date'){
        						f.dateFormat = 'Y-m-d H:i:s';
        					}
        				});
        			}
        			var fs = new Array();
        	    	Ext.each(fields, function(a){
        	    		fs.push(a.id_field);
        	    	});
        	    	fs.push('sl_id');
        	    	fs.push('sl_ilid');
        			var store = Ext.create('Ext.data.Store', {
            		    fields: fs,
            		    data: data,
            		    filterOnLoad: false 
            		});
        			var limits = res.limits, limitArr = new Array();
        			if(limits != null && limits.length > 0) {//权限外字段
    					limitArr = Ext.Array.pluck(limits, 'lf_field');
    				}
        			var grid = this;
               		var col = me.createColumns(fields);
        			me.reconfigure(store, col);//用这个方法每次都会add一个checkbox列
            		me.basecolumns=res.basecolumns;
            		rendered = true;
        		}
        		var toolbar=me.down('erpDatalistToolbar');
        		toolbar.afterOnLoad();
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
	        	                    		if(f.filterSelect||f.inputEl.dom.disabled||(f.rawValue==''&&f.emptyText==value)){
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
	        	                    			if(!f.autoDim) {
		        	                    			if(SimplizedValue!=value){
		        	                    				value = "("+fn + " LIKE '" + value + "%' or "+fn+" LIKE '"+SimplizedValue+"%')";
		        	                    			}else value = fn + " LIKE '" + value + "%' ";       	                    					        	                    			
		        	                    		}else{
		        	                    			if(SimplizedValue!=value){
	        	                    					value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
	        	                    				}else value = fn + " LIKE '%" + value + "%' ";
	        	                    			}
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
                this.getColumnsAndStore();
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
    getCondition: function(isForm){
    	var condition = '';
    	if(!Ext.isEmpty(this.defaultCondition)) {
    		condition = this.defaultCondition;
    	}
    	if (this.searchGrid) {
    		var s = this.searchGrid.getCondition();
    		if(s != null && s.length > 0) {
    			if(condition.length > 0)
        			condition += ' AND (' + s + ')';
    			else
    				condition = s;
    		}
    	}
    	if(!isForm && this.formCondition) {
    		if(condition.length > 0)
    			condition += ' AND (' + this.formCondition + ')';
			else
				condition = this.formCondition;
    	}
    	if(!Ext.isEmpty(this.filterCondition)||this.fromFilter) {
    		if(!Ext.isEmpty(this.filterCondition)){
	    		if(condition == '') {
	    			condition = this.filterCondition;
	    		} else {
	    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
	    		}
    		}
    	}else if(!Ext.isEmpty(this.defaultFilterCondition)){
    		if(condition == '') {
    			condition = this.defaultFilterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.defaultFilterCondition + ')';
    		}
    	}

    	return condition;// .replace(/=/g, '%3D')
    },
    getFields: function() {
    	var  array = [];
     	Ext.Ajax.request({
    		url: basePath + 'system/initDetails.action',
    		params: {
    			caller: 'Salary'
    		},
    		async: false,
    		method: 'post',
    		timeout:10000,
    		callback: function(opts, success, response){
				var res = new Ext.decode(response.responseText);
	    		if(res.exceptionInfo != null){
	    			Ext.getCmp('upexcel').hide();
	    			showError(res.exceptionInfo);return;
	    		} else {
	    			if(res.data)
	    				array = res.data;
	    		}
			} 		
     	});
     	return array;
    },
    createColumns: function(arr){
        			var me = this, d = new Array(),o,flag,
        			arrStr=new Array('sl_detno','sl_name','sl_emcode','sl_phone','sl_type','sl_realpay','sl_status','sl_result','sl_fpid','sl_remark');
        		    //	d.push({xtype: 'rownumberer', width: 20});
        		    	Ext.each(arr,function(a){
        		    		//必显示列
        		    		Ext.each(arrStr,function(b){
        		    			if(a.id_field==b){
        		    				o = new Object();
        		    	    		o.text = a.id_caption;
        		    	    		o.dataIndex = a.id_field;
        		    	    		o.hidden = a.id_visible == 0;
        		    	    		o.width = a.id_width;
        		    	    		o.rule = a.id_rule;
        		    	    		o.dataType = a.id_type;
        		    	    		o.isNeed = a.id_need == 1 ? '是' : '否';
        		    	    		o.logic = a.id_logic;
        		    	    		o.logicdesc = me.parseLogic(o.logic);
        		    	    		obj={
        		    	    			xtype:a.id_type,
        		    	    			dataIndex:a.id_field,
        		    	    			queryMode:"local",
        		    	    			displayField:"display",
        		    	    			valueField:"value",
        		    	    			hideTrigger:true,
        		    	    			ignoreCase:false
        		    	    		};
        		    	    		o.filter=obj;
        		    	    		d.push(o);
        		    			}
        		    		}) ; 		
        		    	});
        		    	d.push({
        		    		text: '工资月份',
        		    		dataIndex: 'sl_date',
        		    		renderer:function(val){
        		    			if(val){
        		    				val=val.replace(/-/g,"/");
        		    				var date=new Date(val);
        		    				return Ext.Date.format(date,'Y年m月');			
        		    			}
        		    		},
        		    	});
        		    	d.push({
        		    		text: 'ilid',
        		    		dataIndex: 'sl_ilid',
        		    		hidden:true,
        		    	});
        		    	d.push({
        		    		text: '工资类型',
        		    		dataIndex: 'sl_type',
        		    	});
        		    	d.push({
        		    		text: '状态',
        		    		dataIndex: 'sl_status',
        		    		width:60,
        		    		renderer:function(val){
        		    			return val?val:'未发送';
        		    		}
        		    	});
        		    	d.push({
        		    		text: '确认结果',
        		    		dataIndex: 'sl_result',
        		    		renderer:function(val){
        		    			if(val==1){
        		    				return '已确认';			
        		    			}else if(val==-1)
        		    				return '报错';
        		    			 else return '未确认';
        		    		},
        		    	});
        		    	d.push({
        		    		text: '签名图片',
        		    		dataIndex: 'sl_fpid',
        		    		width:60,
        		    		renderer:function(val){
        		    			if(val&&val!=0){
        		    				return '<a href='+basePath+'common/downloadbyId.action?id='+val+' "><img src='+basePath+'resource/images/picture.png></a>';
        		    			}
        		    		},
        		    		align:'center',
        		    	});
        		    	d.push({
        		    		text: '报错信息',
        		    		dataIndex: 'sl_remark',
        		    		width:120,
        		    	});
        		    	Ext.each(arr, function(a){
        		    		var f;
        		    		Ext.each(arrStr,function(b){
        		    			if(a.id_field==b){
        		    				f=true;
        		    				return false;
        		    			}
        		    		});
        		    		if(!f){
        		    			o = new Object();
        		        		o.text = a.id_caption;
        		        		
        		        		o.dataIndex = a.id_field;
        		        		o.hidden = a.id_visible == 0;
        		        		o.width = a.id_width;
        		        		o.rule = a.id_rule;
        		        		o.dataType = a.id_type;
        		        		o.isNeed = a.id_need == 1 ? '是' : '否';
        		        		o.logic = a.id_logic;
        		        		o.logicdesc = me.parseLogic(o.logic);
        		        		o.editor = {
        		        				xtype: 'textfield'
        		        		};
        		        		if(o.dataIndex=='sl_date')
        		        			flag=true;
        		    			if(/combo(.)/.test(o.logic)){
        		    				var s = o.logic.substring(6, o.logic.lastIndexOf(')')).split(','),
        		    					da = new Array();
        		    				Ext.each(s, function(t){
        		    					da.push({
        		    						display: t,
        		    						value: t
        		    					});
        		    				});
        		    				o.editor = {
        		    	    				xtype: 'combobox',
        		    	    				displayField: 'display',
        		    	    				valueField: 'value',
        		    	    				queryMode: 'local',
        		    	    				editable: false,
        		    	    				store:  Ext.create('Ext.data.Store', {
        		    	    		            fields: ['display', 'value'],
        		    	    		            data : da
        		    	    		        })
        		    	    		};
        		    			}
        		        		o.renderer = function(val, meta, record, x, y, store, view){
        		        			if(view) {
        		        				var grid = view.ownerCt,errNodes = grid.errorNodes,cm = grid.columns[y];
        		            			if(errNodes && y > 0 && !!record.get('log') && Ext.Array.contains(errNodes, record.data['id_id'] + ':' + cm.dataIndex)){
        		                			meta.tdCls = 'x-td-warn';
        		                			meta.tdAttr = 'data-qtip="' + (cm.rule || cm.dataType) + '"';
        		            			}else
        		            				meta.tdCls = '';
        		        			}
        		        			return val;
        		        		};
        		        		if(o.dataIndex!='sl_date')
        		        		d.push(o);
        		    		}	
        		    	});
        		    	if(!flag){
        		    		var obj=new Object();
        		    		obj.text = "工资月份";
        		    		obj.dataIndex = "sl_date";
        		    		obj.width =70;
        		    		obj.renderer=function(val){
        		    			if(val){
        		    				var date=Ext.Date.prase(val,'Ym');
        		    				return date;
        		    			}
        		    		};
        		    		d.push(obj);
        		    	}    
        			      Ext.each(d,function(dd){
        		  			    dd.filter= {
        		  			         dataIndex: dd.dataIndex,
        		  			         xtype: "textfield",
        		  			      };
        		  				dd.filterJson_={};
        		  		  }) ;
        		    	return d;
    	},
        parseLogic: function(logic) {
        	if(logic != null) {
        		var gc = logic.split(';'), str = '';
        		for(var i in gc) {
        			var s = gc[i];
        			if(s != null) {
        				if(s.indexOf('unique') > -1) {
        					str += '唯一性;';
        				} else if(s.indexOf('trim') > -1) {
        					str += '不能包含' + s.replace('trim', '') + ';';
        				} else if(s.indexOf('combo') > -1) {
        					str += '只能是' + s.replace('combo', '') + '之一;';
        				} else if(s.indexOf('accord') > -1) {
        					str += '必须存在于' + s.replace('accord', '') + '中;';
        				}else if(s.indexOf('combine') > -1) {
        					str += '组合字段在关联表中不存在;';
        				} else if(s.indexOf('diffence') > -1) {
        					str += '必须与' + s.replace('diffence', '') + '不同;';
        				}else if(s.indexOf('minValue') > -1) {
        					str += '必须大于' + s.replace('minValue', '') + ';';
        				}
        			}
        		}
        		return str;
        	}
        	return null;
        }
	});
     	
     	