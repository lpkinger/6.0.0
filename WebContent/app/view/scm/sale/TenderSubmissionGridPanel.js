Ext.QuickTips.init();

Ext.define('erp.view.scm.sale.TenderSubmissionGridPanel',{
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpTenderSubmissionGridPanel',
	id:'productGrid',
	title:'产品信息',
	plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit : 1
	}),Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	GridUtil:Ext.create('erp.util.GridUtil'),
	store: Ext.create('Ext.data.Store', {
		fields: ['id','index', 'prodCode','brand','prodTitle','unit', 'qty','cycle','taxrate',
			{name:'price',convert: function(value, record) {
				if(value){
					return Math.round(value*Math.pow(10, 6))/Math.pow(10, 6);
				}
            }},'totalprice',{name:'applyStatus', defaultValue: 0},'description']
	}),
	necessaryFields:['cycle','taxrate','price'],
	dbfinds:[{
		dbGridField:'pr_detail',
		field:'pr_detail'
	}],
	columns:[{
		header:'ID',
		dataIndex:'id',
		width:80,
		hidden:true
	},{
		header:'序号',
		dataIndex:'index',
		logic:'ignore',
		align:'center',
		cls : 'x-grid-header-1',
		width:40
	},{
		header:'型号',
		dataIndex:'prodCode',
		cls : 'x-grid-header-1',
		width:200
	},{
		header:'产品名称',
		dataIndex:'prodTitle',
		logic:'ignore',
		cls : 'x-grid-header-1',
		width:300 
	},{
		header:'品牌',
		dataIndex:'brand',
		logic:'ignore',
		cls : 'x-grid-header-1',
		width:100
	},{
		header:'单位',
		dataIndex:'unit',
		logic:'ignore',
		cls : 'x-grid-header-1',
		width:100
	},{
		header:'采购数量',
		xtype:'numbercolumn',
		dataIndex:'qty',
		logic:'ignore',
		align:"right",
		cls : 'x-grid-header-1',
		width:100
	},{
		header:'采购周期',
		xtype:'numbercolumn',
		style:'color:red',
		dataIndex:'cycle',
		align:"right",
		cls : 'x-grid-header-1',
		width:100,
		editor:{
			xtype:'numberfield',
			regex:/^[1-9]\d*$/,
			regexText:'采购周期为大于0的整数',
			hideTrigger:true
		}
	},{
		header:'税率(%)',
		xtype:'numbercolumn',
		style:'color:red',
		dataIndex:'taxrate',
		align:"right",
		cls : 'x-grid-header-1',
		width:100,
		editor:{
			xtype:'numberfield',
			hideTrigger:true,
			minValue:0,
			maxValue:100
		}
	},{
		header:'单价',
		xtype:'numbercolumn',
		style:'color:red',
		dataIndex:'price',
		align:"right",
		cls : 'x-grid-header-1',
		width:100,
		format:'0,000.000000',
		editor:{
			xtype:'numberfield',
			hideTrigger:true,
			minValue:0.000001
		}
	},{
		header:'含税金额',
		xtype:'numbercolumn',
		dataIndex:'totalprice',
		cls : 'x-grid-header-1',
		align:"right",
		width:100,
		logic:'ignore',
		renderer:function(val,meta,rec){
			var num = rec.get('qty');
			var price = rec.get('price');
			if(num&&price){
				var result = Math.round(num*price*100)/100;
				if(val!=result){
					rec.set('totalprice',result);
					return result;
				}
			}
			return val;
		}
	},{
		header:'评标结果',
		dataIndex:'applyStatus',
		logic:'ignore',
		cls : 'x-grid-header-1',
		width:80,
		hidden:true,
		renderer:function(val,meta,rec){
			return val?'中标':'未中标';
		}
	},{
		header:'评标说明',
		dataIndex:'description',
		logic:'ignore',
		hidden:true,
		cls : 'x-grid-header-1',
		width:200,
		renderer:function(val, meta, record, x, y, store, view){
		 	var grid = view.ownerCt,column = grid.columns[y];
		 	meta.style="padding-right:0px!important";
		 	meta.tdAttr = 'data-qtip="' + Ext.String.htmlEncode(val) + '"';  
		 	if(val){
		 		return  '<span style="display:inline-block;padding-left:2px;width:85%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+Ext.String.htmlEncode(val)+'</span>'+
		 				'<span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;"' +
		 				'onClick="Ext.getCmp(\'productGrid\').showTrigger(' + '\''+escape(val)+'\');"></span>';
		 	}
		 	return '';
		 }
	}],	
	showTrigger:function(val){//明细行文本框
		val = unescape(val);
		Ext.MessageBox.minPromptWidth = 600;
	    Ext.MessageBox.defaultTextHeight = 200;
	    Ext.MessageBox.style= 'background:#e0e0e0;';
	    Ext.MessageBox.prompt("详细内容", '',
	    function(btn, text) { 
	    },
	    this, true, //表示文本框为多行文本框    
	    val);
	}
});