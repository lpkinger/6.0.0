/**
 *对制造单进行 "关闭",“投放”,"批准","组合"等操作
 */	
Ext.define('erp.view.core.button.DealMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDealMakeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'dealmake',
    	text: $I18N.common.button.erpDealMakeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
			this.addEvents({
				base: true,
				formal: true
			});
		},
		menu: [{
			iconCls: 'main-msg',
	        text: '投放',
	        id: 'base',
	        listeners: {
	        	click: function(m){
	        		Ext.getCmp('dealmake').fireEvent('base');
	        	}
	        }
	    },{
	    	iconCls: 'main-msg',
	        text: '批准',
	        id: 'formal',
	        listeners: {
	        	click: function(m){
	        		console.log(this);
	        		Ext.getCmp('dealmake').fireEvent('formal');
	        	}
	        }
	    },
	    {
	    	iconCls: 'main-msg',
	        text: '组合',
	        id: 'combine',
	        listeners: {
	        	click: function(m){
	        		console.log(this);
	        		Ext.getCmp('dealmake').fireEvent('combine');
	        	}
	        }
	    }]
	});