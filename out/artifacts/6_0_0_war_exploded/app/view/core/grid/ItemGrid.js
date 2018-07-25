/**
 * form配置的grid 相当于form组件  可自定义数据
 */
Ext.define('erp.view.core.grid.ItemGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.itemgrid',
	region: 'south',
	layout : 'fit',
	requires: ['erp.view.core.button.AddDetail', 'erp.view.core.button.DeleteDetail', 'erp.view.core.plugin.CopyPasteMenu'],
//	id: 'grid1', 
//	height:200,
	forceFit:true,
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    readOnly : true,
    GridUtil: Ext.create('erp.util.GridUtil'),
    viewConfig: {
    	stripeRows:false
	},
	bbar:[{	xtype : 'erpAddDetailButton',
		handler:function(btn){	
			var me = this,	
			grid = me.grid, store = grid.store,
			record = grid.selModel.lastSelected;
			var o = new Object();
		    if (record) {
		    	if(!grid.detno){
					o.ig_mainid = grid.value;					
					grid.store.insert(store.indexOf(record) + 1, o);	
		    	}else{
		    		var detno = Number(record.data[grid.detno]),d = detno;
					store.each(function(item){
						d = item.data[grid.detno];
						if(Number(d) > detno) {
							item.set(grid.detno, Number(d) + 1);
						}
					});
					o.ig_mainid = grid.value;
					o[grid.detno] = detno + 1;
					grid.store.insert(store.indexOf(record) + 1, o);	
					
		    	}
			}else{
				o.ig_mainid = grid.value;
				grid.store.insert(1,o);
			}
	}},{xtype : 'erpDeleteDetailButton'},{xtype :'button',iconCls: 'x-button-icon-confirm',cls: 'x-btn-tb',tooltip:'保存明细',name:'saveItemGrid',
			listeners:{
				   click:function(btn){
				   	     var grid = this.ownerCt.ownerCt;	
						 if(grid.value!='' && grid.value != null){
						    grid.updateValue(grid,btn);							    
						 }else{
						    grid.saveValue(grid,btn);
					   }
				   },afterrender:function(btn){		
				   	  if(btn.ownerCt.ownerCt.readOnly){//grid 只读 不显示itemgrid 中的保存按钮
				   	  	  btn.setDisabled(true);
				   	  }
				   }
		        }
	},
      '->',{xtype:'tbtext',name :'sum'}],
	initComponent : function(){ 
		var me = this;
		var logic = this.logic;
        var value = this.value;
        me.caller = logic;
        me.readOnly = true;
        me.plugins = new Ext.create('Ext.grid.plugin.CellEditing',{
        	id : 'plugins_'+me.id,
        	clicksToEdit: 1
        });
        var objectArray = new Array();
        if(this.iniValue!= null &&this.iniValue!= ''){
            //解析  this.defaultValue 的数据
            //this.defaultValue 的数据格式为  columnname1:cn11,cn12,cn13;columnname2:cn21,cn22,cn23        
            var defaultColumns = this.iniValue.split(';');  //得到[{columnname1:cn11,cn12,cn13},{columnname2:cn21,cn22,cn23}]
            var c = defaultColumns[0].split(':');
            var vc = c[1].split(',');
            for(var i = 0; i < vc.length;i++){
            	var o = new Object();
            	objectArray.push(o);
            }           
            Ext.each(defaultColumns,function(dColumn,index){
            	var dd = dColumn.split(':');
                var vd = dd[1].split(',');
                Ext.each(objectArray,function(o,index){
                	o[dd[0]] = vd[index];
                });
            });
        }       
		var condition = "ig_mainid = '"+value+"'";
		var gridParam = {caller: logic , condition: condition};
    	me.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, objectArray);//从后台拿到gridpanel的配置及数据		
		this.callParent(arguments);  
	},
	listeners:{
		itemclick : function(selModel,record){
    			this.onGridItemClick(selModel, record);
    	   }
	},
	//把初始化的数据初始化grid
	initGridData: function(grid, count, append, objectArray){
		var store = grid.store, 
		    items = store.data.items, arr = new Array();	
		var detno = grid.detno;
		    count = objectArray.length||3;//默认添加3行数据
		append = append === undefined ? true : false;
		if(detno){
			var index = items.length == 0 ? 0 : Number(store.last().get(detno));
				for(var i=0;i < count;i++ ){
					var o = new Object();
					o[detno] = index + i + 1;
					o['ig_mainid']=grid.value;
					arr.push(o);
				}
		} else {
			for(var i=0;i < count;i++ ){
				var o = new Object();					
				arr.push(o);
			}
		}	 
		if(objectArray.length == 0){
		    store.loadData(arr,append);
		}else{	
		    store.loadData(objectArray, append);
		}
		var i = 0;
		store.each(function(item, x){
			if(item.index) {
			   i = item.index;
			} else {
				if (i) {
					item.index = i++;
				} else {
					item.index = x;
				}
			}
	   });
	},
	getGridColumnsAndStore: function(grid, url, param, objectArray){
		var me = this;
		var form = Ext.ComponentQuery.query('form');
		if(form && form.length > 0){ 
			grid.readOnly = form[0].readOnly;//grid不可编辑
		}   
		grid.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	async: (grid.sync ? false : true),
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			var limits = res.limits, limitArr = new Array();
        			if(limits != null && limits.length > 0) {//权限外字段
    					limitArr = Ext.Array.pluck(limits, 'lf_field');
    				}
        			Ext.each(res.columns, function(column, y){
        				// column有取别名
        				if(column.dataIndex.indexOf(' ') > -1) {
        					column.dataIndex = column.dataIndex.split(' ')[1];
        				}
        				//power
        				if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
        					column.hidden = true;
        				}
        				//renderer
        				me.GridUtil.setRenderer(grid, column);
        				//logictype
        				me.GridUtil.setLogicType(grid, column);
        			});
        			//data
            		var data = [];
            		if(!res.data || res.data.length == 2){
            			if (grid.buffered) {
                			me.GridUtil.add10EmptyData(grid.detno, data);
                			me.GridUtil.add10EmptyData(grid.detno, data);//添加20条空白数据            				
            			} else {
            				grid.on('reconfigure', function(){// 改为Grid加载后再添加空行,节约200~700ms
                				me.initGridData(grid, 3, false,objectArray);
                			});
            			}
            		} else {
            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		}
            		//store
            		var store = me.GridUtil.setStore(grid, res.fields, data, grid.groupField, grid.necessaryField);
            		//view
            		if(grid.selModel && grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		//dbfind
            		if(res.dbfinds && res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
            		//reconfigure
            		if(grid.sync) {//同步加载的Grid
            			grid.reconfigure(store, res.columns);
            			grid.on('afterrender', function(){
            				me.GridUtil.setToolbar(grid, grid.columns, grid.necessaryField, limitArr);           				
            			});
            		} else {
            			//toolbar
            			if (grid.generateSummaryData === undefined) {// 改为Grid加载后再添加合计,节约60ms
            				me.GridUtil.setToolbar(grid, res.columns, grid.necessaryField, limitArr);
            			}
            			grid.reconfigure(store, res.columns);
            		}
            		//统计总条数
        			grid.down('tbtext[name=sum]').setText("共  "+data.length+"  条数据");
            		if(grid.buffered) {//缓冲数据的Grid
            			grid.verticalScroller = Ext.create('Ext.grid.PagingScroller', {
            				activePrefetch: false,
            				store: store
            			});
            			store.guaranteeRange(0, store.pageSize - 1);
            		}          		     		
        		} else {
        			grid.hide();
        			var form = Ext.ComponentQuery.query('form')[0];
        			if(form) {
        				if(form.items.items.length == 0) {
        					form.on('afterload', function(){
            					me.GridUtil.updateFormPosition(form);//字段较少时，修改form布局
            				});
        				} else {
        					me.GridUtil.updateFormPosition(form);//字段较少时，修改form布局
        				}
        			}
        		}
        	}
        });
	},
	saveValue:function(g,btn){		
		var me = this;
		if(me.value != '' && me.value != null){//已经保存过,更新
			me.updateValue(g,btn);
		}else{
			if(g){			
				if(btn){
					btn.setDisabled(true);
				}
				g.setLoading(true);
			}
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + 'common/getSequenceId.action',
	        	params: {
	        		seqname:'itemgrid_main_seq',
	        		_token: me.id
	        	},
	        	async: false,
	        	method : 'post',
	        	callback : function(options,success,response){
	        		var res = Ext.decode(response.responseText);
	        		if(res.id){
	        			me.value = res.id;
	        			Ext.each(me.store.data.items,function(item,index){
	        				item.set('ig_mainid',res.id);
	        			});
	        			var jsondata = me.GridUtil.getGridStore(me);
	        			Ext.Ajax.request({
	        				url : basePath + 'common/saveItemGrid.action',
	        				params: {
	        					data:jsondata
	        				},
	        				async: false,
	        				method:'post',
	        				callback:function(options,success,response){
	        					if(g){
	        						g.setLoading(false);
	        						if(btn){
									    btn.setDisabled(false);
									 }}
		        					var res = Ext.decode(response.responseText);
				        			if(res.exceptionInfo){
				        				showError(res.exceptionInfo);
				        				return;
									}else{
										me.GridUtil.loadNewStore(me, {
				                             caller : me.logic,
				                             condition : "ig_mainid = '"+me.value+"'"
				                        });
									}	        					
	        				}
	        			});
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        			if(g){			
							if(btn){
								btn.setDisable(false);
							}
							g.setLoading(false);
						}
	        			return;
	        		}
	        	}
	        });	
		}
	},
	updateValue:function(g,btn){
		if(g){			
			if(btn){
				btn.setDisabled(true);
			}
			g.setLoading(true);
		}
		var me = this;
		var jsondata = me.GridUtil.getGridStore(me);
		if(jsondata.length == 0){//未修改数据
			if(g){
				g.setLoading(false);
				if(btn){
				    btn.setDisabled(false);
				 }
				 showError('还未修改或新增数据');
			}
			return ;
		}			
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/updateItemGrid.action',
        	params: {
        		data:jsondata
        	},
        	async: false,
        	method : 'post',
        	callback:function(options,success,response){
				if(g){
					g.setLoading(false);
					if(btn){
					    btn.setDisabled(false);
					}
			   }
			   var res = Ext.decode(response.responseText);
    			if(res.exceptionInfo){
    				showError(res.exceptionInfo);
    				return;
				}else{
					me.GridUtil.loadNewStore(me, {
                         caller : me.logic,
                         condition : "ig_mainid = '"+me.value+"'"
                    });
			 }
           }
        });	
	},
     onGridItemClick:function(selModel,record){  		
			var me = this;
			var grid = selModel.ownerCt;
			//me.GridUtil.onGridItemClick(selModel,record);
			if(grid && !grid.readOnly && !grid.NoAdd){
				var btn = grid.down('erpDeleteDetailButton');
				if(btn)
					btn.setDisabled(false);
				btn = grid.down('erpAddDetailButton');
				if(btn)
					btn.setDisabled(false);
				var btn = grid.down('button[name=saveItemGrid]');
				if(btn)
					btn.setDisabled(false);
			}
      }
});