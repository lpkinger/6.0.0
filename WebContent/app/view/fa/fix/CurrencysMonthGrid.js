/**
 * ERP项目gridpanel通用样式1
 */
Ext.define('erp.view.fa.fix.CurrencysMonthGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.currencysMonthGrid',
	region: 'south',
	layout : 'fit',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
	tbar:[{
		xtype:'monthdatefield',
		id:'monthfield',
		editable:false
	},{
		id:'searchCurrency',
		cls: 'x-btn-gray',
		iconCls: 'x-button-icon-query',
		width: 60,
		text:'查看'
	},{
		xtype:'erpSaveButton'
	},{
		xtype:'erpDeleteButton'
	},{
		text: '取上期月末汇率',
		name: 'getlastend',
		cls: 'x-btn-gray',
		margin: '0 0 0 5'
	},{
		text: '取月初汇率',
		name: 'getcrrate',
		cls: 'x-btn-gray',
		margin: '0 0 0 5'
	},{
		xtype:'erpSyncButton'
	},'->',{
		xtype:'erpCloseButton'
	}],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
	initComponent : function(){ 
		var gridCondition = this.BaseUtil.getUrlParam('gridCondition');
		gridCondition = (gridCondition == null || gridCondition == "null") ? "" : gridCondition;
		var conf = ' 1=1 ';
		if(Ext.String.trim(gridCondition)==''||Ext.String.trim(gridCondition)==null||Ext.String.trim(gridCondition)=='null'){
			var searchfield = Number(Ext.Date.format(new Date(), 'Ym'));
			conf = conf+' and cm_yearmonth='+searchfield;
		}else{
			conf = conf+gridCondition;
		}
		
    	var gridParam = {caller: caller, condition: conf};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
		this.callParent(arguments); 
	},
	getGridStore: function(){
		var grid = this;
		var jsonGridData = new Array();
		var s = grid.getStore().data.items;//获取store里面的数据
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			if(s[i].dirty){
				var bool = true;
				Ext.each(grid.necessaryField, function(f){
					if(data[f] == null){
						bool = false;
						showError("有必填项未填写!代号:" + f);return;
					}
				});
				if(bool){
					Ext.each(grid.columns, function(c){
						if(c.xtype == 'datecolumn'){
							if(Ext.isDate(data[c.dataIndex])){
								data[c.dataIndex] = Ext.Date.toString(data[c.dataIndex]);//在这里把GMT日期转化成Y-m-d格式日期
							} else {
								data[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d');//如果用户没输入日期，或输入有误，就给个默认日期，
								//或干脆return；并且提示一下用户
							}
						} else if(c.xtype == 'datetimecolumn'){
							if(Ext.isDate(data[c.dataIndex])){
								data[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
							} else {
								data[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
							}
						} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
							if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
								data[c.dataIndex] = '0';//也可以从data里面去掉这些字段
							}
						}
					});
					jsonGridData.push(Ext.JSON.encode(data));
				}
			}
		}
		return jsonGridData;
	}
});