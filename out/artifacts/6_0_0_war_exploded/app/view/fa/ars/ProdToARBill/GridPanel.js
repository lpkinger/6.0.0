Ext.define('erp.view.fa.ars.ProdToARBill.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpProdToARBillGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar'],
	id: 'batchDealGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store: [],
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    bbar: {xtype: 'erpToolbar', enableAdd: false, enableDelete: false, enableCopy: false, enablePaste: false, enableUp: false, enableDown: false},
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.grid.HeaderFilter'),Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    headerCt: Ext.create("Ext.grid.header.Container",{
 	    forceFit: false,
        sortable: true,
        enableColumnMove:true,
        enableColumnResize:true,
        enableColumnHide: true
     }),
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
	            //fieldData,
	            //key,
	            comp;
            //将feature的data打印在toolbar上面
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
	    	checkOnly:true,
			listeners:{
	            selectionchange:function(selModel, selected, options){
	            	var me = this, grid = selModel.view.ownerCt;
	            	me.countAmount();
	            	grid.summary();
	            }
    			
	        },
	        getEditor: function(){
	        	return null;
	        },
	        countAmount: function(record){
	        	var me = this;
            	var grid = Ext.getCmp('batchDealGridPanel');
            	var items = grid.selModel.selected.items;
            	var countamount=0;
            	var arcount=0;
            	var taxsum = 0;
            	var noamounttotal = 0;
            	var	amount = 0, m = 0, 
            	    priceFormat = grid.down('gridcolumn[dataIndex=pd_thisvoprice]').format,
            		fsize = (priceFormat && priceFormat.indexOf('.') > -1) ? 
            				priceFormat.substr(priceFormat.indexOf('.') + 1).length : 6;
            	if(caller =='ProdInOut!ToARBill!Deal!ars' || caller=='ProdInOut!ToARCheck!Deal' ){
                	Ext.each(items,function(item,index){
                		var a = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoprice']),fsize);
                		var b = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoqty']),4);
                		var rate = grid.BaseUtil.numberFormat(Number(item.data['pi_rate']),4);
                		var taxrate = grid.BaseUtil.numberFormat(Number(item.data['pd_taxrate']),4);
                		
                		m =  grid.BaseUtil.numberFormat(a*(b*100)/100,2);
                		countamount += m;
                		arcount = arcount + b;
                		taxsum = taxsum + grid.BaseUtil.numberFormat(m*taxrate/(100+taxrate),2);
                		noamounttotal = noamounttotal + grid.BaseUtil.numberFormat(m/(1+taxrate/100),2);
                		amount = amount + grid.BaseUtil.numberFormat(m*rate,4); 
                	});
                	//金额合计   不能填写  自动显示所选数据条目的本次发票数*本次发票单价 的总和
	               	Ext.getCmp('pi_amounttotal').setValue(countamount);
	               	//开票数量 不能填写 自动显示所选条目的本次发票数的总和
	               	Ext.getCmp('pi_counttotal').setValue(arcount);
	               	Ext.getCmp('ab_taxamount')._val = taxsum;
	               	var differ = Ext.getCmp('ab_differ');
	               	if(differ && !Ext.isEmpty(differ.value)){
	               		Ext.getCmp('ab_taxamount').setValue(taxsum  + Number(differ.value));
	               	} else {
	                	Ext.getCmp('ab_taxamount').setValue(Ext.util.Format.number(taxsum, "0.00"));
	                }
	               	Ext.getCmp('noamounttotal').setValue(noamounttotal);
	               	if(Ext.getCmp('curr_amount')){
	               		Ext.getCmp('curr_amount').setValue(amount);
	               	}
            	}else if(caller=='ProdInOut!ToAPBill!Deal!ars' || caller=='ProdInOut!ToAPCheck!Deal' || caller=='APBill!ToAPCheck!Deal'|| caller =='ARBill!ToARCheck!Deal'){
                	Ext.each(items,function(item,index){
                		var a = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoprice']),fsize);
                		var b = grid.BaseUtil.numberFormat(Number(item.data['pd_thisvoqty']),4);
                		var taxrate = grid.BaseUtil.numberFormat(Number(item.data['pd_taxrate']),4);
                		
                		m =  grid.BaseUtil.numberFormat(a*(b*100)/100,2);
                		countamount += m;
                		arcount = arcount + b;
                		taxsum = taxsum + grid.BaseUtil.numberFormat(m*taxrate/(100+taxrate),2);
                		noamounttotal = noamounttotal + grid.BaseUtil.numberFormat(m/(1+taxrate/100),2);
                	});
                	//金额合计   不能填写  自动显示所选数据条目的本次发票数*本次发票单价 的总和
	               	Ext.getCmp('pi_amounttotal').setValue(countamount);
	               	//开票数量 不能填写 自动显示所选条目的本次发票数的总和
	               	Ext.getCmp('pi_counttotal').setValue(arcount);
	               	Ext.getCmp('taxsum')._val = taxsum;
	               	var differ = Ext.getCmp('differ');
	               	if(differ && !Ext.isEmpty(differ.value)){
	               		Ext.getCmp('taxsum').setValue(taxsum  + Number(differ.value));
	               	} else {
	                	Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum, "0.00"));
	                }
	               	Ext.getCmp('noamounttotal').setValue(noamounttotal);
            	}else{
                	Ext.each(items,function(item,index){
                		var taxrate = grid.BaseUtil.numberFormat(Number(item.data['pd_taxrate']),4);
                		var a = Number(item.data['pd_thisvoprice']);
                		var b = Number(item.data['pd_thisvoqty']);
                		countamount += grid.BaseUtil.numberFormat(a*b,2);
                		arcount = arcount + b;
                		taxsum = taxsum +  Number(grid.BaseUtil.numberFormat((a*b*taxrate/100)/(1+taxrate/100),2));
                		noamounttotal = noamounttotal + grid.BaseUtil.numberFormat(Number(item.data['pd_nettotal']),2);
                	});
                	//金额合计   不能填写  自动显示所选数据条目的本次发票数*本次发票单价 的总和
	               	Ext.getCmp('pi_amounttotal').setValue(grid.BaseUtil.numberFormat(countamount,2));
	               	//开票数量 不能填写 自动显示所选条目的本次发票数的总和
	               	Ext.getCmp('pi_counttotal').setValue(arcount);
	               	Ext.getCmp('taxsum')._val = taxsum;
	               	Ext.getCmp('taxsum').setValue(taxsum  + Number(Ext.getCmp('differ').value));
	               	Ext.getCmp('noamounttotal').setValue(noamounttotal);
            	}

	        }
	}),
	setToolbar: function(columns){
		var grid = this;
		var items = [];
		Ext.each(columns, function(column){
			if(column.summaryType == 'sum'){
				items.push('-',{
					id: column.dataIndex + '_sum',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.text + '(sum):0'
				});
			} else if(column.summaryType == 'average') {
				items.push('-',{
					id: column.dataIndex + '_average',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.text + '(average):0'
				});
			} else if(column.summaryType == 'count') {
				items.push('-',{
					id: column.dataIndex + '_count',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.text + '(count):0'
				});
			}
		});
		grid.bbar = {
			xtype: 'toolbar',
	        dock: 'bottom',
	        items: items
		};
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
    		column.haveRendered = true;
    	}
	},
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	summary: function(){
		var me = this,
			store = this.store,
			value,
			items = me.selModel.getSelection();
		Ext.each(me.columns, function(c){
			if(c.summaryType == 'sum'){
                value = me.getSum(items, c.dataIndex);
                if(c.xtype == 'numbercolumn') {
    				value = Ext.util.Format.number(value, (c.format || '0,000.000'));
    			}
                me.down('tbtext[id=' + c.dataIndex + '_sum]').setText(c.text + '(sum):' + value);		
			} else if(c.summaryType == 'count'){
                value = store.getCount();
                me.down('tbtext[id=' + c.dataIndex + '_count]').setText(c.text + '(count):' + value);			
			} else if(c.summaryType == 'average'){
				value = store.getAverage(c.dataIndex);   
				if(c.xtype == 'numbercolumn') {
					value = Ext.util.Format.number(value, (c.format || '0,000.000'));
				}
				me.down('tbtext[id=' + c.dataIndex + '_average]').setText(c.text + '(average):' + value);
			}		
		});
	},
	getSum: function(records, field) {
    	if (arguments.length  < 2) {
    		return 0;
    	}
        var total = 0,
            i = 0,
            len = records.length;
        for (; i < len; ++i) {
			total += records[i].get(field);
		}
        return total;
    },
	setLogicType: function(column, logic, y){
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
		} else if(logic == 'groupField'){
			grid.groupField = column.dataIndex;
		}
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
	sync: true,
	initComponent : function(){ 
		condition = this.BaseUtil.getUrlParam('urlcondition');
		this.RenderUtil = Ext.create('erp.util.RenderUtil');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
//		this.defaultCondition = Ext.isEmpty(condition) ? 'abs(pd_invoqty)<abs(pd_inqty-pd_outqty)' : 
//			(condition + ' AND abs(pd_invoqty)<abs(pd_inqty-pd_outqty)');
		var gridParam = {caller: caller, condition: condition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");
    	this.callParent(arguments); 
	},
	listeners : {
		storeloaded : function(grid, data) {
			this.setMore(data.length);
		},
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
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