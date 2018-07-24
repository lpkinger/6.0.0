Ext.override(Ext.grid.column.Column, {
	// 点列头后，进行后台数据排序查找
	doSort: function(state) {
		var tablePanel = this.up('tablepanel');
		if(typeof tablePanel.getCount === 'function') {
			var sortParam = this.getSortParam(),column=this;
			tablePanel.store.sort(sortParam, state); 
		} else {
			var tablePanel = this.up('tablepanel'),
				store = tablePanel.store;
			if (tablePanel.ownerLockable && store.isNodeStore) {
				store = tablePanel.ownerLockable.lockedGrid.store;
			}
			store.sort({
				property: this.getSortParam(),
				direction: state
			});
		}
    }
});
Ext.define('erp.view.crm.chance.b2bBusinessChance.BBCGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.BBCGridPanel',
	layout : 'fit',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	checkOnly : true,
		ignoreRightMouseSelection : false,
	    getEditor: function(){
	    	return null;
	    },
	    onHeaderClick: function(headerCt, header, e) {
	        if (header.isCheckerHd) {
	            e.stopEvent();
	            //如果有已转内部商机的 则无法全选
	            var bad = false;
	            Ext.Array.each(this.store.data.items, function(data,index){
	            	if(data.get('turnBusin')=='1'){
	            		bad = true
	            	}
	            	if(data.get('quoted')=='1'){
	            		bad = true
	            	}
	            });
	            if(bad){
	            	showError('本页有已转内部商机，无法全选！')
	            	return false;
	            }
	            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
	            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
	                this.deselectAll(true);
	            } else {
	                this.selectAll(true);
	                this.view.ownerCt.selectall = true;
	            }
	        }
	    },
	    listeners:{
	    	beforeselect:function(rec){
	    		if(rec.lastSelected.get('turnBusin')=='1'){
	    			showError('此条公共询价已转内部商机，不允许重复转入！')
	    			return false;
	    		}
	    		if(rec.lastSelected.get('quoted')=='1'){
	    			showError('此条公共询价已平台报价！')
	    			return false;
	    		}
	    	}
	    }
	}),
    tbar:{
		xtype:'toolbar',
		height:50,
		style:'background:#f7f7f7',
		items:[{
			margin:'0 0 0 5',
			xtype:'button',
			cls:'x-btn-gray',
			height:22,
			name:'choose',
			text:'转公司商机'
		},{
			margin:'0 0 0 5',
			xtype:'button',
			cls:'x-btn-gray',
			height:22,
			name:'turn',
			text:'我要报价'
		},{
			margin:'2 0 0 15',
			width:215,
			id:'searchfield',
	        xtype: 'searchfield',
	        cls: 'search-field',
	        emptyText:'搜索',//企业、品牌、产品名称、产品型号和产品规格进行模糊查询
	        fieldStyle:'height: 22px;color: rgb(0, 0, 0) !important;background: #fff;border-color: rgb(180, 180, 180);border-radius: 15px;',
	        onTriggerClick: function(){
	        	var f = this;
	        	var g = Ext.getCmp('grid');
        		condition = f.value;
        		g.setLoading(true);
				g.getCount(condition,page,pageSize);
	        }
		},{
			margin:'0 0 0 5',
			xtype:'displayfield',
			cls:'x-display',
			value:'可以对询价企业、品牌、类目(产品名称)、型号和规格进行模糊查询'
		},'->']
    },
    columns:[],
    bodyStyle:'background-color:#f1f1f1;',
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'BBCToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	listeners:{
		afterrender:function(g){
			g.ownerCt.setLoading(true);
		}
	},
	showRowNum:true,
	noSpecialQuery:false,//无特殊查询
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [ Ext.create('erp.view.core.plugin.CopyPasteMenu')];
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){
		this.getCount(condition,page,pageSize);
		this.callParent(arguments); 
		this.addEvents({
		    keydown: true
		});
	} ,
	getCount: function(d, p,n){
		var me = this;
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/crm/chance/getBBClist.action',
        	params: {
        		condition: d,
        		page:p,
        		pageSize:n
        	},
        	timeout: 6000000,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}       
        		dataCount = res.data.totalElements;
        		data = res.data.data;
        		me.dataCount=dataCount;
        		if(me.ownerCt){
        			me.ownerCt.setLoading(false);
        		}
        		me.getColumnsAndStore(data);
        	}
        });
	},
	getColumnsAndStore: function(data){
		var store = Ext.create('Ext.data.Store', {
		    fields: ['date','inbrand','prodTitle','cmpCode','spec','needquantity','enName',
		             'remainingTime','busid','endDate','userTel','recorder','unit','ship','enUU','turnBusin','quoted'],
		    data: data
		});
		var col = [{
			text:'已转公司商机',
			cls:'report-col',
			flex:0.6,
			dataIndex:'turnBusin',
			renderer:function(value){
				if(value=='1'){
					return '是';
				}else if(value=='0'){
					return '否';
				}
			}
		},{
			text:'平台已报价',
			cls:'report-col',
			flex:0.6,
			dataIndex:'quoted',
			renderer:function(value){
				if(value=='1'){
					return '是';
				}else if(value=='0'){
					return '否';
				}
			}
		},{
			text:'发布日期',
			cls:'report-col',
			flex:0.7,
			xtype:'datecolumn',
			dataIndex:'date',
			renderer:function(value){
				return Ext.Date.format(new Date(value),'Y-m-d');
			}
		},{
			text:'品牌',
			cls:'report-col',
			flex:0.7,
			dataIndex:'inbrand'
		},{
			text:'类目(产品名称)',
			dataIndex:'prodTitle',
			cls:'report-col',
			flex:1
		},{
			text:'型号',
			dataIndex:'cmpCode',
			cls:'report-col',
			flex:1
		},{
			text: '规格', 
			dataIndex:'spec',
			flex: 1
		},{
			text:'数量',
			align : 'right', 
			dataIndex:'needquantity',
			cls:'report-col',
			flex:0.5
		},{
			text:'询价企业',
			dataIndex:'enName',
			cls:'report-col',
			flex:1
		},{
			text:'报价截止日期',
			dataIndex:'remainingTime',
			cls:'report-col',
			flex:0.5,
			renderer:function(value){
				return '剩余' + parseInt(value/(1000 * 60 * 60 * 24)) + '天';
			}
		}]
		var grid = this;
		grid.store = store;
		grid.reconfigure(grid.store,col);
		var toolbar=grid.down('BBCToolbar');
		toolbar.afterOnLoad();
		grid.setLoading(false);
	},
	AddMouth:function(date, num){
        //date为格式化后的日期字符yyyy-MM-dd,num为增加的月份
        var mouthnum = parseInt(num);
        var year = parseInt(date.substring(0, 4));
        var mouth = parseInt(date.substring(5, 7));
        var day = parseInt(date.substring(8, 10));
        var time = date.substring(10,date.length);
        if (mouth + mouthnum > 12)
        {
            var newyear = year + 1;
            var newmouth = mouth + mouthnum - 12;
            var newday = day;
        }
        else
        {
            var newyear = year
            var newmouth = mouth + mouthnum;
            var newday = day;
        }
        if(newmouth<10){
        	newmouth = '0'+newmouth
        }
        if(newday<10){
        	newday = '0'+newday
        }
        var newdate = newyear + "-" + newmouth + "-" + newday + time;
        return newdate;
    }
});