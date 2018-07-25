/**
 *订单转制造单整批生成
 */	
Ext.define('erp.view.core.button.SaleOccur',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSaleOccurButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'saleoccur',
    	text: $I18N.common.button.erpSaleOccurButton,
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
	        text: '生成清单',
	        id: 'list',
	        listeners: {
	        	click: function(m){
	        		Ext.getCmp('saleoccur').fireEvent('list');
	        	}
	        }
	    },{
	    	iconCls: 'main-msg',
	        text: '生成制造单',
	        id: 'make',
	        listeners: {
	        	click: function(m){
	        		console.log(this);
	        		Ext.getCmp('saleoccur').fireEvent('make');
	        	}
	        }
	    }
]
	});