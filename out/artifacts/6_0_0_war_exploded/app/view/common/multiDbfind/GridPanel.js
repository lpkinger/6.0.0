Ext.define('erp.view.common.multiDbfind.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpMultiDbfindGridPanel',
	layout : 'fit',
	id: 'dbfindGridPanel', 
	headerfilter:false,
 	emptyText: '<div id="emptytext">'+$I18N.common.grid.emptyText+'</div>',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    multiselected: new Array(),
    selectAll:true,
    selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	ignoreRightMouseSelection : false,
	    	checkOnly: true,
			listeners:{
	            selectionchange:function(selModel, selected, options){//表头全选、取消全选
	            	var grid=selModel.view.ownerCt;
	            	if(grid.selectAll){
	            		if(selected.length>0){//全选
	            			Ext.each(selected,function(select){
		            			var resgrid=Ext.getCmp('dbfindresultgrid');
		            			var d=select.data;
	            				delete d.RN;
		            			resgrid.selectObject[Ext.JSON.encode(d)]=d;
	            			});
	            			parent.Ext.getCmp('onlyChecked').show();
	            			grid.updateInfo();
	            		}else{//取消全选
	            			var grid=selModel.view.ownerCt;
		            		var resgrid=Ext.getCmp('dbfindresultgrid');
		            		Ext.each(grid.store.data.items,function(deselect){
		            			var d=deselect.data;
		            			delete d.RN;
			            		var key=Ext.JSON.encode(d);
			            		delete resgrid.selectObject[key];
		            		});
		            		if(Ext.Object.getKeys(resgrid.selectObject).length==0){
		            			parent.Ext.getCmp('onlyChecked').hide();
		            		}
	            			var datachecked=new Array();
	            			var resgrid=Ext.getCmp('dbfindresultgrid');
	            			if(resgrid.selectObject){
	            				Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
	            					datachecked.push(resgrid.selectObject[k]);
	            				});
	            			}	            			
	            			grid.updateInfo();
	            		}	            		
	            	}else{
	            	 	grid.selectAll=true;
	            	 	grid.headerfilter=false;
	            	}
	            },
	            select:function(selModel, record, index, opts){//选中
	            	var grid=selModel.view.ownerCt;
	            	grid.selectAll=false;
	            	var d=record.data;
	            	delete d.RN;
		            var resgrid=Ext.getCmp('dbfindresultgrid');
		            resgrid.selectObject[Ext.JSON.encode(d)]=d;
		            parent.Ext.getCmp('onlyChecked').show();
		            grid.updateInfo();
	            },
	            deselect:function(selModel, record, index, opts){//取消选中
	            	var grid=selModel.view.ownerCt;
	            	if(grid.selectAll){
		            	var resgrid=Ext.getCmp('dbfindresultgrid');
		            	var d=record.data;
		            	delete d.RN;
		            	var key=Ext.JSON.encode(d);
		            	delete resgrid.selectObject[key];
		            	if(Ext.Object.getKeys(resgrid.selectObject).length==0){
	            			parent.Ext.getCmp('onlyChecked').hide();
	            		}
		            	grid.selectAll=false;
	            	}
	            	grid.updateInfo();
	            }
	        }
	}),
	plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	initComponent : function(){ 
		this.callParent(arguments);
		this.getCount();
	},
	RenderUtil: Ext.create('erp.util.RenderUtil'),
	updateInfo:function(){
		var grid=this;
		var count_select=grid.selModel.getSelection().length;
		//hey start 合计栏
		var datachecked=new Array();
		var resgrid=Ext.getCmp('dbfindresultgrid');
		if(resgrid.selectObject){
			Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
				datachecked.push(resgrid.selectObject[k]);
			});
		}
		var columns = grid.columns;			    				    	
    	var items = [];   	    	
    	if(columns){
			Ext.Array.each(columns,function(column){	
				if(column.summaryType){
					//合计
					if(column.summaryType=='sum'){
						var sum = 0;
						if(datachecked.length!=0){					
							Ext.Array.each(datachecked,function(row){
								sum+=parseFloat(row[column.dataIndex]);
							});
							items.push(column.text + ':'+Ext.util.Format.number(sum,'0,000.00'));
						}else{
							items.push(column.text + ':'+Ext.util.Format.number(0,'0,000.00'));
						}	
					}
					//条数
					if(column.summaryType=='count'){					
				    	items.push(column.text + ':'+datachecked.length+'条');					
					}
					//平均数
					if(column.summaryType=='avergae'){	
						var sum = 0;
						if(datachecked.length>0){
				    		Ext.Array.each(datachecked,function(row){
								sum+=parseFloat(row[column.dataIndex]);
							});
				    	    items.push(column.text + ':'+Ext.util.Format.number(sum/datachecked.length,'0,000.00'));		    		
				    	}else{
				    		items.push(column.text + ':'+Ext.util.Format.number(0,'0,000.00'));
				    	}					
					}
				}					
			});
		}	
		if(items.length>0) Ext.getCmp('list_summary').update(items.join(" | "));
		//hey end
	},
	setMultiValues: function() {
		var me=this;
		var selected=new Array();
		var resgrid=Ext.getCmp('dbfindresultgrid');
		Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
			selected.push(resgrid.selectObject[k]);
		});
		if(selected.length > 0) {
			if(dbfinds){
				trigger.multiValue = new Object();
				var keys = Ext.Object.getKeys(selected[0]);
	        	Ext.Array.each(selected, function(item){
	        		var keyValues = item;
	        		Ext.each(keys, function(k){
	        			Ext.Array.each(dbfinds,function(ds){
	    					if(k == ds.dbGridField) {
	    						if(ds.field && parent.Ext.getCmp(ds.field)){
	    							if(trigger.multiValue == null){
	    								trigger.multiValue = new Object();
	    							}
	    							if(trigger.multiValue[ds.field] == null || trigger.multiValue[ds.field] == ''){
	    								trigger.multiValue[ds.field] = keyValues[k];
	    							} else if(!Ext.Array.contains(trigger.multiValue[ds.field].split(trigger.separator), keyValues[k])){
	    								trigger.multiValue[ds.field] = trigger.multiValue[ds.field] + trigger.separator + keyValues[k];
	    							}
	    						}
	    					}
	    				});
	        		});
	        	});
	        } else {
	        	trigger.multiValue = selected;
	        }
		} else {
			if(dbfinds)
				trigger.multiValue = {};
			else
				trigger.multiValue = [];
		}
		//给trigger.multiRecords赋值，用于aftertrigger事件
		var datachecked=new Array();
		Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
			datachecked.push(resgrid.selectObject[k]);
		});
		resgrid.store.loadData(datachecked);
		resgrid.selModel.selectAll();
		trigger.multiRecords = resgrid.selModel.getSelection();
	},
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
        	url : basePath + 'common/dbfind.action',
        	method : 'post',
        	params : {
        		which : which,
	   			caller : c,
	   			field: key,
	   			condition: f,
	   			page: g,
	   			pageSize: s,
	   			_config:getUrlParam('_config')
	   		},
        	callback : function(options, success, response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return ;
        		}
        		var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        		if(callback)
        			callback.call(me, data);
        		else {
        			if(me.columns && me.columns.length > 2){
            			me.store.loadData(data);
            			if(me.store.data.items.length != data.length){
            				me.store.add(data);
            			}
            			//勾选已选中记录
            			//me.selectAll=false;
            			//me.headerfilter=false;
            			me.selectDefaultRecord();
            		} else {
        				//处理render
            			var grid = this;
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
                        		}
                        		column.renderer = grid.RenderUtil[renderName];
                        		column.haveRendered = true;
                        	}
                        });
                        res.columns.push({            				
        					filter: {xtype: 'textfield',editable:false},
        				    filterJson_: {},
                        	cls:'x-dbfind-lastrow',
                        	flex:1
                        });
            			me.reconfigure(Ext.create('Ext.data.Store', {
                		    fields: res.fields,
                		    data: data
                		}), res.columns);
                		Ext.getCmp('dbfindresultgrid').setDefaultColumns(res.fields,res.columns);
                		/*Ext.create('Ext.data.Store', {
                		    fields: res.fields,
                		    data: [],
                		    listeners:{
                		    	'datachanged':function(){
                		    		Ext.getCmp('dbfindresultgrid').selectDefault();
                		    	}
                		    }
                		}), res.columns);*/
                		dbfinds = res.dbfinds;
                		me.selectDefault();
                	}
        			Ext.getCmp('pagingtoolbar').afterOnLoad();
        		}
        	}
        });
	},
	/**
	 * 只取全部数据
	 */
	getAllData: function(callback) {
		if(callback) {
			var me = this;
			me.getCount(null, null, function(count, cal, cond){
				if(count>200){
					count=200;
				}
				me.getColumnsAndStore(cal, cond, 1, count, callback);
			});
		}
	},
	getCount: function(c, d, callback){
		var me = this;
		c = c || caller;
		d = d || condition;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		var _f = getUrlParam('_f');
		if( _f == 1 || me.isFast){//解决商城询价
			dataCount = 1000*pageSize;// 直接作1000页数据处理
			me.dataCount=dataCount;
			me.noCount = true;
    		me.getColumnsAndStore(c,d);
    		return;
		}
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + 'common/dbfindCount.action',
        	params : {
        		which : which,
	   			caller : c,
	   			field: key,
	   			condition: f,
	   			_config:getUrlParam('_config')
	   		},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if( res.count == -1 ){//解决商城询价
        			dataCount = 1000*pageSize;// 直接作1000页数据处理
        			me.isFast=true;
        		}else{
            		dataCount = res.count;    
        		}		
        		if(callback) 
        			callback.call(me, dataCount, c, d);
        		else
        			me.getColumnsAndStore(c, d);
        	}
        });
	},
	selectDefault: function(){
		var grid = this;
		var def = new Array();
		if(!Ext.isEmpty(trigger.value)){
			var f = '';
			Ext.each(dbfinds, function(d){
				if(d.field == key){
					f = d.dbGridField;
				}
			});
			var arr = trigger.value.split(trigger.separator);
			Ext.each(grid.store.data.items, function(item){
				if(Ext.Array.contains(arr, item.data[f])){
					def.push(item);
					var keyValues = item.data;
	        		Ext.each(Ext.Object.getKeys(keyValues), function(k){
	        			Ext.Array.each(dbfinds,function(ds){
	    					if(k == ds.dbGridField) {
	    						if(parent.Ext.getCmp(ds.field)){
	    							if(trigger.multiValue == null){
	    								trigger.multiValue = new Object();
	    							}
	    							if(trigger.multiValue[ds.field] == null || trigger.multiValue[ds.field] == ''){
	    								trigger.multiValue[ds.field] = keyValues[k];
	    							} else if(!Ext.Array.contains(trigger.multiValue[ds.field].split(trigger.separator), keyValues[k])){
	    								trigger.multiValue[ds.field] = trigger.multiValue[ds.field] + trigger.separator + keyValues[k];
	    							}
	    						}
	    					}
	    				});
	        		});
				}
			});
			grid.selModel.select(def);
			Ext.each(def,function(select){
		        var resgrid=Ext.getCmp('dbfindresultgrid');
		        resgrid.selectObject[Ext.JSON.encode(select.data)]=select.data;
	        });
		}
			var datachecked=new Array();
			var resgrid=Ext.getCmp('dbfindresultgrid');
			Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
				datachecked.push(resgrid.selectObject[k]);
			});
			if(datachecked.length>0){
				var selectArr=new Array();
				Ext.each(grid.store.data.items, function(item){
					delete item.data.RN;
					Ext.each(datachecked,function(checked){
						var checkflag=true;
						var keys=Ext.Object.getKeys(item.data);
						for(var i=0;i<keys.length && checkflag;i++){
							var k=keys[i];
							if(item.data[k]!=checked[k]){
								checkflag=false;
							}
							if(i==keys.length-1&&checkflag){
								selectArr.push(item);
							}
						} 					
					});
				});
				grid.selModel.select(selectArr);
			}
		
	},
	selectDefaultRecord:function(){//数据回显
			var grid = this;
			var datachecked=new Array();
			var resgrid=Ext.getCmp('dbfindresultgrid');
			Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
				datachecked.push(resgrid.selectObject[k]);
			});
			//hey start 合计栏
			var columns = grid.columns;			    				    	
	    	var items = [];	    	
			if(columns){
				Ext.Array.each(columns,function(column){	
					if(column.summaryType){
						//合计
						if(column.summaryType=='sum'){
							var sum = 0;
							if(datachecked.length!=0){					
								Ext.Array.each(datachecked,function(row){
									sum+=parseFloat(row[column.dataIndex]);
								});
								items.push(column.text + ':'+Ext.util.Format.number(sum,'0,000.00'));
							}else{
								items.push(column.text + ':'+Ext.util.Format.number(0,'0,000.00'));
							}	
						}
						//条数
						if(column.summaryType=='count'){					
					    	items.push(column.text + ':'+datachecked.length+'条');					
						}
						//平均数
						if(column.summaryType=='avergae'){	
							var sum = 0;
							if(datachecked.length>0){
					    		Ext.Array.each(datachecked,function(row){
									sum+=parseFloat(row[column.dataIndex]);
								});
					    	    items.push(column.text + ':'+Ext.util.Format.number(sum/datachecked.length,'0,000.00'));		    		
					    	}else{
					    		items.push(column.text + ':'+Ext.util.Format.number(0,'0,000.00'));
					    	}					
						}
					}					
				});
			}
			//hey end
			if(items.length>0) Ext.getCmp('list_summary').update(items.join(" | "));
			if(datachecked.length>0){
				var selectArr=new Array();
				Ext.each(grid.store.data.items, function(item){
					delete item.data.RN;
					Ext.each(datachecked,function(checked){
						var checkflag=true;
						var keys=Ext.Object.getKeys(item.data);
						for(var i=0;i<keys.length && checkflag;i++){
							var k=keys[i];
							if(item.data[k]!=checked[k]){
								checkflag=false;
							}
							if(i==keys.length-1&&checkflag){
								selectArr.push(item);
							}
						} 					
					});
				});
				grid.selModel.select(selectArr);
			}
		if(Ext.Object.getKeys(resgrid.selectObject).length==0){
	        parent.Ext.getCmp('onlyChecked').hide();
	    }
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		grid.headerfilter=true;
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