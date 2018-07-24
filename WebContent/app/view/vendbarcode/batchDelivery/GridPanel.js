Ext.define('erp.view.vendbarcode.batchDelivery.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpBatchDeliveryGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'batchDeliveryGridPanel', 
	keyField:'PD_ID',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    tempStore:new Object(),
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    dockedItems: [{xtype: 'erpToolbar', dock: 'bottom', enableAdd: false, enableDelete: false, enableCopy: false, enablePaste: false, enableUp: false, enableDown: false}],
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    /*headerCt: Ext.create("Ext.grid.header.Container",{
 	    forceFit: false,
        sortable: true,
        enableColumnMove:true,
        enableColumnResize:true,
        enableColumnHide: true
     }),*/
   	features : [Ext.create('Ext.grid.feature.Grouping',{
   		//startCollapsed: true,
   		hideGroupedHeader: true,
        groupHeaderTpl: '{name} (Count:{rows.length})'
    }),{
        ftype : 'summary',
        showSummaryRow : false,//不显示默认合计行
        generateSummaryData: function(){
	        return {};
        }
    }],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	checkOnly : true,
		ignoreRightMouseSelection : false,
		listeners:{
	        selectionchange:function(selModel, selected, options){
	        	selModel.view.ownerCt.summary(true);
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
		var me = this;
	    this.addEvents({
		    storeloaded: true
		});
    	this.callParent(arguments);
    	me.down('erpExportDetailButton').hide(); //隐藏下载模板和导入明细数据按钮
    	this.initRecords();
	},
    iconCls: 'icon-grid',
    frame: true,
	fieldStyle:'background:#e0e0e0;',
    bodyStyle:'background-color:#f1f1f1;',
	columns: [{
        header: 'ID',
        width: 0,
        align:'center',
        hidden:true,
        dataIndex: 'PD_ID',
        readOnly:true
    },{
       /* header: '<font color="red">数量</font>',*/
    	header:'本次数量',
    	cls:'myCls',
        style :"text-align:center",
        width: 80,
        align:'center',
        dataIndex: 'PD_TQTY',
        readOnly:false,
        logic:"necessaryField",
        allowBlank:false,
        filter: {xtype:"textfield", filterName:"PD_TQTY"},
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			},
		 renderer : function(val, meta, record) {
				var v2 = record.get('FINISHQTY');
				if(val > v2){
					showError("不允许超过默认数量"+v2);
					record.set('PD_TQTY', v2);
					record.get('PD_TQTY').setValue(v2);
				}else{
					return val;
				}
				
			}
    },{
    	header: '采购单号',
    	style :"text-align:center",
        width: 135,
        lign:'left',
        dataIndex: 'PD_CODE',
        sortable: true,
        readOnly:true,
        filter: {xtype:"textfield", filterName:"PD_CODE"}
    },{
        text: '序号',
        width: 40,
        align:'center',
        dataIndex: 'PD_DETNO',
        sortable: true,
        readOnly:true,
        filter: {xtype:"textfield", filterName:"PD_DETNO"}
    },{
        header: '物料编号',
        style :"text-align:center",
        width: 125,
        align:'left',
        dataIndex: 'PD_PRODCODE',
        sortable: true,
        readOnly:true,
        filter: {xtype:"textfield", filterName:"PD_PRODCODE"}
    },{
        header: '名称',
        style :"text-align:center",
        width: 150,
        align:'left',
        dataIndex: 'PR_DETAIL',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PR_DETAIL"}
    },{
        header: '规格',
        style :"text-align:center",
        width: 225,
        align:'left',
        dataIndex: 'PR_SPEC',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PR_SPEC"}
    },{
        header: '品牌',
        style :"text-align:center",
        width: 100,
        align:'left',
        dataIndex: 'PR_BRAND',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PR_BRAND"}
    },{
        header: '原厂型号',
        style :"text-align:center",
        width: 125,
        align:'left',
        dataIndex: 'PR_ORISPECCODE',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PR_ORISPECCODE"}
    },{
        header: '采购数量',
        style :"text-align:center",
        width: 80,
        align:'left',
        dataIndex: 'PD_QTY',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PD_QTY"}
    },{
        header: '供应商名称',
        style :"text-align:center",
        width: 0,
        align:'left',
        dataIndex: 'PU_VENDNAME',
        sortable: true,
        readOnly:true,
        filter: {xtype:"textfield", filterName:"PU_VENDNAME"}
    },{
        header: '分装数量',
        style :"text-align:center",
        width: 80,
        align:'left',
        dataIndex: 'UNITPACKAGE',
        sortable: true,
        filter: {xtype:"textfield", filterName:"UNITPACKAGE"},
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
        header: '尾数分装',
        style :"text-align:center",
        width: 125,
        align:'left',
        dataIndex: 'MANTISSAPACKAGE',
        sortable: true,
        filter: {xtype:"textfield", filterName:"MANTISSAPACKAGE"},
        editor : {
    		xtype : 'textfield',
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
        header: '外箱容量',
        style :"text-align:center",
        width: 80,
        align:'left',
        dataIndex: 'BOXQTY',
        sortable: true,
        filter: {xtype:"textfield", filterName:"BOXQTY"},
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
        header: '供应商批号',
        style :"text-align:center",
        width: 120,
        align:'left',
        dataIndex: 'LOTCODE',
        sortable: true,
        filter: {xtype:"textfield", filterName:"LOTCODE"},
        editor : {
    		xtype : 'textfield',
			}
    },{
        header: '生产日期',
        style :"text-align:center",
        width: 100,
        align:'left',
        dataIndex: 'MADEDATE',
        sortable: true,
        format:'Y-m-d',
        filter: {xtype:"textfield", filterName:"MADEDATE"},
        editor : {
        		xtype:"datefield"
    			},
		renderer : function(val, meta, record) {
			if(val !=null  && val != ''){
				val = Ext.Date.format(record.data['MADEDATE'], 'Y-m-d');
				return val;
			}else{
				return val;
			}
			
		}
    	
    },{
        header: '备品数',
        style :"text-align:center",
        width: 80,
        align:'left',
        dataIndex: 'BEIPINQTY',
        sortable: true,
        filter: {xtype:"textfield", filterName:"BEIPINQTY"},
        editor : {
    		xtype : 'numberfield',
    		minValue: "0",
            maxLength: 4000,
			hideTrigger: true,
			editable: true,
			}
    },{
        header: '已转数量',
        style :"text-align:center",
        width: 0,
        align:'left',
        dataIndex: 'FINISHIQTY',
        sortable: true,
        filter: {xtype:"textfield", filterName:"FINISHIQTY"}
    },{
        header: '应付供应商',
        style :"text-align:center",
        width: 0,
        align:'left',
        dataIndex: 'PU_RECEIVECODE',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PU_RECEIVECODE"},
        editor : {
    		xtype : 'textfield',
			}
    },{
        header: '币别',
        style :"text-align:center",
        width: 0,
        align:'left',
        dataIndex: 'PU_RECEIVECODE',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PU_RECEIVECODE"},
        editor : {
    		xtype : 'textfield',
			}
    },{
        header: '付款方式',
        style :"text-align:center",
        width: 0,
        align:'left',
        dataIndex: 'PU_PAYMENTSCODE',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PU_PAYMENTSCODE"},
        editor : {
    		xtype : 'textfield',
			}
    },{
        header: '采购员编号',
        style :"text-align:center",
        width: 0,
        align:'left',
        dataIndex: 'PU_BUYERCODE',
        sortable: true,
        filter: {xtype:"textfield", filterName:"PU_BUYERCODE"},
        editor : {
    		xtype : 'textfield',
			}
    }],
	sync: true,
	getMultiSelected: function(){
		var grid = this;
		grid.multiselected = [];
        var items = grid.selModel.getSelection();
        if(grid.selectall && items.length == grid.store.pageSize && grid.store.prefetchData) {
        	items = grid.store.prefetchData.items;
        }
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		return grid.multiselected;
	},
	unique: function(items) {
		var d = new Object();
		Ext.Array.each(items, function(item){
			d[item.id] = item;
		});
		return Ext.Object.getValues(d);
	},
	setRenderer: function(column){
		var grid = this;
		if(!column.haveRendered && column.renderer != null && column.renderer != ""){
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
    		column.renderName=renderName;
    		column.haveRendered = true;
    	}
	},
	setLogicType: function(column, logic, headerCss,y){
		var grid = this;
		if(logic == 'detno'){
			grid.detno = column.dataIndex;
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
			if(headerCss.headerColor)
					column.style = 'color:#' + headerCss.headerColor;
		} else if(logic == 'groupField'){
			grid.groupField = column.dataIndex;
		}
	},
	setToolbar: function(columns){
		var grid = this;
		var items = [];
		var bool = true;
		Ext.each(grid.dockedItems.items, function(item){
			if(item.dock == 'bottom' && item.items){//bbar已存在
				bool = false;
			}
		});
		if(bool){
			Ext.each(columns, function(column){
				if(column.summaryType == 'sum'){
					items.push('-',{
						id: (column.dataIndex + '_sum').replace(/,/g,'$'),
						itemId: (column.dataIndex).replace(/,/g,'$'),
						xtype: 'tbtext',
						text: column.text + '(sum):0'
					});
				} else if(column.summaryType == 'average') {
					items.push('-',{
						id: (column.dataIndex + '_average').replace(/,/g,'$'),
						itemId: (column.dataIndex).replace(/,/g,'$'),
						xtype: 'tbtext',
						text: column.text + '(average):0'
					});
				} else if(column.summaryType == 'count') {
					items.push('-',{
						id: (column.dataIndex + '_count').replace(/,/g,'$'),
						itemId: (column.dataIndex).replace(/,/g,'$'),
						xtype: 'tbtext',
						text: column.text + '(count):0'
					});
				}
			});
			grid.addDocked({
	    			xtype: 'toolbar',
	    	        dock: 'bottom',
	    	        items: items
	    	});
		}else{
			var bars = Ext.ComponentQuery.query('erpToolbar');
			if(bars.length > 0){
				Ext.each(columns, function(column){
        			if(column.summaryType == 'sum'){
        				bars[0].add('-');
        				bars[0].add({
        					id: (column.dataIndex+'_sum').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(sum):0'
        				});
        			} else if(column.summaryType == 'average') {
        				bars[0].add('-');
        				bars[0].add({
        					id: (column.dataIndex + '_average').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(average):0'
        				});
        			} else if(column.summaryType == 'count') {
        				bars[0].add('-');
        				bars[0].add({
        					id: (column.dataIndex + '_count').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(count):0'
        				});
        			}
        		});
			}
		}		
	},
	setDefaultStore: function(d, f){
		var me = this;
		var data = [];
		if(!d || d.length == 2){
			me.GridUtil.add10EmptyData(me.detno, data);
			me.GridUtil.add10EmptyData(me.detno, data);
		} else {
			data = Ext.decode(d.replace(/,}/g, '}').replace(/,]/g, ']'));
		}
		var store = Ext.create('Ext.data.Store', {
		    fields: f,
		    data: data,
		    groupField: me.groupField,
		    getSum: function(field) {
	            var records = me.selModel.getSelection(),
	            	total = 0,
	                i = 0,
	                len = records.length;
	            for (; i < len; ++i) {
	            	total += records[i].get(field);
	            }
	            return total;
		    },
		    getCount: function() {
		    	var records = me.selModel.getSelection(),
		    		count = 0;
		    	Ext.each(records, function(item){
		    		count++;
		    	});
		        return count;
		    },
		    getAverage: function(field) {
		    	var records = me.selModel.getSelection(),
		    		count = 0,
		    		sum = 0;
		    	Ext.each(records, function(item){
		    		if(item.data[me.necessaryField] != null && item.data[me.necessaryField] != ''){
		    			count++;sum += item.data[field];
		    		}
		    	});
		        return Ext.Number.format(sum/count, '0.00');
		    }
		});
		return store;
	},
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	summary: function(onlySelected){
		var me = this,
			store = this.store, items = store.data.items, selected = me.selModel.getSelection(), 
			value, bar = (onlySelected ? me.down('toolbar[to=select]') : me.down('erpToolbar'));
		Ext.each(me.columns, function(c){
			if (onlySelected && !bar)
				bar = me.addDocked({
			    	xtype: 'toolbar',
			    	dock: 'bottom',
			    	to: 'select',
			    	items: [{
			    		xtype: 'tbtext',
			    		text: '已勾选',
			    		style: {
			    			marginLeft: '6px'
			    		}
			    	}]
			    })[0];
			if(c.summaryType == 'sum'){
				me.updateSummary(c, me.getSum(onlySelected ? selected : items, c.dataIndex), 'sum', bar);
			} else if(c.summaryType == 'count'){
                me.updateSummary(c, (onlySelected ? selected.length : items.length), 'count', bar);
			}
		});
		if (bar) {
			var counter = bar.down('tbtext[itemId=count]');
			if (!counter) {
				bar.add('->');
				counter = bar.add({
					xtype: 'tbtext',
					itemId: 'count'
				});
			}
			counter.setText(onlySelected ? ('已选: ' + selected.length + ' 条' ) : ('共: ' + items.length + ' 条'));
		}
	},
	updateSummary: function(column, value, type, scope) {
		var id = column.dataIndex + '_' + type + (scope.to == 'select' ? '_select' : '');
		id=id.replace(/,/g,'$');
		b = scope.down('tbtext[id=' + id + ']');
		if (!b) {
			scope.add('-');
			b = scope.add({xtype: 'tbtext', id: id});
		}
		if(column.xtype == 'numbercolumn') {
			value = Ext.util.Format.number(value, (column.format || '0,000.000'));
		}
		b.setText(column.text + '(' + type + '):' + value);
	},
	initRecords: function(){
		var records = this.store.data.items;
		var count = 0;
		Ext.each(records, function(record){
			if(!record.index){
				record.index = count++;
			}
		});
	},
	getSum: function(records, field) {
        var total = 0,
            i = 0,
            len = records.length;
        (len == 0) && (records = this.store.data.items); 
        for (; i < len; ++i) {
			total += records[i].get(field);
		}
        return total;
	},
	setMore : function(c) {
		if(c >= 1000) {
			var g = this, e = g.down('erpToolbar');
			if(!g.bigVolume) {
		    	var m = e.down('tool[name=more]');
		    	if(!m) {
		    		m = Ext.create('Ext.panel.Tool', {
		    			name: 'more',
		    			type: 'right',
						margin: '0 5 0 5',
						handler: function() {
							g.bigVolume = true;
							g.ownerCt.down('form').onQuery();
							m.disable();
						}
		    		});
		    		e.add('->');
		    		e.add(m);
		    	} else {
		    		m.show();
		    	}
		    }
		}
	}
});