Ext.QuickTips.init();

Ext.define('erp.view.scm.purchase.AutoInquiryGrid',{
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpAutoInquiryGridPanel',
	id:'inquiryGrid',
	columnLines : true,
    autoScroll : true,
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu','Ext.ux.grid.GridHeaderFilters'],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
	plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit : 1
	}), Ext.create('erp.view.core.plugin.CopyPasteMenu'),Ext.create('Ext.ux.grid.GridHeaderFilters')],
	autoScroll : true, 
	columnLines : true,
	initComponent : function(){
		var me = this;
		this.callParent(arguments);
		me.griddata();
	},
	store : Ext.create('Ext.data.Store', {
		fields: [{name:'pk_id',type:'long'},{name:'pk_code',type:'string'}, {name:'pk_detno',type:'int'}, {name:'pk_name',type:'string'},{name:'kindname',type:'string'},{name:'pk_dhzc',type:'string'},{name:'pk_autoinquirydays',type:'int', defaultValue: '0'}, {name:'pk_autoinquiry',type:'number', defaultValue: '-1'},{name:'qty',type:'double', defaultValue: '0'},{name:'price',type:'double', defaultValue: '0'},{name:'pk_targetqty',type:'double', defaultValue: '0'},{name:'pk_targetprice',type:'double', defaultValue: '0'}],
		data:[]
	}),
	griddata:function(){
		var me = this;
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + 'scm/purchase/getAutoInquiry.action',
        	params: {
        		caller: caller
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data = res.data;
        		var pk = [];
        		if(data != null && data.length > 0){
        			Ext.each(data, function(d, index){
        				var da = {
        					pk_id : d.PK_ID,
            				pk_detno : d.PK_DETNO,
            				pk_name : d.PK_NAME,
            				pk_code : d.PK_CODE,
            				kindname : d.KINDNAME,
            				pk_dhzc : d.PK_DHZC,
            				pk_autoinquiry : d.PK_AUTOINQUIRY,
            				pk_autoinquirydays : d.PK_AUTOINQUIRYDAYS,
            				qty : d.QTY,
            				price : d.PRICE,
            				pk_targetprice:d.PK_TARGETPRICE,
            				pk_targetqty:d.PK_TARGETQTY
        				};
        				pk[index] = da;
        			});
        			Ext.getCmp('inquiryGrid').store.loadData(pk);
        		} 
        	}
		})
	},
	bbar: {xtype:'toolbar',
		items: [{
			xtype : 'tbtext',
			name : 'row'
		},{
			xtype : 'button',
			id:'deletedetail',
			iconCls: 'x-button-icon-close',
	    	cls: 'x-btn-tb',
	    	tooltip: $I18N.common.button.erpDeleteDetailButton,
	    	disabled: true
		}, {
			xtype : 'copydetail'
		}, {
			xtype : 'pastedetail'
		}, {
			xtype : 'updetail'
		}, {
			xtype : 'downdetail'
		}, {
			xtype : 'erpExportDetailButton'
		}]
	},
	necessaryFields:['pk_id','ID'],
	columns:[{
		header:'ID',
		dataIndex:'pk_id',
		width:80,
		hidden:true,filter:{autoDim: true,dataIndex: 'pk_id',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'编号',
		dataIndex:'pk_code',
		align:'center',
		cls : 'x-grid-header-1',
		width:35,
		hidden:true,filter:{autoDim: true,dataIndex: 'pk_code',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'序号',
		dataIndex:'pk_detno',
		xtype:'numbercolumn',
		align:'center',
		logic:'ignore',
		cls : 'x-grid-header-1',
		width:35,filter:{autoDim: true,dataIndex: 'pk_detno',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'种类',
		dataIndex:'pk_name',
		cls : 'x-grid-header-1',
		width:200,
		hidden:true,filter:{autoDim: true,dataIndex: 'pk_name',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'物料种类',
		dataIndex:'kindname',
		logic:'ignore',
		cls : 'x-grid-header-1',
		style:'color:red',
		width:300 ,filter:{autoDim: true,dataIndex: 'kindname',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'计划类型',
		dataIndex:'pk_dhzc',
		cls : 'x-grid-header-1',
		width:100,filter:{autoDim: true,dataIndex: 'em_code',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		editor: {
	        xtype: "combo",
	        queryMode: 'local',
			displayField: 'display',
			valueField: 'value',
			value:'7',
			store: new Ext.data.Store({
	    		fields: ['display', 'value'],
	    		data: [
	    	            {"display": '7', "value": '7'},
	    	            {"display": '30', "value": '30'},
	    	            {"display": '60', "value": '60'},
	    	            {"display": '90', "value": '90'},
	    	            {"display": '180', "value": '180'},
	    	            {"display": '365', "value": '365'}
	    	    ]
	    	}),filter:{autoDim: true,dataIndex: 'pk_dhzc',
				displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	    }, 
	    header:'自动询价天数',
		dataIndex:'pk_autoinquirydays',
		width:100,filter:{autoDim: true,dataIndex: 'em_code',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'实际采购数量',
		dataIndex:'qty',
		cls : 'x-grid-header-1',
		width:100,filter:{autoDim: true,dataIndex: 'qty',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'实际采购金额',
		dataIndex:'price',
		cls : 'x-grid-header-1',
		width:100,filter:{autoDim: true,dataIndex: 'price',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'目标采购数量',
		dataIndex:'pk_targetqty',
		cls : 'x-grid-header-1',
		editor:{
			xtype:'numberfield',
			minValue:0
		},
		width:100,filter:{autoDim: true,dataIndex: 'pk_targetqty',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'目标采购金额',
		dataIndex:'pk_targetprice',
		cls : 'x-grid-header-1',
		editor:{
			xtype:'numberfield',
			minValue:0
		},
		width:100,filter:{autoDim: true,dataIndex: 'pk_targetprice',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	},{
		header:'是否启用(-1/0)',
		dataIndex:'pk_autoinquiry',
		width:120,
		style:'color:red',
		cls : 'x-grid-header-1',
		xtype:'actioncolumn',
		processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
	        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
	        	var record = null;
	        	var dataIndex = this.dataIndex;
	        	var checked = null;					        	
        		record = view.panel.store.getAt(recordIndex);
        		checked = record.get(dataIndex) ==-1?0:-1;
	            record.set(dataIndex, checked);
	            this.fireEvent('checkchange', this, recordIndex, checked);
	            return false;
	        }
	    },
		renderer:function(value, m, record){
	        var cssPrefix = Ext.baseCSSPrefix,
	            cls = [cssPrefix + 'grid-checkheader'];
	        if (value) {
	            cls.push(cssPrefix + 'grid-checkheader-checked');
	        }
	        return '<div class="' + cls.join(' ') + '">&#160;</div>';
	    },filter:{autoDim: true,dataIndex: 'pk_autoinquiry',
			displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
	}],
	listeners: {
		'afterrender': function(){
        	var me = this ;
        	me.reconfigure();
        }
    },
    onExport: function(){
    	var me = this;
    	me.BaseUtil.exportGrid(me,me.title,'',1);//1,不导出合计
    }
});