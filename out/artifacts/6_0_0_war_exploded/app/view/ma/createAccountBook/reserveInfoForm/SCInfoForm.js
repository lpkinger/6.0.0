Ext.define('erp.view.ma.createAccountBook.reserveInfoForm.SCInfoForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.reserscinfo',
	hideBorders: true, 
	id:'reserveInfo_SCInfo',
	title: '供应链信息',
	cls: 'infos',
	frame:false,
	initComponent : function(){
		var me=this;
		me.callParent(arguments);
	},
	items:[{
        xtype : 'checkboxgroup', 
        columns: 2,
        vertical: true,
        defaults: {
            labelAlign: 'right'
        },
        items : [{
            boxLabel  : '预测类型',
            name: 'SALEFORECASTKIND',
            inputValue: '1'
        }, {
            boxLabel  : 'SALEKIND',
            name: 'sale_type',
            inputValue: '1'
        }, {
            boxLabel  : '客户分类',
            name: 'CUSTOMERKIND',
            inputValue: '1'
        }, {
            boxLabel  : '客户资料',
            name: 'CUSTOMER',
            inputValue: '1'
        }, {
            boxLabel  : '价格资料',
            name: 'PRICE',
            inputValue: '1'
        }, {
            boxLabel  : '采购类型',
            name: 'PURCHASEKIND',
            inputValue: '1'
        }, {
            boxLabel  : '供应商分类',
            name: 'VENDORKIND',
            inputValue: '1'
        }, {
            boxLabel  : '供应商资料',
            name: 'VENDOR',
            inputValue: '1'
        }, {
            boxLabel  : '仓库资料',
            name: 'WAREHOUSE',
            inputValue: '1'
        }, {
            boxLabel  : '仓位资料',
            name: 'WHLOCATION',
            inputValue: '1'
        }, {
            boxLabel  : '制造类别',
            name: 'MAKEKIND',
            inputValue: '1'
        }, {
            boxLabel  : '线别',
            name: 'LINE',
            inputValue: '1'
        }] 
    }]
});