Ext.define('erp.view.b2c.purchase.B2CPurchase.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpB2CPurchaseForm',
	region: 'north',
	frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	cls: 'u-form-default',
	/*fieldDefaults : {
		fieldStyle : "background:#FFFAFA;color:#515151;",
		focusCls: 'x-form-field-cir-focus',
		labelAlign : "right",
		msgTarget: 'side',
		blankText : $I18N.common.form.blankText
	},*/
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	defaults:{
		xtype:'textfield',
		columnWidth:0.2,
		readOnly:true,
		fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;'
	},
	layout:'column',
	bodyStyle: 'background: #f1f1f1;border:none;',
	items:[{
			fieldLabel:'物料',
			name:'pr_code',
			columnWidth:.30,
			labelWidth:80
        },{
            fieldLabel : '总库存量',
            name:'totalqty',
            columnWidth:.25
        },{
            fieldLabel:'请购数量',
            name:'puqty'
        },{	 
            fieldLabel:'待选购数',
            name:'needbuyqty'
      }],
	initComponent : function(){ 
		this.callParent(arguments);
	},
	getCurrentStore: function(value){}
});
