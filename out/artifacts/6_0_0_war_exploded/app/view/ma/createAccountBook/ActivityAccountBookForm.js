// step3:激活账套
Ext.define('erp.view.ma.createAccountBook.ActivityAccountBookForm',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.activityaccountbook',
	hideBorders: true, 
	frame:false,
	autoScroll:true,
	layout: {
        type: 'hbox',
        pack: 'center',
        align: 'middle'
	},
	title: '激活账套',
	initComponent : function(){
		var me=this;
		me.pageIndex = 2;
		me.callParent(arguments);
	},
	items:[{
        xtype:'form',
        id:'createAccountBook_activityAccountBookForm',
        bodyPadding: 10,
        defaultType: 'textfield',
        cls: 'toTop',
        buttonAlign: 'center',
        defaults: {
        	labelAlign: 'right',
        	allowBlank: false,
        	width: 500,
        	msgTarget: 'side',
        	padding: '5 0',
        	readOnly: true
        },
        items :[{
        	xtype: 'hidden',
        	fieldLabel: '新账套ID',
        	name: 'newAccountBookID'
        },{
        	xtype: 'hidden',
        	fieldLabel: '新账套描述',
        	name: 'newAccountBookDesc'
        
        },{
            fieldLabel: '新账套名称',
            name: 'newAccountBookName'
        }, {
            fieldLabel: '管理员账号',
            name: 'managerID'
        }, {
            fieldLabel: '管理员名称',
            name: 'managerName'
        }, {
            fieldLabel: '管理员密码',
            name: 'managerPassword'
        },{
        	xtype: 'displayfield',
        	fieldLabel: '备注',
        	value: '新开立的账套需要激活才能正式使用，建议在非上班时间进行激活',
        	style: 'margin-top: 20px;color: blue;'
        }],
	    buttons: [{
	    	id: 'activityAccountBook_closeBtn',
	    	text: '关闭'
	    },{
	    	id: 'activityAccountBook_activeBtn',
	        text: '激活'
	    }]
    }]
});