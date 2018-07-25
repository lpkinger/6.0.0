/**
 *整批子制造单生成
 *生成清单,生成制造单
 */	
Ext.define('erp.view.core.button.MakeOccur',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMakeOccurButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'makeoccur',
    	text: $I18N.common.button.erpMakeOccurButton,
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
	        		Ext.getCmp('makeoccur').fireEvent('list');
	        	}
	        }
	    },{
	    	iconCls: 'main-msg',
	        text: '生成制造单',
	        id: 'make',
	        listeners: {
	        	click: function(m){
	        		console.log(this);
	        		Ext.getCmp('makeoccur').fireEvent('make');
	        	}
	        }
	    }
]
	});