Ext.define('erp.view.scm.sale.PreForecastGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpPreForecastGrid',
 	emptyText : $I18N.common.grid.emptyText,
 	requires: ['erp.view.core.plugin.CopyPasteMenu'],
    columnLines : true,
    autoScroll : true,
    id:'grid',
    store: [],
    region: 'south',
	layout : 'fit',
    columns: new Array(),
    height:height,
    bbar: {
    	xtype: 'toolbar',
    	id:'toolbar',
    	items:[{
			xtype: 'tbtext',
			name: 'row',
			id:'toolbar_tbtext'
    	},'-',{
			xtype: 'erpDeleteDetailButton',
			id:'erpDeleteDetailButton_btn',
			hidden: false
		}]
    },
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	/*enableLocking:true,*/
	/*lockable: true,*/
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1,
        listeners:{
        	'edit':function(editor,e,Opts){
        		var record=e.record,data=null,qty=0;
        		if(record){
        	        data=record.data;
        		   for(var property in data){ 
        			   if(!/^[a-zA-Z]/.test(property)){
        				 qty+=data[property];
        			   }
        		   }
        		   if(qty>(data.ma_qty-data.ma_madeqty)){
        			  showError('排产数不能超过未交数!请重新输入');
        			  e.record.reject();
        		   }
        		}
        	    
        	}
        }
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    initComponent : function(){
		var condition = this.condition;
		if(!condition){
			var urlCondition = this.BaseUtil.getUrlParam('gridCondition');
			urlCondition = urlCondition == null || urlCondition == "null" ? "" : urlCondition;
			gridCondition = (gridCondition == null || gridCondition == "null") ? "" : gridCondition;
			gridCondition = gridCondition + urlCondition;
	    	gridCondition = gridCondition.replace(/IS/g, "=");
			if(gridCondition.search(/!/) != -1){
				gridCondition = gridCondition.substring(0, gridCondition.length - 4);
			}
			condition = gridCondition;
		}
    	var gridParam = {caller: this.caller || caller, condition: condition};
        this.getGridColumnsAndStore(this, 'scm/sale/getPreGridConfig.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments); 
	},
	loadNewStore:function(grid, url, param, no){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		/*var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));*/
    			//对data 进行处理   必须要先取到主记录的开始时间和结束时间
    			var data=res.data;
    			var Map={},arr=new Array();  
    	        var len = data.length; 
    	        startdate=new Date(data[0].sf_fromdate);
    	        enddate=new Date(data[0].sf_todate);           	      
    	        for(var i=0;i<len;i++){
    	            if(Map[data[i].sd_id] == undefined){ 
    	            	var smalldata=data[i];
    	            	smalldata[smalldata.wd_date.substring(0,10)]=smalldata.wd_planqty;
    	                Map[data[i].wd_makecode] =smalldata;             	              
    	            }else{
    	            	var smalldata=Map[data[i].wd_code];
    	            	smalldata[data[i].wd_date.substring(0,10)]=data[i].wd_planqty;
    	                Map[data[i].wd_makecode]=smalldata;  
    	            }            	    
    	          
    	        } 
    	        for(var property in Map){
    	        	arr.push(Map[property]);
    	        }
    	        data=arr;
		        grid.store.loadData(data);
        	}
		});
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		grid.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	async:false,
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			var limits = res.limits, limitArr = new Array(),startdate=null,enddate=null;
        			if(limits != null && limits.length > 0) {//权限外字段
    					limitArr = Ext.Array.pluck(limits, 'lf_field');
    				}
        			Ext.each(res.columns, function(column, y){
        				//power
        				if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
        					column.hidden = true;
        				}
        				//renderer
        				me.setRenderer(grid, column);
        				//logictype
        				me.setLogicType(grid, column);
        			});
        			//data
            		var data = [];
            		var gridfields=res.fields;
            		var newdetno=0;
            		if(!res.data || res.data.length == 2){
            			me.add10EmptyData(grid.detno, data);
            			me.add10EmptyData(grid.detno, data);//添加20条空白数据
            		} else {
            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            			//对data 进行处理   必须要先取到主记录的开始时间和结束时间
            			var Map={},arr=new Array();  
            	        var len = data.length;         
            	        for(var i=0;i<len;i++){
            	            if(Map[data[i].sd_prodcode+"!"+data[i].sd_custcode] == undefined){ 
            	            	var smalldata=data[i];
            	            	smalldata[smalldata.sd_startdate.substring(0,10)+"#"+smalldata.sd_enddate.substring(0,10)]=smalldata.sd_qty;             	              
            	            	newdetno++;
            	            	smalldata.sd_detno=newdetno;
            	            	Map[data[i].sd_prodcode+"!"+data[i].sd_custcode] =smalldata;
            	            }else{
            	            	var smalldata=Map[data[i].sd_prodcode+"!"+data[i].sd_custcode];
            	            	smalldata[data[i].sd_startdate.substring(0,10)+"#"+data[i].sd_enddate.substring(0,10)]=data[i].sd_qty;
            	            	smalldata.sd_qty+=data[i].sd_qty;  
            	            	Map[data[i].sd_prodcode+"!"+data[i].sd_custcode] =smalldata;
            	            }  
            	            
            	        } 
            	        for(var property in Map){
            	        	arr.push(Map[property]);
            	        }
            	        data=arr;
            		}
            		//store
            		//view
            		if(grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		//dbfind
            		if(res.dbfinds&&res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
    				//toolbar
            		//me.setToolbar(grid, res.columns, res.necessaryField, limitArr);
                    gridcolumns=res.columns;
                   
                    startdate=new Date(res.date.startdate);
                    enddate=new Date(res.date.enddate);
                    method=res.date.method;
                    if(startdate!=null){
                    	var lastcolumns=[];
                    	var startfield="";
                    	var endfield="";
                    	 Ext.Array.each(gridcolumns,function(item,m){
 	    		 			if(item.dataIndex=='sd_enddate'){
 	    		 				if(method=='周'){
 	    		 					item.header='起止时间段(1周)';
 	    		 					var columns=new Array();
 	    		 					var count=(enddate-startdate)/(86400000*7);
 	    		 					count = Math.ceil(count);
 	    		 					for(var i=0;i<count;i++){
	 	    		 					var sdate=Ext.Date.add(startdate,Ext.Date.DAY,7*i);
	 	    		 					startfield=Ext.Date.format(sdate,'Y-m-d');
	 	    		 					if(i <count-1){
	 	    		 						endfield=Ext.Date.format(Ext.Date.add(sdate,Ext.Date.DAY,6),'Y-m-d');
	 	    		 					} else {
	 	    		 						endfield = Ext.Date.format(enddate,'Y-m-d');
	 	    		 				}
 	    		 					gridfields.push({
 	    		 						name:startfield+"#"+endfield,
 	    		 						type:'int'
 	    		 					});
 	    		 					columns.push({
	 	    		 				     readOnly:false,	
	 	    		 					 header:startfield.substring(5,10)+"~"+endfield.substring(5,10),
	 	    		 					 cls: "x-grid-header-1",
	 	    		 					 dataIndex:startfield+"#"+endfield,
	 	    		 					 width    : 120,
	 	    		 					 xtype:'numbercolumn',
	 	    		 					 align:'right',
	 	    		 					 format:'0', 	    		 					
	 	    		 				     editor:{
	 	    		 				    	 xtype:'numberfield',
	 	    		 				    	 format:'0',
	 	    		 					     hideTrigger: true
	 	    		 				     }
	 	    		 					});
	 	    		 				}
	 	    		 				item.columns=columns;
 	    		 				}else if(method=='月'){
 	    		 					item.header='起止时间段(1月)';
 	    		 					var columns=new Array();
	 	    		 				var y1=(''+res.date.enddate).substr(0, 4);
	 	    		 				var y2=(''+res.date.startdate).substr(0, 4);
	 	    		 				var m1=(''+res.date.enddate).substr(4, 2);
	 	    		 				var m2=(''+res.date.startdate).substr(4, 2);
	 	    		 				var count =(y1 - y2) * 12 + (m1 - m2);
	 	    		 				var t1=res.date.startdate+'';
	 	    		 				var from=t1.substring(0,4)+'-'+t1.substring(4,6)+'-01';
	 	    		 				var date= new Date(from);
	 	    		 				for(var i=0;i<=count;i++){
	 	    		 					var sdate=Ext.Date.add(date,Ext.Date.MONTH,i);
	 	    		 					startfield=Ext.Date.format(sdate,'Y-m-d');
	 	    		 					var edate=Ext.Date.getLastDateOfMonth(sdate);
	 	    		 					endfield = Ext.Date.format(edate,'Y-m-d');
	 	    		 					gridfields.push({
	 	    		 						name:startfield+"#"+endfield,
	 	    		 						type:'int'
	 	    		 					});
	 	    		 					columns.push({
	 	    		 				     readOnly:false,	
	 	    		 					 header:startfield.substring(0,7),
	 	    		 					 cls: "x-grid-header-1",
	 	    		 					 dataIndex:startfield+"#"+endfield,
	 	    		 					 width    : 120,
	 	    		 					 xtype:'numbercolumn',
	 	    		 					 align:'right',
	 	    		 					 format:'0', 	    		 					
	 	    		 				     editor:{
	 	    		 				    	 xtype:'numberfield',
	 	    		 				    	 format:'0',
	 	    		 					     hideTrigger: true
	 	    		 				     }
	 	    		 					});
	 	    		 				}
	 	    		 				item.columns=columns;
 	    		 				}else if(method=='天'){
 	    		 					item.header = '起止时间段(1天)';
 	    		 					var columns=new Array();
 	    		 					var count = (enddate-startdate)/(86400000);
	    		 					count = Math.ceil(count);
	    		 					for(var i=0;i<=count;i++){
	    		 						var sdate=Ext.Date.add(startdate,Ext.Date.DAY,i);
	 	    		 					var startfield=Ext.Date.format(sdate,'Y-m-d');
	 	    		 					var endfield = Ext.Date.format(enddate,'Y-m-d');
	 	    		 					if(i <count-1){
	 	    		 						endfield=Ext.Date.format(Ext.Date.add(sdate,Ext.Date.DAY,1),'Y-m-d');
	 	    		 					} else {
	 	    		 						endfield = Ext.Date.format(enddate,'Y-m-d');
	 	    		 					}
	 	    		 					gridfields.push({
	 	    		 						name:startfield+"#"+endfield,
	 	    		 						type:'int'
	 	    		 					});
	 	    		 					columns.push({
		 	    		 				     readOnly:false,	
		 	    		 					 header:startfield,
		 	    		 					 cls: "x-grid-header-1",
		 	    		 					 dataIndex:startfield+"#"+endfield,
		 	    		 					 width    : 120,
		 	    		 					 xtype:'numbercolumn',
		 	    		 					 align:'right',
		 	    		 					 format:'0', 	    		 					
		 	    		 				     editor:{
		 	    		 				    	 xtype:'numberfield',
		 	    		 				    	 format:'0',
		 	    		 					     hideTrigger: true
		 	    		 				     }
		 	    		 					});
	    		 					}
	    		 					item.columns=columns;
 	    		 				}		 				
 	    		 			}
 	    		 			lastcolumns[m]=item;
 	    		 		});
                    	 gridcolumns=lastcolumns;
                    }
                    var store = me.setStore(gridfields, data, grid.groupField, grid.necessaryField);
            		//grid.store=store;
            		grid.reconfigure(store, gridcolumns);
            		//grid.columns=gridcolumns;
            		var form = Ext.ComponentQuery.query('form')[0];
        			if(form){ 
        				if(form.readOnly){
        					grid.readOnly = true;//grid不可编辑
        				}
        			}
        			/*var lockedView = grid.view.lockedView;
                    if(lockedView){
                        var tableEl = lockedView.el; 
                        console.log(tableEl);
                        if(tableEl){
                      	  tableEl.dom.style.marginBottom = '27px';
                        }                 
                    }*/
        		} else {
        			grid.hide();
        			var form = Ext.ComponentQuery.query('form')[0];
        			me.updateFormPosition(form);//字段较少时，修改form布局
        		}
        	}
        });
	},
	setRenderer: function(grid, column){
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
	},
	setLogicType: function(grid, column){
		var logic = column.logic;
		if(logic != null){
			if(logic == 'detno'){
				grid.detno = column.dataIndex;
				column.width = 40;
				column.renderer = function(val, meta) {
			        meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
			        return val;
			    };
			} else if(logic == 'keyField'){
				grid.keyField = column.dataIndex;
			} else if(logic == 'mainField'){
				grid.mainField = column.dataIndex;
			} else if(logic == 'necessaryField'){
				grid.necessaryField = column.dataIndex;
				if(!grid.necessaryFields){
					grid.necessaryFields = new Array();
				}
				grid.necessaryFields.push(column.dataIndex);
				if(!column.haveRendered){
					column.renderer = function(val, meta, record, x, y, store, view){
						var c = this.columns[y];
						if(val != null && val.toString().trim() != ''){
							if(c&&c.xtype == 'datecolumn' && Ext.Date.formatContainsDateInfo(val)){
								val = Ext.Date.format(val, 'Y-m-d');
							}
							return val;
						} else {
							if(c&&c.xtype == 'datecolumn'){
								val = '';
							}
							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
						}
				   };
				}
			} else if(logic == 'groupField'){
				grid.groupField = column.dataIndex;
			}
		}
	},
	setStore: function(fields, data, groupField, necessaryField){
		griddata=data;
		if(!Ext.isChrome){
			Ext.each(fields, function(f){
				if(f.type == 'date'){
					f.dateFormat = 'Y-m-d H:i:s';
				}
			});
		}
		return Ext.create('Ext.data.Store', {
		    fields: fields,
		    data: data,
		    groupField: groupField,
		    getSum: function(records, field) {
		    	if (arguments.length  < 2) {
		    		return 0;
		    	}
	            var total = 0,
	                i = 0,
	                len = records.length;
	            if(necessaryField) {
            		for (; i < len; ++i) {//重写getSum,grid在合计时，只合计填写了必要信息的行
            			var necessary = records[i].get(necessaryField);
	            		if(necessary != null && necessary != ''){
		            		total += records[i].get(field);
		            	}
            		}
            	} else {
            		for (; i < len; ++i) {
            			total += records[i].get(field);
            		}
            	}
	            return total;
		    },
		    getCount: function() {
		    	if(necessaryField) {
		    		var count = 0;
		    		Ext.each(this.data.items, function(item){//重写getCount,grid在合计时，只合计填写了必要信息的行
			    		if(item.data[necessaryField] != null && item.data[necessaryField] != ''){
			    			count++;
			    		}
			    	});
		    		return count;
		    	}
		    	return this.data.items.length;
		    }
		});
	},
	setToolbar: function(grid, columns, necessaryField, limitArr){
		var items = [];
		var bool = true;
		if(!grid.dockedItems)
			return;
		Ext.each(grid.dockedItems.items, function(item){
			if(item.dock == 'bottom' && item.items){//bbar已存在
				bool = false;
			}
		});
		if(bool){
    		Ext.each(columns, function(column){
    			if(limitArr.length == 0 || !Ext.Array.contains(limitArr, column.dataIndex)) {
    				if(column.summaryType == 'sum'){
        				items.push('-',{
        					id: column.dataIndex + '_sum',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.header + '(sum):0'
        				});
        			} else if(column.summaryType == 'average') {
        				items.push('-',{
        					id: column.dataIndex + '_average',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.header + '(average):0'
        				});
        			} else if(column.summaryType == 'count') {
        				items.push('-',{
        					id: column.dataIndex + '_count',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.header + '(count):0'
        				});
        			}
    			}
    			if(column.dataIndex == necessaryField){
    				column.renderer = function(val){
    					if(val != null && val.toString().trim() != ''){
							return val;
						} else {
							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
						}
    				};
    			}
    		});
			grid.addDocked({
    			xtype: 'toolbar',
    	        dock: 'bottom',
    	        items: items
    		});
		} else {
			var bars = Ext.ComponentQuery.query('erpToolbar');
			if(bars.length > 0){
				Ext.each(columns, function(column){
        			if(column.summaryType == 'sum'){
        				bars[0].add('-');
        				bars[0].add({
        					id: column.dataIndex + '_sum',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.header + '(sum):0'
        				});
        			} else if(column.summaryType == 'average') {
        				bars[0].add('-');
        				bars[0].add({
        					id: column.dataIndex + '_average',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.header + '(average):0'
        				});
        			} else if(column.summaryType == 'count') {
        				bars[0].add('-');
        				bars[0].add({
        					id: column.dataIndex + '_count',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.header + '(count):0'
        				});
        			}
        		});
			}
		}
	},
	add10EmptyData: function(detno, data){
		if(detno){
			var index = data.length == 0 ? 0 : Number(data[data.length-1][detno]);
			for(var i=0;i<10;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				data.push(o);
			}
		} else {
			for(var i=0;i<10;i++){
				var o = new Object();
				data.push(o);
			}
		}
	},
	checkGridDirty: function(grid){
		var s = grid.getStore().data.items;//获取store里面的数据
		var msg = '';
		for(var i=0;i<s.length;i++){
			if(s[i].dirty){//明细行被修改过哦
				msg = msg + '第' + (i+1) + '行:';
				var changed = Ext.Object.getKeys(s[i].modified);//拿到被修改了的字段
				for(var j = 0;j < changed.length;j++){
					Ext.each(grid.columns, function(c, index){
						if(index == 0){
							return;
						}
						if(c.dataIndex == changed[j] && c.logic != 'ignore'){//由字段dataindex匹配其text
							msg = msg + '<font color=blue>' + c.text + '</font>&nbsp;';
						}
					});
				}
				msg = msg + ";";
			}
		}
		if(msg != '' && msg != ';'){//明细行被修改过哦
			msg = "明细行<font color=green>" + msg.substring(0, msg.length-1) + "</font>已编辑过";
		}
		return msg;
	},
	getGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,
		jsonGridData = new Array();
		var form = Ext.getCmp('form');
		var s = grid.getStore().data.items;//获取store里面的数据
		var dd;
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			dd = new Object();
			
			if(s[i].dirty && !me.isBlank(grid, data)){
				Ext.each(gridcolumns, function(c){
					if(c.logic != 'ignore'){//只需显示，无需后台操作的字段，自动略去
						if(c.dataIndex=='sd_enddate'){
							Ext.Array.each(c.columns,function(column){
								dd[column.dataIndex]=data[column.dataIndex];
							});
							
						}else {
						if(c.xtype == 'datecolumn'){
							c.format = c.format || 'Y-m-d';
							if(Ext.isDate(data[c.dataIndex])){
								dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
							} else {
								if(c.editor){
									dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
								}
							}
						} else if(c.xtype == 'datetimecolumn'){
							if(Ext.isDate(data[c.dataIndex])){
								dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
							} else {
								if(c.editor){
									dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
								}
							}
						} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
							if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
								dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
						} else {
							dd[c.dataIndex] = s[i].data[c.dataIndex];
						}
						}
					}
				});
				if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
					dd[grid.mainField] = Ext.getCmp(form.keyField).value;
				}
				jsonGridData.push(Ext.JSON.encode(dd));
			}
		}
		return jsonGridData;
	},
	isBlank: function(grid, data) {
		var ff = grid.necessaryFields,bool = true;
		if(ff) {
			Ext.each(ff, function(f) {
				if(!Ext.isEmpty(data[f]) && data[f] != 0) {
					bool = false;
				}else {
					bool=true;
				    return
				}
			});
		} else {
			if(!grid.necessaryField || !Ext.isEmpty(data[grid.necessaryField])) {
				bool = false;
			} 
		}
		return bool;
	}
});