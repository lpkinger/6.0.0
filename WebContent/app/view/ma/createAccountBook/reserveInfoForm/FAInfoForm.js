Ext.define('erp.view.ma.createAccountBook.reserveInfoForm.FAInfoForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.reserfainfo',
	hideBorders: true, 
	id:'reserveInfo_FAInfo',
	title: '财务信息',
	cls: 'infos',
	frame:false,
	border: 1,
	initComponent : function(){
		var me=this;
		me.callParent(arguments);
	},
	items:[{
        xtype : 'fieldset', 
        autoHeight : true, 
        defaultType : 'checkbox',
        defaults: {
            flex: 1,
            labelAlign: 'right'
        },
        items : [{
            boxLabel  : '本位币',
            name: 'CONFIGS where code ="defaultCurrency" and caller="sys"',
            inputValue: '1'
        }, {
            boxLabel  : '交易币别',
            name: 'CURRENCYS',
            inputValue: '1'
        }, {
            boxLabel  : '付款方式',
            name: 'payments(PA_CLASS="付款方式")',
            inputValue: '1'
        }, {
            boxLabel  : '收款方式',
            name: 'payments(PA_CLASS="收款方式")',
            inputValue: '1'
        }, {
            boxLabel  : '科目',
            name: 'CATEGORY',
            inputValue: '1'
        }, {
            boxLabel  : '其他出入库科目设置',
            name: 'PRODIOCATESET',
            inputValue: '1'
        }, {
            boxLabel  : '费用科目设置',
            name: 'FEECATEGORYSET',
            inputValue: '1'
        }, {
            boxLabel  : '发票类型对应科目设置',
            name: 'SENDKIND',
            inputValue: '1'
        }, {
            boxLabel  : '固定资产类型',
            name: 'ASSETSKIND',
            inputValue: '1'
        }, {
            boxLabel  : '成本中心',
            name: 'COSTCENTER',
            inputValue: '1'
        }] 
    }]
});