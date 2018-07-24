/**
 * 转供应商按钮
 */	
Ext.define('erp.view.core.button.TurnVendor',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnVendorButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'turnVendor',
    	text: $I18N.common.button.erpTurnVendorButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
/*			this.addEvents({
				base: true,
				formal: true
			});*/
		}/*,
		menu: [{
			iconCls: 'main-msg',
	        text: '基本资料',
	        id: 'base',
	        listeners: {
	        	click: function(m){
	        		Ext.getCmp('turnVendor').fireEvent('base');
	        	}
	        }
	    },{
	    	iconCls: 'main-msg',
	        text: '正式',
	        id: 'formal',
	        listeners: {
	        	click: function(m){
	        		Ext.getCmp('turnVendor').fireEvent('formal');
	        	}
	        }
	    }]*/
	});