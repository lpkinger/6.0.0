Ext.define('erp.view.crm.marketmgr.annualPlans.YearDesingGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpYearDesingGrid',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true,
	id:'grid',
	store: [],
	region: 'south',
	layout : 'fit',
	columns: new Array(),
	height:height,
	GridUtil: Ext.create('erp.util.GridUtil'),
	bodyStyle:'background-color:#f1f1f1;',
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		headerWidth: 0
	}),
	/*enableLocking:true,*/
	/*lockable: true,*/
	/*loadMask: true,*/
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
	})],
	
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
		this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
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
					var data = [],arr=[];
					arr.push(1);
					arr.remove(1);
					var gridfields=res.fields;
					var newdetno=0;
					if(!res.data || res.data.length == 2){
						me.add10EmptyData(grid.detno, arr);
						me.add10EmptyData(grid.detno, arr);//添加20条空白数据
					} else {
						data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
						//对data 进行处理   必须要先取到主记录的开始时间和结束时间

						var Map={};  
						var len = data.length;    
						for(var i=0;i<len;i++){
							if(Map[data[i].mhd_prodcode] == undefined){ 
								var smalldata=data[i];
								smalldata[smalldata.mhd_month+"#mhd_qty"]=smalldata.mhd_qty;   
								smalldata[smalldata.mhd_month+"#mhd_total"]=smalldata.mhd_total;    
								newdetno++;
								smalldata.mhd_detno=newdetno;
								Map[data[i].mhd_prodcode] =smalldata;
							}else{
								var smalldata=Map[data[i].mhd_prodcode];
								smalldata[data[i].mhd_month+"#mhd_qty"]=data[i].mhd_qty;
								smalldata[data[i].mhd_month+"#mhd_total"]=data[i].mhd_total;  
								//smalldata.sd_qty+=data[i].sd_qty;  
								Map[data[i].mhd_prodcode] =smalldata;
							}  

						} 
						for(var property in Map){
							arr.push(Map[property]);
						}				
					}
					//view
					if(grid.selModel.views == null){
						grid.selModel.views = [];
					}
					//dbfind
					if(res.dbfinds&&res.dbfinds.length > 0){
						grid.dbfinds = res.dbfinds;
					}
					//toolbar
					
					var gridcolumns=res.columns;
					for(var i=1;i<13;i++){
						var o={
								readOnly:false,
								header:'第'+i+'月',
								dataIndex:'月份#'+i,
								cls: "x-grid-header-1",
								columns:[{
									readOnly:false,	
									header:'数量',
									cls: "x-grid-header-1",
									dataIndex:i+"#mhd_qty",
									width    : 80,
									xtype:'numbercolumn',
									align:'right',
									format:'0', 	    		 					
									editor:{
										xtype:'numberfield',
										format:'0',
										hideTrigger: true
									}
								},{
									readOnly:false,	
									header:'金额',
									cls: "x-grid-header-1",
									dataIndex:i+"#mhd_total",
									width    : 80,
									xtype:'numbercolumn',
									align:'right',
									format:'0,000.00', 
									summaryType:'sum',
									editor:{
										xtype:'numberfield',
										format:'0.00',
										hideTrigger: true
									}

								}]

						};
						gridcolumns.push(o);
						gridfields.push({
							name:"月份#"+i,
							type:'int'
						});
						gridfields.push({
							name:i+"#mhd_qty",
							type:'int'
						});
						gridfields.push({
							name:i+"#mhd_total",
							format: "0.00",
							type:'number'
						});
					}
					var store = me.setStore(gridfields, arr, grid.groupField, grid.necessaryField);
				    me.setToolbar(grid, data);
					grid.store=store;
					grid.columns=gridcolumns;
					var form = Ext.ComponentQuery.query('form')[0];
					if(form){ 
						if(form.readOnly){
							grid.readOnly = true;//grid不可编辑
						}
					}
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
							if(c.xtype == 'datecolumn'){
								val = Ext.Date.format(val, 'Y-m-d');
							}
							return val;
						} else {
							if(c.xtype == 'datecolumn'){
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
	setToolbar: function(grid,data){
		var total=[0,0,0,0,0,0,0,0,0,0,0,0];
		Ext.Array.each(data,function(item){
			total[Number(item.mhd_month)-1]+=Number(item.mhd_total);
		});
		grid.dockedItems=[{
			xtype: 'toolbar',
			dock: 'bottom',
			items:[{
				xtype: 'tbtext',
				id:'1#',
				text:'1月:'+total[0]
			},'-',{
				xtype: 'tbtext',
				id:'2#',
				text:'2月:'+total[1]
			},'-',{
				xtype:'tbtext',
				id:'3#',
				text:'3月:'+total[2]
			},'-',{
				xtype:'tbtext',
				id:'4#',
				text:'4月:'+total[3]
			},'-',{
				xtype:'tbtext',
				id:'5#',
				text:'5月:'+total[4]
			},'-',{
				xtype:'tbtext',
				id:'6#',
				text:'6月:'+total[5]		
			},'-',{
				xtype:'tbtext',
				id:'7#',
				text:'7月:'+total[6]
			},'-',{
				xtype:'tbtext',
				id:'8#',
				text:'8月:'+total[7]
			},'-',{
				xtype:'tbtext',
				id:'9#',
				text:'9月:'+total[8]
			},'-',{
				xtype:'tbtext',
				id:'10#',
				text:'10月:'+total[9]
			},'-',{
				xtype:'tbtext',
				id:'11#',
				text:'11月:'+total[10]
			},'-',{
				xtype:'tbtext',
				id:'12#',
				text:'12月:'+total[11]
		   }]
		}];
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
		var multivalue="";
		var flag=0;
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			dd = new Object();

			if(s[i].dirty && !me.isBlank(grid, data)){
				Ext.each(gridcolumns, function(c){
					if(c.logic != 'ignore'){//只需显示，无需后台操作的字段，自动略去
						if(c.dataIndex.indexOf("#")>0){
							multivalue="";
							flag=0;
							Ext.Array.each(c.columns,function(column,index){
								if(Number(data[column.dataIndex])==0&&column.dataIndex.indexOf('mhd_total')>0) {
									flag=1;return
								}
								multivalue+=index==0?data[column.dataIndex]:";"+data[column.dataIndex];
							});
							if(flag==0)
								dd[c.dataIndex]=multivalue;
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
	reconfigure: function(store, columns){
		var me = this,
	        view = me.getView(),
	        originalDeferinitialRefresh,
	        oldStore = me.store,
	        headerCt = me.headerCt,
	        oldColumns = headerCt ? headerCt.items.getRange() : me.columns;
	    if (columns) {
	        columns = Ext.Array.slice(columns);
	    }
	    me.fireEvent('beforereconfigure', me, store, columns, oldStore, oldColumns);
	    if (me.lockable) {
	        me.reconfigureLockable(store, columns);
	    } else {
	        Ext.suspendLayouts();
	        if (columns) {
	            delete me.scrollLeftPos;
	            headerCt.removeAll();
	            headerCt.add(columns);
	        }
	        if (store && (store = Ext.StoreManager.lookup(store)) !== oldStore) {
	            originalDeferinitialRefresh = view.deferInitialRefresh;
	            view.deferInitialRefresh = false;
	            try {
	            	me.bindStore(store);
	            } catch ( e ) {
	            	
	            }
	            view.deferInitialRefresh = originalDeferinitialRefresh;
	        } else {
	            me.getView().refresh();
	        }
	        Ext.resumeLayouts(true);
	    }
	    me.fireEvent('reconfigure', me, store, columns, oldStore, oldColumns);
		this.fireEvent("summary", this);
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