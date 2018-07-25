Ext.define('erp.view.pm.source.SourceGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.SourceGridPanel',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    height:height-26,//坑爹的样式
    layout : 'fit',
    multiselected: [],
    store: [],
    columns: [],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
    headerCt: Ext.create("Ext.grid.header.Container",{
	   forceFit: false,
       sortable: true,
       enableColumnMove:true,
       enableColumnResize:true,
       enableColumnHide: true
    }),
    invalidateScrollerOnRefresh: false,
    viewConfig: {
        trackOver: false
    },
    buffered: true,
    sync: true,
   /* selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	ignoreRightMouseSelection : false,
			listeners:{
	            selectionchange:function(selectionModel, selected, options){
	          
	            }
	        },
	        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
	        	var me = Ext.getCmp('grid');
	        	var bool = true;
	        	var items = me.selModel.getSelection();
	            Ext.each(items, function(item, index){
	            	if(this.index == record.index){
	            		bool = false;
	            		me.selModel.deselect(record);
	            		Ext.Array.remove(items, item);
	            		Ext.Array.remove(me.multiselected, record);
	            	}
	            });
	            Ext.each(me.multiselected, function(item, index){
	            	items.push(item);
	            });
	            me.selModel.select(items);
	        	if(bool){
	        		view.el.focus();
		        	var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
		        	if(checkbox.getAttribute && checkbox.getAttribute('class') == 'x-grid-row-checker'){
		        		me.multiselected.push(record);
		        		items.push(record);
		        		me.selModel.select(items);
		        	} else {
		        		me.selModel.deselect(record);
		        		Ext.Array.remove(me.multiselected, record);
		        	}
	        	}
	        },
	        onHeaderClick: function(headerCt, header, e) {
	            if (header.isCheckerHd) {
	                e.stopEvent();
	                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
	                if (isChecked) {
	                    this.deselectAll(true);
	                    var grid = Ext.getCmp('grid');
	                    this.deselect(grid.multiselected);
	                    grid.multiselected = new Array();
	                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                } else {
	                	var grid = Ext.getCmp('grid');
	                	this.deselect(grid.multiselected);
		                grid.multiselected = new Array();
		                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    this.selectAll(true);
	                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                }
	            }	           
	        }
	}),*/
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	checkOnly : true,
		ignoreRightMouseSelection : false,
		listeners:{
	        selectionchange:function(selModel, selected, options){
	        	selModel.view.ownerCt.summary();
	        	selModel.view.ownerCt.selectall = false;
	        }
	    },
	    getEditor: function(){
	    	return null;
	    },
	    onHeaderClick: function(headerCt, header, e) {
	        if (header.isCheckerHd) {
	            e.stopEvent();
	            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
	            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
	                this.deselectAll(true);
	            } else {
	                this.selectAll(true);
	                this.view.ownerCt.selectall = true;
	            }
	        }
	    }
	}),
	initComponent : function(){ 
	    //caller='MRPSSaleM';
		caller = getUrlParam('isSaleForecast')=='true'?'MRPSForeCastM':'MRPSSaleM';
		var ismrpSeparateFactory = getUrlParam('ismrpSeparateFactory');
		if(ismrpSeparateFactory=='true'){
			if(caller='MRPSSaleM'){
				caller='MRPSSaleD';
			}else{
				caller='MRPSForeCastD';
			}
		}
		this.getCount(caller, condition);
    	this.callParent(arguments); 
	},
	/*listeners:{
		scrollershow:function(scroller,orientation){
			if(scroller.dock=="bottom"){
				scroller.setPosition( 100,50,true );
			}
		}
	},*/
	getMultiSelected: function(){
		var grid = this;
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	grid.multiselected.push(item);
        });
		return this.unique(grid.multiselected);
	},
	unique: function(items) {
		var d = new Object();
		Ext.Array.each(items, function(item){
			d[item.id] = item;
		});
		return Ext.Object.getValues(d);
	},
	getFields: function(tn, fields, con){
		var des = '';
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/getFields.action",
        	params: {
        		tablename: tn,
        		field: fields,
    			condition: con
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success && res.data != null){
        			des = res.data;
        		}
        	}
		});
		return des;
	},
	getCount: function(caller, condition){
		var me = this;
		//condition
		var OrderKind=caller.indexOf("Sale") >0 ? 'SALE' :'FORECAST';
		var distinct=null;
		if(kind=='MDS'){			
		  condition=condition+"  sd_id not in (select mdd_sdid from mdsdetail where mdd_orderkind='"+OrderKind+"' AND mdd_mainid="+keyValue+") ";  
		  
		}else {
		  condition=condition+"  sd_id not in (select md_sdid from mpsdetail where md_orderkind='"+OrderKind+"' AND md_mainid="+keyValue+") ";  
		}
		var sourcecode = me.getFields("MPSMain","mm_sourcecode","mm_id="+keyValue);
		if (sourcecode && sourcecode!='' && sourcecode!=' ' && sourcecode!='ALL' && sourcecode!='全部' ){
			if(OrderKind=='SALE'){
				condition=condition+" and sa_cop='"+sourcecode+"' ";
			}else if(OrderKind=='FORECAST'){
				condition=condition+" and sf_cop='"+sourcecode+"' ";
			} 
		}
		if(caller=='MRPSSaleM'){
			distinct="sa_code";
		}else if(caller=="MRPSForeCastM"){
			distinct="sf_code";
		}
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + 'pm/source/sourceCount.action',
        	params: {
        		caller: caller,
        		condition: condition,
        		distinct:distinct
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		dataCount = res.count;
        		Ext.getCmp('datacount').setValue('共  '+dataCount+' 条');
        		me.getColumnsAndStore(caller, condition, page, pageSize);
        	}
        });
	},
	getColumnsAndStore: function(caller, condition, page, pageSize){
		var me = this;
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'pm/source/source.action',
        	params: {
        		caller: caller,
        		condition:  condition,         		
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
        		
        			var store = Ext.create('Ext.data.Store', {
            		    fields: res.fields,
            		    data: data
            		}); 
            		me.reconfigure(store, res.columns);
            		var View = me.view;//grid 会往下隐藏几条记录  
                    if(View){
                        var tableEl = View.el.child('.x-grid-table');
                        if(tableEl){
                      	  tableEl.dom.style.marginBottom = '307px';
                        }
                    }
                  var grid=Ext.getCmp('grid');
                  grid.setHeight(height);//不解释
                  grid.getSelectionModel().deselectAll(true);//不解释
                  grid.multiselected = new Array();
        	}
        });
	},
});