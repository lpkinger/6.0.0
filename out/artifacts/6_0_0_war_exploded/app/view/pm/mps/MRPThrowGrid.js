Ext.define('erp.view.pm.mps.MRPThrowGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpBatchDealGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'batchDealGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store: [],
    columns: [],
    tempStore:new Object(),
    bodyStyle: 'background-color:#f1f1f1;',
    headerCt: Ext.create("Ext.grid.header.Container"),
    invalidateScrollerOnRefresh: false,
    viewConfig: {
        trackOver: false
    },
   buffered: true,
   bufferSize:300,
   headerCt: Ext.create("Ext.grid.header.Container",{
	    forceFit: false,
       sortable: true,
       enableColumnMove:true,
       enableColumnResize:true,
       enableColumnHide: true
    }),
    dockedItems: [{
        xtype: 'toolbar',
        items:[{
        	xtype: 'tbtext',
        	id: 'storeCount',
        	tpl: '筛选结果: {count}条'
        }, '->', {
        	xtype: 'tbtext',
        	id: 'selectedCount',
        	tpl: '已选择: {count}条'
        }],
        dock: 'bottom'
	}],
    sync: true,
    plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
    	remoteFilter: true
    }),
     Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1,
        listeners:{
        	'edit':function(editor,e,Opts){
        		var record=e.record;
        		if(e.originalValue!=e.value){
        			  if(record.data.md_status && record.data.md_status!='未投放'){
			    			showError('不能修改已投放的数据');
			    			return ;
			    		}   				    	
			    		var grid=Ext.getCmp('batchDealGridPanel');
			    		Ext.Ajax.request({
			    			url:basePath+'pm/mrp/updateFieldData.action',
		    			   	params: {
		    			   		caller:caller,
		    			   		data:e.value,
		    			   		field:e.field,
		    			   		keyField:grid.keyField,
		    			   	    keyValue:record.data[grid.keyField]
		    			   	},
		    			   	method : 'post',
		    			   	callback : function(options,success,response){
		    			   		var local=Ext.decode(response.responseText);
		    			   		if(local.success) {
		    			   		 showMessage('提示', '保存成功!', 1000);
		    			   		}else {
		    			   			showError(local.exceptionInfo);
		    			   		}
		    			   	}
			    		 });
        		/*	Ext.MessageBox.show({
   				     title:'保存修改?',
   				     msg: '数据已修改需要保存吗？',
   				     buttons: Ext.Msg.YESNO,
   				     icon: Ext.Msg.WARNING,
   				     fn: function(btn){
   				    	 if(btn == 'yes'){
   				    		//保存  
   				    		if(record.data.md_status && record.data.md_status!='未投放'){
   				    			showError('不能修改已投放的数据');
   				    			return ;
   				    		}   				    	
   				    		var grid=Ext.getCmp('batchDealGridPanel');
   				    		Ext.Ajax.request({
   				    			url:basePath+'pm/mrp/updateFieldData.action',
   			    			   	params: {
   			    			   		caller:caller,
   			    			   		data:e.value,
   			    			   		field:e.field,
   			    			   		keyField:grid.keyField,
   			    			   	    keyValue:record.data[grid.keyField]
   			    			   	},
   			    			   	method : 'post',
   			    			   	callback : function(options,success,response){
   			    			   		var local=Ext.decode(response.responseText);
   			    			   		if(local.success) {
   			    			   		 showMessage('提示', '保存成功!', 1000);
   			    			   		}else {
   			    			   			showError(local.exceptionInfo);
   			    			   		}
   			    			   	}
   				    		 });
   				    	 } else if(btn == 'no'){
   				    		//不保存	
   				    		 e.record.reject();
   				    	 } else {
   				    		 return;
   				    	 }
   				     }
   				});*/
        		}
        	}
        }
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
   	features : [Ext.create('Ext.grid.feature.Grouping',{
   		//startCollapsed: true,
   		hideGroupedHeader: true,
        groupHeaderTpl: '{name} (Count:{rows.length})'
    }),{
        ftype : 'summary',
        showSummaryRow : false,//不显示默认合计行
        generateSummaryData: function(){
            var me = this,
	            data = {},
	            store = me.view.store,
	            columns = me.view.headerCt.getColumnsForTpl(),
	            i = 0,
	            length = columns.length,
	            comp;
	        for (i = 0, length = columns.length; i < length; ++i) {
	            comp = Ext.getCmp(columns[i].id);
	            data[comp.id] = me.getSummary(store, comp.summaryType, comp.dataIndex, false);
	            var tb = Ext.getCmp(columns[i].dataIndex + '_' + comp.summaryType);
	            if(tb){
	            	tb.setText(tb.text.split(':')[0] + ':' + data[comp.id]);
	            }
	        }
	        return data;
        }
    }],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	ignoreRightMouseSelection : false,
    	checkOnly: true,
		listeners:{
			selectionchange:function(selModel, selected, options){
				var grid = selModel.view.ownerCt;
				var win = Ext.getCmp('floatWin');
				if(win&&selected.length==0){
					Ext.getCmp('floatWin').close();
				}else if(!win){
					Ext.create('erp.view.core.window.FloatWindow',{});
				}
				if(selected.length == grid.store.totalCount && grid.store.totalCount != grid.multiselected.length){
					grid.multiselected = selected;
				}
				grid.down('#selectedCount').update({
					count: grid.store.data.items[0].get(grid.keyField) ==0 ? 0 : selected.length
				});
            }
        },
        onHeaderClick: function(headerCt, header, e) {
        	var grid = headerCt.ownerCt;
	        if (header.isCheckerHd) {
	            e.stopEvent();
	            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
	            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
	                this.deselectAll(true);
	                grid.multiselected = [];
	            } else {
	            	if(grid.store.totalCount>grid.store.pageSize){
	            		grid.store.pageSize = grid.store.totalCount+1;
	            		var arr = [];
		            	for(var i = 0 ; i < grid.store.prefetchData.length; i++ ){
		            		arr.push(grid.store.prefetchData.items[i].data);
		            	}
		            	grid.getStore().loadData(arr);
	            	}
	                this.selectAll(true);
	                this.view.ownerCt.selectall = true;
	            }
	        }
	    },
        onRowMouseDown: function(view, record, item, index, e) {
            view.el.focus();
            var me = this,
                checker = e.getTarget('.' + Ext.baseCSSPrefix + 'grid-row-checker');
            var grid = view.ownerCt;
            var value = grid.keyField;
            if (!me.allowRightMouseSelection(e)) {
                return;
            }
            if (me.checkOnly && !checker) {
                return;
            }
            if (checker) {
                var mode = me.getSelectionMode();
                if (mode !== 'SINGLE') {
                    me.setSelectionMode('SIMPLE');
                }
                if (me.isSelected(record)) {
                    me.doDeselect(record);
                    Ext.Array.remove(grid.multiselected, record);
                } else {
                    me.doSelect(record, true);
                	if(record.data[value]!=0){
    		        	grid.multiselected.push(record);
                	}
                }
                me.setSelectionMode(mode);
            } else {
                me.selectWithEvent(record, e);
            }
        },
        getEditor: function(){
        	return null;
        }
    }),
    maxDataSize: 3000,
	initComponent : function(){ 
	    this.GridUtil = Ext.create('erp.util.GridUtil');
	    this.BaseUtil = Ext.create('erp.util.BaseUtil');
	    this.RenderUtil = Ext.create('erp.util.RenderUtil');
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		var gridParam = {caller: caller, condition: '',start:1,end:1000};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");  	
    	this.callParent(arguments);
	},
	getMultiSelected: function(){
		var grid = this;
		grid.multiselected = [];
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		return Ext.Array.unique(grid.multiselected);
	},
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	summary: function(selected){
		var me = this,
			store = this.store,
			items = selected || store.data.items,
			value;
		Ext.each(me.columns, function(c){
			if(c.summaryType == 'sum'){
                value = store.getSum(c.dataIndex);
                me.down('tbtext[id=' + c.dataIndex + '_sum]').setText(c.header + '(sum):' + value);		
			} else if(c.summaryType == 'count'){
                value = items.length;
                me.down('tbtext[id=' + c.dataIndex + '_count]').setText(c.header + '(count):' + value);			
			} else if(c.summaryType == 'average'){
				value = store.getAverage(c.dataIndex);       		        			
				me.down('tbtext[id=' + c.dataIndex + '_average]').setText(c.header + '(average):' + value);
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
                var QueryCondition=Ext.getCmp('dealform').getCondition();
                QueryCondition=QueryCondition!=""&&QueryCondition!=null?
                		QueryCondition+(grid.defaultCondition!=""&&grid.defaultCondition!=null?" AND "+grid.defaultCondition:""):grid.defaultCondition;
               if(QueryCondition!=""&&QueryCondition!=null){
            	   QueryCondition= this.filterCondition!=""&&this.filterCondition!=null?QueryCondition+" AND "+this.filterCondition:QueryCondition;
               }else{
            	   QueryCondition= this.filterCondition!=""&&this.filterCondition!=null?this.filterCondition:QueryCondition;
               }
                this.GridUtil.loadNewStore(grid,{caller:caller,condition:QueryCondition, start: 1, end: 3000});
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        },
        scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		sortchange:function(headerCt, columns, direction){
			var grid = headerCt.ownerCt;
			if(grid.store.totalCount>grid.store.pageSize){
        		grid.store.pageSize = grid.store.totalCount+1;
        		var arr = [];
            	for(var i = 0 ; i < grid.store.prefetchData.length; i++ ){
            		arr.push(grid.store.prefetchData.items[i].data);
            	}
            	grid.getStore().loadData(arr);
            	grid.store.guaranteedEnd = grid.store.totalCount-1;
            	grid.store.sort({
					property : this.dataIndex,
					direction : direction
        		});
        	}
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
    }
});