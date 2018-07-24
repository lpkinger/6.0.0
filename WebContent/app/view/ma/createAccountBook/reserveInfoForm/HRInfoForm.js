Ext.define('erp.view.ma.createAccountBook.reserveInfoForm.HRInfoForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.reserhrinfo',
	hideBorders: true, 
	id:'reserveInfo_HRInfo',
	title: '人事信息',
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
            boxLabel  : '组织资料',
            name: 'HRORG',
            inputValue: '1'
        }, {
            boxLabel  : '岗位资料',
            name: 'JOB',
            inputValue: '1'
        }, {
            boxLabel  : '财务核算部门',
            name: 'DEPARTMENT',
            inputValue: '1'
        }, {
            boxLabel  : '人员资料',
            name: 'EMPLOYEE',
            inputValue: '1'
        }, {
            boxLabel  : '权限',
            name: 'PERSONALPOWER,POSITIONPOWER,LIMITFIELDS,SPECIALPOWER,SYSSPECIALPOWER',
            inputValue: '1'
        }] 
    }]
});