Ext.define('erp.view.ma.createAccountBook.reserveInfoForm.ProInfoForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.reserproinfo',
	hideBorders: true, 
	id:'reserveInfo_ProInfo',
	title: '产品信息',
	cls: 'infos',
	frame:false,
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
            boxLabel  : '物料类型',
            name: 'PRODUCTTYPE',
            inputValue: '1'
        }, {
            boxLabel  : '物料种类',
            name: 'PRODUCTKIND',
            inputValue: '1'
        }, {
            boxLabel  : '物料资料',
            name: 'PRODUCT',
            inputValue: '1'
        }, {
            boxLabel  : 'BOM资料',
            name: 'BOMDETAIL,BOM,ProdReplace',
            inputValue: '1'
        }] 
    }]
});