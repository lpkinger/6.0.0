// step2:账套信息
Ext.define('erp.view.ma.createAccountBook.AccountBookInfoForm',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.accountbookinfo',
	hideBorders: true, 
	frame:false,
	autoScroll:true,
	layout: 'border',
	title: '账套信息',
	initComponent : function(){
		var me=this;
		me.pageIndex = 1;
		me.callParent(arguments);
	},
	items:[{
        xtype:'form',
        id:'createAccountBook_accountBookInfoForm',
        bodyPadding: 10,
        cls: 'toTop',
        region: 'center',
        labelAlign: 'top',
        buttonAlign: 'center',
        
        inCloud: false,
        inAccount: false,
        error: false,
        
        defaults: {
        	allowBlank: false,
        	msgTarget: 'side',
        	padding: '5 0'
        },
        items :[{
        	xtype: 'textfield',
        	fieldLabel: 'uuid',
        	id: 'account_uuid',
        	name: 'uuid',
        	hidden: true
        },{
        	xtype: 'radiogroup',
        	id: 'refer_sys',
        	name: 'refer_sys',
        	defaultType: 'radiofield',
            fieldLabel: '选择开立参照系统',
            labelWidth: 119,
            layout: 'hbox',
            items: [{
	            boxLabel  : '当前系统',
	            name: 'refer_sys',
	            inputValue: 'CURRENT_SYS'
	        },{
                boxLabel  : 'UAS 1.0标准账套',
                style: 'margin-left: 10px;',
                name: 'refer_sys',
                inputValue: 'UAS_1_0_SYS'
            }]
        }, {
			xtype : 'datefield',
			fieldLabel : '设置系统财务开账账期',
			labelWidth: 147,
			name : 'fa_account_period',
			format: 'Y-m-d',
			value: new Date()
        }, {
        	xtype: 'fieldset',
        	style: 'margin-top: 15px;',
        	title: '选择开立账套需要保留的信息:',
        	layout: 'anchor',
        	defaults: {
	            anchor: '100%'
	        },
	        collapsible: false,
        	items: {
        		xtype: 'container',
        		layout : 'column', 
        		items: [{
        			columnWidth: 0.2,
	        		xtype: 'reserhrinfo'
	        	},{
	        		columnWidth: 0.3,
	        		xtype: 'reserfainfo'
	        	},{
	        		columnWidth: 0.2,
	        		xtype: 'reserproinfo'
	        	},{
	        		columnWidth: 0.3,
	        		xtype: 'reserscinfo'
	        	}]
        	}
        }],
	    buttons: [{
	    	id: 'accountBookInfoPrevBtn',
	    	text: '上一步'
	    }, {
	    	id: 'accountBookInfoConfirmBtn',
	        text: '确认开通'
	    }]
    }]
});