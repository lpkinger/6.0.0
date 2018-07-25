Ext.define('erp.view.oa.attention.AttentionSubGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.AttentionSubGridPanel',
	id: 'AttentionSubGrid', 
	layout : 'auto',
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    multiselected: [],
    bodyStyle: 'background: #f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),

      selModel: Ext.create('Ext.selection.CheckboxModel',{
    	ignoreRightMouseSelection : false,
    	id:'subgrid',
		listeners:{
            selectionchange:function(selectionModel, selected, options){
          
            }
        },
        renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
          metaData.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
          if(record.data.ap_isuse==1||record.data.aa_isuse==1){
          var grid=Ext.getCmp('AttentionSubGrid');
           grid.multiselected.push(record);
           grid.getSelectionModel().select(grid.multiselected); 
          }
         return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker">&#160;</div>';
        },
        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
        	var me = Ext.getCmp('AttentionSubGrid');
        	var bool = true;
        	var items = me.selModel.getSelection();
            Ext.each(items, function(item, index){
            	if(item&&item.data == record.data){
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
	        	if(checkbox.getAttribute('class') == 'x-grid-row-checker'){
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
                    var grid = Ext.getCmp('AttentionGridPanel');
                    this.deselect(grid.multiselected);
                    grid.multiselected = new Array();
                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
	                Ext.each(els, function(el, index){
	                	el.setAttribute('class','x-grid-row-checker');
	                });
                   header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                } else {
                header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                	var grid = Ext.getCmp('AttentionGridPanel');
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
	}),
	initComponent : function(){ 
	    this.addEvents({
         mouseover: true
        });
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "1=1": condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		var attenedemid=this.BaseUtil.getUrlParam('emid');
		/**if(attenedemid){
		 condition="ap_attentedemid="+attenedemid+" AND ap_emid="+emid;
		}else condition="ap_emid="+0;**/
		var gridParam = {caller: caller, condition: condition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");
    	this.callParent(arguments); 
	},
	viewConfig: {
        stripeRows: true
    },
	loadNewStore: function(grid, param){
		var me = this;
		var main = parent.Ext.getCmp("content-panel");
		if(!main)
			main = parent.parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params: param,
        	async: false,
        	method : 'post',
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		if(!data || data.length == 0){
        			data = [];
        			me.add10EmptyData(grid.detno, data);
        			me.add10EmptyData(grid.detno, data);//添20条吧
        		}
        		grid.store.loadData(data);
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid);
        	}
        });
	},
   getMultiSelected: function(){
		var grid = this;
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		var records=Ext.Array.unique(grid.multiselected);
	      	var params = new Object();
			params.caller = caller;
			var data = new Array();
			console.log(grid.necessaryFields);
			Ext.each(records, function(record, index){
					var o = new Object();
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							o[f] = v;
						});
					}
					data.push(o);
			});
	        params.data = Ext.encode(data);
	        return params;
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
	 getSearchValue: function() {
        var me = this,
            value = Ext.getCmp('search').getValue();
            
        if (value === '') {
            return null;
        }
        if (!me.regExpMode) {
            value = value.replace(me.regExpProtect, function(m) {
                return '\\' + m;
            });
        } else {
            try {
                new RegExp(value);
            } catch (error) {
                me.statusBar.setStatus({
                    text: error.message,
                    iconCls: 'x-status-error'
                });
                return null;
            }
            // this is stupid
            if (value === '^' || value === '$') {
                return null;
            }
        }

        return value;
    },
});